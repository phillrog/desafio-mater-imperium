package com.materimperium.backend.modules.seguranca.repositories;

import com.materimperium.backend.modules.seguranca.domain.entities.Token;
import com.materimperium.backend.modules.seguranca.domain.entities.User;
import com.materimperium.backend.modules.seguranca.domain.entities.Role;
import com.materimperium.backend.modules.seguranca.domain.entities.TokenType;
import com.materimperium.backend.modules.seguranca.domain.repositories.TokenRepository;
import com.materimperium.backend.modules.seguranca.domain.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TokenRepositoryTest {

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Deve buscar todos os tokens válidos de um usuário específico")
    void findAllValidTokenByUserSuccess() {
        // Arrange
        User user = User.builder()
                .firstname("Admin")
                .email("admin@teste.com")
                .role(Role.CONSULTA)
                .build();
        userRepository.save(user);

        Token t1 = Token.builder()
                .token("valid-token-1")
                .expired(false)
                .revoked(false)
                .user(user)
                .tokenType(TokenType.BEARER)
                .build();

        Token t2 = Token.builder()
                .token("expired-token")
                .expired(true)
                .revoked(false)
                .user(user)
                .build();

        tokenRepository.saveAll(List.of(t1, t2));

        // Act
        List<Token> validTokens = tokenRepository.findAllValidTokenByUser(user.getId());

        // Assert
        assertThat(validTokens).hasSize(1);
        assertThat(validTokens.get(0).getToken()).isEqualTo("valid-token-1");
    }

    @Test
    @DisplayName("Deve encontrar token pela string do hash")
    void findByTokenString() {
        // Arrange
        String tokenStr = "secret-hash-123";
        Token token = Token.builder()
                .token(tokenStr)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);

        // Act
        var result = tokenRepository.findByToken(tokenStr);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getToken()).isEqualTo(tokenStr);
    }
}