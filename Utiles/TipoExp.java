/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utiles;

/**
 *
 * @author Gaston
 */
public class TipoExp {

    private String tipoDato;
    private String operacion;

    private final String opCompatiblesInteger[] = {"suma", "resta", "division", "multiplicacion", "asignacion"};
    private String opCompatiblesBoolean[] = {"or", "and", "not"};

    public TipoExp(String unTipoDato) {

        this.tipoDato = unTipoDato;

    }

    public boolean verifCompatibilidadOperacion(String unaOperacion) {

        boolean encontrado;
        if (this.tipoDato.equalsIgnoreCase("integer")) {
            encontrado = buscarOperacion(this.opCompatiblesInteger, unaOperacion);
        } else {
            encontrado = buscarOperacion(this.opCompatiblesBoolean, unaOperacion);

        }
        return encontrado;

    }

    private boolean buscarOperacion(String[] operadoresValidos, String unaOperacion) {

        int i = 0;
        int l = operadoresValidos.length;
        boolean salir = false;
        while ((i < l) && (!salir)) {
            if (operadoresValidos[i].equalsIgnoreCase(unaOperacion)) {
                salir = true;
            }
            i++;
        }
        return salir;
    }

}
