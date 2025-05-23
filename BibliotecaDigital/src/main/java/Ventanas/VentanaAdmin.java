/*
 * Ventana admin que sirve para administrar los libros existentes
 */
package Ventanas;

import ConexionDAO.ConexionBD;
import ConexionDAO.DAOAutor;
import ConexionDAO.DAOLibro;
import Ficheros.Fichero;
import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import modelo.Autor;
import modelo.Libro;

/**
 *
 * @author south
 */
public class VentanaAdmin extends javax.swing.JFrame {

    private VentanaPrincipal ventanaPrincipal;

    /**
     * Creates new form VentanaAdmin
     */
    public VentanaAdmin() {
        initComponents();
        idYautores();
        librosDisponibles();
    }

    private void librosDisponibles() {
        try {
            DAOLibro daoLibro = new DAOLibro();
            StringBuilder texto = new StringBuilder();

            for (Libro libro : daoLibro.listarLibros()) {
                texto.append("ID: ").append(libro.getIdLibro())
                        .append(" - Titulo: ").append(libro.getTitulo())
                        .append(" - ISBN: ").append(libro.getIsbn())
                        .append(" - Id autor: ").append(libro.getAutor().getIdAutor())
                        .append("\n");
            }

            librosDisponiblesArea.setText(texto.toString());
        } catch (SQLException e) {
            librosDisponiblesArea.setText("Error en la base de datos: " + e.getMessage());
        }
    }

    private void idYautores() {
        try {
            DAOAutor daoAutor = new DAOAutor();
            StringBuilder texto = new StringBuilder();

            for (Autor autor : daoAutor.listarAutores()) {
                texto.append("ID: ").append(autor.getIdAutor())
                        .append(" - Nombre: ").append(autor.getNombre())
                        .append(" - Nacionalidad: ").append(autor.getNacionalidad())
                        .append("\n");
            }

            idsYAutroesArea.setText(texto.toString());
        } catch (SQLException e) {
            idsYAutroesArea.setText("Error en la base de datos: " + e.getMessage());
        }
    }

    private void agregarLibro() {
        String idAutorStr = campoIdAutor.getText().trim();
        String nombreAutor = campoNombreAutor.getText().trim();
        String nacionalidad = campoNacionalidad.getText().trim();
        String titulo = campoTitulo.getText().trim();
        String categoria = campoCategoria.getText().trim();
        String isbn = campoISBN.getText().trim();

        if (idAutorStr.isEmpty() || nombreAutor.isEmpty() || nacionalidad.isEmpty()
                || titulo.isEmpty() || categoria.isEmpty() || isbn.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, complete todos los campos.");
            return;
        }

        Connection conn = null;
        try {
            conn = ConexionBD.conectarBD();
            conn.setAutoCommit(false);

            int idAutor = Integer.parseInt(idAutorStr);
            DAOAutor daoAutor = new DAOAutor();
            DAOLibro daoLibro = new DAOLibro();

            // Verificar si el autor ya existe
            Autor autorExistente = daoAutor.buscarAutorPorId(idAutor);

            if (autorExistente == null) {
                // Autor no existe, crear nuevo
                Autor nuevoAutor = new Autor(nombreAutor, nacionalidad);
                nuevoAutor.setIdAutor(idAutor);
                daoAutor.insertarAutor(nuevoAutor);
            } else {
                // Autor existe, verificar si los datos coinciden
                if (!autorExistente.getNombre().equals(nombreAutor)
                        || !autorExistente.getNacionalidad().equals(nacionalidad)) {
                    JOptionPane.showMessageDialog(this,
                            "Los datos del autor no coinciden con los existentes. Use el botón Actualizar para modificarlos.");
                    return;
                }
            }

            // Crear el libro
            Libro libro = new Libro();
            libro.setTitulo(titulo);
            libro.setIsbn(isbn);
            libro.setCategoria(categoria);

            Autor autor = new Autor();
            autor.setIdAutor(idAutor);
            libro.setAutor(autor);

            daoLibro.insertarLibro(libro);
            conn.commit();

            Fichero.registrar("AGREGAR LIBRO",
                    String.format("Libro: %s, ISBN: %s, Autor: %s", titulo, isbn, nombreAutor));

            JOptionPane.showMessageDialog(this, "Libro agregado correctamente.");
            idYautores(); // Actualizar la lista de autores
            librosDisponibles();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El ID del autor debe ser un número válido.");
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Error al hacer rollback: " + ex.getMessage());
                }
            }
            JOptionPane.showMessageDialog(this, "Error al agregar el libro: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) {
                    System.err.println("Error al cerrar conexión: " + ex.getMessage());
                }
            }
        }
    }

    private void actualizarAutor() {
        String idAutorStr = campoIdAutor.getText().trim();
        String nombre = campoNombreAutor.getText().trim();
        String nacionalidad = campoNacionalidad.getText().trim();

        if (idAutorStr.isEmpty() || nombre.isEmpty() || nacionalidad.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos del autor son obligatorios");
            return;
        }

        try {
            int idAutor = Integer.parseInt(idAutorStr);
            DAOAutor daoAutor = new DAOAutor();
            Autor autor = new Autor(nombre, nacionalidad);
            autor.setIdAutor(idAutor);

            if (daoAutor.actualizarAutor(autor)) {
                Fichero.registrar("ACTUALIZAR AUTOR",
                        String.format("ID: %d, Nombre: %s, Nacionalidad: %s",
                                idAutor, nombre, nacionalidad));
                JOptionPane.showMessageDialog(this, "Autor actualizado correctamente");
                idYautores(); // Refrescar lista
                librosDisponibles();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo actualizar el autor");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID de autor debe ser numérico");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error de base de datos: " + e.getMessage());
        }
    }

    private void eliminarLibro() {
        String isbn = campoISBN.getText().trim();

        if (isbn.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor introduzca un ISBN válido", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            DAOLibro daoLibro = new DAOLibro();
            int filasBorradas = daoLibro.eliminarLibroPorISBN(isbn);

            if (filasBorradas > 0) {
                Fichero.registrar("ELIMINAR LIBRO",
                        String.format("ISBN: %s", isbn));
                JOptionPane.showMessageDialog(this, "Libro eliminado correctamente");
                librosDisponibles();
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró un libro con ese ISBN", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en la base de datos:  porfavor introduzca el autor", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        botonCerrarSesion = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        campoTitulo = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        campoNombreAutor = new javax.swing.JTextField();
        botonAgregar = new javax.swing.JToggleButton();
        botonBorrar = new javax.swing.JToggleButton();
        jLabel5 = new javax.swing.JLabel();
        campoCategoria = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        campoNacionalidad = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        campoISBN = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        campoIdAutor = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        idsYAutroesArea = new javax.swing.JTextArea();
        jLabel9 = new javax.swing.JLabel();
        botonActulizar = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        librosDisponiblesArea = new javax.swing.JTextArea();
        jLabel10 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        botonCerrarSesion.setText("Cerrar sesion");
        botonCerrarSesion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonCerrarSesionActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel1.setText("Titulo");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel2.setText("Apartado para agregar o borrar libros");

        jLabel3.setForeground(new java.awt.Color(204, 0, 0));
        jLabel3.setText("(En caso de querer borra un libro deberas de colocar el nombre completo del autor)");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel4.setText("Nombre del autor");

        botonAgregar.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        botonAgregar.setText("Agregar");
        botonAgregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonAgregarActionPerformed(evt);
            }
        });

        botonBorrar.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        botonBorrar.setText("Borrar");
        botonBorrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonBorrarActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel5.setText("Categoria");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel6.setText("Nacionaidad");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel7.setText("ISBN");

        campoISBN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                campoISBNActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel8.setText("ID del autor");

        idsYAutroesArea.setEditable(false);
        idsYAutroesArea.setColumns(20);
        idsYAutroesArea.setRows(5);
        jScrollPane1.setViewportView(idsYAutroesArea);

        jLabel9.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel9.setText("Autores");

        botonActulizar.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        botonActulizar.setText("Actualizar");
        botonActulizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonActulizarActionPerformed(evt);
            }
        });

        librosDisponiblesArea.setEditable(false);
        librosDisponiblesArea.setColumns(20);
        librosDisponiblesArea.setRows(5);
        jScrollPane2.setViewportView(librosDisponiblesArea);

        jLabel10.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel10.setText("Libros disponibles");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(botonCerrarSesion))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(90, 90, 90)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(26, 26, 26))
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(layout.createSequentialGroup()
                                                            .addGap(10, 10, 10)
                                                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addComponent(jLabel5))
                                                    .addGap(28, 28, 28)
                                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(campoTitulo)
                                                        .addComponent(campoCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                .addGroup(layout.createSequentialGroup()
                                                    .addComponent(jLabel7)
                                                    .addGap(79, 79, 79)
                                                    .addComponent(campoISBN, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel6)
                                                .addGap(40, 40, 40)
                                                .addComponent(campoNacionalidad, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                        .addComponent(jLabel4)
                                                        .addGap(18, 18, 18))
                                                    .addGroup(layout.createSequentialGroup()
                                                        .addGap(11, 11, 11)
                                                        .addComponent(jLabel8)
                                                        .addGap(57, 57, 57)))
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                    .addComponent(campoNombreAutor)
                                                    .addComponent(campoIdAutor, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(botonAgregar)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(botonActulizar)
                                        .addGap(73, 73, 73)))
                                .addComponent(botonBorrar)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(75, 75, 75)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 337, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(50, 50, 50)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 406, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(204, 204, 204)
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel10)
                                .addGap(126, 126, 126)))))
                .addContainerGap(61, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(botonCerrarSesion)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10))
                .addGap(21, 21, 21)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addComponent(campoIdAutor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(campoNombreAutor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(campoNacionalidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(campoTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(campoCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(24, 24, 24)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(campoISBN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7))
                        .addGap(46, 46, 46)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(botonAgregar)
                            .addComponent(botonBorrar)
                            .addComponent(botonActulizar, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE)))
                .addContainerGap(100, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void botonCerrarSesionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonCerrarSesionActionPerformed
        if (ventanaPrincipal == null || !ventanaPrincipal.isDisplayable()) {
            this.dispose();
            ventanaPrincipal = new VentanaPrincipal();
            ventanaPrincipal.setVisible(true);
        } else {
            ventanaPrincipal.toFront();
        }
    }//GEN-LAST:event_botonCerrarSesionActionPerformed

    private void botonAgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonAgregarActionPerformed
        agregarLibro();
    }//GEN-LAST:event_botonAgregarActionPerformed

    private void botonBorrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonBorrarActionPerformed
        eliminarLibro();
    }//GEN-LAST:event_botonBorrarActionPerformed

    private void campoISBNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_campoISBNActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_campoISBNActionPerformed

    private void botonActulizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonActulizarActionPerformed
        actualizarAutor();
    }//GEN-LAST:event_botonActulizarActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(VentanaAdmin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VentanaAdmin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VentanaAdmin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VentanaAdmin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new VentanaAdmin().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton botonActulizar;
    private javax.swing.JToggleButton botonAgregar;
    private javax.swing.JToggleButton botonBorrar;
    private javax.swing.JButton botonCerrarSesion;
    private javax.swing.JTextField campoCategoria;
    private javax.swing.JTextField campoISBN;
    private javax.swing.JTextField campoIdAutor;
    private javax.swing.JTextField campoNacionalidad;
    private javax.swing.JTextField campoNombreAutor;
    private javax.swing.JTextField campoTitulo;
    private javax.swing.JTextArea idsYAutroesArea;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea librosDisponiblesArea;
    // End of variables declaration//GEN-END:variables
}
