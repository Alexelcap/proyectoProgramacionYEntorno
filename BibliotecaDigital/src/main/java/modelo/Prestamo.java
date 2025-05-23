/**
 *
 */
package modelo;

/**
 *
 * @author south
 */

import java.time.LocalDate;

public class Prestamo {

    private int idPrestamo;
    private Usuario usuario;
    private Libro libro;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    public Prestamo() {
        this.fechaInicio = LocalDate.now();
    }

    public Prestamo(Usuario usuario, Libro libro, LocalDate fechaFin) {
        this();
        this.usuario = usuario;
        this.libro = libro;
        this.fechaFin = fechaFin;
    }

    public Prestamo(Usuario usuarioActual, Libro libro, LocalDate now, LocalDate plusDays) {
        this();
        this.usuario = usuarioActual;
        this.libro = libro;
        this.fechaInicio = now;
        this.fechaFin = plusDays;
    }

    // Getters y Setters
    public int getIdPrestamo() {
        return idPrestamo;
    }

    public void setIdPrestamo(int idPrestamo) {
        this.idPrestamo = idPrestamo;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Libro getLibro() {
        return libro;
    }

    public void setLibro(Libro libro) {
        this.libro = libro;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    @Override
    public String toString() {
        return libro.getTitulo() + " prestado a " + usuario.getNombre()
                + " hasta " + fechaFin.toString();
    }
}
