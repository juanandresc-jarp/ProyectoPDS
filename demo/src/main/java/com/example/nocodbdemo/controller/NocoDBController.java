package com.example.nocodbdemo.controller;

import com.example.nocodbdemo.model.DocumentoDTO;
import com.example.nocodbdemo.service.RegistroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import java.util.Map;


@RestController
@RequestMapping("/api/documentos")
public class NocoDBController {

    @Autowired
    private RegistroService registroService;

    @PostMapping
    public Mono<String> createOrUpdateRegistro(@RequestBody DocumentoDTO documento) {
        return registroService.createOrUpdateRegistro(documento);
    }

    @GetMapping
    public Mono<String> readAll() {
        return registroService.readAll();
    }

    @GetMapping("/{recordId}")
    public Mono<String> readOne(@PathVariable String recordId) {
        return registroService.readOne(recordId);
    }

    @PatchMapping
    public Mono<String> updateDocument(@RequestBody Map<String, Object> updates) {
        return registroService.updateDocument(updates);
    }

    @DeleteMapping("/{recordId}")
    public Mono<String> delete(@PathVariable String recordId) {
        return registroService.delete(recordId);
    }
} 
