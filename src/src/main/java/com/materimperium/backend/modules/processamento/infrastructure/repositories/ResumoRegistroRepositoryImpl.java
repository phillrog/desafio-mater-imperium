package com.materimperium.backend.modules.processamento.infrastructure.repositories;

import com.materimperium.backend.modules.processamento.domain.entities.ResumoRegistro;
import com.materimperium.backend.modules.processamento.domain.interfaces.repositories.ResumoRegistroRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
public interface ResumoRegistroRepositoryImpl extends JpaRepository<ResumoRegistro, Long>, ResumoRegistroRepository {
    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO resumo_registros (processamento_id, codigo_registro, quantidade)
        VALUES (:procId, :codigo, :qtd)
        ON CONFLICT (processamento_id, codigo_registro) 
        DO UPDATE SET quantidade = resumo_registros.quantidade + EXCLUDED.quantidade
        """, nativeQuery = true)
    void upsertResumo(@Param("procId") UUID procId, @Param("codigo") String codigo, @Param("qtd") Long qtd);
}