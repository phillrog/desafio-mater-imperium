package com.materimperium.backend.modules.processamento.domain.interfaces.repositories;

import java.util.UUID;

public interface ResumoRegistroRepository {
    void upsertResumo(UUID procId, String codigo, Long qtd);
}