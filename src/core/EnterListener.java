/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import gui.Ed;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 *
 * @author Alberto
 */
public class EnterListener implements KeyListener {

    Ed ed;

    public EnterListener(Ed ed) {
        this.ed = ed;

    }

    @Override
    public void keyTyped(KeyEvent ke) {
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        if (ke.getKeyChar() == KeyEvent.VK_ENTER) {
            //System.out.println("Enter presionado");
        } else if (ke.isControlDown() && ke.getKeyCode() == KeyEvent.VK_C) {
            System.out.println("CTRL+C");
        } else if (ke.isControlDown() && ke.getKeyCode() == KeyEvent.VK_X) {
            System.out.println("CTRL+X");
        } else if (ke.isControlDown() && ke.getKeyCode() == KeyEvent.VK_V) {
            System.out.println("CTRL+V");
        } else if (ke.isControlDown() && ke.getKeyCode() == KeyEvent.VK_Z) {
            ed.ctrlZ();
            System.out.println("CTRL+Z");
        } else if (ke.isControlDown() && ke.getKeyCode() == KeyEvent.VK_Y) {
            ed.ctrlY();
            System.out.println("CTRL+Y");
        }
    }

    @Override
    public void keyReleased(KeyEvent ke) {
    }
}
