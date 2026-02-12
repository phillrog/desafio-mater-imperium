package com.materimperium.backend.modules.processamento.repositories;

import com.materimperium.backend.modules.processamento.domain.entities.ProcessamentoArquivo;
import com.materimperium.backend.modules.processamento.domain.entities.StatusProcessamento;
import com.materimperium.backend.modules.processamento.domain.interfaces.repositories.ProcessamentoArquivoRepository;
import com.materimperium.backend.modules.processamento.domain.interfaces.repositories.ResumoRegistroRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ResumoRegistroRepositoryTest {

    @Autowired
    private ResumoRegistroRepository repository;

    @Autowired
    private ProcessamentoArquivoRepository processamentoRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("Deve inserir um novo resumo quando não houver conflito")
    void deveInserirNovoResumo() {
        // Arrange
        Long procId = criarProcessamentoMock();

        // Act
        repository.upsertResumo(procId, "C100", 50L);

        // Importante: Limpa o cache para forçar o JPA a ler o INSERT nativo do banco
        entityManager.flush();
        entityManager.clear();

        // Assert
        Optional<ProcessamentoArquivo> proc = processamentoRepository.findByIdWithResumos(procId);

        assertThat(proc).isPresent();
        assertThat(proc.get().getResumos()).hasSize(1);
        assertThat(proc.get().getResumos().get(0).getQuantidade()).isEqualTo(50L);
        assertThat(proc.get().getResumos().get(0).getCodigoRegistro()).isEqualTo("C100");
    }

    @Test
    @DisplayName("Deve somar a quantidade quando houver conflito de código de registro (UPSERT)")
    void deveSomarQuantidadeNoUpsert() {
        // Arrange
        Long procId = criarProcessamentoMock();

        // Act
        repository.upsertResumo(procId, "0000", 10L);

        // Sincroniza e limpa para o próximo comando ler o estado atual
        entityManager.flush();
        entityManager.clear();

        repository.upsertResumo(procId, "0000", 25L); // Deve disparar o ON CONFLICT

        entityManager.flush();
        entityManager.clear();

        // Assert
        Optional<ProcessamentoArquivo> proc = processamentoRepository.findByIdWithResumos(procId);

        assertThat(proc).isPresent();
        // O tamanho deve continuar sendo 1, pois o segundo comando foi um UPDATE
        assertThat(proc.get().getResumos()).hasSize(1);
        // A quantidade deve ser a soma (10 + 25)
        assertThat(proc.get().getResumos().get(0).getQuantidade()).isEqualTo(35L);
    }

    private Long criarProcessamentoMock() {
        ProcessamentoArquivo proc = ProcessamentoArquivo.builder()
                .nomeArquivo("teste_upsert.txt")
                .status(StatusProcessamento.EM_PROCESSAMENTO)
                .build();

        ProcessamentoArquivo salvo = processamentoRepository.save(proc);

        // Garante que o ID foi gerado e o objeto está no banco antes do UPSERT
        entityManager.flush();

        return salvo.getId();
    }
}