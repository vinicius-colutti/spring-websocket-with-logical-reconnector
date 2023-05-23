package com.colutti.websocketclient.configuration;

import com.colutti.websocketclient.websocket.WebSocketClientConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SocketStartClient {

    @Autowired
    WebSocketClientConnector webSocketClientConnector;

    @Bean
    public void initConnection() {
        webSocketClientConnector.start();
    }

}
