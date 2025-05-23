package ConexionDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import modelo.Usuario;

/**
 *
 * @author south
 */
public class DAOUsuarios {

    public Usuario buscarPorLogin(String login, String contraseña) {
        Usuario u = null;
        Connection conn = null;
        try {
            conn = ConexionBD.conectarBD();
            PreparedStatement pst = conn.prepareStatement(
                    "select id_usuario, contraseña, nombre, correo, rol from usuario where correo = ? and contraseña = ?");
            pst.setString(1, login);
            pst.setString(2, contraseña);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                u = new Usuario(
                        rs.getInt("id_usuario"),
                        rs.getString("nombre"),
                        rs.getString("correo"),
                        rs.getString("contraseña"),
                        rs.getString("rol")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error en buscarPorLogin: " + e.getMessage());
        } finally {
            ConexionBD.desconectarBD(conn);
        }
        return u;
    }

    public boolean insertarUsuario(Usuario u) {
        Connection conn = null;
        boolean exito = false;
        try {
            conn = ConexionBD.conectarBD();

            if (existeUsuario(conn, u.getNombre())) {
                return false;
            }

            PreparedStatement pst = conn.prepareStatement(
                    "insert into Usuario (id_usuario, nombre, correo, contraseña, rol) "
                    + "values(?, ?, ?, ?, ?)");

            int nextId = obtenerProximoId(conn);
            pst.setInt(1, nextId);
            pst.setString(2, u.getNombre());
            pst.setString(3, u.getCorreo());
            pst.setString(4, u.getPassword());
            pst.setString(5, u.getRol() != null ? u.getRol() : "usuario");

            exito = pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en insertarUsuario: " + e.getMessage());
        } finally {
            ConexionBD.desconectarBD(conn);
        }
        return exito;
    }

    private int obtenerProximoId(Connection conn) throws SQLException {
        try (PreparedStatement pst = conn.prepareStatement(
                "select nvl(max(id_usuario), 0) + 1 from usuario"); ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 1; // Si no hay registros, empezar con 1
        }
    }

    private boolean existeUsuario(Connection conn, String nombreUsuario) throws SQLException {
        PreparedStatement pst = conn.prepareStatement(
                "select count(*) from usuario where nombre = ?");
        pst.setString(1, nombreUsuario);

        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) > 0;
        }
        return false;
    }

}
