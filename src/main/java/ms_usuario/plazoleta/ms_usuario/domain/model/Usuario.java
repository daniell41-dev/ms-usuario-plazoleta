package ms_usuario.plazoleta.ms_usuario.domain.model;

import java.time.LocalDate;

/**
 * Modelo de dominio que representa a un Usuario en el sistema Plazoleta.
 *
 * REGLA DE ORO: Esta clase NO tiene anotaciones de Spring (@Component, @Service),
 * ni de JPA (@Entity, @Column), ni de Lombok (@Data). Es Java puro.
 *
 * ¿Por qué? Porque el dominio debe poder existir y ser testeado sin ningún
 * framework. Si mañana migramos de Spring a Quarkus, esta clase no cambia.
 *
 * ¿Por qué no usamos Lombok aquí?
 * Lombok genera código en tiempo de compilación (getters, setters, constructores).
 * En el dominio preferimos ser explícitos: ver el constructor completo nos ayuda
 * a entender qué campos son obligatorios para crear un Usuario válido.
 */
public class Usuario {

    private Long id;
    private String nombre;
    private String apellido;
    private String documentoDeIdentidad;
    private String celular;
    private LocalDate fechaNacimiento;
    private String correo;
    private String clave;
    private Rol rol;

    // Constructor completo: para crear un Usuario necesitas TODOS sus datos.
    // No existe un "Usuario a medias" — eso garantiza que el objeto siempre
    // sea válido desde el momento en que se crea.
    public Usuario(Long id, String nombre, String apellido, String documentoDeIdentidad,
                   String celular, LocalDate fechaNacimiento, String correo,
                   String clave, Rol rol) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.documentoDeIdentidad = documentoDeIdentidad;
        this.celular = celular;
        this.fechaNacimiento = fechaNacimiento;
        this.correo = correo;
        this.clave = clave;
        this.rol = rol;
    }

    // Getters: permiten leer los valores del objeto
    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getDocumentoDeIdentidad() { return documentoDeIdentidad; }
    public String getCelular() { return celular; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public String getCorreo() { return correo; }
    public String getClave() { return clave; }
    public Rol getRol() { return rol; }

    // Setters: solo para los campos que pueden cambiar después de la creación
    public void setId(Long id) { this.id = id; }
    public void setClave(String clave) { this.clave = clave; }
    public void setRol(Rol rol) { this.rol = rol; }
}
