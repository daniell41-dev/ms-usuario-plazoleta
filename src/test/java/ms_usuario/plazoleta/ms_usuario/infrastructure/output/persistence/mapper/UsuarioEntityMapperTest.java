package ms_usuario.plazoleta.ms_usuario.infrastructure.output.persistence.mapper;

import ms_usuario.plazoleta.ms_usuario.domain.model.Rol;
import ms_usuario.plazoleta.ms_usuario.domain.model.Usuario;
import ms_usuario.plazoleta.ms_usuario.infrastructure.output.persistence.entity.UsuarioEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UsuarioEntityMapperTest {

    private UsuarioEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new UsuarioEntityMapper();
    }

    @Test
    void cuandoConvierteUsuario_mapeaTodosLosCamposAEntidad() {
        // ARRANGE
        Usuario usuario = new Usuario(
                1L, "Carlos", "Ramírez", "11223344",
                "+573001112233",
                LocalDate.of(1988, 3, 22),
                "carlos@correo.com", "$2a$hashClave", Rol.PROPIETARIO
        );

        // ACT
        UsuarioEntity entidad = mapper.toEntity(usuario);

        // ASSERT — cada campo debe coincidir exactamente
        assertEquals(usuario.getId(),                  entidad.getId());
        assertEquals(usuario.getNombre(),              entidad.getNombre());
        assertEquals(usuario.getApellido(),            entidad.getApellido());
        assertEquals(usuario.getDocumentoDeIdentidad(),entidad.getDocumentoDeIdentidad());
        assertEquals(usuario.getCelular(),             entidad.getCelular());
        assertEquals(usuario.getFechaNacimiento(),     entidad.getFechaNacimiento());
        assertEquals(usuario.getCorreo(),              entidad.getCorreo());
        assertEquals(usuario.getClave(),               entidad.getClave());
        assertEquals(usuario.getRol(),                 entidad.getRol());
    }

    @Test
    void cuandoConvierteEntidad_mapeaTodosLosCamposAUsuario() {
        // ARRANGE
        UsuarioEntity entidad = new UsuarioEntity(
                2L, "Laura", "Mendoza", "55667788",
                "+573004445566",
                LocalDate.of(1992, 11, 5),
                "laura@correo.com", "$2a$otroClave", Rol.PROPIETARIO
        );

        // ACT
        Usuario usuario = mapper.toUsuario(entidad);

        // ASSERT
        assertEquals(entidad.getId(),                  usuario.getId());
        assertEquals(entidad.getNombre(),              usuario.getNombre());
        assertEquals(entidad.getApellido(),            usuario.getApellido());
        assertEquals(entidad.getDocumentoDeIdentidad(),usuario.getDocumentoDeIdentidad());
        assertEquals(entidad.getCelular(),             usuario.getCelular());
        assertEquals(entidad.getFechaNacimiento(),     usuario.getFechaNacimiento());
        assertEquals(entidad.getCorreo(),              usuario.getCorreo());
        assertEquals(entidad.getClave(),               usuario.getClave());
        assertEquals(entidad.getRol(),                 usuario.getRol());
    }
}
