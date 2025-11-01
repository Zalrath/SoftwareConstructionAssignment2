/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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
import java.time.LocalDate;

public class BudgetVsActualPanel extends JPanel 
{
    private final InventoryManager manager;
    
    private ThemedProgressBar weeklyPanel;
    private ThemedProgressBar monthlyPanel;
    private ThemedProgressBar yearlylPanel;
    private ThemedProgressBar alltimePanel;
    
    private JTextField weeklyBudgetField;
    private JTextField monthlyBudgetField;
    private JTextField yearlyBudgetField;
    private JTextField allTimeBudgetField;
    
    
    
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
        JLabel header = new JLabel("budget vs inventory spending", SwingConstants.CENTER);
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
        
        // take input of the last years worth of weekly shopping budget and calculate
        // 1. weekly -> the this weeks set budget
        // 2. monthly -> months worth (1-31st)
        // 3. yearly -> add all the months
        // 4. all time -> add all budgets 
        
        
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
        
        mainContent.add(buttonColumn, BorderLayout.EAST);
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
        
        
        weeklyBudgetField = createLabeledField(rightPanel, "Weekly:", "300");
        monthlyBudgetField = createLabeledField(rightPanel, "Monthly:", "1200");
        yearlyBudgetField = createLabeledField(rightPanel, "Yearly:", "14400");
        allTimeBudgetField = createLabeledField(rightPanel, "All Time:", "100000");

         
        
        allTimeBudgetField.setBackground(currentPalette.accent);
        allTimeBudgetField.setForeground(Color.WHITE);

        yearlyBudgetField.setBackground(currentPalette.accent);
        yearlyBudgetField.setForeground(Color.WHITE);
        
        monthlyBudgetField.setBackground(currentPalette.accent);
        monthlyBudgetField.setForeground(Color.WHITE);
        
        weeklyBudgetField.setBackground(currentPalette.accent);
        weeklyBudgetField.setForeground(Color.WHITE);
                
        // apply button
        JButton applyButton = new JButton("Apply");
        applyButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        applyButton.addActionListener(e -> applyBudgets());
      
        applyButton.setPreferredSize(new Dimension(Integer.MAX_VALUE, 28));
        applyButton.setFocusPainted(false);
        applyButton.setContentAreaFilled(true);
        applyButton.setOpaque(true);       
        applyButton.setBackground(currentPalette.accent);
        applyButton.setForeground(Color.WHITE);
        applyButton.setFont(Theme.TITLE_FONT.deriveFont(18f));
        
        
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(applyButton);
        
        
        
        
        return rightPanel;
    }
    
    private JTextField createLabeledField(JPanel parent, String label, String defaultValue) {
        JLabel jLabel = new JLabel(label);
        jLabel.setForeground(Color.WHITE);
        jLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        parent.add(jLabel);

        JTextField field = new JTextField(defaultValue);
        field.setMaximumSize(new Dimension(140, 25));
        field.setAlignmentX(Component.CENTER_ALIGNMENT);
        parent.add(field);
        parent.add(Box.createVerticalStrut(5));
        return field;
    }
    
    // ----- main refresh logic ----- //
    public void refresh() 
    {


        // Spending
        double weeklySpending = manager.getTotalSpendingForPeriod("week");
        double monthlySpending = manager.getTotalSpendingForPeriod("month");
        double yearlySpending = manager.getTotalSpendingForPeriod("year");
        double allTimeSpending = manager.getTotalSpendingForPeriod("all");


        // Budgets  
        double weeklyBudget = Double.parseDouble(weeklyBudgetField.getText());
        double monthlyBudget = Double.parseDouble(monthlyBudgetField.getText());
        double yearlyBudget = Double.parseDouble(yearlyBudgetField.getText());
        double allTimeBudget = Double.parseDouble(allTimeBudgetField.getText());

    

        
        
        // Update progress bars
        weeklyPanel.setMaxDollarValue(weeklyBudget);

        weeklyPanel.updateValue((weeklySpending / weeklyBudget) * 100);

        monthlyPanel.setMaxDollarValue(monthlyBudget);

        monthlyPanel.updateValue((monthlySpending / monthlyBudget) * 100);

        yearlylPanel.setMaxDollarValue(yearlyBudget);

        yearlylPanel.updateValue((yearlySpending / yearlyBudget) * 100);

        alltimePanel.setMaxDollarValue(allTimeBudget);

        alltimePanel.updateValue((allTimeSpending / allTimeBudget) * 100);

    }

    private void applyBudgets() {
        try {
            double weeklyBudget = Double.parseDouble(weeklyBudgetField.getText());
            double monthlyBudget = Double.parseDouble(monthlyBudgetField.getText());
            double yearlyBudget = Double.parseDouble(yearlyBudgetField.getText());
            double allTimeBudget = Double.parseDouble(allTimeBudgetField.getText());

            weeklyPanel.setMaxDollarValue(weeklyBudget);
            monthlyPanel.setMaxDollarValue(monthlyBudget);
            yearlylPanel.setMaxDollarValue(yearlyBudget);
            alltimePanel.setMaxDollarValue(allTimeBudget);
                
            
            
            refresh();
            
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for all budgets.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }



}