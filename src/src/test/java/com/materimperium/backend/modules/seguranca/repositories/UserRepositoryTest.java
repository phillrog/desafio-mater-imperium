package com.materimperium.backend.modules.seguranca.repositories;

import com.materimperium.backend.modules.seguranca.domain.entities.User;
import com.materimperium.backend.modules.seguranca.domain.entities.Role;
import com.materimperium.backend.modules.seguranca.domain.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Deve encontrar usuário por email com sucesso")
    void findByEmailSuccess() {
        // Arrange
        User user = User.builder()
                .firstname("João")
                .lastname("Silva")
                .email("joao@materimperium.com")
                .password("123456")
                .role(Role.ENVIO)
                .build();
        userRepository.save(user);

        // Act
        Optional<User> result = userRepository.findByEmail("joao@materimperium.com");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    @DisplayName("Não deve encontrar usuário quando o email não existir")
    void findByEmailNotFound() {
        // Act
        Optional<User> result = userRepository.findByEmail("inexistente@teste.com");

        // Assert
        assertThat(result).isEmpty();
    }
}