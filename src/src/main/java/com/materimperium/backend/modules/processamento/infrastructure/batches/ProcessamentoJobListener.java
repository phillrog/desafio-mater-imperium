package com.materimperium.backend.modules.processamento.infrastructure.batches;

import com.materimperium.backend.modules.processamento.domain.entities.StatusProcessamento;
import com.materimperium.backend.modules.processamento.domain.interfaces.repositories.ProcessamentoArquivoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProcessamentoJobListener implements JobExecutionListener {

    private final ProcessamentoArquivoRepository processamentoArquivoRepository;

    @Override
    public void afterJob(JobExecution jobExecution) {
        String procId = jobExecution.getJobParameters().getString("processamentoId");
        var proc = processamentoArquivoRepository.findById(UUID.fromString(procId)).orElseThrow();

        if (jobExecution.getStatus().isUnsuccessful()) {
            proc.setStatus(StatusProcessamento.FINALIZADO_COM_ERROS);
        } else {
            proc.setStatus(StatusProcessamento.FINALIZADO_COM_SUCESSO);
        }
        processamentoArquivoRepository.save(proc);
    }
}