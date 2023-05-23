package com.colutti.websocketclient.websocket;

import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.stereotype.Component;


@Component
public class ClientSessionHandler extends StompSessionHandlerAdapter {

    private StompSession stompSession;

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        this.stompSession = session;
        System.out.println("Conexão estabelecida com o servidor WebSocket");
        // Lógica adicional após a conexão ser estabelecida
    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        System.err.println("Erro de transporte na conexao WebSocket");
        try {
            reconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void reconnect() throws Exception {
        try {
            System.out.println("tentando conectar novamente");
            Thread.sleep(5000); // Espera por 5 segundos antes de tentar reconectar
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        stompSession = null;
        WebSocketClientConnector clientRunner = new WebSocketClientConnector();
        clientRunner.start();
    }

}
