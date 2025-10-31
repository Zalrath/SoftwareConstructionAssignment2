/*
 * click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Assignment2.UI.Template;

/**
 *
 * @author megan
 */

import Assignment2.UI.Theme;

import javax.swing.*;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.*;

public class ThemedProgressBar extends JPanel 
{
    // ----- public fields ----- //
    public final JProgressBar progressBar;
    public final JLabel label;
    private final JLabel maxValueLabel; 
    
    private double maxDollarValue = 100.00;
    
    private class CustomProgressBarUI extends BasicProgressBarUI 
    {
        @Override
        protected void paintDeterminate(Graphics g, JComponent c) 
        {
            if (!(g instanceof Graphics2D)) return;
            
            Theme.Palette currentPalette = Theme.palette();
            Graphics2D g2 = (Graphics2D) g;
            
            Insets insets = progressBar.getInsets();
            int width = progressBar.getWidth() - (insets.left + insets.right);
            int height = progressBar.getHeight() - (insets.top + insets.bottom);
            
            // draw track
            g2.setColor(currentPalette.tileMediumDark);
            g2.fillRect(insets.left, insets.top, width, height);
            
            // compute fill
            int min = progressBar.getMinimum();
            int max = progressBar.getMaximum();
            int value = progressBar.getValue();
            int amountFilled = (int) Math.round((double)(value - min) / (max - min) * width);
            
            // fill progress
            g2.setColor(progressBar.getForeground());
            g2.fillRect(insets.left, insets.top, amountFilled, height);
            
            // border
            g2.setColor(currentPalette.tileDark);
            g2.setStroke(new BasicStroke(1));
            g2.drawRect(insets.left, insets.top, width, height);
            
            // text
            if (progressBar.isStringPainted()) 
            {
                paintString(g, insets.left, insets.top, width, height, amountFilled, insets);
            }
        }

        @Override
        protected void paintString(Graphics g, int x, int y, int width, int height, int amountFilled, Insets insets) 
        {
            Theme.Palette currentPalette = Theme.palette();
            String text = progressBar.getString();
            Graphics2D g2 = (Graphics2D) g;
            
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            
            FontMetrics fm = g2.getFontMetrics();
            int textX = x + (width - fm.stringWidth(text)) / 2;
            int textY = y + ((height + fm.getAscent() - fm.getDescent()) / 2);
            
            // always draw the text with textlight color
            g2.setColor(currentPalette.textLight);
            g2.setFont(progressBar.getFont());
            g2.drawString(text, textX, textY);
        }
    }
    // ----- constructor ----- //
    public ThemedProgressBar(String labelText, double initialValue) 
    {
        super(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        
        Theme.Palette currentPalette = Theme.palette();
        
        // header
        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);
        
        label = new JLabel(labelText);
        label.setForeground(currentPalette.textLight);
        label.setFont(Theme.BODY_FONT.deriveFont(Font.BOLD, 18f));
        headerRow.add(label, BorderLayout.WEST);
        
        maxValueLabel = new JLabel(String.format("$%.2f", maxDollarValue));
        maxValueLabel.setForeground(currentPalette.textLight.darker());
        maxValueLabel.setFont(Theme.BODY_FONT.deriveFont(Font.PLAIN, 16f));
        headerRow.add(maxValueLabel, BorderLayout.EAST);
        
        add(headerRow, BorderLayout.NORTH);
        
        // progress bar
        progressBar = new JProgressBar(0, 100);
        progressBar.setUI(new CustomProgressBarUI());
        progressBar.setStringPainted(true);
        progressBar.setBackground(currentPalette.tileMediumDark);
        progressBar.setFont(Theme.TITLE_FONT.deriveFont(16f));
        progressBar.setBorder(BorderFactory.createEmptyBorder());
        progressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        
        add(progressBar, BorderLayout.CENTER);
        
        updateValue(initialValue);
    }
    
    // ----- changes /100% to / any other number ----- //
    public void setMaxDollarValue(double maxValue) 
    {
        this.maxDollarValue = maxValue;
        maxValueLabel.setText(String.format("$%.2f", maxDollarValue));
    }
    
    // ----- update value method ----- // 
    public void updateValue(double value) 
    {
        Theme.Palette currentPalette = Theme.palette();
        
        // update label colors and fonts dynamically
        label.setForeground(currentPalette.textLight);
        label.setFont(Theme.TITLE_FONT.deriveFont(Font.BOLD, 18f));
        
        // update the right-side max value label (if it exists)
        if (this instanceof ThemedProgressBar) 
        {
            // defensive check; ensure label exists
            try 
            {
                java.lang.reflect.Field f = this.getClass().getDeclaredField("maxValueLabel");
                f.setAccessible(true);
                JLabel maxLabel = (JLabel) f.get(this);
                if (maxLabel != null) 
                {
                    maxLabel.setForeground(currentPalette.textLight.darker());
                    maxLabel.setFont(Theme.TITLE_FONT.deriveFont(Font.PLAIN, 16f));
                }
            }
            catch (Exception ignore) 
            {
                
            }
        }
        
        // update progress bar background & font
        progressBar.setBackground(currentPalette.tileMediumDark);
        progressBar.setFont(Theme.TITLE_FONT.deriveFont(16f));
        
        // clamp visual bar but show full value text
        double cappedValue = Math.min(value, 100);
        Color fillColor = (value > 100) ? currentPalette.hazard : currentPalette.accent;
        
        progressBar.setForeground(fillColor);
        progressBar.setValue((int) Math.round(cappedValue));
        
        // get dollar value for label
        double dollarEquivalent = (value / 100.0) * 100.00; // or replace with your maxdollarvalue field
        
        // use textlight for bar text
        if (value > 100) 
        {
            progressBar.setString(String.format("%.1f%% ($%.2f) – over budget!", value, dollarEquivalent));
        } else 
        {
            progressBar.setString(String.format("%.1f%% ($%.2f)", value, dollarEquivalent));
        }
        
        // force ui to re-render using the new palette
        progressBar.repaint();
    }
}