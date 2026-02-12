package com.materimperium.backend.modules.seguranca.domain.repositories;

import com.materimperium.backend.modules.processamento.domain.entities.ProcessamentoArquivo;
import com.materimperium.backend.modules.seguranca.domain.entities.User;

import java.util.Optional;

public interface UserRepository  {
    Optional<User> findByEmail(String email);
    User save(User usuario);
}
