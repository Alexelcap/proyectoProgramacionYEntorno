/*
 * 
 */
package ConexionDAO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import modelo.Prestamo;

/**
 *
 * @author south
 */
public class DAOPrestamo {

    public boolean guardarPrestamo(Connection conn, Prestamo prestamo) throws SQLException {
        String sql = "insert into Prestamo (id_prestamo, id_usuario, id_libro, fecha_inicio, fecha_fin) "
                + "values ((select nvl(max(id_prestamo),0)+1 from prestamo), ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, prestamo.getUsuario().getIdUsuario());
            ps.setInt(2, prestamo.getLibro().getIdLibro());
            ps.setDate(3, Date.valueOf(prestamo.getFechaInicio()));
            ps.setDate(4, Date.valueOf(prestamo.getFechaFin()));

            return ps.executeUpdate() > 0;
        }
    }

    public boolean usuarioTienePrestamoActivo(int idUsuario) {
        Connection conn = null;
        boolean tienePrestamo = false;
        try {
            conn = ConexionBD.conectarBD();
            PreparedStatement pst = conn.prepareStatement(
                    "select count(*) from prestamo where id_usuario = ? and fecha_fin is NULL"
            );
            pst.setInt(1, idUsuario);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                tienePrestamo = rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar préstamo activo: " + e.getMessage());
        } finally {
            ConexionBD.desconectarBD(conn);
        }
        return tienePrestamo;
    }
}
