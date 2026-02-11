package com.materimperium.backend.modules.processamento.infrastructure.repositories;

import com.materimperium.backend.modules.processamento.domain.entities.ProcessamentoArquivo;
import com.materimperium.backend.modules.processamento.domain.entities.StatusProcessamento;
import com.materimperium.backend.modules.processamento.domain.interfaces.repositories.ProcessamentoArquivoRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProcessamentoArquivoRepositoryImpl
        extends JpaRepository<ProcessamentoArquivo, UUID>, ProcessamentoArquivoRepository {
    // O Spring Data JPA implementará os métodos automaticamente


    @EntityGraph(attributePaths = {"resumos"})
    @Query("SELECT p FROM ProcessamentoArquivo p WHERE p.id = :id")
    Optional<ProcessamentoArquivo> findByIdWithResumos(UUID id);


    @Query("SELECT p FROM ProcessamentoArquivo p " +
            "WHERE (:status IS NULL OR p.status = :status) " +
            "ORDER BY p.dataCriacao DESC")
    List<ProcessamentoArquivo> findByStatusWithoutResumos(StatusProcessamento status);
}