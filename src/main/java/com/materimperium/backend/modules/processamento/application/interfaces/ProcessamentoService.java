package com.materimperium.backend.modules.processamento.application.interfaces;

import com.materimperium.backend.modules.processamento.application.dtos.ProcessamentoCriadoResponse;
import com.materimperium.backend.modules.shared.abstractions.Result;
import com.materimperium.backend.modules.processamento.application.dtos.ProcessamentoResponse;
import com.materimperium.backend.modules.processamento.domain.entities.StatusProcessamento;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface ProcessamentoService {
    Result<ProcessamentoCriadoResponse> iniciarProcessamento(MultipartFile file) throws Exception;
    Result<ProcessamentoResponse> consultarProcessamento(Long id);
    Result<List<ProcessamentoResponse>>  listarTodos(StatusProcessamento status);
}