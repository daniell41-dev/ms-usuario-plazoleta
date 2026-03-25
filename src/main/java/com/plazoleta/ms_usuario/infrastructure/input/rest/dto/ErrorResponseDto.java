package com.plazoleta.ms_usuario.infrastructure.input.rest.dto;

/**
 * DTO de respuesta para errores HTTP.
 *
 * ¿Por qué existe este DTO?
 * Sin él, cuando ocurre una excepción Spring devuelve su propio JSON de error,
 * que incluye campos internos como "trace", "path", "timestamp" — información
 * técnica que no le sirve al cliente y que puede exponer detalles sensibles.
 *
 * Con este DTO controlamos exactamente qué le mostramos al cliente:
 * solo el código HTTP y un mensaje descriptivo.
 *
 * Ejemplo de respuesta:
 * {
 *   "status": 409,
 *   "mensaje": "Ya existe un usuario registrado con ese correo electrónico"
 * }
 *
 * ¿Por qué no usa Lombok?
 * Es un DTO simple con dos campos. El constructor explícito y los getters
 * son suficientes y hacen el código más legible sin dependencias adicionales.
 */
public class ErrorResponseDto {

    private int status;
    private String mensaje;

    public ErrorResponseDto(int status, String mensaje) {
        this.status = status;
        this.mensaje = mensaje;
    }

    public int getStatus() {
        return status;
    }

    public String getMensaje() {
        return mensaje;
    }
}
