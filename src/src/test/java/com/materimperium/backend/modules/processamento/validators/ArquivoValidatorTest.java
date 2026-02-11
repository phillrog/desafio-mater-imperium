package com.materimperium.backend.modules.processamento.validators;

import com.materimperium.backend.modules.processamento.application.validators.ArquivoValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ArquivoValidatorTest {

    private final ArquivoValidator validator = new ArquivoValidator();

    @Test
    @DisplayName("Deve retornar lista vazia quando o cabeçalho for 017 e segunda linha correta")
    void deveValidarComSucesso017() {
        String conteudo = "|0000|017|\n|0001|0|";
        InputStream is = new ByteArrayInputStream(conteudo.getBytes());

        List<String> erros = validator.validarCabecalho(is);

        assertThat(erros).isEmpty();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando o cabeçalho for 006 e segunda linha correta")
    void deveValidarComSucesso006() {
        String conteudo = "|0000|006|\n|0001|0|";
        InputStream is = new ByteArrayInputStream(conteudo.getBytes());

        List<String> erros = validator.validarCabecalho(is);

        assertThat(erros).isEmpty();
    }

    @Test
    @DisplayName("Deve retornar erro quando a primeira linha for inválida")
    void deveFalharPrimeiraLinhaInvalida() {
        String conteudo = "|X000|999|\n|0001|0|";
        InputStream is = new ByteArrayInputStream(conteudo.getBytes());

        List<String> erros = validator.validarCabecalho(is);

        assertThat(erros)
                .isNotEmpty()
                .anyMatch(s -> s.contains("Cabeçalho da primeira linha incorreto"));
    }

    @Test
    @DisplayName("Deve retornar erro quando a segunda linha não contiver o marcador")
    void deveFalharSegundaLinhaInvalida() {
        String conteudo = "|0000|017|\n|0002|1|";
        InputStream is = new ByteArrayInputStream(conteudo.getBytes());

        List<String> erros = validator.validarCabecalho(is);

        assertThat(erros)
                .isNotEmpty()
                .anyMatch(s -> s.contains("Segunda linha não contém o marcador esperado"));
    }

    @Test
    @DisplayName("Deve retornar erro quando o arquivo estiver vazio")
    void deveFalharArquivoVazio() {
        InputStream is = new ByteArrayInputStream("".getBytes());

        List<String> erros = validator.validarCabecalho(is);

        assertThat(erros)
                .isNotEmpty()
                .anyMatch(s -> s.contains("Cabeçalho da primeira linha incorreto"));
    }
}