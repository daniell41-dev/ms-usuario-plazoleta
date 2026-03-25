package com.plazoleta.ms_usuario.infrastructure.output.persistence.repository;

import com.plazoleta.ms_usuario.infrastructure.output.persistence.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositorio JPA para la entidad UsuarioEntity.
 *
 * ¿Por qué extiende JpaRepository<UsuarioEntity, Long>?
 * JpaRepository es una interfaz de Spring Data que ya trae implementados
 * los métodos más comunes: save(), findById(), findAll(), delete(), etc.
 * El primer genérico es la entidad, el segundo es el tipo del ID.
 * No necesitamos escribir ni una línea de SQL para estas operaciones básicas.
 *
 * ¿Por qué está en infraestructura y no en el dominio?
 * Porque extiende JpaRepository — una clase de Spring Data.
 * El dominio no puede conocer Spring Data. Por eso el dominio tiene
 * IUsuarioPersistencePort (interfaz pura), y este repositorio es
 * un detalle de implementación que queda oculto en infraestructura.
 *
 * Convención de Spring Data: si el método se llama "findByCorreo",
 * Spring AUTOMÁTICAMENTE genera el SQL: SELECT * FROM usuarios WHERE correo = ?
 * No necesitas escribir la query — Spring la infiere del nombre del método.
 */
public interface IUsuarioRepository extends JpaRepository<UsuarioEntity, Long> {

    /**
     * Busca un usuario por su correo electrónico.
     * Spring Data genera: SELECT * FROM usuarios WHERE correo = :correo
     *
     * Devuelve Optional para forzar el manejo explícito del caso "no existe".
     */
    Optional<UsuarioEntity> findByCorreo(String correo);

    /**
     * Verifica si existe un usuario con el documento de identidad dado.
     * Spring Data genera: SELECT COUNT(*) > 0 FROM usuarios WHERE documento_de_identidad = :documentoDeIdentidad
     *
     * Devuelve boolean directamente — más eficiente que traer toda la entidad
     * cuando solo necesitamos saber si existe o no.
     */
    boolean existsByDocumentoDeIdentidad(String documentoDeIdentidad);
}
