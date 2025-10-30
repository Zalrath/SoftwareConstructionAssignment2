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

import java.awt.*;

/**
 * Top-right panel: visualises weekly, monthly, yearly, and all-time comparisons.
 */
public class BudgetVsActualPanel extends JPanel {

    private final InventoryManager manager;
    private final Theme.Palette p;

    public BudgetVsActualPanel(InventoryManager manager, Theme.Palette p) {
        this.manager = manager;
        this.p = p;
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(p.tileMediumDark);
        setBorder(BorderFactory.createLineBorder(p.tileDark, 2));

        JLabel header = new JLabel("Budget vs Actual", SwingConstants.CENTER);
        header.setFont(Theme.TITLE_FONT.deriveFont(20f));
        header.setForeground(p.textLight);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, p.accent));

        // Placeholder for future chart
        JPanel chartPlaceholder = new JPanel();
        chartPlaceholder.setBackground(p.tileDark);
        chartPlaceholder.add(new JLabel("(Bar chart placeholder)", SwingConstants.CENTER));

        add(header, BorderLayout.NORTH);
        add(chartPlaceholder, BorderLayout.CENTER);
    }

    public void refresh() {
        // TODO: Recompute chart data from InventoryManager
    }
}
