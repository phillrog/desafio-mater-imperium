package com.materimperium.backend.modules.processamento.services;

import com.materimperium.backend.modules.processamento.application.abstractions.Result;
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
import java.util.*;

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
    @DisplayName("Deve iniciar processamento com sucesso quando validador não retorna erros")
    void deveIniciarProcessamentoComSucesso() throws Exception {
        // Arrange
        UUID idManual = UUID.randomUUID();
        ProcessamentoArquivo mockup = ProcessamentoArquivo.builder()
                .id(idManual)
                .nomeArquivo("teste.txt")
                .status(StatusProcessamento.EM_PROCESSAMENTO)
                .build();

        // IMPORTANTE: Mockar o validador retornando lista VAZIA (sucesso)
        when(validator.validarCabecalho(any())).thenReturn(Collections.emptyList());
        when(file.getInputStream()).thenReturn(mock(InputStream.class));
        when(file.getOriginalFilename()).thenReturn("teste.txt");
        when(repository.save(any())).thenReturn(mockup);

        // Act
        Result<UUID> result = service.iniciarProcessamento(file);

        // Assert
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.value()).isEqualTo(idManual); // O valor dentro do Result deve ser o ID
        verify(validator).validarCabecalho(any());
        verify(jobLauncher).run(any(), any());
    }

    @Test
    @DisplayName("Deve retornar falha quando o validador encontrar erros no cabeçalho")
    void deveRetornarFalhaQuandoValidadorEncontrarErros() throws Exception {
        // Arrange
        List<String> errosMock = List.of("Cabeçalho inválido");
        when(validator.validarCabecalho(any())).thenReturn(errosMock);
        when(file.getInputStream()).thenReturn(mock(InputStream.class));

        // Act
        Result<UUID> result = service.iniciarProcessamento(file);

        // Assert
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.errors()).contains("Cabeçalho inválido");
        verify(repository, never()).save(any()); // Não deve salvar se houver erro
        verify(jobLauncher, never()).run(any(), any()); // Não deve iniciar o job
    }

    @Test
    @DisplayName("Deve consultar um processamento por ID e converter para DTO")
    void deveConsultarComSucessoEConverterParaDto() {
        // Arrange
        UUID id = UUID.randomUUID();
        ProcessamentoArquivo entity = ProcessamentoArquivo.builder()
                .id(id)
                .nomeArquivo("documento.txt")
                .status(StatusProcessamento.FINALIZADO_COM_SUCESSO)
                .dataCriacao(LocalDateTime.now())
                .resumos(new ArrayList<>())
                .build();

        when(repository.findByIdWithResumos(id)).thenReturn(Optional.of(entity));

        // Act
        ProcessamentoResponse response = service.consultarProcessamento(id);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(id);
        verify(repository).findByIdWithResumos(id);
    }

    @Test
    @DisplayName("Deve lançar exceção quando o processamento não for encontrado")
    void deveLancarExcecaoAoConsultarIdInexistente() {
        UUID id = UUID.randomUUID();
        when(repository.findByIdWithResumos(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.consultarProcessamento(id))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Processamento não encontrado.");
    }

    @Test
    @DisplayName("Deve listar processamentos filtrando por status")
    void deveListarComFiltroDeStatus() {
        StatusProcessamento statusFiltro = StatusProcessamento.EM_PROCESSAMENTO;
        ProcessamentoArquivo p1 = ProcessamentoArquivo.builder()
                .id(UUID.randomUUID())
                .status(statusFiltro)
                .resumos(Collections.emptyList())
                .build();

        when(repository.findByStatusWithoutResumos(statusFiltro)).thenReturn(List.of(p1));

        List<ProcessamentoResponse> results = service.listarTodos(statusFiltro);

        assertThat(results).hasSize(1);
        verify(repository).findByStatusWithoutResumos(statusFiltro);
    }
}