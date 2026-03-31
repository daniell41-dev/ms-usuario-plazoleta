package ms_usuario.plazoleta.ms_usuario.domain.util;

import ms_usuario.plazoleta.ms_usuario.domain.exception.MenorDeEdadException;

import java.time.LocalDate;
import java.time.Period;

public class EdadUtil {

    private EdadUtil() {}

    /**
     * Valida que la fecha de nacimiento corresponda a una persona
     * con al menos {@code edadMinima} años cumplidos a la fecha actual.
     * Lanza {@link MenorDeEdadException} si no se cumple la condición.
     */
    public static void validarMayoriaDeEdad(LocalDate fechaNacimiento, int edadMinima) {
        int edad = Period.between(fechaNacimiento, LocalDate.now()).getYears();
        if (edad < edadMinima) {
            throw new MenorDeEdadException();
        }
    }
}
