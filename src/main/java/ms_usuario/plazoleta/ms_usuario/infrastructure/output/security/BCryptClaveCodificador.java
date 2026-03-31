package ms_usuario.plazoleta.ms_usuario.infrastructure.output.security;

import ms_usuario.plazoleta.ms_usuario.domain.ports.out.IClaveCodificadorPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Adaptador de seguridad: implementa IClaveCodificadorPort usando BCrypt de Spring Security.
 *
 * Vive en infraestructura porque es aquí donde está permitido usar Spring Security.
 * El dominio y la capa de aplicación solo conocen IClaveCodificadorPort —
 * no saben que por debajo existe BCrypt ni Spring.
 *
 * @RequiredArgsConstructor genera el constructor con PasswordEncoder inyectado.
 * Spring resuelve la implementación concreta (BCryptPasswordEncoder) registrada
 * como bean en BeanConfiguration.
 */
@Component
@RequiredArgsConstructor
public class BCryptClaveCodificador implements IClaveCodificadorPort {

    private final PasswordEncoder passwordEncoder;

    @Override
    public String codificar(String claveTextoPlano) {
        return passwordEncoder.encode(claveTextoPlano);
    }
}
