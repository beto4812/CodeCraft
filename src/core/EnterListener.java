/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 *
 * @author Alberto
 */
public class EnterListener implements KeyListener {

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
        }
    }

    @Override
    public void keyReleased(KeyEvent ke) {
    }
}
