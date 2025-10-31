/*
 * click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
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

    // a placeholder for the monthly savings goal (for demonstration)
    private static final double MAX_SAVINGS_VALUE = 1000.00;

    // store bars for each month
    private final JProgressBar[] monthBars = new JProgressBar[12];
    private final JLabel[] monthLabels = new JLabel[12];

    // ----- constructor ----- //
    public SavingsPanel (InventoryManager manager, Theme.Palette palette)
    {
        this.manager = manager;
        this.palette = palette;
        buildUI();
    }
    
    // ----- initialise ui ----- //
    private void buildUI()
    {
        setLayout(new BorderLayout());
        
        // update theme variables before building
        this.palette = Theme.palette();
        
        setBackground(palette.tileMediumDark);
        setBorder(BorderFactory.createLineBorder(palette.tileDark, 2));
        
        // header
        JLabel header = new JLabel("savings by month", SwingConstants.CENTER);
        header.setFont(Theme.TITLE_FONT.deriveFont(28f));
        header.setForeground(palette.textLight);
        header.setPreferredSize(new Dimension(300, 45));
        header.setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, palette.tileDark));
        add(header, BorderLayout.NORTH);
        
        // main content
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(palette.tileMediumDark);
        
        // middle panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(palette.tileDark);
        mainPanel.setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, palette.tileDark));

        // bar container (bottom-aligned)
        JPanel barsContainer = new JPanel(new GridBagLayout());
        barsContainer.setBackground(palette.tileDark);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.SOUTH; // align all to bottom
        gbc.insets = new Insets(0, 4, 0, 4);

        JPanel barsPanel = new JPanel();
        barsPanel.setLayout(new BoxLayout(barsPanel, BoxLayout.X_AXIS));
        barsPanel.setBackground(palette.tileDark);
        barsPanel.setAlignmentY(Component.BOTTOM_ALIGNMENT);

        // create bars
        for (int i = 0; i < 12; i++) {
            JPanel monthPanel = new JPanel();
            monthPanel.setLayout(new BoxLayout(monthPanel, BoxLayout.Y_AXIS));
            monthPanel.setBackground(palette.tileDark);
            
            // max value label
            JLabel maxValueLabel = new JLabel(String.format("$%.2f", MAX_SAVINGS_VALUE), SwingConstants.CENTER);
            maxValueLabel.setFont(Theme.TITLE_FONT.deriveFont(Font.PLAIN, 12f)); 
            maxValueLabel.setForeground(palette.textLight.darker());
            maxValueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // vertical bar
            JProgressBar bar = new JProgressBar(SwingConstants.VERTICAL, 0, 100);
            bar.setValue((int) (Math.random() * 120)); // placeholder random value
            bar.setUI(new VerticalBarUI());
            bar.setStringPainted(true);
            
            bar.setFont(Theme.BODY_FONT.deriveFont(13f)); 
            
            bar.setForeground(bar.getValue() > 100 ? palette.hazard : palette.accent);
            bar.setBackground(palette.tileMediumDark);
            bar.setBorder(BorderFactory.createLineBorder(palette.tileMediumDark, 1));
            
            bar.setPreferredSize(new Dimension(40, 270)); 
            bar.setMaximumSize(new Dimension(40, Integer.MAX_VALUE)); // lock width, allow stretching up

            // label
            JLabel label = new JLabel(Month.of(i + 1).name().substring(0, 3), SwingConstants.CENTER);
            label.setFont(Theme.TITLE_FONT.deriveFont(Font.BOLD, 15f)); 
            label.setForeground(palette.textLight);
            label.setAlignmentX(Component.CENTER_ALIGNMENT);

            // ensure bars sit on the bottom 
            monthPanel.add(Box.createVerticalGlue()); // glue is now at the top to push elements down
            monthPanel.add(maxValueLabel);
            monthPanel.add(Box.createVerticalStrut(1)); 
            monthPanel.add(bar);
            monthPanel.add(Box.createVerticalStrut(1));
            monthPanel.add(label);
            
            monthBars[i] = bar;
            monthLabels[i] = label;

            barsPanel.add(monthPanel);
            if (i < 11) barsPanel.add(Box.createHorizontalStrut(10));
        }

        gbc.weightx = 1;
        gbc.weighty = 1;
        barsContainer.add(barsPanel, gbc);

        mainPanel.add(barsContainer, BorderLayout.CENTER);
        mainContent.add(mainPanel, BorderLayout.CENTER);
        add(mainContent, BorderLayout.CENTER);
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

            // text (centered inside bar near top)
            String text = progressBar.getString();
            if (progressBar.isStringPainted()) {
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

    // ----- main refresh logic ----- //
    public void refresh()
    {
        for (int i = 0; i < 12; i++) {
            int percentVal = (int) (Math.random() * 130); 
            
            // calculate the actual dollar amount saved based on the percentage
            double currentSavings = (percentVal / 100.0) * MAX_SAVINGS_VALUE;

            // update the JProgressBar value (clamped to 100% for the visual fill)
            monthBars[i].setValue(Math.min(percentVal, 100));
            monthBars[i].setForeground(percentVal > 100 ? palette.hazard : palette.accent);
            
            // set the string to the dollar amount
            // NOTE: We rely on this string being painted in VerticalBarUI
            monthBars[i].setString(String.format("$%.2f", currentSavings)); 
        }
    }
}