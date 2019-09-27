package Utiles;

import static Compilador.Errores.errorSemantico;
import java.util.ArrayList;

public class Entorno {

    private int indiceTabla;
    private ArrayList<Variable> tablaSimbolos;
    private Entorno invocador;    // un entorno puede llamar a muchos ...pero en un determinado momento solo tiene uno
    private String nombreEntorno;

    public Entorno() {

	// ver que crea como entorno inicial
    }

    public void setInvocador(Entorno unEntorno) {
        this.invocador = unEntorno;

    }

    public Entorno getInvocador() {
        return this.invocador;
    }

    public ArrayList<Variable> getTablaSimbolos() {
        return tablaSimbolos;
    }

    public void setTablaSimbolos(ArrayList<Variable> tablaSimbolos) {
        this.tablaSimbolos = tablaSimbolos;
    }

    public void setIndiceTabla(int indiceTabla) {
        this.indiceTabla = indiceTabla;
    }

    public int getIndiceTabla() {
        return indiceTabla;
    }

    public String getNombreEntorno() {
        return nombreEntorno;
    }

    public void setNombreEntorno(String nombreEntorno) {
        this.nombreEntorno = nombreEntorno;
    }

    //el metodo retorna -1 si no existe la variable o el indice si existe, a nivel local
    public int existeVariableEntorno(String nombreVar) {
        int i = 0;
        int indice = -1;
        int longitud = this.tablaSimbolos.size();
        boolean salir = false;
        while ((i < longitud) && (!salir)) {
            if (this.tablaSimbolos.get(i).getNombre().equalsIgnoreCase(nombreVar)) {
                salir = true;
                indice = i;
            } else {
                i++;
            }
        }
        return indice;
    }

    // el metodo esta diseñado pensando en que ya se verifico la existencia, por ende se tiene el indice
    public Variable obtenerVariableEntorno(int indice) {
        Variable var = tablaSimbolos.get(indice);

        return var;
    }

    public boolean agregarVariable(String nombre) {

        boolean estado = false;
        if (existeVariableEntorno(nombre) == -1) {
            Variable nuevaVar = new Variable(nombre);
            tablaSimbolos.add(nuevaVar);
            estado = true;
        } else {
            errorSemantico("Error, la variable " + nombre + " ya existe en el entorno");
        }

        return estado;
    }

} // fin de la clase
