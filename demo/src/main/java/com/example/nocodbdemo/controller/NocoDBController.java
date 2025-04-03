package com.example.nocodbdemo.controller;

import com.example.nocodbdemo.model.DocumentoDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpMethod;

@RestController
@RequestMapping("/api/documentos")
public class NocoDBController {

    private final WebClient webClient;

    @Value("${nocodb.api.url}")
    private String apiUrl;

    @Value("${nocodb.api.key}")
    private String apiKey;

    @Value("${nocodb.table.id}")
    private String tableId;

    public NocoDBController(WebClient.Builder webClientBuilder, 
                            @Value("${nocodb.api.url}") String apiUrl,
                            @Value("${nocodb.api.key}") String apiKey) {
        this.webClient = webClientBuilder
                .baseUrl(apiUrl)
                .defaultHeader("xc-token", apiKey)
                .build();
    }

    // CREATE
    @PostMapping
    public Mono<String> create(@RequestBody DocumentoDTO documento) {
        return webClient.post()
                .uri("/tables/{tableId}/records", tableId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(documento)
                .retrieve()
                .bodyToMono(String.class);
    }

    // READ ALL
    @GetMapping
    public Mono<String> readAll() {
        return webClient.get()
                .uri("/tables/{tableId}/records", tableId)
                .retrieve()
                .bodyToMono(String.class);
    }

    // READ ONE
    @GetMapping("/{recordId}")
    public Mono<String> readOne(@PathVariable String recordId) {
        return webClient.get()
                .uri("/tables/{tableId}/records/{recordId}", tableId, recordId)
                .retrieve()
                .bodyToMono(String.class);
    }

    
    @PatchMapping
    public Mono<String> updateDocument(@RequestBody Map<String, Object> updates) {
        return webClient.patch()
        .uri("/tables/{tableId}/records", tableId) 
                .header("xc-token", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updates) // Single update object
                .retrieve()
                .bodyToMono(String.class);
    }
    
    // DELETE (enviando ID en el body como lo hacías)
    @DeleteMapping("/{recordId}")
    public Mono<String> delete(@PathVariable String recordId) {
        Map<String, Object> body = new HashMap<>();
        body.put("Id", Integer.parseInt(recordId));

        return webClient.method(HttpMethod.DELETE)
                .uri("/tables/{tableId}/records", tableId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class);
    }
}
