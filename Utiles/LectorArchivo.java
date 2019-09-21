
package Utiles;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import static Compilador.Errores.errorLexico;

/**
 *
 * @author Gaston
 */
public class LectorArchivo {

    private static int lineaActual = 0;
    private String direccion;
    private final FileReader fr;
    private final BufferedReader br;

    public LectorArchivo(String archivoIn) throws FileNotFoundException {
        direccion = new File(archivoIn).getAbsolutePath();
        fr = new FileReader(direccion);
        br = new BufferedReader(fr);
    }

    public String obtDireAbs() {
        return direccion;
    }

    public String obtLinea() {
        String respuesta = "Fin de archivo.";
        try {
            if ((respuesta = br.readLine()) != null) {
                lineaActual++;
            } else {
                respuesta = "Fin de archivo.";
                br.close();
            }
        } catch (IOException e) {
            errorLexico("LectorArchivo, Descripcion: Lectura del archivo");
        }
        return respuesta;
    }

    public static int obtLineaActual() {
        return lineaActual;
        
    }
}
