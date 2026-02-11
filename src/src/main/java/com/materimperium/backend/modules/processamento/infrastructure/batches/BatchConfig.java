package com.materimperium.backend.modules.processamento.infrastructure.batches;


import com.materimperium.backend.modules.processamento.domain.entities.ProcessamentoArquivo;
import com.materimperium.backend.modules.processamento.domain.interfaces.repositories.ProcessamentoArquivoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class BatchConfig {

    private final ProcessamentoArquivoRepository processamentoArquivoRepository;

    @Bean
    @StepScope
    public FlatFileItemReader<String> reader(@Value("#{jobParameters['filePath']}") String filePath) {
        return new FlatFileItemReaderBuilder<String>()
                .name("arquivoReader")
                .resource(new FileSystemResource(filePath))
                .lineMapper((line, lineNumber) -> line) // Lê a linha pura para processarmos o pipe
                .build();
    }

    @Bean
    public ItemProcessor<String, String> processor() {
        return line -> {
            if (line == null || line.isBlank()) return null;
            String[] parts = line.split("\\|");
            // O primeiro elemento após o primeiro pipe é o código do registro (ex: 0000, 0001, C100)
            return (parts.length > 1) ? parts[1] : null;
        };
    }

    @Bean
    @StepScope
    public ItemWriter<String> writer(@Value("#{jobParameters['processamentoId']}") String processamentoId) {
        return chunks -> {
            ProcessamentoArquivo proc = processamentoArquivoRepository.findById(UUID.fromString(processamentoId))
                    .orElseThrow(() -> new RuntimeException("Processamento não encontrado no Batch"));

            Map<String, Long> localMap = new HashMap<>();
            chunks.forEach(codigo -> localMap.merge(codigo, 1L, Long::sum));

            localMap.forEach((codigo, qtd) -> {
                // Procuramos se o código já existe na lista atual do processamento
                proc.getResumos().stream()
                        .filter(r -> r.getCodigoRegistro().equals(codigo))
                        .findFirst()
                        .ifPresentOrElse(
                                r -> r.setQuantidade(r.getQuantidade() + qtd), // Se existe, soma
                                () -> proc.adicionarResumo(codigo, qtd)
                        );
            });

            processamentoArquivoRepository.save(proc);
        };
    }

    @Bean
    public Step processamentoStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                                  FlatFileItemReader<String> reader, ItemProcessor<String, String> processor, ItemWriter<String> writer) {
        return new StepBuilder("processamentoStep", jobRepository)
                .<String, String>chunk(100, transactionManager) // Processa de 100 em 100 linhas
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public Job processarArquivoJob(JobRepository jobRepository, Step processamentoStep, ProcessamentoJobListener listener) {
        return new JobBuilder("processarArquivoJob", jobRepository)
                .listener(listener)
                .start(processamentoStep)
                .build();
    }
}