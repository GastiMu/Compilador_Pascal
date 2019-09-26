package Utiles;

public class Variable {

	private String nombre;
	private String tipo;
	// definir mas atributos ?
	
public Variable(){
	this.nombre = "";
	this.tipo = "";
	
}

public Variable(String nombre, String tipo){
	this.nombre = nombre;
	this.tipo = tipo;
	
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


} // fin de la clase
