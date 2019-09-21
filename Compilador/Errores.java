
package Compilador;

import static Compilador.AnalizadorLexico.obtNumLinea;

/**
 *
 * @author Gaston
 */
public class Errores {

    
    public static void errorLexico(String error) {
        System.out.println("Error lexico en linea "+obtNumLinea() + ": "+ error + ".");
        System.exit(0);
    }
    
    public static void errorSintactico(String error) {
        System.out.println("Error sintactico en linea " + obtNumLinea() + ": " + error + ".");
        System.exit(0);
    }

    public static void ejecucionExitosa() {
        System.out.println("Ejecucion exitosa.");
        System.exit(1);
    }
}
