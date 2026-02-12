package com.materimperium.backend.modules.processamento.infrastructure.batches;

import com.materimperium.backend.modules.processamento.domain.entities.StatusProcessamento;
import com.materimperium.backend.modules.processamento.domain.interfaces.repositories.ProcessamentoArquivoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProcessamentoJobListener implements JobExecutionListener {

    private final ProcessamentoArquivoRepository processamentoArquivoRepository;

    @Override
    public void afterJob(JobExecution jobExecution) {
        Long processamentoId = jobExecution.getJobParameters().getLong("processamentoId");
        var proc = processamentoArquivoRepository.findById(processamentoId).orElseThrow();

        if (jobExecution.getStatus().isUnsuccessful()) {
            proc.setStatus(StatusProcessamento.FINALIZADO_COM_ERROS);
        } else {
            proc.setStatus(StatusProcessamento.FINALIZADO_COM_SUCESSO);
        }

        proc.setDataHoraFinalizou(LocalDateTime.now());

        var stepExecution = jobExecution.getStepExecutions().iterator().next();
        long lidas = stepExecution.getReadCount();
        long escritas = stepExecution.getWriteCount();

        log.info("=== FIM DO PROCESSAMENTO ===");
        log.info("Total de linhas lidas do arquivo: {}", lidas);
        log.info("Total de registros enviados para UPSERT: {}", escritas);
        log.info("Status Final: {}", jobExecution.getStatus());
        processamentoArquivoRepository.save(proc);
    }
}