package Utiles;

public class Variable {

	private String nombre;
	private String tipo;
	private String procedencia;
	private int anidamiento;
        private int despl;  // para parametros −(n + 3 − i)
        private String etiqueta;
        private boolean funcion;
	
public Variable(){
	this.nombre = "";
	this.tipo = "";
	
}

public Variable(String nombre){
	this.nombre = nombre;
	
}

public Variable(String nombre, int anidamiento, int desplazamiento){ //solo para read
	this.nombre = nombre;	
        this.anidamiento = anidamiento;
        this.despl = desplazamiento;
}

    public void setFuncion(boolean funcion) {
        this.funcion = funcion;
    }

    public boolean isFuncion() {
        return funcion;
    }

    public void setEtiqueta(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

public String getProcedencia() {
	return procedencia;
}

public boolean esParametro() {
	return procedencia.equalsIgnoreCase("parametro");
}

public void setProcedencia(String p) {
	this.procedencia = p;
}

public String getTipo() {
	return tipo;
}

public void setTipo(String tipo) {
	this.tipo = tipo;
}

public String getNombre() {
	return nombre;
}

public void setNombre(String nombre) {
	this.nombre = nombre;
}

public int getAnidamiento() {
	return anidamiento;
}

public void setAnidamiento(int anidamiento) {
	this.anidamiento = anidamiento;
}

public void setDesplazamiento(int n, int i) {
    
    //i empieza de 1, porque i=0 es retorno (0,1,2)
    //n son los parametros (2)
    this.despl = -(n+3-i);
}

public void setDesplazamiento(int posicion) {
    
    
    this.despl = posicion;
}

public int getDesplazamiento() {
	return despl;
}


} // fin de la clase
