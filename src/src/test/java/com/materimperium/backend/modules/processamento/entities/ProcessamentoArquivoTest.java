package com.materimperium.backend.modules.processamento.entities;

import com.materimperium.backend.modules.processamento.domain.entities.ProcessamentoArquivo;
import com.materimperium.backend.modules.processamento.domain.entities.StatusProcessamento;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ProcessamentoArquivoTest {

    @Test
    @DisplayName("Deve adicionar um novo resumo")
    void deveAdicionarResumoComSucesso() {
        // Arrange
        ProcessamentoArquivo proc = ProcessamentoArquivo.builder()
                .nomeArquivo("teste.txt")
                .status(StatusProcessamento.EM_PROCESSAMENTO)
                .build();

        // Act
        proc.adicionarResumo("C100", 10L);

        // Assert
        assertThat(proc.getResumos()).hasSize(1);
        assertThat(proc.getResumos().get(0).getCodigoRegistro()).isEqualTo("C100");
        assertThat(proc.getResumos().get(0).getProcessamento()).isEqualTo(proc);
    }
}