package com.materimperium.backend.modules.processamento.application.services;

import com.materimperium.backend.modules.processamento.domain.entities.ProcessamentoArquivo;
import com.materimperium.backend.modules.processamento.domain.services.ProcessamentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProcessamentoServiceImpl implements ProcessamentoService {


    @Override
    public UUID iniciarProcessamento(MultipartFile file) throws Exception {
        return null;
    }

    @Override
    public ProcessamentoArquivo consultarProcessamento(UUID id) {
        return null;
    }
}