package com.grepp.spring;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class App {

    public static void main(final String[] args) {
        org.springframework.boot.SpringApplication.run(App.class, args);
    }
}
