package com.tecnologia.service.adapters.out.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    @Value("${jwt.expirationMs}")
    private long expirationTimeMs;

    @Value("${jwt.secret}")
    private String secretKey;

    // Genera el algoritmo para firmar/verificar el token usando Auth0
    private Algorithm getSigningAlgorithm() {
        return Algorithm.HMAC256(secretKey);
    }

    // Extraer el nombre de usuario del token de manera reactiva
    public Mono<String> extractUsername(String token) {
        return Mono.fromCallable(() -> {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getSubject();
        });
    }

    // Validar el token JWT de manera reactiva
    public Mono<Boolean> validateToken(String token) {
        return Mono.fromCallable(() -> {
            try {
                JWTVerifier verifier = JWT.require(getSigningAlgorithm()).build();
                verifier.verify(token);
                return true;
            } catch (JWTVerificationException e) {
                logger.error("Error al verificar el token: {}", e.getMessage());
                return false;
            }
        });
    }

    // Verificar si el token ha expirado
    public Mono<Boolean> isTokenExpired(String token) {
        return extractExpiration(token)
                .map(expirationDate -> expirationDate.before(new Date()));
    }

    // Extraer la fecha de expiración del token
    public Mono<Date> extractExpiration(String token) {
        return Mono.fromCallable(() -> {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getExpiresAt();
        });
    }

    // Generar un token basado en los detalles del usuario
    public Mono<String> generateToken(UserDetails userDetails) {
        return Mono.fromCallable(() -> {
            try {
                List<String> roles = userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList());

                return JWT.create()
                        .withSubject(userDetails.getUsername())
                        .withClaim("roles", roles)
                        .withIssuedAt(new Date())
                        .withExpiresAt(new Date(System.currentTimeMillis() + expirationTimeMs))
                        .sign(getSigningAlgorithm());
            } catch (Exception e) {
                logger.error("Error al generar el token: {}", e.getMessage());
                throw new RuntimeException("Error al generar el token");
            }
        });
    }

    // Extraer los roles del token JWT
    public Mono<List<String>> extractRoles(String token) {
        return Mono.fromCallable(() -> {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("roles").asList(String.class);
        });
    }

    // Obtener la autenticación basada en el token JWT
    public Mono<Authentication> getAuthentication(String token) {
        logger.info("Intentando autenticar con token: {}", token);
        return extractUsername(token)
                .flatMap(username -> extractRoles(token)
                        .map(roles -> {
                            logger.info("Username extraído: {}", username);
                            List<GrantedAuthority> authorities = roles.stream()
                                    .map(SimpleGrantedAuthority::new)
                                    .collect(Collectors.toList());
                            return (Authentication) new UsernamePasswordAuthenticationToken(username, null, authorities);
                        }))
                .onErrorResume(e -> {
                    logger.error("Error al extraer autenticación: {}", e.getMessage());
                    return Mono.empty(); // Retorna Mono vacío si ocurre un error
                });
    }

    // Extraer el token JWT del encabezado de la solicitud
    public Mono<String> extractToken(ServerWebExchange exchange) {
        return Mono.defer(() -> {
            String bearerToken = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
                return Mono.just(bearerToken.substring(7));
            }
            return Mono.empty();  // Devolver un Mono vacío si no hay token o no tiene el formato correcto
        });
    }

}
