package com.materimperium.backend.modules.processamento.application.services;

import com.materimperium.backend.modules.processamento.application.dtos.ProcessamentoCriadoResponse;
import com.materimperium.backend.modules.processamento.application.interfaces.AuthenticatedUserService;
import com.materimperium.backend.modules.shared.abstractions.Result;
import com.materimperium.backend.modules.processamento.application.dtos.ProcessamentoResponse;
import com.materimperium.backend.modules.processamento.application.dtos.ResumoResponse;
import com.materimperium.backend.modules.processamento.application.validators.ArquivoValidator;
import com.materimperium.backend.modules.processamento.domain.entities.ProcessamentoArquivo;
import com.materimperium.backend.modules.processamento.domain.entities.StatusProcessamento;
import com.materimperium.backend.modules.processamento.domain.interfaces.repositories.ProcessamentoArquivoRepository;
import com.materimperium.backend.modules.processamento.application.interfaces.ProcessamentoService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProcessamentoServiceImpl implements ProcessamentoService {
    private final ProcessamentoArquivoRepository processamentoArquivoRepository;
    private final ArquivoValidator validator;
    private final JobLauncher jobLauncher;
    private final Job processarArquivoJob;
    private final AuthenticatedUserService authenticatedUserService;

    public ProcessamentoServiceImpl(
            ProcessamentoArquivoRepository processamentoArquivoRepository,
            ArquivoValidator validator,
            @Qualifier("asyncJobLauncher") JobLauncher jobLauncher,
            Job processarArquivoJob,
            AuthenticatedUserService authenticatedUserService) {
        this.processamentoArquivoRepository = processamentoArquivoRepository;
        this.validator = validator;
        this.jobLauncher = jobLauncher;
        this.processarArquivoJob = processarArquivoJob;
        this.authenticatedUserService = authenticatedUserService;
    }

    @Override
    public Result<ProcessamentoCriadoResponse> iniciarProcessamento(MultipartFile file) throws Exception {
        List<String> erros = validator.validar(file);

        if (!erros.isEmpty()) {
            return Result.failure(erros);
        }

        Integer idUsuario = authenticatedUserService.getAuthenticatedUserId();

        ProcessamentoArquivo processamento = ProcessamentoArquivo.builder()
                .nomeArquivo(file.getOriginalFilename())
                .status(StatusProcessamento.EM_PROCESSAMENTO)
                .usuarioId(idUsuario)
                .dataHoraInicio(LocalDateTime.now())
                .build();

        processamento = processamentoArquivoRepository.save(processamento);
        Long id = processamento.getId();

        Path tempFile = Files.createTempFile("upload_" + id + "_", ".txt");
        file.transferTo(tempFile.toFile());

        JobParameters params = new JobParametersBuilder()
                .addLong("processamentoId", id)
                .addString("filePath", tempFile.toAbsolutePath().toString())
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();
        
        jobLauncher.run(processarArquivoJob, params);

        return Result.success(new ProcessamentoCriadoResponse(id,StatusProcessamento.EM_PROCESSAMENTO.toString()));
    }

    @Override
    public Result<ProcessamentoResponse> consultarProcessamento(Long id) {
        return processamentoArquivoRepository.findByIdWithResumos(id)
                .map(entity -> Result.success(this.toResponse(entity)))
                .orElseGet(() -> Result.failure("Processamento com ID " + id + " não encontrado."));
    }

    @Override
    public Result<List<ProcessamentoResponse>> listarTodos(StatusProcessamento status) {
        List<ProcessamentoResponse> lista = processamentoArquivoRepository.findByStatusWithoutResumos(status)
                .stream()
                .map(this::toResponse)
                .toList();

        return Result.success(lista);
    }

    private ProcessamentoResponse toResponse(ProcessamentoArquivo entity) {
        List<ResumoResponse> resumosDto = entity.getResumos().stream()
                .map(r -> new ResumoResponse(r.getCodigoRegistro(), r.getQuantidade()))
                .toList();

        return new ProcessamentoResponse(
                entity.getId(),
                entity.getNomeArquivo(),
                entity.getStatus().name(),
                entity.getDataCriacao(),
                entity.getDataHoraInicio(),
                entity.getDataHoraFinalizou(),
                resumosDto
        );
    }
}