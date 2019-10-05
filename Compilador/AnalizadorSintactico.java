package Compilador;

import Utiles.Token;
import static Compilador.Errores.errorLexico;
import static Compilador.Errores.errorSemantico;
import static Compilador.Errores.errorSintactico;
import Utiles.Entorno;
import Utiles.PilaEntornos;
import utiles.TipoExp;

public class AnalizadorSintactico {

    static String[] primLLamProc = {"parenAbre", "read", "write"}; // estos forman parte de tipo ...por eso es complicado comparar
    static String[] primSubprog = {"function", "procedure"};
    static String[] primExpr10 = {"tokenNum", "tokenId", "parenAbre", "true", "false"};
    static String[] primExpresionFinal = {"tokenNum", "tokenId", "true", "false"};
    static String[] primCompartido = {"tokenId", "if", "while", "read", "write"};
    static String[] primExpr3 = {"sum_res", "tokenNum", "tokenId", "parenAbre", "true", "false"};
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
            } else if (t.getValor().equalsIgnoreCase(primeros[i])) {
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
                    if (preanalisis.getValor().equalsIgnoreCase("tokenId")) {
                        temporal = preanalisis.getNombre();
                    }
                    preanalisis = lexico.retornarToken();
                    if (preanalisis.getValor().equalsIgnoreCase("Fin")) {
                        Errores.ejecucionExitosa();
                    }
                    //caso de haber leido fin de archivo

                } catch (Exception e) {
                    errorSintactico("No se pudo obtener token");
                }
            } else {
                String valor = t.getValor();

                //switch para refinar un poco los errores y darle mas detalle
                //valor = lo que espero
                //preanalisis = lo que leo
                switch (valor) {
                    case "begin":
                        if (preanalisis.getValor().equalsIgnoreCase("tokenId")) {
                            errorSintactico("Se esperaba begin o var y se leyo " + preanalisis.getValor());
                        } else {
                            errorSintactico("Se esperaba " + t.getValor() + " y se leyo " + preanalisis.getValor());
                        }
                        break;

                    case "dosPuntos":
                        if (preanalisis.getValor().equalsIgnoreCase("tokenId")) {
                            errorSintactico("Se esperaba , o : y se leyo " + preanalisis.getValor());
                        } else if (preanalisis.getValor().equalsIgnoreCase("parenAbre")) {
                            errorSintactico("Probablemente falte la palabra reservada del subprograma");
                        } else {
                            errorSintactico("Se esperaba " + t.getValor() + " y se leyo " + preanalisis.getValor());
                        }
                        break;

                    default:
                        errorSintactico("Se esperaba " + t.getValor() + " y se leyo " + preanalisis.getValor());
                        break;

                }
            }
        } else {
            if (preanalisis.getValor().equalsIgnoreCase("}")) {
                errorLexico("Se esperaba " + "}" + " y nunca se leyo ");
            } else {
                errorLexico("Se esperaba " + "{" + " y nunca se leyo ");
            }
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
        match(new Token("identificador", "tokenId"));
        principal.setNombreEntorno(temporal);

        match(new Token("opPuntuacion", "puntoYComa"));

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
        Entorno procedimiento = new Entorno();
        pila.apilarEntorno(procedimiento);
        match(new Token("identificador", "tokenId"));
        procedimiento.setNombreEntorno(temporal);
        AnalizadorSemantico.insertarVariables(procedimiento, temporal);

        if (preanalisis.getValor().equalsIgnoreCase("parenAbre")) {
            parametrosFormales(procedimiento);
        }
        match(new Token("opPuntuacion", "puntoYComa"));
        if (preanalisis.getValor().equalsIgnoreCase("var")) {
            declaracionVariables(procedimiento);
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
        pila.desapilarEntorno();
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

    public void sentenciaAsignacion(TipoExp ladoIzq) {
        boolean compatible;
        match(new Token("opAsignacion", "asignacion"));

        if (!compatible) {
            errorSemantico("Error, no son compatibles la operacion con el tipo de dato.");
        } else {
            TipoExp ladoDer = expresion();
            compatible = ladoIzq.compararAmbosLados(ladoDer);
            if (!compatible) {
                errorSemantico("Error, no son compatibles los tipos de dato.");
            } else {
                exp.setTipoDato("integer");
            }

            //sentencia de asignacion lo resolvemos en la vuelta
        }

    

    public TipoExp expresion() {
        TipoExp td = new TipoExp();
        expresion1(td);
        expresionAux(td);
    }

    public void expresionAux(TipoExp td) {

        if (preanalisis.getValor().equalsIgnoreCase("or")) {
            match(new Token("palabraReservada", "or"));
            expresion1(td);
            expresionAux(td);
        }
    }

    public void expresion1(TipoExp td) {
        TipoExp tipoD;
        tipoD = expresion2(td);
        expresion1Aux(td, tipoD);
    }

    public void expresion1Aux(TipoExp td) {

        if (preanalisis.getValor().equalsIgnoreCase("and")) {

            match(new Token("palabraReservada", "and"));
            expresion2(td);
            expresion1Aux(td);
        }
    }

    public TipoExp expresion2(TipoExp td) {
        boolean compatible;
        TipoExp exp = new TipoExp();
        if (preanalisis.getValor().equalsIgnoreCase("not")) {
            match(new Token("palabraReservada", "not"));
            
            TipoExp ladoDer = expresion2(td);
            compatible = ladoDer.verifCompatibilidadOperacion("not");
            if (!compatible) {
                errorSemantico("Error, no son compatibles la operacion con el tipo de dato.");
            } else {
                exp.setTipoDato("boolean");
            }
        } else {
            if (enPrimeros(preanalisis, primExpr3)) {
                exp = comparacion(td);
            } else {

                errorSintactico("se esperaba: +, -, constante numerica, id, (, o NOT");
                //System.out.println("Error, se esperaba +, -, constante numerica, id, (, o NOT");
            }
        }
        return exp;
    }

    public TipoExp comparacion(TipoExp td) {
        TipoExp tipoD;
        tipoD = terminoSumRest(td);
        tipoD = comparacionAux(td, tipoD);

        return tipoD;
    }

    public TipoExp comparacionAux(TipoExp ladoIzq, TipoExp tipoD) {
        boolean compatible;
        TipoExp exp = new TipoExp();
        switch (preanalisis.getValor()) {

            case "igual":
                match(new Token("operador_relacional", "igual"));
                compatible = ladoIzq.verifCompatibilidadOperacion("igual");

                if (!compatible) {
                    errorSemantico("Error, no son compatibles la operacion con el tipo de dato.");
                } else {
                    TipoExp ladoDer = auxiliarComparacion(ladoIzq);
                    compatible = ladoIzq.compararAmbosLados(ladoDer);
                    if (!compatible) {
                        errorSemantico("Error, no son compatibles los tipos de dato.");
                    } else {
                        exp.setTipoDato("integer");
                    }
                }
                break;

            case "distinto":
                match(new Token("operador_relacional", "distinto"));
                compatible = ladoIzq.verifCompatibilidadOperacion("distinto");

                if (!compatible) {
                    errorSemantico("Error, no son compatibles la operacion con el tipo de dato.");
                } else {
                    TipoExp ladoDer = auxiliarComparacion(ladoIzq);
                    compatible = ladoIzq.compararAmbosLados(ladoDer);
                    if (!compatible) {
                        errorSemantico("Error, no son compatibles los tipos de dato.");
                    } else {
                        exp.setTipoDato("integer");
                    }
                }
                break;

            case "menor":
                match(new Token("operador_relacional", "menor"));
                compatible = ladoIzq.verifCompatibilidadOperacion("menor");

                if (!compatible) {
                    errorSemantico("Error, no son compatibles la operacion con el tipo de dato.");
                } else {
                    TipoExp ladoDer = auxiliarComparacion(ladoIzq);
                    compatible = ladoIzq.compararAmbosLados(ladoDer);
                    if (!compatible) {
                        errorSemantico("Error, no son compatibles los tipos de dato.");
                    } else {
                        exp.setTipoDato("integer");
                    }
                }
                break;

            case "mayor":
                match(new Token("operador_relacional", "mayor"));
                compatible = ladoIzq.verifCompatibilidadOperacion("mayor");

                if (!compatible) {
                    errorSemantico("Error, no son compatibles la operacion con el tipo de dato.");
                } else {
                    TipoExp ladoDer = auxiliarComparacion(ladoIzq);
                    compatible = ladoIzq.compararAmbosLados(ladoDer);
                    if (!compatible) {
                        errorSemantico("Error, no son compatibles los tipos de dato.");
                    } else {
                        exp.setTipoDato("integer");
                    }
                }
                break;

            case "menor_igual":
                match(new Token("operador_relacional", "menor_igual"));
                compatible = ladoIzq.verifCompatibilidadOperacion("menor_igual");

                if (!compatible) {
                    errorSemantico("Error, no son compatibles la operacion con el tipo de dato.");
                } else {
                    TipoExp ladoDer = auxiliarComparacion(ladoIzq);
                    compatible = ladoIzq.compararAmbosLados(ladoDer);
                    if (!compatible) {
                        errorSemantico("Error, no son compatibles los tipos de dato.");
                    } else {
                        exp.setTipoDato("integer");
                    }
                }
                break;

            case "mayor_igual":
                match(new Token("operador_relacional", "mayor_igual"));
                compatible = ladoIzq.verifCompatibilidadOperacion("mayor_igual");

                if (!compatible) {
                    errorSemantico("Error, no son compatibles la operacion con el tipo de dato.");
                } else {
                    TipoExp ladoDer = auxiliarComparacion(ladoIzq);
                    compatible = ladoIzq.compararAmbosLados(ladoDer);
                    if (!compatible) {
                        errorSemantico("Error, no son compatibles los tipos de dato.");
                    } else {
                        exp.setTipoDato("integer");
                    }
                }
                break;
            default:
                exp = tipoD;
        }
        return exp;
    }

    public TipoExp auxiliarComparacion(TipoExp td) {
        TipoExp tipoD;
        tipoD = terminoSumRest(td);
        tipoD = comparacionAux(td, tipoD);

        return tipoD;
    }

    public TipoExp terminoSumRest(TipoExp td) {
        TipoExp tipoD;
        tipoD = terminoProdDiv(td);
        tipoD = terminoSumRestAux(td, tipoD);

        return tipoD;
    }

    public TipoExp terminoSumRestAux(TipoExp ladoIzq, TipoExp tipoD) {
        boolean compatible;
        TipoExp exp = new TipoExp();
        if (preanalisis.getValor().equalsIgnoreCase("suma")) {
            match(new Token("sum_res", "suma"));

            compatible = ladoIzq.verifCompatibilidadOperacion("suma");

            if (!compatible) {
                errorSemantico("Error, no son compatibles la operacion con el tipo de dato.");
            } else {
                TipoExp ladoDer = auxiliarSumaRest(ladoIzq);
                compatible = ladoIzq.compararAmbosLados(ladoDer);
                if (!compatible) {
                    errorSemantico("Error, no son compatibles los tipos de dato.");
                } else {
                    exp.setTipoDato("integer");
                }
            }

        } else {
            if (preanalisis.getValor().equalsIgnoreCase("resta")) {
                match(new Token("sum_res", "resta"));
                compatible = ladoIzq.verifCompatibilidadOperacion("resta");

                if (!compatible) {
                    errorSemantico("Error, no son compatibles la operacion con el tipo de dato.");
                } else {
                    TipoExp ladoDer = auxiliarSumaRest(ladoIzq);
                    compatible = ladoIzq.compararAmbosLados(ladoDer);
                    if (!compatible) {
                        errorSemantico("Error, no son compatibles los tipos de dato.");
                    } else {
                        exp.setTipoDato("integer");
                    }
                }
            } else {
                exp = tipoD;
            }
            /*else {
             if (AnalizadorLexico.finDeLinea() && !preanalisis.getValor().equalsIgnoreCase("parenCierra") && !preanalisis.getValor().equalsIgnoreCase("coma")&& !preanalisis.getValor().equalsIgnoreCase("puntoYComa"))
             errorSintactico("Simbolo no identificado");
             }*/
        }
        return exp;
    }

    public TipoExp auxiliarSumaRest(TipoExp td) {
        TipoExp tipoD;
        tipoD = terminoProdDiv(td);
        tipoD = terminoSumRestAux(td, tipoD);

        return tipoD;
    }

    public TipoExp terminoProdDiv(TipoExp td) {
        TipoExp tipoD;
        tipoD = expresion3(td);
        tipoD = terminoProdDivAux(td, tipoD);

        return tipoD;
    }

    public TipoExp terminoProdDivAux(TipoExp ladoIzq, TipoExp tipoD) {
        boolean compatible;
        TipoExp exp = new TipoExp();
        if (preanalisis.getValor().equalsIgnoreCase("producto")) {
            match(new Token("mult_div", "producto"));
            compatible = ladoIzq.verifCompatibilidadOperacion("producto");

            if (!compatible) {
                errorSemantico("Error, no son compatibles la operacion con el tipo de dato.");
            } else {
                TipoExp ladoDer = auxiliarProdDiv(ladoIzq);
                compatible = ladoIzq.compararAmbosLados(ladoDer);
                if (!compatible) {
                    errorSemantico("Error, no son compatibles los tipos de dato.");
                } else {
                    exp.setTipoDato("integer");
                }
            }
        } else {
            if (preanalisis.getValor().equalsIgnoreCase("division")) {
                match(new Token("mult_div", "division"));
                compatible = ladoIzq.verifCompatibilidadOperacion("dvision");

                if (!compatible) {
                    errorSemantico("Error, no son compatibles la operacion con el tipo de dato.");
                } else {
                    TipoExp ladoDer = auxiliarSumaRest(ladoIzq);
                    compatible = ladoIzq.compararAmbosLados(ladoDer);
                    if (!compatible) {
                        errorSemantico("Error, no son compatibles los tipos de dato.");
                    } else {
                        exp.setTipoDato("integer");
                    }
                }
            } else {
                exp = tipoD;
            }
            /* else {
             // verificar la longitud antes de tirar el error y verifica que no sea ")" preanalisis
             String auxiliar = preanalisis.getValor();
             if (AnalizadorLexico.finDeLinea() && !auxiliar.equalsIgnoreCase("parenCierra") && !auxiliar.equalsIgnoreCase("suma") && !auxiliar.equalsIgnoreCase("resta")&& !auxiliar.equalsIgnoreCase("coma") && !auxiliar.equalsIgnoreCase("puntoYComa")){
             errorSintactico("Simbolo no identificado");                    
             }
             }*/
        }
        return exp;
    }

    public TipoExp auxiliarProdDiv(TipoExp td) {
        TipoExp tipoD;
        tipoD = expresion3(td); //tipoD salvado por parametro en la siguiente linea
        tipoD = terminoProdDivAux(td, tipoD);

        return tipoD;
    }

    public TipoExp expresion3(TipoExp ladoIzq) {
        boolean compatible;
        TipoExp exp = new TipoExp();
        if (preanalisis.getValor().equalsIgnoreCase("suma")) {
            match(new Token("sum_res", "suma"));
            compatible = ladoIzq.verifCompatibilidadOperacion("suma");

            if (!compatible) {
                errorSemantico("Error, no son compatibles la operacion con el tipo de dato.");
            } else {
                TipoExp ladoDer = expresion10();
                compatible = ladoIzq.compararAmbosLados(ladoDer);
                if (!compatible) {
                    errorSemantico("Error, no son compatibles los tipos de dato.");
                } else {
                    exp.setTipoDato("integer");
                }
            }
        } else {
            if (preanalisis.getValor().equalsIgnoreCase("resta")) {
                match(new Token("sum_res", "resta"));
                compatible = ladoIzq.verifCompatibilidadOperacion("resta");

                if (!compatible) {
                    errorSemantico("Error, no son compatibles la operacion con el tipo de dato.");
                } else {
                    TipoExp ladoDer = expresion10();
                    compatible = ladoIzq.compararAmbosLados(ladoDer);
                    if (!compatible) {
                        errorSemantico("Error, no son compatibles los tipos de dato.");
                    } else {
                        exp.setTipoDato("integer");
                    }
                }
            } else {
                if (enPrimeros(preanalisis, primExpr10)) {
                    exp = expresion10();
                } else {
                    errorSintactico("Se espera SUMA, RESTA, NUM, ID O (");
                }
            }
        }
        return exp;
    }

    public TipoExp expresion10() {
        TipoExp td = new TipoExp();
        if (enPrimeros(preanalisis, primExpresionFinal)) {   // metodo auxiliar para comparar las palabras que estan dentro de los primeros 
            td = expresionFinal();
        } else {
            if (preanalisis.getValor().equalsIgnoreCase("parenAbre")) {
                match(new Token("parentizacion", "parenAbre"));
                expresion();
                match(new Token("parentizacion", "parenCierra"));
            } else {
                errorSintactico("Error, se esperaba 'constanteNum', 'id' o '(' .");
            }
        }
        return td;
    }

    public TipoExp expresionFinal() {
        TipoExp td = new TipoExp();
        switch (preanalisis.getValor()) {

            case "tokenNum":
                match(new Token("constanteNumerica", "tokenNum"));
                td.setTipoDato("integer");
                break;

            case "tokenId":
                match(new Token("identificador", "tokenId"));

                expresionFinalAux();
                break;

            case "true":
                match(new Token("palabraReservada", "true"));
                break;

            case "false":
                match(new Token("palabraReservada", "false"));
                break;

            default:
                errorSintactico("Error, se esperaba constanteNum o id true o false ");
                break;

        }
        return td;
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
            TipoExp primerTD = new TipoExp();
            sentenciaSimple(primerTD);
        } else {
            if (preanalisis.getValor().equalsIgnoreCase("begin")) {
                cuerpoSubprograma();
            } else {
                errorSintactico("Error, se esperaba 'id', 'if', 'while' o 'begin' .");
            }
        }
    }

    public void sentenciaSimple(TipoExp td) {

        switch (preanalisis.getValor()) {
            case "tokenId":
                match(new Token("identificador", "tokenId"));
                if (!pila.existeVarEnPila(temporal)) {
                    errorSemantico("La variable no existe en el programa");
                } else {
                    String tipo = pila.obtenerTipoVarEnPila(temporal);  //en la variable tipo guardo el tipo de dato del tokenId
                    TipoExp ladoIzq = new TipoExp(tipo);
                    absId(ladoIzq);
                }

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

    public void absId(TipoExp ladoIzq) {
        if (preanalisis.getValor().equalsIgnoreCase("asignacion")) {
            sentenciaAsignacion(ladoIzq);

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
        } else if (!preanalisis.getValor().equalsIgnoreCase("end")) {
            errorSintactico("Error, se esperaba ; and, or suma resta, etc  y llego " + preanalisis.getValor());
        }

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
