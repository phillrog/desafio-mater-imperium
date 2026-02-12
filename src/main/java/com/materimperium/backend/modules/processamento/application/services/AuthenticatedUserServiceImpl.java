package com.materimperium.backend.modules.processamento.application.services;

import com.materimperium.backend.modules.processamento.application.interfaces.AuthenticatedUserService;
import com.materimperium.backend.modules.seguranca.domain.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticatedUserServiceImpl implements AuthenticatedUserService {

    @Override
    public Integer getAuthenticatedUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Usuário não autenticado");
        }
        
        User usuario = (User) authentication.getPrincipal();
        return usuario.getId();
    }
}
