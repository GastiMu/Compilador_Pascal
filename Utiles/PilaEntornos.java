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
        //no tengo que indicar el invocador porque lo cambie en sintactico cuando cree entorno
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
        //aca cambie porque era el invocador del tope cuando en realidad es el tope
        return tope;
    }

    public int getCantEntornos(){
        return cantidadEntornos;
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
        int indice = -1;
        int i = 0;
        boolean salir = false;
        while (i < cantidadEntornos && indice == -1) {
            
            indice = aux.existeVariableEntorno(nombre); 
            if(indice == -1){
                i++;
                aux = aux.getInvocador();
            }
            if(indice != -1){
                var = aux.obtenerVariableEntorno(indice);
                if(var.getProcedencia().equalsIgnoreCase("parametroHijo")){
                    indice = -1; //no esta en el entorno realmente
                    i++;
                }
            }
        }
        if (indice != -1) {
            var = aux.obtenerVariableEntorno(indice);
        } 
        else {
                errorSemantico("La variable no existe en el programa");
        }
        return var;
    }
    
    public boolean existeVarEnPila(String nombre) {
        Entorno aux = tope;
        int indice = -1;
        int i = 0;
        boolean salir = false;
        while (i < cantidadEntornos && indice == -1) {
            
            indice = aux.existeVariableEntorno(nombre); 
            if(indice == -1){
                i++;
                aux = aux.getInvocador();
            }
        }
        if (indice != -1) {
            salir = true;
        }
        return salir;
    }
    
    //mismo metodo con distinto retorno para evaluar tipo de dato en expresiones
    public String obtenerTipoVarEnPila(String nombre) {
        Entorno aux = tope;
        int indice = -1;
        int i = 0;
        String salida = "";
        while (i < cantidadEntornos && indice == -1) {
            
            indice = aux.existeVariableEntorno(nombre); 
            if(indice == -1){
                i++;
                aux = aux.getInvocador();
            }
        }
        if (indice != -1) {
            salida = aux.obtenerVariableEntorno(indice).getTipo();
        }
        return salida;
    }

    public Entorno devolverEntorno(String nombre){
        boolean encontrado=false;
        int cant = this.cantidadEntornos;
        Entorno actual = tope;
        String etiqueta="";
        
        while(!encontrado && cant>0){
            if(actual.getNombreEntorno().equalsIgnoreCase(nombre)){
                encontrado = true;
            }
            else{    
                cant--;
                actual = actual.getInvocador();
                    }
        }
        return actual;
    }
    
}
