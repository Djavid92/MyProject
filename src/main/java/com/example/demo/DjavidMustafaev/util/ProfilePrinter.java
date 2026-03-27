package com.example.demo.DjavidMustafaev.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ProfilePrinter implements CommandLineRunner {

    @Autowired
    private Environment env;

    @Override
    public void run(String... args) {
        System.out.println("=== АКТИВНЫЕ ПРОФИЛИ ===");
        for (String profile : env.getActiveProfiles()) {
            System.out.println("  - " + profile);
        }
        System.out.println("=========================");

        System.out.println("=== ПОДКЛЮЧЕНИЕ К БД ===");
        System.out.println("URL: " + env.getProperty("spring.datasource.url"));
        System.out.println("=========================");
    }
}