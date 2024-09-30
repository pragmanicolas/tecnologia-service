package com.tecnologia.service.adapters.in.security;

import com.tecnologia.service.adapters.out.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class JwtRequestFilter implements WebFilter {

    private final JwtService jwtService;
    private final ReactiveUserDetailsService reactiveUserDetailsService;

    public JwtRequestFilter(JwtService jwtService, ReactiveUserDetailsService reactiveUserDetailsService) {
        this.jwtService = jwtService;
        this.reactiveUserDetailsService = reactiveUserDetailsService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        // Excluir la ruta de login del filtro
        if (exchange.getRequest().getURI().getPath().contains("/api/auth/login")) {
            return chain.filter(exchange);  // Continúa sin filtrar
        }

        return jwtService.extractToken(exchange)
                .flatMap(token -> jwtService.validateToken(token)
                        .flatMap(isValid -> {
                            if (isValid) {
                                // Si el token es válido, extraer autenticación y continuar
                                return jwtService.getAuthentication(token)
                                        .flatMap(authentication -> chain.filter(exchange)
                                                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication)));
                            } else {
                                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                                return exchange.getResponse().setComplete();
                            }
                        }))
                .switchIfEmpty(chain.filter(exchange)) // Si no hay token, continúa el filtro
                .onErrorResume(e -> {
                    exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                    return exchange.getResponse().setComplete();
                });
    }

}
