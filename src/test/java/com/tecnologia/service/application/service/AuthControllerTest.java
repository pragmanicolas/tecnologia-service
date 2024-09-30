package com.tecnologia.service.application.service;

import com.tecnologia.service.adapters.in.web.LoginRequest;
import com.tecnologia.service.adapters.out.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ReactiveAuthenticationManager authenticationManager;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private ReactiveUserDetailsService reactiveUserDetailsService;

    @Test
    void testLogin() {
        String username = "testUser";
        String password = "password";
        String token = "jwtToken";

        // Mock del servicio de detalles del usuario
        when(reactiveUserDetailsService.findByUsername(username))
                .thenReturn(Mono.just(new User(username, password, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))));

        // Mock del servicio JWT para generar el token
        when(jwtService.generateToken(any(UserDetails.class)))
                .thenReturn(Mono.just(token));

        // Mock del servicio de autenticación reactiva
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(Mono.just(new UsernamePasswordAuthenticationToken(
                        new User(username, password, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))),
                        password,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                )));


        // Ejecuta la solicitud POST a /api/auth/login
        webTestClient.post()
                .uri("/api/auth/login")
                .bodyValue(new LoginRequest(username, password))
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(response -> assertEquals(token, response));
    }
}