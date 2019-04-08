package com.mercadolibre.furytestapp.controller;

import org.junit.Test;
import org.junit.runner.RunWith;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by ngrau on 11/24/17.
 */
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@WebMvcTest(controllers = PingController.class)
public class PingControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testHandlePing() throws Exception{

        mockMvc.perform(
                get("/ping")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("pong"));
    }
}
