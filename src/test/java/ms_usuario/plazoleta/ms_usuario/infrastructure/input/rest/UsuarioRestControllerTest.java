package ms_usuario.plazoleta.ms_usuario.infrastructure.input.rest;

import ms_usuario.plazoleta.ms_usuario.domain.model.Rol;
import ms_usuario.plazoleta.ms_usuario.domain.model.Usuario;
import ms_usuario.plazoleta.ms_usuario.domain.ports.in.IUsuarioServicePort;
import ms_usuario.plazoleta.ms_usuario.infrastructure.input.rest.dto.UsuarioRequestDto;
import ms_usuario.plazoleta.ms_usuario.infrastructure.input.rest.mapper.IUsuarioRequestMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioRestControllerTest {

    @Mock
    private IUsuarioServicePort usuarioServicePort;

    @Mock
    private IUsuarioRequestMapper usuarioRequestMapper;

    @InjectMocks
    private UsuarioRestController usuarioRestController;

    @Test
    void cuandoRecibeRequestValido_devuelve201() {
        // ARRANGE
        UsuarioRequestDto dto = new UsuarioRequestDto(
                "Juan", "Pérez", "12345678",
                "+573001234567",
                LocalDate.of(2000, 1, 15),
                "juan@correo.com", "clave123"
        );

        Usuario usuarioDominio = new Usuario(
                null, "Juan", "Pérez", "12345678",
                "+573001234567",
                LocalDate.of(2000, 1, 15),
                "juan@correo.com", "clave123", null
        );

        when(usuarioRequestMapper.toDomain(dto)).thenReturn(usuarioDominio);
        doNothing().when(usuarioServicePort).guardarPropietario(any());

        // ACT
        ResponseEntity<Void> respuesta = usuarioRestController.guardarPropietario(dto);

        // ASSERT
        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        verify(usuarioRequestMapper, times(1)).toDomain(dto);
        verify(usuarioServicePort, times(1)).guardarPropietario(usuarioDominio);
    }
}
