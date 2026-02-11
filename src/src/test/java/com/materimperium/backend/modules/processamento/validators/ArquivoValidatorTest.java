package com.materimperium.backend.modules.processamento.validators;

import com.materimperium.backend.modules.processamento.application.validators.ArquivoValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.*;

class ArquivoValidatorTest {

    private final ArquivoValidator validator = new ArquivoValidator();

    @Test
    @DisplayName("Deve validar com sucesso quando o cabeçalho for 017 e segunda linha correta")
    void deveValidarComSucesso017() {
        String conteudo = "|0000|017|\n|0001|0|";
        InputStream is = new ByteArrayInputStream(conteudo.getBytes());

        assertThatCode(() -> validator.validarCabecalho(is))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Deve validar com sucesso quando o cabeçalho for 006 e segunda linha correta")
    void deveValidarComSucesso006() {
        String conteudo = "|0000|006|\n|0001|0|";
        InputStream is = new ByteArrayInputStream(conteudo.getBytes());

        assertThatCode(() -> validator.validarCabecalho(is))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Deve lançar exceção quando a primeira linha for inválida")
    void deveFalharPrimeiraLinhaInvalida() {
        String conteudo = "|X000|999|\n|0001|0|";
        InputStream is = new ByteArrayInputStream(conteudo.getBytes());

        assertThatThrownBy(() -> validator.validarCabecalho(is))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cabeçalho da primeira linha incorreto");
    }

    @Test
    @DisplayName("Deve lançar exceção quando a segunda linha não contiver o marcador")
    void deveFalharSegundaLinhaInvalida() {
        String conteudo = "|0000|017|\n|0002|1|";
        InputStream is = new ByteArrayInputStream(conteudo.getBytes());

        assertThatThrownBy(() -> validator.validarCabecalho(is))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Segunda linha não contém o marcador esperado");
    }

    @Test
    @DisplayName("Deve lançar exceção quando o arquivo estiver vazio")
    void deveFalharArquivoVazio() {
        InputStream is = new ByteArrayInputStream("".getBytes());

        assertThatThrownBy(() -> validator.validarCabecalho(is))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cabeçalho da primeira linha incorreto");
    }
}