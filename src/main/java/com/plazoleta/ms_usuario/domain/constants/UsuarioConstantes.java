package com.plazoleta.ms_usuario.domain.constants;

public final class UsuarioConstantes {

    private UsuarioConstantes() {}

    /**
     * Edad mínima requerida para registrarse como propietario.
     * Regla de negocio: solo mayores de edad pueden ser propietarios.
     */
    public static final int EDAD_MINIMA = 18;

    /**
     * Prefijo estándar del header Authorization para autenticación Bearer.
     * Formato: "Authorization: Bearer <token>"
     */
    public static final String BEARER_PREFIX = "Bearer ";
}
