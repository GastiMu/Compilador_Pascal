package Compilador;

import Utiles.Token;
import static Compilador.Errores.errorLexico;
import static Compilador.Errores.errorSintactico;
import Utiles.Entorno;
import Utiles.PilaEntornos;


public class AnalizadorSintactico {

    static String[] primLLamProc = {"parenAbre", "read", "write"}; // estos forman parte de tipo ...por eso es complicado comparar
    static String[] primSubprog = {"function", "procedure"};
    static String[] primExpr10 = {"tokenNum", "tokenId", "parenAbre","true","false"};
    static String[] primExpresionFinal = {"tokenNum", "tokenId","true","false"};
    static String[] primCompartido = {"tokenId", "if", "while", "read", "write"};
    static String[] primExpr3 = {"sum_res", "tokenNum", "tokenId", "parenAbre","true","false"};
    private final AnalizadorLexico lexico;
    private static Token preanalisis;
    private PilaEntornos pila;
    private static String temporal = " "; 

    public static void main(String[] args) {

    } // fin de main

    //CONSTRUCTOR
    public AnalizadorSintactico(String archivo) {
        lexico = new AnalizadorLexico(archivo);
        preanalisis = lexico.retornarToken();
        pila = new PilaEntornos();
    }

    private static boolean enPrimeros(Token t, String[] primeros) {

        boolean salir = false;
        int i = 0;
        int l = primeros.length;
        while ((i < l) && (!salir)) {
            if (primeros[i].equalsIgnoreCase(t.getTipo())) {    // voy a obtener la palabra de primeros que son tipo---> constNum 
            	salir = true;
            }
            else
               	if (t.getValor().equalsIgnoreCase(primeros[i])) {
                salir = true;
            }
            i++;
        }
        return salir;
    }

    public void match(Token t) {
        //compara con el equals redefinido en la clase Token
        if (!(preanalisis.getTipo().equalsIgnoreCase("Error"))) {
    	
            if (preanalisis.equals(t)) {
                try {
                    if(preanalisis.getValor().equalsIgnoreCase("tokenId")){
                        temporal = preanalisis.getNombre();
                    }
                    preanalisis = lexico.retornarToken();
                    if(preanalisis.getValor().equalsIgnoreCase("Fin")){
                        Errores.ejecucionExitosa();
                    }
                    //caso de haber leido fin de archivo
                    
                } catch (Exception e) { 
                    errorSintactico("No se pudo obtener token");
                }
            } else{ 
                String valor = t.getValor();
                
                //switch para refinar un poco los errores y darle mas detalle
                //valor = lo que espero
                //preanalisis = lo que leo
                switch(valor){
                    case "begin": if(preanalisis.getValor().equalsIgnoreCase("tokenId")) errorSintactico("Se esperaba begin o var y se leyo " + preanalisis.getValor());
                                    else errorSintactico("Se esperaba " + t.getValor() + " y se leyo " + preanalisis.getValor());break;
                        
                    case "dosPuntos": if(preanalisis.getValor().equalsIgnoreCase("tokenId")) errorSintactico("Se esperaba , o : y se leyo " + preanalisis.getValor());
                                      else if(preanalisis.getValor().equalsIgnoreCase("parenAbre")) errorSintactico("Probablemente falte la palabra reservada del subprograma");
                                      else errorSintactico("Se esperaba " + t.getValor() + " y se leyo " + preanalisis.getValor());break;     
                                    
                    
                    default: errorSintactico("Se esperaba " + t.getValor() + " y se leyo " + preanalisis.getValor());break;
            
                }
            }
        }
        else {
            if (preanalisis.getValor().equalsIgnoreCase("}"))	
                errorLexico("Se esperaba " + "}" + " y nunca se leyo ");
            else
                errorLexico("Se esperaba " + "{" + " y nunca se leyo ");
        }
    }

    public String tipoDato() {
        String tipoAux;
        if (preanalisis.getValor().equalsIgnoreCase("boolean")) {
            match(new Token("palabraReservada", "boolean"));
            tipoAux = "boolean";
        } else {
            match(new Token("palabraReservada", "integer"));
            tipoAux = "integer";
        }
        return tipoAux;
    }

    public void declaracionVariables(Entorno entorno) {
        String tipoAux; // va a almacenar el tipo de dato de el conjunto de variables
        match(new Token("palabraReservada", "var"));
        do {
            match(new Token("identificador", "tokenId"));
            AnalizadorSemantico.insertarVariables(entorno, temporal);
            while (preanalisis.getValor().equalsIgnoreCase("coma")) {
                match(new Token("opPuntuacion", "coma"));
                match(new Token("identificador", "tokenId"));
                AnalizadorSemantico.insertarVariables(entorno, temporal);
            }
            match(new Token("opPuntuacion", "dosPuntos"));
            tipoAux = tipoDato();
            AnalizadorSemantico.seteadorTipos(tipoAux, entorno);
            match(new Token("opPuntuacion", "puntoYComa"));
        } while (preanalisis.getValor().equalsIgnoreCase("tokenId"));
        //aca si leo abajo de la declaracion de variables, una funcion o proced sin su palabra reservada lo toma como tokenId
    }

    public void programaPrincipal() {

        match(new Token("palabraReservada", "program"));
        Entorno principal = new Entorno();
        pila.apilarEntorno(principal);
        match(new Token("identificador","tokenId"));
        principal.setNombreEntorno(temporal);
        
        match(new Token("opPuntuacion","puntoYComa"));
        
        if (preanalisis.getValor().equalsIgnoreCase("var")) {
            declaracionVariables(principal);
        }

        while (enPrimeros(preanalisis, primSubprog)) {
            
            subprograma();
        }
            
           // System.out.println("antes cuerpo");
        cuerpoSubprograma();
            //System.out.println("paso cuerpo");
        match(new Token("opPuntuacion", "punto"));
        
    } // fin del metodo programaPrincipal

    public void subprograma() {
        
        if (preanalisis.getValor().equalsIgnoreCase("function")) {
            declaracionFuncion();
        } else {
            if (preanalisis.getValor().equalsIgnoreCase("procedure")) {
                declaracionProcedimiento();
            } else {
            	errorSintactico("Se esperaba function o procedure");
            	
            }
        }
    }

    public void declaracionFuncion() {
        String tipoAux;
        match(new Token("palabraReservada", "function"));
        Entorno funcion = new Entorno();
        pila.apilarEntorno(funcion);
        match(new Token("identificador", "tokenId"));
        funcion.setNombreEntorno(temporal);
        AnalizadorSemantico.insertarVariables(funcion, temporal);
        funcion.setIndiceTabla(1); //el indice de tabla comienza en 1 porque en 0 esta la variable de retorno de la funcion(y no hay que setear su tipo)
        
        if (preanalisis.getValor().equalsIgnoreCase("parenAbre")) {
            parametrosFormales(funcion); 
        }
        match(new Token("opPuntuacion", "dosPuntos"));
        tipoAux = tipoDato();
        funcion.getTablaSimbolos().get(0).setTipo(tipoAux); //setea la variable de retorno con el mismo tipo de dato que la funcion
        match(new Token("opPuntuacion", "puntoYComa"));
        if (preanalisis.getValor().equalsIgnoreCase("var")) {
            declaracionVariables(funcion);
        }

        while ((preanalisis.getValor().equalsIgnoreCase("function")) || (preanalisis.getValor().equalsIgnoreCase("procedure"))) {
            if (preanalisis.getValor().equalsIgnoreCase("function")) {
                declaracionFuncion();
            } else {
                declaracionProcedimiento();
            }
        }
        cuerpoSubprograma();
        match(new Token("opPuntuacion", "puntoYComa"));
    } // fin de declaracionFuncion

    public void declaracionProcedimiento() {
        
        match(new Token("palabraReservada", "procedure"));
        match(new Token("identificador", "tokenId"));
        if (preanalisis.getValor().equalsIgnoreCase("parenAbre")) {
            parametrosFormales();
        }
        match(new Token("opPuntuacion", "puntoYComa"));
        if (preanalisis.getValor().equalsIgnoreCase("var")) {
            declaracionVariables();
        }
        while ((preanalisis.getValor().equalsIgnoreCase("function")) || (preanalisis.getValor().equalsIgnoreCase("procedure"))) {
            if (preanalisis.getValor().equalsIgnoreCase("function")) {
                declaracionFuncion();
            } else {
                declaracionProcedimiento();
            }
        }
        cuerpoSubprograma();
        match(new Token("opPuntuacion", "puntoYComa"));
    }

    public void cuerpoSubprograma() {

        match(new Token("palabraReservada", "begin"));
        bloque();
        match(new Token("palabraReservada", "end"));
    }

    public void parametrosFormales(Entorno entorno) {
        
        match(new Token("parentizacion", "parenAbre"));
        parametroFormalValor(entorno);
        while (preanalisis.getValor().equalsIgnoreCase("tokenId")) {
            match(new Token("opPuntuacion", "puntoYComa"));
            parametroFormalValor(entorno);
        }
        match(new Token("parentizacion", "parenCierra"));
    }

    public void parametroFormalValor(Entorno entorno) {
        String tipoAux;
        match(new Token("identificador", "tokenId"));
        AnalizadorSemantico.insertarVariables(entorno, temporal);
        while (preanalisis.getValor().equalsIgnoreCase("coma")) {
            match(new Token("opPuntuacion", "coma"));
            match(new Token("identificador", "tokenId"));
            AnalizadorSemantico.insertarVariables(entorno, temporal);
        }
        match(new Token("opPuntuacion", "dosPuntos"));
        tipoAux = tipoDato();
        AnalizadorSemantico.seteadorTipos(tipoAux, entorno);

    } // fin de parametro formal Valor

    public void parametrosReales() {
        match(new Token("parentizacion", "parenAbre"));
        expresion();
        while (preanalisis.getValor().equalsIgnoreCase("coma")) {
            match(new Token("opPuntuacion", "coma"));
            expresion();
        }
        match(new Token("parentizacion", "parenCierra"));
    }

    public void procedEsp() {
        switch (preanalisis.getValor()) {
            case "read":
                match(new Token("palabraReservada", "read"));
                match(new Token("parentizacion", "parenAbre"));
                match(new Token("identificador", "tokenId"));
                match(new Token("parentizacion", "parenCierra"));
                break;

            case "write":
                match(new Token("palabraReservada", "write"));
                match(new Token("parentizacion", "parenAbre"));
                expresion();
                match(new Token("parentizacion", "parenCierra"));
                break;

            default:
                System.out.println("Error, se esperaba read o write");
                break;
        }
    }

    public void llamadaFunc() {
        if (preanalisis.getValor().equalsIgnoreCase("parenAbre")) {
            parametrosReales();
        }
    }

    public void sentenciaAsignacion() {

        match(new Token("opAsignacion", "asignacion"));
        expresion();
    }

    public void expresion() {
        expresion1();
        expresionAux();
    }

    public void expresionAux() {

        if (preanalisis.getValor().equalsIgnoreCase("or")) {
            match(new Token("palabraReservada", "or"));
            expresion1();
            expresionAux();
        }
    }

    public void expresion1() {
        expresion2();
        expresion1Aux();
    }

    public void expresion1Aux() {

        if (preanalisis.getValor().equalsIgnoreCase("and")) {
            match(new Token("palabraReservada", "and"));
            expresion2();
            expresion1Aux();
        }
    }

    public void expresion2() {
        if (preanalisis.getValor().equalsIgnoreCase("not")) {
            match(new Token("palabraReservada", "not"));
            expresion2();
        } else {
            if (enPrimeros(preanalisis, primExpr3)) {
                comparacion();
            } else {
            	
            	errorSintactico("se esperaba: +, -, constante numerica, id, (, o NOT");
            	//System.out.println("Error, se esperaba +, -, constante numerica, id, (, o NOT");
            }
        }
    }

    public void comparacion() {
        terminoSumRest();
        comparacionAux();

    }

    public void comparacionAux() {
        switch (preanalisis.getValor()){
            
            case "igual" :  match(new Token("operador_relacional", "igual"));
                            auxiliarComparacion();break;
            
            case "distinto" :   match(new Token("operador_relacional", "distinto"));
                                auxiliarComparacion();break;
            
            
            case "menor" :  match(new Token("operador_relacional", "menor"));
                            auxiliarComparacion();break;
            
            
            case "mayor" :  match(new Token("operador_relacional", "mayor"));
                            auxiliarComparacion();break;
            
            
            case "menor_igual" :    match(new Token("operador_relacional", "menor_igual"));
                                    auxiliarComparacion();break;
            
            
            case "mayor_igual" :  match(new Token("operador_relacional", "mayor_igual"));
                                  auxiliarComparacion();break;
        }
    }
    
    public void auxiliarComparacion(){
        terminoSumRest();
        comparacionAux();
    }

    public void terminoSumRest() {

        terminoProdDiv();
        terminoSumRestAux();

    }

    public void terminoSumRestAux() {
        if (preanalisis.getValor().equalsIgnoreCase("suma")) {
            match(new Token("sum_res", "suma"));
            auxiliarSumaRest();
        }
        else{
            if(preanalisis.getValor().equalsIgnoreCase("resta")){
                match(new Token("sum_res", "resta"));
                auxiliarSumaRest();
            }
            /*else {
            	if (AnalizadorLexico.finDeLinea() && !preanalisis.getValor().equalsIgnoreCase("parenCierra") && !preanalisis.getValor().equalsIgnoreCase("coma")&& !preanalisis.getValor().equalsIgnoreCase("puntoYComa"))
                    errorSintactico("Simbolo no identificado");
            }*/
        }
    }

    public void auxiliarSumaRest(){
        terminoProdDiv();
        terminoSumRestAux();
    }
    
    public void terminoProdDiv() {

        expresion3();
        terminoProdDivAux();

    }

    public void terminoProdDivAux() {
        if (preanalisis.getValor().equalsIgnoreCase("producto")) {
            match(new Token("mult_div", "producto"));
            auxiliarProdDiv();
        }
        else{
            if(preanalisis.getValor().equalsIgnoreCase("division")){
                match(new Token("mult_div", "division"));
                auxiliarSumaRest();
            }
           /* else {
                // verificar la longitud antes de tirar el error y verifica que no sea ")" preanalisis
            	String auxiliar = preanalisis.getValor();
                if (AnalizadorLexico.finDeLinea() && !auxiliar.equalsIgnoreCase("parenCierra") && !auxiliar.equalsIgnoreCase("suma") && !auxiliar.equalsIgnoreCase("resta")&& !auxiliar.equalsIgnoreCase("coma") && !auxiliar.equalsIgnoreCase("puntoYComa")){
                    errorSintactico("Simbolo no identificado");                    
                }
            }*/
        }
    }

    public void auxiliarProdDiv(){
        expresion3();
        terminoProdDivAux();
        
    }
    

    public void expresion3() {

        if (preanalisis.getValor().equalsIgnoreCase("suma")) {
            match(new Token("sum_res", "suma"));
            expresion10();
        }
        else{
            if(preanalisis.getValor().equalsIgnoreCase("resta")){
                match(new Token("sum_res", "resta"));
                expresion10();
            }
            else {
                if (enPrimeros(preanalisis, primExpr10)) {
                    expresion10();
                } else {
                    errorSintactico("Se espera SUMA, RESTA, NUM, ID O (");
                }
            }
        }
    }
    
    public void expresion10() {
        if (enPrimeros(preanalisis, primExpresionFinal)) {   // metodo auxiliar para comparar las palabras que estan dentro de los primeros 
            expresionFinal();
        } else {
            if (preanalisis.getValor().equalsIgnoreCase("parenAbre")) {
                match(new Token("parentizacion", "parenAbre"));
                expresion();
                match(new Token("parentizacion", "parenCierra"));
            } else {
            	errorSintactico("Error, se esperaba 'constanteNum', 'id' o '(' .");
            }
        }
    }

    public void expresionFinal() {

    	switch (preanalisis.getValor()){
        
        case "tokenNum" : match(new Token("constanteNumerica", "tokenNum"));
        				  ;break;
        
        case "tokenId" :  match(new Token("identificador", "tokenId"));
        				  expresionFinalAux(); 
        				  ;break;
                
        case "true" : match(new Token("palabraReservada", "true")); 
        			break;
        
        
        case "false" : match(new Token("palabraReservada", "false"));
        				break;
        
        default: errorSintactico("Error, se esperaba constanteNum o id true o false ");
        		 break;
        
    }
      	
    	// ***********************************************************
    /*	if (preanalisis.getValor().equalsIgnoreCase("tokenNum")) {
            match(new Token("constanteNumerica", "tokenNum"));
        } else {
            if (preanalisis.getValor().equalsIgnoreCase("tokenId")) {
                match(new Token("identificador", "tokenId"));
                expresionFinalAux();
            } else {
                errorSintactico("Error, se esperaba constanteNum o id ");
            }
        } */
    } // fin del metodo

    public void expresionFinalAux() {
        if (preanalisis.getValor().equalsIgnoreCase("parenAbre")) {
            parametrosReales();
        }
    }

    public void sentenciaCondicional() {

        match(new Token("palabraReservada", "if"));
        expresion();
        match(new Token("palabraReservada", "then"));
        sentencias();
        if (preanalisis.getValor().equalsIgnoreCase("else")) {
            match(new Token("palabraReservada", "else"));
            sentencias();
        }
    }

    public void sentencias() {
        if (enPrimeros(preanalisis, primCompartido)) {
            sentenciaSimple();
         } else {
            if (preanalisis.getValor().equalsIgnoreCase("begin")) {
                cuerpoSubprograma();
            } else {
                errorSintactico("Error, se esperaba 'id', 'if', 'while' o 'begin' .");
            }
        }
    }

    public void sentenciaSimple() {

        switch (preanalisis.getValor()) {
            case "tokenId":
                match(new Token("identificador", "tokenId"));
                absId();
                break;

            case "if":
                sentenciaCondicional();
                break;

            case "while":
                sentenciaRepetitiva();
                break;

            case "write":
                procedEsp();
                break;

            case "read":
                procedEsp();
                break;

            default:
                errorSintactico("Error, se esperaba id, if, while, read o write");
                break;
        }
    }

    public void absId() {
        if (preanalisis.getValor().equalsIgnoreCase("asignacion")) {
            sentenciaAsignacion();
            
        } else {
            if (preanalisis.getValor().equalsIgnoreCase("parenAbre")) {
                parametrosReales();
            } else {
                if (preanalisis.getValor().equalsIgnoreCase("puntoYComa")) {
                    match(new Token("opPuntuacion", "puntoYComa"));
                } else {
                    errorSintactico("Error, se esperaba :=, (, o ; .");
                }
            }
        }
    }

    public void bloque() {
        sentenciaSimple();
        bloqueAux();
    }

    public void bloqueAux() {

        if (preanalisis.getValor().equalsIgnoreCase("puntoYComa")) {
            match(new Token("opPuntuacion", "puntoYComa"));
            bloqueAux2();
        }
        else
        	if (!preanalisis.getValor().equalsIgnoreCase("end"))
        		errorSintactico("Error, se esperaba ; and, or suma resta, etc  y llego "+preanalisis.getValor() );
        
    }

    public void bloqueAux2() {
        if (enPrimeros(preanalisis, primCompartido)) {
            bloque();
        }
    }

    public void sentenciaRepetitiva() {
        match(new Token("palabraReservada", "while"));
        expresion();
        match(new Token("palabraReservada", "do"));
        sentencias();
    }

} // fin de la clase  