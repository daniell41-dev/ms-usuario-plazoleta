package com.plazoleta.ms_usuario.domain.exception;

/**
 * Excepción de dominio: se lanza cuando se intenta crear un usuario
 * con un correo que ya está registrado en el sistema.
 *
 * ¿Por qué extiende RuntimeException y no Exception?
 * Las excepciones "checked" (que extienden Exception) obligan a quien llama
 * a manejarlas con try-catch o declararlas con throws. En aplicaciones modernas
 * de Spring preferimos "unchecked" (RuntimeException) porque Spring las captura
 * automáticamente y las convierte en respuestas HTTP apropiadas.
 *
 * ¿Por qué está en el dominio y no en infraestructura?
 * "El correo ya existe" es una regla de NEGOCIO — el dominio decide que
 * no puede haber dos usuarios con el mismo correo. No es un error de HTTP
 * ni de base de datos, es una violación de una regla del sistema.
 */
public class UsuarioYaExisteException extends RuntimeException {
    public UsuarioYaExisteException() {
        super("Ya existe un usuario registrado con ese correo electrónico");
    }
}
