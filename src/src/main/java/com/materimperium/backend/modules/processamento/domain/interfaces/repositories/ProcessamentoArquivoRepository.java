package com.materimperium.backend.modules.processamento.domain.interfaces.repositories;

import com.materimperium.backend.modules.processamento.domain.entities.ProcessamentoArquivo;
import com.materimperium.backend.modules.processamento.domain.entities.StatusProcessamento;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProcessamentoArquivoRepository {
    Optional<ProcessamentoArquivo> findByIdWithResumos(UUID id);
    List<ProcessamentoArquivo> findByStatusWithoutResumos(StatusProcessamento status);
    ProcessamentoArquivo save(ProcessamentoArquivo processamento); // Contrato de persistência
    Optional<ProcessamentoArquivo> findById(UUID id);
}