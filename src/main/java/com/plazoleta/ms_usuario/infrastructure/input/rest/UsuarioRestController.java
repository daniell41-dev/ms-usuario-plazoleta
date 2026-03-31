package com.plazoleta.ms_usuario.infrastructure.input.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.plazoleta.ms_usuario.domain.ports.in.IUsuarioServicePort;
import com.plazoleta.ms_usuario.infrastructure.input.rest.dto.ClienteRequestDto;
import com.plazoleta.ms_usuario.infrastructure.input.rest.dto.EmpleadoRequestDto;
import com.plazoleta.ms_usuario.infrastructure.input.rest.dto.UsuarioRequestDto;
import com.plazoleta.ms_usuario.infrastructure.input.rest.dto.UsuarioRolResponseDto;
import com.plazoleta.ms_usuario.infrastructure.input.rest.mapper.IUsuarioRequestMapper;
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

    @PostMapping("/empleado")
    public ResponseEntity<Void> guardarEmpleado(@Valid @RequestBody EmpleadoRequestDto empleadoRequestDto) {
        usuarioServicePort.guardarEmpleado(
                usuarioRequestMapper.toEmpleadoDomain(empleadoRequestDto)
        );
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/cliente")
    public ResponseEntity<Void> guardarCliente(@Valid @RequestBody ClienteRequestDto clienteRequestDto) {
        usuarioServicePort.guardarCliente(
                usuarioRequestMapper.toClienteDomain(clienteRequestDto)
        );
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{id}/rol")
    public ResponseEntity<UsuarioRolResponseDto> obtenerRolPorId(@PathVariable Long id){
        String usuarioRolResponseDto = usuarioServicePort.obtenerRolUsuario(id);
        return ResponseEntity.ok(new UsuarioRolResponseDto(usuarioRolResponseDto));
    }
}
