/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Utiles;
/**
 *
 * @author mmuzas
 */
public class PilaEntornos {

    private Entorno tope;

    //pila vacia
    public PilaEntornos(){
        tope=null;
    }
    
    public boolean apilarEntorno(Entorno ent){
        //actualizo quien llamo al ambiente
        ent.setInvocador(tope);
            
        //actualiza el tope para que apunte al nuevo etorno
        tope=ent; //puntero tope

        return true;
    }
    
    public boolean desapilarEntorno(){
        boolean exito=true;
        
        if(!esVacia()){
            tope=tope.getEntorno();
            }
        else exito=false;
        
        return exito; 
    }
    
    public int obtenerTope(){
        
        return tope.getEntorno();
    }
    
    public boolean esVacia(){
        
        return (tope==null);
    }
    
    public void vaciar(){
        
        tope=null;
    }
 
    public String mostrarAmbientes(){
        String cadena="";
        Entorno aux=tope;
        
        //mientras enlace del nodo no sea null concateno los elementos
        while(aux!=null){
            cadena+=aux.getNombreEntorno();
            aux=aux.getEntorno();
        }
        
        return cadena;
    }
}