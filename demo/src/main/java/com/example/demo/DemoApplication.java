package com.example.demo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.CompletableFuture;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.converter.JacksonJsonMessageConverter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;

@SpringBootApplication
public class DemoApplication {

	private static BufferedReader r = new BufferedReader(
			new InputStreamReader(System.in));

	public static void main(String[] args) {
		WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
		stompClient.setMessageConverter(new JacksonJsonMessageConverter());
		StompSessionHandler sessionHandler = new MyStompSessionHandler();
		CompletableFuture<StompSession> future = stompClient.connectAsync("ws://localhost:9090/chess", sessionHandler);
		StompSession session;
		String in;
		try {
			session = future.get();
		} catch (Exception e) {
			return;
		}
		while (true) {

		}
	}

}
