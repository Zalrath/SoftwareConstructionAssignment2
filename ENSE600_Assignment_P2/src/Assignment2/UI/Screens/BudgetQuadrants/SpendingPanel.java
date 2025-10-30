/*
 * click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Assignment2.UI.Screens.BudgetQuadrants;

/**
 *
 * @author megan
 */

import Assignment2.UI.Template.ToggleableButton;
import Assignment2.UI.Template.ToggleableButtonGroup;
import Assignment2.UI.Theme;
import Assignment2.Inventory.InventoryManager;
import Assignment2.Inventory.Item;
import Assignment2.Inventory.PurchaseLog;
import static Assignment2.UI.Theme.*;  

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.chart.ui.RectangleInsets;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class SpendingPanel extends JPanel
{

    private final InventoryManager manager;
    private Theme.Palette palette;  

    // chart state
    private final DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
    private JFreeChart chart;
    private ChartPanel chartPanel;

    // ----- constructor ----- //
    public SpendingPanel(InventoryManager manager, Theme.Palette palette)
    {
        this.manager = manager;
        this.palette = palette;
        buildUI();
    }

    // ----- initialise ui ----- //
    private void buildUI()
    {
        setLayout(new BorderLayout());
        
        // update theme variables before building
        this.palette = Theme.palette();
       
        
        setBackground(palette.tileMediumDark);
        setBorder(BorderFactory.createLineBorder(palette.tileDark, 2));
        
        // header
        JLabel header = new JLabel("spending by category", SwingConstants.CENTER);
        header.setFont(Theme.TITLE_FONT.deriveFont(28f)); 
        header.setForeground(palette.textLight);
        header.setPreferredSize(new Dimension(300, 45));
        header.setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, palette.tileDark));
        add(header, BorderLayout.NORTH);
        
        // main content 
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(palette.tileMediumDark);
        
        JPanel buttonColumn = createButtonColumn();
        chartPanel = buildPieChartPanel();
        
        mainContent.add(buttonColumn, BorderLayout.WEST);
        mainContent.add(chartPanel, BorderLayout.CENTER);
        
        add(mainContent, BorderLayout.CENTER);
        refresh("week");
    }

    // ----- button column builder ----- //
    private JPanel createButtonColumn()
    {
        JPanel col = new JPanel();
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        col.setBackground(palette.tileMediumDark);
        col.setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, palette.tileDark));
        col.setPreferredSize(new Dimension(120, 0));
        
        // filter header box
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(palette.accent);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        JLabel filterLabel = new JLabel("filter by", SwingConstants.CENTER);
        filterLabel.setForeground(palette.textLight);
        filterLabel.setFont(Theme.TITLE_FONT.deriveFont(18f));
        header.add(filterLabel, BorderLayout.CENTER);
        
        col.add(header);
        col.add(Box.createVerticalStrut(10));
        
        // buttons
        ToggleableButton weekly = new ToggleableButton("week");
        ToggleableButton monthly = new ToggleableButton("month");
        ToggleableButton yearly = new ToggleableButton("year");
        ToggleableButton all = new ToggleableButton("all time");
        
        ToggleableButtonGroup group = new ToggleableButtonGroup();
        group.addButton(weekly);
        group.addButton(monthly);
        group.addButton(yearly);
        group.addButton(all);
        weekly.setSelected(true);
        
        Dimension size = new Dimension(100, 30);
        ToggleableButton[] buttons = {weekly, monthly, yearly, all};
        for (ToggleableButton btn : buttons)
        {
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setMaximumSize(size);
            btn.setPreferredSize(size);
            btn.addActionListener(e ->
            {
                // refresh on click
                refresh(btn.getText().toLowerCase());
            });
            col.add(btn);
            col.add(Box.createVerticalStrut(8));
        }
        
        return col;
    }
    
    // ----- chart builder ----- //
    private ChartPanel buildPieChartPanel()
    {
        // initial default data
        dataset.setValue("no data", 100);
        
        chart = ChartFactory.createPieChart(null, dataset, false, true, false);
        PiePlot<String> plot = (PiePlot<String>) chart.getPlot();
        
        // style
        plot.setBackgroundPaint(palette.tileDark);
        plot.setOutlineVisible(false);
        plot.setLabelBackgroundPaint(palette.tileMediumDark);
        plot.setLabelFont(Theme.BODY_FONT.deriveFont(12f));
        plot.setLabelPaint(palette.textLight);
        plot.setShadowPaint(null);
        plot.setInteriorGap(0.04);
        plot.setSimpleLabels(false);
        plot.setLabelLinksVisible(true);
        plot.setLabelLinkStroke(new BasicStroke(1.2f));
        plot.setLabelLinkPaint(palette.textLight);
        plot.setLabelLinkMargin(0.02);
        plot.setLabelGap(0.04);
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: {1} ({2})"));
        
        chart.setBackgroundPaint(palette.tileDark);
        chart.setPadding(new RectangleInsets(5, 5, 5, 5));
        
//        // color mapping (fallbacks) - these will be overridden by refreshChart
//        plot.setSectionPaint("groceries", new Color(0x66BB6A));
//        plot.setSectionPaint("entertainment", palette.hazard);
//        plot.setSectionPaint("utilities", palette.accent);
//        plot.setSectionPaint("transport", new Color(0xFFB300));
//        plot.setSectionPaint("misc", palette.tileMedium);
        
        ChartPanel panel = new ChartPanel(chart);
        panel.setOpaque(false);
        panel.setMouseWheelEnabled(true);
        panel.setPopupMenu(null);
        panel.setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, palette.tileDark));
        return panel;
    }
    
    // ----- refresh chart ----- //
    public void refreshChart(Map<String, Double> spendingData)
    {
        dataset.clear();
        PiePlot<String> plot = (PiePlot<String>) chart.getPlot();
        
        // check trheme colours
        this.palette = Theme.palette();
        
        // use tags to create slices
        List<String> categories = new ArrayList<>(spendingData.keySet());
        int count = categories.size();
        
        if (spendingData.isEmpty())
        {
            dataset.setValue("no data", 100);
            // default slice
            plot.setSectionPaint("no data", palette.tileDark);
        }
        else
        {
            // create colours depending on accent and slices
            List<Color> generatedColors = generateChartColors(palette.accent, count);
            
            int i = 0;
            for (var entry : spendingData.entrySet())
            {
                String category = entry.getKey();
                dataset.setValue(category, entry.getValue());
                
                // apply the colour
                plot.setSectionPaint(category, generatedColors.get(i % generatedColors.size()));
                i++;
            }
        }
        
        // update background 
        plot.setLabelLinkPaint(palette.textLight);
        plot.setLabelPaint(palette.textLight);
        
        chart.fireChartChanged();
        chartPanel.repaint();
    }
    
    // ----- main refresh logic ----- //
    public void refresh()
    {
        refresh("all time");
    }
    
    public void refresh(String filter)
    {
        Map<UUID, List<PurchaseLog>> history = manager.getPurchaseHistory();
        if (history == null || history.isEmpty())
        {
            refreshChart(Collections.emptyMap());
            return;
        }
        
        Map<String, Double> spendingByTag = new HashMap<>();
        java.time.LocalDate cutoff = switch (filter)
        {
            case "week" -> java.time.LocalDate.now().minusDays(7);
            case "month" -> java.time.LocalDate.now().minusMonths(1);
            case "year" -> java.time.LocalDate.now().minusYears(1);
            default -> null; // all time
        };
        
        for (var entry : history.entrySet())
        {
            UUID itemId = entry.getKey();
            List<PurchaseLog> logs = entry.getValue();
            if (logs == null || logs.isEmpty()) continue;
            
            Item item = manager.getItemByUUID(itemId);
            if (item == null) continue;
            
            ArrayList<String> tags = item.getTags();
            if (tags == null || tags.isEmpty()) continue;
            String firstTag = tags.get(0);
            
            double totalForItem = 0.0;
            for (PurchaseLog log : logs)
            {
                if (cutoff != null && log.getPurchaseDate().isBefore(cutoff)) continue;
                totalForItem += log.getPrice() * log.getQuantity();
            }
            
            if (totalForItem > 0)
                spendingByTag.merge(firstTag, totalForItem, Double::sum);
        }
        
        refreshChart(spendingByTag);
    }
    // ----- chart colourer ----- //
    private List<Color> generateChartColors(Color accent, int count)
    {
        List<Color> colors = new ArrayList<>();
        
        // rgb to hsb
        float[] hsb = Color.RGBtoHSB(accent.getRed(), accent.getGreen(), accent.getBlue(), null);
        float baseHue = hsb[0]; 
        float baseSat = hsb[1];
        float baseBright = hsb[2];
        
        // range
        float brightnessStep = 0.6f / count; 
        
        for (int i = 0; i < count; i++)
        {
            // step bnrightness
            float brightness = baseBright - (i * brightnessStep);
            if (brightness < 0.4f)
            {
                brightness = 1.0f - brightness; // wrap/shift to use lighter tones too
            }
            
            // slightly drop saturation for darker shades
            float saturation = baseSat * (1 - (i * 0.1f)); 
            
            // clamp values
            saturation = Math.max(0.3f, Math.min(1.0f, saturation));
            brightness = Math.max(0.3f, Math.min(1.0f, brightness));
            
            // create the new color
            colors.add(Color.getHSBColor(baseHue, saturation, brightness));
        }
        
        // always start with the original accent color for the largest slice if desired
        if (!colors.isEmpty())
        {
            colors.set(0, accent);
        }
        
        return colors;
    }
}