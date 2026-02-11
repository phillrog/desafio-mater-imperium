package com.materimperium.backend.modules.processamento.application.services;

import com.materimperium.backend.modules.shared.abstractions.Result;
import com.materimperium.backend.modules.processamento.application.dtos.ProcessamentoResponse;
import com.materimperium.backend.modules.processamento.application.dtos.ResumoResponse;
import com.materimperium.backend.modules.processamento.application.validators.ArquivoValidator;
import com.materimperium.backend.modules.processamento.domain.entities.ProcessamentoArquivo;
import com.materimperium.backend.modules.processamento.domain.entities.StatusProcessamento;
import com.materimperium.backend.modules.processamento.domain.interfaces.repositories.ProcessamentoArquivoRepository;
import com.materimperium.backend.modules.processamento.application.interfaces.ProcessamentoService;
import org.springframework.beans.factory.annotation.Qualifier; // Adicionado
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

@Service
public class ProcessamentoServiceImpl implements ProcessamentoService {
    private final ProcessamentoArquivoRepository processamentoArquivoRepository;
    private final ArquivoValidator validator;
    private final JobLauncher jobLauncher;
    private final Job processarArquivoJob;

    public ProcessamentoServiceImpl(
            ProcessamentoArquivoRepository processamentoArquivoRepository,
            ArquivoValidator validator,
            @Qualifier("asyncJobLauncher") JobLauncher jobLauncher,
            Job processarArquivoJob) {
        this.processamentoArquivoRepository = processamentoArquivoRepository;
        this.validator = validator;
        this.jobLauncher = jobLauncher;
        this.processarArquivoJob = processarArquivoJob;
    }

    @Override
    public Result<UUID> iniciarProcessamento(MultipartFile file) throws Exception {
        List<String> erros = validator.validar(file);

        if (!erros.isEmpty()) {
            return Result.failure(erros);
        }

        ProcessamentoArquivo processamento = ProcessamentoArquivo.builder()
                .nomeArquivo(file.getOriginalFilename())
                .status(StatusProcessamento.EM_PROCESSAMENTO)
                .build();

        processamento = processamentoArquivoRepository.save(processamento);
        UUID id = processamento.getId();

        Path tempFile = Files.createTempFile("upload_" + id + "_", ".txt");
        file.transferTo(tempFile.toFile());

        JobParameters params = new JobParametersBuilder()
                .addString("processamentoId", id.toString())
                .addString("filePath", tempFile.toAbsolutePath().toString())
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        // Agora este comando não trava mais o Controller!
        jobLauncher.run(processarArquivoJob, params);

        return Result.success(id);
    }

    @Override
    public ProcessamentoResponse consultarProcessamento(UUID id) {
        return processamentoArquivoRepository.findByIdWithResumos(id)
                .map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("Processamento não encontrado."));
    }

    @Override
    public List<ProcessamentoResponse> listarTodos(StatusProcessamento status) {
        return processamentoArquivoRepository.findByStatusWithoutResumos(status).stream()
                .map(this::toResponse)
                .toList();
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
                resumosDto
        );
    }
}