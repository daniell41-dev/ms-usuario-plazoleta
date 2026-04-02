package com.plazoleta.ms_usuario.infrastructure.config.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenBlacklistServiceTest {

    private TokenBlacklistService tokenBlacklistService;

    @BeforeEach
    void setUp() {
        tokenBlacklistService = new TokenBlacklistService();
    }

    // ─── estaInvalidado ──────────────────────────────────────────────────────

    @Test
    void estaInvalidado_cuandoTokenNoFueInvalidado_devuelveFalse() {
        assertFalse(tokenBlacklistService.estaInvalidado("token.no.invalidado"));
    }

    @Test
    void estaInvalidado_cuandoTokenFueInvalidado_devuelveTrue() {
        String token = "token.a.invalidar";
        tokenBlacklistService.invalidar(token);

        assertTrue(tokenBlacklistService.estaInvalidado(token));
    }

    // ─── invalidar ───────────────────────────────────────────────────────────

    @Test
    void invalidar_cuandoSeLlamaDoasVecesConElMismoToken_noLanzaExcepcion() {
        String token = "token.repetido";
        tokenBlacklistService.invalidar(token);

        assertDoesNotThrow(() -> tokenBlacklistService.invalidar(token));
        assertTrue(tokenBlacklistService.estaInvalidado(token));
    }

    @Test
    void invalidar_cuandoSeInvalidanMultiplesTokens_todosQuedaronInvalidados() {
        tokenBlacklistService.invalidar("token.uno");
        tokenBlacklistService.invalidar("token.dos");
        tokenBlacklistService.invalidar("token.tres");

        assertTrue(tokenBlacklistService.estaInvalidado("token.uno"));
        assertTrue(tokenBlacklistService.estaInvalidado("token.dos"));
        assertTrue(tokenBlacklistService.estaInvalidado("token.tres"));
    }
}
