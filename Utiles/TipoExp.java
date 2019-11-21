/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utiles;

/**
 *
 * @author Gaston
 */
public class TipoExp {

    private String nombre; //utilizado solo para variable de retorno de funcion
    private String tipoDato;
    private String operacion;

    private final String opCompatiblesInteger []= {"suma", "resta", "division", "producto", "asignacion","menor","mayor", "menor_igual", "mayor_igual","distinto", "igual"};
    private String opCompatiblesBoolean[] = {"or", "and", "not", "igual"};

    public TipoExp(String unTipoDato) {

        this.tipoDato = unTipoDato;

    }

    public TipoExp() {

    }

    public String getTipoDato() {
        return tipoDato;
    }

    public void setTipoDato(String tipoDato) {
        this.tipoDato = tipoDato;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
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

    public boolean compararAmbosLados(TipoExp expDerecha) {

        return (this.tipoDato.equalsIgnoreCase(expDerecha.tipoDato));

    }

}
