package Compilador;

import java.io.FileNotFoundException;
import Utiles.LectorArchivo;
import Utiles.Token;
import static Compilador.Errores.errorLexico;

/**
 * implementacion del analizador lexico
 *
 * @author Muzas Gaston, Wilson Mariano
 * @version 20/05/19
 */
public class AnalizadorLexico {

    static String palabrasReservadas[] = {"program", "var", "integer", "boolean", "procedure", "function", "begin", "end", "or", "and", "not", "if", "then", "else", "while", "do", "read", "write", "true", "false"};
    static int nroLinea;
    static LectorArchivo lectorArch;
    static int index = 0;
    static int inicio = 3;
    static String lineaAuxiliar = "nanoskyBoton";
    static String linea = " ";
    static boolean salir = false;

    //CONSTRUCTOR
    public AnalizadorLexico(String archivo) {
        //listaTokens = new LinkedList<>();
        //creador = new CreadorTokens();
        nroLinea = -1;
        try {
            lectorArch = new LectorArchivo(archivo);
        } catch (FileNotFoundException e) {
            errorLexico("AnalizadorLexico, Descripcion: error en nombre de archivo");
        }
    }

    public Token retornarToken() {

        Token token = new Token();
        
        if ((index == 0) || (index == lineaAuxiliar.length()))
            LocalizarProximaLinea();
        
        if(linea.equalsIgnoreCase("Fin de archivo.")){
            token.setTipo("Fin");
            token.setValor("Fin");
        }
        else{
            token = MacheoTokens(ObtengoCaracterActual(),ObtengoCaracterSiguiente());
        }
    	return token;
        
    } // fin del metodo       

       
 // ***************************************************** fin de la clase principal *******************************************   
  
    public static boolean finDeLinea(){
    	return index!=linea.length();
    }
        
    
    public static Token verificar(String linea) {
        //es digito, letra o un caracter que no esta en el alfabeto de entrada
        Token token = new Token();
        String cadena = "";
        String lexema = "";
        if (Character.isDigit(linea.charAt(index))) {
            //aca deberia ver el proximo caracter, pero ya se que es un tokenNum
            while ((index < linea.length() - 1) && (Character.isDigit(linea.charAt(index + 1)))) {
                lexema = lexema + linea.charAt(index);
                index++;
            }
            lexema = lexema + linea.charAt(index);
            index++;
            int numb = Integer.parseInt(lexema);
            //String pal = "tokenNum: " + numb + "@";
            token = new Token("constanteNumerica", "tokenNum");
        } else {

            if (Character.isLetter(linea.charAt(index))) {
                cadena += linea.charAt(index);
                //aca deberia ver el proximo caracter, pero ya se que es un tokenId(puede ser palabra reservada)
                //mientras me queden simbolos en linea, y sean letras o num
                while ((index < linea.length() - 1) && (Character.isLetter(linea.charAt(index + 1)) || Character.isDigit(linea.charAt(index + 1)))) {
                    index++;
                    cadena += linea.charAt(index);
                }
                index++;
                cadena = buscarPalRes(cadena);
                //no es identificador
                if (!cadena.equalsIgnoreCase("tokenId")) {
                    token = new Token("palabraReservada", cadena);
                } //es identificador
                else {
                    token = new Token("identificador", cadena);
                }
            } //no comienza con digito ni letra
            else {
                errorLexico("AnalizadorLexico, Descripcion: el simbolo '" + linea.charAt(index) + "' en la posicion: " + index + " no pertenece al alfabeto de entrada");
                System.exit(0);
            }
        }
        return token;
    }

    public static String buscarPalRes(String pal) {
        boolean encontrado = false;
        int i = 0;

        while (!encontrado && i < palabrasReservadas.length) {
            if (palabrasReservadas[i].equalsIgnoreCase(pal)) {
                encontrado = true;
                pal = palabrasReservadas[i];   //" "+palabrasReservadas[i]+
            }
            i++;
        }
        if (!encontrado) {		//si nunca encontro la palabra es un id, caso contrario ya esta la palabra en pal
            pal = "tokenId";
        }
        return pal;
    }

    public static int longitudComentario(String linea) {

        while ((index < linea.length()) && ((linea.charAt(index) != '}'))) {
            index++;
        }
        if (index == linea.length()) {
            index = -1;
        } else {
            index++;
        }

        return index;
    }

public static int obtNumLinea() {
 
	return nroLinea;
}


public static char ObtengoCaracterActual(){
	
	
	char caracterActual;
	
	if (linea.charAt(index) == ' ') {
        		
		caracterActual = linea.charAt(LocalizarProxCaracter());
		while (caracterActual==' ')
			caracterActual = linea.charAt(LocalizarProxCaracter());
				
		}
        else{ 
            caracterActual = linea.charAt(index);
        }
	return caracterActual;
}

public static char ObtengoCaracterSiguiente(){
	
	char proxCaracter = ' ';
    if (index + 1 != linea.length()) 
        proxCaracter = linea.charAt(index + 1); //si no hay nada queda en blanco
    
    return proxCaracter;
	
}

public static void LocalizarProximaLinea(){
    	
	lineaAuxiliar = lectorArch.obtLinea();
	while (lineaAuxiliar.isEmpty()){ 
		lineaAuxiliar = lectorArch.obtLinea();
                nroLinea++;
        }	
                nroLinea++;
                linea = lineaAuxiliar;
                index = 0;
        
}  // fin del metodo
    
  
public static int LocalizarProxCaracter() {
    	// aqui tengo el indice donde esta el primer blanco...y la linea...deberia arrastrar hasta el primer caracter, en
		// caso de encontrar el fin de la linea pide otra
	
	   int longitudRestante = linea.length();
       int i = index;
       boolean encontrado = false;
       while ((i < longitudRestante) && (!encontrado)) {
    	   if (linea.charAt(i) != ' ') 
                encontrado = true;
    	   else
    		   i++;
       } // fin de while
       
       if (i==longitudRestante)
    	   LocalizarProximaLinea();
       else
    	   index = i;
       
      return index;
    }

        
    public static Token ObtenerSigToken() {

        Token token = new Token();
        char caracterActual, proxCaracter;
        
        LocalizarProximaLinea();
              
        return MacheoTokens(ObtengoCaracterActual(),ObtengoCaracterSiguiente());
       
    }  // fin del metodo
    
   public static Token MacheoTokens (char caracterActual, char proxCaracter) {
        
    	
    	Token token = new Token();
    	if (caracterActual != ' ') {
            switch (caracterActual) {
                case '=':
                    token = new Token("operador_relacional", "igual");
                    index++;
                    break;

                case '<':
                    if (proxCaracter == '=') {
                        token = new Token("operador_relacional", "menor_igual");
                        index = index + 2;
                    } else {
                        if (proxCaracter == '>') {
                            token = new Token("operador_relacional", "distinto");
                            index = index + 2;
                        } else {
                            token = new Token("operador_relacional", "menor");
                            index++;
                        }
                    }
                    break;

                case '>':
                    if (proxCaracter == '=') {
                        token = new Token("operador_relacional", "mayor_igual");
                        index = index + 2;
                    } else {
                        token = new Token("operador_relacional", "mayor");
                        index++;
                    }
                    break;

                case ':':
                    if (proxCaracter == '=') {
                        token = new Token("opAsignacion", "asignacion");
                        index = index + 2;
                    } else {
                        token = new Token("opPuntuacion", "dosPuntos");
                        index++;
                    }
                    break;

                case '+':
                    token = new Token("sum_res", "suma");
                    index++;
                    break;
                case '-':
                    token = new Token("sum_res", "resta");
                    index++;
                    break;
                case '*':
                    token = new Token("mult_div", "producto");
                    index++;
                    break;
                case '/':
                    token = new Token("mult_div", "division");
                    index++;
                    break;
                case '.':
                    token = new Token("opPuntuacion", "punto");
                    index++;
                    break;
                case ';':
                    token = new Token("opPuntuacion", "puntoYComa");
                    index++;
                    break;
                case ',':
                    token = new Token("opPuntuacion", "coma");
                    index++;
                    break;
                case '(':
                    token = new Token("parentizacion", "parenAbre");
                    index++;
                    break;
                case ')':
                    token = new Token("parentizacion", "parenCierra");
                    index++;
                    break;

                case '{':
                    int a = longitudComentario(linea);
                    if (a != -1) {
                        index = a;
                        token = ObtenerSigToken();
                    } else {
                        token = new Token("Error", "}");
                    	                        
                    }
                    break;

                case '}':
                	token = new Token("Error", "{");
                	break;

                case '@':
                    index++;
                    token = new Token();
                    break;

                default:
                    token = verificar(linea);
                    break;

            }  // fin de switch

        }  // fin de if != ' '
        return token;
    	
    }  // fin de macheo de tokens

}  // fin de la clase

