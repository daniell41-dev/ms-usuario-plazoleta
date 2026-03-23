package com.plazoleta.ms_usuario.infrastructure.output.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.plazoleta.ms_usuario.domain.model.Rol;

import java.time.LocalDate;

/**
 * Entidad JPA que representa la tabla "usuarios" en la base de datos.
 *
 * ¿Por qué usamos Lombok aquí y no en el dominio?
 * En infraestructura sí aceptamos dependencias externas como Lombok y JPA.
 * @Getter y @Setter generan automáticamente todos los getters/setters.
 * @NoArgsConstructor genera el constructor vacío que JPA NECESITA obligatoriamente
 *   para poder reconstruir objetos desde la base de datos (usa reflexión internamente).
 * @AllArgsConstructor genera un constructor con todos los campos, útil para el mapper.
 *
 * ¿Qué significa @Entity?
 * Le dice a JPA: "esta clase representa una tabla en la base de datos".
 * Hibernate (la implementación de JPA que usa Spring Boot) creará/validará
 * la tabla "usuarios" según los campos de esta clase.
 *
 * ¿Qué significa @Table(name = "usuarios")?
 * Por defecto JPA usaría el nombre de la clase ("UsuarioEntity") como nombre
 * de tabla. Con @Table le decimos explícitamente que queremos "usuarios".
 */
@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioEntity {

    /**
     * @Id → este campo es la clave primaria de la tabla
     * @GeneratedValue → la BD genera el valor automáticamente (auto-increment)
     * GenerationType.IDENTITY → delega la generación al motor de BD (PostgreSQL usa SERIAL/SEQUENCE)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * @Column(nullable = false) → este campo NO puede ser null en la BD.
     * Es una restricción a nivel de base de datos, adicional a las validaciones
     * que ya hacemos en el dominio.
     */
    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Column(name = "documento_de_identidad", nullable = false, unique = true)
    private String documentoDeIdentidad;

    @Column(nullable = false, length = 13)
    private String celular;

    @Column(name = "fecha_nacimiento", nullable = true)
    private LocalDate fechaNacimiento;

    @Column(nullable = false, unique = true)
    private String correo;

    @Column(nullable = false)
    private String clave;

    /**
     * @Enumerated(EnumType.STRING) → guarda el nombre del enum como texto en BD.
     * Sin esto, JPA guardaría el ÍNDICE numérico (0, 1, 2...) lo cual es
     * muy peligroso: si agregas un valor al enum en otra posición, todos
     * los datos existentes quedan inconsistentes.
     * Con STRING siempre se guarda "PROPIETARIO", "CLIENTE", etc.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;
}
