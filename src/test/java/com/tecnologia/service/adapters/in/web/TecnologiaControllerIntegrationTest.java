package com.tecnologia.service.adapters.in.web;

import com.tecnologia.service.adapters.out.security.JwtService;
import com.tecnologia.service.application.port.in.TecnologiaService;
import com.tecnologia.service.application.service.TestSecurityConfig;
import com.tecnologia.service.domain.Tecnologia;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
class TecnologiaControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private TecnologiaService tecnologiaService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private ReactiveUserDetailsService reactiveUserDetailsService;

    @Autowired
    private JwtService jwtService;

    private String jwtToken;

    @BeforeEach
    void setUp() {
        // Agregar un rol al usuario
        UserDetails userDetails = new User("user", "password", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        // Mockear la autenticación del usuario
        when(reactiveUserDetailsService.findByUsername(anyString()))
                .thenReturn(Mono.just(userDetails));

        when(userDetailsService.loadUserByUsername(anyString()))
                .thenReturn(userDetails);

        // Generar un token JWT válido con roles usando UserDetails
        jwtToken = "Bearer " + jwtService.generateToken(userDetails).block();

        System.out.println("Generated JWT Token: " + jwtToken);  // Imprimir el token para depuración
    }




    @Test
    void testCreateTecnologia() {
        Tecnologia tecnologia = new Tecnologia(1L, "Spring Boot", "Description");
        when(tecnologiaService.registrarTecnologia(any(Tecnologia.class))).thenReturn(Mono.just(tecnologia));

        webTestClient
                .post()
                .uri("/api/tecnologias")
                .header(HttpHeaders.AUTHORIZATION, jwtToken)  // Usamos el token JWT generado
                .bodyValue(tecnologia)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Tecnologia.class)
                .value(response -> assertEquals("Spring Boot", response.getNombre()));
    }

    @Test
    void testGetBootcampById() {
        Tecnologia tecnologia = new Tecnologia(1L, "Spring Boot", "Description");
        when(tecnologiaService.obtenerTecnologiaPorId(1L)).thenReturn(Mono.just(tecnologia));

        webTestClient.get()
                .uri("/api/tecnologias/{id}", 1L)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)  // Usamos el token JWT generado
                .exchange()
                .expectStatus().isOk()
                .expectBody(Tecnologia.class)
                .value(response -> assertEquals("Spring Boot", response.getNombre()));
    }

}

