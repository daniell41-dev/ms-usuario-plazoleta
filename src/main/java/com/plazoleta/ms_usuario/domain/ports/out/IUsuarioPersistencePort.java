package com.plazoleta.ms_usuario.domain.ports.out;

import com.plazoleta.ms_usuario.domain.model.Usuario;

import java.util.Optional;

/**
 * Puerto de SALIDA (Driven Port / Output Port).
 *
 * Define el CONTRATO de lo que el dominio necesita del mundo exterior
 * para persistir y recuperar usuarios. El dominio dice: "necesito poder
 * guardar un usuario y buscar uno por correo" — pero NO sabe si eso
 * se hace con PostgreSQL, MySQL, un archivo JSON o en memoria.
 *
 * ¿Por qué Optional<Usuario> en buscarPorCorreo?
 * Optional es la forma correcta en Java de expresar "esto puede no existir".
 * Es mejor que devolver null, porque obliga a quien recibe el resultado
 * a manejar explícitamente el caso de "no encontrado" — evita NullPointerException.
 *
 * Esta interfaz vive en el DOMINIO pero su implementación (UsuarioJpaAdapter)
 * vive en INFRAESTRUCTURA. Así el dominio no depende de JPA.
 */
public interface IUsuarioPersistencePort {

    /**
     * Persiste un usuario en el almacenamiento de datos.
     * @param usuario El objeto Usuario ya validado y con la clave encriptada.
     */
    void guardarUsuario(Usuario usuario);

    /**
     * Busca un usuario por su correo electrónico.
     * Se usa para verificar si ya existe antes de crear uno nuevo.
     * @param correo El correo a buscar.
     * @return Optional con el Usuario si existe, Optional.empty() si no.
     */
    Optional<Usuario> buscarPorCorreo(String correo);

    /**
     * Obtiene el id del usuario en cuestion
     * @param id
     * @return Optional con el id del Usuario si existe
     */
    Optional<Usuario> obtenerPorId(Long id);

    /**
     * Verifica si ya existe un usuario con el documento de identidad dado.
     * Se usa para evitar duplicados antes de crear un nuevo usuario.
     * @param documentoDeIdentidad El documento a verificar.
     * @return true si ya existe, false si no.
     */
    boolean existePorDocumento(String documentoDeIdentidad);
}
