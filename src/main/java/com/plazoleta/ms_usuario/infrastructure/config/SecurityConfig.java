package com.plazoleta.ms_usuario.infrastructure.config;


import com.plazoleta.ms_usuario.infrastructure.config.security.JwtAuthenticationFilter;
import com.plazoleta.ms_usuario.infrastructure.input.rest.dto.ErrorResponseDto;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tools.jackson.databind.ObjectMapper;

/**
 * Configuración real de Spring Security con autenticación JWT.
 *
 * Reemplaza la configuración temporal que permitía todo sin autenticación.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ObjectMapper objectMapper;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, ObjectMapper objectMapper) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.objectMapper = objectMapper;
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
                .requestMatchers(HttpMethod.POST, "/usuarios/cliente").permitAll()
                .requestMatchers(HttpMethod.GET, "/usuarios/{id}/rol").permitAll()

                // Endpoints protegidos — requieren rol específico
                .requestMatchers(HttpMethod.POST, "/usuarios/propietario").hasAuthority("ADMINISTRADOR")
                .requestMatchers(HttpMethod.POST, "/usuarios/empleado").hasAuthority("PROPIETARIO")

                // Cualquier otro endpoint requiere estar autenticado
                .anyRequest().authenticated()
            )

            // Token ausente o inválido — el filtro JWT no pudo autenticar al usuario
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    objectMapper.writeValue(response.getWriter(),
                            new ErrorResponseDto(HttpStatus.UNAUTHORIZED.value(),
                                    "No autorizado: token inválido o ausente"));
                })
                // Token válido pero el rol no tiene permiso para este endpoint
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    objectMapper.writeValue(response.getWriter(),
                            new ErrorResponseDto(HttpStatus.FORBIDDEN.value(),
                                    "No tiene permisos para realizar esta acción"));
                })
            )

            // Registrar nuestro filtro JWT ANTES del filtro de usuario/contraseña de Spring.
            // Así, cuando Spring evalúa las reglas de autorización, ya sabe quién es el usuario.
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
