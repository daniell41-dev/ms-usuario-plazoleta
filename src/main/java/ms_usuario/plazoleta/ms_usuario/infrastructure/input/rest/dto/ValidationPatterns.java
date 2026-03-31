package ms_usuario.plazoleta.ms_usuario.infrastructure.input.rest.dto;

/**
 * Constantes de validación para los DTOs de entrada.
 *
 * ¿Por qué una clase separada y no las constantes dentro del DTO?
 * Las anotaciones de Jakarta (@Pattern, @Size) requieren valores que sean
 * constantes en tiempo de compilación (compile-time constants). Al definirlas
 * aquí como public static final, pueden usarse en cualquier DTO del paquete.
 *
 * ¿Por qué está en infraestructura y no en el dominio?
 * Estas regex validan FORMATO HTTP — "¿tiene forma de email?", "¿son solo dígitos?".
 * Las reglas de negocio ("¿es mayor de edad?") siguen en el dominio.
 * El formato de entrada es responsabilidad de la capa de infraestructura.
 *
 * Constructor privado: esta clase no debe instanciarse, solo usarse como
 * contenedor de constantes.
 */
public final class ValidationPatterns {

    private ValidationPatterns() {}

    /**
     * Uno o más dígitos (0-9). Sin letras, sin espacios, sin símbolos.
     * Usado para validar documentos de identidad.
     */
    public static final String SOLO_DIGITOS = "\\d+";

    /**
     * Número de celular internacional.
     * Permite un + opcional al inicio, seguido de entre 7 y 13 dígitos.
     * Ejemplos válidos: +573005698325, 3005698325, 5551234567
     */
    public static final String FORMATO_CELULAR = "^\\+?\\d{7,13}$";

    /**
     * Longitud máxima de un número de celular (con el + incluido).
     * +573005698325 = 13 caracteres.
     */
    public static final int LONGITUD_MAXIMA_CELULAR = 13;
}
