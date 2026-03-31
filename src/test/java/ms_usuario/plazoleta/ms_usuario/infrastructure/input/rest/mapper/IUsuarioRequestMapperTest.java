package ms_usuario.plazoleta.ms_usuario.infrastructure.input.rest.mapper;

import ms_usuario.plazoleta.ms_usuario.domain.model.Usuario;
import ms_usuario.plazoleta.ms_usuario.infrastructure.input.rest.dto.UsuarioRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

// No necesita @ExtendWith(MockitoExtension.class) porque IUsuarioRequestMapper
// no tiene dependencias — es una clase concreta que instanciamos directamente.
class IUsuarioRequestMapperTest {

    private IUsuarioRequestMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new IUsuarioRequestMapper();
    }

    @Test
    void cuandoConvierteDto_mapeaTodosLosCamposCorrectamente() {
        // ARRANGE
        LocalDate fechaNacimiento = LocalDate.of(1995, 6, 15);
        UsuarioRequestDto dto = new UsuarioRequestDto(
                "María",
                "González",
                "98765432",
                "+573009876543",
                fechaNacimiento,
                "maria@correo.com",
                "clave456"
        );

        // ACT
        Usuario resultado = mapper.toDomain(dto);

        // ASSERT — verificamos campo por campo
        // El id debe ser null: aún no fue persistido, la BD lo generará
        assertNull(resultado.getId());

        assertEquals("María",            resultado.getNombre());
        assertEquals("González",         resultado.getApellido());
        assertEquals("98765432",         resultado.getDocumentoDeIdentidad());
        assertEquals("+573009876543",    resultado.getCelular());
        assertEquals(fechaNacimiento,    resultado.getFechaNacimiento());
        assertEquals("maria@correo.com", resultado.getCorreo());
        assertEquals("clave456",         resultado.getClave());

        // El rol debe ser null: el caso de uso lo asigna, no el mapper
        assertNull(resultado.getRol());
    }
}
