package com.tecnologia.service.adapters.in.web;

import com.tecnologia.service.adapters.out.security.JwtService;
import com.tecnologia.service.application.port.in.TecnologiaService;
import com.tecnologia.service.application.service.TestSecurityConfig;
import com.tecnologia.service.domain.Tecnologia;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
class TecnologiaControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private TecnologiaService tecnologiaService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private ReactiveUserDetailsService reactiveUserDetailsService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @Test
    void testCreateTecnologia() {
        // Simular un Tecnologia que será creado
        Tecnologia tecnologia = new Tecnologia(null, "Java", "Learn Java");

        // Simular el comportamiento del servicio para crear una tecnología
        when(tecnologiaService.registrarTecnologia(any())).thenReturn(Mono.just(tecnologia));

        // Simular el token JWT y su validación
        String jwtToken = "mockedJwtToken";
        when(jwtService.extractToken(any())).thenReturn(Mono.just(jwtToken));
        when(jwtService.validateToken(jwtToken)).thenReturn(Mono.just(true));
        when(jwtService.getAuthentication(jwtToken)).thenReturn(Mono.just(
                new UsernamePasswordAuthenticationToken("user", null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))))
        );

        // Ejecutar la solicitud POST con el WebTestClient simulando la autenticación
        webTestClient
                .post()
                .uri("/api/tecnologias")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)  // Simular el token JWT en el encabezado
                .bodyValue(tecnologia)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.nombre").isEqualTo("Java");
    }
}
