package com.materimperium.backend.modules.processamento.application.dtos;

public record ResumoResponse(
        String codigoRegistro,
        Long quantidade
) {}