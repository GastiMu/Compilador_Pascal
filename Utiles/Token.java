
package Utiles;

/**
 *
 * @author Gaston
 */
public class Token {

    private String tipo;
    private String valor;
    private String nombre;

    public Token() {
    }
    
    public Token(String tipo, String valor) {
        this.tipo = tipo;
        this.valor = valor;
    }
    
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nom) {
        this.nombre = nom;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        return "(" + tipo + "," + valor + ')';
    }

    public boolean equals(Token t) {
        
        boolean igual = false;
        if (this.tipo.equalsIgnoreCase(t.tipo) && this.valor.equalsIgnoreCase(t.valor)) {
            return true;
        }
        return igual;
    }
    
        
}
