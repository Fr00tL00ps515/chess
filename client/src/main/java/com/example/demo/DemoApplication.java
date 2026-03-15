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

@SpringBootApplication
public class DemoApplication {

	private static String[][] field = {
			{ "5", "4", "3", "2", "1", "3", "4", "5" },
			{ "6", "6", "6", "6", "6", "6", "6", "6" },
			{ " ", " ", " ", " ", " ", " ", " ", " " },
			{ " ", " ", " ", " ", " ", " ", " ", " " },
			{ " ", " ", " ", " ", " ", " ", " ", " " },
			{ " ", " ", " ", " ", " ", " ", " ", " " },
			{ "6", "6", "6", "6", "6", "6", "6", "6" },
			{ "5", "4", "3", "2", "1", "3", "4", "5" } };

	private static void printField() {
		for (String[] i : field) {
			System.out.println(Arrays.toString(i));
		}
	}

	private static boolean gameEnd = false;
	private static int[] objectPosition = new int[2];
	private static int[] objectDestination = new int[2];
	private static BufferedReader r = new BufferedReader(
			new InputStreamReader(System.in));
	private static String in = "";

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
			return;
		}
		try {
			Thread.sleep(2000);
		} catch (Exception e) {

		}

	}

}
