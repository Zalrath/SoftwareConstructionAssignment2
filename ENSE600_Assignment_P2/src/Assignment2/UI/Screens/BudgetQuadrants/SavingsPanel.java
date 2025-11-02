/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
 /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Assignment2.UI.Screens.BudgetQuadrants;

/**
 *
 * @author megan
 */

import Assignment2.UI.Theme;
import Assignment2.Inventory.InventoryManager;

import javax.swing.*;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.*;
import java.time.Month;

public class SavingsPanel extends JPanel
{
    private final InventoryManager manager;
    private Theme.Palette palette;  
    
    private static final double MAX_SAVINGS_VALUE = 1000.00;
    
    private final JProgressBar[] monthBars = new JProgressBar[12];
    private final JLabel[] monthLabels = new JLabel[12];
    
    // ----- constructor ----- //
    public SavingsPanel(InventoryManager manager, Theme.Palette palette)
    {
        this.manager = manager;
        this.palette = palette;
        buildUI();
    }
    
    // ----- build the UI ----- //
    private void buildUI()
    {
        removeAll(); // prevent duplicate table 
        this.palette = Theme.palette();
        
        setLayout(new BorderLayout());
        setBackground(palette.tileMediumDark);
        setBorder(BorderFactory.createLineBorder(palette.tileDark, 2));
        
        // header
        JLabel header = new JLabel("savings by month", SwingConstants.CENTER);
        header.setFont(Theme.TITLE_FONT.deriveFont(28f));
        header.setForeground(palette.textLight);
        header.setPreferredSize(new Dimension(300, 45));
        header.setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, palette.tileDark));
        add(header, BorderLayout.NORTH);
        
        // main content container
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(palette.tileMediumDark);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(palette.tileDark);
        mainPanel.setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, palette.tileDark));
        
        JPanel barsContainer = new JPanel(new GridBagLayout());
        barsContainer.setBackground(palette.tileDark);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.insets = new Insets(0, 4, 0, 4);
        
        // create bars for each month
        for (int i = 0; i < 12; i++) 
        {
            JPanel monthPanel = new JPanel(new BorderLayout(0, 4));
            monthPanel.setBackground(palette.tileDark);
            
            // max value label
            JLabel maxValueLabel = new JLabel(String.format("$%.2f", MAX_SAVINGS_VALUE), SwingConstants.CENTER);
            maxValueLabel.setFont(Theme.TITLE_FONT.deriveFont(Font.PLAIN, 12f)); 
            maxValueLabel.setForeground(palette.textLight.darker());
            
            // progress bar
            JProgressBar bar = new JProgressBar(SwingConstants.VERTICAL, 0, 100);
            bar.setValue((int) (Math.random() * 120));
            bar.setUI(new VerticalBarUI());
            bar.setStringPainted(true);
            bar.setFont(Theme.BODY_FONT.deriveFont(13f)); 
            bar.setForeground(bar.getValue() > 100 ? palette.hazard : palette.accent);
            bar.setBackground(palette.tileMediumDark);
            bar.setBorder(BorderFactory.createLineBorder(palette.tileMediumDark, 1));
            bar.setPreferredSize(new Dimension(40, 0)); 
            
            // month label
            JLabel label = new JLabel(Month.of(i + 1).name().substring(0, 3), SwingConstants.CENTER);
            label.setFont(Theme.TITLE_FONT.deriveFont(Font.BOLD, 15f)); 
            label.setForeground(palette.textLight);
            
            // add elements
            monthPanel.add(maxValueLabel, BorderLayout.NORTH);
            monthPanel.add(bar, BorderLayout.CENTER);
            monthPanel.add(label, BorderLayout.SOUTH);
            
            gbc.gridx = i;
            barsContainer.add(monthPanel, gbc);
            
            monthBars[i] = bar;
            monthLabels[i] = label;
        }
        
        mainPanel.add(barsContainer, BorderLayout.CENTER);
        mainContent.add(mainPanel, BorderLayout.CENTER);
        add(mainContent, BorderLayout.CENTER);
        
        revalidate();
        repaint();
    }
    
    // ----- custom vertical bar ui -----
    private class VerticalBarUI extends BasicProgressBarUI
    {
        @Override
        protected void paintDeterminate(Graphics g, JComponent c)
        {
            Graphics2D g2 = (Graphics2D) g;
            Theme.Palette p = Theme.palette();
            
            Insets insets = progressBar.getInsets();
            int barWidth = progressBar.getWidth() - (insets.left + insets.right);
            int barHeight = progressBar.getHeight() - (insets.top + insets.bottom);
            
            // background
            g2.setColor(p.tileMediumDark);
            g2.fillRect(insets.left, insets.top, barWidth, barHeight);
            
            // fill height
            int value = progressBar.getValue();
            int filled = (int) ((value / 100.0) * barHeight);
            int y = progressBar.getHeight() - filled - insets.bottom;
            
            // fill color
            g2.setColor(value > 100 ? p.hazard : p.accent);
            g2.fillRect(insets.left, y, barWidth, filled);
            
            // border
            g2.setColor(p.tileDark);
            g2.drawRect(insets.left, insets.top, barWidth - 1, barHeight - 1);
            
            // text
            String text = progressBar.getString();
            if (progressBar.isStringPainted())
            {
                g2.setFont(progressBar.getFont()); 
                g2.setColor(p.textLight);
                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(text);
                int textX = (progressBar.getWidth() - textWidth) / 2;
                int textY = y + 15;
                g2.drawString(text, textX, Math.max(fm.getAscent() + 2, textY));
            }
        }
    }
    
    // ----- refresh logic test ----- //
    public void refresh()
    {
        for (int i = 0; i < 12; i++) {
            int percentVal = (int) (Math.random() * 130); 
            double currentSavings = (percentVal / 100.0) * MAX_SAVINGS_VALUE;
            
            monthBars[i].setValue(Math.min(percentVal, 100));
            monthBars[i].setForeground(percentVal > 100 ? palette.hazard : palette.accent);
            monthBars[i].setString(String.format("$%.2f", currentSavings));
        }
    }
}
