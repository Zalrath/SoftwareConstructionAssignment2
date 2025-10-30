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
 * Top-left panel: shows income and expense breakdown.
 */
public class IncomeExpensePanel extends JPanel {

    private final InventoryManager manager;
    private final Theme.Palette p;

    public IncomeExpensePanel(InventoryManager manager, Theme.Palette p) {
        this.manager = manager;
        this.p = p;
        buildUI();
    }

    private void buildUI() {
        setLayout(new GridLayout(1, 2, 10, 0));
        setBackground(p.tileMediumDark);
        setBorder(BorderFactory.createLineBorder(p.tileDark, 2));

        // Income side
        JPanel income = createSection("Income");
        JPanel expenses = createSection("Expenses");

        add(income);
        add(expenses);
    }

    private JPanel createSection(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(p.tileDark);

        JLabel header = new JLabel(title, SwingConstants.CENTER);
        header.setFont(Theme.TITLE_FONT.deriveFont(20f));
        header.setForeground(p.textLight);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, p.accent));

        JTextArea area = new JTextArea();
        area.setText("Static placeholder — link to DB or user input here.");
        area.setBackground(p.tileDark);
        area.setForeground(p.textLight);
        area.setFont(Theme.BODY_FONT.deriveFont(14f));
        area.setEditable(false);
        area.setMargin(new Insets(10, 10, 10, 10));

        panel.add(header, BorderLayout.NORTH);
        panel.add(new JScrollPane(area), BorderLayout.CENTER);
        return panel;
    }

    public void refresh() {
        // TODO: Load new income/expense data from manager
    }
}
