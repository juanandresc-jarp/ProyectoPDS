package com.example.nocodbdemo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DocumentoDTO {

    @JsonProperty("Id")
    @JsonInclude(JsonInclude.Include.NON_NULL) // Solo lo incluye si no es null
    private Integer id;

    @JsonProperty("Documento de identificación")
    private String documentoIdentificacion;

    @JsonProperty("Empresa")
    private String empresa;

    @JsonProperty("Nombre ciudadano")
    private String nombreCiudadano;

    @JsonProperty("Fecha queja")
    private String fechaQueja;

    @JsonProperty("Tipo de queja")
    private String tipoQueja;

    @JsonProperty("Estado")
    private String estado;

    // ❌ Estos dos campos son calculados, no se incluyen:
    // - Tiempo de respuesta
    // - Vencida
}
