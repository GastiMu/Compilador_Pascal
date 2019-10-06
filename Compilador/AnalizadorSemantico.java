/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Compilador;

import Utiles.Entorno;
import Utiles.Variable;

/**
 *
 * @author Gaston
 */
public class AnalizadorSemantico {

    public static void seteadorTipos(String tipo, Entorno entorno) {
        int i=0;
        int indiceActual = entorno.getIndiceTabla();
        if (indiceActual == 0) {

            for (i = 0 ; i < entorno.getTablaSimbolos().size(); i++) {
                entorno.getTablaSimbolos().get(i).setTipo(tipo);
            }
            entorno.setIndiceTabla(i); //actualiza el indice de la tabla porque ya seteo tipos de datos en las variables
        }
        else{
            for (i = indiceActual ; i < entorno.getTablaSimbolos().size(); i++) {
                entorno.getTablaSimbolos().get(i).setTipo(tipo);
            }
            entorno.setIndiceTabla(i);
        }
    }

    public static void insertarVariables(Entorno entorno, String nombreVar) {
        Variable var = new Variable();
        var.setNombre(nombreVar);
        entorno.agregarVariable(nombreVar);
    }

}
