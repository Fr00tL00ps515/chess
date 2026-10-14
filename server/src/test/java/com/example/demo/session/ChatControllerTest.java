package com.example.demo.session;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.JacksonJsonMessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import com.example.demo.assets.ErrorResponse;
import com.example.demo.assets.Response;
import com.example.demo.assets.PlayerResponse;
import com.example.demo.assets.MoveRequest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ChatControllerTest {

    @LocalServerPort
    private int port;

    private final WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
    private final List<Player> players = new ArrayList<>();

    @BeforeEach
    void setUp() {
        stompClient.setMessageConverter(new JacksonJsonMessageConverter());
    }

    @AfterEach
    void tearDown() {
        players.forEach(Player::disconnect);
    }

    @Test
    void thirdPlayerWaitsUntilASeatIsFree() throws Exception {
        Player white = join();
        assertThat(white.nextPlayerResponse()).isEqualTo(new PlayerResponse("WHITE", 0));
        Player black = join();
        assertThat(black.nextPlayerResponse()).isEqualTo(new PlayerResponse("BLACK", 0));

        Player third = join();
        assertThat(third.nextPlayerResponse()).isEqualTo(new PlayerResponse(null, 1));
        third.move(6, 4, 4, 4);
        assertThat(third.nextError().message()).isEqualTo("You are not playing in this game");

        white.disconnect();
        assertThat(third.nextPlayerResponse()).isEqualTo(new PlayerResponse("WHITE", 0));

        third.move(6, 4, 4, 4);
        assertThat(black.awaitState(state -> state.lastMove() != null).turn()).isEqualTo("BLACK");
    }

    @Test
    void movesAreSentToBothPlayers() throws Exception {
        Player white = join();
        white.nextPlayerResponse();
        Player black = join();
        black.nextPlayerResponse();

        white.move(6, 4, 4, 4);

        Response state = black.awaitState(s -> s.lastMove() != null);
        assertThat(state.turn()).isEqualTo("BLACK");
        assertThat(state.board()[4][4]).isEqualTo("P");
        assertThat(state.board()[6][4]).isNull();
        assertThat(state.legalMoves()).hasSize(20);
    }

    @Test
    void illegalMovesAreRejected() throws Exception {
        Player white = join();
        white.nextPlayerResponse();
        white.move(6, 4, 4, 4);
        assertThat(white.nextError().message()).isEqualTo("Wait for your opponent to join");

        Player black = join();
        black.nextPlayerResponse();
        black.move(1, 4, 3, 4);
        assertThat(black.nextError().message()).isEqualTo("It is not your turn");

        white.move(6, 4, 3, 4);
        assertThat(white.nextError().message()).isEqualTo("Illegal move");
    }

    private Player join() throws Exception {
        StompSession session = stompClient
                .connectAsync("ws://localhost:" + port + "/chess", new StompSessionHandlerAdapter() {
                })
                .get(5, TimeUnit.SECONDS);
        Player player = new Player(session);
        players.add(player);
        session.send("/input/join_game", "");
        return player;
    }

    private static class Player {

        private final StompSession session;
        private final BlockingQueue<PlayerResponse> joinResponses = new LinkedBlockingQueue<>();
        private final BlockingQueue<Response> states = new LinkedBlockingQueue<>();
        private final BlockingQueue<ErrorResponse> errors = new LinkedBlockingQueue<>();

        Player(StompSession session) {
            this.session = session;
            session.subscribe("/user/output/player", handler(PlayerResponse.class, joinResponses));
            session.subscribe("/user/output/errors", handler(ErrorResponse.class, errors));
            session.subscribe("/output/game", handler(Response.class, states));
        }

        void move(int fromRow, int fromCol, int toRow, int toCol) {
            session.send("/input/move", new MoveRequest(fromRow, fromCol, toRow, toCol, null));
        }

        PlayerResponse nextPlayerResponse() throws InterruptedException {
            return next(joinResponses);
        }

        ErrorResponse nextError() throws InterruptedException {
            return next(errors);
        }

        Response awaitState(Predicate<Response> condition) throws InterruptedException {
            Response state = next(states);
            while (!condition.test(state)) {
                state = next(states);
            }
            return state;
        }

        void disconnect() {
            if (session.isConnected()) {
                session.disconnect();
            }
        }

        private static <T> T next(BlockingQueue<T> queue) throws InterruptedException {
            T message = queue.poll(5, TimeUnit.SECONDS);
            assertThat(message).as("message from the server").isNotNull();
            return message;
        }

        private static <T> StompFrameHandler handler(Class<T> type, BlockingQueue<T> queue) {
            return new StompFrameHandler() {
                @Override
                public Type getPayloadType(StompHeaders headers) {
                    return type;
                }

                @Override
                public void handleFrame(StompHeaders headers, Object payload) {
                    queue.add(type.cast(payload));
                }
            };
        }
    }
}
