package modelo;

/**
 *
 * @author south
 */

public class Usuario {
    private int idUsuario; 
    private String nombre;
    private String correo;
    private String contraseña;
    private String rol;

    public Usuario() {
        this.rol = "usuario";
    }
    
    public Usuario(int idUsuario, String nombre, String correo, String contraseña, String rol) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.correo = correo;
        this.contraseña = contraseña;
        this.rol = rol;
    }

    public Usuario(String login, String password) {
        this.nombre = login;
        this.contraseña = password;
    }

    // Getters y Setters
    public String getLogin() {
        return contraseña;
    }

    public String getPassword() {
        return contraseña;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public String getRol() {
        return rol;
    }

    public void setLogin(String login) {
        this.nombre = login;
    }

    public void setPassword(String password) {
        this.contraseña = password;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    @Override
    public String toString() {
        return nombre + " (" + correo + ") - Rol: " + rol;
    }
}