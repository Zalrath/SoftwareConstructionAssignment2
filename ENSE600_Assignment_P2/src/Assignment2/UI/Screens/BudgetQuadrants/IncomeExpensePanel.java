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
import javax.swing.table.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class IncomeExpensePanel extends JPanel
{
    // ----- instance fields ----- //
    private final InventoryManager manager;
    private JTable table;
    private DefaultTableModel tableModel;
    
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
        
        JPanel income = createSection(currentPalette, null);
        JPanel expenses = createSection(currentPalette, null);
        
        contentContainer.add(income);
        contentContainer.add(expenses);
        add(contentContainer, BorderLayout.CENTER);
        
        // ----- third panel: dynamic summary table ----- //
        JPanel tableContainer = createSummaryTable(currentPalette);
        add(tableContainer, BorderLayout.SOUTH);
        
        revalidate();
        repaint();
    }
    
    // ----- dual header bar builder ----- //
    private JPanel createDualHeaderBar(Theme.Palette currentPalette, String title1, String title2)
    {
        JPanel container = new JPanel(new GridLayout(1, 2));
        container.setBackground(currentPalette.tileDark);
        container.setPreferredSize(new Dimension(0, 45));
        
        JLabel header1 = new JLabel(title1, SwingConstants.CENTER);
        header1.setFont(Theme.TITLE_FONT.deriveFont(28f));
        header1.setForeground(currentPalette.textLight);
        header1.setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, currentPalette.tileDark));
        header1.setBackground(currentPalette.tileMediumDark);
        header1.setOpaque(true);
        container.add(header1);
        
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
        
        JTextArea area = new JTextArea("program here");
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
        
        // --- table model setup --- //
        String[] columns = {"transaction", "tag", "date", "amount", "occurrence"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        
        // --- table styling --- //
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
        
        // --- populate initial data --- //
        populateTableData();
        
        return panel;
    }
    
    // ----- dynamic data filler ----- //
    private void populateTableData()
    {
        // --- example transactions --- //
        List<Object[]> transactions = new ArrayList<>();
        transactions.add(new Object[]{"milk purchase", "dairy", "2025-10-29", "$4.50", "weekly"});
        transactions.add(new Object[]{"internet bill", "utilities", "2025-10-20", "-$85.00", "monthly"});
        transactions.add(new Object[]{"groceries", "pantry", "2025-10-15", "$60.00", "weekly"});
        transactions.add(new Object[]{"fuel", "transport", "2025-10-10", "$50.00", "fortnightly"});
        transactions.add(new Object[]{"gym", "health", "2025-10-01", "$30.00", "monthly"});
        transactions.add(new Object[]{"gym", "health", "2025-10-01", "$30.00", "monthly"});
        transactions.add(new Object[]{"gym", "health", "2025-10-01", "$10.00", "monthly"});
        transactions.add(new Object[]{"gym", "health", "2025-10-01", "$20.00", "monthly"});
        transactions.add(new Object[]{"gym", "health", "2025-10-01", "$40.00", "monthly"});
        
        tableModel.setRowCount(0); // clear old data
        for (Object[] row : transactions)
        {
            tableModel.addRow(row);
        }
    }
    
    // ----- custom scrollbars (reused from inventorypanel) ----- //
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
    
    // ----- external refresh ----- //
    public void refresh()
    {
        populateTableData();
    }
}
