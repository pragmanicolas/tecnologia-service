package com.tecnologia.service.application.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import reactor.core.publisher.Mono;

@Configuration
@Profile("test")  // Aplicar esta configuración solo cuando el perfil de prueba esté activo
public class TestSecurityConfig {


    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http.csrf(csrf -> csrf.disable())  // Deshabilitar CSRF para pruebas
                .authorizeExchange(exchanges -> exchanges.anyExchange().permitAll())  // Permitir todas las solicitudes
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance()) // Evitar contexto de seguridad
                .authenticationManager(authentication -> Mono.empty()); // No autenticación requerida
        return http.build();
    }
}
