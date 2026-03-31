package ms_usuario.plazoleta.ms_usuario.infrastructure.input.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ms_usuario.plazoleta.ms_usuario.domain.ports.in.IUsuarioServicePort;
import ms_usuario.plazoleta.ms_usuario.infrastructure.input.rest.dto.UsuarioRequestDto;
import ms_usuario.plazoleta.ms_usuario.infrastructure.input.rest.mapper.IUsuarioRequestMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para operaciones de Usuario.
 *
 * @RestController → combina @Controller + @ResponseBody.
 * Indica que esta clase maneja peticiones HTTP y que los métodos
 * devuelven datos directamente en el body de la respuesta (JSON),
 * no una vista HTML.
 *
 * @RequestMapping("/usuarios") → todos los endpoints de este controlador
 * empiezan con /usuarios.
 *
 * @RequiredArgsConstructor → Lombok genera el constructor con los campos final,
 * permitiendo a Spring inyectar las dependencias automáticamente.
 *
 * Nota importante: el controlador depende de IUsuarioServicePort (interfaz),
 * NO de UsuarioUseCase (implementación). Así respetamos la inversión
 * de dependencias — el controlador no sabe NI LE IMPORTA cómo se implementa
 * la lógica de negocio.
 */
@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioRestController {

    private final IUsuarioServicePort usuarioServicePort;
    private final IUsuarioRequestMapper usuarioRequestMapper;

    @PostMapping("/propietario")
    public ResponseEntity<Void> guardarPropietario(@Valid @RequestBody UsuarioRequestDto usuarioRequestDto) {
        usuarioServicePort.guardarPropietario(
                usuarioRequestMapper.toDomain(usuarioRequestDto)
        );
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
