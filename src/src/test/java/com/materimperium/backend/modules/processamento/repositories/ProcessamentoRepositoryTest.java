package com.materimperium.backend.modules.processamento.repositories;

import com.materimperium.backend.modules.processamento.domain.entities.ProcessamentoArquivo;
import com.materimperium.backend.modules.processamento.domain.interfaces.repositories.ProcessamentoArquivoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
                .build();
        
        when(repository.findById(id)).thenReturn(Optional.of(mockEntity));

        // Act
        Optional<ProcessamentoArquivo> resultado = repository.findById(id);

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(id);
    }
}