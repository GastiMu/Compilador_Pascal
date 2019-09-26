/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Utiles;

import static Compilador.Errores.errorSemantico;

/**
 *
 * @author mmuzas
 */
public class PilaEntornos {

    private Entorno tope;
    private int cantidadEntornos;

    //pila vacia
    public PilaEntornos() {
        tope = null;
        cantidadEntornos = 0;
    }

    public boolean apilarEntorno(Entorno ent) {
        //actualizo quien llamo al ambiente
        ent.setInvocador(tope);
        cantidadEntornos++;
        //actualiza el tope para que apunte al nuevo etorno
        tope = ent; //puntero tope

        return true;
    }

    public boolean desapilarEntorno() {
        boolean exito = true;

        if (!esVacia()) {
            tope = tope.getInvocador();
            cantidadEntornos--;
        } else {
            exito = false;
        }

        return exito;
    }

    public Entorno obtenerTope() {

        return tope.getInvocador();
    }

    public boolean esVacia() {

        return (tope == null);
    }

    public void vaciar() {
        cantidadEntornos = 0;
        tope = null;
    }

    public String mostrarAmbientes() {
        String cadena = "";
        Entorno aux = tope;

        //mientras enlace del nodo no sea null concateno los elementos
        while (aux != null) {
            cadena += aux.getNombreEntorno();
            aux = aux.getInvocador();
        }

        return cadena;
    }

    public Variable buscarAparicionVar(String nombre) {
        Variable var = new Variable();
        Entorno aux = tope;
        int i = 0;
        boolean salir = false;
        while (i < cantidadEntornos && !salir) {
            
          //  salir = aux.existeVar(nombre); //aca va el metodo para buscar dentro de un entorno de la clase Entorno
            i++;
        }
        if (salir) {
            //   var = //aux.getVariable();
        } 
        else {
                errorSemantico("La variable no existe en el programa");
        }
        return var;
    }

}
