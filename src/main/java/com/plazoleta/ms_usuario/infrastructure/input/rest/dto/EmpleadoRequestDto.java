package com.plazoleta.ms_usuario.infrastructure.input.rest.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * DTO de entrada para crear un empleado.
 *
 * ¿Por qué un DTO separado de UsuarioRequestDto?
 * Porque los campos son distintos: el empleado no requiere fechaNacimiento
 * (no hay validación de mayoría de edad) y sí incluye idRol.
 * Reutilizar el DTO de propietario agregaría campos irrelevantes o
 * requeriría hacerlos opcionales, lo que rompería las validaciones.
 *
 * Ejemplo del JSON esperado:
 * {
 *   "nombre": "Carlos",
 *   "apellido": "López",
 *   "documentoDeIdentidad": "987654321",
 *   "celular": "+573001234567",
 *   "correo": "carlos@email.com",
 *   "idRol": 3,
 *   "clave": "miClave123"
 * }
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmpleadoRequestDto {

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
     * Identificador del rol a asignar.
     * El caso de uso ignorará este valor y siempre asignará EMPLEADO,
     * igual que guardarPropietario siempre asigna PROPIETARIO.
     */
    @NotNull(message = "El idRol es obligatorio")
    private Long idRol;

    @NotBlank(message = "La clave es obligatoria")
    private String clave;
}
