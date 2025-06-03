package com.example.nocodbdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class NocodbDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(NocodbDemoApplication.class, args);
    }

    @Configuration
public class WebClientConfig {

    @Bean
        public WebClient.Builder webClientBuilder() {
            return WebClient.builder();
        }
    }

    
}
