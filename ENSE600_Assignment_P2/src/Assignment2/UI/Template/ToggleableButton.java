/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Assignment2.UI.Template;

/**
 *
 * @author megan
 */

import Assignment2.UI.Theme ;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter ;
import java.awt.event.MouseEvent ;


public class ToggleableButton extends JButton
{
    private boolean selected = false;
    private boolean grouped = false; // true when in a group array
    private final Theme.Palette palette; // theme 
    private ToggleableButtonGroup parentGroup = null;
    
    // ----- constructor ----- //
    public ToggleableButton(String text)
    {
        super(text);
        this.palette = Theme.palette();
        
        initiateStyle();
        installMouseBehavior();
    }
    
    // ----- start ui and style ----- //
    private void initiateStyle()
    {
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setFont(Theme.TITLE_FONT.deriveFont(16f));
        setForeground(palette.textLight);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setMargin(new Insets(8, 16, 8, 16));
    }
    
    // ----- paint the theme ----- //
    @Override
    protected void paintComponent(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        Color fill = selected ? palette.tileDark : palette.tileMediumDark;
        Color border = selected ? palette.accent : palette.tileDark;
        
        // background fill
        g2.setColor(fill);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        
        // draw selection border
        if (selected)
        {
            g2.setStroke(new BasicStroke(2f));
            g2.setColor(border);
            g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 10, 10);
        }
        
        // draw text
        FontMetrics fm = g2.getFontMetrics(getFont());
        int textX = (getWidth() - fm.stringWidth(getText())) / 2;
        int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
        
        g2.setColor(palette.textLight);
        g2.drawString(getText(), textX, textY);
        
        g2.dispose();
    }
    
    // ----- handlers ----- //
    private void installMouseBehavior()
    {
        addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent e)
            {
                if (grouped && parentGroup != null)
                {
                    parentGroup.selectButton(ToggleableButton.this);
                }
                else
                {
                    toggleSelected();
                }
            }
            
            // repaint on click
            @Override
            public void mouseEntered(MouseEvent e)
            {
                repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e)
            {
                repaint();
            }
        });
    }
    
    // -----api ----- //
    public boolean isSelected()
    {
        return selected;
    }
    
    public void setSelected(boolean selected)
    {
        this.selected = selected;
        repaint();
    }
    
    public void toggleSelected()
    {
        this.selected = !this.selected;
        repaint();
    }
    
    // group manager
    void setGrouped(boolean grouped, ToggleableButtonGroup group)
    {
        this.grouped = grouped;
        this.parentGroup = group;
    }
}