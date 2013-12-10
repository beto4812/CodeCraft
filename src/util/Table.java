package util;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class Table extends JFrame implements Serializable {

    private JPanel contentPane;
    private JTable table;
    private Object[][] objetosTabla = {
        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},};
    private String[] columnas;
    DefaultTableModel modelo;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Table frame = new Table();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public Table() {
        columnas = new String[objetosTabla[0].length];
        for (int i = 0; i < objetosTabla.length; i++) {
            columnas[i] = "c" + i;
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);

        JScrollPane scrollPane = new JScrollPane();
        GroupLayout gl_contentPane = new GroupLayout(contentPane);
        gl_contentPane.setHorizontalGroup(
                gl_contentPane.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_contentPane.createSequentialGroup()
                .addGap(2)
                .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 422, Short.MAX_VALUE)));
        gl_contentPane.setVerticalGroup(
                gl_contentPane.createParallelGroup(Alignment.LEADING)
                .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 252, Short.MAX_VALUE));

        this.modelo = new DefaultTableModel(objetosTabla, columnas);
        table = new JTable();
        table.addKeyListener(new TablaListener());
        table.setModel(modelo);
        scrollPane.setViewportView(table);
        new PegarExcel(table);
        contentPane.setLayout(gl_contentPane);
        this.requestFocus();
    }
    
    public String[][] obtenerInformacion(){
        
        int numFilas = this.modelo.getRowCount();
        int numColumnas = modelo.getColumnCount();
        String matrix[][] = new String[numFilas][numColumnas];
        for (int rowIndex = 0; rowIndex < numFilas; rowIndex++) {
            for (int colIndex = 0; colIndex < numColumnas; colIndex++) {
                matrix[rowIndex][colIndex] = (String)modelo.getValueAt(rowIndex, colIndex);
            }
        }
        return matrix;
    }

    public void guardarTabla() {
        try {
            ObjectOutputStream salida = new ObjectOutputStream(new FileOutputStream("media.obj"));
            salida.writeObject(table);
            salida.close();
            
            File archivo = new File("tabla.tabla"); 
            PrintWriter salida2 = new PrintWriter(new FileWriter(archivo));
            String data[][] = obtenerInformacion();
            for (int i = 0; i < data.length; i++) {
                salida2.print(data[i][0]);
                for (int j = 0; j < data.length; j++) {
                    
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        
    }

    private class TablaListener implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if ((e.getKeyCode() == KeyEvent.VK_S) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                System.out.println("woot!");
                guardarTabla();
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }
    }
}