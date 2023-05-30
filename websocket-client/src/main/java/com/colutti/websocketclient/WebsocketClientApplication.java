package com.colutti.websocketclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WebsocketClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebsocketClientApplication.class, args);
	}

}
