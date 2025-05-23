/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package DAOLibroTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import ConexionDAO.DAOLibro;
import java.sql.SQLException;
import modelo.Libro;
import java.util.List;

public class DAOLibroTest {

    private DAOLibro dao;

    @BeforeEach
    @DisplayName("Inicializar DAOLibro antes de cada test")
    void setUp() {
        dao = new DAOLibro();
    }

    @Test
    @DisplayName("Listar catálogo de libros con autores no debe ser nulo ni vacío")
    void testListarCatalogo() throws SQLException {
        List<Libro> catalogo = dao.listarLibrosConAutores();
        assertNotNull(catalogo, "La lista de libros no debe ser nula");
        assertFalse(catalogo.isEmpty(), "El catálogo debe contener libros");

        boolean existeLibro = catalogo.stream()
            .anyMatch(libro -> libro.getTitulo().equals("Cien Años de Soledad"));
        assertTrue(existeLibro, "Debe existir el libro 'Cien Años de Soledad' en el catálogo");
    }
}

