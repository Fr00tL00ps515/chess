# Chess

Chess for two players in the browser. The server runs a single game with two seats. Whoever opens the page while a seat is free gets the board, everybody else sees a waiting screen and gets the first seat that frees up. When a player leaves or loses the connection, the next person in line takes over their pieces and the game goes on from the same position.

## Structure

- `server/` - Spring Boot 4 (Java 17). Holds the game, checks every move and talks to the browsers with STOMP over a WebSocket (`/chess`).
- `client/` - React app built with Vite. Shows the board or the waiting screen and sends the moves.

Only the server knows the rules (check, checkmate, stalemate, castling, en passant, promotion). With every update it sends the list of legal moves, so the client just shows them. Draws by repetition, the 50 move rule and insufficient material are not detected.

## Running locally

```
cd server
./mvnw spring-boot:run
```

```
cd client
npm install
npm run dev
```

Open http://localhost:5173 in two tabs to play against yourself, a third tab gets the waiting screen. Vite forwards `/chess` to the server on port 8080.

## Docker

The Dockerfile builds the client, puts it into the Spring Boot jar and runs it, so the whole app is served on one port:

```
docker build -t chess .
docker run -p 8080:8080 chess
```

Then open http://localhost:8080.

## Messages

| Destination          | Direction         | Content                                                                  |
| -------------------- | ----------------- | ------------------------------------------------------------------------ |
| `/input/join_game`         | client to server  | asks for a seat                                                          |
| `/input/move`          | client to server  | `{fromRow, fromCol, toRow, toCol, promotion}`, promotion is Q, R, B or N |
| `/input/restart`       | client to server  | starts a new game, only after checkmate or stalemate                     |
| `/user/output/player` | server to client  | `{color, queuePosition}`, color is null while waiting                    |
| `/output/game`        | server to clients | board, turn, status, legal moves, last move and taken seats              |
| `/user/output/errors` | server to client  | why a move was rejected                                                  |

Rows and columns are counted from the top left corner as white sees the board, so a8 is `(0, 0)` and h1 is `(7, 7)`.
