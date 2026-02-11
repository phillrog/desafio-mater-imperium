package com.materimperium.backend.modules.processamento.application.validators;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class ArquivoValidator {

    public List<String> validar(MultipartFile file) {
        List<String> erros = new ArrayList<>();

        // 1. Validação de Extensão
        String nomeArquivo = file.getOriginalFilename();
        if (nomeArquivo == null || !nomeArquivo.toLowerCase().endsWith(".txt")) {
            erros.add("Arquivo inválido: Apenas extensões .txt são permitidas.");
        }

        // 2. Validação de Content-Type
        // O tipo padrão para TXT é "text/plain"
        String contentType = file.getContentType();
        if (contentType == null || !contentType.equals("text/plain")) {
            erros.add("Arquivo inválido: O tipo do conteúdo deve ser text/plain.");
        }

        // Se houver erros de formato, nem abrimos o arquivo para poupar recursos
        if (!erros.isEmpty()) {
            return erros;
        }

        // 3. Validação de Cabeçalho (Conteúdo)
        try (InputStream inputStream = file.getInputStream()) {
            erros.addAll(validarConteudo(inputStream));
        } catch (Exception e) {
            erros.add("Erro técnico ao ler o arquivo: " + e.getMessage());
        }

        return erros;
    }

    private List<String> validarConteudo(InputStream inputStream) {
        List<String> erros = new ArrayList<>();
        // Usamos BufferedReader para ler apenas o necessário sem carregar o arquivo gigante na RAM
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            String primeiraLinha = reader.readLine();
            if (primeiraLinha == null || (!primeiraLinha.startsWith("|0000|017|") && !primeiraLinha.startsWith("|0000|006|"))) {
                erros.add("Arquivo inválido: Cabeçalho da primeira linha incorreto.");
            }

            String segundaLinha = reader.readLine();
            if (segundaLinha == null || !segundaLinha.contains("|0001|0|")) {
                erros.add("Arquivo inválido: Segunda linha não contém o marcador esperado.");
            }
        } catch (Exception e) {
            erros.add("Erro ao processar o conteúdo do arquivo.");
        }
        return erros;
    }
}