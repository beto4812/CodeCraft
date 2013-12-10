/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador;

import java.io.File;
import java.util.ArrayList;
import jxl.Cell;
import jxl.CellType;
import jxl.Sheet;
import jxl.Workbook;

/**
 *
 * @author beto
 */
public class FirstFollow {
    //los NoTerminales <>
    //los termiales tkn_

    ArrayList<String> noTerminales; //A-> (un string)
    ArrayList<String[]> producciones; // -> abA (un vector de Strings)
    ArrayList<String[]> first; //lista de first's
    ArrayList<String[]> follow; //lista de follow's
    int numeroDeFF, numeroDTokens;
    int matriz[][];
    String[] columnas;
    String[] filas;

    public static void main(String[] args) {
        FirstFollow fi = new FirstFollow();
        fi.ejecutarFirstFollow();
    }

    public ArrayList<String[]> getProducciones() {
        return producciones;
    }

    public ArrayList<String> getNoTerminales() {
        return noTerminales;
    }

    public int[][] ejecutarFirstFollow() {

        recogerDatos();
        //recogerDatos();
        //imprimirTabla();
        encontrarFirst();
        encontrarFollow();
        this.imprimirFF();
        this.generarTabla();
        imprimirTablaFinal();
        return matriz;
    }

    public boolean existeTokenEn(String[] ent, String token) {
        try {
            int si = ent.length;
        } catch (Exception e) {
            return false;
        }
        for (int i = 0; i < ent.length; i++) {
            if (ent[i].equals(token)) {
                //System.err.println("token igual encontrado");
                return true;
            }
        }
        return false;
    }

    public int buscarIndexDeNoTerminal(String nT) {
        //busca la posicion de first o follow donde este un NT
        for (int i = 0; i < first.size(); i++) {
            //System.out.println("comparando: " + first.get(i)[0] + " con" + nT);
            if (first.get(i)[0].equals(nT)) {
                return i;
            }
        }
        return -1;
    }

    public int buscarIndexDeTerminal(String t) {
        //busca la posicion de la fila de acuerdo al terminal dado
        for (int i = 0; i < columnas.length; i++) {
            //System.out.println("comparando: " + columnas[i] + " con " + t);
            if (columnas[i].equals(t)) {
                //System.out.println("retorono: " + i);
                return i;
            }
        }
        return -1;
    }

    public void agregarAFirst(String ent, String nT) {
        //System.out.println("agregando first: "+ent+" en pos "+pos);
        //buscar a donde voy a agregar el first
        int pos = buscarIndexDeNoTerminal(nT);
        //System.out.println("indexDAg: " + pos);
        String[] temp = first.get(pos);
        if (!existeTokenEn(temp, ent)) {
            String[] nuev;
            //System.out.println("length de temp: " + temp.length);
            nuev = new String[temp.length + 1];
            System.arraycopy(temp, 0, nuev, 0, temp.length);
            //System.out.println("length de nuev: "+nuev.length);
            nuev[temp.length] = ent;
            first.set(pos, nuev);
        }
    }

    public void agregarAFollow(String ent, String nT) {
        //System.out.println("agregando first: "+ent+" en pos "+pos);
        //buscar a donde voy a agregar el first
        int pos = buscarIndexDeNoTerminal(nT);
        //System.out.println("indexDAg: " + pos);
        String[] temp = follow.get(pos);
        if (!existeTokenEn(temp, ent)) {
            String[] nuev;
            //System.out.println("length de temp: " + temp.length);
            nuev = new String[temp.length + 1];
            System.arraycopy(temp, 0, nuev, 0, temp.length);
            //System.out.println("length de nuev: "+nuev.length);
            nuev[temp.length] = ent;
            follow.set(pos, nuev);
        }
    }

    public void encontrarFirst() {
        //si el primero de la produccion es t, se agrega a First de noTerminales
        System.out.println("size de las producciones: " + producciones.size());
        for (int i = 0; i < producciones.size(); i++) {
            if (this.esT(producciones.get(i)[0])) {
                //System.out.println("encontrado first de: "+i);
                this.agregarAFirst(producciones.get(i)[0], noTerminales.get(i));
            }
        }
        ArrayList<String> temp = new ArrayList<>();
        //si el primero de la produccion es Nt, se agregan los First del Nt

        for (int a = 0; a < 10; a++) {
            for (int i = 0; i < producciones.size(); i++) {
                if (this.esNT(producciones.get(i)[0])) {
                    //busco lo que voy a agregar, se encuentra ya en first
                    //voy a agregar los first de producciones.get(i)[0]
                    //System.out.println("buscando index de: " + producciones.get(i)[0]);
                    int indx = this.buscarIndexDeNoTerminal(producciones.get(i)[0]);
                    for (int j = 1; j < first.get(indx).length; j++) {
                        //temp.add(first.get(indx)[j]);
                        if (this.esT(first.get(indx)[j])) {
                            //System.out.println("intentando agregar: " + first.get(indx)[j] + " a " + noTerminales.get(i)); //es al no terminal del que lo vy a meter
                            //this.agregarAFirst(producciones.get(i)[0], first.get(indx)[j]);
                            this.agregarAFirst(first.get(indx)[j], noTerminales.get(i));
                        }
                    }
                }
            }
        }
    }

    public void encontrarFollow() {
        //recorro producciones, si hay un NT seguido de un NT, agrefo first del de la derecha a follows de la izquierda
        //Incluir produccion inicial
        agregarAFollow("tkn_$", "<cuerpo>");

        for (int i = 0; i < producciones.size(); i++) {
            for (int j = 0; j < producciones.get(i).length - 1; j++) {
                if (this.esNT(producciones.get(i)[j]) && this.esNT(producciones.get(i)[j + 1])) {
                    //agrego first de nT a follow de nT en i
                    //busco los first de 
                    //System.out.println("busco los first de: " + producciones.get(i)[j + 1]);
                    int n = this.buscarIndexDeNoTerminal(producciones.get(i)[j + 1]);
                    //System.out.println("index: " + n);
                    for (int k = 1; k < this.first.get(n).length; k++) {
                        //System.out.println(first.get(n)[k]);
                        if (!"tkn_l".equals(first.get(n)[k])) {
                            //System.out.println("agregando a follow de: " + producciones.get(i)[j] + " ->" + first.get(n)[k]);
                            agregarAFollow(first.get(n)[k], producciones.get(i)[j]);
                        }
                    }
                }
                if (this.esNT(producciones.get(i)[j]) && this.esT(producciones.get(i)[j + 1])) {
                    //agrego T a follow de nT en i
                    //System.out.println("intentando agregar: " + producciones.get(i)[j + 1] + " a " + producciones.get(i)[j]); //es al no terminal del que lo vy a meter
                    agregarAFollow(producciones.get(i)[j + 1], producciones.get(i)[j]);
                }
            }
        }
        //
        //this.imprimirFF();
        //recorro de nuevo producciones, si hay un NT seguido de un NT y el segundo en sus First contiene lambda, entonces se agregan los follows del que lo produjo al primero
        //for (int ñ = 0; ñ < 3; ñ++) {
        boolean contiene = false;
        for (int i = 0; i < producciones.size(); i++) {
            for (int j = 0; j < producciones.get(i).length - 1; j++) {
                if (this.esNT(producciones.get(i)[j]) && this.esNT(producciones.get(i)[j + 1])) {
                    //busco first del segundo no terminal
                    int indx = this.buscarIndexDeNoTerminal(producciones.get(i)[j + 1]);
                    //busco los first's de ese no terminal
                    this.first.get(indx);
                    for (int k = 1; k < first.get(indx).length; k++) {
                        if (first.get(indx)[k].equals("tkn_l")) {
                            contiene = true;
                        }
                    }
                    //hago el traspaso de follows
                    if (contiene) {
                        //buscar index del NT que produjo
                        int indx2 = buscarIndexDeNoTerminal(this.noTerminales.get(i));

                        //buscar los follows de ese NT
                        //System.out.println(this.noTerminales.get(i) + " tiene: " + follow.get(indx2).length + " follows");
                        for (int k = 1; k <= this.follow.get(indx2).length - 1; k++) {
                            //System.out.println("origen del follow: " + this.noTerminales.get(i) + "intentando agregar: " + follow.get(indx2)[k] + " a " + producciones.get(i)[j]); //es al no terminal del que lo vy a meter
                            agregarAFollow(follow.get(indx2)[k], producciones.get(i)[j]);
                        }
                    }
                    contiene = false;
                }
            }
        }



        //}

        //recorro producciones, si hay un NT al final de la cadena, tambien traspaso follows
        for (int i = 0; i < producciones.size(); i++) {
            if (this.esNT(producciones.get(i)[producciones.get(i).length - 1])) {
                //traspaso
                //buscar index del NT que produjo
                int indx2 = buscarIndexDeNoTerminal(this.noTerminales.get(i));
                //buscar los follows de ese NT
                for (int k = 1; k < this.follow.get(indx2).length; k++) {
                    //System.out.println("origen del follow: " + this.noTerminales.get(i) + "intentando agregar: " + follow.get(indx2)[k] + " a " + producciones.get(i)[producciones.get(i).length - 1]); //es al no terminal del que lo vy a meter
                    agregarAFollow(follow.get(indx2)[k], producciones.get(i)[producciones.get(i).length - 1]);
                }
            }
        }

        contiene = false;
        for (int i = 0; i < producciones.size(); i++) {
            for (int j = 0; j < producciones.get(i).length - 1; j++) {
                if (this.esNT(producciones.get(i)[j]) && this.esNT(producciones.get(i)[j + 1])) {
                    //busco first del segundo no terminal
                    int indx = this.buscarIndexDeNoTerminal(producciones.get(i)[j + 1]);
                    //busco los first's de ese no terminal
                    this.first.get(indx);
                    for (int k = 1; k < first.get(indx).length; k++) {
                        if (first.get(indx)[k].equals("tkn_l")) {
                            contiene = true;
                        }
                    }
                    //hago el traspaso de follows
                    if (contiene) {
                        //buscar index del NT que produjo
                        int indx2 = buscarIndexDeNoTerminal(this.noTerminales.get(i));

                        //buscar los follows de ese NT
                        //System.out.println(this.noTerminales.get(i) + " tiene: " + follow.get(indx2).length + " follows");
                        for (int k = 1; k <= this.follow.get(indx2).length - 1; k++) {
                            //System.out.println("origen del follow: " + this.noTerminales.get(i) + "intentando agregar: " + follow.get(indx2)[k] + " a " + producciones.get(i)[j]); //es al no terminal del que lo vy a meter
                            agregarAFollow(follow.get(indx2)[k], producciones.get(i)[j]);
                        }
                    }
                    contiene = false;
                }
            }
        }

        contiene = false;
        for (int i = 0; i < producciones.size(); i++) {
            for (int j = 0; j < producciones.get(i).length - 1; j++) {
                if (this.esNT(producciones.get(i)[j]) && this.esNT(producciones.get(i)[j + 1])) {
                    //busco first del segundo no terminal
                    int indx = this.buscarIndexDeNoTerminal(producciones.get(i)[j + 1]);
                    //busco los first's de ese no terminal
                    this.first.get(indx);
                    for (int k = 1; k < first.get(indx).length; k++) {
                        if (first.get(indx)[k].equals("tkn_l")) {
                            contiene = true;
                        }
                    }
                    //hago el traspaso de follows
                    if (contiene) {
                        //buscar index del NT que produjo
                        int indx2 = buscarIndexDeNoTerminal(this.noTerminales.get(i));

                        //buscar los follows de ese NT
                        //System.out.println(this.noTerminales.get(i) + " tiene: " + follow.get(indx2).length + " follows");
                        for (int k = 1; k <= this.follow.get(indx2).length - 1; k++) {
                            //System.out.println("origen del follow: " + this.noTerminales.get(i) + "intentando agregar: " + follow.get(indx2)[k] + " a " + producciones.get(i)[j]); //es al no terminal del que lo vy a meter
                            agregarAFollow(follow.get(indx2)[k], producciones.get(i)[j]);
                        }
                    }
                    contiene = false;
                }
            }
        }
        //this.imprimirFF();
        //recorro producciones, si hay un NT al final de la cadena, tambien traspaso follows
        for (int i = 0; i < producciones.size(); i++) {
            if (this.esNT(producciones.get(i)[producciones.get(i).length - 1])) {
                //traspaso
                //buscar index del NT que produjo
                int indx2 = buscarIndexDeNoTerminal(this.noTerminales.get(i));
                //buscar los follows de ese NT
                for (int k = 1; k < this.follow.get(indx2).length; k++) {
                    //System.out.println("origen del follow: " + this.noTerminales.get(i) + "intentando agregar: " + follow.get(indx2)[k] + " a " + producciones.get(i)[producciones.get(i).length - 1]); //es al no terminal del que lo vy a meter
                    agregarAFollow(follow.get(indx2)[k], producciones.get(i)[producciones.get(i).length - 1]);
                }
            }
        }

        //recorro producciones, si hay un NT al final de la cadena, tambien traspaso follows
        for (int i = 0; i < producciones.size(); i++) {
            if (this.esNT(producciones.get(i)[producciones.get(i).length - 1])) {
                //traspaso
                //buscar index del NT que produjo
                int indx2 = buscarIndexDeNoTerminal(this.noTerminales.get(i));
                //buscar los follows de ese NT
                for (int k = 1; k < this.follow.get(indx2).length; k++) {
                    //System.out.println("origen del follow: " + this.noTerminales.get(i) + "intentando agregar: " + follow.get(indx2)[k] + " a " + producciones.get(i)[producciones.get(i).length - 1]); //es al no terminal del que lo vy a meter
                    agregarAFollow(follow.get(indx2)[k], producciones.get(i)[producciones.get(i).length - 1]);
                }
            }
        }
    }

    public  ArrayList<String> getFollowNT(String nT) {
        ArrayList<String> follow = new ArrayList<>();
        for (int i = 0; i < first.size(); i++) {
            if (first.get(i)[0].equals(nT)) {
                for (int j = 1; j < first.get(i).length; j++) {
                    follow.add(first.get(i)[j]);
                }
            }
        }
        return follow;
    }

    public void recogerDatos() {
        noTerminales = new ArrayList<>();
        producciones = new ArrayList<>();
        first = new ArrayList<>();
        follow = new ArrayList<>();
        try {
            File inputWorkbook = new File("C:\\Users\\Alberto\\Documents\\NetBeansProjects\\compilador\\datos\\GramaticaTabla.xls");
            //System.out.println(getClass().getResource("compilador/GramaticaTabla.xls").toURI());
            //File inputWorkbook = new File(getClass().getResource("GramaticaTabla.xls").toURI());
            Workbook w;
            w = Workbook.getWorkbook(inputWorkbook);
            Sheet sheet = w.getSheet(3);
            System.out.println("columnas: " + sheet.getColumns());
            System.out.println("filas :" + sheet.getRows());
            for (int j = 0; j < sheet.getColumns(); j++) {
                for (int i = 0; i < sheet.getRows(); i++) {
                    Cell cell = sheet.getCell(j, i);
                    CellType type = cell.getType();
                    //System.out.println(cell.getContents());
                    if (j == 0) {
                        noTerminales.add(cell.getContents());
                    } else {
                        //System.out.println("añadiendoa prod");
                        //tengo que desglozar la cadena obtenida de producciones en el vector de Strings
                        producciones.add(desglozarProduccion(cell.getContents()));
                    }
                }
            }
            Sheet sheet2 = w.getSheet(4);
            this.numeroDeFF = sheet2.getRows();
            filas = new String[numeroDeFF];
            for (int i = 0; i < sheet2.getRows(); i++) {
                Cell cell = sheet2.getCell(0, i);
                //System.out.println(cell.getContents());
                this.first.add(new String[]{cell.getContents()});
                this.follow.add(new String[]{cell.getContents()});
                filas[i] = cell.getContents();

            }
            sheet2 = w.getSheet(5);
            this.numeroDTokens = sheet2.getRows();
            this.columnas = new String[sheet2.getRows()];
            for (int i = 0; i < sheet2.getRows(); i++) {
                Cell cell = sheet2.getCell(0, i);
                columnas[i] = cell.getContents();
            }
            //System.out.println("Num: " + numeroDeFF);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String[] desglozarProduccion(String entrada) {
        return entrada.split(" ");
    }

    public void imprimirTabla() {
        for (int i = 0; i < noTerminales.size(); i++) {
            System.out.print(i + ".-" + noTerminales.get(i) + "->");
            for (int j = 0; j < producciones.get(i).length; j++) {
                System.out.print(producciones.get(i)[j]);
            }
            System.out.println();
        }
    }

    public void imprimirFF() {
        System.out.println("FIRST: size del first" + first.size());
        for (int i = 0; i < first.size(); i++) {
            for (int j = 0; j < first.get(i).length; j++) {
                System.out.print(first.get(i)[j] + ",");
            }
            System.out.println();
        }
        System.out.println("FOLLOW: ");
        for (int i = 0; i < follow.size(); i++) {
            for (int j = 0; j < follow.get(i).length; j++) {
                System.out.print(follow.get(i)[j] + ",");
            }
            System.out.println();
        }
    }

    public boolean esNT(String entrada) {
        if (entrada.contains("<")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean esT(String entrada) {
        if (entrada.contains("tkn_")) {
            return true;
        } else {
            return false;
        }
    }

    public void generarTabla() {
        matriz = new int[numeroDeFF][numeroDTokens];
        System.out.println("filas: " + numeroDeFF + " columnas: " + numeroDTokens);
        for (int i = 0; i < filas.length; i++) {
            //System.out.println(filas[i]);
            //System.out.println(columnas[i]);
        }

        for (int i = 0; i < producciones.size(); i++) {
            //System.out.println(producciones.get(i)[0]);
            if ("tkn_l".equals(producciones.get(i)[0])) {
                //si encuentro lambda agrego todos los follows a la tabla
                //System.out.println("encontrado lambda");
                int indx = buscarIndexDeNoTerminal(this.noTerminales.get(i));
                System.out.println("NoTerminal: " + this.noTerminales.get(i) + " index: " + indx);
                for (int j = 1; j < this.follow.get(indx).length; j++) {
                    System.out.print(" agregar: " + follow.get(indx)[j] + " columna: " + this.buscarIndexDeTerminal(follow.get(indx)[j]) + " matriz[" + indx + "][" + buscarIndexDeTerminal(follow.get(indx)[j]) + "]=" + (i + 1));
                    matriz[indx][buscarIndexDeTerminal(follow.get(indx)[j])] = (i + 1);
                }
                //System.out.println();
            }
            if (this.esT(producciones.get(i)[0])) {
                //Se agrega el terminal a la tabla con el numero i+1
                //&int indx = producciones.get(i)[0];
                int indx = buscarIndexDeTerminal(producciones.get(i)[0]);
                int indx2 = this.buscarIndexDeNoTerminal(this.noTerminales.get(i));
                //System.out.println("indx2: "+indx2+", indx"+indx);
                if (indx > -1 && indx2 > -1) {
                    matriz[indx2][indx] = (i + 1);
                    //System.out.println("produjo: "+this.noTerminales.get(i));
                    //System.out.println("buscando el terminal: " + producciones.get(i)[0] + " agregando a la tabla en columna " + producciones.get(i)[0] + " numero agregado: " + (i + 1) + " fila: " + indx2 + "->"+this.noTerminales.get(i)+" columna:  "+indx);
                }
            } else if (this.esNT(producciones.get(i)[0])) {
                //Se agrega a la tabla los follows de ese NT
                int indx = this.buscarIndexDeNoTerminal(producciones.get(i)[0]);
                //System.out.print("produjo: "+ this.noTerminales.get(i) +" NT: "+producciones.get(i)[0]+" fila: "+buscarIndexDeNoTerminal(this.noTerminales.get(i)));
                //this.;
                for (int j = 1; j < first.get(indx).length; j++) {
                    //System.out.print(" agegar: "+first.get(indx)[j]+" columna: "+this.buscarIndexDeTerminal(first.get(indx)[j])+" num: "+(i+1));
                    if (this.buscarIndexDeTerminal(first.get(indx)[j]) > -1) {
                        matriz[buscarIndexDeNoTerminal(this.noTerminales.get(i))][this.buscarIndexDeTerminal(first.get(indx)[j])] = (i + 1);
                    }
                }
                System.out.println();
            }
        }
    }

    public void imprimirTablaFinal() {
        System.out.println();
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[0].length; j++) {
                if (matriz[i][j] == 0) {
                    System.out.print(matriz[i][j] + "  ,");
                } else if (matriz[i][j] < 10) {
                    System.out.print(matriz[i][j] + "  ,");

                } else {
                    System.out.print(matriz[i][j] + " ,");
                }
            }
            System.out.println();
        }
    }
}
