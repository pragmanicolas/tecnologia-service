package com.tecnologia.service.adapters.in.security;

import com.tecnologia.service.domain.User;
import com.tecnologia.service.domain.UserService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements ReactiveUserDetailsService {

    private final UserService userService;  // Servicio de dominio

    public CustomUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userService.findByUsername(username)  // Devuelve un Mono<User> del servicio de dominio
                .map(this::mapToUserDetails)  // Mapea de User (dominio) a UserDetails (Spring Security)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found")));
    }

    // Método que realiza el mapeo de User (dominio) a UserDetails (Spring Security)
    private UserDetails mapToUserDetails(User user) {
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),  // El nombre de usuario del dominio
                user.getPassword(),  // La contraseña del dominio
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()))  // Mapeo del rol a la autoridad de Spring Security
        );
    }
}

