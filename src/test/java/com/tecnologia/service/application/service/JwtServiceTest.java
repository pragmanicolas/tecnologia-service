package com.tecnologia.service.application.service;

import com.tecnologia.service.adapters.out.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")  // Activa el perfil "test"
@Import(TestSecurityConfig.class)
class JwtServiceTest {


    @MockBean
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @MockBean
    private ReactiveUserDetailsService reactiveUserDetailsService;

    @MockBean
    private UserDetailsService userDetailsService;  // Asegúrate de tener este mock para que funcione

    private UserDetails userDetails;

    @BeforeEach
    public void setup() {
        userDetails = new User("testUser", "password", List.of(new SimpleGrantedAuthority("ROLE_USER")));
        when(reactiveUserDetailsService.findByUsername(anyString())).thenReturn(Mono.just(userDetails));    }

    // Test para generar un token y verificar que no es nulo
    @Test
    @WithMockUser(username = "testUser", roles = {"USER"})
    void testGenerateToken() {
        Mono<String> tokenMono = jwtService.generateToken(userDetails);
        String token = tokenMono.block();  // Bloqueamos el Mono para obtener el valor
        assertNotNull(token);
    }

    // Test para extraer el nombre de usuario desde un token generado
    @Test
    void testExtractUsername() {
        Mono<String> tokenMono = jwtService.generateToken(userDetails);
        String token = tokenMono.block();  // Bloquear para obtener el valor
        Mono<String> usernameMono = jwtService.extractUsername(token);
        String username = usernameMono.block();  // Bloquear para obtener el valor
        assertEquals("testUser", username);
    }

    // Test para validar un token correcto
    @Test
    void testValidateToken() {
        String token = jwtService.generateToken(userDetails).block();  // Solo se bloquea para obtener el token
        StepVerifier.create(jwtService.validateToken(token))
                .expectNext(true)  // Espera que el token sea válido
                .verifyComplete();
    }


    // Test para verificar que un token expirado es identificado correctamente
    @Test
    void testTokenExpiration() {
        // Clona el servicio JWT para la prueba de expiración rápida
        JwtService jwtServiceWithShortExpiration = new JwtService();

        // Usa ReflectionTestUtils para inyectar la clave secreta y el tiempo de expiración corto
        ReflectionTestUtils.setField(jwtServiceWithShortExpiration, "secretKey", "mysecretkeymysecretkeymysecretkeymysecretkey");
        ReflectionTestUtils.setField(jwtServiceWithShortExpiration, "expirationTimeMs", 1L);  // Expiración rápida de 1ms

        // Genera el token con la expiración rápida
        Mono<String> tokenMono = jwtServiceWithShortExpiration.generateToken(userDetails);
        String token = tokenMono.block();  // Bloquear para obtener el valor

        // Esperar un tiempo mínimo para garantizar que el token expire
        try {
            Thread.sleep(1000);  // Solo 10 ms ya es suficiente para que el token expire
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();  // Restaura el estado interrumpido
        }

        // Valida que el token ha expirado
        Mono<Boolean> isValidMono = jwtServiceWithShortExpiration.validateToken(token);
        boolean isValid = isValidMono.block();  // Bloquear para obtener el valor

        // Esperar que el token ya no sea válido
        assertFalse(isValid);
    }


    // Test para validar que un token inválido es identificado correctamente
    @Test
    void testInvalidToken() {
        String invalidToken = "invalidTokenExample";
        Mono<Boolean> isValidMono = jwtService.validateToken(invalidToken);
        boolean isValid = isValidMono.block();  // Bloquear para obtener el valor
        assertFalse(isValid);
    }

    // Test para extraer roles del token
    @Test
    void testExtractRoles() {
        Mono<String> tokenMono = jwtService.generateToken(userDetails);
        String token = tokenMono.block();  // Bloquear para obtener el valor
        Mono<List<String>> rolesMono = jwtService.extractRoles(token);
        List<String> roles = rolesMono.block();  // Bloquear para obtener el valor
        assertNotNull(roles);
        assertTrue(roles.contains("ROLE_USER"));
    }

    // Test para generar un token con roles y validar que los roles están presentes en el token
    @Test
    void testGenerateTokenWithRoles() {
        UserDetails userDetailsWithRoles = new User("testUser", "password", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        Mono<String> tokenMono = jwtService.generateToken(userDetailsWithRoles);
        String token = tokenMono.block();  // Bloquear para obtener el valor
        Mono<List<String>> rolesMono = jwtService.extractRoles(token);
        List<String> roles = rolesMono.block();  // Bloquear para obtener el valor
        assertNotNull(roles);
        assertTrue(roles.contains("ROLE_ADMIN"));
    }
}
