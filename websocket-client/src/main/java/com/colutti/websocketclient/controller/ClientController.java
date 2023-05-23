package com.colutti.websocketclient.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/teste")
public class ClientController {

    @GetMapping
    public String teste(){
        return "OK"+ Math.random();
    }

}
