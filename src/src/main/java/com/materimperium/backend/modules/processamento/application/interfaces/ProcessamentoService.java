package com.materimperium.backend.modules.processamento.application.interfaces;

import com.materimperium.backend.modules.processamento.application.dtos.ProcessamentoResponse;
import com.materimperium.backend.modules.processamento.domain.entities.ProcessamentoArquivo;
import com.materimperium.backend.modules.processamento.domain.entities.StatusProcessamento;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;


public interface ProcessamentoService {
    UUID iniciarProcessamento(MultipartFile file) throws Exception;
    ProcessamentoResponse consultarProcessamento(UUID id);
    List<ProcessamentoResponse>  listarTodos(StatusProcessamento status);
}