package com.materimperium.backend.modules.processamento.domain.repositories;

import com.materimperium.backend.modules.processamento.domain.entities.ProcessamentoArquivo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ProcessamentoArquivoRepository extends JpaRepository<ProcessamentoArquivo, UUID> {
}