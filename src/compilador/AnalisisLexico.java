/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador;

import java.util.ArrayList;
import java.util.StringTokenizer;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 *
 * @author alberto Errores: != ==
 */
public class AnalisisLexico {

    String palabrasReservadas[] = {/*Estructuras de control*/"if", "else", "do", "while", "for", "switch", "break", "case",/*Tipos de dato*/ "integer", "double", "string", "boolean", "char",/*Otro*/ "declare", "as",/*Logico*/ "true", "false", "read", "print"};
    char separadores[] = {
        ' ', '{', '}', '(', ')', ';', '&', '|', '<', '>', '+', '-', '=', '*', '/', '%', '!', ',', '"', '\n', '\'', ',', ':'
    };
    String tokens[] = {"tkn_id", "tkn_string", "tkn_op_relacional", "tkn_num", "tkn_char", "<OperadorAritmetico>", "tkn_;", "tkn_igual", "tkn_op_logico", "<TipoDato>", "tkn_{", "tkn_}", "tkn_(", "tkn_)", "tkn_,", "tkn_:", "tkn_+", "tkn_-", "tkn_*", "tkn_/" /*AQUI*/, "tkn_if", "tkn_else", "tkn_do", "tkn_while", "tkn_for", "tkn_switch", "tkn_break", "tkn_case",/*Tipos de dato*/ "tkn_palabra_integer", "tkn_palabra_double", "tkn_palabra_string", "tkn_palabra_boolean", "tkn_palabra_char",/*Otro*/ "tkn_declare", "tkn_as", "tkn_true", "tkn_false", "tkn_read", "tkn_print"};
    int numTokensId = 20;
    int automata[][] = {
        //0//1 / //2///3 /4 /5  /6  /7  /8  /9  10  11  12  13  14  15  16  17 18 19  20   21 22  23  24  25  26  27   28
        //C//FC ///T///L /D /C  /%  /*  /+  /-  //  /=  /e  /E  /&  /|  /¡  /< /> /"  /'   /. /;  /(  /)  /{  /}  /,  /:
        {0, 501, -1, 15, 5, -1, 23, 35, 33, 34, 36, 25, 15, 15, 17, 19, 21, 3, 1, 13, 11, -1, 24, 27, 28, 29, 30, 31, 32},
        {1, 501, 3, -1, -1, -1, -1, -1, -1, -1, -1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {2, 501, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {3, 501, 3, -1, -1, -1, -1, -1, -1, -1, -1, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {4, 501, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {5, 501, 4, -1, 5, -1, -1, -1, -1, -1, -1, -1, 9, 9, -1, -1, -1, -1, -1, -1, -1, 6, -1, -1, -1, -1, -1, -1, -1},
        {6, 500, -1, -1, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {7, 501, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, 9, 9, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {8, 500, -1, -1, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {9, 500, -1, -1, 10, -1, -1, -1, 8, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {10, 501, 4, -1, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {11, 500, -1, 16, 16, 16, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 12, -1, -1, -1, -1, -1, -1, -1, -1},
        {12, 501, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {13, 500, -1, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 14, 13, 13, 13, 13, 13, 13, 13, 13, -1},
        {14, 501, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {15, 501, 1, 15, 15, -1, -1, -1, -1, -1, -1, -1, 15, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {16, 500, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 12, -1, -1, -1, -1, -1, -1, -1, -1},
        {17, 500, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 18, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {18, 501, 9, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {19, 500, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 20, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {20, 501, 9, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {21, 501, 9, -1, -1, -1, -1, -1, -1, -1, -1, 22, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {22, 501, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {23, 501, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {24, 501, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {25, 501, 8, -1, -1, -1, -1, -1, -1 - 1, -1, -1, 26, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {26, 501, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {27, 501, 13, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {28, 501, 14, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {29, 501, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {30, 501, 12, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {31, 501, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {32, 501, 16, -1, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {33, 501, 17, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {34, 501, 18, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {35, 501, 19, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {36, 501, 20, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}
    };
    //El estado en el que esta el automata y la columna caracter en la que se encuentra     //Posicion en el vector en el que fue encontrada la palabra a identificar como token
    int estado = 0, entradaActual = 0, inicioCadena = 0, finCadena, contComillaAbierta, fila = 1, columna = 0, columnaActual;
    //Token actual identificado //Todda la entrada que recibira el analisis 
    String tokenActual = "", entrada;
    //Si veo un caracter vacio, no lo proceso //Si hay comilla abierta, acepto el caracter vacio hasta que se vuelva a cerrar
    boolean caracterVacio;
    //Caracteres de la entrada
    char[] caracteres;
    //Lista de palabras reservadas
    ArrayList<String[]> tabla = new ArrayList<>();
    boolean error = false;

    //Recibe el index del caracter y regresa la entrada actual
    public boolean estadoSiguienteEsFinDeCadena(int indexCaracter) {
        if (indexCaracter < caracteres.length) {


            int entradaTemp, estadoSig;
            entradaTemp = -10;
            if (caracteres[indexCaracter] != 10) {
                System.out.print(indexCaracter + "->automata siguiente caracter actual: ->" + caracteres[indexCaracter] + "<- ");
            }
            if (caracteres[indexCaracter] == '%') {
                //%
                entradaTemp = 5;
            } else if (caracteres[indexCaracter] == '*') {
                //*
                entradaTemp = 6;
            } else if (caracteres[indexCaracter] == '+') {
                //+
                entradaTemp = 7;
            } else if (caracteres[indexCaracter] == '-') {
                //-
                entradaTemp = 8;
            } else if (caracteres[indexCaracter] == '/') {
                // /
                entradaTemp = 9;
            } else if (caracteres[indexCaracter] == '=') {
                // =
                entradaTemp = 10;
            } else if (caracteres[indexCaracter] == 'e') {
                // e
                entradaTemp = 11;
            } else if (caracteres[indexCaracter] == 'E') {
                // E
                entradaTemp = 12;
            } else if (caracteres[indexCaracter] == '&') {
                //&
                entradaTemp = 13;
            } else if (caracteres[indexCaracter] == '|') {
                // |
                entradaTemp = 14;
            } else if (caracteres[indexCaracter] == '!') {
                // ¡ 
                System.out.println("!");
                entradaTemp = 15;
            } else if (caracteres[indexCaracter] == '<') {
                // <
                entradaTemp = 16;
            } else if (caracteres[indexCaracter] == '>') {
                // >
                entradaTemp = 17;
            } else if (caracteres[indexCaracter] == '"') {
                //"
                //contComillaAbierta%2=0 cerrada -> contComillaAbierta%2=1 abierta
                entradaTemp = 18;
            } else if (caracteres[indexCaracter] == '\'') {
                // '
                entradaTemp = 19;
            } else if (caracteres[indexCaracter] == '.') {
                //.
                entradaTemp = 20;
            } else if (caracteres[indexCaracter] == ';') {
                //;
                entradaTemp = 21;
            } else if (caracteres[indexCaracter] == '(') {
                //;
                entradaTemp = 22;
            } else if (caracteres[indexCaracter] == ')') {
                //;
                entradaTemp = 23;
            } else if (caracteres[indexCaracter] == '{') {
                //;
                entradaTemp = 24;
            } else if (caracteres[indexCaracter] == '}') {
                //;
                entradaTemp = 25;
            } else if (caracteres[indexCaracter] == ',') {
                //,
                entradaTemp = 26;
            } else if (caracteres[indexCaracter] == ':') {
                //,
                entradaTemp = 27;
            } else if (Character.isLetter(caracteres[indexCaracter])) {
                //Letras
                entradaTemp = 2;
                System.out.print("letra ");
            } else if (Character.isDigit(caracteres[indexCaracter])) {
                //Digitos
                entradaTemp = 3;
                System.out.print("digito ");
            } else if (caracteres[indexCaracter] > 32 && caracteres[indexCaracter] < 176) {
                //Caracteres
                entradaTemp = 4;
                System.out.print("caracter numero: " + caracteres[indexCaracter]);
            } else if (caracteres[indexCaracter] == ' ' && contComillaAbierta % 2 == 0) {
                System.out.print("cadena vacia\n");
            } else if ((caracteres[indexCaracter] == 10) && contComillaAbierta % 2 == 0) {
                System.out.print("salto de linea\n");
            } else if (caracteres[indexCaracter] == ' ' && contComillaAbierta % 1 == 0) {
                entradaTemp = 5;
                System.out.print("cadena vacia\n");
            } else {
                System.err.println("Contiene simbolos no reconocidos:  " + indexCaracter + " -> :)" + (char) (caracteres[indexCaracter]) + "<-");
            }
            System.out.print("Buscando estado siguiente: " + estado + "," + (entradaTemp + 1));
            boolean si;
            try {
                estadoSig = automata[estado][entradaTemp + 1];
                System.out.println(" ->el estado siguiente es: " + estadoSig);
                si = automata[estadoSig][1] == 501;
            } catch (Exception e) {
                return false;
            }
            return si;
        }
        return false;
    }

    public ArrayList<String[]> iniciarAnalisisLexico(boolean imprimeTabla) {
        for (int i = 0; i < caracteres.length; i++) {
            if (caracteres[i] != 10) {
                System.out.print(i + " caracter actual: " + caracteres[i] + " ");
            }
            if (caracteres[i] == '%') {
                //%
                entradaActual = 5;
                this.columna++;
            } else if (caracteres[i] == '*') {
                //*
                entradaActual = 6;
                this.columna++;
            } else if (caracteres[i] == '+') {
                //+
                entradaActual = 7;
                this.columna++;
            } else if (caracteres[i] == '-') {
                //-
                entradaActual = 8;
                this.columna++;
            } else if (caracteres[i] == '/') {
                // /
                entradaActual = 9;
                this.columna++;
            } else if (caracteres[i] == '=') {
                // =
                entradaActual = 10;
                this.columna++;
            } else if (caracteres[i] == 'e') {
                // e
                entradaActual = 11;
                this.columna++;
            } else if (caracteres[i] == 'E') {
                // E
                entradaActual = 12;
                this.columna++;
            } else if (caracteres[i] == '&') {
                //&
                entradaActual = 13;
                this.columna++;
            } else if (caracteres[i] == '|') {
                // |
                entradaActual = 14;
                this.columna++;
            } else if (caracteres[i] == 33) {
                // ¡ 
                entradaActual = 15;
                this.columna++;
            } else if (caracteres[i] == '<') {
                // <
                entradaActual = 16;
                this.columna++;
            } else if (caracteres[i] == '>') {
                // >
                entradaActual = 17;
                this.columna++;
            } else if (caracteres[i] == '"') {
                //"
                this.contComillaAbierta++; //contComillaAbierta%2=0 cerrada -> contComillaAbierta%2=1 abierta
                entradaActual = 18;
                this.columna++;
            } else if (caracteres[i] == '\'') {
                // '
                entradaActual = 19;
                this.columna++;
            } else if (caracteres[i] == '.') {
                //.
                entradaActual = 20;
                this.columna++;
            } else if (caracteres[i] == ';') {
                //;
                entradaActual = 21;
                this.columna++;
            } else if (caracteres[i] == '(') {
                //(
                entradaActual = 22;
                this.columna++;
            } else if (caracteres[i] == ')') {
                //)
                entradaActual = 23;
                this.columna++;
            } else if (caracteres[i] == '{') {
                //{
                entradaActual = 24;
                this.columna++;
            } else if (caracteres[i] == '}') {
                //}
                entradaActual = 25;
                this.columna++;
            } else if (caracteres[i] == ',') {
                entradaActual = 26;
                this.columna++;
            } else if (caracteres[i] == ':') {
                entradaActual = 27;
                this.columna++;
            } else if (Character.isLetter(caracteres[i])) {
                //Letras
                entradaActual = 2;
                this.columna++;
                System.out.print("letra ");
            } else if (Character.isDigit(caracteres[i])) {
                //Digitos
                entradaActual = 3;
                this.columna++;
                System.out.print("digito ");
            } else if (caracteres[i] > 32 && caracteres[i] < 176) {
                //Caracteres
                entradaActual = 4;
                this.columna++;
                System.out.print("caracter numero: " + (int) caracteres[i]);
            } else if (caracteres[i] == ' ' && contComillaAbierta % 2 == 0) {
                inicioCadena++;
                this.columna++;
                System.out.print("cadena vacia\n");
                caracterVacio = true;
            } else if (caracteres[i] == 10 && contComillaAbierta % 2 == 0) {
                inicioCadena++;
                this.columna++;
                System.out.print("salto de linea\n");
                this.fila++;
                this.columna = 0;
                caracterVacio = true;
            } else if (caracteres[i] == ' ' && contComillaAbierta % 1 == 0) {
                //Si esta la comilla abierta y viene un espacio
                this.entradaActual = 5;
                this.columna++;
                System.out.print("cadena vacia\n");
            } else if(caracteres[i] == 9){
                //tabulador
                caracterVacio = true;
                inicioCadena++;
                //this.columna = 0;
            }else{
                System.err.println("Contiene simbolos no reconocidos:  " + i + " ->:)" + (int) caracteres[i] + "<-");
                error = true;
                this.muestraError(1, fila, columna, caracteres[i]);
                break;
            }

            if (!caracterVacio) {
                try {
                    System.out.println("\n||| automata-> estado actual: " + estado + ", caracter: " + (entradaActual + 1) + ": " + caracteres[i]);
                    estado = automata[estado][entradaActual + 1];
                } catch (Exception e) {
                    System.err.println("Contiene cadenas no aceptadas por el automata");
                    error = true;
                    this.muestraError(2, fila, columna, caracteres[i]);
                    break;
                }
                try {
                    System.out.println("i vale: " + i + "carac.length: " + caracteres.length + " esCaracterSeparador(" + caracteres[i + 1] + "): " + esCaracterSeparador(caracteres[i + 1]) + " estoy en estado final? automata[" + estado + "][" + 1 + "]==511?: " + (automata[estado][1] == 501) + " estado: " + estado + " automata[" + estado + "][" + 1 + "]=" + automata[estado][1]);
                } catch (Exception e) {
                    try {
                        System.out.println("i vale: " + i + "carac.length: " + caracteres.length + " esCaracterSeparador(" + caracteres[i] + "): " + esCaracterSeparador(caracteres[i]) + " estoy en estado final? automata[" + estado + "][" + 1 + "]==511?: " + (automata[estado][1] == 501) + " estado: " + estado + " automata[" + estado + "][" + 1 + "]=" + automata[estado][1]);
                    } catch (Exception f) {
                    }
                }
                if (i != caracteres.length - 1) {
                    System.out.println("\nEs estado automatasiguiente 501: " + this.estadoSiguienteEsFinDeCadena(i + 1));
                } else {
                    System.out.println("No existe caracter siguiente");
                }
                try {
                    if ((i < caracteres.length - 1 && esCaracterSeparador(caracteres[i + 1]) && automata[estado][1] == 501 && !estadoSiguienteEsFinDeCadena(i + 1) && i != caracteres.length - 1)
                            || (esCaracterSeparador(caracteres[i]) && (automata[estado][1] == 501) && caracteres[i] != ' ' && !estadoSiguienteEsFinDeCadena(i + 1) && i != caracteres.length - 1)
                            || (i == caracteres.length - 1 && automata[estado][1] == 501)) {
                        this.finCadena = i;
                        //Calcular fila: columna Xes el final, columnaToken=columna-(finCadena-inicioCadena);
                        //System.out.println("colToken: columna: "+columna+" finCadena: "+finCadena+" inicioCadena: "+inicioCadena+" colCalculada: "+(columna-(finCadena-inicioCadena)));
                        this.columnaActual = (columna - (finCadena - inicioCadena));
                        for (int j = inicioCadena; j < finCadena + 1; j++) {
                            this.tokenActual += caracteres[j];
                        }
                        this.inicioCadena = finCadena + 1;
                        tokenIdentificado(automata[estado][2], tokenActual, fila, columnaActual);
                        tokenActual = "";
                        estado = 0;
                        entradaActual = 0;
                    } else if (automata[estado][1] == 500 && caracteres[i + 1] == ' ') {
                        //JOptionPane.showMessageDialog(null, "Aqui");
                    }
                } catch (Exception e) {
                    //e.printStackTrace();
                    error = true;
                    this.muestraError(3, fila, columna, caracteres[i]);
                    break;
                }
            }
            caracterVacio = false;
        }
        if (imprimeTabla) {
            imprimeTabla();
        }
        return tabla;
    }

    public AnalisisLexico(String entrada) {
        //this.caracteres = this.eliminarEspaciosEnBlanco(entrada).toCharArray();
        this.caracteres = entrada.toCharArray();

    }

    public void imprimeTabla() {
        if (!error) {
            JFrame frame = new JFrame("Tabla de Simbolos");
            String datosTabla[][] = new String[tabla.size()][4];
            String columnas[] = {"Lexema", "Token", "Fila", "Columna"};
            if (!error) {
                System.out.println(" __________________________________________\n|________________TABLA_____________________|");
                for (int i = 0; i < tabla.size(); i++) {
                    datosTabla[i][0] = tabla.get(i)[0];
                    datosTabla[i][1] = tabla.get(i)[1];
                    datosTabla[i][2] = tabla.get(i)[2];
                    datosTabla[i][3] = tabla.get(i)[3];
                    System.out.println(tabla.get(i)[0] + " " + tabla.get(i)[1] + " fila: " + tabla.get(i)[2] + " columna: " + tabla.get(i)[3] + " id: " + tabla.get(i)[4]);
                }
            } else {
                //muestraError();
            }
            JTable table = new JTable(datosTabla, columnas);
            table.setFillsViewportHeight(true);
            JScrollPane scrollPane = new JScrollPane(table);
            frame.add(scrollPane);
            frame.setSize(500, 500);
            frame.setVisible(true);

        } else {
        }
    }

    public void muestraError(int idError, int fila, int columna, char caracter) {
        System.out.println("ERROR, NO SE PUDO IDENTIFICAR: ORIGEN DEL ERROR: " + idError + " EN FILA:" + fila + " COLUMNA: " + columna);
        JOptionPane.showMessageDialog(null, "Error lexico en la fila " + fila + " columna " + columna + " error cerca del caracter: '" + caracter + "'");
    }

    public String eliminarEspaciosEnBlanco(String sTexto) {
        String sCadenaSinBlancos = "";
        StringTokenizer stTexto = new StringTokenizer(sTexto);

        while (stTexto.hasMoreElements()) {
            sCadenaSinBlancos += stTexto.nextElement();
        }
        return sCadenaSinBlancos;
    }

    public boolean esCaracterSeparador(char car) {
        for (int i = 0; i < separadores.length; i++) {
            if (car == separadores[i]) {
                //System.out.println("EL SEPARADOR ES->" + separadores[i] + "<-");
                return true;
            }
        }
        return false;
    }

    //Recibe como entrada la cadena que fue identificada como final
    public String tokenIdentificado(int id, String cad, int fila, int columna) {
        int tempTokenId = 0;
        if (automata[estado][2] == 1) {
            tempTokenId = 0;
            //Si es un supuesto ID comparo con mis palabras reservadas
            for (int i = 0; i < palabrasReservadas.length; i++) {
                if (cad.equals(palabrasReservadas[i])) {
                    tempTokenId = i + this.numTokensId;
                }
            }

        } else {
            tempTokenId = id - 1;
        }
        System.out.println("--->ENCONTRADO EL TOKEN " + tokenActual + " CON ID: " + automata[estado][2]);
        tabla.add(new String[]{cad, tokens[tempTokenId].toString(), "" + fila, "" + columna, "" + tempTokenId, "TD"});
        //Manejar los tokens que son IDS
        //Aqui hago comparacion de ID y regreso token correcto
        return tokens[id - 1];
    }
}
