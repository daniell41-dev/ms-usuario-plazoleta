package com.plazoleta.ms_usuario.infrastructure.output.persistence.mapper;

import com.plazoleta.ms_usuario.domain.model.Usuario;
import com.plazoleta.ms_usuario.infrastructure.output.persistence.entity.UsuarioEntity;
import org.springframework.stereotype.Component;

/**
 * Implementación concreta del mapper de entidades.
 *
 * @Component → registra esta clase como un bean de Spring para que pueda
 * ser inyectada donde se necesite (en UsuarioJpaAdapter).
 *
 * La conversión es campo a campo: simplemente copiamos los valores de un
 * objeto al otro. No hay lógica de negocio aquí — solo traducción.
 */
@Component
public class UsuarioEntityMapper implements IUsuarioEntityMapper {

    @Override
    public UsuarioEntity toEntity(Usuario usuario) {
        return new UsuarioEntity(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getDocumentoDeIdentidad(),
                usuario.getCelular(),
                usuario.getFechaNacimiento(),
                usuario.getCorreo(),
                usuario.getClave(),
                usuario.getRol()
        );
    }

    @Override
    public Usuario toUsuario(UsuarioEntity entity) {
        return new Usuario(
                entity.getId(),
                entity.getNombre(),
                entity.getApellido(),
                entity.getDocumentoDeIdentidad(),
                entity.getCelular(),
                entity.getFechaNacimiento(),
                entity.getCorreo(),
                entity.getClave(),
                entity.getRol()
        );
    }
}
