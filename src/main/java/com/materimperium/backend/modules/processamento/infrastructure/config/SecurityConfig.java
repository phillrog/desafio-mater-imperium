package com.materimperium.backend.modules.processamento.infrastructure.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                // 1. Desabilita CSRF para permitir que ferramentas como Bruno/Postman façam POST
//                .csrf(AbstractHttpConfigurer::disable)
//
//                // 2. Resolve o problema do 'x-frame-options: DENY' (permite frames da mesma origem)
//                .headers(headers -> headers
//                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
//                )
//
//                // 3. Configuração de permissões de acesso
//                .authorizeHttpRequests(auth -> auth
//                        // Libera explicitamente o Swagger e a documentação da API
//                        .requestMatchers(
//                                "/v3/api-docs/**",
//                                "/swagger-ui/**",
//                                "/swagger-ui.html",
//                                "/swagger-resources/**",
//                                "/webjars/**"
//                        ).permitAll()
//
//                        // Libera os endpoints do seu módulo de processamento
//                        .requestMatchers("/api/v1/processamentos/**").permitAll()
//
//                        // Qualquer outra requisição exige autenticação
//                        .anyRequest().authenticated()
//                );
//
//        return http.build();
//    }
//}