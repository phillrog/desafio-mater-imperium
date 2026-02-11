package com.materimperium.backend.modules.processamento.application.services;

import com.materimperium.backend.modules.processamento.application.dtos.ProcessamentoResponse;
import com.materimperium.backend.modules.processamento.application.dtos.ResumoResponse;
import com.materimperium.backend.modules.processamento.application.validators.ArquivoValidator;
import com.materimperium.backend.modules.processamento.domain.entities.ProcessamentoArquivo;
import com.materimperium.backend.modules.processamento.domain.entities.StatusProcessamento;
import com.materimperium.backend.modules.processamento.domain.interfaces.repositories.ProcessamentoArquivoRepository;
import com.materimperium.backend.modules.processamento.application.interfaces.ProcessamentoService;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class ProcessamentoServiceImpl implements ProcessamentoService {
    private final ProcessamentoArquivoRepository processamentoArquivoRepository;
    private final ArquivoValidator validator;
    private final JobLauncher jobLauncher;
    private final Job processarArquivoJob;

    @Override
    public UUID iniciarProcessamento(MultipartFile file) throws Exception {
        // Validação de cabeçalho (Regra de Negócio)
        validator.validarCabecalho(file.getInputStream());

        ProcessamentoArquivo processamento = ProcessamentoArquivo.builder()
                .nomeArquivo(file.getOriginalFilename())
                .status(StatusProcessamento.EM_PROCESSAMENTO)
                .build();

        processamento = processamentoArquivoRepository.save(processamento);
        UUID id = processamento.getId();

        // Persistência temporária para processamento em Chunk do Spring Batch
        Path tempFile = Files.createTempFile("upload_", "_" + file.getOriginalFilename());
        file.transferTo(tempFile.toFile());

        JobParameters params = new JobParametersBuilder()
                .addString("processamentoId", id.toString())
                .addString("filePath", tempFile.toString())
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        jobLauncher.run(processarArquivoJob, params);

        return id;
    }

    @Override
    public ProcessamentoResponse consultarProcessamento(UUID id) {
        return processamentoArquivoRepository.findByIdWithResumos(id)
                .stream().map(this::toResponse)
                .findFirst()
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