package com.materimperium.backend.modules.processamento.domain.services;

import com.materimperium.backend.modules.processamento.domain.entities.ProcessamentoArquivo;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;


public interface ProcessamentoService {
    UUID iniciarProcessamento(MultipartFile file) throws Exception;
    ProcessamentoArquivo consultarProcessamento(UUID id);
}