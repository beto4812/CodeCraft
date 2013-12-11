/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador;

import gui.Ed;
import java.util.ArrayList;
import java.util.Stack;
import java.util.StringTokenizer;
import org.apache.commons.lang3.ArrayUtils;

/**
 *
 * @author Alberto
 */
public class CodigoIntermedio {

    Ed ed;
    //String codigoExpandido = "";
    int i = 0;
    ArrayList<String[]> nuevaTabla = null;
    String codigoIntermedio = "";
    int contVarTemp = 1;
    ArrayList<String[]> etiquetas = new ArrayList<>(); //"Nombre"//"Numero"
    int numeroEtiquetas = 1;
    int produccionesCont = 1;
    ////Generando ensablador
    String segmento_data;
    String segmento_code;

    public CodigoIntermedio(Ed ed) {

        this.ed = ed;

    }

    public void iniciarCodigoIntermedio(ArrayList<String[]> entrada) {
        switch (i) {
            case 0:
        }

        System.out.println("///////////////////////////////////////////////////////////////////////////////////////////////////////////////////\n/////////////////LLAMANDO AL CODIGO INTERMEDIO///////////////////////////////////////////////////////////////////////\n///////////////////////////////////////////////////////////////////////////////////////////////////////////////////");
        String codigoExpandidio = expandirCodigo(entrada);
        AnalisisLexico an = new AnalisisLexico(codigoExpandidio);
        //Obtenemos una nueva tabla de simbolos y sobre ella trabajamos el codigo intermedio
        nuevaTabla = an.iniciarAnalisisLexico(true);
        //Llamo el analisis sintactico para comprobar que mis expansiones son correctas (solo para encontrar errores)
        /*
         AnalisisSintactico aS = new AnalisisSintactico(nuevaTabla, ed);
         aS.iniciarAnalisisSintactico();*/
        //en base a la nueva tabla expando las expresiones
        this.traducirCodigoIntermedio();
    }

    public String expandirCodigo(ArrayList<String[]> entrada) {
        String codigoExpandido = "";
        try {
            i = 0;
            if (entrada.isEmpty()) {
                return "";
            }
            System.out.println("INICIANDO EXPANSION: size" + entrada.size());
            for (; i < entrada.size(); i++) {
                System.out.println("entrada: " + entrada.get(i)[0]);
                //for
                switch (entrada.get(i)[0]) {
                    case "for":
                        System.out.println("FOR");
                        codigoExpandido += expandirFor(i, entrada) + "\n";
                        break;
                    case "switch":
                        System.out.println("SWITCH");
                        codigoExpandido += expandirSwitch(i, entrada) + "\n";
                        break;
                    case "declare":
                        System.out.println("DECLARE");
                        codigoExpandido += expandirDeclare(i, entrada, true) + "\n";
                        break;
                    default:
                        codigoExpandido += entrada.get(i)[0] + " ";
                        break;
                }
            }
            System.out.println("EXPANSION FINAL:\n\n\n" + codigoExpandido);
            System.out.println("\nTERMINADA EXPANSION");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return codigoExpandido;
    }

    /**
     * Si hay un declare con n>2 declaraciones lo convierto en n declaraciones
     * /Separa la declaracion y la asignacion (falta)
     *
     * @return
     */
    public String expandirDeclare(int i, ArrayList<String[]> entrada, boolean editarVariable) {
        String tipoDato;
        ArrayList<String> declaraciones = new ArrayList<>();
        ArrayList<Boolean> declaracionAsignada = new ArrayList<>();
        boolean declaracionAsignadaBool = false;
        int nDeclaraciones = 0;
        String declaracionTemp = "";
        boolean declaracionAsignadaTemp = false;
        String expansion = "";
        for (int j = i + 1;; j++) {
            System.out.println("dec voy: " + entrada.get(j)[0]);
            if (entrada.get(j)[0].equals("=")) {
                declaracionAsignadaBool = true;
            }

            if (entrada.get(j)[0].equals(",") || entrada.get(j)[0].equals("as")) {
                //busco el conjunto de declaraciones 
                System.out.println("declaracion: " + declaracionTemp + " asignada: " + declaracionAsignadaBool);
                declaracionAsignada.add(declaracionAsignadaBool);
                declaraciones.add(declaracionTemp);
                nDeclaraciones++;
                if (entrada.get(j)[0].equals(",")) {
                    j++;
                }
                //j++;
                declaracionTemp = "";
                declaracionAsignadaBool = false;
            }
            declaracionTemp += entrada.get(j)[0];
            //cuando encuentro el as ya tengo el tipo de Dato
            if (entrada.get(j)[0].equals("as")) {
                System.out.println("");
                //busco el tipo de dato
                tipoDato = entrada.get(j + 1)[0];
                if (editarVariable) {
                    this.i = j + 2;
                }
                break;
            }
        }
        //Construyo declaraciones
        System.out.println("NDec" + declaraciones.size());
        for (int j = 0; j < declaraciones.size(); j++) {
            if (declaracionAsignada.get(j)) {
                String declaracion;
                String asignacion;
                //separo la declaracion
                StringTokenizer tokens = new StringTokenizer(declaraciones.get(j), "=");
                declaracion = tokens.nextToken();
                System.out.println("declaracion: " + declaracion);
                asignacion = tokens.nextToken();
                System.out.println("asignacion: " + asignacion);
                expansion += "declare " + declaracion + " as " + tipoDato + ";\n";
                expansion += declaracion + "=" + asignacion + ";" + "\n";
            } else {
                expansion += "declare " + declaraciones.get(j) + " as " + tipoDato + ";\n";
            }
        }
        System.out.println("\n\n\nExpansion: " + expansion + "\n\n\n");
        return expansion;

    }

    public String expandirFor(int i, ArrayList<String[]> entrada) {
        String expansion = "";
        System.out.println("for encontrado");
        boolean termine = false;
        ArrayList<String[]> decArray = new ArrayList<>();
        String dec = "", cond = "", asig = "", cuerpo = "", cuerpoFinal = "";
        int llaveAbierta = 1; //hay una llave abierta, cada que encuentro una ++
        int llaveCerrada = 0; //Aun no cierra la llave
        for (int j = i; termine == false; j++) {
            System.out.println("voy en: " + entrada.get(j)[0]);
            if ("(".equals(entrada.get(j)[0])) {
                for (int k = j + 1;; k++) {
                    if (termine) {
                        break;
                    }
                    if (";".equals(entrada.get(k)[0])) {
                        System.out.println("declaracion encontrado: " + dec + " voy en: " + entrada.get(k)[0]);
                        for (int l = k + 1;; l++) {
                            if (termine) {
                                break;
                            }
                            if (";".equals(entrada.get(l)[0])) {
                                System.out.println("cond encontrado: " + cond + " voy en: " + entrada.get(l)[0]);
                                for (int m = l + 1;; m++) {
                                    if (termine) {
                                        break;
                                    }
                                    if (")".equals(entrada.get(m)[0])) {
                                        System.out.println("asig encontrado: " + asig + " voy en: " + entrada.get(l)[0]);
                                        ArrayList<String[]> cuerpo2 = new ArrayList<>();
                                        cuerpo = "";
                                        llaveAbierta = 1;
                                        llaveCerrada = 0;
                                        for (int n = m + 2;; n++) {
                                            //Aqui busco el cuerpo
                                            if (entrada.get(n)[0].equals("{")) {
                                                System.out.println("añadiendo llave abierta");
                                                llaveAbierta++;
                                            } else if (entrada.get(n)[0].equals("}")) {
                                                //System.out.println("añadiendo llave cerrada");
                                                llaveCerrada++;
                                            }
                                            System.out.println("llaveAbierta: " + llaveAbierta + ", llaveCerrada: " + llaveCerrada);
                                            if (llaveAbierta - llaveCerrada == 0) {
                                                System.out.println("cuerpo a expandir: " + cuerpo);
                                                cuerpoFinal = this.expandirCodigo(cuerpo2);
                                                termine = true;

                                                this.i = n;
                                                break;
                                                //
                                            }
                                            //System.out.println("añadiendo a cuerpo: " + entrada.get(n)[0]);
                                            cuerpo2.add(new String[]{entrada.get(n)[0]});
                                            cuerpo += entrada.get(n)[0] + " ";
                                        }
                                        break;
                                    }
                                    asig += entrada.get(m)[0];
                                }
                                break;
                            }
                            cond += entrada.get(l)[0];
                        }
                        break;
                    }
                    decArray.add(new String[]{entrada.get(k)[0]});
                    dec += entrada.get(k)[0] + " ";
                }
            }
        }
        System.out.println("for expandido-> dec:" + dec + " cond: " + cond + " asig: " + asig + "cuerpo: " + cuerpo);
        decArray.add(new String[]{";"});
        System.out.println("mandando a expandir declaracion :" + this.imprimirLista(decArray));
        expansion += this.expandirDeclare(0, decArray, false);
        expansion += "while(" + cond + "){" + cuerpoFinal + " " + asig + " ;}";
        System.out.println("expansion: " + expansion);
        return expansion;
    }

    public String expandirSwitch(int i, ArrayList<String[]> entrada) {
        String var = entrada.get(i + 2)[0];
        System.out.println("var identificada: " + entrada.get(i + 2)[0]);
        ArrayList<Integer> condCases = new ArrayList<>();
        ArrayList<String> condCuerpo = new ArrayList<>();
        ArrayList<Boolean> breakActivo = new ArrayList<>();
        ArrayList<String[]> cuerpoTemp;
        String cuerpoTempString;
        String cuerpoFinal = "";
        String cadenaExpandida = "";
        int llaveAbierta = 1; //hay una llave abierta, cada que encuentro una ++
        int llaveCerrada = 0; //Aun no cierra la llave
        boolean caseCerro = false;
        //encontrar los cases
        for (int j = i;; j++) {
            if (caseCerro) {
                this.i = j;
                break;
            }
            System.out.println("voy en: " + entrada.get(j)[0]);
            if (entrada.get(j)[0].equals("case")) {
                boolean banderaBreak = false;
                cuerpoTempString = "";
                cuerpoTemp = new ArrayList<>();
                //añado el condCase
                System.out.println("añadiendo a condCases: " + Integer.parseInt(entrada.get(j + 1)[0]));
                condCases.add(Integer.parseInt(entrada.get(j + 1)[0]));
                for (int k = j + 3;; k++) {
                    switch (entrada.get(k)[0]) {
                        case "{":
                            llaveAbierta++;
                            break;
                        case "}":
                            llaveCerrada++;
                            break;
                    }
                    //for para buscar el cuerpo
                    //si llaveAbierta-llaveCerrada=1 estan emparejados los switchs de adentro
                    //System.out.println("llaveAbierta: " + llaveAbierta + "llaveCerrada: " + llaveCerrada);
                    if ((llaveAbierta - llaveCerrada == 1 && entrada.get(k)[0].equals("case")) || llaveAbierta - llaveCerrada == 0) {
                        if (llaveAbierta - llaveCerrada == 0) {
                            caseCerro = true;
                        }
                        j = k - 1;
                        //se acabo el cuerpo
                        break;
                    }
                    //Si encuentro el break no lo añado al cuerpo. ++ para saltarme el ;. Añado bandera a la lista de breaks
                    if (entrada.get(k)[0].equals("break")) {
                        banderaBreak = true;
                        k++;
                    } else {
                        cuerpoTemp.add(new String[]{entrada.get(k)[0]});
                        cuerpoTempString += entrada.get(k)[0];
                        //System.out.println("añadiendo a cuerpo: " + entrada.get(k)[0]);
                    }

                }
                if (banderaBreak) {
                    breakActivo.add(true);
                } else {
                    breakActivo.add(false);
                }
                cuerpoFinal = this.expandirCodigo(cuerpoTemp);
                condCuerpo.add(cuerpoFinal);
                System.out.println("termine de analizar case, cuerpo encontrado: " + cuerpoFinal);
            }

        }
        //Aqui termino el switch construyo los if-else
        boolean primerIf = true;
        for (int j = 0; j < condCases.size(); j++) {
            String cuerpoTempCascada = "";
            //si el escalon no tiene break añado el cuerpo de abajo hasta encontrar un break
            if (!breakActivo.get(j)) {
                for (int k = j; k < condCases.size(); k++) {
                    cuerpoTempCascada += condCuerpo.get(k);
                    if (breakActivo.get(j)) {
                        break;
                    }
                }
            } else {
                cuerpoTempCascada = condCuerpo.get(j);
            }

            if (primerIf) {
                cadenaExpandida += "if(" + var + "==" + condCases.get(j) + "){\n" + cuerpoTempCascada + "}";
                primerIf = false;
            } else {
                cadenaExpandida += "else{ if(" + var + "==" + condCases.get(j) + "){\n" + cuerpoTempCascada + "}}";
            }
        }

        System.out.println("cadena expandida: " + cadenaExpandida);
        return cadenaExpandida;
    }

    public String expandirElse(int i, ArrayList<String[]> entrada) {
        String expansion = "";
        if (entrada.get(0 + 1).equals("else")) {
            //Si expande
        } else {
            //Regreso todo igual
            for (int j = i; j < entrada.size(); j++) {
                expansion += entrada.get(j)[0] + " ";
            }
        }
        return expansion;
    }

    public void traducirCodigoIntermedio() {
        System.out.println("////////////INICIANDO TRADUCCION");
        String codigoIntermedio = "";
        //tengo que separar las sentencias en producciones completas enviarle por partes un o un while, cuando encuentre una la añado
        //a un arraylist y corro el contador hasta la proxima expresion
        //hasta este punto solo existen:
        //declare: declare
        //asignacion: =
        //if: if
        //if_else: buscar cuerpo completo para saber si tiene else
        //while: while
        ArrayList<String[]> sentencia;
        int parAbierto;
        int parCerrado;
        for (int j = 0; j < nuevaTabla.size(); j++) {
            System.out.println("estoy en: " + nuevaTabla.get(j)[0]);
            switch (nuevaTabla.get(j)[0]) {
                case "declare":
                    System.out.println("j: " + j);
                    sentencia = new ArrayList<>();
                    for (int k = j;; k++) {
                        sentencia.add(new String[]{nuevaTabla.get(k)[0]});
                        if (nuevaTabla.get(k)[0].equals(";")) {
                            j = k;
                            break;
                        }
                    }
                    System.out.println("DECLARE ENCONTRADO: " + this.imprimirLista(sentencia));
                    codigoIntermedio += P(sentencia, "P");
                    break;
                case "=":
                    sentencia = new ArrayList<>();
                    for (int k = j - 1;; k++) {
                        sentencia.add(new String[]{nuevaTabla.get(k)[0]});
                        if (nuevaTabla.get(k)[0].equals(";")) {
                            j = k;
                            break;
                        }
                    }
                    System.out.println("ASIGNACION ENCONTRADA: " + this.imprimirLista(sentencia));
                    codigoIntermedio += P(sentencia, "P");
                    break;
                case "if":
                    parAbierto = 0;
                    parCerrado = 0;
                    sentencia = new ArrayList<>();
                    for (int k = j;; k++) {
                        switch (nuevaTabla.get(k)[0]) {
                            case "{":
                                parAbierto++;
                                break;
                            case "}":
                                parCerrado++;
                                break;
                        }
                        sentencia.add(new String[]{nuevaTabla.get(k)[0]});
                        //System.out.println("agregando sentencia if: " + nuevaTabla.get(k)[0] + " pA: " + parAbierto + "pC: " + parCerrado);
                        if ((parAbierto - parCerrado == 0) && nuevaTabla.get(k)[0].equals("}")) {
                            System.out.println("ENTRE");
                            if (k + 1 < nuevaTabla.size() && nuevaTabla.get(k + 1)[0].equals("else")) {
                                //continua agregando
                                parAbierto = 0;
                                parCerrado = 0;
                                for (int l = k + 1;; l++) {
                                    switch (nuevaTabla.get(l)[0]) {
                                        case "{":
                                            parAbierto++;
                                            break;
                                        case "}":
                                            parCerrado++;
                                            break;
                                    }
                                    sentencia.add(new String[]{nuevaTabla.get(l)[0]});
                                    //System.out.println("agregando sentencia else: " + nuevaTabla.get(l)[0] + " pA: " + parAbierto + "pC: " + parCerrado);
                                    if (parAbierto - parCerrado == 0 && nuevaTabla.get(l)[0].equals("}")) {
                                        System.out.println("BREAK");
                                        j = l;
                                        break;
                                    }
                                }
                                break;
                            }
                            j = k;
                            break;
                        }
                    }
                    System.out.println("IF ENCONTRADA: " + this.imprimirLista(sentencia));
                    codigoIntermedio += P(sentencia, "P");
                    break;
                case "while":
                    parAbierto = 0;
                    parCerrado = 0;
                    sentencia = new ArrayList<>();
                    for (int k = j;; k++) {
                        switch (nuevaTabla.get(k)[0]) {
                            case "{":
                                parAbierto++;
                                break;
                            case "}":
                                parCerrado++;
                                break;
                        }
                        sentencia.add(new String[]{nuevaTabla.get(k)[0]});
                        //System.out.println("agregando sentencia while: " + nuevaTabla.get(k)[0] + " pA: " + parAbierto + "pC: " + parCerrado);
                        if ((parAbierto - parCerrado == 0) && nuevaTabla.get(k)[0].equals("}")) {
                            j = k;
                            break;
                        }
                    }
                    System.out.println("WHILE ENCONTRADA: " + this.imprimirLista(sentencia));
                    codigoIntermedio += P(sentencia, "P");
                    break;
                case "print":
                    codigoIntermedio += "CODIGO PRINT\n";
                    j = j + 2;
                    break;
                case "read":
                    codigoIntermedio += "CODIGO READ\n";
                    j = j + 2;
                    break;

            }
        }
        System.out.println("//////////////////////////CODIGO FINAL:::::\n" + codigoIntermedio + "\n\n/////////////////////////::::::::::::::::\n");
        //this.P(nuevaTabla);
    }

    public String enviarProducciones(ArrayList<String[]> codigoEntrada, String prefijo) {
        String codigoIntermedio = "";
        ArrayList<String[]> sentencia;
        int parAbierto;
        int parCerrado;
        for (int j = 0; j < codigoEntrada.size(); j++) {
            System.out.println("estoy en: " + codigoEntrada.get(j)[0]);
            switch (codigoEntrada.get(j)[0]) {
                case "declare":
                    System.out.println("j: " + j);
                    sentencia = new ArrayList<>();
                    for (int k = j;; k++) {
                        sentencia.add(new String[]{codigoEntrada.get(k)[0]});
                        if (codigoEntrada.get(k)[0].equals(";")) {
                            j = k;
                            break;
                        }
                    }
                    System.out.println("DECLARE ENCONTRADO: " + this.imprimirLista(sentencia));
                    codigoIntermedio += P(sentencia, prefijo + "P");
                    break;
                case "=":
                    sentencia = new ArrayList<>();
                    for (int k = j - 1;; k++) {
                        sentencia.add(new String[]{codigoEntrada.get(k)[0]});
                        if (codigoEntrada.get(k)[0].equals(";")) {
                            j = k;
                            break;
                        }
                    }
                    System.out.println("ASIGNACION ENCONTRADA: " + this.imprimirLista(sentencia));
                    codigoIntermedio += P(sentencia, prefijo + "P");
                    break;
                case "if":
                    parAbierto = 0;
                    parCerrado = 0;
                    sentencia = new ArrayList<>();
                    for (int k = j;; k++) {
                        switch (codigoEntrada.get(k)[0]) {
                            case "{":
                                parAbierto++;
                                break;
                            case "}":
                                parCerrado++;
                                break;
                        }
                        sentencia.add(new String[]{codigoEntrada.get(k)[0]});
                        //System.out.println("agregando sentencia if: " + codigoEntrada.get(k)[0] + " pA: " + parAbierto + "pC: " + parCerrado);
                        if ((parAbierto - parCerrado == 0) && codigoEntrada.get(k)[0].equals("}")) {
                            System.out.println("ENTRE");
                            if (k + 1 < codigoEntrada.size() && codigoEntrada.get(k + 1)[0].equals("else")) {
                                //continua agregando
                                parAbierto = 0;
                                parCerrado = 0;
                                for (int l = k + 1;; l++) {
                                    switch (codigoEntrada.get(l)[0]) {
                                        case "{":
                                            parAbierto++;
                                            break;
                                        case "}":
                                            parCerrado++;
                                            break;
                                    }
                                    sentencia.add(new String[]{codigoEntrada.get(l)[0]});
                                    //System.out.println("agregando sentencia else: " + codigoEntrada.get(l)[0] + " pA: " + parAbierto + "pC: " + parCerrado);
                                    if (parAbierto - parCerrado == 0 && codigoEntrada.get(l)[0].equals("}")) {
                                        System.out.println("BREAK");
                                        j = l;
                                        break;
                                    }
                                }
                                break;
                            }
                            j = k;
                            break;
                        }
                    }
                    System.out.println("IF ENCONTRADA: " + this.imprimirLista(sentencia));
                    codigoIntermedio += P(sentencia, prefijo + "P");
                    break;
                case "while":
                    parAbierto = 0;
                    parCerrado = 0;
                    sentencia = new ArrayList<>();
                    for (int k = j;; k++) {
                        switch (codigoEntrada.get(k)[0]) {
                            case "{":
                                parAbierto++;
                                break;
                            case "}":
                                parCerrado++;
                                break;
                        }
                        sentencia.add(new String[]{codigoEntrada.get(k)[0]});
                        //System.out.println("agregando sentencia while: " + codigoEntrada.get(k)[0] + " pA: " + parAbierto + "pC: " + parCerrado);
                        if ((parAbierto - parCerrado == 0) && codigoEntrada.get(k)[0].equals("}")) {
                            j = k;
                            break;
                        }
                    }
                    System.out.println("WHILE ENCONTRADA: " + this.imprimirLista(sentencia));
                    codigoIntermedio += P(sentencia, prefijo + "P");
                    break;
            }
        }
        System.out.println("//////////////////////////CODIGO FINAL:::::\n" + codigoIntermedio + "\n\n/////////////////////////::::::::::::::::\n");
        //this.P(nuevaTabla);        
        return codigoIntermedio;
    }

    public String nuevaEtiqueta(String nombre) {
        System.out.println("añadiendo etiqueta: " + nombre + ": " + numeroEtiquetas);
        this.etiquetas.add(new String[]{nombre, "" + numeroEtiquetas});
        numeroEtiquetas++;
        return nombre;
    }

    /**
     *
     * crea una nueva etiqueta con un id ya existente
     *
     * @param nombre
     * @param id
     * @return
     */
    public String nuevaEtiqueta(String nombre, int id) {
        System.out.println("añadiendo etiqueta: " + nombre + ": " + id);
        this.etiquetas.add(new String[]{nombre, "" + id});
        return nombre;
    }

    public int getIdEtiqueta(String nombre) {
        //System.out.println("busqueda de etiqueta: " + nombre);
        for (int j = 0; j < etiquetas.size(); j++) {
            //System.out.println("comparando: "+etiquetas.get(j)[0]);
            if (etiquetas.get(j)[0].equals(nombre)) {
                return Integer.parseInt(etiquetas.get(j)[1]);
            }

        }
        return -1;

    }

    public boolean contieneLista(ArrayList<String[]> lista, String cad) {
        System.out.println("buscando: " + cad);
        for (int j = 0; j < lista.size(); j++) {
            if (lista.get(j)[0].equals(cad)) {
                return true;
            }
        }
        return false;
    }

    public String nuevaLinea() {

        return "\n";
    }

    /**
     * Recibe el nombre de la etiqueta y la regresa en modo de codigo intermedio
     * 100:
     *
     * @param nombre
     */
    public String imprimirEtiqueta(String nombre) throws Exception {
        if (getIdEtiqueta(nombre) == -1) {
            throw new Exception("No se pudo imprimir etiqueta->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>><");
            //return "-1";
        }
        return this.getIdEtiqueta(nombre) + ": ";
    }

    public String imprimirLista(ArrayList<String[]> codigoEntrada) {
        String salida = "";

        for (int j = 0; j < codigoEntrada.size(); j++) {
            salida += codigoEntrada.get(j)[0] + " ";
        }
        return salida;
    }

    /**
     * Todas las producciones reciben una lista de la tabla sintactica y
     * regresan un string del codigo intermedio
     *
     * @param codigo
     * @return
     */
    public String P(ArrayList<String[]> codigoEntrada, String prefijo) {
        System.out.println("empezando produccion, recibi codigo: " + this.imprimirLista(codigoEntrada));
        String codigoSalida = "";
        String etiqS = nuevaEtiqueta(prefijo + produccionesCont + "S");
        codigoSalida += S(codigoEntrada, etiqS);
        codigoSalida += nuevaLinea();
        try {
            codigoSalida += imprimirEtiqueta(etiqS) + "\n";
        } catch (Exception e) {
            System.out.println("--->NO PUDE IMPRIMIR ETIQUETA: " + etiqS + "S" + " EN P" + " etiq creada: " + etiqS);
        }

        System.out.println("\n\n\nP Cuerpo final de: " + prefijo + "/P\n" + codigoSalida);
        produccionesCont++;
        return codigoSalida;
    }

    /**
     *
     * @param codigoEntrada
     * @param prefijo el string de las llamadas que an ocurrido antes de mi
     * @return
     */
    public String S(ArrayList<String[]> codigoEntrada, String prefijo) {
        //el sufijo es el identificador
        //Ya que tengo el codigo de entrada decido a que produccion S_* lo enviare
        ArrayList<String[]> S1;
        switch (codigoEntrada.get(0)[0]) {
            case "if":
                System.out.println("encontrado IF");
                S1 = new ArrayList<>();
                int llaveAbierta = 1;
                int llaveCerrada = 0;
                int cuerpoTermina = 0;
                for (int j = 2;; j++) {
                    if (codigoEntrada.get(j)[0].equals("}")) {
                        //System.out.println("llave abierta");
                        llaveAbierta++;
                    } else if (codigoEntrada.get(j)[0].equals("}")) {
                        //System.out.println("llave cerrada");
                        llaveCerrada++;
                    }
                    if (codigoEntrada.get(j)[0].equals(")")) {
                        if (codigoEntrada.get(j)[0].equals("}")) {
                            //System.out.println("llave abierta");
                            llaveAbierta++;
                        } else if (codigoEntrada.get(j)[0].equals("}")) {
                            //System.out.println("llave cerrada");
                            llaveCerrada++;
                        }
                        //ya tengo la comparacion
                        for (int k = j + 2;; k++) {
                            if (codigoEntrada.get(k)[0].equals("{")) {
                                //System.out.println("llave abierta");
                                llaveAbierta++;
                            } else if (codigoEntrada.get(k)[0].equals("}")) {
                                //System.out.println("llave cerrada");

                                llaveCerrada++;
                            }
                            if (llaveAbierta - llaveCerrada == 0) {
                                //ya tengo el cuerpo
                                cuerpoTermina = k;
                                break;
                            }
                            System.out.println("añadiendo a cuerpo if: " + codigoEntrada.get(k)[0]);
                            S1.add(new String[]{codigoEntrada.get(k)[0]});
                        }
                        break;
                    }
                }
                System.out.println("cuerpo S1 Final: " + this.imprimirLista(S1));
                //verifico si lo envio al if_else o al if
                if (cuerpoTermina + 1 >= codigoEntrada.size()) {
                    //Es final, por lo tanto no tiene else
                    return this.S_if(codigoEntrada, S1, prefijo);
                } else {
                    if (codigoEntrada.get(cuerpoTermina + 1)[0].equals("else")) {
                        ArrayList<String[]> S2 = new ArrayList<>();
                        //busco el cuerpo del else
                        llaveAbierta = 1;
                        llaveCerrada = 0;
                        for (int k = cuerpoTermina + 3;; k++) {
                            System.out.println("comprobando else: " + codigoEntrada.get(k)[0]);
                            if (codigoEntrada.get(k)[0].equals("{")) {
                                //System.out.println("llave abierta");
                                llaveAbierta++;
                            } else if (codigoEntrada.get(k)[0].equals("}")) {
                                //System.out.println("llave cerrada");
                                llaveCerrada++;
                            }
                            if (llaveAbierta - llaveCerrada == 0) {
                                //ya tengo el cuerpo
                                break;
                            }
                            //System.out.println("añadiendo a cuerpo: " + codigoEntrada.get(k)[0]);
                            S2.add(new String[]{codigoEntrada.get(k)[0]});
                        }
                        System.out.println("cuerpo S2 else: " + this.imprimirLista(S2));
                        return this.S_if_else(codigoEntrada, S1, S2, prefijo);
                        //produccion if/else //busca cuerpo del else
                    } else {
                        return this.S_if(codigoEntrada, S1, prefijo);
                        //produccion if //enviarle cuerpo
                    }
                    //System.out.println("buscando else en: " + codigoEntrada.get(cuerpoTermina + 1)[0]);
                }
            case "while":
                System.out.println("WHILE");
                return S_while(codigoEntrada, prefijo);
            case "declare":
                return "codigo declaracion :" + this.imprimirLista(codigoEntrada);
            default:
                //Es una asignacion //verificar funciones
                //S_assign
                return S_assign(codigoEntrada, prefijo);
        }

        //return "CODIGO NO DISPONIBLE";
    }

    /**
     * falta codigo
     *
     * @param codigoEntrada
     * @param prefijo
     * @return
     */
    public String S_assign(ArrayList<String[]> codigoEntrada, String prefijo) {
        String entrada = this.imprimirLista(codigoEntrada);
        String codigoSalida = "";
        int nTemps = 1;
        int a = 1, t = 1;
        boolean aActivo = false, tActivo = true;
        String ultimoA = "", ultimoT = "";
        //divido todo lo que esta despues del igual
        StringTokenizer tokens = new StringTokenizer(entrada, "=");
        String var = tokens.nextToken();
        System.out.println("var: " + var);
        String expr = tokens.nextToken();
        expr = expr.replace(";", "");
        System.out.println("asignacion: " + expr);
        Stack<String> pila1 = new Stack<>();
        Stack<String> pila2 = new Stack<>();
        int temp1 = -1, temp2 = -1;
        String ultimoPeek = "";
        String ultimoAsignado = "";
        String tokensVector[];
        int cont = 0;

        String st = InfixPostfix4.ejecutar(expr);
        StringTokenizer tokens2 = new StringTokenizer(st, " ");


        if (tokens2.countTokens() > 1) {
            tokensVector = new String[tokens2.countTokens()];
            cont = tokens2.countTokens();
            //System.out.println("//Sin reversa");
            int count = 0;
            while (tokens2.hasMoreTokens()) {
                tokensVector[count] = tokens2.nextToken();
                //System.out.println(tokensVector[count]);
                count++;
            }
            ArrayUtils.reverse(tokensVector);
            //System.out.println("PILA: ");
            for (String tokensVector1 : tokensVector) {
                //System.out.println(tokensVector1);
                pila1.push(tokensVector1);
            }
            int o = 0;
            while (pila1.size() != 0) {
                int idTemp = -1;
                ultimoPeek = pila1.peek();
                //System.out.println("ULTIMO PILA: " + pila1.peek() + " size: " + pila1.size() + " size2: " + pila2.size());
                if (pila1.peek().equals("-") || pila1.peek().equals("+") || pila1.peek().equals("*") || pila1.peek().equals("/")) {
                    //Operador //agarro los ultimos 2 de la pila y los opero
                    System.out.println("op2: " + pila2.peek());
                    String op2 = pila2.pop();
                    System.out.println("op1: " + pila2.peek());
                    String op1 = pila2.pop();
                    //Se usan en orden: t1, t2 y despues a1, a2
                    String codigoSalidaTemp = "";
                    String oper = op1 + pila1.peek() + op2;
                    System.out.println("t" + nTemps + "=" + oper);
                    //codigoSalida += "t" + nTemps + "=" + oper + "\n";
                    if (tActivo) {
                        System.out.println("Entro a t");
                        ///TTTT
                        a = 1;
                        codigoSalidaTemp = "t" + t + "=" + op1 + pila1.peek() + op2 + "\n";//Operacion real
                        codigoSalidaTemp = CodigoEnsamblador.asignaciones("t" + t, op1, pila1.peek(), op2);
                        ultimoAsignado = "t" + t;
                        //verificar si la operacion no es un id ya
                        if (op1.contains("t") || op1.contains("a") || op2.contains("t") || op2.contains("a")) {
                            //Si contiene variables cambio de temp
                            codigoSalidaTemp = "a" + a + "=" + op1 + pila1.peek() + op2 + "\n";//Operacion real
                            codigoSalidaTemp = CodigoEnsamblador.asignaciones(("a" + a), op1, pila1.peek(), op2);
                            ultimoAsignado = "a" + a;
                            pila2.push("a" + a);
                            //System.out.println("metiendo a pila2: ");
                            tActivo = false;
                            a++;
                        } else {
                            pila2.push("t" + t);
                        }
                        t++;
                    } else {
                        System.out.println("entro a A");
                        //AAAA
                        t = 1;
                        codigoSalidaTemp = "a" + a + "=" + op1 + pila1.peek() + op2 + "\n"; //Aqui intercepto y realizo la operacion real
                        codigoSalidaTemp = CodigoEnsamblador.asignaciones("a" + a, op1, pila1.peek(), op2);
                        ultimoAsignado = "a" + a;
                        if (op1.contains("a") || op1.contains("t") || op2.contains("a") || op2.contains("t")) {
                            codigoSalidaTemp = "t" + t + "=" + op1 + pila1.peek() + op2 + "\n"; //Aqui intercepto y realizo la operacion real
                            codigoSalidaTemp = CodigoEnsamblador.asignaciones("t" + t, op1, pila1.peek(), op2);
                            ultimoAsignado = "t" + t;
                            pila2.push("t" + t);
                            tActivo = true;
                            t++;
                        } else {
                            pila2.push("a" + a);
                        }
                        a++;
                    }
                    codigoSalida += codigoSalidaTemp;
                    System.out.println("codigoSalidaTemp: " + codigoSalidaTemp);
                    pila1.pop();
                } else {
                    //Numero o temporal
                    pila2.push(pila1.peek());
                    pila1.pop();

                }
                o++;
            }
            //codigoSalida+=(";"+var+"="+ultimoAsignado);
            codigoSalida += CodigoEnsamblador.asignacionVar(var, ultimoAsignado);
            // System.err.println("ultimo pila: "+ultimoPeek+"SALI DE LOOP ");
        }
        /*
         System.out.println("cont vale: " + cont);
         if (cont == 0) {
         codigoSalida = this.imprimirLista(codigoEntrada).replace(";", "").replace("\n", "");

         } else {
         codigoSalida += var + "=t" + (nTemps - 1);
         }*/


        System.out.println("Cuerpo final de: " + prefijo + "/S_assign->\n" + codigoSalida);
        return codigoSalida;
    }

    /**
     *
     * @param codigoEntrada
     * @param S1
     * @param prefijo
     * @return
     */
    public String S_if(ArrayList<String[]> codigoEntrada, ArrayList<String[]> S1, String prefijo) {
        System.out.println("llamado: " + prefijo + "/S_if");
        //Etiquetas
        nuevaEtiqueta(prefijo + "B.true");
        nuevaEtiqueta(prefijo + "B.false", this.getIdEtiqueta(prefijo));
        nuevaEtiqueta(prefijo + "S1", this.getIdEtiqueta(prefijo));
        //Buscar B local
        ArrayList<String[]> B = new ArrayList<>();
        for (int j = 2;; j++) {
            if (codigoEntrada.get(j)[0].equals(")")) {
                break;
            }
            B.add(new String[]{codigoEntrada.get(j)[0]});
        }
        System.out.println("B: " + this.imprimirLista(B));
        //Codigo
        String codigoSalida = "";
        codigoSalida += B(B, prefijo + "B") + "\n";
        try {
            codigoSalida += imprimirEtiqueta(prefijo + "B.true") + "\n";
        } catch (Exception e) {
            System.out.println("--->NO PUDE IMPRIMIR ETIQUETA" + prefijo + "B.true");
        }
        if (S1.size() > 0) {
            codigoSalida += this.enviarProducciones(S1, prefijo) + "\n";
        }
        System.out.println("Cuerpo final de: " + prefijo + "/S_if->\n" + codigoSalida);
        return codigoSalida;
    }

    public String S_if_else(ArrayList<String[]> codigoEntrada, ArrayList<String[]> S1, ArrayList<String[]> S2, String prefijo) {
        //Etiquetas
        nuevaEtiqueta(prefijo + "B.true");
        nuevaEtiqueta(prefijo + "B.false");
        nuevaEtiqueta(prefijo + "S1", this.getIdEtiqueta(prefijo));
        nuevaEtiqueta(prefijo + "S2", this.getIdEtiqueta(prefijo));
        //Buscar B local
        ArrayList<String[]> B = new ArrayList<>();
        for (int j = 2;; j++) {
            if (codigoEntrada.get(j)[0].equals(")")) {
                break;
            }
            B.add(new String[]{codigoEntrada.get(j)[0]});
        }
        System.out.println("B: " + this.imprimirLista(B));
        //Codigo
        String codigoSalida = "";
        codigoSalida += B(B, prefijo + "B") + "\n";
        try {
            codigoSalida += this.imprimirEtiqueta(prefijo + "B.true") + "\n";
        } catch (Exception e) {
            System.out.println("--->NO PUDE IMPRIMIR ETIQUETA: " + prefijo + "B.true");
        }
        if (S1.size() > 0) {
            codigoSalida += this.enviarProducciones(S1, prefijo) + "\n";
        }
        codigoSalida += "goto " + this.getIdEtiqueta(prefijo) + "\n";
        try {
            codigoSalida += this.imprimirEtiqueta(prefijo + "B.false") + "\n";
        } catch (Exception e) {
            System.out.println("--->NO PUDE IMPRIMIR ETIQUETA: " + prefijo + "B.false");
        }
        if (S1.size() > 0) {
            codigoSalida += this.enviarProducciones(S2, prefijo) + "\n";
        }
        System.out.println("Cuerpo final de: " + prefijo + "/S_if_else->\n" + codigoSalida);
        return codigoSalida;
    }

    public String S_while(ArrayList<String[]> codigoEntrada, String prefijo) {
        //Buscando el cuerpo del while
        ArrayList<String[]> S1 = new ArrayList<>();
        //busco el cuerpo del else
        int llaveAbierta = 1;
        int llaveCerrada = 0;
        ArrayList<String[]> B = new ArrayList<>();
        //Busco B
        for (int j = 2;; j++) {
            if (codigoEntrada.get(j)[0].equals(")")) {
                //Busco S1
                for (int k = j + 2;; k++) {
                    if (codigoEntrada.get(k)[0].equals("{")) {
                        //System.out.println("llave abierta");
                        llaveAbierta++;
                    } else if (codigoEntrada.get(k)[0].equals("}")) {
                        //System.out.println("llave cerrada");
                        llaveCerrada++;
                    }
                    if (llaveAbierta - llaveCerrada == 0) {
                        //ya tengo el cuerpo
                        break;
                    }
                    //System.out.println("añadiendo a cuerpo: " + codigoEntrada.get(k)[0]);
                    S1.add(new String[]{codigoEntrada.get(k)[0]});
                }
                System.out.println("cuerpo S1 Final: " + this.imprimirLista(S1));
                break;
            }
            System.out.println("añadiendo a b: " + codigoEntrada.get(j)[0]);
            B.add(new String[]{codigoEntrada.get(j)[0]});
        }
        System.out.println("B encontrado: " + this.imprimirLista(B));
        //Etiquetas
        this.nuevaEtiqueta(prefijo + "inicio");
        this.nuevaEtiqueta(prefijo + "B.true");
        this.nuevaEtiqueta(prefijo + "B.false", this.getIdEtiqueta(prefijo));
        this.nuevaEtiqueta(prefijo + "S1", this.getIdEtiqueta(prefijo + "inicio"));
        //Codigo
        String codigoSalida = "";
        try {
            codigoSalida += this.imprimirEtiqueta(prefijo + "inicio") + "\n";
        } catch (Exception e) {
            System.out.println("--->NO PUDE IMPRIMIR ETIQUETA: " + prefijo + "inicio");
        }
        codigoSalida += B(B, prefijo + "B") + "\n";
        try {
            codigoSalida += this.imprimirEtiqueta(prefijo + "B.true") + "\n";
        } catch (Exception e) {
            System.out.println("--->NO PUDE IMPRIMIR ETIQUETA: " + prefijo + "B.true");
        }
        if (S1.size() > 0) {
            codigoSalida += this.enviarProducciones(S1, prefijo) + "";
        }
        codigoSalida += "goto " + this.getIdEtiqueta(prefijo + "inicio");
        System.out.println("Cuerpo final de: " + prefijo + "/S_while->\n" + codigoSalida);
        return codigoSalida;
    }

    public String B(ArrayList<String[]> codigoEntrada, String prefijo) {
        String codigoSalida = "CODIGO DE B";
        ArrayList<String[]> B1Local = new ArrayList<>();
        ArrayList<String[]> B2Local = new ArrayList<>();
        //redireciona B al metodo correcto
        if (contieneLista(codigoEntrada, "&&") || contieneLista(codigoEntrada, "||")) {
            //contiene B's anidadas
            //Agarro la primera B y la segunda B la envio a B
            //var1>=20 && var<10 && var>10
            //    B      (       B        )
            String divisor = "";
            for (int j = 0;; j++) {
                if (codigoEntrada.get(j)[0].equals("&&") || codigoEntrada.get(j)[0].equals("||")) {
                    if (codigoEntrada.get(j)[0].equals("&&")) {
                        divisor = "&&";
                    } else if (codigoEntrada.get(j)[0].equals("||")) {
                        divisor = "||";
                    }
                    for (int k = j + 1; k < codigoEntrada.size(); k++) {
                        B2Local.add(new String[]{codigoEntrada.get(k)[0]});
                    }
                    break;
                }
                B1Local.add(new String[]{codigoEntrada.get(j)[0]});
            }
            System.out.println("Dividiendo B's ---- B1: " + this.imprimirLista(B1Local) + " B2: " + this.imprimirLista(B2Local));
            //
            switch (divisor) {
                case "&&":
                    codigoSalida = B_B1ANDB2(B1Local, B2Local, prefijo);
                    break;
                case "||":
                    codigoSalida = B_B1ORB2(B1Local, B2Local, prefijo);
                    break;
            }
        } else {
            //No contiene B's anidadas se llama B_E1RELE2
            codigoSalida = B_E1RELE2(codigoEntrada, prefijo);

        }
        return codigoSalida;
    }

    public String B_B1ORB2(ArrayList<String[]> B1, ArrayList<String[]> B2, String prefijo) {
        String codigoSalida = "";
        this.nuevaEtiqueta(prefijo + "B1.true", this.getIdEtiqueta(prefijo + ".true"));
        this.nuevaEtiqueta(prefijo + "B1.false");
        this.nuevaEtiqueta(prefijo + "B2.true", this.getIdEtiqueta(prefijo + ".true"));
        this.nuevaEtiqueta(prefijo + "B2.false", this.getIdEtiqueta(prefijo + ".false"));
        codigoSalida += B(B1, prefijo + "B1") + "\n";
        try {
            codigoSalida += this.imprimirEtiqueta(prefijo + "B1.false") + "\n";
        } catch (Exception e) {
            System.out.println("--->NO PUDE IMPRIMIR ETIQUETA: " + prefijo + "B1.false");
        }
        codigoSalida += B(B2, prefijo + "B2") + "\n";
        System.out.println("Codigo final de: " + prefijo + "/B_B1ORB2->\n" + codigoSalida);
        return codigoSalida;
    }

    public String B_B1ANDB2(ArrayList<String[]> B1, ArrayList<String[]> B2, String prefijo) {
        String codigoSalida = "";
        this.nuevaEtiqueta(prefijo + "B1.true");
        this.nuevaEtiqueta(prefijo + "B1.false", this.getIdEtiqueta(prefijo + ".false"));
        this.nuevaEtiqueta(prefijo + "B2.true", this.getIdEtiqueta(prefijo + ".true"));
        this.nuevaEtiqueta(prefijo + "B2.false", this.getIdEtiqueta(prefijo + ".false"));
        codigoSalida += B(B1, prefijo + "B1") + "\n";
        try {
            codigoSalida += this.imprimirEtiqueta(prefijo + "B1.true") + "\n";

        } catch (Exception e) {
            System.out.println("--->NO PUDE IMPRIMIR ETIQUETA" + prefijo + "B1.true");
        }
        codigoSalida += B(B2, prefijo + "B2") + "\n";
        System.out.println("Codigo final de: " + prefijo + "/B_B1ORB2->\n" + codigoSalida);
        return codigoSalida;
    }

    public String B_E1RELE2(ArrayList<String[]> codigoEntrada, String prefijo) {
        String codigoSalida = "if " + codigoEntrada.get(0)[0] + " " + codigoEntrada.get(1)[0] + " " + codigoEntrada.get(2)[0] + " goto " + this.getIdEtiqueta(prefijo + ".true") + " \n"
                + "goto " + this.getIdEtiqueta(prefijo + ".false");
        System.out.println("Codigo final de: " + prefijo + "/B_E1RELE2->\n" + codigoSalida);
        return codigoSalida;
    }
}