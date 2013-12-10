/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador;

import gui.Ed;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Stack;
import java.util.StringTokenizer;
import org.apache.commons.lang3.ArrayUtils;

/**
 *
 * @author Alberto
 */
public class AnalisisSemantico {

    Ed ed;
    ArrayList<String[]> analisisLexico = new ArrayList<>();
    ArrayList<String[]> listaDeclaraciones = new ArrayList<>(); //0Contiene la variable , 1El tipo de dato declarado, 2Ya fue asignado?
    ArrayList<String[]> listaErrores = new ArrayList<>();  //0Tipo Error{1:VariableYaDeclarada 2:VariableNoDeclarada 3:VariableNoAsignada 4:TiposIncorrecto}  ,1Variable referenciada, 2Fila, 3Columna
    int tablaOperadores[][] = {
        /*int, double*/
        /*int*/{0, 1},
        /*doubl*/ {1, 1}
    };

    public AnalisisSemantico(ArrayList<String[]> analisisLexico, Ed ed) {

        this.ed = ed;
        this.analisisLexico = analisisLexico;

    }

    public void imprimirDatos() {
        System.out.println("//Lista de declaraciones: ");
        for (int i = 0; i < listaDeclaraciones.size(); i++) {
            System.out.println(i + ": " + listaDeclaraciones.get(i)[0] + "," + listaDeclaraciones.get(i)[1] + " inicializada:" + listaDeclaraciones.get(i)[2]);
        }
        if (listaErrores.size() > 0) {
            System.out.println("//Lista de errores: ");
            for (int i = 0; i < listaErrores.size(); i++) {
                System.out.println((i + 1) + ": " + listaErrores.get(i)[0] + "," + listaErrores.get(i)[1] + "," + listaErrores.get(i)[2] + "," + listaErrores.get(i)[3]);
                //Imprime los errores en colores diferentes
                switch (listaErrores.get(i)[0]) {
                    case "1"://Variable YA declarada //AMARILLO
                        ed.subrrallarTexto(Integer.parseInt(listaErrores.get(i)[2]), Integer.parseInt(listaErrores.get(i)[3]), listaErrores.get(i)[1].length(), Color.ORANGE);
                        break;
                    case "2"://Variable NO declarada //AZUL
                        ed.subrrallarTexto(Integer.parseInt(listaErrores.get(i)[2]), Integer.parseInt(listaErrores.get(i)[3]), listaErrores.get(i)[1].length(), Color.BLUE);
                        break;
                    case "3"://Variable NO asignada //VERDE
                        ed.subrrallarTexto(Integer.parseInt(listaErrores.get(i)[2]), Integer.parseInt(listaErrores.get(i)[3]), listaErrores.get(i)[1].length(), Color.GREEN);
                        break;
                    case "4"://Tipos Incorrectos //ROJO
                        ed.subrrallarTexto(Integer.parseInt(listaErrores.get(i)[2]), Integer.parseInt(listaErrores.get(i)[3]), listaErrores.get(i)[1].length(), Color.RED);
                        break;
                }
            }
            ed.jLabelEstado.setForeground(Color.BLACK);
            ed.jLabelEstado.setText("<html>Guia de errores: <font color='orange'>Var ya declarada/</font><font color='blue'>Var no declarada/</font><font color='green'>Var no asignada/</font><font color='red'>Tipos incorrectos</font> </html>");
        } else {
            ed.jLabelEstado.setForeground(Color.DARK_GRAY);
            ed.jLabelEstado.setText("Analisis semantico correcto");
        }

    }

    public void iniciarAnalisisSemantico() {
        System.out.println("//////////////////////INICIANDO ANALISIS SEMANTICO//////////////////////");
        for (int i = 0; i < analisisLexico.size(); i++) {
            revisarDeclaraciones(i);
            revisarAsignaciones(i);
            revisarSwitch(i);
            revisarOperadoresRelacionales(i);
            revisarPrint(i);

        }
        ////
        imprimirDatos();
    }

    public int getNumeroDeErrores() {
        return this.listaErrores.size();
    }

    public void revisarPrint(int i) {
        boolean error = false;
        //Print puede contener cualquier tipo de variables concatendadas, error de tipos si hay algun otro operador o parentesis
        if (analisisLexico.get(i)[0].equals("print")) {
            for (int j = i+2;; j++) {
                if (analisisLexico.get(j)[0].equals(";")) {
                    break;
                }
                if (analisisLexico.get(j)[0].equals("*") || analisisLexico.get(j)[0].equals("/") || analisisLexico.get(j)[0].equals("(") ) {
                    error = true;
                }
            }
        }

        if (error) {
            //ERROR :4: Tipos de datos incorrectos
            this.listaErrores.add(new String[]{"4", analisisLexico.get(i)[0], analisisLexico.get(i)[2], analisisLexico.get(i)[3]});


        }
    }

    public void revisarDeclaraciones(int i) {
        if (analisisLexico.get(i)[0].equals("declare")) {
            String tipoDato = "";
            //Busco el tipo de dato y lo asigno a una lista de declaraciones y tipos, las asignaciones se mandan llamar solitas
            for (int j = i + 1;; j++) {
                if (analisisLexico.get(j)[0].equals("as")) {
                    tipoDato = analisisLexico.get(j + 1)[0];
                    break;
                }
            }
            //
            for (int j = i + 1;; j++) {
                if (analisisLexico.get(j)[0].equals("as")) {
                    break;
                }
                System.out.println("analizando: " + analisisLexico.get(j)[0]);
                if (!isDeclarada(analisisLexico.get(j)[0])) {
                    //Añado todas las variables a declarar al tipo correspondiente y reviso si ya fueron declaradas anteriormente
                    this.listaDeclaraciones.add(new String[]{analisisLexico.get(j)[0], tipoDato, "0"});
                    System.out.println("Añadido a lista de declaraciones. " + analisisLexico.get(j)[0] + ".SIGUIENTE: " + analisisLexico.get(j + 1)[0] + " tipoDato: " + tipoDato);
                } else {
                    System.out.println("Error ya declarada: " + analisisLexico.get(j)[0]);
                    this.listaErrores.add(new String[]{"1", analisisLexico.get(j)[0], analisisLexico.get(j)[2], analisisLexico.get(j)[3]});
                    //ERROR: ID: 1 : Variable ya declarada
                }
                switch (analisisLexico.get(j + 1)[0]) {
                    case ",":
                        j++;
                        //Avanzo
                        break;
                    case "=":
                        //Se analizan los tipos de datos de las asignaciones (con el mismo metodo creado) para registrar el error en el lugar correcto
                        for (int k = j;; k++) {
                            if (analisisLexico.get(k)[0].equals(",")) {
                                break;
                            } else if (analisisLexico.get(k)[0].equals("as")) {
                                j--;
                                break;
                            }
                            j++;
                        }
                }
            }
        }

    }

    public boolean isDeclarada(String entrada) {
        for (int i = 0; i < listaDeclaraciones.size(); i++) {
            if (listaDeclaraciones.get(i)[0].equals(entrada)) {
                return true;
            }
        }
        return false;
    }

    public boolean isAsignada(String entrada) {
        for (int i = 0; i < listaDeclaraciones.size(); i++) {
            if (listaDeclaraciones.get(i)[0].equals(entrada) && listaDeclaraciones.get(i)[2].equals("1")) {
                return true;
            }
        }
        return false;
    }

    public void setAsignada(String entrada) {
        for (int i = 0; i < listaDeclaraciones.size(); i++) {
            if (listaDeclaraciones.get(i)[0].equals(entrada)) {
                listaDeclaraciones.get(i)[2] = "1";
            }
        }
    }

    public boolean isEnTablaDeErrores(String var[]) {
        for (int i = 0; i < this.listaErrores.size(); i++) {
            if (listaErrores.get(i)[1].equals(var[0]) && listaErrores.get(i)[2].equals(var[2]) && listaErrores.get(i)[3].equals(var[3])) {
                return true;
            }
        }
        return false;
    }

    public void revisarAsignaciones(int i) {
        boolean errorString = false;
        boolean errorBoolean = false;
        boolean errorInt = false;
        boolean asignandoString = false;
        boolean asignandoBoolean = false;
        boolean encontradaExpresionBooleana = false;
        if (analisisLexico.get(i)[0].equals("=")) {
            String variableAsignada = analisisLexico.get(i - 1)[0];
            System.out.println("revisando variable asignacion: " + analisisLexico.get(i - 1)[0]);
            if ("string".equals(this.getTipoDato(analisisLexico.get(i - 1)[0]))) {
                System.out.println("\nes un string");
                asignandoString = true;
            } else if ("boolean".equals(this.getTipoDato(analisisLexico.get(i - 1)[0]))) {
                System.out.println("\nes un boolean");
                asignandoBoolean = true;
            }
            //Antes hay que buscar del lado izquierdo del igual. Que ya este declarada
            int varSwitch = 0;
            int varC1 = 0;
            switch (varSwitch) {


            }
            if (!isEnTablaDeErrores(analisisLexico.get(i - 1))) {
                if (isDeclarada(analisisLexico.get(i - 1)[0])) { //Si ya esta declarada la que voy a asignar
                    //Continuo
                    System.out.println("no esta en tabla de errores y declarada: " + analisisLexico.get(i - 1)[0]);
                    String expresion = "";
                    boolean error = false;
                    for (int j = i + 1;; j++) {
                        if (analisisLexico.get(j)[0].equals(";") || analisisLexico.get(j)[0].equals(",") || analisisLexico.get(j)[0].equals("as") || analisisLexico.get(j)[0].equals("{")) {//condicion de salida de la declaracion
                            if (analisisLexico.get(j)[0].equals("{")) {
                                //Si hago el break con { quito el ultimo parentesis que cierra )por que la expresion esta dentro del parentesis que cierra del for
                                System.out.println("expresion antes: " + expresion);
                                String reverse = new StringBuffer(expresion).reverse().toString();

                                int indx = reverse.indexOf(")");
                                if (indx == 0) {
                                    String fin = reverse.substring(1, reverse.length());
                                    System.out.println("3expresion despues: " + fin);
                                    reverse = new StringBuffer(fin).reverse().toString();
                                    expresion = reverse;
                                }/* else {
                                 reverse = new StringBuffer(reverse).reverse().toString();
                                 expresion = reverse;
                                 }*/
                                System.out.println("expresion final: " + expresion);

                            }
                            break;
                        }
                        expresion += analisisLexico.get(j)[0];
                        //verifico que tipo de dato voy a comparar
                        System.out.println("tipos de datos: " + analisisLexico.get(j)[1]);
                        if ("tkn_id".equals(analisisLexico.get(j)[1]) && asignandoString) {
                            System.out.println("encontrado tkn_id en la asignacion: " + analisisLexico.get(j)[0]);
                            //
                            if (!this.getTipoDato(analisisLexico.get(j)[0]).equals("string")) {
                                errorString = true;
                                System.out.println("variable no es tipo string");
                            }

                        } else if ("tkn_id".equals(analisisLexico.get(j)[1]) && asignandoBoolean) {
                            if (!this.getTipoDato(analisisLexico.get(j)[0]).equals("boolean")) {
                                errorBoolean = true;
                                System.out.println("variable no es tipo string");
                            } else {
                                encontradaExpresionBooleana = true;
                            }
                        } else if ("tkn_num".equals(analisisLexico.get(j)[1]) && asignandoString) {
                            errorString = true;
                        } else if ("tkn_true".equals(analisisLexico.get(j)[1])) {
                            errorString = true;
                            encontradaExpresionBooleana = true;
                            System.out.println("encontro bool");
                        } else if ("tkn_false".equals(analisisLexico.get(j)[1])) {
                            errorString = true;
                            encontradaExpresionBooleana = true;
                            System.out.println("encontro bool");
                        }
                        //if(analisisLexico.get(j)[0].) //si es un numero, un operador o un parentesis me salto la verificacion de declaracion y asignacion
                        if (!(analisisLexico.get(j)[1].equals("tkn_num") || analisisLexico.get(j)[1].equals("tkn_+") || analisisLexico.get(j)[1].equals("tkn_-") || analisisLexico.get(j)[1].equals("tkn_*") || analisisLexico.get(j)[1].equals("tkn_/") || analisisLexico.get(j)[1].equals("tkn_(") || analisisLexico.get(j)[1].equals("tkn_)") || analisisLexico.get(j)[1].equals("tkn_string") || analisisLexico.get(j)[1].equals("tkn_true") || analisisLexico.get(j)[1].equals("tkn_false"))) {
                            System.out.println("entro arriba");
                            if (!isDeclarada(analisisLexico.get(j)[0])) {
                                error = true;
                                //ERROR: 2 (No declarada)
                                this.listaErrores.add(new String[]{"2", analisisLexico.get(j)[0], analisisLexico.get(j)[2], analisisLexico.get(j)[3]});
                                //Se añade error de variable no declarada //bandera para no evaluar expresion
                            } else {
                                //ERROR: 3 (No asignada)
                                if (!isAsignada(analisisLexico.get(j)[0])) {
                                    error = true;
                                    this.listaErrores.add(new String[]{"3", analisisLexico.get(j)[0], analisisLexico.get(j)[2], analisisLexico.get(j)[3]});
                                    //Se añade error de no asignada //bandera para no evaluar expresion
                                }
                            }
                        } else if ((analisisLexico.get(j)[1].equals("tkn_+") || analisisLexico.get(j)[1].equals("tkn_-") || analisisLexico.get(j)[1].equals("tkn_*") || analisisLexico.get(j)[1].equals("tkn_/") || analisisLexico.get(j)[1].equals("tkn_(") || analisisLexico.get(j)[1].equals("tkn_)")) && asignandoBoolean) {
                            System.out.println("entro a error de boolean");
                            errorBoolean = true;
                        }
                    }
                    //cuando termino evaluo la expresion de tipos compatibles si no contiene ningun error
                    if (!error) {
                        //evaluo tipo de dato y verifico si es correcto
                        System.out.println("paso pruebas asignacion de: " + analisisLexico.get(i - 1)[0] + " expresion: " + expresion);


                        //si analisisLexico.get(i - 1)[0] es string buscar que la expresion no contenga ningun otro tipo de dato mas que string
                        boolean correcto = true;
                        char expresionChars[] = expresion.toCharArray();
                        if (asignandoString && errorString) {
                            correcto = false;
                        } else {
                            String datoAsignar = "";
                            if (encontradaExpresionBooleana) {
                                if (!errorBoolean) {
                                    datoAsignar = "boolean";
                                } else {
                                    datoAsignar = "booleanIncorrecto";
                                }
                            } else {
                                datoAsignar = this.comprobarExpresion(expresion);
                                System.out.println("resultado comprobar expresion: " + this.comprobarExpresion(expresion));
                            }

                            System.out.println("datoAsignar vale: " + datoAsignar);

                            switch (getTipoDato(analisisLexico.get(i - 1)[0])) {
                                // boolean correcto = true;
                                case "integer":
                                    if (!datoAsignar.equals("integer")) {
                                        correcto = false;
                                    }
                                    break;
                                case "double":
                                    if (!(datoAsignar.equals("integer") || datoAsignar.equals("double"))) {
                                        correcto = false;
                                    }
                                    break;
                                case "string":
                                    if (!(datoAsignar.equals("integer") || datoAsignar.equals("double") || datoAsignar.equals("string"))) {
                                        correcto = false;
                                    }
                                    break;
                                case "boolean":
                                    if (!datoAsignar.equals("boolean")) {
                                        correcto = false;
                                    }
                                    break;
                            }
                        }
                        if (correcto) {
                            System.out.println("tipo de dato correcto seteando asignacion: " + analisisLexico.get(i - 1)[0]);//hago la asignacion
                            this.setAsignada(analisisLexico.get(i - 1)[0]);
                            //this.listaDeclaraciones.get(i - 1)[2] = "1";
                        } else {
                            System.out.println("incorrecto");
                            //ERROR :4: Tipos de datos incorrectos
                            this.listaErrores.add(new String[]{"4", analisisLexico.get(i - 1)[0], analisisLexico.get(i - 1)[2], analisisLexico.get(i - 1)[3]});
                        }
                    }
                } else {
                    //ERROR: 2 (No declarada)
                    this.listaErrores.add(new String[]{"2", analisisLexico.get(i - 1)[0], analisisLexico.get(i - 1)[2], analisisLexico.get(i - 1)[3]});
                    //Se añade error de variable no declarada //bandera para no evaluar expresion

                }
            }
        }

    }

    /**
     * Revisa que la entrada del switch sea siempre integer, tambien revisa que
     * este declarada, asignada y sus tipos de dato en case: //Lo modifique para
     * que se pudieran comparar variables double con integer y visceversa, no se
     * pueden comparar string
     *
     * @param i
     */
    public void revisarSwitch(int i) {
        if (this.analisisLexico.get(i)[0].equals("switch")) {
            //reviso que ya este declarada //asignada //tipos correctos
            System.out.println("encontrado switch analizando variable dentro: " + analisisLexico.get(i + 2)[0]);
            if (this.isDeclarada(this.analisisLexico.get(i + 2)[0])) {
                if (this.isAsignada(this.analisisLexico.get(i + 2)[0])) {
                    if ("integer".equals(this.getTipoDato(this.analisisLexico.get(i + 2)[0]))) {
                    } else {
                        //ERROR: 4 (Tipos incompatibles)
                        this.listaErrores.add(new String[]{"4", analisisLexico.get(i + 2)[0], analisisLexico.get(i + 2)[2], analisisLexico.get(i + 2)[3]});
                    }
                } else {
                    //ERROR: 3 (No asignada)
                    this.listaErrores.add(new String[]{"3", analisisLexico.get(i + 2)[0], analisisLexico.get(i + 2)[2], analisisLexico.get(i + 2)[3]});
                }
            } else {
                //ERROR: 2 (No declarada)
                this.listaErrores.add(new String[]{"2", analisisLexico.get(i + 2)[0], analisisLexico.get(i + 2)[2], analisisLexico.get(i + 2)[3]});
            }
        } else if (this.analisisLexico.get(i)[0].equals("case")) {
            //revisar que lo que este despues del: sea integer
            if (this.analisisLexico.get(i + 1)[0].contains(".")) {
                //ERROR: 4 (Tipos incompatibles)
                this.listaErrores.add(new String[]{"4", analisisLexico.get(i + 1)[0], analisisLexico.get(i + 1)[2], analisisLexico.get(i + 1)[3]});
            }
        }
    }

    /**
     * Revisa que los operadores relacionales usados esten declarados, asignados
     * y sean de tipos compatibles
     *
     * @param i
     */
    public void revisarOperadoresRelacionales(int i) {
        if (analisisLexico.get(i)[1].equals("tkn_op_relacional")) {
            System.out.println("se encontro una comparacion");
            String tipoDato1 = "1", tipoDato2 = "2";
            boolean error1 = false;
            boolean error2 = false;
            //revisar el primer argumento
            //reviso que no sea un numero
            switch (analisisLexico.get(i - 1)[1]) {
                case "tkn_num":
                    if (analisisLexico.get(i - 1)[0].contains(".")) {
                        tipoDato1 = "tkn_numero";
                    } else {
                        tipoDato1 = "tkn_numero";
                    }
                    break;
                case "tkn_string":
                    error1 = true;
                    this.listaErrores.add(new String[]{"4", analisisLexico.get(i - 1)[0], analisisLexico.get(i - 1)[2], analisisLexico.get(i - 1)[3]});
                    break;
                default:
                    if (this.isDeclarada(analisisLexico.get(i - 1)[0])) {
                        if (this.isAsignada(analisisLexico.get(i - 1)[0])) {
                            //tipoDato1 = this.getTipoDato(analisisLexico.get(i - 1)[0]);
                            tipoDato1 = "tkn_numero";
                        } else {
                            this.listaErrores.add(new String[]{"3", analisisLexico.get(i - 1)[0], analisisLexico.get(i - 1)[2], analisisLexico.get(i - 1)[3]});
                            //Se añade error de no asignada //bandera para no evaluar expresion
                            error1 = true;
                        }
                    } else {
                        //ERROR: 2 (No declarada)
                        this.listaErrores.add(new String[]{"2", analisisLexico.get(i - 1)[0], analisisLexico.get(i - 1)[2], analisisLexico.get(i - 1)[3]});
                        error1 = true;
                    }
                    break;
            }
            switch (analisisLexico.get(i + 1)[1]) {
                case "tkn_num":
                    if (analisisLexico.get(i + 1)[0].contains(".")) {
                        tipoDato2 = "tkn_numero";
                    } else {
                        tipoDato2 = "tkn_numero";
                    }
                    break;
                case "tkn_string":
                    error2 = true;
                    this.listaErrores.add(new String[]{"4", analisisLexico.get(i + 1)[0], analisisLexico.get(i + 1)[2], analisisLexico.get(i + 1)[3]});
                    break;
                default:
                    //revisar el segundo argumento
                    //revisar el primer argumento
                    if (this.isDeclarada(analisisLexico.get(i + 1)[0])) {
                        if (this.isAsignada(analisisLexico.get(i + 1)[0])) {
                            //tipoDato2 = this.getTipoDato(analisisLexico.get(i + 1)[0]);
                            tipoDato2 = "tkn_numero";
                        } else {
                            error2 = true;
                            this.listaErrores.add(new String[]{"3", analisisLexico.get(i + 1)[0], analisisLexico.get(i + 1)[2], analisisLexico.get(i + 1)[3]});
                            //Se añade error de no asignada //bandera para no evaluar expresion
                        }
                    } else {
                        error2 = true;
                        //ERROR: 2 (No declarada)
                        this.listaErrores.add(new String[]{"2", analisisLexico.get(i + 1)[0], analisisLexico.get(i + 1)[2], analisisLexico.get(i + 1)[3]});
                    }
                    break;
            }
            if (!tipoDato1.equals(tipoDato2) && !error1 && !error2) {
                //ERROR: 4 Tipos incompatibles
                this.listaErrores.add(new String[]{"4", analisisLexico.get(i - 1)[0], analisisLexico.get(i - 1)[2], analisisLexico.get(i - 1)[3]});
                this.listaErrores.add(new String[]{"4", analisisLexico.get(i + 1)[0], analisisLexico.get(i + 1)[2], analisisLexico.get(i + 1)[3]});
            }
        }

    }

    /**
     *
     * @param lexema: recibe como entrada el lexema
     * @return regresa el tipo de dato de acuerdo a la tabla de tipos: 0 int, 1
     * double, 2 string
     */
    public String getTipoDato(String lexema) {
        for (int i = 0; i < listaDeclaraciones.size(); i++) {
            if (listaDeclaraciones.get(i)[0].equals(lexema)) {
                return listaDeclaraciones.get(i)[1];

            }
        }

        return "";
    }

    /**
     *
     * @param expr: expresion despues del igual
     * @return tipo de dato encontrado
     */
    public String comprobarExpresion(String expr) {
        if (expr.contains("\"")) {
            if (expr.contains("*") || expr.contains("/") || expr.contains("-")) {
                return "stringIncorrecto";
            }
            return "string";
        }

        Stack<String> pila1 = new Stack<>();
        Stack<String> pila2 = new Stack<>();
        int temp1 = -1, temp2 = -1;
        String ultimoPeek = "";

        String st = InfixPostfix4.ejecutar(expr);
        StringTokenizer tokens = new StringTokenizer(st, " ");
        //System.out.println("tokens: " + tokens.countTokens());
        if (tokens.countTokens() > 1) {
            String tokensVector[] = new String[tokens.countTokens()];
            //System.out.println("//Sin reversa");
            int count = 0;
            while (tokens.hasMoreTokens()) {
                tokensVector[count] = tokens.nextToken();
                //System.out.println(tokensVector[count]);
                count++;
            }
            ArrayUtils.reverse(tokensVector);
            //System.out.println("PILA: ");
            for (String tokensVector1 : tokensVector) {
                //.println(tokensVector1);
                pila1.push(tokensVector1);
            }
            int o = 0;
            while (pila1.size() != 0) {
                int idTemp = -1;
                ultimoPeek = pila1.peek();
                //System.out.println("ULTIMO PILA: " + pila1.peek() + " size: " + pila1.size() + " size2: " + pila2.size());
                switch (pila1.peek()) {
                    case "integer":
                        pila1.pop();
                        pila2.push("integer");
                        break;
                    case "double":
                        pila1.pop();
                        pila2.push("double");
                        break;
                    default:
                        idTemp = this.buscarLexema(pila1.peek());
                        //System.out.println("lexema : " + idTemp);
                        switch (this.analisisLexico.get(idTemp)[1]) {
                            case "tkn_num":
                                //analizo que tipo de numero es
                                if (this.analisisLexico.get(idTemp)[0].contains(".")) {
                                    pila1.pop();
                                    pila2.push("double");
                                    //System.out.println("agregando tipo de dato double");
                                } else {
                                    pila1.pop();
                                    pila2.push("integer");
                                    //System.out.println("agregando tipo de dato integer");
                                }
                                break;
                            case "tkn_id":
                                //Un ID
                                pila1.pop();//
                                //necesito saber que tipo de dato es (supongo que ya fue declarado y asignado correctamente)
                                //int tD = ;
                                pila2.push(this.getTipoDato(this.analisisLexico.get(idTemp)[0]));
                                break;
                            default:
                                //System.out.println("operador: " + this.analisisLexico.get(idTemp)[0]);
                                //un operador
                                //System.out.println("peek: " + pila2.peek());
                                switch (pila2.peek()) {
                                    case "double":
                                        temp1 = 1;
                                        pila2.pop();
                                        //System.out.println("temp1: " + temp1);
                                        break;
                                    case "integer":
                                        temp1 = 0;
                                        pila2.pop();
                                        //System.out.println("temp1: " + temp1);
                                        break;
                                }
                                //System.out.println("peek: " + pila2.peek());
                                switch (pila2.peek()) {
                                    case "double":
                                        temp2 = 1;
                                        pila2.pop();
                                        //System.out.println("temp1: " + temp2);
                                        break;
                                    case "integer":
                                        temp2 = 0;
                                        pila2.pop();
                                        //System.out.println("temp2: " + temp2);
                                        break;
                                }
                                try {
                                    int res = this.tablaOperadores[temp1][temp2];
                                    //System.out.println("res: " + res);
                                    pila1.pop();
                                    switch (res) {
                                        case 0:
                                            pila1.push("integer");
                                            break;
                                        case 1:
                                            pila1.push("double");
                                            break;
                                    }
                                } catch (Exception e) {
                                    //
                                    System.err.println("ultimo pila: " + pila1.peek() + " regresando -1");
                                    return "-1";
                                }
                        }
                        break;

                }

            }
            // System.err.println("ultimo pila: "+ultimoPeek+"SALI DE LOOP ");
        } else {
            if (tokens.nextToken().contains(".")) {
                return "double";
            } else {
                return "integer";
            }
        }
        //System.err.println("ultimo pila: "+pila1.peek()+"REGRESANDO 1");
        return ultimoPeek;
        //return tipoDatoFinal;        
    }

    public int buscarLexema(String lex) {

        for (int i = 0; i < analisisLexico.size() - 1; i++) {

            if (analisisLexico.get(i)[0].equals(lex)) {
                return i;
            }
        }
        System.out.println("sail buscar lexema");
        return -1;
    }
}
