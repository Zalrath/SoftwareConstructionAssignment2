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

public class SavingsPanel extends JPanel
{

    private final InventoryManager manager;
    private Theme.Palette palette;  
    
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
        JLabel header = new JLabel("saving by month", SwingConstants.CENTER);
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
        
        mainContent.add(mainPanel, BorderLayout.CENTER);
        
        add(mainContent, BorderLayout.CENTER); 
    }
    
    // ----- main refresh logic ----- //
    public void refresh()
    {
        
    }
}