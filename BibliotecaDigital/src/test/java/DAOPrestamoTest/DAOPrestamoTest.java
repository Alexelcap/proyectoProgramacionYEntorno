package DAOPrestamoTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import ConexionDAO.DAOPrestamo;
import ConexionDAO.ConexionBD;
import modelo.Prestamo;
import modelo.Usuario;
import modelo.Libro;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

public class DAOPrestamoTest {

    private DAOPrestamo dao;
    private Connection conn;

    @BeforeEach
    @DisplayName("Conectar a la base de datos e inicializar DAOPrestamo antes de cada test")
    void setUp() throws SQLException {
        conn = ConexionBD.conectarBD();
        dao = new DAOPrestamo();
    }

    @Test
    @DisplayName("Prestar libro disponible debe ser exitoso")
    void testPrestarLibroDisponible() throws SQLException {
        Prestamo prestamo = new Prestamo();

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(2); // Usuario normal

        Libro libro = new Libro();
        libro.setIdLibro(1); // Libro disponible

        prestamo.setUsuario(usuario);
        prestamo.setLibro(libro);
        prestamo.setFechaInicio(LocalDate.now());
        prestamo.setFechaFin(LocalDate.now().plusDays(15));

        boolean resultado = dao.guardarPrestamo(conn, prestamo);
        assertTrue(resultado, "El préstamo debe realizarse si el libro está disponible");
    }

    @Test
    @DisplayName("No se debe prestar un libro que ya está prestado")
    void testPrestarLibroNoDisponible() throws SQLException {
        Prestamo prestamo = new Prestamo();

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(2);

        Libro libro = new Libro();
        libro.setIdLibro(2); // Libro ya prestado

        prestamo.setUsuario(usuario);
        prestamo.setLibro(libro);
        prestamo.setFechaInicio(LocalDate.now());
        prestamo.setFechaFin(LocalDate.now().plusDays(15));

        boolean resultado = dao.guardarPrestamo(conn, prestamo);
        assertFalse(resultado, "El préstamo no debe realizarse si el libro no está disponible");
    }
}
