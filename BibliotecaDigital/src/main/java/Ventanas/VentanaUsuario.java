/*
 * Ventana del usuario en la que puede reservar un libro para leerlo
 */
package Ventanas;

import ConexionDAO.ConexionBD;
import ConexionDAO.DAOLibro;
import ConexionDAO.DAOPrestamo;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import modelo.Libro;
import modelo.Prestamo;
import modelo.Usuario;

/**
 *
 * @author south
 */
public class VentanaUsuario extends javax.swing.JFrame {

    private final Usuario usuarioActual;

    private VentanaPrincipal ventanaprincipal;

    /**
     * Creates new form VentanaUsuario
     */
    public VentanaUsuario(Usuario usuarioActual) {
        initComponents();
        mostrarLibrosEnLista();
        this.usuarioActual = usuarioActual;
    }

    private void escogerLibro() {
        int[] seleccionados = listaLibros.getSelectedIndices();

        if (seleccionados.length != 1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar exactamente un libro.", "Selección inválida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String seleccion = listaLibros.getSelectedValue();
        String titulo = seleccion.split(" - ")[0].trim();
        String estado = seleccion.substring(seleccion.lastIndexOf('(') + 1, seleccion.lastIndexOf(')')).trim();

        if (!estado.equalsIgnoreCase("disponible")) {
            JOptionPane.showMessageDialog(this, "El libro ya está prestado.", "No disponible", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            DAOPrestamo daoPrestamo = new DAOPrestamo();

            if (daoPrestamo.usuarioTienePrestamoActivo(usuarioActual.getIdUsuario())) {
                JOptionPane.showMessageDialog(this, "Ya tienes un préstamo activo. Debes devolverlo antes de tomar otro libro.", "Límite de préstamo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            DAOLibro daoLibro = new DAOLibro();
            int idLibro = daoLibro.obtenerIdLibroPorTitulo(titulo);
            if (idLibro == -1) {
                JOptionPane.showMessageDialog(this, "No se pudo encontrar el ID del libro.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Prestamo nuevoPrestamo = new Prestamo(
                    usuarioActual,
                    new Libro(idLibro, titulo, estado),
                    LocalDate.now(),
                    LocalDate.now().plusDays(15)
            );

            Connection conn = ConexionBD.conectarBD();
            boolean exito = daoPrestamo.guardarPrestamo(conn, nuevoPrestamo);
            if (exito) {
                daoLibro.actualizarEstadoLibro(idLibro, "prestado");
                JOptionPane.showMessageDialog(this, "Préstamo registrado con éxito recuerda que tendra 15 dias para leerlo", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                mostrarLibrosEnLista();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo registrar el préstamo.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            conn.close();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al registrar el préstamo: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarLibrosEnLista() {
        try {
            DAOLibro daoLibro = new DAOLibro();
            List<Libro> libros = daoLibro.listarLibrosConAutores(); 

            DefaultListModel<String> modelo = new DefaultListModel<>();
            for (Libro libro : libros) {
                modelo.addElement(libro.toString());
            }

            listaLibros.setModel(modelo);

        } catch (SQLException e) {
            System.out.println("Error al cargar libros: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error al cargar libros: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscarLibro() {
        String buscar = campoBuscar.getText().trim();

        if (buscar.isEmpty()) {
            mostrarLibrosEnLista();
            return;
        }

        try {
            DAOLibro daoLibro = new DAOLibro();
            List<String> libros = daoLibro.buscarLibrosPorTitulo(buscar);

            DefaultListModel<String> model = new DefaultListModel<>();
            for (String libro : libros) {
                model.addElement(libro);
            }
            listaLibros.setModel(model);

            if (model.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se encontraron libros con ese criterio", "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error al buscar libros: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ordenarLibrosAscendente() {
        try {
            DAOLibro daoLibro = new DAOLibro();
            List<Libro> libros = daoLibro.listarLibrosConAutores();

            libros.sort((l1, l2) -> l1.getTitulo().compareToIgnoreCase(l2.getTitulo()));

            DefaultListModel<String> modelo = new DefaultListModel<>();
            for (Libro libro : libros) {
                modelo.addElement(libro.toString());
            }
            listaLibros.setModel(modelo);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al ordenar libros: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ordenarLibrosDescendente() {
        try {
            DAOLibro daoLibro = new DAOLibro();
            List<Libro> libros = daoLibro.listarLibrosConAutores();

            libros.sort((l1, l2) -> l2.getTitulo().compareToIgnoreCase(l1.getTitulo()));

            DefaultListModel<String> modelo = new DefaultListModel<>();
            for (Libro libro : libros) {
                modelo.addElement(libro.toString());
            }

            listaLibros.setModel(modelo);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al ordenar libros: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

        jToggleButton2 = new javax.swing.JToggleButton();
        campoBuscar = new javax.swing.JTextField();
        botonBuscar = new javax.swing.JToggleButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        listaLibros = new javax.swing.JList<>();
        botonReserva = new javax.swing.JToggleButton();
        botonCerrarSesion = new javax.swing.JToggleButton();
        jLabel1 = new javax.swing.JLabel();
        botonOdenarMayor = new javax.swing.JButton();
        botonOdenarMenor = new javax.swing.JButton();

        jToggleButton2.setText("jToggleButton2");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        botonBuscar.setText("Buscar");
        botonBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonBuscarActionPerformed(evt);
            }
        });

        listaLibros.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(listaLibros);

        botonReserva.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        botonReserva.setText("Escoger");
        botonReserva.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonReservaActionPerformed(evt);
            }
        });

        botonCerrarSesion.setText("Cerrar sesion");
        botonCerrarSesion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonCerrarSesionActionPerformed(evt);
            }
        });

        jLabel1.setText("Ordenar");

        botonOdenarMayor.setText("Mayor a menor");
        botonOdenarMayor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonOdenarMayorActionPerformed(evt);
            }
        });

        botonOdenarMenor.setText("Menor a mayor");
        botonOdenarMenor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonOdenarMenorActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(93, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(campoBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 369, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(35, 35, 35)
                        .addComponent(botonBuscar))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(154, 154, 154)
                        .addComponent(botonReserva))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(46, 46, 46)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 303, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(100, 100, 100)
                                .addComponent(jLabel1))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(69, 69, 69)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(botonOdenarMenor)
                                    .addComponent(botonOdenarMayor))))))
                .addGap(41, 41, 41))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(botonCerrarSesion)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap(13, Short.MAX_VALUE)
                        .addComponent(botonCerrarSesion)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(campoBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(botonBuscar))
                        .addGap(37, 37, 37)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 336, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(64, 64, 64))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(143, 143, 143)
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(botonOdenarMayor)
                        .addGap(18, 18, 18)
                        .addComponent(botonOdenarMenor)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addComponent(botonReserva)
                .addGap(47, 47, 47))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void botonBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonBuscarActionPerformed
        buscarLibro();
    }//GEN-LAST:event_botonBuscarActionPerformed

    private void botonCerrarSesionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonCerrarSesionActionPerformed
        if (ventanaprincipal == null || !ventanaprincipal.isDisplayable()) {
            this.dispose();
            ventanaprincipal = new VentanaPrincipal();
            ventanaprincipal.setVisible(true);
        } else {
            ventanaprincipal.toFront();
        }
    }//GEN-LAST:event_botonCerrarSesionActionPerformed

    private void botonReservaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonReservaActionPerformed
        escogerLibro();
    }//GEN-LAST:event_botonReservaActionPerformed

    private void botonOdenarMayorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonOdenarMayorActionPerformed
        ordenarLibrosAscendente();
    }//GEN-LAST:event_botonOdenarMayorActionPerformed

    private void botonOdenarMenorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonOdenarMenorActionPerformed
        ordenarLibrosDescendente();
    }//GEN-LAST:event_botonOdenarMenorActionPerformed

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
            java.util.logging.Logger.getLogger(VentanaUsuario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VentanaUsuario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VentanaUsuario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VentanaUsuario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Usuario usuarioPrueba = new Usuario(1, "Juan", "juan@mail.com", "1234", "usuario");
                new VentanaUsuario(usuarioPrueba).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton botonBuscar;
    private javax.swing.JToggleButton botonCerrarSesion;
    private javax.swing.JButton botonOdenarMayor;
    private javax.swing.JButton botonOdenarMenor;
    private javax.swing.JToggleButton botonReserva;
    private javax.swing.JTextField campoBuscar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToggleButton jToggleButton2;
    private javax.swing.JList<String> listaLibros;
    // End of variables declaration//GEN-END:variables

}
