package com.plazoleta.ms_usuario.infrastructure.config.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    private static final String SECRET_DE_PRUEBA = "clave-secreta-de-prueba-suficientemente-larga-32chars";
    private static final long EXPIRACION_NORMAL = 86400000L;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "secret", SECRET_DE_PRUEBA);
        ReflectionTestUtils.setField(jwtTokenProvider, "expiration", EXPIRACION_NORMAL);
    }

    // ─── generarToken ────────────────────────────────────────────────────────

    @Test
    void generarToken_cuandoSeProporcionanDatosValidos_devuelveTokenNoNulo() {
        String token = jwtTokenProvider.generarToken(1L, "juan@correo.com", "PROPIETARIO");

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void generarToken_cuandoSeProporcionanDatosValidos_devuelveTokenConTresPartes() {
        String token = jwtTokenProvider.generarToken(1L, "juan@correo.com", "PROPIETARIO");

        // Un JWT tiene siempre el formato: header.payload.signature
        assertEquals(3, token.split("\\.").length);
    }

    // ─── extraerCorreo ───────────────────────────────────────────────────────

    @Test
    void extraerCorreo_cuandoTokenEsValido_devuelveCorreoCorrecto() {
        String token = jwtTokenProvider.generarToken(1L, "juan@correo.com", "PROPIETARIO");

        assertEquals("juan@correo.com", jwtTokenProvider.extraerCorreo(token));
    }

    // ─── extraerRol ──────────────────────────────────────────────────────────

    @Test
    void extraerRol_cuandoTokenEsValido_devuelveRolCorrecto() {
        String token = jwtTokenProvider.generarToken(1L, "juan@correo.com", "ADMINISTRADOR");

        assertEquals("ADMINISTRADOR", jwtTokenProvider.extraerRol(token));
    }

    // ─── validarToken ────────────────────────────────────────────────────────

    @Test
    void validarToken_cuandoTokenEsValido_devuelveTrue() {
        String token = jwtTokenProvider.generarToken(1L, "juan@correo.com", "PROPIETARIO");

        assertTrue(jwtTokenProvider.validarToken(token));
    }

    @Test
    void validarToken_cuandoTokenEstaManipulado_devuelveFalse() {
        assertFalse(jwtTokenProvider.validarToken("esto.no.esUnToken"));
    }

    @Test
    void validarToken_cuandoTokenEstaExpirado_devuelveFalse() {
        ReflectionTestUtils.setField(jwtTokenProvider, "expiration", -1000L);
        String tokenExpirado = jwtTokenProvider.generarToken(1L, "juan@correo.com", "PROPIETARIO");

        assertFalse(jwtTokenProvider.validarToken(tokenExpirado));
    }

    @Test
    void validarToken_cuandoTokenFirmadoConOtroSecret_devuelveFalse() {
        JwtTokenProvider otroProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(otroProvider, "secret", "otro-secret-completamente-diferente-32chars-x");
        ReflectionTestUtils.setField(otroProvider, "expiration", EXPIRACION_NORMAL);

        String tokenDeOtroProvider = otroProvider.generarToken(1L, "juan@correo.com", "PROPIETARIO");

        assertFalse(jwtTokenProvider.validarToken(tokenDeOtroProvider));
    }
}
