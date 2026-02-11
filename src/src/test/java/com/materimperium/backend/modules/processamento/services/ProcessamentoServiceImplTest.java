package com.materimperium.backend.modules.processamento.services;

import com.materimperium.backend.modules.processamento.application.interfaces.AuthenticatedUserService;
import com.materimperium.backend.modules.shared.abstractions.Result;
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

    @Mock
    private AuthenticatedUserService authenticatedUserService;

    @Test
    @DisplayName("Deve iniciar processamento com sucesso quando validador não retorna erros")
    void deveIniciarProcessamentoComSucesso() throws Exception {
        // Arrange
        Long idManual = 1L;
        Integer usuarioIdMock = 123;

        ProcessamentoArquivo mockup = ProcessamentoArquivo.builder()
                .id(idManual)
                .nomeArquivo("teste.txt")
                .status(StatusProcessamento.EM_PROCESSAMENTO)
                .build();

        when(authenticatedUserService.getAuthenticatedUserId()).thenReturn(usuarioIdMock);

        when(validator.validar(any())).thenReturn(Collections.emptyList());
        when(file.getOriginalFilename()).thenReturn("teste.txt");
        when(repository.save(any())).thenReturn(mockup);

        doAnswer(invocation -> null).when(file).transferTo(any(java.io.File.class));

        // Act
        Result<Long> result = service.iniciarProcessamento(file);

        // Assert
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.value()).isEqualTo(idManual);
        verify(validator).validar(file);
        verify(jobLauncher).run(any(), any());
    }

    @Test
    @DisplayName("Deve retornar falha quando o validador encontrar erros")
    void deveRetornarFalhaQuandoValidadorEncontrarErros() throws Exception {
        // Arrange
        List<String> errosMock = List.of("Arquivo inválido: Apenas extensões .txt são permitidas.");
        when(validator.validar(file)).thenReturn(errosMock);

        // Act
        Result<Long> result = service.iniciarProcessamento(file);

        // Assert
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.errors()).contains("Arquivo inválido: Apenas extensões .txt são permitidas.");
        verify(repository, never()).save(any());
        verify(jobLauncher, never()).run(any(), any());
    }

    @Test
    @DisplayName("Deve consultar um processamento por ID e converter para DTO")
    void deveConsultarComSucessoEConverterParaDto() {
        // Arrange
        Long id = 1L;
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
        Long id = 1L;
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
                .id(1L)
                .status(statusFiltro)
                .resumos(Collections.emptyList())
                .build();

        when(repository.findByStatusWithoutResumos(statusFiltro)).thenReturn(List.of(p1));

        List<ProcessamentoResponse> results = service.listarTodos(statusFiltro);

        assertThat(results).hasSize(1);
        verify(repository).findByStatusWithoutResumos(statusFiltro);
    }
}