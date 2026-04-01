package com.plazoleta.ms_usuario.infrastructure.input.rest;

import com.plazoleta.ms_usuario.domain.exception.CredencialesInvalidasException;
import com.plazoleta.ms_usuario.domain.exception.MenorDeEdadException;
import com.plazoleta.ms_usuario.domain.exception.UsuarioNoEncontradoException;
import com.plazoleta.ms_usuario.domain.exception.UsuarioYaExisteException;
import com.plazoleta.ms_usuario.infrastructure.input.rest.dto.ErrorResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleUsuarioYaExiste_devuelve409ConMensaje() {
        UsuarioYaExisteException ex = new UsuarioYaExisteException("Correo ya registrado");

        ResponseEntity<ErrorResponseDto> response = handler.handleUsuarioYaExiste(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(409, response.getBody().getStatus());
        assertEquals("Correo ya registrado", response.getBody().getMensaje());
    }

    @Test
    void handleUsuarioNoEncontrado_devuelve404ConMensaje() {
        UsuarioNoEncontradoException ex = new UsuarioNoEncontradoException("Usuario no encontrado");

        ResponseEntity<ErrorResponseDto> response = handler.handleUsuarioNoEncontrado(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Usuario no encontrado", response.getBody().getMensaje());
    }

    @Test
    void handleMenorDeEdad_devuelve400ConMensaje() {
        MenorDeEdadException ex = new MenorDeEdadException();

        ResponseEntity<ErrorResponseDto> response = handler.handleMenorDeEdad(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().getStatus());
    }

    @Test
    void handleCredencialesInvalidas_devuelve401ConMensaje() {
        CredencialesInvalidasException ex = new CredencialesInvalidasException();

        ResponseEntity<ErrorResponseDto> response = handler.handleCredencialesInvalidas(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(401, response.getBody().getStatus());
    }

    @Test
    void handleDuplicado_devuelve409ConMensajeGenerico() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException("duplicate key");

        ResponseEntity<ErrorResponseDto> response = handler.handleDuplicado(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(409, response.getBody().getStatus());
        assertEquals("Ya existe un registro con esos datos", response.getBody().getMensaje());
    }

    @Test
    void handleGeneral_devuelve500SinDetallesInternos() {
        Exception ex = new RuntimeException("error interno inesperado");

        ResponseEntity<ErrorResponseDto> response = handler.handleGeneral(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("Error interno del servidor", response.getBody().getMensaje());
    }

    @Test
    void handleValidacion_devuelve400ConPrimerMensajeDeError() throws Exception {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "dto");
        bindingResult.addError(new FieldError("dto", "correo", "El correo es obligatorio"));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ErrorResponseDto> response = handler.handleValidacion(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("El correo es obligatorio", response.getBody().getMensaje());
    }
}
