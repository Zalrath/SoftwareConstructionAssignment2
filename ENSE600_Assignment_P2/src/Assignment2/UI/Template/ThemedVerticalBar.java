/*
 * click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Assignment2.UI.Template;

/**
 * @author megan
 */

import Assignment2.UI.Theme;

import javax.swing.*;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.*;
import java.text.DecimalFormat;

public class ThemedVerticalBar extends JPanel
{
    // ----- public fields ----- //
    public final JProgressBar progressBar;
    public final JLabel label;
    private double maxDollarValue = 100.00;
    
    // Decimal format for consistent output
    private static final DecimalFormat dollarFormat = new DecimalFormat("$#,##0.00");

    private class CustomVerticalBarUI extends BasicProgressBarUI
    {
        @Override
        protected void paintDeterminate(Graphics g, JComponent c)
        {
            if (!(g instanceof Graphics2D)) return;
            
            Theme.Palette currentPalette = Theme.palette();
            Graphics2D g2 = (Graphics2D) g;
            
            Insets insets = progressBar.getInsets();
            int barWidth = progressBar.getWidth() - (insets.left + insets.right);
            int barHeight = progressBar.getHeight() - (insets.top + insets.bottom);
            
            // draw track (background)
            g2.setColor(currentPalette.tileMediumDark);
            g2.fillRect(insets.left, insets.top, barWidth, barHeight);
            
            // compute fill
            int min = progressBar.getMinimum();
            int max = progressBar.getMaximum();
            int value = progressBar.getValue();
            
            // vertical filling calculation (from bottom up)
            int amountFilled = (int) Math.round((double)(value - min) / (max - min) * barHeight);
            int y = progressBar.getHeight() - amountFilled - insets.bottom;

            // fill progress
            g2.setColor(progressBar.getForeground());
            g2.fillRect(insets.left, y, barWidth, amountFilled);
            
            // border
            g2.setColor(currentPalette.tileDark);
            g2.setStroke(new BasicStroke(1));
            g2.drawRect(insets.left, insets.top, barWidth, barHeight);
            
            // text
            if (progressBar.isStringPainted())
            {
                // Pass y for the top of the filled area
                paintString(g, insets.left, y, barWidth, amountFilled, insets); 
            }
        }

        // Custom vertical paint string method
        protected void paintString(Graphics g, int x, int y, int width, int height, Insets insets)
        {
            Theme.Palette currentPalette = Theme.palette();
            String text = progressBar.getString();
            Graphics2D g2 = (Graphics2D) g;
            
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setFont(progressBar.getFont());
            g2.setColor(currentPalette.textLight);
            
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            
            // center text horizontally
            int textX = x + (width - textWidth) / 2;
            
            // position text vertically near the top of the filled area
            int textY = y + fm.getAscent() + 5; 
            
            // ensure text is always drawn within the bar boundaries (avoid drawing outside the panel)
            textY = Math.max(textY, fm.getAscent() + 2);

            g2.drawString(text, textX, textY);
        }
    }
    
    // ----- constructor ----- //
    public ThemedVerticalBar(String labelText, double initialValue, double maxDollarValue)
    {
        // Use BoxLayout for vertical stacking: max value label, vertical bar, month label
        super(); 
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        // VITAL for stretch: Ensure the component itself is aligned to TOP_ALIGNMENT 
        // to take up full vertical space within its parent (barsPanel)
        setAlignmentY(Component.TOP_ALIGNMENT); 

        Theme.Palette currentPalette = Theme.palette();
        
        // --- 1. Max Value Label (at the top of the column) ---
        JLabel maxValueLabel = new JLabel(dollarFormat.format(maxDollarValue), SwingConstants.CENTER);
        maxValueLabel.setForeground(currentPalette.textLight.darker());
        maxValueLabel.setFont(Theme.TITLE_FONT.deriveFont(Font.PLAIN, 12f));
        maxValueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // --- 2. Vertical Progress Bar ---
        progressBar = new JProgressBar(SwingConstants.VERTICAL, 0, 100);
        progressBar.setUI(new CustomVerticalBarUI());
        progressBar.setStringPainted(true);
        progressBar.setBackground(currentPalette.tileMediumDark);
        progressBar.setFont(Theme.BODY_FONT.deriveFont(Font.BOLD, 13f));
        progressBar.setBorder(BorderFactory.createLineBorder(currentPalette.tileMediumDark, 1));
        
        // Set preferred size to 270, which should be honored by the layout
        progressBar.setPreferredSize(new Dimension(40, 270)); 
        progressBar.setMaximumSize(new Dimension(40, Integer.MAX_VALUE)); 
        progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // --- 3. Month Label (at the bottom of the column) ---
        label = new JLabel(labelText, SwingConstants.CENTER);
        label.setForeground(currentPalette.textLight);
        label.setFont(Theme.TITLE_FONT.deriveFont(Font.BOLD, 15f));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // --- Stacking Components (With Double Glue for Vertical Stretch) ---
        add(Box.createVerticalGlue()); // 1. Pushes content down (fills space above max value label)
        add(maxValueLabel);
        add(Box.createVerticalStrut(5));
        add(progressBar);
        add(Box.createVerticalStrut(8));
        add(label);
        add(Box.createVerticalGlue()); // 2. Pushes content up (fills space below month label)

        // Initialize value and max
        this.maxDollarValue = maxDollarValue;
        updateValue(initialValue);
    }
    
    // ----- update value method ----- // 
    public void updateValue(double value)
    {
        Theme.Palette currentPalette = Theme.palette();
        
        // clamp visual bar but show full value text
        double cappedValue = Math.min(value, 100);
        Color fillColor = (value > 100) ? currentPalette.hazard : currentPalette.accent;
        
        progressBar.setForeground(fillColor);
        progressBar.setValue((int) Math.round(cappedValue));
        
        // calculate dollar equivalent based on actual value (which may exceed 100%)
        double dollarEquivalent = (value / 100.0) * this.maxDollarValue;
        
        // Set the string to the dollar amount
        progressBar.setString(dollarFormat.format(dollarEquivalent));
        
        // force ui to re-render using the new palette
        progressBar.repaint();
    }
}