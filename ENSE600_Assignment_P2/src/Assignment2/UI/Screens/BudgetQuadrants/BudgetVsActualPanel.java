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
    private final Theme.Palette palette;
    
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
        
        // main content 
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(palette.tileMediumDark);
        
        // middle panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(palette.tileDark);
        mainPanel.setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, palette.tileDark));
        
        JPanel buttonColumn = createRightPanel();
        
        mainContent.add(buttonColumn, BorderLayout.EAST);
        mainContent.add(mainPanel, BorderLayout.CENTER);
        
        add(mainContent, BorderLayout.CENTER);    
    }
    
    // ----- button column builder ----- //
    private JPanel createRightPanel()
    {
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(palette.tileMediumDark);
        rightPanel.setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, palette.tileDark));
        rightPanel.setPreferredSize(new Dimension(120, 0));
        
        // summary header box
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(palette.accent);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        JLabel filterLabel = new JLabel("summary", SwingConstants.CENTER);
        filterLabel.setForeground(palette.textLight);
        filterLabel.setFont(Theme.TITLE_FONT.deriveFont(18f));
        header.add(filterLabel, BorderLayout.CENTER);
        
        rightPanel.add(header);
        rightPanel.add(Box.createVerticalStrut(10));
        
        return rightPanel;
    }
    
    // ----- main refresh logic ----- //
    public void refresh()
    {
        
    }
}