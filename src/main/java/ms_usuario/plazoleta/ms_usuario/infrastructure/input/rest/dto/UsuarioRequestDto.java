package ms_usuario.plazoleta.ms_usuario.infrastructure.input.rest.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO (Data Transfer Object) de entrada para crear un usuario.
 *
 * ¿Qué es un DTO?
 * Es un objeto cuyo único propósito es transportar datos entre capas.
 * NO tiene lógica de negocio. Solo define la estructura del JSON
 * que el cliente debe enviar en el body del request HTTP.
 *
 * Ejemplo del JSON esperado:
 * {
 *   "nombre": "Juan",
 *   "apellido": "Pérez",
 *   "documentoDeIdentidad": "123456789",
 *   "celular": "+573005698325",
 *   "fechaNacimiento": "1990-05-20",
 *   "correo": "juan@email.com",
 *   "clave": "miClave123"
 * }
 *
 * ¿Por qué las validaciones están aquí y no en el dominio?
 * @NotBlank, @Email, @Pattern son anotaciones de Jakarta Validation —
 * una dependencia externa. El dominio no puede tener dependencias externas.
 * Estas validaciones verifican FORMATO (¿es un email válido? ¿tiene el
 * formato correcto?). Las reglas de NEGOCIO (¿es mayor de edad?) siguen
 * en el dominio.
 *
 * ¿Por qué solo @Getter y no @Setter?
 * Un DTO de entrada no necesita setters. Una vez que Spring deserializa
 * el JSON, el objeto no debería modificarse. @AllArgsConstructor y
 * @NoArgsConstructor son necesarios para la deserialización de Jackson.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRequestDto {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    /**
     * @Pattern: verifica que el documento sea únicamente numérico.
     * La expresión regular \d+ significa "uno o más dígitos (0-9)".
     */
    @NotBlank(message = "El documento de identidad es obligatorio")
    @Pattern(regexp = "\\d+", message = "El documento de identidad debe ser únicamente numérico")
    private String documentoDeIdentidad;

    /**
     * @Size(max = 13): máximo 13 caracteres.
     * @Pattern: permite dígitos y el símbolo + al inicio (opcional).
     * La regex ^\+?\d{7,13}$ significa:
     *   ^ → inicio de la cadena
     *   \+? → el símbolo + es opcional (0 o 1 vez)
     *   \d{7,13} → entre 7 y 13 dígitos
     *   $ → fin de la cadena
     */
    @NotBlank(message = "El celular es obligatorio")
    @Size(max = 13, message = "El celular debe tener máximo 13 caracteres")
    @Pattern(regexp = "^\\+?\\d{7,13}$", message = "El celular debe contener solo números y puede iniciar con +")
    private String celular;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    private LocalDate fechaNacimiento;

    /**
     * @Email: verifica que tenga la estructura de un email válido (algo@algo.algo).
     */
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo debe tener una estructura válida")
    private String correo;

    @NotBlank(message = "La clave es obligatoria")
    private String clave;
}
