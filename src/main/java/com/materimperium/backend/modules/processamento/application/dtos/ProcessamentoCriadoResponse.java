package com.materimperium.backend.modules.processamento.application.dtos;

public record ProcessamentoCriadoResponse(
        Long processamentoId,
        String status
) {}