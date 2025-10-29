package Assignment2.UI.Screens;

import Assignment2.UI.Template.BaseScreenPanel;
import Assignment2.UI.Theme;
import Assignment2.Inventory.InventoryManager;
import Assignment2.Inventory.Item;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

public class InventoryPanel extends BaseScreenPanel 
{
    private final InventoryManager manager;
    private final List<Item> currentItems;
    
    private DefaultTableModel tableModel;
    private JTable table;
    
    public InventoryPanel(InventoryManager manager, List<Item> currentItems) 
    {
        super("Inventory", true, true, "Add Item", "dashboard");
        this.manager = manager;
        this.currentItems = currentItems;
        buildBaseUI();
    }
    
    // ----- Content ----- //
    @Override
    protected JComponent createCentre() 
    {
        // ----- Table Setup ----- //
        String[] columns = {"Item", "Tags", "Last Purchased", "Price", "Favourite", "Quantity"};
        tableModel = new DefaultTableModel(columns, 0) 
        {
            @Override
            public boolean isCellEditable(int row, int column) 
            {
                return false;
            }
        };
        
        // Populate
        populateTable();
        
        // Theme table
        table = new JTable(tableModel);
        table.setAutoCreateRowSorter(true);
        
        Theme.Palette p = Theme.palette();
        table.setFont(Theme.BODY_FONT);
        table.setForeground(p.textLight);
        table.setBackground(p.tileMediumDark);
        table.setGridColor(p.tileMedium);
        table.setRowHeight(28);
        table.getTableHeader().setBackground(p.accent);
        table.getTableHeader().setForeground(p.textLight);
        table.getTableHeader().setFont(Theme.TITLE_FONT.deriveFont(24f));
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(p.tileDark);
        
        // ----- LEFT PANEL ----- //
        JPanel leftPanel = new JPanel();
        leftPanel.setPreferredSize(new Dimension(220, 0)); // width, height = flexible
        leftPanel.setBackground(p.tileDark);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        
        JLabel leftTitle = new JLabel("Filters");
        leftTitle.setFont(Theme.TITLE_FONT.deriveFont(22f));
        leftTitle.setForeground(p.textLight);
        leftPanel.add(Box.createVerticalStrut(16));
        leftPanel.add(leftTitle);
        leftPanel.add(Box.createVerticalStrut(8));
        
        // Example: add a filter dropdown
        JComboBox<String> tagFilter = new JComboBox<>(new String[]{"All", "Breakfast", "Pantry", "Produce"});
        leftPanel.add(tagFilter);
        leftPanel.add(Box.createVerticalGlue());
        
  
        
        // ----- Assemble Main Layout ----- //
        JPanel card = new JPanel(new BorderLayout(12, 12));
        card.setOpaque(false);
        card.add(leftPanel, BorderLayout.WEST);
        card.add(scrollPane, BorderLayout.CENTER);
        
        
        return card;
    }

    private void populateTable()
    {
        tableModel.setRowCount(0);
        
        // will make this configurable
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd MMM yyyy");

        for (Item item : manager.getAllItems()) 
        {
            UUID id = item.getUuid();
            String tags = String.join(", ", item.getTags());
            String date = item.getLastPurchased() != null ? item.getLastPurchased().format(df) : "—";
            
            Object[] row = {
                item.getName(),
                tags,
                date,
                String.format("$%.2f", manager.getLatestPrice(id)),
                item.getFavorite() ? "Y" : "",
                item.getCurrentAmount()
            };
            tableModel.addRow(row);
        }
    }
}