package com.materimperium.backend.modules.processamento.infrastructure.repositories;

import com.materimperium.backend.modules.processamento.domain.entities.ProcessamentoArquivo;
import com.materimperium.backend.modules.processamento.domain.repositories.ProcessamentoArquivoRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface ProcessamentoArquivoRepositoryImpl
        extends JpaRepository<ProcessamentoArquivo, UUID>, ProcessamentoArquivoRepository {
    // O Spring Data JPA implementará os métodos automaticamente
}