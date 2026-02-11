package com.materimperium.backend.modules.processamento.api.controllers;

import com.materimperium.backend.modules.processamento.application.dtos.ProcessamentoCriadoResponse;
import com.materimperium.backend.modules.shared.abstractions.Result;
import com.materimperium.backend.modules.processamento.application.dtos.ProcessamentoResponse;
import com.materimperium.backend.modules.processamento.application.interfaces.ProcessamentoService;
import com.materimperium.backend.modules.processamento.domain.entities.StatusProcessamento;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/processamento")
@RequiredArgsConstructor
@Tag(name = "Processamento de Arquivos", description = "Endpoints para carga e consulta de arquivos fiscais")
public class ProcessamentoController {

    private final ProcessamentoService processamentoService;

    @Operation(summary = "Inicia o processamento de um arquivo", description = "Faz o upload do arquivo e inicia o job de processamento assíncrono.")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ENVIO')")
    public ResponseEntity<?> uploadArquivo(@RequestParam("file") MultipartFile file) throws Exception {
        Result<ProcessamentoCriadoResponse> result = processamentoService.iniciarProcessamento(file);

        if (!result.isSuccess()) {
            return ResponseEntity.badRequest().body(result); // Retorna 400 com lista de erros
        }

        return ResponseEntity.accepted().body(result);
    }

    @Operation(summary = "Consulta um processamento por ID", description = "Retorna os detalhes do processamento e a contagem de registros processados.")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ENVIO', 'CONSULTA')")
    public ResponseEntity<ProcessamentoResponse> consultar(@PathVariable Long id) {
        ProcessamentoResponse processamento = processamentoService.consultarProcessamento(id);
        return ResponseEntity.ok(processamento);
    }

    @Operation(summary = "Consulta todos os processamentos", description = "Retorna os detalhes de todos os processamentos e permite filtrar por status.")
    @GetMapping("")
    @PreAuthorize("hasRole('CONSULTA')")
    public ResponseEntity<List<ProcessamentoResponse>> listarTodos(
            @RequestParam(required = false) StatusProcessamento status) {
        List<ProcessamentoResponse> processamentos = processamentoService.listarTodos(status);
        return ResponseEntity.ok(processamentos);
    }
}