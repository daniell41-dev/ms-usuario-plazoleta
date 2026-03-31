package ms_usuario.plazoleta.ms_usuario.infrastructure.output.persistence.adapter;

import ms_usuario.plazoleta.ms_usuario.domain.model.Rol;
import ms_usuario.plazoleta.ms_usuario.domain.model.Usuario;
import ms_usuario.plazoleta.ms_usuario.infrastructure.output.persistence.entity.UsuarioEntity;
import ms_usuario.plazoleta.ms_usuario.infrastructure.output.persistence.mapper.IUsuarioEntityMapper;
import ms_usuario.plazoleta.ms_usuario.infrastructure.output.persistence.repository.IUsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioJpaAdapterTest {

    @Mock
    private IUsuarioRepository usuarioRepository;

    @Mock
    private IUsuarioEntityMapper usuarioEntityMapper;

    @InjectMocks
    private UsuarioJpaAdapter usuarioJpaAdapter;

    // -------------------------------------------------------------------------
    // guardarUsuario
    // -------------------------------------------------------------------------

    @Test
    void cuandoGuardaUsuario_convierteAEntidadYLlamaAlRepository() {
        // ARRANGE
        Usuario usuario = buildUsuario();
        UsuarioEntity entidad = buildEntidad();

        // El mapper convierte el dominio → entidad
        when(usuarioEntityMapper.toEntity(usuario)).thenReturn(entidad);

        // ACT
        usuarioJpaAdapter.guardarUsuario(usuario);

        // ASSERT
        // El mapper fue invocado para convertir
        verify(usuarioEntityMapper, times(1)).toEntity(usuario);
        // El repository recibió la entidad resultante del mapeo
        verify(usuarioRepository, times(1)).save(entidad);
    }

    // -------------------------------------------------------------------------
    // buscarPorCorreo
    // -------------------------------------------------------------------------

    @Test
    void cuandoBuscaPorCorreoExistente_devuelveOptionalConUsuario() {
        // ARRANGE
        String correo = "juan@correo.com";
        UsuarioEntity entidad = buildEntidad();
        Usuario usuario = buildUsuario();

        // El repository encuentra la entidad en BD
        when(usuarioRepository.findByCorreo(correo)).thenReturn(Optional.of(entidad));
        // El mapper convierte la entidad → dominio
        when(usuarioEntityMapper.toUsuario(entidad)).thenReturn(usuario);

        // ACT
        Optional<Usuario> resultado = usuarioJpaAdapter.buscarPorCorreo(correo);

        // ASSERT
        assertTrue(resultado.isPresent());
        assertEquals(usuario, resultado.get());
        verify(usuarioEntityMapper, times(1)).toUsuario(entidad);
    }

    @Test
    void cuandoBuscaPorCorreoInexistente_devuelveOptionalEmpty() {
        // ARRANGE
        String correo = "noexiste@correo.com";
        when(usuarioRepository.findByCorreo(correo)).thenReturn(Optional.empty());

        // ACT
        Optional<Usuario> resultado = usuarioJpaAdapter.buscarPorCorreo(correo);

        // ASSERT
        assertTrue(resultado.isEmpty());
        // El mapper nunca fue llamado — no hay entidad que convertir
        verify(usuarioEntityMapper, never()).toUsuario(any());
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private Usuario buildUsuario() {
        return new Usuario(
                1L, "Juan", "Pérez", "12345678",
                "+573001234567",
                LocalDate.of(2000, 1, 15),
                "juan@correo.com", "$2a$hashClave", Rol.PROPIETARIO
        );
    }

    private UsuarioEntity buildEntidad() {
        return new UsuarioEntity(
                1L, "Juan", "Pérez", "12345678",
                "+573001234567",
                LocalDate.of(2000, 1, 15),
                "juan@correo.com", "$2a$hashClave", Rol.PROPIETARIO
        );
    }
}
