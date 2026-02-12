package com.materimperium.backend.modules.seguranca.api.constrollers;


import com.materimperium.backend.modules.seguranca.api.request.AuthenticationRequest;
import com.materimperium.backend.modules.seguranca.api.request.AuthenticationResponse;
import com.materimperium.backend.modules.seguranca.api.request.RegisterRequest;
import com.materimperium.backend.modules.seguranca.applications.services.AuthenticationService;
import com.materimperium.backend.modules.shared.abstractions.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints para registro e login de usuários")
public class AuthController {

    private final AuthenticationService service;

    @Operation(summary = "Registrar um novo usuário (ENVIO ou CONSULTA)")
    @PostMapping("/register")
    public ResponseEntity<Result<AuthenticationResponse>> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(service.register(request));
    }

    @Operation(summary = "Autenticar usuário e obter token Bearer")
    @PostMapping("/authenticate")
    public ResponseEntity<Result<AuthenticationResponse>> authenticate(
            @Valid @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }
}