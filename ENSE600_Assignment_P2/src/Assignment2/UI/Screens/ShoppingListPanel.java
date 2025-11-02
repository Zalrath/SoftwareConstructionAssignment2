/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Assignment2.UI.Screens;

/**
 *
 * @author megan
 */

import Assignment2.UI.Template.BaseScreenPanel;
import Assignment2.UI.Theme;
import Assignment2.Inventory.InventoryManager;
import Assignment2.Inventory.Item;
import Assignment2.UI.Template.ToggleableButton;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

public class ShoppingListPanel extends BaseScreenPanel
{
    private final InventoryManager manager;
    private final Theme.Palette palette = Theme.palette();
    
    private JTable itemTable;
    private DefaultTableModel tableModel;
    private JTextArea shoppingListArea;
    
    // ----- constructor ----- // 
    public ShoppingListPanel(InventoryManager manager)
    {
        super("Shopping List", /*showBack*/ true, /*showAdd*/ false, /*addLabel*/ "Add Item", /*backTarget*/ "dashboard");
        this.manager = manager;
        buildBaseUI();
    }
    
    // ----- initialise content ----- // 
    @Override
    protected JComponent createCentre()
    {
        Theme.Palette palette = Theme.palette();
        
        // main container
        JPanel container = new JPanel(new BorderLayout(12, 12));
        container.setOpaque(false);
        
        //----- left panel ----- // 
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(320, 0)); // fixed width
        leftPanel.setBackground(palette.tileDark);
        leftPanel.setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, palette.tileDark));

        // --- accent header bar --- // 
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(palette.accent);
        titleBar.setPreferredSize(new Dimension(0, 48));
        titleBar.setBorder(BorderFactory.createMatteBorder(4, 4, 0, 4, palette.tileDark));
        
        JLabel leftTitle = new JLabel("you’ve purchased before:", SwingConstants.CENTER);
        leftTitle.setFont(Theme.TITLE_FONT.deriveFont(22f));
        leftTitle.setForeground(palette.textLight);
        leftTitle.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        titleBar.add(leftTitle, BorderLayout.CENTER);
        leftPanel.add(titleBar, BorderLayout.NORTH);
        
        // --- item list table --- // 
        JScrollPane itemScroll = createPurchasedItemTable();
        leftPanel.add(itemScroll, BorderLayout.CENTER);
        container.add(leftPanel, BorderLayout.WEST);
        
        // ----- right panel ----- // 
        JPanel rightPanel = new JPanel(new BorderLayout(0, 8));
        rightPanel.setBackground(palette.tileDark);
        rightPanel.setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, palette.tileDark));
        
        // --- middle section --- // 
        JPanel middleSection = createBulletListPanel();
        rightPanel.add(middleSection, BorderLayout.CENTER);
        
        // --- bottom section --- // 
        JPanel bottomSection = new JPanel(new BorderLayout());
        bottomSection.setBackground(palette.tileDark);
        bottomSection.setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, palette.tileDark));
        
        // --- buttons panel --- // 
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 8));
        buttonPanel.setOpaque(false);
        
        // buttons
        ToggleableButton exportTxt = new ToggleableButton("Export as TXT");

        // non toggleable
        exportTxt.addActionListener(e -> 
        {
            exportToTxt();
            exportTxt.setSelected(false);
            exportTxt.repaint();
        });

        // size
        exportTxt.setFont(Theme.TITLE_FONT.deriveFont(16f));
        
        buttonPanel.add(exportTxt);
        
        bottomSection.add(buttonPanel, BorderLayout.CENTER);
        bottomSection.setPreferredSize(new Dimension(0, 80));
        
        rightPanel.add(bottomSection, BorderLayout.SOUTH);
        container.add(rightPanel, BorderLayout.CENTER);
        
        return container;
    }
    
    // ----- bullet list ----- // 
    private JPanel createBulletListPanel()
    {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(palette.tileMediumDark);
        panel.setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, palette.tileDark));
        
        // --- accent header bar --- // 
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(palette.accent);
        titleBar.setPreferredSize(new Dimension(0, 48));
        
        JLabel title = new JLabel("create your shopping list", SwingConstants.CENTER);
        title.setFont(Theme.TITLE_FONT.deriveFont(22f));
        title.setForeground(palette.textLight);
        title.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        titleBar.add(title, BorderLayout.CENTER);
        panel.add(titleBar, BorderLayout.NORTH);
        
        // --- writable bullet area --- // 
        shoppingListArea = new JTextArea();
        shoppingListArea.setBackground(palette.tileDark);
        shoppingListArea.setForeground(palette.textLight);
        shoppingListArea.setFont(Theme.BODY_FONT.deriveFont(15f));
        shoppingListArea.setLineWrap(true);
        shoppingListArea.setWrapStyleWord(true);
        shoppingListArea.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        // --- auto bullet points --- //
        shoppingListArea.addKeyListener(new java.awt.event.KeyAdapter()
        {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e)
            {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER)
                {
                    SwingUtilities.invokeLater(() ->
                    {
                        shoppingListArea.insert("• ", shoppingListArea.getCaretPosition());
                    });
                }
            }
        });
        
        shoppingListArea.setText("• "); // new line with bullets
        
        JScrollPane scrollPane = new JScrollPane(shoppingListArea);
        scrollPane.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, palette.tileMediumDark));
        scrollPane.getViewport().setBackground(palette.tileDark);
        customizeScrollPane(scrollPane);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
    
    // ----- left panel ----- // 
    private JScrollPane createPurchasedItemTable()
    {
        String[] columns = {"item", "last purchased", "price"};
        tableModel = new DefaultTableModel(columns, 0)
        {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        itemTable = new JTable(tableModel);
        itemTable.setRowHeight(26);
        itemTable.setFont(Theme.BODY_FONT.deriveFont(14f));
        itemTable.setForeground(palette.textLight);
        itemTable.setBackground(palette.tileDark);
        itemTable.setGridColor(palette.tileMediumDark);
        itemTable.setShowVerticalLines(false);
        
        JTableHeader header = itemTable.getTableHeader();
        header.setBackground(palette.accent);
        header.setForeground(palette.textLight);
        header.setFont(Theme.TITLE_FONT.deriveFont(18f));
        
        JScrollPane scrollPane = new JScrollPane(itemTable);
        scrollPane.setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, palette.tileDark));
        scrollPane.getViewport().setBackground(palette.tileDark);
        
        customizeScrollPane(scrollPane);
        populatePurchasedItems();
        
        return scrollPane;
    }
    
    // ----- populate the table ----- // 
    private void populatePurchasedItems()
    {
        tableModel.setRowCount(0);
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        Collection<Item> allItems = manager.getAllItems();
        for (Item item : allItems)
        {
            if (item.getLastPurchased() != null)
            {
                String date = item.getLastPurchased().format(df);
                double price = manager.getLatestPrice(item.getUuid());
                tableModel.addRow(new Object[]{
                    item.getName(),
                    date,
                    String.format("$%.2f", price)
                });
            }
        }
    }
    
    // ----- scrollbar styling ----- // 
    private void customizeScrollPane(JScrollPane scrollPane)
    {
        customizeScrollBar(scrollPane.getVerticalScrollBar(), true);
        customizeScrollBar(scrollPane.getHorizontalScrollBar(), false);
        
        JPanel corner1 = new JPanel();
        corner1.setBackground(palette.tileDark);
        scrollPane.setCorner(JScrollPane.LOWER_RIGHT_CORNER, corner1);
        
        JPanel corner2 = new JPanel();
        corner2.setBackground(palette.tileDark);
        scrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, corner2);
    }
    
    private void customizeScrollBar(JScrollBar scrollBar, boolean vertical)
    {
        scrollBar.setUI(new BasicScrollBarUI()
        {
            @Override
            protected void configureScrollBarColors()
            {
                this.thumbColor = palette.accent;
                this.trackColor = palette.tileMediumDark;
            }
            
            @Override
            protected JButton createDecreaseButton(int orientation) { return createStopperButton(vertical); }
            @Override
            protected JButton createIncreaseButton(int orientation) { return createStopperButton(vertical); }
            
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
    public void refresh() { populatePurchasedItems(); }
    
    //----- export handlers ----- // 
    private void exportToTxt() 
    {
        String content = shoppingListArea.getText();
        Assignment2.Utils.SimpleTextWriter.saveTextWithDialog(this, content);
    }
}
