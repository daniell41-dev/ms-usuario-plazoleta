package ms_usuario.plazoleta.ms_usuario.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de Spring Security.
 *
 * ¿Por qué necesitamos esta clase ahora?
 * Spring Security, por defecto, protege TODOS los endpoints — ni siquiera
 * puedes hacer un POST sin autenticarte. Eso nos bloquearía durante el
 * desarrollo antes de tener implementado el sistema de autenticación.
 *
 * Esta configuración temporal permite todas las peticiones sin autenticación
 * para que podamos probar los endpoints mientras construimos la app.
 *
 * IMPORTANTE: Esta configuración es TEMPORAL. Cuando implementemos JWT
 * o el sistema de autenticación real, esta clase será reemplazada por
 * una configuración que proteja los endpoints correctamente.
 *
 * @EnableWebSecurity → activa la configuración personalizada de Spring Security.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * SecurityFilterChain → define las reglas de seguridad HTTP.
     *
     * csrf().disable() → desactiva la protección CSRF.
     * CSRF (Cross-Site Request Forgery) es un ataque web. La protección
     * de Spring usa cookies de sesión, pero en APIs REST con JWT no se usan
     * sesiones, así que esta protección no aplica y solo añade complejidad.
     *
     * authorizeHttpRequests → reglas de autorización por endpoint.
     * anyRequest().permitAll() → permite todas las peticiones sin autenticación.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            );
        return http.build();
    }
}
