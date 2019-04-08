package com.mercadolibre;

import com.mercadolibre.router.Router;
import spark.Spark;

public class Main {

    private static final int PORT = 8080;

    public static void main(String[] args) {
        Spark.port(PORT);
        new Router().init();
    }

}
