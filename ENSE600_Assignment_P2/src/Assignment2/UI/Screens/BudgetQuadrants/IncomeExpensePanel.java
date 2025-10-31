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

public class IncomeExpensePanel extends JPanel
{
    // ----- instance fields ----- //
    private final InventoryManager manager;
    
    // ----- constructor ----- //
    public IncomeExpensePanel(InventoryManager manager, Theme.Palette palette)
    {
        this.manager = manager; 
        
        buildUI(); 
    }
    
    // ----- theme refresh override ----- //
    @Override
    public void updateUI() 
    {
        super.updateUI();
        
        SwingUtilities.invokeLater(this::buildUI);
    }
    
    // ----- initialise ui ----- //
    private void buildUI()
    {
        Theme.Palette currentPalette = Theme.palette();
        
        // layout
        setLayout(new BorderLayout());
        setBackground(currentPalette.tileDark);
        setBorder(BorderFactory.createLineBorder(currentPalette.tileDark, 4));
        
        // header and containers
        JPanel headerContainer = createDualHeaderBar(currentPalette, "current income", "current expenses");
        add(headerContainer, BorderLayout.NORTH);
        
        JPanel contentContainer = new JPanel(new GridLayout(1, 2, 10, 0));
        contentContainer.setOpaque(false);
        
        JPanel income = createSection(currentPalette, null);
        JPanel expenses = createSection(currentPalette, null);
        
        contentContainer.add(income);
        contentContainer.add(expenses);
        add(contentContainer, BorderLayout.CENTER);
    }
    
    // ----- dual header bar builder ----- //
    private JPanel createDualHeaderBar(Theme.Palette currentPalette, String title1, String title2)
    {
        JPanel container = new JPanel(new GridLayout(1, 2));
        container.setBackground(currentPalette.tileDark);
        container.setPreferredSize(new Dimension(0, 45));
        
        // ----- header 1 ----- //
        JLabel header1 = new JLabel(title1, SwingConstants.CENTER);
        header1.setFont(Theme.TITLE_FONT.deriveFont(28f));
        header1.setForeground(currentPalette.textLight);
        header1.setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, currentPalette.tileDark));
        header1.setBackground(currentPalette.tileMediumDark);
        header1.setOpaque(true);
        container.add(header1);
        
        // ----- header 2 ----- // 
        JLabel header2 = new JLabel(title2, SwingConstants.CENTER);
        header2.setFont(Theme.TITLE_FONT.deriveFont(28f));
        header2.setForeground(currentPalette.textLight);
        header2.setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, currentPalette.tileDark));
        header2.setBackground(currentPalette.tileMediumDark);
        header2.setOpaque(true);
        container.add(header2);
        
        return container;
    }
    
    // ----- section builder ----- //
    private JPanel createSection(Theme.Palette currentPalette, String title)
    {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(currentPalette.tileDark);
        
        JTextArea area = new JTextArea();
        area.setText("program here");
        area.setBackground(currentPalette.tileDark);
        area.setForeground(currentPalette.textLight);
        area.setFont(Theme.BODY_FONT.deriveFont(14f));
        area.setEditable(false);
        area.setMargin(new Insets(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(area);
        scrollPane.setBorder(null);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
    
    // ----- external refresh ----- //
    public void refresh()
    {
        // todo: load new income/expense data from manager
    }
}