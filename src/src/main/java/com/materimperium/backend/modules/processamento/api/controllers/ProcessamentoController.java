package com.materimperium.backend.modules.processamento.api.controllers;

import com.materimperium.backend.modules.processamento.application.abstractions.Result;
import com.materimperium.backend.modules.processamento.application.dtos.ProcessamentoResponse;
import com.materimperium.backend.modules.processamento.application.interfaces.ProcessamentoService;
import com.materimperium.backend.modules.processamento.domain.entities.StatusProcessamento;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/processamentos")
@RequiredArgsConstructor
@Tag(name = "Processamento de Arquivos", description = "Endpoints para carga e consulta de arquivos fiscais")
public class ProcessamentoController {

    private final ProcessamentoService processamentoService;

    @Operation(summary = "Inicia o processamento de um arquivo", description = "Faz o upload do arquivo e inicia o job de processamento assíncrono.")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadArquivo(@RequestParam("file") MultipartFile file) throws Exception {
        Result<UUID> result = processamentoService.iniciarProcessamento(file);

        if (!result.isSuccess()) {
            return ResponseEntity.badRequest().body(result.errors()); // Retorna 400 com lista de erros
        }

        return ResponseEntity.accepted().body(result.value());
    }

    @Operation(summary = "Consulta um processamento por ID", description = "Retorna os detalhes do processamento e a contagem de registros processados.")
    @GetMapping("/{id}")
    public ResponseEntity<ProcessamentoResponse> consultar(@PathVariable UUID id) {
        ProcessamentoResponse processamento = processamentoService.consultarProcessamento(id);
        return ResponseEntity.ok(processamento);
    }

    @Operation(summary = "Consulta todos os processamentos", description = "Retorna os detalhes de todos os processamentos e permite filtrar por status.")
    @GetMapping("")
    public ResponseEntity<List<ProcessamentoResponse>> listarTodos(
            @RequestParam(required = false) StatusProcessamento status) {
        List<ProcessamentoResponse> processamentos = processamentoService.listarTodos(status);
        return ResponseEntity.ok(processamentos);
    }
}