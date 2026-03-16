package ms_usuario.plazoleta.ms_usuario.infrastructure.input.rest.mapper;

import ms_usuario.plazoleta.ms_usuario.domain.model.Usuario;
import ms_usuario.plazoleta.ms_usuario.infrastructure.input.rest.dto.UsuarioRequestDto;
import org.springframework.stereotype.Component;

/**
 * Mapper que convierte el DTO de entrada al modelo de dominio.
 *
 * ¿Por qué necesitamos este mapper adicional?
 * El controlador recibe un UsuarioRequestDto (con datos del HTTP).
 * El caso de uso espera un Usuario (modelo de dominio).
 * Este mapper hace esa traducción.
 *
 * Nota: el id se pasa como null porque aún no fue persistido
 * (la BD lo generará automáticamente). El rol tampoco se mapea
 * desde el DTO — el caso de uso lo asigna según la lógica de negocio.
 */
@Component
public class IUsuarioRequestMapper {

    public Usuario toDomain(UsuarioRequestDto dto) {
        return new Usuario(
                null,                          // id: la BD lo genera
                dto.getNombre(),
                dto.getApellido(),
                dto.getDocumentoDeIdentidad(),
                dto.getCelular(),
                dto.getFechaNacimiento(),
                dto.getCorreo(),
                dto.getClave(),
                null                           // rol: el caso de uso lo asigna
        );
    }
}
