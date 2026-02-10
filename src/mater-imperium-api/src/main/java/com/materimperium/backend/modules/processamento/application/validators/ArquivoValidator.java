package com.materimperium.backend.modules.processamento.application.validators;

import org.springframework.stereotype.Component;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Component
public class ArquivoValidator {

    public void validarCabecalho(InputStream inputStream) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        // Regra: 1ª linha deve começar com |0000|017| ou |0000|006|
        String primeiraLinha = reader.readLine();
        if (primeiraLinha == null || (!primeiraLinha.startsWith("|0000|017|") && !primeiraLinha.startsWith("|0000|006|"))) {
            throw new IllegalArgumentException("Arquivo inválido: Cabeçalho da primeira linha incorreto.");
        }

        // Regra: 2ª linha deve conter exatamente |0001|0|
        String segundaLinha = reader.readLine();
        if (segundaLinha == null || !segundaLinha.contains("|0001|0|")) {
            throw new IllegalArgumentException("Arquivo inválido: Segunda linha não contém o marcador esperado.");
        }
    }
}