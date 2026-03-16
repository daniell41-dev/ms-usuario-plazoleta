package ms_usuario.plazoleta.ms_usuario.domain.exception;

/**
 * Excepción de dominio: se lanza cuando se intenta crear un usuario
 * que no cumple el requisito de mayoría de edad (menor de 18 años).
 *
 * Esta validación vive en el DOMINIO porque "ser mayor de edad" es una
 * regla de negocio del sistema Plazoleta, no una validación de formato
 * ni una restricción de base de datos.
 */
public class MenorDeEdadException extends RuntimeException {
    public MenorDeEdadException() {
        super("El usuario debe ser mayor de edad para registrarse");
    }
}
