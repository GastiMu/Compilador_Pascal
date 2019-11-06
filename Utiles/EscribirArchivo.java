/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utiles;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author Gaston
 */
public class EscribirArchivo {

    File archivo;
    BufferedWriter bw;

    //genera un archivo extension .mep
    public EscribirArchivo(String nombre) {
        if (nombre.contains(".")) {
            nombre = nombre.substring(0, nombre.lastIndexOf("."));
        }
        archivo = new File(nombre + ".mep");
    }

    public void escribirFuente(String fuente) {
        try {
            bw = new BufferedWriter(new FileWriter(archivo));
            bw.write(fuente);
            bw.close();
        } catch (IOException ex) {
            System.out.println("Error en escritura");
        }
    }
}

