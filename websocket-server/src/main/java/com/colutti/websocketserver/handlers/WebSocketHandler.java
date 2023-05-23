package com.colutti.websocketserver.handlers;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class WebSocketHandler extends TextWebSocketHandler {

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Chamado quando uma nova conexão WebSocket é estabelecida
        super.afterConnectionEstablished(session);
        System.out.println("Nova conexão estabelecida: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // Chamado quando uma mensagem de texto é recebida do cliente
        super.handleTextMessage(session, message);
        System.out.println("Mensagem recebida: " + message.getPayload());
        // Lógica para processar a mensagem recebida e enviar uma resposta, se necessário
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // Chamado quando a conexão WebSocket é fechada
        super.afterConnectionClosed(session, status);
        System.out.println("Conexão fechada: " + session.getId());
    }

}
