package com.plazoleta.ms_usuario.infrastructure.input.rest;

import com.plazoleta.ms_usuario.domain.exception.CredencialesInvalidasException;
import com.plazoleta.ms_usuario.domain.exception.MenorDeEdadException;
import com.plazoleta.ms_usuario.domain.exception.UsuarioNoEncontradoException;
import com.plazoleta.ms_usuario.domain.exception.UsuarioYaExisteException;
import com.plazoleta.ms_usuario.infrastructure.input.rest.dto.ErrorResponseDto;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Manejador global de excepciones.
 *
 * @RestControllerAdvice intercepta todas las excepciones lanzadas por cualquier
 * controlador REST y las convierte en respuestas HTTP con formato uniforme.
 *
 * Sin esta clase, Spring devuelve su propio JSON de error con campos internos
 * (trace, path, timestamp) que exponen detalles técnicos al cliente.
 *
 * Flujo:
 *   Excepción lanzada en UseCase o Controller
 *     → Spring busca un @ExceptionHandler que la maneje
 *     → Devuelve ResponseEntity<ErrorResponseDto>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Validaciones del DTO (@NotBlank, @Email, @Pattern, etc.)
     * Spring lanza esta excepción ANTES de llegar al caso de uso.
     * Extrae el primer mensaje de error de validación para mostrárselo al cliente.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidacion(MethodArgumentNotValidException ex) {
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getDefaultMessage())
                .findFirst()
                .orElse("Error de validación en los datos enviados");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDto(HttpStatus.BAD_REQUEST.value(), mensaje));
    }

    /**
     * Correo o documento de identidad ya registrado.
     * HTTP 409 Conflict: el recurso ya existe, no es un error del cliente.
     */
    @ExceptionHandler(UsuarioYaExisteException.class)
    public ResponseEntity<ErrorResponseDto> handleUsuarioYaExiste(UsuarioYaExisteException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDto(HttpStatus.CONFLICT.value(), ex.getMessage()));
    }

    /**
     * Usuario no encontrado por ID.
     * HTTP 404 Not Found: el recurso solicitado no existe.
     */
    @ExceptionHandler(UsuarioNoEncontradoException.class)
    public ResponseEntity<ErrorResponseDto> handleUsuarioNoEncontrado(UsuarioNoEncontradoException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponseDto(HttpStatus.NOT_FOUND.value(), ex.getMessage()));
    }

    /**
     * El usuario es menor de edad.
     * HTTP 400 Bad Request: los datos enviados no cumplen las reglas de negocio.
     */
    @ExceptionHandler(MenorDeEdadException.class)
    public ResponseEntity<ErrorResponseDto> handleMenorDeEdad(MenorDeEdadException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDto(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
    }

    /**
     * Credenciales inválidas en el login.
     * HTTP 401 Unauthorized: no se pudo autenticar con los datos proporcionados.
     */
    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<ErrorResponseDto> handleCredencialesInvalidas(CredencialesInvalidasException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponseDto(HttpStatus.UNAUTHORIZED.value(), ex.getMessage()));
    }

    /**
     * Violación de restricción única en la base de datos (fallback de BD).
     * HTTP 409 Conflict: ocurre si un campo único se duplica a nivel de BD,
     * aunque el caso de uso no lo haya detectado antes.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleDuplicado(DataIntegrityViolationException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDto(HttpStatus.CONFLICT.value(),
                        "Ya existe un registro con esos datos"));
    }

    /**
     * Fallback: cualquier excepción no contemplada.
     * HTTP 500: no exponemos detalles internos al cliente.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGeneral(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Error interno del servidor"));
    }
}
