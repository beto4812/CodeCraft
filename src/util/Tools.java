/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

/**
 *
 * @author Alberto
 */
public class Tools {
    
    public ImageIcon resizeAndLoadIcon(String url, int ancho, int alto){
        
        ImageIcon imgTemp = new ImageIcon(getClass().getResource(url));
        Image img = imgTemp.getImage();
        Image img2 = img.getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
        
        
        return new ImageIcon(img2);
        
    }
    
}
