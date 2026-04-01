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
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    // ─── handleValidacion ────────────────────────────────────────────────────

    @Test
    void handleValidacion_deberiaRetornar400ConMensajeDelPrimerCampoInvalido() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("usuarioRequestDto", "correo", "El correo es obligatorio");
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<ErrorResponseDto> response = handler.handleValidacion(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getMensaje()).isEqualTo("El correo es obligatorio");
    }

    @Test
    void handleValidacion_sinErroresDeCampo_usaMensajeFallback() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of());

        ResponseEntity<ErrorResponseDto> response = handler.handleValidacion(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getMensaje()).isEqualTo("Error de validación en los datos enviados");
    }

    // ─── handleUsuarioYaExiste ───────────────────────────────────────────────

    @Test
    void handleUsuarioYaExiste_deberiaRetornar409ConMensajeDeLaExcepcion() {
        UsuarioYaExisteException ex = new UsuarioYaExisteException("Ya existe un correo registrado");

        ResponseEntity<ErrorResponseDto> response = handler.handleUsuarioYaExiste(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().getStatus()).isEqualTo(409);
        assertThat(response.getBody().getMensaje()).isEqualTo("Ya existe un correo registrado");
    }

    // ─── handleUsuarioNoEncontrado ───────────────────────────────────────────

    @Test
    void handleUsuarioNoEncontrado_deberiaRetornar404ConMensajeDeLaExcepcion() {
        UsuarioNoEncontradoException ex = new UsuarioNoEncontradoException("No se encontró el usuario con id 5");

        ResponseEntity<ErrorResponseDto> response = handler.handleUsuarioNoEncontrado(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getStatus()).isEqualTo(404);
        assertThat(response.getBody().getMensaje()).isEqualTo("No se encontró el usuario con id 5");
    }

    // ─── handleMenorDeEdad ───────────────────────────────────────────────────

    @Test
    void handleMenorDeEdad_deberiaRetornar400ConMensajeDeDominio() {
        MenorDeEdadException ex = new MenorDeEdadException();

        ResponseEntity<ErrorResponseDto> response = handler.handleMenorDeEdad(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getMensaje()).contains("mayor de edad");
    }

    // ─── handleCredencialesInvalidas ─────────────────────────────────────────

    @Test
    void handleCredencialesInvalidas_deberiaRetornar401() {
        CredencialesInvalidasException ex = new CredencialesInvalidasException();

        ResponseEntity<ErrorResponseDto> response = handler.handleCredencialesInvalidas(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().getStatus()).isEqualTo(401);
        assertThat(response.getBody().getMensaje()).isEqualTo("Correo o clave incorrectos");
    }

    // ─── handleDuplicado ─────────────────────────────────────────────────────

    @Test
    void handleDuplicado_deberiaRetornar409ConMensajeGenerico() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException("duplicate key");

        ResponseEntity<ErrorResponseDto> response = handler.handleDuplicado(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().getStatus()).isEqualTo(409);
        assertThat(response.getBody().getMensaje()).isEqualTo("Ya existe un registro con esos datos");
    }

    // ─── handleGeneral ───────────────────────────────────────────────────────

    @Test
    void handleGeneral_deberiaRetornar500SinExponerDetallesInternos() {
        Exception ex = new RuntimeException("Fallo en base de datos: detalles internos sensibles");

        ResponseEntity<ErrorResponseDto> response = handler.handleGeneral(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getStatus()).isEqualTo(500);
        assertThat(response.getBody().getMensaje()).isEqualTo("Error interno del servidor");
        assertThat(response.getBody().getMensaje()).doesNotContain("base de datos");
    }
}
