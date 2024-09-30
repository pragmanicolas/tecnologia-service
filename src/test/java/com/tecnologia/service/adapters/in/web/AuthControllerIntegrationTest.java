package com.tecnologia.service.adapters.in.web;

import com.tecnologia.service.adapters.out.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class AuthControllerIntegrationTest {

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

        // Mock de la autenticación
        when(authenticationManager.authenticate(any()))
                .thenReturn(Mono.just(
                        new UsernamePasswordAuthenticationToken(
                                new User(username, password, new ArrayList<>()), // Aquí pasa un UserDetails válido
                                password,
                                new ArrayList<>()
                        )
                ));


        when(reactiveUserDetailsService.findByUsername(username))
                .thenReturn(Mono.just(new User(username, password, new ArrayList<>())));

        // Mock de la generación del token JWT
        when(jwtService.generateToken(any(UserDetails.class)))
                .thenReturn(Mono.just(token));

        // Prueba la ruta de login
        webTestClient.post()
                .uri("/api/auth/login")
                .bodyValue(new LoginRequest(username, password))
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(response -> assertEquals(token, response));
    }
}


