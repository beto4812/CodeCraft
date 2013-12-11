package gui;

import compilador.AnalisisLexico;
import compilador.AnalisisSintactico;
import compilador.CodigoIntermedio;
import compilador.AnalisisSemantico;
import core.EnterListener;
import core.MyDocumentListener;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.UndoManager;
import textLine.TextLine;
import util.Tools;

//Incluir debug de caracteres no admitidos
//Cadena seguida de ID

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Alberto
 */
//Tareas:
//Aumentar debug
//No permitir 2 o mas archivos abiertos con el mismo nombre
//Bugs:
//A veces cuando abres varias pestañas y cierras alguna (o todas) sin haber guardado ninguna anteriormente, te pide que guardes cambios cuando aun no existe archivo donde guardar
public class Ed extends javax.swing.JFrame {

    /**
     * Creates new form EditorJFrame
     */
    Tools tools = new Tools();
    //Constantes
    private int numeroDePestañas = 0;
    private int pestañaActual = 0;
    private int numeroDePestañasCreadas = 0;
    //Lista de paneles 
    ArrayList<JPanel> panelesTexto = new ArrayList<>();
    //Lista de JTextArea
    ArrayList<JTextPane> areasTexto = new ArrayList<>();
    //Lista del manager undo y redo
    ArrayList<UndoManager> undoManager = new ArrayList<>();
    //Lista de JScrollPane
    ArrayList<JScrollPane> scrollPanes = new ArrayList<>();
    //Lista de estado de los documentos (0 no existe archivo, 1 ya existe archivo por que ya guarde o por que fue cargado
    ArrayList<Integer> guardadoPrimeraVez = new ArrayList<>();
    //Hay cambios en el archivo que tienen que ser guardados
    ArrayList<Integer> cambiosArchivo = new ArrayList<>();//0 cambios sin guardar //1 cambios ya se guardaron
    //Nombre del archivo una vez guardado (Se actualiza la pestaña)
    ArrayList<String> stringsAbsolutePath = new ArrayList<>();
    //documentListenerActual
    MyDocumentListener myDocumentListener = new MyDocumentListener(this);
    JTextPane jTextAreaActual;
    EnterListener enterListenerActual = new EnterListener(this);
    int idActualizacion;
    int tamañoLetra = 1; //0 chica 1 mediana 2 grande 3 extra grande
    //copiar, cortar, pegar
    String copiado = "";  //o cortado
    //VariablesCompilador
    ArrayList<String[]> analisisLexico;
    //TextLineNumber tln;
    TextLine tln;
    Highlighter highlighter;

    public Ed() {
        initComponents();
        initEditor();
    }

    //Inicializo componentes
    public void initEditor() {
        nuevo();
        //this.jTextAreaContador.setFont(new Font("Courrier New", Font.PLAIN, 12));
    }

    public void actualizarEditor() {
        //System.out.println("Actualizacion del editor: " + this.pestañaActual);
        this.cambiosArchivo.add(this.pestañaActual, 0);
        this.cambiosArchivo.remove(this.pestañaActual + 1);
        switch (cambiosArchivo.get(this.pestañaActual)) {
            case 0:
                jLabelEstado.setForeground(Color.black);
                this.jLabelEstado.setText("Cambios sin guardar");
                break;
            case 1:
                jLabelEstado.setForeground(Color.black);
                this.jLabelEstado.setText("Estado...");
                break;
        }
        //this.jLabelLengthLines.setText("Length:" + this.jTextAreaActual.getDocument().getLength() + "  Lines:" + this.jTextAreaActual.getLineCount() + "     ");
        //this.jLabelLineColumn.setText("Ln:"+this.jTextAreaActual.get+"    Col: ");
        idActualizacion++;
        //actualizarPanelIzquierdo();
        actualizarBotones();
        //System.out.println("carret: "+this.jTextAreaActual.getCaret().getMark()+", "+this.jTextAreaActual.getCaret().getMagicCaretPosition().getY());
    }

    public void actualizarBotones() {
        UndoManager temp = undoManager.get(this.pestañaActual);
        this.jButtonDeshacer.setEnabled(temp.canUndo());
        this.jButtonRehacer.setEnabled(temp.canRedo());
    }

    private void setearPestaña(int num) {
        this.pestañaActual = num;
        this.jTextAreaActual = this.areasTexto.get(num);
        highlighter = jTextAreaActual.getHighlighter();
        //this.jTextAreaContador.setText(stringsIzquierda.get(this.pestañaActual));
        System.err.println("pestaña seteada: " + num);
        switch (cambiosArchivo.get(this.pestañaActual)) {
            case 0:
                this.jLabelEstado.setText("Cambios sin guardar");
                break;
            case 1:
                this.jLabelEstado.setText("Estado...");
                break;
        }
        jTextAreaActual.requestFocus();
        this.actualizarTamañoLetra();
        try {
            this.actualizarBotones();
        } catch (Exception e) {
        }
    }

    public void subrrallarTexto(int fila, int columna, int lengthPalaba, Color color) {
        //analizar el texto para ir bajando de fila y columna
        char[] carac = this.jTextAreaActual.getText().toCharArray();
        //System.out.println("imprimiendo: ");System.out.println(texto);
        //System.out.println("subrrallando fila: " + fila + " columna: " + columna);
        int filaActual = 1;
        int columnas = 0;
        int inicialC = 0, finalC = 0;
        int contadorExtra = 0;
        int tabuladorEnc = 0;

        for (int i = 0; i < carac.length; i++) {
            inicialC++;
            //System.out.println("caracter: " + (int) carac[i]);
            //System.out.println("filaActual: " + filaActual);
            if (carac[i] == 13) {
                filaActual++;
                i++;//me salto el caracter de retorno de carro
            }
            if (filaActual == fila) {
                //fila correcta
                //System.out.println("fila correcta");
                //contar hacia la derecha, si veo un tabulador recorro hacia adelante
                if (carac[i] != 9) {
                    //System.out.println("---columna: " + columnas);
                    if (columnas == columna) {
                        //System.out.println("columaEncontrada");
                        break;
                    }
                    columnas++;
                } else {
                    //tabulador me lo salto pero cuento inicio
                }
            } else {
            }
        }
        if (fila == 1) {
            inicialC--;
        }
        inicialC--;
        try {
            this.highlighter.addHighlight(inicialC, inicialC + lengthPalaba, new DefaultHighlighter.DefaultHighlightPainter(color));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    /*
     public void subrallarTexto(int inicio, int fin, Color color) {
     System.out.println("llamando a subrallar");
     try {
     this.highlighter.addHighlight(inicio, fin,new DefaultHighlighter.DefaultHighlightPainter(Color.RED));
     } catch (Exception e) {
     e.printStackTrace();
     }
     }*/

    //Se presiona nuevo. Se crea un nuevo panel y un nuevo JTextArea y se agrega al JTabedPane
    public void nuevo() {
        //Setea como cambios guardados
        JTextPane textAreaTemp = new JTextPane();
        highlighter = textAreaTemp.getHighlighter();

        JPanel panelTemp = new JPanel();
        JScrollPane scrollPaneTemp = new JScrollPane();
        tln = new TextLine(textAreaTemp);
        scrollPaneTemp.setRowHeaderView(tln);

        panelTemp.setLayout(new FlowLayout());

        javax.swing.GroupLayout jPanelTempLayout = new javax.swing.GroupLayout(panelTemp);
        panelTemp.setLayout(jPanelTempLayout);
        jPanelTempLayout.setHorizontalGroup(
                jPanelTempLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(scrollPaneTemp, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 865, Short.MAX_VALUE));
        jPanelTempLayout.setVerticalGroup(
                jPanelTempLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanelTempLayout.createSequentialGroup()
                .addComponent(scrollPaneTemp, javax.swing.GroupLayout.PREFERRED_SIZE, 402, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 65, Short.MAX_VALUE)));

        panelesTexto.add(panelTemp);
        areasTexto.add(textAreaTemp);
        scrollPanes.add(scrollPaneTemp);
        this.guardadoPrimeraVez.add(0);
        this.cambiosArchivo.add(1);
        //this.jTextAreaContador.setText("1\n");

        this.jTabbedPaneMain.addTab("Nuevo " + (this.numeroDePestañasCreadas + 1), panelTemp);
        System.err.println("Creando pestaña " + "Nuevo " + (this.numeroDePestañasCreadas + 1));
        this.stringsAbsolutePath.add("Nuevo " + (this.numeroDePestañasCreadas + 1));
        panelTemp.add(scrollPaneTemp);
        scrollPaneTemp.setViewportView(textAreaTemp);
        textAreaTemp.setFont(new Font("Monospaced", Font.PLAIN, 12));

        this.jTabbedPaneMain.setSelectedIndex(numeroDePestañas);
        //this.pestañaActual = numeroDePestañas;
        this.setearPestaña(numeroDePestañas);
        this.numeroDePestañas++;
        this.numeroDePestañasCreadas++;
        this.jTextAreaActual = textAreaTemp;
        final UndoManager undoManagerTemp = new UndoManager();
        jTextAreaActual.getDocument().addUndoableEditListener(
                new UndoableEditListener() {
            public void undoableEditHappened(UndoableEditEvent e) {
                undoManagerTemp.addEdit(e.getEdit());
                actualizarBotones();
            }
        });
        this.undoManager.add(undoManagerTemp);
        jTextAreaActual.getDocument().addDocumentListener(myDocumentListener);
        jTextAreaActual.addKeyListener(this.enterListenerActual);
        textAreaTemp.requestFocus();
        this.actualizarBotones();
    }

    public void ctrlZ() {
        this.deshacer();
    }
    
    public void ctrlY(){
        
        this.rehacer();
    }

    public void deshacer() {
        try {
            this.undoManager.get(this.pestañaActual).undo();
        } catch (Exception e) {
            //cre.printStackTrace();
        }
        this.actualizarBotones();
    }

    public void rehacer() {
        try {
            this.undoManager.get(this.pestañaActual).redo();
        } catch (Exception e) {
            //cre.printStackTrace();
        }
        this.actualizarBotones();
    }

    public void setearTamañoLetra() {
        this.tamañoLetra = this.jComboBoxTamaño.getSelectedIndex();
        actualizarTamañoLetra();

    }

    public void actualizarTamañoLetra() {
        switch (tamañoLetra) {
            case 0:
                this.jTextAreaActual.setFont(new Font("Monospaced", Font.PLAIN, 10));
                //this.jTextAreaContador.setFont(new Font("Courrier New", Font.PLAIN, 10));
                break;
            case 1:
                this.jTextAreaActual.setFont(new Font("Monospaced", Font.PLAIN, 12));
                //this.jTextAreaContador.setFont(new Font("Courrier New", Font.PLAIN, 12));
                break;
            case 2:
                this.jTextAreaActual.setFont(new Font("Monospaced", Font.PLAIN, 14));
                //this.jTextAreaContador.setFont(new Font("Courrier New", Font.PLAIN, 14));
                break;
            case 3:
                Map<TextAttribute, Integer> fontAttributes = new HashMap<TextAttribute, Integer>();
                fontAttributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
                this.jTextAreaActual.setFont(new Font("Monospaced", Font.BOLD, 12).deriveFont(fontAttributes));
                //this.jTextAreaContador.setFont(new Font("Courrier New", Font.PLAIN, 12));
                //this.jTextAreaActual.setFont(new Font("Courrier New", Font.PLAIN, 16));
                //this.jTextAreaContador.setFont(new Font("Courrier New", Font.PLAIN, 16));
                break;
        }
    }

    public void guardar() {
        System.err.println("llamando a guardar en: " + this.stringsAbsolutePath.get(this.pestañaActual));

        //Actualizar que ya se guardaron los cambios
        this.cambiosArchivo.add(this.pestañaActual, 1);
        this.cambiosArchivo.remove(pestañaActual + 1);
        switch (cambiosArchivo.get(this.pestañaActual)) {
            case 0:
                this.jLabelEstado.setText("Cambios sin guardar");
                break;
            case 1:
                this.jLabelEstado.setText("Estado...");
                break;
        }
        File file = new File(this.stringsAbsolutePath.get(this.pestañaActual));
        BufferedWriter outFile = null;
        try {
            outFile = new BufferedWriter(new FileWriter(file));
            this.jTextAreaActual.write(outFile);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (outFile != null) {
                try {
                    outFile.close();
                } catch (IOException e) {
                }
            }
        }
    }

    //0 es que se guardo, //1 no se guardo
    public int guardarComo() {
        FileNameExtensionFilter extensionFilter = new FileNameExtensionFilter("Text File", "txt");
        JFileChooser saveAsFileChooser = new JFileChooser();
        saveAsFileChooser.setApproveButtonText("Guardar");
        saveAsFileChooser.setFileFilter(extensionFilter);
        int actionDialog = saveAsFileChooser.showOpenDialog(this);
        //System.out.println("actionDialogShow: "+actionDialog);
        if (actionDialog != JFileChooser.APPROVE_OPTION) {
            return 1;
        }

        File file = saveAsFileChooser.getSelectedFile();
        if (!file.getName().endsWith(".txt")) {
            file = new File(file.getAbsolutePath() + ".txt");
        }
        boolean exists = (new File(file.getAbsolutePath())).exists();
        if (exists) {
            Object[] options = {"Si", "No"};
            int opc = JOptionPane.showOptionDialog(this, "¿El archivo ya existe, desea sobreescribirlo?", "Cuidado!", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
            if (opc == 1) {
                return 1;
            }
        } else {
            System.err.println("No existia el archivo");
        }

        this.stringsAbsolutePath.add(this.pestañaActual, file.getAbsolutePath());
        this.stringsAbsolutePath.remove(this.pestañaActual + 1);
        System.err.println("agregando a archivo de paths: " + file.getAbsolutePath());
        jTabbedPaneMain.setTitleAt(this.pestañaActual, file.getName());
        BufferedWriter outFile = null;
        try {
            outFile = new BufferedWriter(new FileWriter(file));
            this.jTextAreaActual.write(outFile);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (outFile != null) {
                try {
                    outFile.close();
                } catch (IOException e) {
                }
            }
        }
        //Actualizar que ya se guardaron los cambios
        this.cambiosArchivo.add(this.pestañaActual, 1);
        this.cambiosArchivo.remove(pestañaActual + 1);
        switch (cambiosArchivo.get(this.pestañaActual)) {
            case 0:
                this.jLabelEstado.setText("Cambios sin guardar");
                break;
            case 1:
                this.jLabelEstado.setText("Estado...");
                break;
        }
        System.err.println("GUARDE");
        return actionDialog; //0 es que guardo
    }

    //opcion: 1 me salto confirmacion y cierro pestaña
    public void cerrarActual(int opcion) {
        if (opcion != 1) {
            System.err.println("estadoArchivo: " + guardadoPrimeraVez.get(this.pestañaActual));
            if ((guardadoPrimeraVez.get(this.pestañaActual) == 0) && this.jTextAreaActual.getDocument().getLength() != 0) {
                Object[] options = {"Si", "No"};
                int opc = JOptionPane.showOptionDialog(this, "¿Desea guardar el archivo " + this.stringsAbsolutePath.get(this.pestañaActual) + "?", "Cuidado!", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
                //int opc = JOptionPane.showConfirmDialog(this, "¿Desea guardar el archivo " + this.stringsAbsolutePath.get(this.pestañaActual) + "?");
                switch (opc) {
                    case 0:
                        if (this.guardarComo() == 1) {
                            return;
                        }
                        break;
                    case 2:
                        return;
                }
            } else if (this.cambiosArchivo.get(this.pestañaActual) == 0) {
                Object[] options = {"Si", "No"};
                //int opc = JOptionPane.showConfirmDialog(this, "¿Desea guardar los cambios en el archivo " + this.stringsAbsolutePath.get(this.pestañaActual) + "?");
                int opc = JOptionPane.showOptionDialog(this, "¿Desea guardar los cambios en el archivo " + this.stringsAbsolutePath.get(this.pestañaActual) + "?", "Cuidado!", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
                switch (opc) {
                    case 0:
                        guardar();
                        break;
                    case 2:
                        return;
                }
            }
        }
        //Elimino
        System.err.println("Elimine la pestaña: " + this.pestañaActual);
        panelesTexto.remove(pestañaActual);
        areasTexto.remove(pestañaActual);
        scrollPanes.remove(pestañaActual);
        guardadoPrimeraVez.remove(pestañaActual);
        stringsAbsolutePath.remove(pestañaActual);
        cambiosArchivo.remove(pestañaActual);
        this.jTabbedPaneMain.remove(pestañaActual);
        numeroDePestañas--;
        if (numeroDePestañas == 0) {
            nuevo();
        } else {
            this.setearPestaña(this.jTabbedPaneMain.getSelectedIndex());
        }
    }

    public void cerrarTodas() {
        for (int i = jTabbedPaneMain.getTabCount() - 1; i >= 0; i--) {
            this.setearPestaña(jTabbedPaneMain.getTabCount() - 1);
            cerrarActual(0);
        }
    }

    public void cerrarPrograma() {
        //Si hay cosa que guardar guardo, si no solo pregunto si desea salir
        for (int i = this.numeroDePestañas - 1; i >= 0; i--) {
            this.setearPestaña(i);
            if (this.guardadoPrimeraVez.get(i) == 0 && this.jTextAreaActual.getDocument().getLength() > 0) {
                //Se verifica si nunca se ha guardado el archivo (Si contiene texto)
                // System.out.println("pestaña " + (i + 1) + " no se guardo");
                Object[] options = {"Si", "No"};
                int opc = JOptionPane.showOptionDialog(this, "Pestaña \"" + this.jTabbedPaneMain.getTitleAt(this.pestañaActual) + "\" no se guardo ¿Desea guardar?", "Cuidado!", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
                System.out.println("opc: " + opc);
                switch (opc) {
                    case 0:
                        this.guardarComo();//nunca se ha guardado
                        break;
                    case 1:
                        //descarto
                        this.cerrarActual(1); //me salto confirmacion
                        break;
                }
            } else if (this.cambiosArchivo.get(i) == 0) {
                Object[] options = {"Si", "No"};
                int opc = JOptionPane.showOptionDialog(this, "¿Desea guardar los cambios de la pestaña \"" + this.jTabbedPaneMain.getTitleAt(this.pestañaActual) + "\"?", "Guardar Cambios", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                System.out.println("opc: " + opc);
                switch (opc) {
                    case 0:
                        this.guardar();//nunca se ha guardado
                        break;
                    case 1:
                        //descarto
                        this.cerrarActual(1); //me salto confirmacion
                        break;
                }
            } else {
                this.cerrarActual(1);
            }
        }
        System.exit(1);
    }

    public void abrirArchivo() {
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int seleccion = jFileChooser.showOpenDialog(this);
        if (seleccion == JFileChooser.APPROVE_OPTION) {
            File f = jFileChooser.getSelectedFile();
            try {
                String nombre = f.getName();
                String path = f.getAbsolutePath();
                String contenido = getArchivo(path);
                if (this.jTextAreaActual.getDocument().getLength() > 0 || this.guardadoPrimeraVez.get(this.pestañaActual) == 1) {
                    nuevo();
                }

                this.jTextAreaActual.setText(contenido);
                jTabbedPaneMain.setTitleAt(this.pestañaActual, f.getName());
                this.guardadoPrimeraVez.set(this.pestañaActual, 1);
                this.cambiosArchivo.set(this.pestañaActual, 1);
                switch (cambiosArchivo.get(this.pestañaActual)) {
                    case 0:
                        this.jLabelEstado.setText("Cambios sin guardar");
                        break;
                    case 1:
                        this.jLabelEstado.setText("Estado...");
                        break;
                }
                this.stringsAbsolutePath.set(this.pestañaActual, f.getAbsolutePath());

            } catch (Exception exp) {
            }
        }
    }

    public String getArchivo(String ruta) {
        FileReader fr = null;
        BufferedReader br = null;
        //Cadena de texto donde se guardara el contenido del archivo
        String contenido = "";
        try {
            //ruta puede ser de tipo String o tipo File
            fr = new FileReader(ruta);
            br = new BufferedReader(fr);

            String linea;
            //Obtenemos el contenido del archivo linea por linea
            String temp = "";
            int cont = 0;
            while ((linea = br.readLine()) != null) {
                if (cont != 0) {
                    contenido += temp + "\n";
                }
                cont++;
                temp = linea;
            }
            contenido += temp;
        } catch (Exception e) {
        } //finally se utiliza para que si todo ocurre correctamente o si ocurre 
        //algun error se cierre el archivo que anteriormente abrimos
        finally {
            try {
                br.close();
            } catch (Exception ex) {
            }
        }
        return contenido;
    }

    public void copiarSeleccionado() {
        String temp = this.jTextAreaActual.getSelectedText();
        if (temp != null) {
            this.copiado = temp;
        }
        System.out.println("copiado: " + copiado);
    }

    public void cortarSeleccionado() {
        int inicio = this.jTextAreaActual.getSelectionStart();
        int fin = jTextAreaActual.getSelectionEnd();
        String s = jTextAreaActual.getText();
        this.copiado = s.substring(inicio, fin);
        String inicioText = jTextAreaActual.getText().substring(0, inicio);
        String finText = jTextAreaActual.getText().substring(fin, jTextAreaActual.getText().length());
        jTextAreaActual.setText(inicioText + finText);
    }

    public void pegarSeleccion() {
        String temp = this.jTextAreaActual.getText();
        System.out.println("texto actual: " + temp);
        this.jTextAreaActual.setText(temp + copiado);
    }

    public void noDisponible() {
        String opc[] = {"ok"};
        JOptionPane.showOptionDialog(this, "No disponible", "Informacion", JOptionPane.INFORMATION_MESSAGE, WIDTH, null, opc, opc[0]);
    }

    //FUNCIONES COMPILADOR
    public void analisisLexico(boolean imprimeTabla) {
        try {
            System.out.println("///////////////////////////////////////////////////////////////////////////////////////////////////////////////////\n/////////////////LLAMANDO AL ANALISIS LEXICO///////////////////////////////////////////////////////////////////////\n///////////////////////////////////////////////////////////////////////////////////////////////////////////////////");
            String cadena = this.jTextAreaActual.getDocument().getText(0, this.jTextAreaActual.getDocument().getLength());
            //System.out.println(cadena);
            AnalisisLexico analisis = new AnalisisLexico(cadena);
            this.analisisLexico = analisis.iniciarAnalisisLexico(imprimeTabla);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void analisisSintactico() {
        analisisLexico(false);
        System.out.println("///////////////////////////////////////////////////////////////////////////////////////////////////////////////////\n/////////////////LLAMANDO AL ANALISIS SINTACTICO///////////////////////////////////////////////////////////////////////\n///////////////////////////////////////////////////////////////////////////////////////////////////////////////////");
        AnalisisSintactico aS = new AnalisisSintactico(analisisLexico, this);
        aS.iniciarAnalisisSintactico();
    }

    public void analisisSemantico() {
        this.highlighter.removeAllHighlights();
        analisisLexico(false);
        System.out.println("///////////////////////////////////////////////////////////////////////////////////////////////////////////////////\n/////////////////LLAMANDO AL ANALISIS SINTACTICO///////////////////////////////////////////////////////////////////////\n///////////////////////////////////////////////////////////////////////////////////////////////////////////////////");
        AnalisisSintactico aSi = new AnalisisSintactico(analisisLexico, this);
        if (aSi.iniciarAnalisisSintactico()) {
            AnalisisSemantico aSe = new AnalisisSemantico(analisisLexico, this);
            aSe.iniciarAnalisisSemantico();
        } else {
            this.jLabelEstado.setText("Analisis Sintactico Incorrecto");
        }
    }

    public void codigoIntermedio() {
        this.highlighter.removeAllHighlights();
        analisisLexico(false);
        System.out.println("///////////////////////////////////////////////////////////////////////////////////////////////////////////////////\n/////////////////LLAMANDO AL CODIGO INTERMEDIO///////////////////////////////////////////////////////////////////////\n///////////////////////////////////////////////////////////////////////////////////////////////////////////////////");
        AnalisisSintactico aSi = new AnalisisSintactico(analisisLexico, this);
        if (aSi.iniciarAnalisisSintactico()) {
            AnalisisSemantico aSe = new AnalisisSemantico(analisisLexico, this);
            aSe.iniciarAnalisisSemantico();
            if (true) {//aSe.getNumeroDeErrores()==0
                CodigoIntermedio cI = new CodigoIntermedio(this);
                cI.iniciarCodigoIntermedio(analisisLexico);
            } else {
                JOptionPane.showMessageDialog(null, "Corrige los errores semanticos antes de continuar");
            }
        } else {
            this.jLabelEstado.setText("Analisis Sintactico Incorrecto");
        }

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabelEstado = new javax.swing.JLabel();
        jPanel = new javax.swing.JPanel();
        jButtonNuevo = new javax.swing.JButton();
        jButtonCortar = new javax.swing.JButton();
        jButtonCopiar = new javax.swing.JButton();
        jButtonPegar = new javax.swing.JButton();
        jButtonAbrir = new javax.swing.JButton();
        jButtonCerrarActual = new javax.swing.JButton();
        jButtonCerrarTodas = new javax.swing.JButton();
        jButtonAnalisisSintactico = new javax.swing.JButton();
        jComboBoxTamaño = new javax.swing.JComboBox();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();
        jLabel1 = new javax.swing.JLabel();
        jSeparator4 = new javax.swing.JSeparator();
        jButton2 = new javax.swing.JButton();
        jButtonDeshacer = new javax.swing.JButton();
        jSeparator5 = new javax.swing.JSeparator();
        jButtonRehacer = new javax.swing.JButton();
        jSeparator6 = new javax.swing.JSeparator();
        jButtonSalir1 = new javax.swing.JButton();
        jButtonAnalisisLexico1 = new javax.swing.JButton();
        jButtonAnalisisSintactico1 = new javax.swing.JButton();
        jButtonAnalisisSintactico2 = new javax.swing.JButton();
        jButtonAnalisisSintactico3 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jTabbedPaneMain = new javax.swing.JTabbedPane();
        jLabelLengthLines = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItemNuevo = new javax.swing.JMenuItem();
        jMenuItemAbrir = new javax.swing.JMenuItem();
        jMenuItemGuardar = new javax.swing.JMenuItem();
        jMenuItemGuardarComo = new javax.swing.JMenuItem();
        jMenuItemSalir = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jRadioButtonMenuItem1 = new javax.swing.JRadioButtonMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("CodeCraft :)");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setIconImage(tools.resizeAndLoadIcon("/images/Iron Sword.png", 35, 35).getImage());
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jLabelEstado.setBackground(new java.awt.Color(153, 180, 209));
        jLabelEstado.setText("Estado");

        jPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel.setPreferredSize(new java.awt.Dimension(98, 40));
        jPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButtonNuevo.setIcon(tools.resizeAndLoadIcon("/images/Book.png", 28, 28));
        jButtonNuevo.setToolTipText("Nuevo");
        jButtonNuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNuevoActionPerformed(evt);
            }
        });
        jPanel.add(jButtonNuevo, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 4, 36, 36));

        jButtonCortar.setIcon(tools.resizeAndLoadIcon("/images/Diamond Sword.png", 28, 28));
        jButtonCortar.setToolTipText("Cortar");
        jButtonCortar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCortarActionPerformed(evt);
            }
        });
        jPanel.add(jButtonCortar, new org.netbeans.lib.awtextra.AbsoluteConstraints(74, 4, 36, 36));

        jButtonCopiar.setIcon(tools.resizeAndLoadIcon("/images/Gold Sword.png", 28, 28));
        jButtonCopiar.setToolTipText("Copiar");
        jButtonCopiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCopiarActionPerformed(evt);
            }
        });
        jPanel.add(jButtonCopiar, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 4, 36, 36));

        jButtonPegar.setIcon(tools.resizeAndLoadIcon("/images/Iron Sword.png", 28, 28));
        jButtonPegar.setToolTipText("Pegar");
        jButtonPegar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPegarActionPerformed(evt);
            }
        });
        jPanel.add(jButtonPegar, new org.netbeans.lib.awtextra.AbsoluteConstraints(146, 4, 36, 36));

        jButtonAbrir.setIcon(tools.resizeAndLoadIcon("/images/folder grass.png", 28, 28));
        jButtonAbrir.setToolTipText("Abrir");
        jButtonAbrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAbrirActionPerformed(evt);
            }
        });
        jPanel.add(jButtonAbrir, new org.netbeans.lib.awtextra.AbsoluteConstraints(38, 4, 36, 36));

        jButtonCerrarActual.setIcon(tools.resizeAndLoadIcon("/images/Grass.png", 28, 28));
        jButtonCerrarActual.setToolTipText("Cerrar Actual");
        jButtonCerrarActual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCerrarActualActionPerformed(evt);
            }
        });
        jPanel.add(jButtonCerrarActual, new org.netbeans.lib.awtextra.AbsoluteConstraints(187, 4, 36, 36));

        jButtonCerrarTodas.setIcon(tools.resizeAndLoadIcon("/images/tnt.png", 28, 28));
        jButtonCerrarTodas.setToolTipText("Cerrar Todas");
        jButtonCerrarTodas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCerrarTodasActionPerformed(evt);
            }
        });
        jPanel.add(jButtonCerrarTodas, new org.netbeans.lib.awtextra.AbsoluteConstraints(223, 4, 36, 36));

        jButtonAnalisisSintactico.setIcon(tools.resizeAndLoadIcon("/images/3D Stone.png", 28, 28));
        jButtonAnalisisSintactico.setToolTipText("Generador de Codigo");
        jButtonAnalisisSintactico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAnalisisSintacticoActionPerformed(evt);
            }
        });
        jPanel.add(jButtonAnalisisSintactico, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 4, 36, 36));

        jComboBoxTamaño.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "chica", "mediana", "grande", "muy grande" }));
        jComboBoxTamaño.setSelectedIndex(1);
        jComboBoxTamaño.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxTamañoItemStateChanged(evt);
            }
        });
        jPanel.add(jComboBoxTamaño, new org.netbeans.lib.awtextra.AbsoluteConstraints(405, 6, 80, 30));
        jPanel.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 20, -1, -1));

        jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jPanel.add(jSeparator3, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 10, 10, 25));

        jLabel1.setText("Tamaño de letra:");
        jPanel.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 14, -1, -1));

        jSeparator4.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jPanel.add(jSeparator4, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 9, 10, 25));

        jButton2.setIcon(tools.resizeAndLoadIcon("/images/Crafting Table.png", 28, 28));
        jButton2.setToolTipText("Guardar Actual");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 4, 36, 36));

        jButtonDeshacer.setIcon(tools.resizeAndLoadIcon("/images/Iron Pickaxe.png", 28, 28));
        jButtonDeshacer.setToolTipText("Deshacer");
        jButtonDeshacer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeshacerActionPerformed(evt);
            }
        });
        jPanel.add(jButtonDeshacer, new org.netbeans.lib.awtextra.AbsoluteConstraints(495, 4, 36, 36));

        jSeparator5.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jPanel.add(jSeparator5, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 9, 10, 25));

        jButtonRehacer.setIcon(tools.resizeAndLoadIcon("/images/Iron Shovel.png", 28, 28));
        jButtonRehacer.setToolTipText("Rehacer");
        jButtonRehacer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRehacerActionPerformed(evt);
            }
        });
        jPanel.add(jButtonRehacer, new org.netbeans.lib.awtextra.AbsoluteConstraints(533, 4, 36, 36));

        jSeparator6.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jPanel.add(jSeparator6, new org.netbeans.lib.awtextra.AbsoluteConstraints(575, 9, 10, 25));

        jButtonSalir1.setIcon(tools.resizeAndLoadIcon("/images/creeper.png", 28, 28));
        jButtonSalir1.setToolTipText("Salir");
        jButtonSalir1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSalir1ActionPerformed(evt);
            }
        });
        jPanel.add(jButtonSalir1, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 4, 36, 36));

        jButtonAnalisisLexico1.setIcon(tools.resizeAndLoadIcon("/images/3D Dirt.png", 28, 28));
        jButtonAnalisisLexico1.setToolTipText("Analisis Lexico");
        jButtonAnalisisLexico1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAnalisisLexico1ActionPerformed(evt);
            }
        });
        jPanel.add(jButtonAnalisisLexico1, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 4, 36, 36));

        jButtonAnalisisSintactico1.setIcon(tools.resizeAndLoadIcon("/images/3D Grass.png", 28, 28));
        jButtonAnalisisSintactico1.setToolTipText("Analisis Sintactico");
        jButtonAnalisisSintactico1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAnalisisSintactico1ActionPerformed(evt);
            }
        });
        jPanel.add(jButtonAnalisisSintactico1, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 4, 36, 36));

        jButtonAnalisisSintactico2.setIcon(tools.resizeAndLoadIcon("/images/3D Mycelium.png", 28, 28));
        jButtonAnalisisSintactico2.setToolTipText("Analisis Semantico");
        jButtonAnalisisSintactico2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAnalisisSintactico2ActionPerformed(evt);
            }
        });
        jPanel.add(jButtonAnalisisSintactico2, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 4, 36, 36));

        jButtonAnalisisSintactico3.setIcon(tools.resizeAndLoadIcon("/images/3D Snow.png", 28, 28));
        jButtonAnalisisSintactico3.setToolTipText("Codigo Intermedio");
        jButtonAnalisisSintactico3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAnalisisSintactico3ActionPerformed(evt);
            }
        });
        jPanel.add(jButtonAnalisisSintactico3, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 4, 36, 36));

        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTabbedPaneMain.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTabbedPaneMainMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTabbedPaneMainMousePressed(evt);
            }
        });
        jTabbedPaneMain.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTabbedPaneMainKeyPressed(evt);
            }
        });
        jPanel2.add(jTabbedPaneMain, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 900, -1));

        jLabelLengthLines.setText("Length:    Lines:       ");
        jLabelLengthLines.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jMenu1.setText("Archivo");

        jMenuItemNuevo.setText("Nuevo");
        jMenuItemNuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemNuevoActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemNuevo);

        jMenuItemAbrir.setText("Abrir...");
        jMenuItemAbrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAbrirActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemAbrir);

        jMenuItemGuardar.setText("Guardar");
        jMenuItemGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemGuardarActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemGuardar);

        jMenuItemGuardarComo.setText("Guardar como...");
        jMenuItemGuardarComo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemGuardarComoActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemGuardarComo);

        jMenuItemSalir.setText("Salir");
        jMenuItemSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSalirActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemSalir);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Editar");

        jMenuItem1.setText("Cortar");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem1);

        jMenuItem2.setText("Copiar");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem2);

        jMenuItem3.setText("Pegar");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem3);

        jMenuItem4.setText("Deshacer");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem4);

        jMenuItem5.setText("Rehacer");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem5);

        jMenuBar1.add(jMenu2);

        jMenu3.setText("Ayuda");

        jRadioButtonMenuItem1.setSelected(true);
        jRadioButtonMenuItem1.setText("Acerca de...");
        jRadioButtonMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItem1ActionPerformed(evt);
            }
        });
        jMenu3.add(jRadioButtonMenuItem1);

        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 900, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabelEstado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelLengthLines, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 430, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelEstado)
                    .addComponent(jLabelLengthLines)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItemGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemGuardarActionPerformed
        if (this.guardadoPrimeraVez.get(this.pestañaActual) == 0) {
            //nunca se ha guardado
            if (this.guardarComo() == 0) {
                this.guardadoPrimeraVez.set(this.pestañaActual, 1);
            }
        } else {
            this.guardar();
        }
    }//GEN-LAST:event_jMenuItemGuardarActionPerformed

    private void jMenuItemNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemNuevoActionPerformed
        this.nuevo();
    }//GEN-LAST:event_jMenuItemNuevoActionPerformed

    private void jMenuItemGuardarComoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemGuardarComoActionPerformed
        this.guardarComo();
    }//GEN-LAST:event_jMenuItemGuardarComoActionPerformed

    private void jMenuItemSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSalirActionPerformed
        this.cerrarPrograma();
    }//GEN-LAST:event_jMenuItemSalirActionPerformed

    private void jButtonNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNuevoActionPerformed
        this.nuevo();
    }//GEN-LAST:event_jButtonNuevoActionPerformed

    private void jTabbedPaneMainKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTabbedPaneMainKeyPressed
    }//GEN-LAST:event_jTabbedPaneMainKeyPressed

    private void jTabbedPaneMainMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTabbedPaneMainMouseClicked
    }//GEN-LAST:event_jTabbedPaneMainMouseClicked

    private void jTabbedPaneMainMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTabbedPaneMainMousePressed
        System.out.println("pestaña seteada: " + this.pestañaActual);
        this.setearPestaña(this.jTabbedPaneMain.getSelectedIndex());
        this.actualizarTamañoLetra();
    }//GEN-LAST:event_jTabbedPaneMainMousePressed

    private void jButtonAnalisisSintacticoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAnalisisSintacticoActionPerformed
        this.subrrallarTexto(1, 2, 2, Color.yellow);
        this.subrrallarTexto(2, 2, 2, Color.yellow);
        this.subrrallarTexto(3, 2, 2, Color.yellow);
        this.subrrallarTexto(4, 2, 2, Color.yellow);
        this.subrrallarTexto(5, 2, 2, Color.yellow);


    }//GEN-LAST:event_jButtonAnalisisSintacticoActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        cerrarPrograma();
    }//GEN-LAST:event_formWindowClosing

    private void jButtonAbrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAbrirActionPerformed
        abrirArchivo();
    }//GEN-LAST:event_jButtonAbrirActionPerformed

    private void jMenuItemAbrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAbrirActionPerformed
        abrirArchivo();
    }//GEN-LAST:event_jMenuItemAbrirActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        this.cortarSeleccionado();
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jButtonCortarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCortarActionPerformed
        this.cortarSeleccionado();
    }//GEN-LAST:event_jButtonCortarActionPerformed

    private void jButtonCopiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCopiarActionPerformed
        this.copiarSeleccionado();
    }//GEN-LAST:event_jButtonCopiarActionPerformed

    private void jButtonPegarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPegarActionPerformed
        this.pegarSeleccion();
    }//GEN-LAST:event_jButtonPegarActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        this.copiarSeleccionado();
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        this.pegarSeleccion();
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jButtonCerrarActualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCerrarActualActionPerformed
        cerrarActual(0);
    }//GEN-LAST:event_jButtonCerrarActualActionPerformed

    private void jButtonCerrarTodasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCerrarTodasActionPerformed
        cerrarTodas();
    }//GEN-LAST:event_jButtonCerrarTodasActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButtonDeshacerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeshacerActionPerformed
        this.deshacer();
    }//GEN-LAST:event_jButtonDeshacerActionPerformed

    private void jButtonRehacerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRehacerActionPerformed
        this.rehacer();
    }//GEN-LAST:event_jButtonRehacerActionPerformed

    private void jComboBoxTamañoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxTamañoItemStateChanged
        this.setearTamañoLetra();
        //System.out.println(this.jComboBoxTamaño.getSelectedIndex());
    }//GEN-LAST:event_jComboBoxTamañoItemStateChanged

    private void jButtonSalir1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSalir1ActionPerformed
        this.cerrarPrograma();
    }//GEN-LAST:event_jButtonSalir1ActionPerformed

    private void jButtonAnalisisLexico1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAnalisisLexico1ActionPerformed
        analisisLexico(true);
    }//GEN-LAST:event_jButtonAnalisisLexico1ActionPerformed

    private void jButtonAnalisisSintactico1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAnalisisSintactico1ActionPerformed
        this.analisisSintactico();
    }//GEN-LAST:event_jButtonAnalisisSintactico1ActionPerformed

    private void jButtonAnalisisSintactico2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAnalisisSintactico2ActionPerformed
        this.analisisSemantico();
    }//GEN-LAST:event_jButtonAnalisisSintactico2ActionPerformed

    private void jButtonAnalisisSintactico3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAnalisisSintactico3ActionPerformed
        this.codigoIntermedio();
    }//GEN-LAST:event_jButtonAnalisisSintactico3ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        this.deshacer();
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        this.rehacer();
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jRadioButtonMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItem1ActionPerformed
        JOptionPane.showMessageDialog(this, "Por: Alberto Vázquez Martínez\nCompiladores\nUniversidad del Valle de México");

    }//GEN-LAST:event_jRadioButtonMenuItem1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Ed.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Ed.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Ed.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Ed.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        try {
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    new Ed().setVisible(true);
                }
            });
        } catch (Exception e) {
        }

    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButtonAbrir;
    private javax.swing.JButton jButtonAnalisisLexico1;
    private javax.swing.JButton jButtonAnalisisSintactico;
    private javax.swing.JButton jButtonAnalisisSintactico1;
    private javax.swing.JButton jButtonAnalisisSintactico2;
    private javax.swing.JButton jButtonAnalisisSintactico3;
    private javax.swing.JButton jButtonCerrarActual;
    private javax.swing.JButton jButtonCerrarTodas;
    private javax.swing.JButton jButtonCopiar;
    private javax.swing.JButton jButtonCortar;
    private javax.swing.JButton jButtonDeshacer;
    private javax.swing.JButton jButtonNuevo;
    private javax.swing.JButton jButtonPegar;
    private javax.swing.JButton jButtonRehacer;
    private javax.swing.JButton jButtonSalir1;
    private javax.swing.JComboBox jComboBoxTamaño;
    private javax.swing.JLabel jLabel1;
    public javax.swing.JLabel jLabelEstado;
    private javax.swing.JLabel jLabelLengthLines;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItemAbrir;
    private javax.swing.JMenuItem jMenuItemGuardar;
    private javax.swing.JMenuItem jMenuItemGuardarComo;
    private javax.swing.JMenuItem jMenuItemNuevo;
    private javax.swing.JMenuItem jMenuItemSalir;
    private javax.swing.JPanel jPanel;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JTabbedPane jTabbedPaneMain;
    // End of variables declaration//GEN-END:variables
}
