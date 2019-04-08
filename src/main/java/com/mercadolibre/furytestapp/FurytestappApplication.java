package com.mercadolibre.furytestapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication
public class FurytestappApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(FurytestappApplication.class, args);
    }
    
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(FurytestappApplication.class);
    }
}
