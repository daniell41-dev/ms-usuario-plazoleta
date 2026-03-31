package com.plazoleta.ms_usuario.domain.ports.out;

/**
 * Puerto de SALIDA para la codificación de claves.
 *
 * ¿Por qué existe este puerto?
 * El caso de uso necesita encriptar claves antes de persistirlas, pero NO debe
 * saber que por debajo se usa BCrypt ni que eso viene de Spring Security.
 * El dominio solo conoce el contrato: "necesito codificar una clave en texto plano".
 *
 * La implementación concreta (BCryptClaveCodificador) vive en infraestructura
 * y usa Spring Security internamente. Si mañana cambiamos el algoritmo, solo
 * cambia el adaptador — el caso de uso no se toca.
 */
public interface IClaveCodificadorPort {

    /**
     * Codifica una clave en texto plano con el algoritmo configurado.
     *
     * @param claveTextoPlano La clave tal como la envió el usuario.
     * @return La clave codificada lista para persistir. Nunca se guarda texto plano.
     */
    String codificar(String claveTextoPlano);
}
