package ConexionDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Autor;

/**
 *
 * @author south
 */
public class DAOAutor {
    
    public List<Autor> listarAutores() throws SQLException {
        List<Autor> autores = new ArrayList<>();
        String sql = "select id_autor, nombre, nacionalidad from autor order by id_autor asc";
        
        try (Connection conn = ConexionBD.conectarBD();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Autor autor = new Autor();
                autor.setIdAutor(rs.getInt("id_autor"));
                autor.setNombre(rs.getString("nombre"));
                autor.setNacionalidad(rs.getString("nacionalidad"));
                autores.add(autor);
            }
        }
        return autores;
    }
    
    public void insertarAutor(Autor autor) throws SQLException {
        String sql = "insert into autor (id_autor, nombre, nacionalidad) values (?, ?, ?)";
        
        try (Connection conn = ConexionBD.conectarBD();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, autor.getIdAutor());
            pstmt.setString(2, autor.getNombre());
            pstmt.setString(3, autor.getNacionalidad());
            pstmt.executeUpdate();
        }
    }
    
    public Autor buscarAutorPorId(int idAutor) throws SQLException {
        String sql = "select nombre, nacionalidad from autor where id_autor = ?";
        Autor autor = null;
        
        try (Connection conn = ConexionBD.conectarBD();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idAutor);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    autor = new Autor();
                    autor.setIdAutor(idAutor);
                    autor.setNombre(rs.getString("nombre"));
                    autor.setNacionalidad(rs.getString("nacionalidad"));
                }
            }
        }
        return autor;
    }
    
    public boolean actualizarAutor(Autor autor) throws SQLException {
        String sql = "update autor set nombre = ?, nacionalidad = ? where id_autor = ?";
        
        try (Connection conn = ConexionBD.conectarBD();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, autor.getNombre());
            pstmt.setString(2, autor.getNacionalidad());
            pstmt.setInt(3, autor.getIdAutor());
            
            return pstmt.executeUpdate() > 0;
        }
    }
}