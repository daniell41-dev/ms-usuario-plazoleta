package com.plazoleta.ms_usuario.infrastructure.input.rest.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * DTO de entrada para crear una cuenta de Cliente.
 *
 * ¿Por qué un DTO separado del de empleado si son casi iguales?
 * Porque representan contextos distintos: el empleado lo crea un PROPIETARIO,
 * el cliente se registra a sí mismo. Si en el futuro los campos divergen
 * (por ejemplo, el cliente agrega dirección de envío), no tenemos que
 * modificar un DTO compartido.
 *
 * Ejemplo del JSON esperado:
 * {
 *   "nombre": "Ana",
 *   "apellido": "García",
 *   "documentoDeIdentidad": "112233445",
 *   "celular": "+573009876543",
 *   "correo": "ana@email.com",
 *   "idRol": 4,
 *   "clave": "miClave123"
 * }
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ClienteRequestDto {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @NotBlank(message = "El documento de identidad es obligatorio")
    @Pattern(regexp = "\\d+", message = "El documento de identidad debe ser únicamente numérico")
    private String documentoDeIdentidad;

    @NotBlank(message = "El celular es obligatorio")
    @Size(max = 13, message = "El celular debe tener máximo 13 caracteres")
    @Pattern(regexp = "^\\+?\\d{7,13}$", message = "El celular debe contener solo números y puede iniciar con +")
    private String celular;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo debe tener una estructura válida")
    private String correo;

    /**
     * El caso de uso ignorará este valor y siempre asignará CLIENTE.
     * Se incluye porque la historia de usuario lo pide como campo obligatorio.
     */
    @NotNull(message = "El idRol es obligatorio")
    private Long idRol;

    @NotBlank(message = "La clave es obligatoria")
    private String clave;
}
