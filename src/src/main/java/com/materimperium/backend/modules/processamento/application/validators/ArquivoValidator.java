package com.materimperium.backend.modules.processamento.application.validators;

import org.springframework.stereotype.Component;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class ArquivoValidator {

    public List<String> validarCabecalho(InputStream inputStream) {
        List<String> erros = new ArrayList<>();

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
            erros.add("Erro técnico ao ler o arquivo: " + e.getMessage());
        }

        return erros;
    }
}