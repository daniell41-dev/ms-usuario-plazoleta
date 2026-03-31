package ms_usuario.plazoleta.ms_usuario.infrastructure.output.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BCryptClaveCodificadorTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private BCryptClaveCodificador bcryptClaveCodificador;

    @Test
    void cuandoCodifica_delegaCorrectamenteAPasswordEncoder() {
        // ARRANGE
        String claveTextoPlano = "miClave123";
        String hashEsperado = "$2a$10$hash_simulado";

        when(passwordEncoder.encode(claveTextoPlano)).thenReturn(hashEsperado);

        // ACT
        String resultado = bcryptClaveCodificador.codificar(claveTextoPlano);

        // ASSERT
        // El resultado es exactamente lo que devolvió el encoder
        assertEquals(hashEsperado, resultado);
        // Se delegó al encoder exactamente una vez — ni más, ni menos
        verify(passwordEncoder, times(1)).encode(claveTextoPlano);
    }
}
