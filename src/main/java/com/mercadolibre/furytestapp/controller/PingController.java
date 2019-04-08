package com.mercadolibre.furytestapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by ngrau on 11/24/17.
 */
@RestController(value = "/ping")
public class PingController {

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> handlePing() {
        return ResponseEntity.ok("pong");
    }
}
