package ms_usuario.plazoleta.ms_usuario.application.usecase;

import ms_usuario.plazoleta.ms_usuario.domain.exception.MenorDeEdadException;
import ms_usuario.plazoleta.ms_usuario.domain.exception.UsuarioYaExisteException;
import ms_usuario.plazoleta.ms_usuario.domain.model.Rol;
import ms_usuario.plazoleta.ms_usuario.domain.model.Usuario;
import ms_usuario.plazoleta.ms_usuario.domain.ports.out.IClaveCodificadorPort;
import ms_usuario.plazoleta.ms_usuario.domain.ports.out.IUsuarioPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

// @ExtendWith(MockitoExtension.class) le dice a JUnit que active Mockito
// para este test. Sin esto, @Mock e @InjectMocks no funcionan.
@ExtendWith(MockitoExtension.class)
class UsuarioUseCaseTest {

    // @Mock crea un objeto falso que implementa la interfaz.
    // No va a la base de datos, no encripta nada — vos le decís qué devolver.
    @Mock
    private IUsuarioPersistencePort usuarioPersistencePort;

    @Mock
    private IClaveCodificadorPort claveCodificadorPort;

    // @InjectMocks crea una instancia REAL de UsuarioUseCase
    // e inyecta los @Mock de arriba por constructor automáticamente.
    @InjectMocks
    private UsuarioUseCase usuarioUseCase;

    // Usuario reutilizable en los tests. Se recrea antes de cada test
    // para que ningún test afecte al siguiente.
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        // Mayor de edad (20 años), datos completos
        usuario = new Usuario(
                null,
                "Juan",
                "Pérez",
                "12345678",
                "+573001234567",
                LocalDate.now().minusYears(20),
                "juan@correo.com",
                "claveTextoPlano",
                null
        );
    }

    @Test
    void cuandoDatosValidos_guardaConRolPropietarioYClaveEncriptada() {
        // ARRANGE
        // El correo no existe todavía en la BD
        when(usuarioPersistencePort.buscarPorCorreo(anyString()))
                .thenReturn(Optional.empty());
        // El codificador devuelve un hash simulado
        when(claveCodificadorPort.codificar("claveTextoPlano"))
                .thenReturn("$2a$hash_simulado");
        // guardarUsuario no devuelve nada (void) — Mockito lo ignora por defecto

        // ACT
        usuarioUseCase.guardarPropietario(usuario);

        // ASSERT
        // 1. La clave fue encriptada: el usuario ahora tiene el hash, no el texto plano
        assertEquals("$2a$hash_simulado", usuario.getClave());

        // 2. El rol fue asignado por el caso de uso, no vino del request
        assertEquals(Rol.PROPIETARIO, usuario.getRol());

        // 3. Se llamó a guardar exactamente una vez con el usuario
        verify(usuarioPersistencePort, times(1)).guardarUsuario(usuario);

        // 4. Se llamó a codificar exactamente una vez con la clave original
        verify(claveCodificadorPort, times(1)).codificar("claveTextoPlano");
    }

    @Test
    void cuandoEsMenorDeEdad_lanzaMenorDeEdadException() {
        // ARRANGE: reemplazamos la fecha por una de 17 años
        usuario = new Usuario(
                null, "Ana", "García", "87654321",
                "+573009876543",
                LocalDate.now().minusYears(17),
                "ana@correo.com", "clave123", null
        );

        // ACT + ASSERT
        assertThrows(MenorDeEdadException.class, () ->
                usuarioUseCase.guardarPropietario(usuario)
        );

        // Verificamos que nunca se llegó a consultar la BD ni a guardar
        // El caso de uso debe fallar rápido en la primera validación
        verify(usuarioPersistencePort, never()).buscarPorCorreo(any());
        verify(usuarioPersistencePort, never()).guardarUsuario(any());
    }

    @Test
    void cuandoCorreoYaExiste_lanzaUsuarioYaExisteException() {
        // ARRANGE: el correo ya está registrado en la BD
        Usuario usuarioExistente = new Usuario(
                1L, "Pedro", "López", "11111111",
                "+573005555555",
                LocalDate.now().minusYears(30),
                "juan@correo.com", "otraClaveHash", Rol.PROPIETARIO
        );
        when(usuarioPersistencePort.buscarPorCorreo("juan@correo.com"))
                .thenReturn(Optional.of(usuarioExistente));

        // ACT + ASSERT
        assertThrows(UsuarioYaExisteException.class, () ->
                usuarioUseCase.guardarPropietario(usuario)
        );

        // Nunca se llegó a encriptar ni guardar — falló antes
        verify(claveCodificadorPort, never()).codificar(any());
        verify(usuarioPersistencePort, never()).guardarUsuario(any());
    }
}
