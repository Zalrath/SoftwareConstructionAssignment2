/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Assignment2.UI.Template;

/**
 *
 * @author megan
 */

import Assignment2.UI.Theme;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

// ----- Theme & Template for dialog ----- // 
public abstract class BaseThemedDialog extends JDialog 
{
    // ----- Constructor ----- //
    public BaseThemedDialog(Window owner, String title, ModalityType modalityType) 
    {
        super(owner, title, modalityType);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        // change the surface colour
        getContentPane().setBackground(Theme.accentColour);
    }
    
    // ----- Util ----- //
    protected JLabel makeLabel(String txt)  // themed label
    {
        JLabel l = new JLabel(txt);
        l.setFont(Theme.BODY_FONT);
        l.setForeground(Theme.palette().textLight);
        return l;
    }
}

