package com.plazoleta.ms_usuario.application.usecase;

import com.plazoleta.ms_usuario.domain.constants.UsuarioConstantes;
import com.plazoleta.ms_usuario.domain.exception.CredencialesInvalidasException;
import com.plazoleta.ms_usuario.domain.exception.MenorDeEdadException;
import com.plazoleta.ms_usuario.domain.exception.UsuarioNoEncontradoException;
import com.plazoleta.ms_usuario.domain.exception.UsuarioYaExisteException;
import com.plazoleta.ms_usuario.domain.model.Rol;
import com.plazoleta.ms_usuario.domain.model.Usuario;
import com.plazoleta.ms_usuario.domain.ports.out.IUsuarioPersistencePort;
import com.plazoleta.ms_usuario.infrastructure.config.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioUseCaseTest {

    @Mock
    private IUsuarioPersistencePort usuarioPersistencePort;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private UsuarioUseCase usuarioUseCase;

    private Usuario usuarioValido;

    @BeforeEach
    void setUp() {
        usuarioValido = new Usuario(
                null,
                "Juan",
                "Pérez",
                "12345678",
                "+573001234567",
                LocalDate.of(1990, 1, 1),
                "juan@correo.com",
                "clave123",
                null
        );
    }

    // ─── guardarEmpleado: camino feliz ───────────────────────────────────────

    @Test
    void guardarEmpleado_cuandoTodosLosDatosSonValidos_guardaCorrectamente() {
        when(usuarioPersistencePort.buscarPorCorreo(usuarioValido.getCorreo())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("claveEncriptada");

        usuarioUseCase.guardarEmpleado(usuarioValido);

        verify(usuarioPersistencePort, times(1)).guardarUsuario(usuarioValido);
        assertEquals(Rol.EMPLEADO, usuarioValido.getRol());
        assertEquals("claveEncriptada", usuarioValido.getClave());
    }

    @Test
    void guardarEmpleado_cuandoCorreoYaExiste_lanzaUsuarioYaExisteException() {
        when(usuarioPersistencePort.buscarPorCorreo(usuarioValido.getCorreo()))
                .thenReturn(Optional.of(usuarioValido));

        assertThrows(UsuarioYaExisteException.class,
                () -> usuarioUseCase.guardarEmpleado(usuarioValido));

        verify(usuarioPersistencePort, never()).guardarUsuario(any());
    }

    // ─── guardarPropietario: camino feliz ────────────────────────────────────

    @Test
    void guardarPropietario_cuandoTodosLosDatosSonValidos_guardaCorrectamente() {
        when(usuarioPersistencePort.buscarPorCorreo(usuarioValido.getCorreo())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("claveEncriptada");

        usuarioUseCase.guardarPropietario(usuarioValido);

        verify(usuarioPersistencePort, times(1)).guardarUsuario(usuarioValido);
        assertEquals(Rol.PROPIETARIO, usuarioValido.getRol());
        assertEquals("claveEncriptada", usuarioValido.getClave());
    }

    // ─── guardarPropietario: mayoría de edad ─────────────────────────────────

    @Test
    void guardarPropietario_cuandoUsuarioEsMenorDeEdad_lanzaMenorDeEdadException() {
        Usuario menorDeEdad = new Usuario(
                null, "Juan", "Pérez", "12345678", "+573001234567",
                LocalDate.now().minusYears(UsuarioConstantes.EDAD_MINIMA).plusDays(1),
                "juan@correo.com", "clave123", null
        );

        assertThrows(MenorDeEdadException.class,
                () -> usuarioUseCase.guardarPropietario(menorDeEdad));

        verify(usuarioPersistencePort, never()).guardarUsuario(any());
    }

    @Test
    void guardarPropietario_cuandoUsuarioTieneExactamente18Anos_guardaCorrectamente() {
        Usuario exactamente18 = new Usuario(
                null, "Juan", "Pérez", "12345678", "+573001234567",
                LocalDate.now().minusYears(UsuarioConstantes.EDAD_MINIMA),
                "juan@correo.com", "clave123", null
        );
        when(usuarioPersistencePort.buscarPorCorreo(exactamente18.getCorreo())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("claveEncriptada");

        usuarioUseCase.guardarPropietario(exactamente18);

        verify(usuarioPersistencePort, times(1)).guardarUsuario(exactamente18);
    }

    @Test
    void guardarPropietario_cuandoCorreoYaExiste_lanzaUsuarioYaExisteException() {
        when(usuarioPersistencePort.buscarPorCorreo(usuarioValido.getCorreo()))
                .thenReturn(Optional.of(usuarioValido));

        assertThrows(UsuarioYaExisteException.class,
                () -> usuarioUseCase.guardarPropietario(usuarioValido));

        verify(usuarioPersistencePort, never()).guardarUsuario(any());
    }

    // ─── obtenerRolUsuario ────────────────────────────────────────────────────

    @Test
    void obtenerRolUsuario_cuandoUsuarioExiste_devuelveRol() {
        usuarioValido.setRol(Rol.EMPLEADO);
        when(usuarioPersistencePort.obtenerPorId(1L)).thenReturn(Optional.of(usuarioValido));

        String rol = usuarioUseCase.obtenerRolUsuario(1L);

        assertEquals("EMPLEADO", rol);
    }

    @Test
    void obtenerRolUsuario_cuandoUsuarioNoExiste_lanzaUsuarioNoEncontradoException() {
        when(usuarioPersistencePort.obtenerPorId(99L)).thenReturn(Optional.empty());

        assertThrows(UsuarioNoEncontradoException.class,
                () -> usuarioUseCase.obtenerRolUsuario(99L));
    }

    // ─── login ────────────────────────────────────────────────────────────────

    @Test
    void login_cuandoCredencialesSonValidas_devuelveToken() {
        usuarioValido.setRol(Rol.EMPLEADO);
        when(usuarioPersistencePort.buscarPorCorreo("juan@correo.com")).thenReturn(Optional.of(usuarioValido));
        when(passwordEncoder.matches("clave123", usuarioValido.getClave())).thenReturn(true);
        when(jwtTokenProvider.generarToken(any(), anyString(), anyString())).thenReturn("token.jwt.generado");

        String token = usuarioUseCase.login("juan@correo.com", "clave123");

        assertEquals("token.jwt.generado", token);
        verify(jwtTokenProvider, times(1)).generarToken(any(), anyString(), anyString());
    }

    @Test
    void login_cuandoCorreoNoExiste_lanzaCredencialesInvalidasException() {
        when(usuarioPersistencePort.buscarPorCorreo("noexiste@correo.com")).thenReturn(Optional.empty());

        assertThrows(CredencialesInvalidasException.class,
                () -> usuarioUseCase.login("noexiste@correo.com", "clave123"));

        verify(jwtTokenProvider, never()).generarToken(any(), anyString(), anyString());
    }

    @Test
    void login_cuandoClaveEsIncorrecta_lanzaCredencialesInvalidasException() {
        when(usuarioPersistencePort.buscarPorCorreo("juan@correo.com")).thenReturn(Optional.of(usuarioValido));
        when(passwordEncoder.matches("claveIncorrecta", usuarioValido.getClave())).thenReturn(false);

        assertThrows(CredencialesInvalidasException.class,
                () -> usuarioUseCase.login("juan@correo.com", "claveIncorrecta"));

        verify(jwtTokenProvider, never()).generarToken(any(), anyString(), anyString());
    }
}
