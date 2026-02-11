package com.materimperium.backend.modules.processamento.infrastructure.batches;

import com.materimperium.backend.modules.processamento.domain.interfaces.repositories.ResumoRegistroRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.support.builder.SynchronizedItemStreamReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.VirtualThreadTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class BatchConfig {

    private final ResumoRegistroRepository resumoRepository;

    @Bean(name = "asyncJobLauncher")
    public JobLauncher asyncJobLauncher(JobRepository jobRepository) throws Exception {
        TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        // O uso de Virtual Threads para liberar o Controller imediatamente
        jobLauncher.setTaskExecutor(new VirtualThreadTaskExecutor("launcher-vt-"));
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }

    @Bean
    @StepScope
    public ItemStreamReader<String> reader(@Value("#{jobParameters['filePath']}") String filePath) {
        // FlatFileItemReader não é thread-safe por padrão
        FlatFileItemReader<String> delegate = new FlatFileItemReaderBuilder<String>()
                .name("arquivoReader")
                .resource(new FileSystemResource(filePath))
                .lineMapper((line, lineNumber) -> line)
                .build();

        // O SynchronizedItemStreamReader permite que múltiplas threads leiam o arquivo sem pular linhas
        return new SynchronizedItemStreamReaderBuilder<String>()
                .delegate(delegate)
                .build();
    }

    @Bean
    public ItemProcessor<String, String> processor() {
        return line -> {
            if (line == null || line.isBlank()) return null;

            // Remove espaços e quebras de linha (evita códigos como "1691\r")
            String cleanLine = line.trim();

            // Split pelo pipe
            String[] parts = cleanLine.split("\\|");

            // Exemplo: |1691|135|
            // parts[0] = "" (antes do primeiro |)
            // parts[1] = "1691" código resumo
            // parts[2] = "135"

            if (parts.length > 1) {
                String codigo = parts[1].trim();
                return codigo.isEmpty() ? null : codigo;
            }

            log.warn("Linha fora do padrão ignorada: {}", cleanLine);
            return null;
        };
    }

    @Bean
    @StepScope
    public ItemWriter<String> writer(@Value("#{jobParameters['processamentoId']}") String processamentoId) {
        return chunk -> {
            Long procId = Long.parseLong(processamentoId);
            // TreeMap ordena os códigos, o que evita Deadlock (threads seguem a mesma fila)
            Map<String, Long> localMap = new java.util.TreeMap<>();

            // Agregação em memória do chunk atual (reduz chamadas ao banco)
            for (String codigo : chunk.getItems()) {
                if (codigo != null) {
                    localMap.merge(codigo, 1L, Long::sum);
                }
            }

            log.info("Processando chunk de {} itens. Itens únicos para UPSERT: {}",
                    chunk.size(), localMap.size());

            // UPSERT atômico: evita carregar entidades para a memória do Hibernate
            localMap.forEach((codigo, qtd) ->
                    resumoRepository.upsertResumo(procId, codigo, qtd)
            );
        };
    }

    @Bean
    public Step processamentoStep(JobRepository jobRepository,
                                  PlatformTransactionManager transactionManager,
                                  ItemStreamReader<String> reader,
                                  ItemProcessor<String, String> processor,
                                  ItemWriter<String> writer,
                                  TaskExecutor taskExecutor) {
        return new StepBuilder("processamentoStep", jobRepository)
                .<String, String>chunk(20000, transactionManager) // Chunk de 20000 para balanço entre memória e IO
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .taskExecutor(taskExecutor) // Processamento paralelo
                .build();
    }

    @Bean
    public Job processarArquivoJob(JobRepository jobRepository, Step processamentoStep, ProcessamentoJobListener listener) {
        return new JobBuilder("processarArquivoJob", jobRepository)
                .listener(listener)
                .start(processamentoStep)
                .build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        //Evita que o Postgres exploda com excesso de concorrência
        ThreadPoolTaskExecutor executor = new org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor();

        // Sugestão: CPU cores * 2
        executor.setCorePoolSize(8);
        executor.setMaxPoolSize(16);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("batch-db-");
        executor.initialize();
        return executor;
    }
}