package modelo;

/**
 *
 * @author south
 */

public class Libro {

    private int idLibro;
    private String titulo;
    private String isbn;
    private String categoria;
    private String estado;
    private Autor autor;

    public Libro() {
        this.estado = "disponible";
        this.autor = new Autor();
    }

    public Libro(int idLibro, String titulo, String estado) {
        this.idLibro = idLibro;
        this.titulo = titulo;
        this.estado = estado;
    }

    // Getters y Setters
    public int getIdLibro() {
        return idLibro;
    }

    public void setIdLibro(int idLibro) {
        this.idLibro = idLibro;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Autor getAutor() {
        return autor;
    }

    public void setAutor(Autor autor) {
        this.autor = autor;
    }

    @Override
    public String toString() {
        return titulo + " - " + autor.getNombre() + " [" + estado + "]";
    }
}
