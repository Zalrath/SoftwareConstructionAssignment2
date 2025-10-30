package Assignment2.UI.Screens;

import Assignment2.UI.Template.BaseScreenPanel;
import Assignment2.UI.Theme;
import Assignment2.Inventory.InventoryManager;
import Assignment2.Inventory.Item;
import Assignment2.UI.AddItemDialog;
import Assignment2.UI.HomeScreen;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InventoryPanel extends BaseScreenPanel 
{
    private DefaultTableModel tableModel;
    private JTable table;
    private final InventoryManager manager;
    private final List<Item> rowItems = new ArrayList<>();
    
    // ----- Constructor ----- //
    public InventoryPanel(InventoryManager manager) 
    {
        super("Inventory", true, true, "Add Item", "dashboard");
        this.manager = manager;
        buildBaseUI();
    }
    
    // ----- Content ----- //
    @Override
    protected JComponent createCentre() 
    {
        // ----- Table Setup ----- //
        String[] columns = {"Item", "Tags", "Last Purchased", "Price", "\u2665", "Quantity"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) 
            {
                // only fav column ios editble 
                return column == 4;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) 
            {
                return columnIndex == 4 ? Boolean.class : Object.class;
            }
        };
        
        populateTable();
        
        // ----- Create JTable ----- // 
        table = new JTable(tableModel);
        table.setAutoCreateRowSorter(true);
        
        Theme.Palette p = Theme.palette();
        JTableHeader header = table.getTableHeader();
        
        // theme table 
        table.setSelectionBackground(table.getBackground());
        table.setSelectionForeground(table.getForeground());
        table.setRowSelectionAllowed(true);   
        table.setCellSelectionEnabled(false);

        table.setFont(Theme.BODY_FONT);
        table.setForeground(p.textLight);
        table.setBackground(p.tileDark);
        table.setGridColor(p.tileMediumDark);
        table.setRowHeight(28);
        
        // header 
        header.setBackground(p.accent);
        header.setForeground(p.textLight);
        header.setFont(Theme.TITLE_FONT.deriveFont(24f));
        header.setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, p.tileDark));
        UIManager.put("TableHeader.cellBorder", BorderFactory.createMatteBorder(0, 0, 2, 1, p.tileDark));
        
        // move fav column
        table.moveColumn(4, 0);
        
        // col widths
        TableColumnModel columnModel = table.getColumnModel();
        int[] minWidths = {40, 180, 180, 160, 100, 100};
        for (int i = 0; i < minWidths.length && i < columnModel.getColumnCount(); i++) 
        {
            TableColumn column = columnModel.getColumn(i);
            column.setMinWidth(minWidths[i]);
            column.setPreferredWidth(minWidths[i]);
        }
        
        // resolve heart column index after move
        int heartViewColumn = table.convertColumnIndexToView(4);
        
        // render for heart 
        DefaultTableCellRenderer heartRenderer = new DefaultTableCellRenderer() 
        {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int col) 
            {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                l.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 20));
                l.setText(Boolean.TRUE.equals(value) ? "♥" : "");
                l.setForeground(p.textLight);
                l.setBackground(isSelected ? t.getSelectionBackground() : p.tileDark);
                l.setOpaque(true);
                return l;
            }
        };
        columnModel.getColumn(heartViewColumn).setCellRenderer(heartRenderer);
        
        // Editor (checkbox in background)
        JCheckBox checkbox = new JCheckBox();
        checkbox.setHorizontalAlignment(SwingConstants.CENTER);
        checkbox.setOpaque(false);
        columnModel.getColumn(heartViewColumn).setCellEditor(new DefaultCellEditor(checkbox));
        
        // header renderer for the heart
        columnModel.getColumn(heartViewColumn).setHeaderRenderer(new DefaultTableCellRenderer() 
        {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int col) 
            {
                JLabel label = (JLabel) super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                label.setOpaque(true);
                label.setBackground(p.accent);
                label.setForeground(p.textLight);
                label.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 24));
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setText("♥");
                return label;
            }
        });
        
        // Update Item favourites when toggled
        tableModel.addTableModelListener(e -> 
        {
            if (e.getType() == TableModelEvent.UPDATE && e.getColumn() == 4) 
            {
                int row = e.getFirstRow();
                if (row >= 0 && row < rowItems.size()) 
                {
                    // read checkbox state
                    Boolean favChecked = (Boolean) tableModel.getValueAt(row, 4);

                    // item row
                    Item item = rowItems.get(row);
                    
                    
                    // table memory
                    item.setFavorite(favChecked != null && favChecked);
                    
                    
                    
                    
                    // probably here corin 
                    
                    
                    
                }
            }
        });


        // Scroll pane
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(p.tileDark);

        // Left panel
        JPanel leftPanel = new JPanel();
        leftPanel.setPreferredSize(new Dimension(220, 0));
        leftPanel.setBackground(p.tileDark);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        JLabel leftTitle = new JLabel("Filters");
        leftTitle.setFont(Theme.TITLE_FONT.deriveFont(22f));
        leftTitle.setForeground(p.textLight);
        leftPanel.add(Box.createVerticalStrut(16));
        leftPanel.add(leftTitle);
        leftPanel.add(Box.createVerticalStrut(8));

        // Assemble layout
        JPanel card = new JPanel(new BorderLayout(12, 12));
        card.setOpaque(false);
        card.add(leftPanel, BorderLayout.WEST);
        card.add(scrollPane, BorderLayout.CENTER);

        return card;
    }

    private void populateTable() 
    {
        tableModel.setRowCount(0);
        rowItems.clear();

        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd MMM yyyy");

        for (Item item : manager.getAllItems()) 
        {
            rowItems.add(item);
            UUID id = item.getUuid();
            String tags = String.join(", ", item.getTags());
            String date = item.getLastPurchased() != null ? item.getLastPurchased().format(df) : "—";
            
            tableModel.addRow(new Object[]
            {
                item.getName(),
                tags,
                date,
                String.format("$%.2f", manager.getLatestPrice(id)),
                item.getFavorite(),
                item.getCurrentAmount()
            });
        }
    }
    
    @Override
    protected void onAdd() 
    {
        Window parent = SwingUtilities.getWindowAncestor(this);
        AddItemDialog.show(parent).ifPresent(data
                -> {
            if (parent instanceof HomeScreen homescreen) 
            {
                homescreen.addNewItem(data);
            }
        });
    }
    
    public void refreshTable() 
    {
        tableModel.setRowCount(0);
        rowItems.clear();
        
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        for (Item item : manager.getAllItems()) 
        {
            rowItems.add(item);
            tableModel.addRow(new Object[]{
                item.getName(),
                String.join(", ", item.getTags()),
                item.getLastPurchased() != null ? item.getLastPurchased().format(fmt) : "-",
                String.format("%.2f", manager.getLatestPrice(item.getUuid())),
                item.getFavorite(),
                item.getCurrentAmount()
            });
        }
    }
}
