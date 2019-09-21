
package Compilador;

/**
 *
 * @author Gaston
 */
public class Lanzador {

    public static void main(String[] args) {
        if (args.length == 1) {
            AnalizadorSintactico sintactico = new AnalizadorSintactico(args[0]);
            sintactico.programaPrincipal();
        } else {
            System.err.println("Introducir el nombre del archivo como parametro");
        }
    }
}