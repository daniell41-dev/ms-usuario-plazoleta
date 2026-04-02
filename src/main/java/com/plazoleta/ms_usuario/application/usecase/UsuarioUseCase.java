package com.plazoleta.ms_usuario.application.usecase;

import com.plazoleta.ms_usuario.domain.exception.CredencialesInvalidasException;
import com.plazoleta.ms_usuario.domain.exception.MenorDeEdadException;
import com.plazoleta.ms_usuario.domain.exception.UsuarioNoEncontradoException;
import com.plazoleta.ms_usuario.domain.exception.UsuarioYaExisteException;
import com.plazoleta.ms_usuario.domain.constants.UsuarioConstantes;
import com.plazoleta.ms_usuario.domain.model.Rol;
import com.plazoleta.ms_usuario.domain.model.Usuario;
import com.plazoleta.ms_usuario.domain.ports.in.IUsuarioServicePort;
import com.plazoleta.ms_usuario.domain.ports.out.IJwtTokenPort;
import com.plazoleta.ms_usuario.domain.ports.out.IUsuarioPersistencePort;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.plazoleta.ms_usuario.domain.constants.UsuarioConstantes;

import java.time.LocalDate;
import java.time.Period;

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
    private final PasswordEncoder passwordEncoder;
    private final IJwtTokenPort jwtTokenPort;

    public UsuarioUseCase(IUsuarioPersistencePort usuarioPersistencePort,
                          PasswordEncoder passwordEncoder,
                          IJwtTokenPort jwtTokenPort) {
        this.usuarioPersistencePort = usuarioPersistencePort;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenPort = jwtTokenPort;
    }

    @Override
    public void guardarCliente(Usuario usuario) {

        // Regla 1: No puede existir otro usuario con el mismo correo
        usuarioPersistencePort.buscarPorCorreo(usuario.getCorreo())
                .ifPresent(u -> { throw new UsuarioYaExisteException("Ya existe un usuario registrado con ese correo electrónico"); });

        // Regla 2: No puede existir otro usuario con el mismo documento de identidad
        if (usuarioPersistencePort.existePorDocumento(usuario.getDocumentoDeIdentidad())) {
            throw new UsuarioYaExisteException("Ya existe un usuario registrado con ese documento de identidad");
        }

        // Regla 3: Encriptar la clave ANTES de persistir
        usuario.setClave(passwordEncoder.encode(usuario.getClave()));

        // Regla 4: El rol siempre es CLIENTE, sin importar el idRol que llegó en el request
        usuario.setRol(Rol.CLIENTE);

        // Regla 5: Persistir
        usuarioPersistencePort.guardarUsuario(usuario);
    }

    @Override
    public void guardarEmpleado(Usuario usuario) {

        // Regla 1: No puede existir otro usuario con el mismo correo
        usuarioPersistencePort.buscarPorCorreo(usuario.getCorreo())
                .ifPresent(u -> { throw new UsuarioYaExisteException("Ya existe un usuario registrado con ese correo electrónico"); });

        // Regla 2: No puede existir otro usuario con el mismo documento de identidad
        if (usuarioPersistencePort.existePorDocumento(usuario.getDocumentoDeIdentidad())) {
            throw new UsuarioYaExisteException("Ya existe un usuario registrado con ese documento de identidad");
        }

        // Regla 3: Encriptar la clave ANTES de persistir
        usuario.setClave(passwordEncoder.encode(usuario.getClave()));

        // Regla 4: El rol siempre es EMPLEADO, sin importar el idRol que llegó en el request
        usuario.setRol(Rol.EMPLEADO);

        // Regla 5: Persistir
        usuarioPersistencePort.guardarUsuario(usuario);
    }

    @Override
    public void guardarPropietario(Usuario usuario) {

        // Regla 1: El usuario debe ser mayor de edad
        validarMayoriaDeEdad(usuario.getFechaNacimiento());

        // Regla 2: No puede existir otro usuario con el mismo correo
        usuarioPersistencePort.buscarPorCorreo(usuario.getCorreo())
                .ifPresent(u -> { throw new UsuarioYaExisteException("Ya existe un usuario registrado con ese correo electrónico"); });

        // Regla 3: No puede existir otro usuario con el mismo documento de identidad
        if (usuarioPersistencePort.existePorDocumento(usuario.getDocumentoDeIdentidad())) {
            throw new UsuarioYaExisteException("Ya existe un usuario registrado con ese documento de identidad");
        }

        // Regla 4: Encriptar la clave ANTES de persistir
        usuario.setClave(passwordEncoder.encode(usuario.getClave()));

        // Regla 5: El rol siempre es PROPIETARIO, sin importar lo que venga del request
        usuario.setRol(Rol.PROPIETARIO);

        // Regla 6: Persistir
        usuarioPersistencePort.guardarUsuario(usuario);
    }

    /**
     * Valida que el usuario tenga al menos 18 años.
     * Usamos Period.between para calcular la diferencia exacta entre fechas,
     * considerando años bisiestos y meses con distinta cantidad de días.
     */
    private void validarMayoriaDeEdad(LocalDate fechaNacimiento) {
        int edad = Period.between(fechaNacimiento, LocalDate.now()).getYears();
        if (edad < UsuarioConstantes.EDAD_MINIMA) {
            throw new MenorDeEdadException();
        }
    }

    @Override
    public String obtenerRolUsuario(Long id) {
        Usuario usuario = usuarioPersistencePort.obtenerPorId(id).orElseThrow(() ->
                new UsuarioNoEncontradoException("No se encontro el usuario con este id"));

        return usuario.getRol().name();
    }

    @Override
    public String login(String correo, String clave) {
        Usuario usuario = usuarioPersistencePort.buscarPorCorreo(correo)
                .orElseThrow(CredencialesInvalidasException::new);

        if (!passwordEncoder.matches(clave, usuario.getClave())) {
            throw new CredencialesInvalidasException();
        }

        return jwtTokenPort.generarToken(usuario.getId(), usuario.getCorreo(), usuario.getRol().name());
    }
}
