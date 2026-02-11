package com.materimperium.backend.modules.processamento.repositories;

import com.materimperium.backend.modules.processamento.domain.entities.ProcessamentoArquivo;
import com.materimperium.backend.modules.processamento.domain.entities.StatusProcessamento;
import com.materimperium.backend.modules.processamento.domain.interfaces.repositories.ProcessamentoArquivoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProcessamentoRepositoryTest {

    @Mock
    private ProcessamentoArquivoRepository repository;

    @Test
    @DisplayName("Deve simular a busca de um processamento por ID no domínio")
    void deveSimularBuscaPorId() {
        // Arrange
        UUID id = UUID.randomUUID();
        ProcessamentoArquivo mockEntity = ProcessamentoArquivo.builder()
                .id(id)
                .nomeArquivo("teste.txt")
                .status(StatusProcessamento.EM_PROCESSAMENTO)
                .build();

        when(repository.findById(id)).thenReturn(Optional.of(mockEntity));

        // Act
        Optional<ProcessamentoArquivo> resultado = repository.findById(id);

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(id);
        assertThat(resultado.get().getNomeArquivo()).isEqualTo("teste.txt");
    }

    @Test
    @DisplayName("Deve simular a busca de processamento com resumos carregados")
    void deveSimularBuscaComResumos() {
        // Arrange
        UUID id = UUID.randomUUID();
        ProcessamentoArquivo mockEntity = ProcessamentoArquivo.builder()
                .id(id)
                .nomeArquivo("gigante.txt")
                .build();

        when(repository.findByIdWithResumos(id)).thenReturn(Optional.of(mockEntity));

        // Act
        Optional<ProcessamentoArquivo> resultado = repository.findByIdWithResumos(id);

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNomeArquivo()).isEqualTo("gigante.txt");
    }

    @Test
    @DisplayName("Deve simular a listagem por status sem carregar resumos")
    void deveSimularListagemPorStatus() {
        // Arrange
        StatusProcessamento status = StatusProcessamento.FINALIZADO_COM_SUCESSO;
        ProcessamentoArquivo p1 = ProcessamentoArquivo.builder().nomeArquivo("arq1.txt").build();

        when(repository.findByStatusWithoutResumos(status)).thenReturn(List.of(p1));

        // Act
        List<ProcessamentoArquivo> resultado = repository.findByStatusWithoutResumos(status);

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNomeArquivo()).isEqualTo("arq1.txt");
    }
}