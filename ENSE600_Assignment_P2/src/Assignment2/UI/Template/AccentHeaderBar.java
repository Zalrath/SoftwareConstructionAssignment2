/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Assignment2.UI.Template;

import Assignment2.UI.Theme;
import javax.swing.*;
import java.awt.*;

/**
 *
 * @author megan
 */

public class AccentHeaderBar extends JPanel 
{
    private final JLabel titleLabel;
    
    public AccentHeaderBar(String titleText) 
    {
        super(new BorderLayout());
        setPreferredSize(new Dimension(0, 80));
        setOpaque(true);
        setBackground(Theme.getAccent());
        
        titleLabel = new JLabel(titleText, SwingConstants.CENTER);
        titleLabel.setFont(Theme.HEADER_FONT);
        titleLabel.setForeground(Color.WHITE);
        add(titleLabel, BorderLayout.CENTER);
    }

    @Override
    public void updateUI() 
    {
        super.updateUI();
        if (titleLabel != null) 
        {
            setBackground(Theme.getAccent());
            titleLabel.setForeground(Color.WHITE);
        }
    }
    
    public void setTitle(String text) 
    {
        titleLabel.setText(text);
    }
}
