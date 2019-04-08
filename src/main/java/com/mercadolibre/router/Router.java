package com.mercadolibre.router;

import com.google.common.net.MediaType;
import spark.Request;
import spark.Response;
import spark.Spark;
import spark.servlet.SparkApplication;

import javax.servlet.http.HttpServletResponse;

public class Router implements SparkApplication {

    @Override
    public void init() {

        Spark.get("/ping", this::pingHandler);

    }

    /**
     * Handles the ping request to show it is alive.
     */
    String pingHandler(Request request, Response response) {

        response.status(HttpServletResponse.SC_OK);
        response.header("Content-Type", MediaType.PLAIN_TEXT_UTF_8.toString());

        return "pong";
    }

}
