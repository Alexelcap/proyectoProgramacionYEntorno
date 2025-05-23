package ConexionDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Libro;
import modelo.Autor;

/**
 *
 * @author south
 */
public class DAOLibro {

    public List<Libro> listarLibros() throws SQLException {
        List<Libro> libros = new ArrayList<>();
        String sql = "select id_libro, titulo, isbn, id_autor from libro order by id_libro asc";

        try (Connection conn = ConexionBD.conectarBD(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Libro libro = new Libro();
                libro.setIdLibro(rs.getInt("id_libro"));
                libro.setTitulo(rs.getString("titulo"));
                libro.setIsbn(rs.getString("isbn"));
                libro.getAutor().setIdAutor(rs.getInt("id_autor"));
                libros.add(libro);
            }
        }
        return libros;

    }

    public void insertarLibro(Libro libro) throws SQLException {
        String sql = "insert into libro (id_libro, titulo, isbn, categoria, estado, id_autor) "
                + "values ((select nvl(max(id_libro), 0) + 1 from libro), ?, ?, ?, 'Disponible', ?)";

        try (Connection conn = ConexionBD.conectarBD(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, libro.getTitulo());
            pstmt.setString(2, libro.getIsbn());
            pstmt.setString(3, libro.getCategoria());
            pstmt.setInt(4, libro.getAutor().getIdAutor());
            pstmt.executeUpdate();
        }
    }

    public int eliminarLibroPorISBN(String isbn) throws SQLException {
        String sql = "delete from libro where isbn = ?";

        try (Connection conn = ConexionBD.conectarBD(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, isbn);
            return pstmt.executeUpdate();
        }
    }

    public List<Libro> buscarLibros(String libroEncontrado) throws SQLException {
        List<Libro> libros = new ArrayList<>();
        String sql = "select l.id_libro, l.titulo, l.isbn, l.categoria, l.estado, "
                + "a.id_autor, a.nombre as autor_nombre, a.nacionalidad "
                + "from Libro l join Autor a on l.id_autor = a.id_autor "
                + "where upper(l.titulo) like upper(?)";

        try (Connection conn = ConexionBD.conectarBD(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + libroEncontrado + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Libro libro = new Libro();
                    libro.setIdLibro(rs.getInt("id_libro"));
                    libro.setTitulo(rs.getString("titulo"));
                    libro.setIsbn(rs.getString("isbn"));
                    libro.setCategoria(rs.getString("categoria"));
                    libro.setEstado(rs.getString("estado"));

                    Autor autor = new Autor();
                    autor.setIdAutor(rs.getInt("id_autor"));
                    autor.setNombre(rs.getString("autor_nombre"));
                    autor.setNacionalidad(rs.getString("nacionalidad"));

                    libro.setAutor(autor);
                    libros.add(libro);
                }
            }
        }
        return libros;
    }


    public void actualizarEstadoLibro(int idLibro, String estado) throws SQLException {
        String sql = "update Libro set estado = ? where id_libro = ?";

        try (Connection conn = ConexionBD.conectarBD(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, estado);
            pstmt.setInt(2, idLibro);
            pstmt.executeUpdate();
        }
    }

    public List<Libro> listarLibrosConAutores() throws SQLException {
        List<Libro> libros = new ArrayList<>();

        String sql = "select Libro.id_libro, Libro.titulo, Libro.isbn, Libro.estado, Libro.categoria, "
                + "Autor.id_autor, Autor.nombre, Autor.nacionalidad "
                + "from Libro join Autor on Libro.id_autor = Autor.id_autor";

        try (Connection conn = ConexionBD.conectarBD(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Libro libro = new Libro();
                libro.setIdLibro(rs.getInt("id_libro"));
                libro.setTitulo(rs.getString("titulo"));
                libro.setIsbn(rs.getString("isbn"));
                libro.setEstado(rs.getString("estado"));
                libro.setCategoria(rs.getString("categoria"));

                Autor autor = new Autor();
                autor.setIdAutor(rs.getInt("id_autor"));
                autor.setNombre(rs.getString("nombre"));
                autor.setNacionalidad(rs.getString("nacionalidad"));

                libro.setAutor(autor);

                libros.add(libro);
            }
        }

        return libros;
    }

    public List<String> buscarLibrosPorTitulo(String libro) throws SQLException {
        List<String> librosInfo = new ArrayList<>();
        String sql = "select Libro.titulo, Autor.nombre as autor, Libro.estado "
                + "from Libro join Autor on Libro.id_autor = Autor.id_autor "
                + "where upper(Libro.titulo) like upper(?)";

        try (Connection conn = ConexionBD.conectarBD(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + libro + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String libroInfo = String.format("%s - %s (%s)",
                            rs.getString("titulo"),
                            rs.getString("autor"),
                            rs.getString("estado"));
                    librosInfo.add(libroInfo);
                }
            }
        }
        return librosInfo;
    }

    public boolean tienePrestamoActivo(int idUsuario) throws SQLException {
        String sql = "select count(*) from Prestamo where id_usuario = ? and fecha_devolucion is null";

        try (Connection conn = ConexionBD.conectarBD(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idUsuario);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public int obtenerIdLibroPorTitulo(String titulo) throws SQLException {
        String sql = "select id_libro from Libro where titulo = ?";

        try (Connection conn = ConexionBD.conectarBD(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, titulo);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_libro");
                }
            }
        }
        return -1;
    }

}
