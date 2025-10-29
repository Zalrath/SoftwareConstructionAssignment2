package Assignment2.UI.Screens;

import Assignment2.UI.Template.BaseScreenPanel;
import Assignment2.UI.Theme;
import Project_p2.InventoryManager;
import Project_p2.Item;

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
    
    // ----- Constructor ----- //
    public InventoryPanel(InventoryManager manager, List<Item> currentItems)
    {
        super("Inventory", true, true, "Add Item", "dashboard");
        this.manager = manager;
        this.currentItems = currentItems;
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
            public boolean isCellEditable(int row, int column) {
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
        
        // ----- RIGHT PANEL ----- //
        JPanel rightPanel = new JPanel();
        rightPanel.setPreferredSize(new Dimension(240, 0));
        rightPanel.setBackground(p.tileDark);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        
        JLabel rightTitle = new JLabel("Details");
        rightTitle.setFont(Theme.TITLE_FONT.deriveFont(22f));
        rightTitle.setForeground(p.textLight);
        rightPanel.add(Box.createVerticalStrut(16));
        rightPanel.add(rightTitle);
        rightPanel.add(Box.createVerticalStrut(8));
        
        // Example: add some placeholder info labels
        rightPanel.add(new JLabel("Select an item to view details."));
        rightPanel.add(Box.createVerticalGlue());
        
        // ----- Assemble Main Layout ----- //
        JPanel card = new JPanel(new BorderLayout(12, 12));
        card.setOpaque(false);
        card.add(leftPanel, BorderLayout.WEST);
        card.add(scrollPane, BorderLayout.CENTER);
        card.add(rightPanel, BorderLayout.EAST);
        
        return card;
    }

    private void populateTable()
    {
        // Handle null pointer issue
        if (manager == null) 
        {
            System.err.println("InventoryPanel ERROR: manager is null — skipping table load");
            return;
        }
        
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
                item.getFavorite() ? "★" : "",
                manager.getLatestQuantity(id)
            };
            tableModel.addRow(row);
        }
    }
}