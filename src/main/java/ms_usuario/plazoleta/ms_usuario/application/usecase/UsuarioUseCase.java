package ms_usuario.plazoleta.ms_usuario.application.usecase;

import ms_usuario.plazoleta.ms_usuario.domain.constants.UsuarioConstantes;
import ms_usuario.plazoleta.ms_usuario.domain.exception.UsuarioYaExisteException;
import ms_usuario.plazoleta.ms_usuario.domain.model.Rol;
import ms_usuario.plazoleta.ms_usuario.domain.model.Usuario;
import ms_usuario.plazoleta.ms_usuario.domain.ports.in.IUsuarioServicePort;
import ms_usuario.plazoleta.ms_usuario.domain.ports.out.IClaveCodificadorPort;
import ms_usuario.plazoleta.ms_usuario.domain.ports.out.IUsuarioPersistencePort;
import ms_usuario.plazoleta.ms_usuario.domain.util.EdadUtil;

/**
 * Caso de uso: Crear Propietario.
 *
 * Esta clase es el CORAZÓN de la lógica de negocio para esta HU.
 * Aquí sucede la orquestación:
 *   1. Verificar que el usuario sea mayor de edad
 *   2. Verificar que el correo no esté registrado
 *   3. Encriptar la clave
 *   4. Asignar el rol PROPIETARIO
 *   5. Persistir
 *
 * ¿Por qué NO tiene @Service de Spring?
 * Para mantener la capa de aplicación lo más limpia posible. @Service es una
 * anotación de Spring — si la ponemos aquí, acoplamos el caso de uso al
 * framework. La registraremos como bean en una clase de configuración
 * en infraestructura (BeanConfiguration).
 *
 * ¿Por qué recibe PasswordEncoder si es de Spring Security?
 * PasswordEncoder es una INTERFAZ, no una implementación concreta.
 * El dominio/aplicación solo conoce el contrato "puedo encriptar una clave",
 * no sabe que por debajo usa BCrypt. Esto respeta la inversión de dependencias.
 */
public class UsuarioUseCase implements IUsuarioServicePort {

    // Dependencias inyectadas por constructor (no por @Autowired)
    // ¿Por qué constructor? Porque hace las dependencias explícitas y
    // facilita enormemente los tests unitarios (puedes pasar mocks directamente).
    private final IUsuarioPersistencePort usuarioPersistencePort;
    private final IClaveCodificadorPort claveCodificadorPort;

    public UsuarioUseCase(IUsuarioPersistencePort usuarioPersistencePort,
                          IClaveCodificadorPort claveCodificadorPort) {
        this.usuarioPersistencePort = usuarioPersistencePort;
        this.claveCodificadorPort = claveCodificadorPort;
    }

    @Override
    public void guardarPropietario(Usuario usuario) {

        // Regla 1: El usuario debe ser mayor de edad
        EdadUtil.validarMayoriaDeEdad(usuario.getFechaNacimiento(), UsuarioConstantes.EDAD_MINIMA_PROPIETARIO);

        // Regla 2: No puede existir otro usuario con el mismo correo
        usuarioPersistencePort.buscarPorCorreo(usuario.getCorreo())
                .ifPresent(usuarioEncontrado -> { throw new UsuarioYaExisteException(); });

        // Regla 3: Encriptar la clave ANTES de persistir
        // Nunca se guarda una clave en texto plano en la BD
        usuario.setClave(claveCodificadorPort.codificar(usuario.getClave()));

        // Regla 4: El rol siempre es PROPIETARIO, sin importar lo que venga del request
        // Esto es importante: no dejamos que el cliente HTTP decida el rol
        usuario.setRol(Rol.PROPIETARIO);

        // Regla 5: Persistir
        usuarioPersistencePort.guardarUsuario(usuario);
    }
}
