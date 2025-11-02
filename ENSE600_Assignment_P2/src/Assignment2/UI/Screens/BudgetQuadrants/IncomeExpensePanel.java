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
import Assignment2.UI.Theme;
import Assignment2.Inventory.InventoryManager;
import Assignment2.Inventory.Transaction;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.UUID;

public class IncomeExpensePanel extends JPanel
{
    // ----- ui constant values ----- //
    private static final int MIN_FIELD_WIDTH = 100;
    private static final int MIN_FIELD_HEIGHT = 32;
    private static final int MIN_BUTTON_WIDTH = 55;
    private static final int LABEL_ROW_GAP = 6;
    private static final int SECTION_GAP = 12;

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
        Theme.Palette p = Theme.palette();
        
        // --- layout --- //
        setLayout(new BorderLayout(6, 6));
        setBackground(p.tileDark);
        setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        
        // ----- header ----- //
        JPanel headerContainer = createDualHeaderBar(p, "current income", "current expenses");
        add(headerContainer, BorderLayout.NORTH);
        
        // ----- income & expense sections ----- //
        JPanel contentContainer = new JPanel(new GridLayout(1, 2, 8, 0));
        contentContainer.setOpaque(false);
        contentContainer.add(createIncomeSection(p));
        contentContainer.add(createExpenseSection(p));
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(contentContainer, BorderLayout.NORTH);
        centerPanel.add(createSummaryTable(p), BorderLayout.CENTER);
        
        add(centerPanel, BorderLayout.CENTER);
        
        revalidate();
        repaint();
    }

    // ----- header bar ----- //
    private JPanel createDualHeaderBar(Theme.Palette p, String title1, String title2)
    {
        JPanel container = new JPanel(new GridLayout(1, 2, 8, 0));
        container.setBackground(p.tileDark);
        container.setPreferredSize(new Dimension(0, 45));
        container.add(createHeaderLabel(title1, p));
        container.add(createHeaderLabel(title2, p));
        return container;
    }
    
    // head helper
    private JLabel createHeaderLabel(String title, Theme.Palette p)
    {
        JLabel lbl = new JLabel(title, SwingConstants.CENTER);
        lbl.setFont(Theme.TITLE_FONT.deriveFont(26f));
        lbl.setForeground(p.textLight);
        lbl.setBackground(p.tileMediumDark);
        lbl.setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, p.tileDark));
        lbl.setOpaque(true);
        return lbl;
    }
    
    // ----- layout helpers ----- //
    private JPanel newTopPinnedColumn(Color bg, Insets padding)
    {
        JPanel col = new JPanel();
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        col.setOpaque(true);
        col.setBackground(bg);
        col.setBorder(BorderFactory.createEmptyBorder(padding.top, padding.left, padding.bottom, padding.right));
        return col;
    }
    
    private <T extends JComponent> T leftAlign(T c)
    {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        return c;
    }
    
    private JPanel createRow(Component... components)
    {
        JPanel row = new JPanel(new GridBagLayout());
        row.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 3, 2, 3);
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        for (int i = 0; i < components.length; i++)
        {
            gbc.gridx = i;
            gbc.weightx = (components[i] instanceof JButton) ? 0 : 1.0;
            row.add(components[i], gbc);
        }
        
        Dimension pref = row.getPreferredSize();
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, pref.height));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        return row;
    }
    
    private void setFieldSizing(JComponent comp)
    {
        comp.setMinimumSize(new Dimension(MIN_FIELD_WIDTH, MIN_FIELD_HEIGHT));
        comp.setPreferredSize(new Dimension(140, MIN_FIELD_HEIGHT));
        comp.setMaximumSize(new Dimension(280, MIN_FIELD_HEIGHT));
    }
    
    // ----- income ----- //
    private JPanel createIncomeSection(Theme.Palette p)
    {
        JPanel panel = newTopPinnedColumn(p.tileDark, new Insets(6, 10, 6, 4));
        
        // ----- salary ----- //
        JLabel salaryLabel = leftAlign(createSectionLabel("Salary", p));
        JTextField salaryField = new JTextField();
        setFieldSizing(salaryField);
        JPanel salaryFieldContainer = createAmountFieldContainer(salaryField, p);
        
        JComboBox<String> frequencyBox = new JComboBox<>(new String[]{"Weekly", "Fortnightly", "Monthly", "Yearly"});
        styleDropdown(frequencyBox, p);
        
        JButton saveSalaryButton = createAccentButton("Save", p);
        saveSalaryButton.addActionListener(e -> 
        {
            try {
                double amount = Double.parseDouble(salaryField.getText());
                String frequency = (String) frequencyBox.getSelectedItem();
                
                Transaction t = new Transaction(
                        UUID.randomUUID(),
                        "income",
                        "Salary",
                        "income",
                        amount,
                        frequency,
                        java.time.LocalDate.now().toString()
                );
                
                budget.addTransaction(t);
                populateTableData();
            } catch (NumberFormatException ex) 
            {
                JOptionPane.showMessageDialog(this, "Please enter a valid income amount.");
            }
        });
        
        panel.add(salaryLabel);
        panel.add(Box.createVerticalStrut(LABEL_ROW_GAP));
        panel.add(leftAlign(createRow(salaryFieldContainer, frequencyBox, saveSalaryButton)));
        
        // ----- one time payment ----- //
        JLabel oneTimeLabel = leftAlign(createSectionLabel("One-Time Payment", p));
        JTextField oneTimeAmountField = new JTextField();
        setFieldSizing(oneTimeAmountField);
        JPanel oneTimeFieldContainer = createAmountFieldContainer(oneTimeAmountField, p);
        JTextField oneTimeDescField = createPlaceholderTextField("Title", p);
        setFieldSizing(oneTimeDescField);
        
        JButton addButton = createAccentButton("Add", p);
        addButton.addActionListener(e -> 
        {
            try {
                double amount = Double.parseDouble(oneTimeAmountField.getText());
                String title = oneTimeDescField.getText();

                Transaction t = new Transaction(
                        UUID.randomUUID(),
                        "income",
                        title,
                        "income",
                        amount,
                        "one-time",
                        java.time.LocalDate.now().toString()
                );
                budget.addTransaction(t);
                populateTableData();
            } 
            catch (NumberFormatException ignored) {}
        });
        
        panel.add(Box.createVerticalStrut(SECTION_GAP));
        panel.add(oneTimeLabel);
        panel.add(Box.createVerticalStrut(LABEL_ROW_GAP));
        panel.add(leftAlign(createRow(oneTimeFieldContainer, oneTimeDescField, addButton)));
        
        // ------ monthly budget ------ //
        JLabel budgetLabel = leftAlign(createSectionLabel("Set This Month's Budget", p));
        JTextField budgetField = new JTextField();
        setFieldSizing(budgetField);
        JPanel budgetFieldContainer = createAmountFieldContainer(budgetField, p);
        JButton saveBudgetButton = createAccentButton("Save", p);
        
        panel.add(Box.createVerticalStrut(SECTION_GAP));
        panel.add(budgetLabel);
        panel.add(Box.createVerticalStrut(LABEL_ROW_GAP));
        panel.add(leftAlign(createRow(budgetFieldContainer, saveBudgetButton)));
        
        panel.add(Box.createVerticalGlue());
        return panel;
    }
    
    // ----- expenses ----- //
    private JPanel createExpenseSection(Theme.Palette p)
    {
        JPanel panel = newTopPinnedColumn(p.tileDark, new Insets(6, 4, 6, 10));
        
        // ----- add new bill ----- //
        JLabel addBillLabel = leftAlign(createSectionLabel("Add a New Bill", p));
        JTextField amountField = new JTextField();
        setFieldSizing(amountField);
        JPanel amountContainer = createAmountFieldContainer(amountField, p);
        JTextField billTitleField = createPlaceholderTextField("Title", p);
        setFieldSizing(billTitleField);
        
        JComboBox<String> tagBox = new JComboBox<>(new String[]
        {
            "Utilities", "Transport", "Shopping", "Housing", "Entertainment", "Take Out", "Other"
        });
        styleDropdown(tagBox, p);
        
        JComboBox<String> frequencyBox = new JComboBox<>(new String[]
        {
                "Weekly", "Fortnightly", "Monthly", "Yearly", "One-Time"
        });
        styleDropdown(frequencyBox, p);
        
        JButton confirmBillButton = createAccentButton("Confirm", p);
        confirmBillButton.addActionListener(e -> {
            try 
            {
                String title = billTitleField.getText();
                String tag = (String) tagBox.getSelectedItem();
                String frequency = (String) frequencyBox.getSelectedItem();
                double amount = Double.parseDouble(amountField.getText());
                
                Transaction t = new Transaction(
                        UUID.randomUUID(),
                        "expense",
                        title,
                        tag,
                        amount,
                        frequency,
                        java.time.LocalDate.now().toString()
                );
                
                budget.addTransaction(t);
                populateTableData();
            } catch (NumberFormatException ex) 
            {
                JOptionPane.showMessageDialog(this, "Please enter a valid expense amount.");
            }
        });
        
        panel.add(addBillLabel);
        panel.add(Box.createVerticalStrut(LABEL_ROW_GAP));
        panel.add(leftAlign(createRow(amountContainer, billTitleField, tagBox, frequencyBox, confirmBillButton)));
        
        // ----- edit existing bill ------ //
        JLabel editBillLabel = leftAlign(createSectionLabel("Edit Existing Bills", p));
        JComboBox<String> existingBillBox = new JComboBox<>(new String[]
        {
                "Internet", "Electricity", "Gym", "Fuel", "Netflix"
        });
        styleDropdown(existingBillBox, p);
        
        JTextField editAmountField = new JTextField();
        setFieldSizing(editAmountField);
        JPanel editAmountContainer = createAmountFieldContainer(editAmountField, p);
        
        JComboBox<String> frequencyBox2 = new JComboBox<>(new String[]
        {
                "Weekly", "Fortnightly", "Monthly", "Yearly"
        });
        styleDropdown(frequencyBox2, p);
        
        JButton confirmEditButton = createAccentButton("Confirm", p);
        JButton deleteBillButton = createAccentButton("Delete", p);
        
        confirmEditButton.addActionListener(e -> 
        {
            try 
            {
                double amount = Double.parseDouble(editAmountField.getText());
                String title = (String) existingBillBox.getSelectedItem();
                String frequency = (String) frequencyBox2.getSelectedItem();
                budget.addTransaction(new Transaction(UUID.randomUUID(), "expense", title, "edited", amount, frequency, java.time.LocalDate.now().toString()));
                populateTableData();
            }
            catch (NumberFormatException ex) 
            {
                JOptionPane.showMessageDialog(this, "Enter a valid amount to update the bill.");
            }
        });
        
        deleteBillButton.addActionListener(e -> 
        {
            String title = (String) existingBillBox.getSelectedItem();
            budget.getTransactions().values().removeIf(t -> t.getTitle().equalsIgnoreCase(title));
            populateTableData();
        });
        
        panel.add(Box.createVerticalStrut(SECTION_GAP));
        panel.add(editBillLabel);
        panel.add(Box.createVerticalStrut(LABEL_ROW_GAP));
        panel.add(leftAlign(createRow(editAmountContainer, existingBillBox, frequencyBox2, confirmEditButton, deleteBillButton)));
        
        // ----- one time expense ----- //
        JLabel oneTimeLabel = leftAlign(createSectionLabel("One-Time Expense", p));
        JTextField oneTimeTitleField = createPlaceholderTextField("Title", p);
        setFieldSizing(oneTimeTitleField);
        JTextField oneTimeamountField = new JTextField();
        setFieldSizing(oneTimeamountField);
        JPanel oneTimeAmountContainer = createAmountFieldContainer(oneTimeamountField, p);
        JButton addExpenseButton = createAccentButton("Add", p);
        
        addExpenseButton.addActionListener(e -> 
        {
            try 
            {
                double amount = Double.parseDouble(oneTimeamountField.getText());
                String title = oneTimeTitleField.getText();
                
                Transaction t = new Transaction(
                        UUID.randomUUID(),
                        "expense",
                        title,
                        "misc",
                        amount,
                        "one-time",
                        java.time.LocalDate.now().toString()
                );
                budget.addTransaction(t);
                populateTableData();
            }
            catch (NumberFormatException ignored) {}
        });
        
        panel.add(Box.createVerticalStrut(SECTION_GAP));
        panel.add(oneTimeLabel);
        panel.add(Box.createVerticalStrut(LABEL_ROW_GAP));
        panel.add(leftAlign(createRow(oneTimeAmountContainer, oneTimeTitleField, addExpenseButton)));
        
        panel.add(Box.createVerticalGlue());
        return panel;
    }
    
    // ----- helpers ----- //
    private JLabel createSectionLabel(String text, Theme.Palette p)
    {
        JLabel label = new JLabel(text, SwingConstants.LEFT);
        label.setFont(Theme.TITLE_FONT.deriveFont(17f));
        label.setForeground(p.textLight);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
    
    private void styleDropdown(JComboBox<String> box, Theme.Palette p)
    {
        box.setBackground(p.tileMediumDark);
        box.setForeground(p.textLight);
        box.setFont(Theme.BODY_FONT.deriveFont(13f));
        box.setFocusable(false);
        setFieldSizing(box);
    }
    
    private JButton createAccentButton(String text, Theme.Palette p)
    {
        JButton button = new JButton(text);
        button.setFont(Theme.TITLE_FONT.deriveFont(13f));
        button.setBackground(p.accent);
        button.setForeground(p.textLight);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(p.tileDark, 1, true));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setMinimumSize(new Dimension(MIN_BUTTON_WIDTH, MIN_FIELD_HEIGHT));
        button.setPreferredSize(new Dimension(70, MIN_FIELD_HEIGHT));
        button.setMaximumSize(new Dimension(110, MIN_FIELD_HEIGHT));
        return button;
    }
    
    private JPanel createAmountFieldContainer(JTextField textField, Theme.Palette p)
    {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(p.tileMediumDark);
        container.setBorder(BorderFactory.createLineBorder(p.tileMediumDark, 1, true));
        container.setMinimumSize(new Dimension(MIN_FIELD_WIDTH, MIN_FIELD_HEIGHT));
        
        JLabel dollarLabel = new JLabel("$", SwingConstants.LEFT);
        dollarLabel.setFont(Theme.TITLE_FONT.deriveFont(14f));
        dollarLabel.setForeground(p.textLight);
        dollarLabel.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 2));
        
        textField.setBackground(p.tileDark);
        textField.setForeground(p.textLight);
        textField.setFont(Theme.BODY_FONT.deriveFont(14f));
        textField.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        
        container.add(dollarLabel, BorderLayout.WEST);
        container.add(textField, BorderLayout.CENTER);
        return container;
    }
    
    private JTextField createPlaceholderTextField(String placeholder, Theme.Palette p)
    {
        JTextField textField = new JTextField(placeholder);
        textField.setBackground(p.tileMediumDark);
        textField.setForeground(Color.GRAY);
        textField.setFont(Theme.BODY_FONT.deriveFont(14f));
        textField.setBorder(BorderFactory.createLineBorder(p.tileMediumDark, 1, true));
        textField.setHorizontalAlignment(SwingConstants.LEFT);
        
        textField.addFocusListener(new java.awt.event.FocusAdapter()
        {
            @Override
            public void focusGained(java.awt.event.FocusEvent e)
            {
                if (textField.getText().equals(placeholder))
                {
                    textField.setText("");
                    textField.setForeground(p.textLight);
                }
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent e)
            {
                if (textField.getText().isEmpty())
                {
                    textField.setText(placeholder);
                    textField.setForeground(Color.GRAY);
                }
            }
        });
        return textField;
    }
    
    // ----- summary table builder ----- //
    private JPanel createSummaryTable(Theme.Palette p)
    {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(p.tileMediumDark);
        panel.setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, p.tileDark));
        
        JLabel title = new JLabel("transaction history", SwingConstants.LEFT);
        title.setFont(Theme.TITLE_FONT.deriveFont(18f));
        title.setForeground(p.textLight);
        title.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 2));
        panel.add(title, BorderLayout.NORTH);
        
        String[] columns = {"transaction", "tag", "date", "amount", "occurrence"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        table.setFont(Theme.BODY_FONT.deriveFont(13f));
        table.setForeground(p.textLight);
        table.setBackground(p.tileDark);
        table.setGridColor(p.tileMediumDark);
        table.setRowHeight(22);
        table.setFillsViewportHeight(true);
        
        JTableHeader header = table.getTableHeader();
        header.setBackground(p.accent);
        header.setForeground(p.textLight);
        header.setFont(Theme.TITLE_FONT.deriveFont(15f));
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(p.tileDark);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        populateTableData();
        return panel;
    }
    
    // ----- table data population ----- //
    private void populateTableData()
    {
        tableModel.setRowCount(0);
        for (Transaction t : budget.getTransactions().values())
        {
            Object[] row = {
                    t.getTitle(),
                    t.getTag(),
                    t.getDate(),
                    String.format("$%.2f", t.getAmount()),
                    t.getFrequency()
            };
            tableModel.addRow(row);
        }
    }
    
    // ----- public refresh method ----- //
    public void refresh()
    {
        populateTableData();
    }
}
