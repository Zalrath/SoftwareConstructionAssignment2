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

import java.awt.*;

public class BudgetVsActualPanel extends JPanel
{

    private final InventoryManager manager;
    private final Theme.Palette palette; // renamed 'p' to 'palette' for clarity
    
    // ----- constructor ----- //
    public BudgetVsActualPanel(InventoryManager manager, Theme.Palette palette)
    {
        this.manager = manager;
        this.palette = palette;
        buildUI();
    }
    
    // ----- initialise ui ----- //
    private void buildUI() 
    {
        setLayout(new BorderLayout());

        setBackground(palette.tileMediumDark);
        setBorder(BorderFactory.createLineBorder(palette.tileDark, 2));

        // header
        JLabel header = new JLabel("budget vs actual", SwingConstants.CENTER);
        header.setFont(Theme.TITLE_FONT.deriveFont(28f));
        header.setForeground(palette.textLight);
        header.setPreferredSize(new Dimension(300, 45));
        header.setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, palette.tileDark));
        add(header, BorderLayout.NORTH);
        
        // container
        JPanel contentPanel = new JPanel(new BorderLayout(10, 0)); // 10px gap between panels
        contentPanel.setOpaque(false);
        add(contentPanel, BorderLayout.CENTER);

        
        // middle panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(palette.tileDark);
        mainPanel.setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, palette.tileDark));

        
        // right panel
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(palette.tileMediumDark);
        rightPanel.setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, palette.tileDark));
        rightPanel.setPreferredSize(new Dimension(120, 0));

        JPanel rightHeader = new JPanel(new BorderLayout());
        rightHeader.setBackground(palette.accent);
        rightHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        
        JLabel summaryLabel = new JLabel("summary", SwingConstants.CENTER);
        summaryLabel.setForeground(palette.textLight);
        summaryLabel.setFont(Theme.TITLE_FONT.deriveFont(18f));
        rightHeader.add(summaryLabel, BorderLayout.CENTER);
        
        
      
        
        rightPanel.add(rightHeader);
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        

        // placeholder for graph
        JPanel barPlaceholder = new JPanel();
        barPlaceholder.setBackground(palette.tileDark);
        
        // placeholder label
        JLabel placeholderLabel = new JLabel("(piechart goes here)", SwingConstants.CENTER);
        placeholderLabel.setForeground(palette.textLight.darker());
        barPlaceholder.add(placeholderLabel);

        contentPanel.add(mainPanel, BorderLayout.CENTER);
        contentPanel.add(rightPanel, BorderLayout.EAST);
    }

    // ----- external refresh ----- //
    public void refresh()
    {
        // todo: recompute chart data from inventorymanager
    }
}