package com.materimperium.backend.modules.processamento.services;

import com.materimperium.backend.modules.processamento.application.dtos.ProcessamentoResponse;
import com.materimperium.backend.modules.processamento.application.services.ProcessamentoServiceImpl;
import com.materimperium.backend.modules.processamento.application.validators.ArquivoValidator;
import com.materimperium.backend.modules.processamento.domain.entities.ProcessamentoArquivo;
import com.materimperium.backend.modules.processamento.domain.entities.StatusProcessamento;
import com.materimperium.backend.modules.processamento.domain.interfaces.repositories.ProcessamentoArquivoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessamentoServiceImplTest {

    @Mock
    private ProcessamentoArquivoRepository repository;

    @Mock
    private ArquivoValidator validator;

    @Mock
    private JobLauncher jobLauncher;

    @Mock
    private Job job;

    @Mock
    private MultipartFile file;

    @InjectMocks
    private ProcessamentoServiceImpl service;

    @Test
    @DisplayName("Deve iniciar processamento, validar cabeçalho e disparar o Job do Batch")
    void deveIniciarProcessamento() throws Exception {
        // Arrange
        UUID idManual = UUID.randomUUID();
        ProcessamentoArquivo mockup = ProcessamentoArquivo.builder()
                .id(idManual)
                .nomeArquivo("teste.txt")
                .status(StatusProcessamento.EM_PROCESSAMENTO)
                .build();

        when(file.getInputStream()).thenReturn(mock(InputStream.class));
        when(file.getOriginalFilename()).thenReturn("teste.txt");
        when(repository.save(any())).thenReturn(mockup);

        // Act
        UUID resultId = service.iniciarProcessamento(file);

        // Assert
        assertThat(resultId).isEqualTo(idManual);
        verify(validator, times(1)).validarCabecalho(any());
        verify(repository, times(1)).save(any());
        verify(jobLauncher, times(1)).run(any(), any());
    }

    @Test
    @DisplayName("Deve consultar um processamento por ID e converter para DTO com sucesso")
    void deveConsultarComSucessoEConverterParaDto() {
        // Arrange
        UUID id = UUID.randomUUID();
        ProcessamentoArquivo entity = ProcessamentoArquivo.builder()
                .id(id)
                .nomeArquivo("documento.txt")
                .status(StatusProcessamento.FINALIZADO_COM_SUCESSO)
                .dataCriacao(LocalDateTime.now())
                .resumos(new ArrayList<>()) // Garante que a lista não é nula
                .build();

        entity.adicionarResumo("1001", 50L);

        // Mockando o retorno como Optional para bater com a interface
        when(repository.findByIdWithResumos(id)).thenReturn(Optional.of(entity));

        // Act
        ProcessamentoResponse response = service.consultarProcessamento(id);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(id);
        assertThat(response.status()).isEqualTo("FINALIZADO_COM_SUCESSO");
        assertThat(response.resumos()).hasSize(1);
        assertThat(response.resumos().get(0).codigoRegistro()).isEqualTo("1001");
        verify(repository).findByIdWithResumos(id);
    }

    @Test
    @DisplayName("Deve lançar exceção quando o processamento não for encontrado")
    void deveLancarExcecaoAoConsultarIdInexistente() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(repository.findByIdWithResumos(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> service.consultarProcessamento(id))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Processamento não encontrado.");
    }

    @Test
    @DisplayName("Deve listar processamentos filtrando por status")
    void deveListarComFiltroDeStatus() {
        // Arrange
        StatusProcessamento statusFiltro = StatusProcessamento.EM_PROCESSAMENTO;
        ProcessamentoArquivo p1 = ProcessamentoArquivo.builder()
                .id(UUID.randomUUID())
                .status(statusFiltro)
                .resumos(Collections.emptyList())
                .build();

        when(repository.findByStatusWithoutResumos(statusFiltro)).thenReturn(List.of(p1));

        // Act
        List<ProcessamentoResponse> results = service.listarTodos(statusFiltro);

        // Assert
        assertThat(results).hasSize(1);
        assertThat(results.get(0).status()).isEqualTo(statusFiltro.name());
        verify(repository).findByStatusWithoutResumos(statusFiltro);
    }

    @Test
    @DisplayName("Deve listar todos os processamentos quando o status for nulo")
    void deveListarTodosQuandoStatusForNulo() {
        // Arrange
        when(repository.findByStatusWithoutResumos(null)).thenReturn(Collections.emptyList());

        // Act
        List<ProcessamentoResponse> results = service.listarTodos(null);

        // Assert
        assertThat(results).isEmpty();
        verify(repository).findByStatusWithoutResumos(null);
    }
}