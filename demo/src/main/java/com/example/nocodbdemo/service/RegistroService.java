package com.example.nocodbdemo.service;

import com.example.nocodbdemo.model.DocumentoDTO;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
public class RegistroService {

    private final WebClient webClient;

    @Value("${nocodb.table.id}")
    private String tableId;

    public RegistroService(WebClient.Builder webClientBuilder,
                           @Value("${nocodb.api.url}") String apiUrl,
                           @Value("${nocodb.api.key}") String apiKey) {
        this.webClient = webClientBuilder
                .baseUrl(apiUrl)
                .defaultHeader("xc-token", apiKey)
                .build();
    }

    public Mono<String> createOrUpdateRegistro(DocumentoDTO documento) {
        String documentoEncoded = URLEncoder.encode(documento.getDocumentoIdentificacion(), StandardCharsets.UTF_8);
        String empresaEncoded = URLEncoder.encode(documento.getEmpresa(), StandardCharsets.UTF_8);
        String where = String.format("(Documento de identificación,eq,%s)~and(Empresa,eq,%s)", documentoEncoded, empresaEncoded);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/tables/{table}/records")
                        .queryParam("where", where)
                        .build(tableId))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .timeout(Duration.ofSeconds(10))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2)))
                .doOnError(error -> {
                    System.err.println("❌ Error al buscar si ya existe el registro:");
                    error.printStackTrace();
                })
                .flatMap(response -> {
                    JsonNode records = response.get("list");
                    if (records != null && records.isArray() && records.size() > 0) {
                        int existingId = records.get(0).get("Id").asInt();
                        documento.setId(existingId);
                        return updateRegistro(documento);
                    } else {
                        return createRegistro(documento);
                    }
                });
    }

    public Mono<String> createRegistro(DocumentoDTO documento) {
        return webClient.post()
                .uri("/tables/{table}/records", tableId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(documento)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(10))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2)))
                .doOnError(error -> {
                    System.err.println("❌ Error al crear registro:");
                    error.printStackTrace();
                });
    }

    public Mono<String> updateRegistro(DocumentoDTO documento) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("Id", documento.getId());
        payload.put("Documento de identificación", documento.getDocumentoIdentificacion());
        payload.put("Empresa", documento.getEmpresa());
        payload.put("Nombre ciudadano", documento.getNombreCiudadano());
        payload.put("Fecha queja", documento.getFechaQueja());
        payload.put("Tipo de queja", documento.getTipoQueja());
        payload.put("Estado", documento.getEstado());

        return webClient.patch()
                .uri("/tables/{table}/records", tableId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(10))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2)))
                .doOnError(error -> {
                    System.err.println("❌ Error al actualizar registro:");
                    error.printStackTrace();
                });
    }

    public Mono<String> readAll() {
        return webClient.get()
                .uri("/tables/{table}/records", tableId)
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<String> readOne(String recordId) {
        return webClient.get()
                .uri("/tables/{table}/records/{recordId}", tableId, recordId)
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<String> updateDocument(Map<String, Object> updates) {
        return webClient.patch()
                .uri("/tables/{table}/records", tableId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updates)
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<String> delete(String recordId) {
        Map<String, Object> body = new HashMap<>();
        body.put("Id", Integer.parseInt(recordId));

        return webClient.method(org.springframework.http.HttpMethod.DELETE)
                .uri("/tables/{table}/records", tableId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class);
    }
}
