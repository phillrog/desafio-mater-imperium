package com.materimperium.backend.modules.seguranca.applications.services;

import com.materimperium.backend.modules.seguranca.api.request.AuthenticationRequest;
import com.materimperium.backend.modules.seguranca.api.request.AuthenticationResponse;
import com.materimperium.backend.modules.seguranca.api.request.RegisterRequest;
import com.materimperium.backend.modules.seguranca.domain.entities.Token;
import com.materimperium.backend.modules.seguranca.domain.entities.TokenType;
import com.materimperium.backend.modules.seguranca.domain.entities.User;
import com.materimperium.backend.modules.seguranca.domain.repositories.TokenRepository;
import com.materimperium.backend.modules.seguranca.domain.repositories.UserRepository;

import com.materimperium.backend.modules.seguranca.infrastructure.services.JwtService;
import com.materimperium.backend.modules.shared.abstractions.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public Result<AuthenticationResponse> register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return Result.failure("Este e-mail já está em uso.");
        }

        try {
            var user = User.builder()
                    .firstname(request.getFirstname())
                    .lastname(request.getLastname())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(request.getRole())
                    .build();

            var savedUser = userRepository.save(user);
            var jwtToken = jwtService.generateToken(user);
            saveUserToken(savedUser, jwtToken);

            var response = AuthenticationResponse.builder()
                    .accessToken(jwtToken)
                    .build();

            return Result.success(response);
        } catch (Exception e) {
            return Result.failure("Erro ao processar o cadastro: " + e.getMessage());
        }
    }

    public Result<AuthenticationResponse> authenticate(AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            var user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado após autenticação"));

            var jwtToken = jwtService.generateToken(user);
            revokeAllUserTokens(user);
            saveUserToken(user, jwtToken);

            var response = AuthenticationResponse.builder()
                    .accessToken(jwtToken)
                    .build();

            return Result.success(response);
        } catch (org.springframework.security.core.AuthenticationException e) {
            return Result.failure("E-mail ou senha inválidos.");
        } catch (Exception e) {
            return Result.failure("Ocorreu um erro inesperado na autenticação.");
        }
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }
}