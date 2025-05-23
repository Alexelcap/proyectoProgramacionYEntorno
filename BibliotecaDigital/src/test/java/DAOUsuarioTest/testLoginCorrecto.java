/*
 * 
 */
package DAOUsuarioTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import ConexionDAO.DAOUsuarios;
import modelo.Usuario;

public class testLoginCorrecto {

    private DAOUsuarios dao;

    @BeforeEach
    @DisplayName("Inicializar DAOUsuarios antes de cada test")
    void setUp() {
        dao = new DAOUsuarios();
    }

    @Test
    @DisplayName("Login con contraseña y usuario validos debe devolver usuario")
    void testLoginCorrecto() {
        Usuario user = dao.buscarPorLogin("admin@biblioteca.com", "admin");
        assertNotNull(user, "Debe devolver un usuario valido");
        assertEquals("admin", user.getRol(), "El rol debe ser admin");
    }

    @Test
    @DisplayName("Login con usuario falso y contraseña falsa y debe devolver un error")
    void testLoginIncorrecto() {
        Usuario user = dao.buscarPorLogin("falso@correo.com", "contraseñaIncorrecta");
        assertNull(user, "Debe devolver null si el login falla");
    }
}
