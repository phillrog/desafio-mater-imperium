package com.materimperium.backend.modules.processamento.domain.interfaces.repositories;


public interface ResumoRegistroRepository {
    void upsertResumo(Long procId, String codigo, Long qtd);
}