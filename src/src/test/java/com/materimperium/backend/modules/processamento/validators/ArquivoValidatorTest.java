package com.materimperium.backend.modules.processamento.validators;

import com.materimperium.backend.modules.processamento.application.validators.ArquivoValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ArquivoValidatorTest {

    private final ArquivoValidator validator = new ArquivoValidator();

    @Test
    @DisplayName("Deve validar com sucesso arquivo .txt com Content-Type correto e cabeçalho 017")
    void deveValidarComSucesso017() {
        String conteudo = "|0000|017|\n|0001|0|";
        MockMultipartFile file = new MockMultipartFile(
                "file", "arquivo.txt", "text/plain", conteudo.getBytes());

        List<String> erros = validator.validar(file);

        assertThat(erros).isEmpty();
    }

    @Test
    @DisplayName("Deve falhar quando a extensão não for .txt")
    void deveFalharExtensaoInvalida() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "arquivo.pdf", "text/plain", "|0000|017|".getBytes());

        List<String> erros = validator.validar(file);

        assertThat(erros)
                .isNotEmpty()
                .contains("Arquivo inválido: Apenas extensões .txt são permitidas.");
    }

    @Test
    @DisplayName("Deve falhar quando o Content-Type não for text/plain")
    void deveFalharContentTypeInvalido() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "arquivo.txt", "application/octet-stream", "|0000|017|".getBytes());

        List<String> erros = validator.validar(file);

        assertThat(erros)
                .isNotEmpty()
                .contains("Arquivo inválido: O tipo do conteúdo deve ser text/plain.");
    }

    @Test
    @DisplayName("Deve falhar quando a primeira linha estiver incorreta")
    void deveFalharPrimeiraLinhaIncorreta() {
        String conteudo = "|X000|999|\n|0001|0|";
        MockMultipartFile file = new MockMultipartFile(
                "file", "arquivo.txt", "text/plain", conteudo.getBytes());

        List<String> erros = validator.validar(file);

        assertThat(erros)
                .anyMatch(s -> s.contains("Cabeçalho da primeira linha incorreto"));
    }

    @Test
    @DisplayName("Deve falhar quando a segunda linha estiver incorreta")
    void deveFalharSegundaLinhaIncorreta() {
        String conteudo = "|0000|006|\n|ERRO|0|";
        MockMultipartFile file = new MockMultipartFile(
                "file", "arquivo.txt", "text/plain", conteudo.getBytes());

        List<String> erros = validator.validar(file);

        assertThat(erros)
                .anyMatch(s -> s.contains("Segunda linha não contém o marcador esperado"));
    }

    @Test
    @DisplayName("Deve falhar quando o arquivo estiver vazio")
    void deveFalharArquivoVazio() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "arquivo.txt", "text/plain", new byte[0]);

        List<String> erros = validator.validar(file);

        assertThat(erros)
                .anyMatch(s -> s.contains("Cabeçalho da primeira linha incorreto"));
    }
}