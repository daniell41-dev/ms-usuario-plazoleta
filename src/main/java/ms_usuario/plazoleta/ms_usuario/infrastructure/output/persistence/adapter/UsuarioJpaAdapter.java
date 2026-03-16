package ms_usuario.plazoleta.ms_usuario.infrastructure.output.persistence.adapter;

import lombok.RequiredArgsConstructor;
import ms_usuario.plazoleta.ms_usuario.domain.model.Usuario;
import ms_usuario.plazoleta.ms_usuario.domain.ports.out.IUsuarioPersistencePort;
import ms_usuario.plazoleta.ms_usuario.infrastructure.output.persistence.mapper.IUsuarioEntityMapper;
import ms_usuario.plazoleta.ms_usuario.infrastructure.output.persistence.repository.IUsuarioRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Adaptador de persistencia: implementa IUsuarioPersistencePort usando JPA.
 *
 * Este es el puente entre el dominio y la base de datos.
 * El dominio solo conoce la interfaz IUsuarioPersistencePort.
 * Este adaptador es quien sabe cómo traducir esa interfaz a operaciones JPA reales.
 *
 * Flujo de guardarUsuario:
 *   dominio entrega Usuario (modelo puro)
 *       → mapper convierte a UsuarioEntity (con anotaciones JPA)
 *           → repository.save() ejecuta el INSERT en PostgreSQL
 *
 * Flujo de buscarPorCorreo:
 *   repository.findByCorreo() ejecuta SELECT en PostgreSQL
 *       → devuelve Optional<UsuarioEntity>
 *           → mapper convierte cada UsuarioEntity a Usuario (modelo puro)
 *               → dominio recibe Optional<Usuario>
 *
 * @RequiredArgsConstructor (Lombok) genera el constructor con todos los campos
 * final, equivalente a escribir manualmente el constructor con ambos parámetros.
 * Spring usa ese constructor para inyectar las dependencias automáticamente.
 */
@Component
@RequiredArgsConstructor
public class UsuarioJpaAdapter implements IUsuarioPersistencePort {

    private final IUsuarioRepository usuarioRepository;
    private final IUsuarioEntityMapper usuarioEntityMapper;

    @Override
    public void guardarUsuario(Usuario usuario) {
        // 1. Convierte el modelo de dominio a entidad JPA
        // 2. JPA ejecuta INSERT INTO usuarios (...) VALUES (...)
        usuarioRepository.save(usuarioEntityMapper.toEntity(usuario));
    }

    @Override
    public Optional<Usuario> buscarPorCorreo(String correo) {
        // findByCorreo devuelve Optional<UsuarioEntity>
        // .map() transforma el contenido del Optional SIN sacarlo:
        //   si hay un UsuarioEntity → lo convierte a Usuario
        //   si está vacío → sigue siendo Optional.empty()
        return usuarioRepository.findByCorreo(correo)
                .map(usuarioEntityMapper::toUsuario);
    }
}
