package com.materimperium.backend.modules.processamento.domain.interfaces.repositories;

import com.materimperium.backend.modules.processamento.domain.entities.ProcessamentoArquivo;
import com.materimperium.backend.modules.processamento.domain.entities.StatusProcessamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProcessamentoArquivoRepository extends JpaRepository<ProcessamentoArquivo, UUID> {
    Optional<ProcessamentoArquivo> findByIdWithResumos(UUID id);
    List<ProcessamentoArquivo> findByStatusWithoutResumos(StatusProcessamento status);
}