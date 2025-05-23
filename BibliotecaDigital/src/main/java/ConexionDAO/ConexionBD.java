/*Clase conexion a la base de datos biblioteca*/

package ConexionDAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author south
 */
public class ConexionBD {
    public static Connection conectarBD() throws SQLException {
        
        String url = "jdbc:oracle:thin:@localhost:1521:xe";
        String usuario = "Programacion";
        String contrasena = "Programacion";
        
        return DriverManager.getConnection(url, usuario, contrasena);
    }

    public static void desconectarBD(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error al desconectar BD: " + e.getMessage());
            }
        }
    }
}
