package com.materimperium.backend.modules.processamento.application.dtos;

import java.time.LocalDateTime;
import java.util.List;

public record ProcessamentoResponse(
        Long id,
        String nomeArquivo,
        String status,
        LocalDateTime dataCriacao,
        LocalDateTime dataHoraInicio,
        LocalDateTime dataHoraFinalizou,
        List<ResumoResponse> resumos
) {}