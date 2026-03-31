package ms_usuario.plazoleta.ms_usuario.domain.util;

import ms_usuario.plazoleta.ms_usuario.domain.exception.MenorDeEdadException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EdadUtilTest {

    // La edad mínima que usamos en todos los tests de este archivo
    private static final int EDAD_MINIMA = 18;

    @Test
    void cuandoTiene20Anos_noLanzaExcepcion() {
        // ARRANGE: fecha de nacimiento = hoy hace exactamente 20 años
        LocalDate fechaNacimiento = LocalDate.now().minusYears(20);

        // ACT + ASSERT: el método no debe lanzar ninguna excepción
        assertDoesNotThrow(() ->
                EdadUtil.validarMayoriaDeEdad(fechaNacimiento, EDAD_MINIMA)
        );
    }

    @Test
    void cuandoTieneExactamente18Anos_noLanzaExcepcion() {
        // ARRANGE: hoy cumple 18 años — debe ser válido (>= 18)
        LocalDate fechaNacimiento = LocalDate.now().minusYears(18);

        // ACT + ASSERT
        assertDoesNotThrow(() ->
                EdadUtil.validarMayoriaDeEdad(fechaNacimiento, EDAD_MINIMA)
        );
    }

    @Test
    void cuandoTiene17Anos_lanzaMenorDeEdadException() {
        // ARRANGE: todavía no cumplió 18
        LocalDate fechaNacimiento = LocalDate.now().minusYears(17);

        // ACT + ASSERT: debe lanzar MenorDeEdadException
        assertThrows(MenorDeEdadException.class, () ->
                EdadUtil.validarMayoriaDeEdad(fechaNacimiento, EDAD_MINIMA)
        );
    }

    @Test
    void cuandoTieneUnDiaAntesDe18Anos_lanzaMenorDeEdadException() {
        // ARRANGE: le falta exactamente 1 día para cumplir 18
        // minusYears(18) = hoy cumple 18 → le sumamos 1 día = aún no cumplió
        LocalDate fechaNacimiento = LocalDate.now().minusYears(18).plusDays(1);

        // ACT + ASSERT
        assertThrows(MenorDeEdadException.class, () ->
                EdadUtil.validarMayoriaDeEdad(fechaNacimiento, EDAD_MINIMA)
        );
    }
}
