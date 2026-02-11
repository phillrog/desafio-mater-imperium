package com.materimperium.backend.modules.seguranca.interfaces.repositories;


import com.materimperium.backend.modules.seguranca.domain.entities.Token;
import com.materimperium.backend.modules.seguranca.domain.repositories.TokenRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface TokenRepositoryImpl extends JpaRepository<Token, Integer>, TokenRepository {
    @Query("""
          select t from Token t inner join User u on t.user.id = u.id
          where u.id = :id and t.expired = false and t.revoked = false""")
    List<Token> findAllValidTokenByUser(Integer id);

    Optional<Token> findByToken(String token);

}