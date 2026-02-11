package com.materimperium.backend.modules.processamento.application.dtos;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ProcessamentoResponse(
        UUID id,
        String nomeArquivo,
        String status,
        LocalDateTime dataCriacao,
        List<ResumoResponse> resumos
) {}