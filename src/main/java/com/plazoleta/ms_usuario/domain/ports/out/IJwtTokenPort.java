package com.plazoleta.ms_usuario.domain.ports.out;

/**
 * Puerto de SALIDA para generación de tokens de autenticación.
 *
 * El dominio/aplicación solo conoce este contrato: "puedo generar un token
 * a partir de los datos de un usuario". No sabe que por debajo se usa JWT,
 * ni que la clave está en application.properties. Eso es infraestructura.
 *
 * ¿Por qué existe este puerto?
 * UsuarioUseCase necesita generar un token al hacer login. Si dependiera
 * directamente de JwtTokenProvider (infraestructura), rompería la regla
 * fundamental de la arquitectura hexagonal: la aplicación no puede depender
 * de la infraestructura, solo al revés.
 */
public interface IJwtTokenPort {
    String generarToken(Long id, String correo, String rol);
}
