package ms_usuario.plazoleta.ms_usuario.domain.model;

/**
 * Enum que representa los roles posibles de un usuario en el sistema Plazoleta.
 *
 * ¿Por qué un enum y no una tabla en BD?
 * Los roles son un conjunto FIJO y CONOCIDO en tiempo de diseño. No van a cambiar
 * en tiempo de ejecución — no hay un endpoint para "crear un nuevo rol".
 * Usar un enum es más seguro (el compilador detecta errores), más eficiente
 * (no hay consulta a BD) y más expresivo.
 *
 * ¿Por qué está en el dominio?
 * El concepto de "rol" es una regla de negocio pura: "un usuario puede ser
 * administrador, propietario, empleado o cliente". Eso no depende de Spring,
 * JPA ni ninguna librería — es solo Java.
 */
public enum Rol {
    ADMINISTRADOR,
    PROPIETARIO,
    EMPLEADO,
    CLIENTE
}
