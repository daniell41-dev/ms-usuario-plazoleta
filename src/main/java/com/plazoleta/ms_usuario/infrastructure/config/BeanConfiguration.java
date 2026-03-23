package com.plazoleta.ms_usuario.infrastructure.config;

import com.plazoleta.ms_usuario.application.usecase.UsuarioUseCase;
import com.plazoleta.ms_usuario.domain.ports.in.IUsuarioServicePort;
import com.plazoleta.ms_usuario.domain.ports.out.IUsuarioPersistencePort;
import com.plazoleta.ms_usuario.infrastructure.config.security.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Clase de configuración de beans de Spring.
 *
 * ¿Por qué existe esta clase?
 * UsuarioUseCase vive en la capa de APLICACIÓN y no tiene @Service.
 * No puede anotarse con @Service porque eso lo acoplaría a Spring.
 * Entonces, ¿cómo sabe Spring que debe crear y gestionar ese objeto?
 * → A través de esta clase de configuración, que vive en INFRAESTRUCTURA.
 *
 * Es la infraestructura quien conoce Spring y quien "conecta" los puertos
 * con sus implementaciones. El dominio y la aplicación permanecen puros.
 *
 * @Configuration → indica que esta clase contiene definiciones de beans.
 * @Bean → indica que el método crea y devuelve un objeto que Spring
 *         debe gestionar en su contenedor de inversión de control (IoC).
 *
 * Flujo de inyección que Spring resuelve automáticamente:
 *   BeanConfiguration necesita IUsuarioPersistencePort
 *     → Spring busca quién implementa esa interfaz
 *       → encuentra UsuarioJpaAdapter (@Component)
 *         → lo inyecta aquí
 *           → se lo pasa al constructor de UsuarioUseCase
 */
@Configuration
public class BeanConfiguration {

    /**
     * Registra UsuarioUseCase como bean de Spring.
     * Spring inyecta automáticamente IUsuarioPersistencePort
     * (cuya implementación concreta es UsuarioJpaAdapter).
     */
    @Bean
    public IUsuarioServicePort usuarioServicePort(
            IUsuarioPersistencePort usuarioPersistencePort,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider) {
        return new UsuarioUseCase(usuarioPersistencePort, passwordEncoder, jwtTokenProvider);
    }

    /**
     * Registra BCryptPasswordEncoder como implementación de PasswordEncoder.
     *
     * BCrypt es el algoritmo de hashing recomendado para contraseñas porque:
     * - Es lento por diseño (dificulta ataques de fuerza bruta)
     * - Incorpora un "salt" aleatorio automáticamente (evita tablas rainbow)
     * - El factor de coste es configurable (más coste = más lento = más seguro)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
