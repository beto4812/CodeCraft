/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import gui.Ed;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author Alberto
 */
public class MyDocumentListener implements DocumentListener{
    
    Ed edit;
    
    public MyDocumentListener(Ed edit){
        
        this.edit = edit;
    }

    @Override
    public void insertUpdate(DocumentEvent de) {
        edit.actualizarEditor();
    }

    @Override
    public void removeUpdate(DocumentEvent de) {
        edit.actualizarEditor();
    }

    @Override
    public void changedUpdate(DocumentEvent de) {
        edit.actualizarEditor();
    }
    
    
    
}
