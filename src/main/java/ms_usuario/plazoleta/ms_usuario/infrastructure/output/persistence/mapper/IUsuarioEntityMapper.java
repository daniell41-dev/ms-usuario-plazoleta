package ms_usuario.plazoleta.ms_usuario.infrastructure.output.persistence.mapper;

import ms_usuario.plazoleta.ms_usuario.domain.model.Usuario;
import ms_usuario.plazoleta.ms_usuario.infrastructure.output.persistence.entity.UsuarioEntity;

/**
 * Contrato del mapper entre el modelo de dominio y la entidad JPA.
 *
 * ¿Por qué necesitamos un mapper?
 * Tenemos dos representaciones del mismo concepto:
 *   - Usuario (dominio): Java puro, sin dependencias
 *   - UsuarioEntity (JPA): con anotaciones @Entity, @Column, etc.
 *
 * El mapper es el "traductor" que convierte entre ambos mundos.
 * Sin él, tendríamos que acoplar el dominio a JPA o viceversa.
 *
 * En proyectos más grandes se usa MapStruct (genera el mapper automáticamente
 * en tiempo de compilación). Aquí lo hacemos manual para entender qué hace
 * internamente MapStruct.
 */
public interface IUsuarioEntityMapper {

    /**
     * Convierte un modelo de dominio → entidad JPA (para guardar en BD).
     */
    UsuarioEntity toEntity(Usuario usuario);

    /**
     * Convierte una entidad JPA → modelo de dominio (para usar en lógica de negocio).
     */
    Usuario toUsuario(UsuarioEntity usuarioEntity);
}
