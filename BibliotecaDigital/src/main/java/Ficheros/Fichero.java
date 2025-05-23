// Clase para guardar cosas que pasan en un archivo de texto 
package Ficheros;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Fichero {

    public static void registrar(String accion, String detalles) {
        try {
            FileWriter writer = new FileWriter("registro_actividades.txt", true);

            DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd-MM-yyyy      HH:mm:ss");

            String registro = "[" + LocalDateTime.now().format(formato) + "] " + accion + " - " + detalles + "\n";

            writer.write(registro);

            writer.close();

            System.out.println("Registro guardado: " + registro.trim());
        } catch (IOException e) {
            
            System.err.println("Error al escribir en el archivo de log: " + e.getMessage());
        }
    }
}
