package com.plazoleta.ms_usuario.infrastructure.config;

import com.plazoleta.ms_usuario.infrastructure.config.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuración real de Spring Security con autenticación JWT.
 *
 * Reemplaza la configuración temporal que permitía todo sin autenticación.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // CSRF no aplica en APIs REST con JWT — no hay sesiones ni cookies de sesión
            .csrf(AbstractHttpConfigurer::disable)

            // STATELESS: Spring no crea ni guarda sesiones en memoria.
            // Cada request debe autenticarse por sí solo con su token.
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // Reglas de autorización por endpoint
            .authorizeHttpRequests(auth -> auth

                // Endpoints públicos — no requieren token
                .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                .requestMatchers(HttpMethod.GET, "/usuarios/{id}/rol").permitAll()

                // Endpoints protegidos — requieren rol específico
                .requestMatchers(HttpMethod.POST, "/usuarios/propietario").hasAuthority("ADMINISTRADOR")
                .requestMatchers(HttpMethod.POST, "/usuarios/empleado").hasAuthority("PROPIETARIO")

                // Cualquier otro endpoint requiere estar autenticado
                .anyRequest().authenticated()
            )

            // Registrar nuestro filtro JWT ANTES del filtro de usuario/contraseña de Spring.
            // Así, cuando Spring evalúa las reglas de autorización, ya sabe quién es el usuario.
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
