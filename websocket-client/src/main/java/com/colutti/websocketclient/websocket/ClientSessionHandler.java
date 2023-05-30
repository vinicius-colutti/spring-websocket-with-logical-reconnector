package com.colutti.websocketclient.websocket;

import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class ClientSessionHandler extends StompSessionHandlerAdapter {

    /*
    Nesta versão atualizada, implementei as seguintes sugestões:

    - Tratamento de reconexão malsucedida: Adicionei uma contagem de tentativas de reconexão (reconnectAttempts)
     e um limite máximo (MAX_RECONNECT_ATTEMPTS). Se o número de tentativas exceder o limite,
      será registrado um erro.

    - Configuração externa: Adicionei um método logReconnectionStatus() anotado com @Scheduled para registrar
     periodicamente o status das tentativas de reconexão.
    */

    private StompSession stompSession;
    private ScheduledExecutorService scheduler;
    private static int reconnectAttempts;
    private static final int MAX_RECONNECT_ATTEMPTS = 5;

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        this.stompSession = session;
        System.out.println("Conexão estabelecida com o servidor WebSocket");
        reconnectAttempts = 0;
    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        System.err.println("Erro de transporte na conexão WebSocket");
        scheduleReconnect();
    }

    private void scheduleReconnect() {
        if (reconnectAttempts < MAX_RECONNECT_ATTEMPTS) {
            reconnectAttempts++;
            scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.schedule(this::reconnect, 5, TimeUnit.SECONDS); // Tentar reconectar após 5 segundos
        } else {
            System.err.println("Não foi possível reconectar após " + MAX_RECONNECT_ATTEMPTS + " tentativas. Notificar um administrador.");
        }
    }

    private void reconnect() {
        System.out.println("Tentando reconectar");
        stompSession = null;
        WebSocketClientConnector clientRunner = new WebSocketClientConnector();
        clientRunner.start();
    }

    /*
     Agora, a classe implementa um método cleanup() anotado com @PreDestroy, que será chamado quando o
     componente for destruído. Esse método realiza o encerramento adequado do ScheduledExecutorService para evitar possíveis
     vazamentos de recursos. Ele aguarda até 5 segundos para que as tarefas em execução sejam concluídas, e se isso
     não ocorrer, é chamado scheduler.shutdownNow() para interromper as tarefas imediatamente.
     */
    @PreDestroy
    private void cleanup() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    @Scheduled(fixedDelay = 30000) // Executar a cada 30 segundos
    private void logReconnectionStatus() {
        if (reconnectAttempts > 0 && reconnectAttempts <= MAX_RECONNECT_ATTEMPTS) {
            System.out.println("Tentativas de reconexão: " + reconnectAttempts + "/" + MAX_RECONNECT_ATTEMPTS);
            System.out.println("A tentativa de reconexão, será realizada a cada 1 minuto, a apartir de agora");
            reconnect();
        }
    }
}
