package Assignment2.UI.Screens;

import Assignment2.UI.Template.BaseScreenPanel;
import Assignment2.UI.Theme;
import Assignment2.Inventory.InventoryManager;
import Assignment2.Inventory.Item;
import Assignment2.UI.AddItemDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

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
        String[] columns = {"Item", "Tags", "Last Purchased", "Price", "u\2764", "Quantity"};
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
        JTableHeader header = table.getTableHeader();
        
        Theme.Palette p = Theme.palette();
        table.setFont(Theme.BODY_FONT);
        table.setForeground(p.textLight);
        table.setBackground(p.tileDark);
        table.setGridColor(p.tileMediumDark);
        table.setRowHeight(28);
        
        // Header
        table.getTableHeader().setBackground(p.accent);
        table.getTableHeader().setForeground(p.textLight);
        table.getTableHeader().setFont(Theme.TITLE_FONT.deriveFont(24f));
        header.setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, p.tileDark));
        UIManager.put("TableHeader.cellBorder", BorderFactory.createMatteBorder(0, 0, 2, 1, p.tileDark));
        
        // Column
        TableColumnModel colModel = table.getColumnModel();
        int[] minWidths = {180, 180, 160, 100, 80, 100};
        for (int i = 0; i < minWidths.length && i < colModel.getColumnCount(); i++) {
            TableColumn column = colModel.getColumn(i);
            column.setMinWidth(minWidths[i]);
            column.setPreferredWidth(minWidths[i]);
        }

        
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
    
    @Override
    protected void onAdd() 
    {
        Window parent = SwingUtilities.getWindowAncestor(this);

        AddItemDialog.show(parent).ifPresent(data -> 
        {
            // new item
            Item newItem = new Item(data.name);
            newItem.setCurrentAmount(data.quantity);

            // tag
            ArrayList<String> tags = new ArrayList<>();
            if (data.category != null && !data.category.isEmpty()) 
            {
                tags.add(data.category.trim());
            }
            newItem.setTags(tags);
            
            // use current day as purchase date
            newItem.setLastPurchased(java.time.LocalDate.now());
            
            // save 
            manager.addItem(newItem);
            manager.logPurchase( newItem.getUuid(),
                    data.unitCost, // price
                    data.quantity, // amount
                    java.time.LocalDate.now()
            );
            
            // refresh the table
            refreshTable();
            
            // test pop up 
            JOptionPane.showMessageDialog(this, "Item added: " + newItem.getName(), "Success", JOptionPane.INFORMATION_MESSAGE);
        });
    }
    
    // for live
    private void refreshTable() 
    {
        tableModel.setRowCount(0);
        
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        for (Item item : manager.getAllItems()) 
        {
            tableModel.addRow(new Object[]
            {
                item.getName(),
                String.join(", ", item.getTags()),
                item.getLastPurchased() != null ? item.getLastPurchased().format(fmt) : "-",
                String.format("%.2f", manager.getLatestPrice(item.getUuid())),
                item.getCurrentAmount()
            });
        }
    }
    
    // my thinking is to move the inventorymanager instance to the homescreen which is where all the other panels are initialised so they can share it,
    // otherwise each panel will make multiple instances of the same db connection which will create more null table bs, please branch once u read this message
    // also if i cant get it to work just be prepared for that lmao
    

    
    
}