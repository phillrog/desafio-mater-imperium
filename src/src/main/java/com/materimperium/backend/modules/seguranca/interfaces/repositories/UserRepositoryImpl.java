package com.materimperium.backend.modules.seguranca.interfaces.repositories;

import com.materimperium.backend.modules.seguranca.domain.entities.User;
import com.materimperium.backend.modules.seguranca.domain.repositories.UserRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRepositoryImpl extends JpaRepository<User, Integer>, UserRepository {
    Optional<User> findByEmail(String email);
}