/*
 * click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Assignment2.UI.Screens.BudgetQuadrants;

/**
 * @author megan
 */

import Assignment2.UI.Theme;
import Assignment2.Inventory.InventoryManager;
import Assignment2.UI.Template.ThemedProgressBar;


import javax.swing.*;
import java.awt.*;

public class BudgetVsActualPanel extends JPanel 
{
    private final InventoryManager manager;
    
    // progress bar panels are now the fields, replacing jprogressbar and jlabel fields
    private ThemedProgressBar weeklyPanel;
    private ThemedProgressBar monthlyPanel;
    private ThemedProgressBar yearlylPanel;
    private ThemedProgressBar alltimePanel;
    
    // ----- constructor ----- //
    public BudgetVsActualPanel(InventoryManager manager, Theme.Palette palette) 
    {
        this.manager = manager;
        buildUI();
    }
    
    // ----- theme refresh override ----- //
    @Override
    public void updateUI() 
    {
        super.updateUI();
        
        // when the theme changes, rebuild the entire ui to ensure all colours are fresh
        SwingUtilities.invokeLater(this::buildUI);
    }
    
    // ----- initialise ui ----- //
    private void buildUI() 
    {
        Theme.Palette currentPalette = Theme.palette();
        
        this.removeAll();

        setLayout(new BorderLayout());
        setBackground(currentPalette.tileMediumDark);
        setBorder(BorderFactory.createLineBorder(currentPalette.tileDark, 2));
        
        // header
        JLabel header = new JLabel("expenses vs income", SwingConstants.CENTER);
        header.setFont(Theme.TITLE_FONT.deriveFont(28f));
        header.setForeground(currentPalette.textLight);
        header.setPreferredSize(new Dimension(300, 45));
        header.setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, currentPalette.tileDark));
        add(header, BorderLayout.NORTH);
        
        // main content
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(currentPalette.tileMediumDark);
        
        // middle panel (main display area for bars)
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(currentPalette.tileDark);
        mainPanel.setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, currentPalette.tileDark));

        // ----- initialize themedprogressbarpanel instances -----
        
        weeklyPanel = new ThemedProgressBar("Weekly", 65);
        weeklyPanel.setMaxDollarValue(150.00);

        monthlyPanel = new ThemedProgressBar("Monthly", 110);
        monthlyPanel.setMaxDollarValue(600.00);

        yearlylPanel = new ThemedProgressBar("Yearly", 80);
        // this will be set to the total income over the specific period later, rn thats not implemented
        yearlylPanel.setMaxDollarValue(7500.00); 

        alltimePanel = new ThemedProgressBar("All Time", 45);
        alltimePanel.setMaxDollarValue(10000.00);


        // add panels to the main layout
        // mainpanel.add(box.createverticalstrut(5));
        mainPanel.add(weeklyPanel);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(monthlyPanel);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(yearlylPanel);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(alltimePanel);

        mainPanel.add(Box.createVerticalGlue());
        
        JPanel buttonColumn = createRightPanel(currentPalette);
        
        // mainContent.add(buttonColumn, BorderLayout.EAST);
        mainContent.add(mainPanel, BorderLayout.CENTER);
        
        add(mainContent, BorderLayout.CENTER);
        
        // call refresh() immediately to update colors and values
        refresh();
        this.revalidate();
        this.repaint();
    }
    
    // ----- button column builder ----- //
    private JPanel createRightPanel(Theme.Palette currentPalette) 
    {
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(currentPalette.tileMediumDark);
        rightPanel.setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, currentPalette.tileDark));
        rightPanel.setPreferredSize(new Dimension(120, 0));
        
        // summary header box
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(currentPalette.accent);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        JLabel filterLabel = new JLabel("summary", SwingConstants.CENTER);
        filterLabel.setForeground(currentPalette.textLight);
        filterLabel.setFont(Theme.TITLE_FONT.deriveFont(18f));
        header.add(filterLabel, BorderLayout.CENTER);
        
        rightPanel.add(header);
        rightPanel.add(Box.createVerticalStrut(10));
        
        return rightPanel;
    }
    
    // ----- main refresh logic ----- //
    public void refresh() 
    {
        weeklyPanel.updateValue(95.4);
        monthlyPanel.updateValue(90.8);
        yearlylPanel.updateValue(80.0);
        alltimePanel.updateValue(45.0);

    }
}