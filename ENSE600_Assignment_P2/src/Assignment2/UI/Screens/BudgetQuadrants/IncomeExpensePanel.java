/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Assignment2.UI.Screens.BudgetQuadrants;


/**
 *
 * @author megan
 */

import Assignment2.Database.BudgetManager;
import Assignment2.Database.DatabaseUtil;
import Assignment2.UI.Theme;
import Assignment2.Inventory.InventoryManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class IncomeExpensePanel extends JPanel
{
    // ----- ui constant values ----- //
    private static final int FIELD_WIDTH = 120;
    private static final int FIELD_HEIGHT = 40;
    private static final int BUTTON_WIDTH = 70; 
    
    // ----- fields ----- //
    private final InventoryManager manager;
    private final BudgetManager budget;
    private JTable table;
    private DefaultTableModel tableModel;
    
    // ----- constructor ----- //
    public IncomeExpensePanel(InventoryManager manager, Theme.Palette palette, BudgetManager budget)
    {
        this.manager = manager;
        this.budget = budget;
        buildUI();
    }
    
    // ----- theme refresh ----- //
    @Override
    public void updateUI()
    {
        super.updateUI();
        SwingUtilities.invokeLater(this::buildUI);
    }
    
    // ----- initialise ui ----- //
    private void buildUI()
    {
        removeAll();
        Theme.Palette currentPalette = Theme.palette();
        
        // --- layout --- //
        setLayout(new BorderLayout());
        setBackground(currentPalette.tileDark);
        setBorder(BorderFactory.createLineBorder(currentPalette.tileDark, 4));
        
        // ----- header ----- //
        JPanel headerContainer = createDualHeaderBar(currentPalette, "current income", "current expenses");
        add(headerContainer, BorderLayout.NORTH);
        
        // ----- income & expense sections ----- //
        JPanel contentContainer = new JPanel(new GridLayout(1, 2, 10, 0));
        contentContainer.setOpaque(false);
        
        JPanel income = createIncomeSection(currentPalette);
        JPanel expenses = createExpenseSection(currentPalette);
        
        contentContainer.add(income);
        contentContainer.add(expenses);
        add(contentContainer, BorderLayout.CENTER);
        
        // ----- transaction log ----- //
        JPanel tableContainer = createSummaryTable(currentPalette);
        add(tableContainer, BorderLayout.SOUTH);
        
        revalidate();
        repaint();
    }
    
    // ----- header bar ----- //
    private JPanel createDualHeaderBar(Theme.Palette currentPalette, String title1, String title2)
    {
        JPanel container = new JPanel(new GridLayout(1, 2));
        container.setBackground(currentPalette.tileDark);
        container.setPreferredSize(new Dimension(0, 45));
        
        JLabel header1 = createHeaderLabel(title1, currentPalette);
        container.add(header1);
        
        JLabel header2 = createHeaderLabel(title2, currentPalette);
        container.add(header2);
        
        return container;
    }

    // head helper
    private JLabel createHeaderLabel(String title, Theme.Palette palette) 
    {
        JLabel header = new JLabel(title, SwingConstants.CENTER);
        header.setFont(Theme.TITLE_FONT.deriveFont(28f));
        header.setForeground(palette.textLight);
        header.setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, palette.tileDark));
        header.setBackground(palette.tileMediumDark);
        header.setOpaque(true);
        return header;
    }
    
    // ----- income section with aligned fields and placeholder text ----- //
    private JPanel createIncomeSection(Theme.Palette palette)
    {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(palette.tileDark);
        
        // padding
        panel.setBorder(BorderFactory.createEmptyBorder(4, 20, 4, 4));
        
        // ----- salary ----- // 
        JLabel salaryLabel = createSectionLabel("Salary", palette, 4);
        
        // layout
        JPanel salaryRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 4));
        salaryRow.setOpaque(false);
        salaryRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JTextField salaryField = new JTextField();
        JPanel salaryFieldContainer = createAmountFieldContainer(salaryField, palette);
        
        JComboBox<String> frequencyBox = new JComboBox<>(new String[]{"Weekly", "Fortnightly", "Monthly", "Yearly"});
        frequencyBox.setBackground(palette.tileMediumDark);
        frequencyBox.setForeground(palette.textLight);
        frequencyBox.setFont(Theme.BODY_FONT.deriveFont(13f));
        frequencyBox.setFocusable(false);
        frequencyBox.setPreferredSize(new Dimension(FIELD_WIDTH, FIELD_HEIGHT));
        
        JButton saveSalaryButton = createAccentButton("Save", palette);
        saveSalaryButton.addActionListener(e -> {
        
        try {
                double amount = Double.parseDouble(salaryField.getText());
                String frequency = (String) frequencyBox.getSelectedItem();
                
    //            budget.saveTransaction("income", "Salary", "income", amount, frequency);
                
                populateTableData();
                refreshTotals();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid income amount.");
            }
        
        
        
        });
        
        salaryRow.add(salaryFieldContainer);
        salaryRow.add(Box.createHorizontalStrut(10)); // small gap
        salaryRow.add(frequencyBox);
        salaryRow.add(Box.createHorizontalStrut(10)); // small gap
        salaryRow.add(saveSalaryButton);
        
        // ----- one time payment ----- // 
        JLabel oneTimeLabel = createSectionLabel("One-Time Payment", palette, 8);
        
        // layout
        JPanel oneTimeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 4));
        oneTimeRow.setOpaque(false);
        oneTimeRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JTextField oneTimeAmountField = new JTextField();
        JPanel oneTimeFieldContainer = createAmountFieldContainer(oneTimeAmountField, palette);
        
        // placeholder
        JTextField oneTimeDescField = createPlaceholderTextField("Title", palette);
       
        
        JButton addButton = createAccentButton("Add", palette);
        
        oneTimeRow.add(oneTimeFieldContainer);
        oneTimeRow.add(Box.createHorizontalStrut(10)); // small gap
        oneTimeRow.add(oneTimeDescField);
        oneTimeRow.add(Box.createHorizontalStrut(10)); // small gap
        oneTimeRow.add(addButton);
        
        // ------ monthly budget ------ // 
        JLabel budgetLabel = createSectionLabel("Set This Month's Budget", palette, 8);
        
        // layout
        JPanel budgetRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 4));
        budgetRow.setOpaque(false);
        budgetRow.setAlignmentX(Component.LEFT_ALIGNMENT);
       
        JTextField budgetField = new JTextField();
        JPanel budgetFieldContainer = createAmountFieldContainer(budgetField, palette);
        
        JButton saveBudgetButton = createAccentButton("Save", palette);
        
        budgetRow.add(budgetFieldContainer);
        budgetRow.add(Box.createHorizontalStrut(10)); // small gap
        budgetRow.add(saveBudgetButton);
        
        // add to layout
        panel.add(salaryLabel);
        panel.add(salaryRow);
        panel.add(oneTimeLabel);
        panel.add(oneTimeRow);
        panel.add(budgetLabel);
        panel.add(budgetRow);
        
        return panel;
        
        
        
    }
    
    // ----- expenses ----- //
    private JPanel createExpenseSection(Theme.Palette palette) 
    {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(palette.tileDark);
        
        // padding
        panel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 20));
        
        // ----- add new bill ----- // 
        JLabel addBillLabel = createSectionLabel("Add a New Bill", palette, 4);
        
        // layout
        JPanel addBillRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 4));
        addBillRow.setOpaque(false);
        addBillRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // trans title
        JTextField billTitleField = createPlaceholderTextField("Transaction Title", palette);
        
        // tag dropdown
        JComboBox<String> tagBox = new JComboBox<>(new String[]
        {
            "Utilities", "Transport", "Shopping", "Housing", "Entertainment", "Take Out", "Other"
        });
        tagBox.setBackground(palette.tileMediumDark);
        tagBox.setForeground(palette.textLight);
        tagBox.setFont(Theme.BODY_FONT.deriveFont(13f));
        tagBox.setFocusable(false);
        tagBox.setPreferredSize(new Dimension(FIELD_WIDTH, FIELD_HEIGHT));
        
        // $ amount
        JTextField amountField = new JTextField();
        JPanel amountContainer = createAmountFieldContainer(amountField, palette);
        
        JButton confirmBillButton = createAccentButton("Confirm", palette);
        confirmBillButton.addActionListener(e -> {
            try {
            String title = billTitleField.getText();
            String tag = (String) tagBox.getSelectedItem();
            double amount = Double.parseDouble(amountField.getText());
            
 //           budget.saveTransaction("expense", title, tag, amount, "one-time");
            
            populateTableData();
            refreshTotals();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid expense amount.");
        }
        
        
        });
        
        
        
        
        
        addBillRow.add(billTitleField);
        addBillRow.add(Box.createHorizontalStrut(10)); // small gap
        addBillRow.add(tagBox);
        addBillRow.add(Box.createHorizontalStrut(10)); // small gap
        addBillRow.add(amountContainer);
        addBillRow.add(Box.createHorizontalStrut(10));// small gap
        addBillRow.add(confirmBillButton);
        
        // ----- edit existing bill ------ // 
        JLabel editBillLabel = createSectionLabel("Edit Existing Bills", palette, 10);
        
        // layout
        JPanel editBillRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 4));
        editBillRow.setOpaque(false);
        editBillRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JComboBox<String> existingBillBox = new JComboBox<>(new String[]
        {
            "Internet", "Electricity", "Gym", "Fuel", "Netflix"
        });
        existingBillBox.setBackground(palette.tileMediumDark);
        existingBillBox.setForeground(palette.textLight);
        existingBillBox.setFont(Theme.BODY_FONT.deriveFont(13f));
        existingBillBox.setPreferredSize(new Dimension(FIELD_WIDTH, FIELD_HEIGHT));
        
        JTextField editAmountField = new JTextField();
        JPanel editAmountContainer = createAmountFieldContainer(editAmountField, palette);
        
        JComboBox<String> frequencyBox = new JComboBox<>(new String[]
        {
            "Weekly", "Fortnightly", "Monthly", "Yearly"
        });
        frequencyBox.setBackground(palette.tileMediumDark);
        frequencyBox.setForeground(palette.textLight);
        frequencyBox.setFont(Theme.BODY_FONT.deriveFont(13f));
        frequencyBox.setFocusable(false);
        frequencyBox.setPreferredSize(new Dimension(FIELD_WIDTH, FIELD_HEIGHT));
        
        JButton confirmEditButton = createAccentButton("Confirm", palette);
        JButton deleteBillButton = createAccentButton("Delete", palette);
        
        editBillRow.add(existingBillBox);
        editBillRow.add(Box.createHorizontalStrut(10)); // small gap
        editBillRow.add(editAmountContainer);
        editBillRow.add(Box.createHorizontalStrut(10)); // small gap
        editBillRow.add(frequencyBox);
        editBillRow.add(Box.createHorizontalStrut(10)); // small gap
        editBillRow.add(confirmEditButton);
        editBillRow.add(Box.createHorizontalStrut(10)); // small gap
        editBillRow.add(deleteBillButton);
        
        // ----- one time expense ----- // 
        JLabel oneTimeLabel = createSectionLabel("One-Time Expense", palette, 10);
        
        // layout
        JPanel oneTimeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 4));
        oneTimeRow.setOpaque(false);
        oneTimeRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // fields
        JTextField oneTimeTitleField = createPlaceholderTextField("Title", palette);
        
        JPanel oneTimeAmountContainer = createAmountFieldContainer(new JTextField(), palette);
        
        JButton addExpenseButton = createAccentButton("Add", palette);
        
        oneTimeRow.add(oneTimeTitleField);
        oneTimeRow.add(Box.createHorizontalStrut(10)); // small gap
        oneTimeRow.add(oneTimeAmountContainer);
        oneTimeRow.add(Box.createHorizontalStrut(10)); // small gap
        oneTimeRow.add(addExpenseButton);
        
        // add panels
        panel.add(addBillLabel);
        panel.add(addBillRow);
        panel.add(editBillLabel);
        panel.add(editBillRow);
        panel.add(oneTimeLabel);
        panel.add(oneTimeRow);
        
        return panel;
    }
    
    // ----- helpers ----- //
    // labels
    private JLabel createSectionLabel(String text, Theme.Palette palette, int topBorder) 
    {
        JLabel label = new JLabel(text);
        label.setFont(Theme.TITLE_FONT.deriveFont(18f));
        label.setForeground(palette.textLight);
        label.setBorder(BorderFactory.createEmptyBorder(topBorder, 0, 0, 0));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
    
    // button
    private JButton createAccentButton(String text, Theme.Palette palette) 
    {
        JButton button = new JButton(text);
        button.setFont(Theme.TITLE_FONT.deriveFont(13f));
        button.setBackground(palette.accent);
        button.setForeground(palette.textLight);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(palette.tileDark, 2, true));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(BUTTON_WIDTH, FIELD_HEIGHT));
        return button;
    }
    
    // dollar buck field
    private JPanel createAmountFieldContainer(JTextField textField, Theme.Palette palette) 
    {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(palette.tileMediumDark);
        container.setBorder(BorderFactory.createLineBorder(palette.tileMediumDark, 2, true));
        container.setPreferredSize(new Dimension(FIELD_WIDTH, FIELD_HEIGHT));
        
        JLabel dollarLabel = new JLabel("$");
        dollarLabel.setFont(Theme.TITLE_FONT.deriveFont(15f));
        dollarLabel.setForeground(palette.textLight);
        dollarLabel.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 4));
        
        textField.setBackground(palette.tileDark);
        textField.setForeground(palette.textLight);
        textField.setFont(Theme.BODY_FONT.deriveFont(14f));
        textField.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        
        container.add(dollarLabel, BorderLayout.WEST);
        container.add(textField, BorderLayout.CENTER);
        return container;
    }
    
    // text block with greyed text
    private JTextField createPlaceholderTextField(String placeholder, Theme.Palette palette) 
    {
        JTextField textField = new JTextField(placeholder);
        textField.setBackground(palette.tileMediumDark);
        textField.setForeground(Color.GRAY);
        textField.setFont(Theme.BODY_FONT.deriveFont(14f));
        textField.setBorder(BorderFactory.createLineBorder(palette.tileMediumDark, 2, true));
        textField.setPreferredSize(new Dimension(FIELD_WIDTH, FIELD_HEIGHT));
        
        textField.addFocusListener(new java.awt.event.FocusAdapter() 
        {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) 
            {
                if (textField.getText().equals(placeholder)) 
                {
                    textField.setText("");
                    textField.setForeground(palette.textLight);
                }
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent e) 
            {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                    textField.setForeground(Color.GRAY);
                }
            }
        });
        return textField;
    }
    
    // ----- summary table builder ----- //
    private JPanel createSummaryTable(Theme.Palette palette)
    {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(palette.tileMediumDark);
        panel.setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, palette.tileDark));
        
        JLabel title = new JLabel("transaction history", SwingConstants.CENTER);
        title.setFont(Theme.TITLE_FONT.deriveFont(20f));
        title.setForeground(palette.textLight);
        title.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        panel.add(title, BorderLayout.NORTH);
        
        String[] columns = {"transaction", "tag", "date", "amount", "occurrence"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        
        table.setFont(Theme.BODY_FONT.deriveFont(14f));
        table.setForeground(palette.textLight);
        table.setBackground(palette.tileDark);
        table.setGridColor(palette.tileMediumDark);
        table.setRowHeight(22);
        table.setFillsViewportHeight(true);
        
        JTableHeader header = table.getTableHeader();
        header.setBackground(palette.accent);
        header.setForeground(palette.textLight);
        header.setFont(Theme.TITLE_FONT.deriveFont(16f));
        
        JScrollPane scrollPane = new JScrollPane(table);
        customizeScrollPane(scrollPane);
        
        scrollPane.setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, palette.tileDark));
        scrollPane.getViewport().setBackground(palette.tileDark);
        scrollPane.setPreferredSize(new Dimension(0, 140));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        populateTableData();
        return panel;
    }
    
    private void populateTableData()
    {
        /*
        tableModel.setRowCount(0);
        List<Object[]> transactions = budget.getTransactions();
        for (Object[] row : transactions) {
            tableModel.addRow(row);
        }
        */
        
        
        List<Object[]> transactions = new ArrayList<>();
        transactions.add(new Object[]{"milk purchase", "dairy", "2025-10-29", "$4.50", "weekly"});
        transactions.add(new Object[]{"internet bill", "utilities", "2025-10-20", "-$85.00", "monthly"});
        transactions.add(new Object[]{"groceries", "pantry", "2025-10-15", "$60.00", "weekly"});
        transactions.add(new Object[]{"fuel", "transport", "2025-10-10", "$50.00", "fortnightly"});
        transactions.add(new Object[]{"gym", "health", "2025-10-01", "$30.00", "monthly"});
        
        tableModel.setRowCount(0);
        for (Object[] row : transactions)
            tableModel.addRow(row);
         

    }
    
    private void customizeScrollPane(JScrollPane scrollPane)
    {
        customizeScrollBar(scrollPane.getVerticalScrollBar(), true);
        customizeScrollBar(scrollPane.getHorizontalScrollBar(), false);
        
        JPanel corner1 = new JPanel();
        corner1.setBackground(Theme.palette().tileDark);
        scrollPane.setCorner(JScrollPane.LOWER_RIGHT_CORNER, corner1);
        
        JPanel corner2 = new JPanel();
        corner2.setBackground(Theme.palette().tileDark);
        scrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, corner2);
    }
    
    private void customizeScrollBar(JScrollBar scrollBar, boolean vertical)
    {
        Theme.Palette palette = Theme.palette();
        scrollBar.setUI(new javax.swing.plaf.basic.BasicScrollBarUI()
        {
            @Override
            protected void configureScrollBarColors()
            {
                this.thumbColor = palette.accent;
                this.trackColor = palette.tileMediumDark;
            }
            
            @Override
            protected JButton createDecreaseButton(int orientation)
            {
                return createStopperButton(vertical);
            }
            
            @Override
            protected JButton createIncreaseButton(int orientation)
            {
                return createStopperButton(vertical);
            }
            
            private JButton createStopperButton(boolean vertical)
            {
                JButton b = new JButton();
                b.setBackground(palette.tileLight);
                b.setBorder(null);
                b.setFocusable(false);
                b.setRolloverEnabled(false);
                b.setPreferredSize(vertical ? new Dimension(0, 10) : new Dimension(10, 0));
                return b;
            }
        });
    }
    
    
    
    private void refreshTotals() {
        double income = budget.getTotalByType("income");
        double expense = budget.getTotalByType("expense");
        
    }
   
    
    

    
    
    
    
    
 
    
    
    public void refresh()
    {
        populateTableData();
    }
}