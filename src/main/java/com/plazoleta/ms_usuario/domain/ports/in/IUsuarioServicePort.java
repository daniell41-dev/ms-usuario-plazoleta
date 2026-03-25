package com.plazoleta.ms_usuario.domain.ports.in;

import com.plazoleta.ms_usuario.domain.model.Usuario;

/**
 * Puerto de ENTRADA (Driving Port / Input Port).
 *
 * Define el CONTRATO de lo que el sistema puede hacer con los usuarios.
 * Es la "puerta de entrada" al dominio — quien quiera usar la lógica de
 * negocio (un controlador REST, un test, un CLI) debe hacerlo a través
 * de esta interfaz.
 *
 * ¿Por qué es una interfaz y no una clase?
 * Porque el controlador REST no debe saber si la lógica está en UsuarioUseCase,
 * en un mock para tests, o en cualquier otra implementación. Solo le importa
 * el contrato: "dame un método guardarPropietario que reciba un Usuario".
 *
 * Convención de nombres: la "I" al inicio indica que es una Interfaz.
 * Esto es una convención del equipo, no de Java — ayuda a distinguir
 * interfaces de clases concretas de un solo vistazo.
 */
public interface IUsuarioServicePort {

    /**
     * Crea un nuevo usuario con rol PROPIETARIO en el sistema.
     *
     * @param usuario El objeto Usuario con los datos a registrar.
     *                La clave llega en texto plano — el caso de uso
     *                se encarga de encriptarla antes de persistir.
     */
    void guardarPropietario(Usuario usuario);

    void guardarEmpleado(Usuario usuario);

    void guardarCliente(Usuario usuario);

    String obtenerRolUsuario(Long id);

    String login(String correo, String clave);

}
