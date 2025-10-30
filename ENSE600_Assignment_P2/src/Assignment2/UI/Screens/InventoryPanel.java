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
import Assignment2.UI.AddItemDialog;
import Assignment2.UI.HomeScreen;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class InventoryPanel extends BaseScreenPanel
{
    private DefaultTableModel tableModel;
    private JTable table;
    private final InventoryManager manager;
    
    // using a list instead of a map for row items
    private final List<Item> rowItems = new ArrayList<>();

    // updater for totals
    private Runnable updateTotalsRunnable;
    private final Theme.Palette palette = Theme.palette();
    
    // totals labels
    private final JLabel totalCountLabel = new JLabel();
    private final JLabel uniqueCountLabel = new JLabel();
    private final JLabel totalPriceLabel = new JLabel();

    // ----- Constructor ----- //
    public InventoryPanel(InventoryManager manager)
    {
        super("Inventory", /*showBack*/ true, /*showAdd*/ true, /*addLabel*/ "Add Item", /*backTarget*/ "dashboard");
        this.manager = manager;
        buildBaseUI();
    }

    // ----- Initialise Content ----- //
    @Override
    protected JComponent createCentre()
    {
        // table
        JScrollPane scrollPane = createTableScrollPane();
        
        // filter panel
        JPanel leftPanel = createFilterPanel();
        
        // totals bar 
        JPanel totalsPanel = createTotalsPanel();
        
        // main panel with table
        JPanel card = new JPanel(new BorderLayout(12, 12));
        card.setOpaque(false);
        card.add(leftPanel, BorderLayout.WEST);
        card.add(scrollPane, BorderLayout.CENTER);
        card.add(totalsPanel, BorderLayout.SOUTH);
        
        // initial calculations
        SwingUtilities.invokeLater(updateTotalsRunnable);
        
        return card;
    }
    
    // ----- Actions ----- //
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

    // ----- refresh ----- //
    public void refreshTable()
    {
        populateTable();
        // refilter after refresh
        if (table.getRowSorter() != null)
        {
             ((TableRowSorter) table.getRowSorter()).setRowFilter(((TableRowSorter) table.getRowSorter()).getRowFilter());
        }
        if (updateTotalsRunnable != null) SwingUtilities.invokeLater(updateTotalsRunnable);
    }
    
    // ----- build table ----- //
    private JScrollPane createTableScrollPane()
    {
        String[] columns = 
        {
            "Item", "Tags", "Last Purchased", "Price", "\u2665", "Quantity", "\u2796", "\u2795"
        };
        
        tableModel = new DefaultTableModel(columns, 0)
        {
            @Override
            public boolean isCellEditable(int row, int column)
            {
                // only favourite column editable (model index 4)
                return column == 4;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex)
            {
                return columnIndex == 4 ? Boolean.class : Object.class;
            }
        };
        
        populateTable();
        
        table = new JTable(tableModel);
        table.setAutoCreateRowSorter(true);
        
        applyTableStyling();
        configureColumnModels();
        addTableListeners();
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(palette.tileDark);
        
        // custom scrollbar
        customizeScrollPane(scrollPane);
        
        // corner blank spot patch
        JPanel corner1 = new JPanel();
        corner1.setBackground(palette.tileDark);
        scrollPane.setCorner(JScrollPane.LOWER_RIGHT_CORNER, corner1);
        
        JPanel corner2 = new JPanel();
        corner2.setBackground(palette.tileDark);
        scrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, corner2);
        
        return scrollPane;
    }
    
    // ----- style the table ----- //
    private void applyTableStyling()
    {
        JTableHeader header = table.getTableHeader();
        
        table.setSelectionBackground(palette.tileMediumDark);
        table.setSelectionForeground(palette.textLight);
        table.setRowSelectionAllowed(true);
        table.setCellSelectionEnabled(false);
        
        table.setFont(Theme.BODY_FONT);
        table.setForeground(palette.textLight);
        table.setBackground(palette.tileDark);
        table.setGridColor(palette.tileMediumDark);
        table.setRowHeight(28);
        
        header.setBackground(palette.accent);
        header.setForeground(palette.textLight);
        header.setFont(Theme.TITLE_FONT.deriveFont(24f));
        header.setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, palette.tileDark));
        UIManager.put("TableHeader.cellBorder", BorderFactory.createMatteBorder(0, 0, 2, 1, palette.tileDark));
    }
    
    // ----- setup columns ----- //
    private void configureColumnModels()
    {
        TableColumnModel cm = table.getColumnModel();
        
        // move favours column from 4 -> 0
        table.moveColumn(4, 0);
        int heartViewColumn = table.convertColumnIndexToView(4);
        
        // set column widths
        Map<Integer, int[]> widths = Map.of(
            0, new int[]{50, 60, 55},     // heart
            1, new int[]{180, 9999, 220}, // item
            2, new int[]{180, 9999, 220}, // tags
            3, new int[]{140, 160, 150},  // last purchased
            4, new int[]{90, 110, 100},   // price
            5, new int[]{80, 100, 90},    // quantity
            6, new int[]{45, 55, 50},     // -
            7, new int[]{45, 55, 50}      // +
        );
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        
        // apply the widths and render the table
        for (Map.Entry<Integer, int[]> e : widths.entrySet())
        {
            int viewIndex = e.getKey();
            int[] w = e.getValue();
            TableColumn c = cm.getColumn(viewIndex);
            c.setMinWidth(w[0]);
            c.setMaxWidth(w[1]);
            c.setPreferredWidth(w[2]);
            // items and tags are resizeable
            c.setResizable(viewIndex == 1 || viewIndex == 2);
            // center align all but emoji columns
            if (viewIndex != 0 && viewIndex != 1 && viewIndex != 2) c.setCellRenderer(centerRenderer);
        }
        
        // render
        cm.getColumn(heartViewColumn).setCellRenderer(createHeartCellRenderer());
        cm.getColumn(heartViewColumn).setCellEditor(new DefaultCellEditor(createCenteredCheckbox()));
        cm.getColumn(6).setCellRenderer(createQuantityAdjustRenderer()); // ➖
        cm.getColumn(7).setCellRenderer(createQuantityAdjustRenderer()); // ➕
        
        // render header
        TableCellRenderer symbolHeaderRenderer = createSymbolHeaderRenderer();
        cm.getColumn(heartViewColumn).setHeaderRenderer(symbolHeaderRenderer);
        cm.getColumn(6).setHeaderRenderer(symbolHeaderRenderer);
        cm.getColumn(7).setHeaderRenderer(symbolHeaderRenderer);
    }
    
    // ----- custom renderers ----- //
    private JCheckBox createCenteredCheckbox()
    {
        JCheckBox checkbox = new JCheckBox();
        checkbox.setHorizontalAlignment(SwingConstants.CENTER);
        checkbox.setOpaque(false);
        return checkbox;
    }
    
    private DefaultTableCellRenderer createHeartCellRenderer()
    {
        return new DefaultTableCellRenderer()
        {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int col)
            {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                l.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 20));
                l.setText(Boolean.TRUE.equals(value) ? "♥" : "");
                l.setForeground(palette.textLight);
                l.setBackground(isSelected ? t.getSelectionBackground() : palette.tileDark);
                l.setOpaque(true);
                return l;
            }
        };
    }
    
    private DefaultTableCellRenderer createQuantityAdjustRenderer()
    {
        return new DefaultTableCellRenderer()
        {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int col)
            {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                l.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 20));
                l.setText(value == null ? "" : value.toString());
                l.setForeground(palette.textLight);
                l.setBackground(isSelected ? palette.tileMediumDark : palette.tileDark);
                l.setOpaque(true);
                return l;
            }
        };
    }
    
    private DefaultTableCellRenderer createSymbolHeaderRenderer()
    {
        return new DefaultTableCellRenderer()
        {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int col)
            {
                JLabel label = (JLabel) super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                label.setOpaque(true);
                label.setBackground(palette.accent);
                label.setForeground(palette.textLight);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 24));
                label.setText(value == null ? "" : value.toString());
                return label;
            }
        };
    }
    
    // ----- add table listeners ----- //
    private void addTableListeners()
    {
        // listener for quantity change columns
        table.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                int viewRow = table.rowAtPoint(e.getPoint());
                int viewCol = table.columnAtPoint(e.getPoint());
                if (viewRow < 0 || viewCol < 0) return;
                
                int modelRow = table.convertRowIndexToModel(viewRow);
                int modelCol = table.convertColumnIndexToModel(viewCol);
                
                // model for plus and minus columns
                if (modelCol == 6 || modelCol == 7)
                {
                    if (modelRow >= 0 && modelRow < rowItems.size())
                    {
                         Item item = rowItems.get(modelRow);
                         double qty = item.getCurrentAmount();
                         qty += (modelCol == 7 ? 1 : -1);
                         if (qty < 0) qty = 0;
                         
                         item.setCurrentAmount(qty);
                         // update quantity 
                         tableModel.setValueAt(qty, modelRow, 5);
                         if (updateTotalsRunnable != null) SwingUtilities.invokeLater(updateTotalsRunnable);
                    }
                }
            }
        });
        
        // listener for favourites column
        tableModel.addTableModelListener(e
                -> {
            if (e.getType() == TableModelEvent.UPDATE && e.getColumn() == 4)
            {
                int row = e.getFirstRow();
                if (row >= 0 && row < rowItems.size())
                {
                    Boolean favChecked = (Boolean) tableModel.getValueAt(row, 4);
                    Item item = rowItems.get(row);
                    item.setFavorite(favChecked != null && favChecked);
                }
            }
        });
    } // corin fix pls
    
    // ----- populate table ----- //
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
            
            double latestPrice = manager.getLatestPrice(id);
            tableModel.addRow(new Object[]{
                item.getName(), tags, date,
                String.format("$%.2f", latestPrice),
                item.getFavorite(), item.getCurrentAmount(),
                "\u2796", "\u2795"
            });
        }
    }
    
    // ----- filter panel ----- //
    private JPanel createFilterPanel()
    {
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(220, 0));
        leftPanel.setBackground(palette.tileDark);
        
        // filter header
        JPanel filterHeader = new JPanel(new BorderLayout());
        filterHeader.setBackground(palette.accent);
        int headerHeight = table.getTableHeader().getPreferredSize().height;
        filterHeader.setPreferredSize(new Dimension(0, headerHeight));
        
        JLabel filterTitle = new JLabel("FILTERS", SwingConstants.CENTER);
        filterTitle.setFont(Theme.TITLE_FONT.deriveFont(24f));
        filterTitle.setForeground(palette.textLight);
        filterHeader.add(filterTitle, BorderLayout.CENTER);
        filterHeader.setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, palette.tileDark));
        
        // tag checkboxes
        JPanel tagBoxPanel = new JPanel();
        tagBoxPanel.setLayout(new BoxLayout(tagBoxPanel, BoxLayout.Y_AXIS));
        tagBoxPanel.setOpaque(false);

        List<String> allTags = manager.extractAllTags().stream()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.toList());
        
        List<JCheckBox> tagBoxes = new ArrayList<>();
        Icon uncheckedIcon = createTagCheckboxIcon(false);
        Icon checkedIcon = createTagCheckboxIcon(true);
        
        for (String tag : allTags)
        {
            JCheckBox cb = new JCheckBox(tag);
            cb.setForeground(palette.textLight);
            cb.setOpaque(false);
            cb.setFocusPainted(false);
            cb.setBackground(palette.tileDark);
            
            // custom style
            cb.setIcon(uncheckedIcon);
            cb.setSelectedIcon(checkedIcon);
            
            cb.addActionListener(e -> applyTagFilter(tagBoxes));
            tagBoxes.add(cb);
            tagBoxPanel.add(cb);
        }
        
        JScrollPane tagScroll = new JScrollPane(tagBoxPanel);
        tagScroll.setBorder(BorderFactory.createEmptyBorder());
        tagScroll.getViewport().setBackground(palette.tileDark);
        
        // use the customizer i painstakingly made
        customizeScrollPane(tagScroll);
        
        leftPanel.add(filterHeader, BorderLayout.NORTH);
        leftPanel.add(tagScroll, BorderLayout.CENTER);
        
        return leftPanel;
    }
    
    // ----- tag checkbox icon ----- //
    private Icon createTagCheckboxIcon(boolean selected)
    {
        return new Icon()
        {
            private final int SIZE = 18;
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y)
            {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (selected)
                {
                    // checked
                    g2.setColor(palette.accent);
                    g2.fillRoundRect(x, y, 16, 16, 4, 4);
                    g2.setColor(palette.textLight);
                    g2.setStroke(new BasicStroke(2.2f));
                    g2.drawLine(x + 4, y + 9, x + 8, y + 13);
                    g2.drawLine(x + 8, y + 13, x + 13, y + 5);
                }
                else
                {
                    // unchecked
                    g2.setColor(palette.tileMediumDark);
                    g2.fillRoundRect(x, y, 16, 16, 4, 4);
                    g2.setColor(palette.accent);
                    g2.drawRoundRect(x, y, 16, 16, 4, 4);
                }
                g2.dispose();
            }
            
            @Override
            public int getIconWidth() { return SIZE; }
            @Override
            public int getIconHeight() { return SIZE; }
        };
    }
    
    // ----- totals panel ----- //
    private JPanel createTotalsPanel()
    {
        JPanel totalsPanel = new JPanel(new GridLayout(1, 3));
        totalsPanel.setBackground(palette.tileMediumDark);
        totalsPanel.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, palette.tileDark));
        
        for (JLabel lbl : new JLabel[]{totalCountLabel, uniqueCountLabel, totalPriceLabel})
        {
            lbl.setForeground(palette.textLight);
            lbl.setFont(Theme.BODY_FONT.deriveFont(Font.BOLD, 16f));
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            totalsPanel.add(lbl);
        }
        
        // define the runnable to update totals
        updateTotalsRunnable = () -> {
            int visibleRows = table.getRowCount();
            double totalValue = 0.0;
            double totalQuantity = 0.0;
            Set<String> uniqueNames = new HashSet<>();
            
            for (int viewRow = 0; viewRow < visibleRows; viewRow++)
            {
                int modelRow = table.convertRowIndexToModel(viewRow);
                
                // use item from rowitems list
                if (modelRow >= 0 && modelRow < rowItems.size())
                {
                    Item item = rowItems.get(modelRow);
                    uniqueNames.add(item.getName());
                    totalQuantity += item.getCurrentAmount();
                    
                    // read and parse the price from the formatted string in the model
                    Object priceObj = tableModel.getValueAt(modelRow, 3);
                    try 
                    {
                        double price = Double.parseDouble(priceObj.toString().replace("$", "").trim());
                        totalValue += price * item.getCurrentAmount();
                    } catch (NumberFormatException ignored) {}
                }
            }
            
            // display
            totalCountLabel.setText(String.format("Total Quantity: %.0f", totalQuantity));
            uniqueCountLabel.setText("Unique Items: " + uniqueNames.size());
            totalPriceLabel.setText(String.format("Total Value: $%.2f", totalValue));
        };
        
        return totalsPanel;
    }
    
    // ----- tag filtererer ----- //
    private void applyTagFilter(List<JCheckBox> tagBoxes)
    {
        Set<String> activeTags = tagBoxes.stream()
                .filter(JCheckBox::isSelected)
                .map(cb -> cb.getText().toLowerCase())
                .collect(Collectors.toSet());
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(tableModel);
        
        // only filter if cb is checked
        if (!activeTags.isEmpty())
        {
            sorter.setRowFilter(new RowFilter<TableModel, Integer>()
            {
                @Override
                public boolean include(Entry<? extends TableModel, ? extends Integer> entry)
                {
                    // read the tags
                    String itemTags = Optional.ofNullable(entry.getStringValue(1)).orElse("").toLowerCase();
                    
                    for (String activeTag : activeTags)
                    {
                        // filter matches if the items tag string is equal to any of the active filtered tags
                        if (itemTags.contains(activeTag)) return true;
                    }
                    return false;
                }
            });
        }
        // if no filter is applied all tags are shown
        
        table.setRowSorter(sorter);
        SwingUtilities.invokeLater(updateTotalsRunnable);
    }
    
    // ----- custom scrollbars ----- //
    private void customizeScrollPane(JScrollPane scrollPane)
    {
        customizeScrollBar(scrollPane.getVerticalScrollBar(), true);
        customizeScrollBar(scrollPane.getHorizontalScrollBar(), false);
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
}