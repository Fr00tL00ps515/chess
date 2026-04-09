package com.example.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.converter.JacksonJsonMessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.web.client.RestClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.messaging.simp.stomp.StompSessionHandler;

import com.example.demo.messages.*;

@SpringBootApplication
public class DemoApplication {

	public static String[][] board = null;

	private static void printBoard() {
		for (String[] i : board) {
			System.out.println(Arrays.toString(i));
		}
	}

	private static boolean game = true;
	public static boolean myTurn = false;
	public static String player;
	private static int[] objectPosition = new int[2];
	private static int[] objectDestination = new int[2];
	private static BufferedReader r = new BufferedReader(
			new InputStreamReader(System.in));
	private static String in = "";
	private static String name;

	public static void main(String[] args) {
		WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
		stompClient.setMessageConverter(new JacksonJsonMessageConverter());
		StompSessionHandler sessionHandler = new MyStompSessionHandler();
		CompletableFuture<StompSession> future = stompClient.connectAsync("ws://localhost:9090/chess",
				sessionHandler);
		StompSession session;
		try {
			session = future.get();
		} catch (Exception e) {
			System.out.println("Cant reach the server");
			return;
		}

		session.send("/input/join_game", new Greeting(session.getSessionId()));
		while (board == null) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				return;
			}
		}
		while (game) {
			while (!myTurn) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
				}
			}
			printBoard();
			System.out.println("Type the object position:");
			try {
				in = r.readLine();
			} catch (IOException e) {
			}
			if (in.equals("exit")) {
				session.send("/input/leave_game", new Greeting(session.getSessionId()));
				return;
			} else {
				objectPosition[0] = in.charAt(0) - 48;
				objectPosition[1] = in.charAt(1) - 48;
			}

			System.out.println("Type the destination:");
			try {
				in = r.readLine();
			} catch (IOException e) {
			}
			if (in.equals("exit")) {
				session.send("/input/leave_game", new Greeting(session.getSessionId()));
				return;
			} else {
				objectDestination[0] = in.charAt(0) - 48;
				objectDestination[1] = in.charAt(1) - 48;
			}

			myTurn = false;
			session.send("/input/" + player, new MoveRequest(objectPosition[0], objectPosition[1],
					objectDestination[0], objectDestination[1], session.getSessionId()));

		}

	}

}
