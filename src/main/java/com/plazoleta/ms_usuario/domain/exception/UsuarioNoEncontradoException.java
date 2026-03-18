package com.plazoleta.ms_usuario.domain.exception;

/**
 * Funcion para colocar mensaje de error para usuario no encontrados
 */
public class UsuarioNoEncontradoException extends RuntimeException {
    public UsuarioNoEncontradoException(String message) {
        super(message);
    }
}
