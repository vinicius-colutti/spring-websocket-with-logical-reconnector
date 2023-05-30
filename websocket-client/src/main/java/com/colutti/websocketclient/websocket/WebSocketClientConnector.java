package com.colutti.websocketclient.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

@Component
public class WebSocketClientConnector{

    @Autowired
    ClientSessionHandler clientSessionHandler;

    private static final String WS_ENDPOINT = "ws://localhost:8080/server"; // Substitua pelo seu endpoint WebSocket

    public void start() {
        WebSocketClient webSocketClient = new StandardWebSocketClient();
        WebSocketStompClient stompClient = new WebSocketStompClient(webSocketClient);

        stompClient.connect(WS_ENDPOINT, clientSessionHandler);
    }
}
