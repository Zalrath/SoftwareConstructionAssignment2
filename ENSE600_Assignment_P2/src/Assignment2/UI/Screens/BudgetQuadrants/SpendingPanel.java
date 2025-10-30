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
 * Bottom-right panel: pie chart of spending by category/tags.
 */
public class SpendingPanel extends JPanel {

    private final InventoryManager manager;
    private final Theme.Palette p;

    public SpendingPanel(InventoryManager manager, Theme.Palette p) {
        this.manager = manager;
        this.p = p;
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        setBackground(p.tileMediumDark);
        setBorder(BorderFactory.createLineBorder(p.tileDark, 2));

        JLabel header = new JLabel("Spending by Category", SwingConstants.CENTER);
        header.setFont(Theme.TITLE_FONT.deriveFont(20f));
        header.setForeground(p.textLight);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, p.accent));

        JPanel piePlaceholder = new JPanel();
        piePlaceholder.setBackground(p.tileDark);
        piePlaceholder.add(new JLabel("(Pie chart placeholder)", SwingConstants.CENTER));

        add(header, BorderLayout.NORTH);
        add(piePlaceholder, BorderLayout.CENTER);
    }

    public void refresh() {
        // TODO: Aggregate spend by tags and update pie chart
    }
}
