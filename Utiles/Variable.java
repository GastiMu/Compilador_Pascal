package Utiles;

public class Variable {

	private String nombre;
	private String tipo;
	private String procedencia;
	private int anidamiento;
        private int despl;
	
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

public void setDesplazamiento(int i, int n) {
    
    
    this.despl = -(n+3-i);
}

public int getDesplazamiento() {
	return despl;
}


} // fin de la clase
