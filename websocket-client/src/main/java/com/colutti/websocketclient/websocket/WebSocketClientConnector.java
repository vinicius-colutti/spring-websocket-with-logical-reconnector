package com.colutti.websocketclient.websocket;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

@Component
public class WebSocketClientConnector{

    private static final String WS_ENDPOINT = "ws://localhost:8080/server"; // Substitua pelo seu endpoint WebSocket

    public void start() throws Exception {
        WebSocketClient webSocketClient = new StandardWebSocketClient();
        WebSocketStompClient stompClient = new WebSocketStompClient(webSocketClient);

        ClientSessionHandler sessionHandler = new ClientSessionHandler();

        stompClient.connect(WS_ENDPOINT, sessionHandler);
    }
}
