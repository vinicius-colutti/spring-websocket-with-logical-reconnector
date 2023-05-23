package com.colutti.websocketclient.websocket;

import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.stereotype.Component;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@Component
public class ClientSessionHandler extends StompSessionHandlerAdapter {

    private StompSession stompSession;
    private ScheduledExecutorService scheduler;

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        this.stompSession = session;
        System.out.println("Conexão estabelecida com o servidor WebSocket");
    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        System.err.println("Erro de transporte na conexão WebSocket");
        scheduleReconnect();
    }

    private void scheduleReconnect() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(this::reconnect, 5, TimeUnit.SECONDS); // Tentar reconectar após 5 segundos
    }

    private void reconnect() {
        System.out.println("tentando reconectar");
        stompSession = null;
        WebSocketClientConnector clientRunner = new WebSocketClientConnector();
        clientRunner.start();
    }
}
