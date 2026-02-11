package com.materimperium.backend.modules.seguranca.domain.repositories;

import com.materimperium.backend.modules.seguranca.domain.entities.Token;
import java.util.List;
import java.util.Optional;

public interface TokenRepository {

    List<Token> findAllValidTokenByUser(Integer id);

    Optional<Token> findByToken(String token);

    <S extends Token> List<S> saveAll(Iterable<S> entities);
    Token save(Token token);
}
