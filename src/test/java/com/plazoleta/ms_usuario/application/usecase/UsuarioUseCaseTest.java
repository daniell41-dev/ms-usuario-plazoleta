package com.plazoleta.ms_usuario.application.usecase;

import com.plazoleta.ms_usuario.domain.constants.UsuarioConstantes;
import com.plazoleta.ms_usuario.domain.exception.CredencialesInvalidasException;
import com.plazoleta.ms_usuario.domain.exception.MenorDeEdadException;
import com.plazoleta.ms_usuario.domain.exception.UsuarioNoEncontradoException;
import com.plazoleta.ms_usuario.domain.exception.UsuarioYaExisteException;
import com.plazoleta.ms_usuario.domain.model.Rol;
import com.plazoleta.ms_usuario.domain.model.Usuario;
import com.plazoleta.ms_usuario.domain.ports.out.IJwtTokenPort;
import com.plazoleta.ms_usuario.domain.ports.out.IUsuarioPersistencePort;
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
    private IJwtTokenPort jwtTokenPort;

    @InjectMocks
    private UsuarioUseCase usuarioUseCase;

    private Usuario usuarioValido;

    @BeforeEach
    void setUp() {
        usuarioValido = new Usuario(
                null, "Juan", "Pérez", "12345678", "+573001234567",
                LocalDate.of(1990, 1, 1), "juan@correo.com", "clave123", null
        );
    }

    // ─── guardarPropietario: camino feliz ────────────────────────────────────

    @Test
    void guardarPropietario_cuandoTodosLosDatosSonValidos_guardaConRolPropietario() {
        when(usuarioPersistencePort.buscarPorCorreo(anyString())).thenReturn(Optional.empty());
        when(usuarioPersistencePort.existePorDocumento(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("claveEncriptada");

        usuarioUseCase.guardarPropietario(usuarioValido);

        verify(usuarioPersistencePort).guardarUsuario(usuarioValido);
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
        when(usuarioPersistencePort.buscarPorCorreo(anyString())).thenReturn(Optional.empty());
        when(usuarioPersistencePort.existePorDocumento(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("claveEncriptada");

        assertDoesNotThrow(() -> usuarioUseCase.guardarPropietario(exactamente18));
        verify(usuarioPersistencePort).guardarUsuario(exactamente18);
    }

    // ─── guardarPropietario: correo duplicado ────────────────────────────────

    @Test
    void guardarPropietario_cuandoCorreoYaExiste_lanzaUsuarioYaExisteException() {
        when(usuarioPersistencePort.buscarPorCorreo(anyString()))
                .thenReturn(Optional.of(usuarioValido));

        assertThrows(UsuarioYaExisteException.class,
                () -> usuarioUseCase.guardarPropietario(usuarioValido));

        verify(usuarioPersistencePort, never()).guardarUsuario(any());
    }

    // ─── guardarPropietario: documento duplicado ─────────────────────────────

    @Test
    void guardarPropietario_cuandoDocumentoYaExiste_lanzaUsuarioYaExisteException() {
        when(usuarioPersistencePort.buscarPorCorreo(anyString())).thenReturn(Optional.empty());
        when(usuarioPersistencePort.existePorDocumento(anyString())).thenReturn(true);

        assertThrows(UsuarioYaExisteException.class,
                () -> usuarioUseCase.guardarPropietario(usuarioValido));

        verify(usuarioPersistencePort, never()).guardarUsuario(any());
    }

    // ─── guardarEmpleado: camino feliz ───────────────────────────────────────

    @Test
    void guardarEmpleado_cuandoTodosLosDatosSonValidos_guardaConRolEmpleado() {
        when(usuarioPersistencePort.buscarPorCorreo(anyString())).thenReturn(Optional.empty());
        when(usuarioPersistencePort.existePorDocumento(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("claveEncriptada");

        usuarioUseCase.guardarEmpleado(usuarioValido);

        verify(usuarioPersistencePort).guardarUsuario(usuarioValido);
        assertEquals(Rol.EMPLEADO, usuarioValido.getRol());
        assertEquals("claveEncriptada", usuarioValido.getClave());
    }

    // ─── guardarEmpleado: correo duplicado ───────────────────────────────────

    @Test
    void guardarEmpleado_cuandoCorreoYaExiste_lanzaUsuarioYaExisteException() {
        when(usuarioPersistencePort.buscarPorCorreo(anyString()))
                .thenReturn(Optional.of(usuarioValido));

        assertThrows(UsuarioYaExisteException.class,
                () -> usuarioUseCase.guardarEmpleado(usuarioValido));

        verify(usuarioPersistencePort, never()).guardarUsuario(any());
    }

    // ─── guardarEmpleado: documento duplicado ────────────────────────────────

    @Test
    void guardarEmpleado_cuandoDocumentoYaExiste_lanzaUsuarioYaExisteException() {
        when(usuarioPersistencePort.buscarPorCorreo(anyString())).thenReturn(Optional.empty());
        when(usuarioPersistencePort.existePorDocumento(anyString())).thenReturn(true);

        assertThrows(UsuarioYaExisteException.class,
                () -> usuarioUseCase.guardarEmpleado(usuarioValido));

        verify(usuarioPersistencePort, never()).guardarUsuario(any());
    }

    // ─── guardarCliente: camino feliz ────────────────────────────────────────

    @Test
    void guardarCliente_cuandoTodosLosDatosSonValidos_guardaConRolCliente() {
        when(usuarioPersistencePort.buscarPorCorreo(anyString())).thenReturn(Optional.empty());
        when(usuarioPersistencePort.existePorDocumento(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("claveEncriptada");

        usuarioUseCase.guardarCliente(usuarioValido);

        verify(usuarioPersistencePort).guardarUsuario(usuarioValido);
        assertEquals(Rol.CLIENTE, usuarioValido.getRol());
        assertEquals("claveEncriptada", usuarioValido.getClave());
    }

    // ─── guardarCliente: correo duplicado ────────────────────────────────────

    @Test
    void guardarCliente_cuandoCorreoYaExiste_lanzaUsuarioYaExisteException() {
        when(usuarioPersistencePort.buscarPorCorreo(anyString()))
                .thenReturn(Optional.of(usuarioValido));

        assertThrows(UsuarioYaExisteException.class,
                () -> usuarioUseCase.guardarCliente(usuarioValido));

        verify(usuarioPersistencePort, never()).guardarUsuario(any());
    }

    // ─── guardarCliente: documento duplicado ─────────────────────────────────

    @Test
    void guardarCliente_cuandoDocumentoYaExiste_lanzaUsuarioYaExisteException() {
        when(usuarioPersistencePort.buscarPorCorreo(anyString())).thenReturn(Optional.empty());
        when(usuarioPersistencePort.existePorDocumento(anyString())).thenReturn(true);

        assertThrows(UsuarioYaExisteException.class,
                () -> usuarioUseCase.guardarCliente(usuarioValido));

        verify(usuarioPersistencePort, never()).guardarUsuario(any());
    }

    // ─── obtenerRolUsuario ────────────────────────────────────────────────────

    @Test
    void obtenerRolUsuario_cuandoUsuarioExiste_devuelveNombreDelRol() {
        usuarioValido.setRol(Rol.PROPIETARIO);
        when(usuarioPersistencePort.obtenerPorId(1L)).thenReturn(Optional.of(usuarioValido));

        String rol = usuarioUseCase.obtenerRolUsuario(1L);

        assertEquals("PROPIETARIO", rol);
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
        usuarioValido.setRol(Rol.PROPIETARIO);
        when(usuarioPersistencePort.buscarPorCorreo("juan@correo.com"))
                .thenReturn(Optional.of(usuarioValido));
        when(passwordEncoder.matches("clave123", usuarioValido.getClave())).thenReturn(true);
        when(jwtTokenPort.generarToken(any(), anyString(), anyString()))
                .thenReturn("token.jwt.generado");

        String token = usuarioUseCase.login("juan@correo.com", "clave123");

        assertEquals("token.jwt.generado", token);
        verify(jwtTokenPort).generarToken(any(), anyString(), anyString());
    }

    @Test
    void login_cuandoCorreoNoExiste_lanzaCredencialesInvalidasException() {
        when(usuarioPersistencePort.buscarPorCorreo(anyString())).thenReturn(Optional.empty());

        assertThrows(CredencialesInvalidasException.class,
                () -> usuarioUseCase.login("noexiste@correo.com", "clave123"));

        verify(jwtTokenPort, never()).generarToken(any(), anyString(), anyString());
    }

    @Test
    void login_cuandoClaveEsIncorrecta_lanzaCredencialesInvalidasException() {
        when(usuarioPersistencePort.buscarPorCorreo("juan@correo.com"))
                .thenReturn(Optional.of(usuarioValido));
        when(passwordEncoder.matches("claveIncorrecta", usuarioValido.getClave())).thenReturn(false);

        assertThrows(CredencialesInvalidasException.class,
                () -> usuarioUseCase.login("juan@correo.com", "claveIncorrecta"));

        verify(jwtTokenPort, never()).generarToken(any(), anyString(), anyString());
    }
}
