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

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.chart.ui.RectangleInsets;

import java.awt.Color;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.Map;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;

public class SpendingPanel extends JPanel
{

    private final InventoryManager manager;
    private final Theme.Palette palette;

    // chart setup
    private DefaultPieDataset<String> dataset;
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
        setBackground(palette.tileMediumDark);
        setBorder(BorderFactory.createLineBorder(palette.tileDark, 2));

        // header
        JLabel header = new JLabel("spending by category", SwingConstants.CENTER);
        header.setFont(Theme.TITLE_FONT.deriveFont(28f));
        header.setForeground(palette.textLight);
        header.setPreferredSize(new Dimension(300, 40));
        header.setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, palette.tileDark));
        add(header, BorderLayout.NORTH);

        // main content area (left column + right chart)
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(palette.tileMediumDark);

        // ----- button column on the left ----- //
        JPanel buttonColumn = createButtonColumn();

        // ----- pie chart panel on the right ----- //
        chartPanel = buildPieChartPanel();

        // add both to the main content area
        mainContent.add(buttonColumn, BorderLayout.WEST);
        mainContent.add(chartPanel, BorderLayout.CENTER);

        add(mainContent, BorderLayout.CENTER);
    }

    // ----- helper methods for ui blocks ----- //
    private JPanel createButtonColumn()
    {
        JPanel buttonColumn = new JPanel();
        buttonColumn.setLayout(new BoxLayout(buttonColumn, BoxLayout.Y_AXIS));
        buttonColumn.setBackground(palette.tileMediumDark);
        buttonColumn.setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, palette.tileDark));
        buttonColumn.setPreferredSize(new Dimension(120, 0)); // sidebar width

        // filter by header block
        JPanel filterHeader = new JPanel(new BorderLayout());
        filterHeader.setBackground(palette.accent);
        filterHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        filterHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
        // no border applied here (removed old bottom border)

        JLabel filterLabel = new JLabel("filter by", SwingConstants.CENTER);
        filterLabel.setForeground(palette.textLight);
        filterLabel.setFont(Theme.TITLE_FONT.deriveFont(24f));
        filterHeader.add(filterLabel, BorderLayout.CENTER);

        // add header to column
        buttonColumn.add(filterHeader);
        buttonColumn.add(Box.createVerticalStrut(10)); // space before buttons

        // create and group buttons
        ToggleableButton weeklyBtn = new ToggleableButton("week");
        ToggleableButton monthlyBtn = new ToggleableButton("month");
        ToggleableButton yearlyBtn = new ToggleableButton("year");
        ToggleableButton allTimeBtn = new ToggleableButton("all time");

        ToggleableButtonGroup group = new ToggleableButtonGroup();
        group.addButton(weeklyBtn);
        group.addButton(monthlyBtn);
        group.addButton(yearlyBtn);
        group.addButton(allTimeBtn);
        weeklyBtn.setSelected(true); // default selection

        // compact sizing and spacing
        Dimension btnSize = new Dimension(100, 30);
        ToggleableButton[] buttons = {weeklyBtn, monthlyBtn, yearlyBtn, allTimeBtn};

        for (ToggleableButton btn : buttons)
        {
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setMaximumSize(btnSize);
            btn.setPreferredSize(btnSize);
            buttonColumn.add(btn);
            buttonColumn.add(Box.createVerticalStrut(8)); // space between buttons
        }

        return buttonColumn;
    }

    // ----- chart builder ----- //
    private ChartPanel buildPieChartPanel()
    {
        dataset = new DefaultPieDataset<>();

        // placeholder sample data
        dataset.setValue("groceries", 35);
        dataset.setValue("entertainment", 15);
        dataset.setValue("utilities", 20);
        dataset.setValue("transport", 10);
        dataset.setValue("misc", 20);

        chart = ChartFactory.createPieChart(
                null,
                dataset,
                false, // no legend
                true,  // tooltips
                false  // no urls
        );

        PiePlot<String> plot = (PiePlot<String>) chart.getPlot();
        plot.setBackgroundPaint(palette.tileDark);
        plot.setOutlineVisible(false);
        plot.setLabelBackgroundPaint(palette.tileMediumDark);
        plot.setLabelFont(Theme.BODY_FONT.deriveFont(12f));
        plot.setLabelPaint(palette.textLight);
        plot.setShadowPaint(null);
        plot.setInteriorGap(0.04);

        // labels and theme integration
        plot.setSimpleLabels(false);
        plot.setLabelLinkPaint(palette.textLight);
        plot.setLabelOutlinePaint(null);
        plot.setLabelShadowPaint(null);
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator(
                "{0}: {1} ({2})", // label format: name : value (percent)
                new DecimalFormat("$#,##0.00"), // value format — shows currency with 2 decimals
                new DecimalFormat("0%") // percentage format
        ));

        chart.setBackgroundPaint(palette.tileDark);
        chart.setPadding(new RectangleInsets(5, 5, 5, 5));

        // use theme colors for sections
        plot.setSectionPaint("groceries", new Color(0x66BB6A));
        plot.setSectionPaint("entertainment", palette.hazard);
        plot.setSectionPaint("utilities", palette.accent);
        plot.setSectionPaint("transport", new Color(0xFFB300));
        plot.setSectionPaint("misc", palette.tileMedium);


        ChartPanel panel = new ChartPanel(chart);
        panel.setOpaque(false);
        panel.setMouseWheelEnabled(true);
        panel.setPopupMenu(null);
        panel.setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, palette.tileDark));

        return panel;
    }


    // ----- chart data api ----- //
    public void refreshChart(Map<String, Double> spendingData)
    {
        dataset.clear();
        for (var entry : spendingData.entrySet())
        {
            dataset.setValue(entry.getKey(), entry.getValue());
        }
        chart.fireChartChanged();
    }

    // ----- external refresh ----- //
    public void refresh()
    {
        // todo: aggregate spend by tags and update pie chart
    }
}