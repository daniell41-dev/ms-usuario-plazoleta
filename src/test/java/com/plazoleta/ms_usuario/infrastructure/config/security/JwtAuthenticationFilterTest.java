package com.plazoleta.ms_usuario.infrastructure.config.security;

import com.plazoleta.ms_usuario.domain.constants.UsuarioConstantes;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @AfterEach
    void tearDown() {
        // Limpiamos el contexto de seguridad entre tests para evitar contaminación
        SecurityContextHolder.clearContext();
    }

    // ─── sin header Authorization ────────────────────────────────────────────

    @Test
    void doFilterInternal_cuandoNoHayHeaderAuthorization_noSeteaAutenticacion() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_cuandoHeaderNoEmpiezaConBearer_noSeteaAutenticacion() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Basic dXN1YXJpbzpjbGF2ZQ==");

        filter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    // ─── token válido ────────────────────────────────────────────────────────

    @Test
    void doFilterInternal_cuandoTokenEsValidoYNoEstaEnBlacklist_seteaAutenticacionEnContexto() throws Exception {
        String token = "token.valido.jwt";
        when(request.getHeader("Authorization")).thenReturn(UsuarioConstantes.BEARER_PREFIX + token);
        when(tokenBlacklistService.estaInvalidado(token)).thenReturn(false);
        when(jwtTokenProvider.validarToken(token)).thenReturn(true);
        when(jwtTokenProvider.extraerCorreo(token)).thenReturn("juan@correo.com");
        when(jwtTokenProvider.extraerRol(token)).thenReturn("PROPIETARIO");

        filter.doFilter(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("juan@correo.com", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_cuandoTokenEsValido_seteaRolCorrectoEnAuthorities() throws Exception {
        String token = "token.valido.jwt";
        when(request.getHeader("Authorization")).thenReturn(UsuarioConstantes.BEARER_PREFIX + token);
        when(tokenBlacklistService.estaInvalidado(token)).thenReturn(false);
        when(jwtTokenProvider.validarToken(token)).thenReturn(true);
        when(jwtTokenProvider.extraerCorreo(token)).thenReturn("admin@correo.com");
        when(jwtTokenProvider.extraerRol(token)).thenReturn("ADMINISTRADOR");

        filter.doFilter(request, response, filterChain);

        var authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        assertEquals(1, authorities.size());
        assertEquals("ADMINISTRADOR", authorities.iterator().next().getAuthority());
    }

    // ─── token en blacklist ───────────────────────────────────────────────────

    @Test
    void doFilterInternal_cuandoTokenEstaEnBlacklist_noSeteaAutenticacion() throws Exception {
        String token = "token.invalidado.jwt";
        when(request.getHeader("Authorization")).thenReturn(UsuarioConstantes.BEARER_PREFIX + token);
        when(tokenBlacklistService.estaInvalidado(token)).thenReturn(true);

        filter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtTokenProvider, never()).validarToken(anyString());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    // ─── token inválido ───────────────────────────────────────────────────────

    @Test
    void doFilterInternal_cuandoTokenEsInvalido_noSeteaAutenticacion() throws Exception {
        String token = "token.corrupto.jwt";
        when(request.getHeader("Authorization")).thenReturn(UsuarioConstantes.BEARER_PREFIX + token);
        when(tokenBlacklistService.estaInvalidado(token)).thenReturn(false);
        when(jwtTokenProvider.validarToken(token)).thenReturn(false);

        filter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtTokenProvider, never()).extraerCorreo(anyString());
        verify(filterChain, times(1)).doFilter(request, response);
    }
}
