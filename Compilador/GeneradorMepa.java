/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Compilador;

import Utiles.*;

/**
 * ver los desplazamientos y los saltos
 *
 * considerar mas de un switch, ya sea para las operaciones o para otras
 * sentencias
 *
 * @author Gaston
 */
public class GeneradorMepa {

    private static String cadena;
    private static int numEtiqueta;

    public GeneradorMepa() {

        this.cadena = " ";
        this.numEtiqueta = 1;

    }

    public String generarEtiqueta() {
        String e = "l" + numEtiqueta;
        numEtiqueta++;
        return e;
    }

    public static String getCadena() {
        return cadena;
    }

    public void iniProgrPrinc() {
        cadena += "INPP \n";
    }

    public void declararProcedimiento(String etiqueta, int anidamiento) {
        cadena += etiqueta + " ENPR " + anidamiento + " \n";
    }

//Agregar anidamiento y desplazamiento a las variables
    public void apilarVar(Variable var) {
        cadena += "APVL " + var.getAnidamiento() + " " + var.getDesplazamiento() + " \n";
    }

    public void apilarConstante(int i) {
        cadena += "APCT " + i + " \n";
    }

    public void saltarSiempre(String etiqueta) {
        cadena += "DSVS " + etiqueta + " \n";
    }

    public void saltarSiFalso(String etiqueta) {
        cadena += "DSVF " + etiqueta + " \n";
    }

    public void destinoDeSalto(String etiqueta) {
        cadena += etiqueta + " NADA \n";
    }

    public void reservarMemoria(int param) {
        this.cadena += "RMEM " + param + "\n";

    }

     public void imprimir() {
        cadena += "IMPR \n";
    }

    public void leer(Variable var) {
        cadena += "LEER \n";
        asignarVariable(var);
    }

    public void finDePrograma() {
        cadena += "PARA \n";
    }

    public void finProcedimiento(int anidamiento, int cantParametros) {
        cadena += "RTPR " + anidamiento + " " + cantParametros + " \n";
    }

    public void liberarMemoria(int param) {
        this.cadena += "LMEM " + param + "\n";

    }

    public void asignarVariable(Variable var) {
        cadena += "ALVL " + var.getAnidamiento() + " " + var.getDesplazamiento() + " \n";
    }

    public void llamarProcedimiento(String etiqueta) {
        cadena += "LLPR " + etiqueta + " \n";
    }

    public void operaciones(String op) {

        switch (op) {
            case "sumar":
                this.cadena = this.cadena + "SUMA" + "\n";break;
            case "restar":
                this.cadena = this.cadena + "SUST" + "\n";break;
            case "multiplicar":
                this.cadena = this.cadena + "MULT" + "\n";break;
            case "dividir":
                this.cadena = this.cadena + "DIVI" + "\n";break;
            case "menosUnario":
                this.cadena = this.cadena + "UMEN" + "\n";break;
            case "conjLogica":
                this.cadena = this.cadena + "CONJ" + "\n";break;
            case "disjLogica":
                this.cadena = this.cadena + "DISJ" + "\n";break;
            case "negacion":
                this.cadena = this.cadena + "NEGA" + "\n";break;
            case "compMenor":
                this.cadena = this.cadena + "CMME" + "\n";break;
            case "compMayor":
                this.cadena = this.cadena + "CMMA" + "\n";break;
            case "compIgual":
                this.cadena = this.cadena + "CMIG" + "\n";break;
            case "compDesigual":
                this.cadena = this.cadena + "CMDG" + "\n";break;
            case "compMenorIgual":
                this.cadena = this.cadena + "CMNI" + "\n";break;
            case "compMayorIgual":
                this.cadena = this.cadena + "CMYI" + "\n";break;
        }
    }

    public void desvioSiempre() {

        this.cadena = this.cadena + "DSVS " + this.numEtiqueta + "\n";
        this.numEtiqueta++;
    }

    public void desvioFalso() {

        this.cadena = this.cadena + "DSVF " + this.numEtiqueta + "\n";
        this.numEtiqueta++;
    }

}  // fin de la clase

