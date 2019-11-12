package Utiles;

import static Compilador.Errores.errorSemantico;
import java.util.ArrayList;

public class Entorno {

    private int indiceTabla;
    private ArrayList<Variable> tablaSimbolos;
    private Entorno invocador;    // un entorno puede llamar a muchos ...pero en un determinado momento solo tiene uno
    private String nombreEntorno;
    private int cantVariables;
    private String etiqueta;
    private int anidamiento;
    private int desplazamiento;
    private boolean funcion;

    //para el programa principal
    public Entorno() {
        indiceTabla = 0;
        tablaSimbolos = new ArrayList<Variable>();
        this.cantVariables=0;
        desplazamiento=0;
    }
    //para el resto de los entornos
    public Entorno(Entorno invocador, boolean tipo, int anidamiento) {
        indiceTabla = 0;
        tablaSimbolos = new ArrayList<Variable>();
        this.invocador = invocador;
        this.cantVariables=0;
        this.anidamiento = anidamiento;
        desplazamiento=0;
        funcion = tipo;
    }

    public void incCantVariables() {
        this.cantVariables ++;
    }

     public String getEtiqueta() {
        return etiqueta;
    }

    public void setEtiqueta(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public int getCantVariables() {
        return cantVariables;
    }
    
    public void setInvocador(Entorno invocador) {
        this.invocador = invocador;
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

    public void setIndiceTablaCargada() { 
        this.indiceTabla++; 
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
        Variable aux;
        int longitud = this.tablaSimbolos.size();
        boolean salir = false;
        while ((i < longitud) && (!salir)) {
            aux = this.tablaSimbolos.get(i);
            if (aux.getNombre().equalsIgnoreCase(nombreVar)) {
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

    public boolean agregarVariable(String nombre, String etiqueta, String tipoSubprograma) {

        boolean estado = false;
        if(tipoSubprograma.equalsIgnoreCase("parametro")){
                Variable nuevaVar = new Variable(nombre); //si es solo parametro para el invocador no importa el nombre
                nuevaVar.setProcedencia(tipoSubprograma);
                tablaSimbolos.add(nuevaVar);
                nuevaVar.setAnidamiento(this.anidamiento);
                estado = true;
        }
        else if (existeVariableEntorno(nombre) == -1){ 
                if(tipoSubprograma.equalsIgnoreCase("funcion") || tipoSubprograma.equalsIgnoreCase("procedimiento")){
                    Variable nuevaVar = new Variable(nombre); //si es solo parametro para el invocador no importa el nombre
                    nuevaVar.setProcedencia(tipoSubprograma);
                    tablaSimbolos.add(nuevaVar);
                    nuevaVar.setEtiqueta(etiqueta);
                    nuevaVar.setAnidamiento(this.anidamiento);
                    
                    if(tipoSubprograma.equalsIgnoreCase("funcion"))
                        nuevaVar.setFuncion(true);
                    
                    else
                        nuevaVar.setFuncion(false);
                    
                    estado = true;
                    }
                else{
                    Variable nuevaVar = new Variable(nombre);
                    nuevaVar.setProcedencia(tipoSubprograma);
                    tablaSimbolos.add(nuevaVar);
                    estado = true;
                    nuevaVar.setAnidamiento(this.anidamiento);
                    nuevaVar.setDesplazamiento(desplazamiento); 
                    desplazamiento++; //cada local que carga incrementa desplazamiento
                }
            }
            else {
                errorSemantico("Error, la variable " + nombre + " ya existe en el entorno");
            }

        return estado;
    }

} // fin de la clase
