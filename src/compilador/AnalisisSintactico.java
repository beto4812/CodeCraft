/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador;

import gui.Ed;
import java.awt.Color;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import org.apache.commons.lang3.ArrayUtils;

/**
 *
 * @author beto
 */
public class AnalisisSintactico {

    int matriz[][];
    ArrayList<String[]> analisisLexico;
    ArrayList<String> noTerminales; //A-> (un string)
    ArrayList<String[]> producciones;
    FirstFollow fF = new FirstFollow();
    ArrayList<String[]> entrada;
    int produccionActual;
    ArrayList<String> pila;
    Ed ed;

    public static void main(String[] args) {
        ArrayList<String[]> analisisLexico2 = new ArrayList<>();
        analisisLexico2.add(new String[]{"id1", "tkn_id"});
        analisisLexico2.add(new String[]{"=", "tkn_igual"});
        analisisLexico2.add(new String[]{"2", "tkn_num"});
        analisisLexico2.add(new String[]{";", "tkn_;"});
        analisisLexico2.add(new String[]{"$", "tkn_$"});
        //new AnalisisSintactico(analisisLexico2).iniciarAnalisisSintactico();
    }

    public AnalisisSintactico(ArrayList<String[]> analisisLexico, Ed ed) {
        this.analisisLexico = analisisLexico;
        this.matriz = fF.ejecutarFirstFollow();
        this.producciones = fF.getProducciones();
        this.noTerminales = fF.getNoTerminales();
        pila = new ArrayList<>();
        //meto a la pila $ y <cuerpo>
        pila.add("tkn_$");
        pila.add("<cuerpo>");
        imprimirPila();
        llenarEntrada();
        imprimirEntrada();
        this.ed = ed;
        //imprimirProducciones();
    }

    public void imprimirProducciones() {

        for (int i = 0; i < this.producciones.size(); i++) {
            for (int j = 0; j < producciones.get(i).length; j++) {
                System.out.print(producciones.get(i)[j] + " ");
            }
            System.out.println();
        }
    }

    public void invertirProduccion(String[] prod) {
        ArrayUtils.reverse(prod);
    }

    public void imprimirPila() {
        System.out.print("pila: ");
        for (int i = 0; i < pila.size(); i++) {
            System.out.print(pila.get(i));
        }
        System.out.println();
    }

    public void imprimirEntrada() {
        System.out.print("entrada: ");
        for (int i = 0; i < entrada.size(); i++) {
            System.out.print(entrada.get(i)[0] + " ");
        }
        System.out.println();
    }

    public void llenarEntrada() {
        entrada = new ArrayList<>();
        for (int i = 0; i < analisisLexico.size(); i++) {
            entrada.add(new String[]{analisisLexico.get(i)[1], " fila: " + analisisLexico.get(i)[2] + " columna: " + analisisLexico.get(i)[3]});
        }
        entrada.add(new String[]{"tkn_$"});
    }

    public void agregarProduccionAPila(int num) {
        System.out.println("");
        String prod[] = this.producciones.get(num - 1);
        this.invertirProduccion(prod);
        for (int i = 0; i < prod.length; i++) {
            this.pila.add(prod[i]);
            //System.out.println("agregando a pila: "+prod[i]);
        }
        this.invertirProduccion(prod);
        imprimirPila();
    }

    public void eliminarTopePila() {
        this.pila.remove(pila.size() - 1);
    }

    public void aceptarToken() {
        this.entrada.remove(0);
    }

    public String topePila() {
        return pila.get(pila.size() - 1);
    }

    public boolean iniciarAnalisisSintactico() {
        String ultimoTopePila = "";
        String ultimoEntrada = "";
        while (true) {
            try {
                System.out.println("-------");
                if (topePila().equals("tkn_l")) {
                    System.out.println("tkn_l detectado");
                    this.eliminarTopePila();
                    imprimirPila();
                    imprimirEntrada();
                    if (!entrada.get(0)[0].equals("tkn_$")) {
                        System.out.println("ultimoEntrada: " + ultimoEntrada);
                        ultimoEntrada =  entrada.get(0)[0]+entrada.get(0)[1];
                    }
                    //if(imprimirEntrada)
                } else if (topePila().equals(entrada.get(0)[0])) {
                    if (!entrada.get(0)[0].equals("tkn_$")) {
                        System.out.println("ultimoEntrada: " + ultimoEntrada);
                        ultimoEntrada =  entrada.get(0)[0]+entrada.get(0)[1];
                    }                    
                    System.out.println("token aceptado");
                    this.eliminarTopePila();
                    this.aceptarToken();
                    this.imprimirPila();
                    this.imprimirEntrada();
                } else {
                    int indxFila = (fF.esT(topePila()) == true ? fF.buscarIndexDeTerminal(topePila()) : fF.buscarIndexDeNoTerminal(topePila()));
                    int indxColumna = (fF.esT(entrada.get(0)[0]) == true ? fF.buscarIndexDeTerminal(entrada.get(0)[0]) : fF.buscarIndexDeNoTerminal(entrada.get(0)[0]));
                    ultimoTopePila = topePila();
                    System.out.println("topePila: " + topePila() + " index topePila: " + indxFila + " entrada: " + entrada.get(0)[0] + " index: " + indxColumna);
                    this.produccionActual = this.matriz[indxFila][indxColumna];
                    System.out.println("produccion: " + produccionActual);
                    this.eliminarTopePila();
                    this.agregarProduccionAPila(produccionActual);
                    this.imprimirEntrada();
                    if (!entrada.get(0)[0].equals("tkn_$")) {
                        System.out.println("ultimoEntrada: " + ultimoEntrada);
                        ultimoEntrada =  entrada.get(0)[0]+entrada.get(0)[1];
                    } else {
                        System.out.println("NO");
                    }
                }
                if (entrada.isEmpty()) {
                    System.out.println("CADENA ACEPTADA");
                    ed.jLabelEstado.setText("Cadena Aceptada");
                    ed.jLabelEstado.setForeground(Color.RED);

                    //JOptionPane.showMessageDialog(null, "Cadena Aceptada!");
                    return true;
                }
            } catch (Exception e) {
                //e.printStackTrace();
                
                String errorMsj = "Se esperaba: ";
                if (fF.esNT(ultimoTopePila)) {
                    System.out.println("Aqui 1 UltimoTopePila: "+ultimoTopePila);
                    ArrayList<String> follow = fF.getFollowNT(ultimoTopePila);
                    for (int i = 0; i < follow.size(); i++) {
                        if (i < follow.size() - 1) {
                            errorMsj += "\"" + follow.get(i).replace("tkn_", "") + "\"" + " o ";
                        } else {
                            errorMsj += "\"" + follow.get(i).replace("tkn_", "") + "\"";
                        }
                    }
                } else {
                    System.out.println("Aqui 2"+ultimoTopePila);
                    errorMsj += ultimoTopePila;
                }

                errorMsj += " cerca de " + ultimoEntrada;
                //mientras no sea un terminal busco las producciones del no terminal 
                //System.out.println("Error: " + ultimoTopePila);
                //recorrer el ultimo de la pila hasta que sea un terminal y decir que se esperaba
                //e.printStackTrace();
                JOptionPane.showMessageDialog(null, errorMsj);
                return false;
            }
        }
        //busca tope de la pila y encuentra produccion adecuada
        //fila-> topeDeLaPila, columna->entrada
        //puede ser cualquiera de los dos T o NT
    }
}
