/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
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

        // ----- header ----- //
        JLabel header = new JLabel("Spending by Category", SwingConstants.CENTER);
        header.setFont(Theme.TITLE_FONT.deriveFont(20f));
        header.setForeground(p.textLight);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, p.accent));
        add(header, BorderLayout.NORTH);

        // ----- button row ----- // 
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(p.tileMedium);

        // create buttons
        ToggleableButton weeklyBtn = new ToggleableButton("Weekly");
        ToggleableButton monthlyBtn = new ToggleableButton("Monthly");
        ToggleableButton yearlyBtn = new ToggleableButton("Yearly");
        ToggleableButton allTimeBtn = new ToggleableButton("All Time");

        // group mode
        ToggleableButtonGroup group = new ToggleableButtonGroup();
        group.addButton(weeklyBtn);
        group.addButton(monthlyBtn);
        group.addButton(yearlyBtn);
        group.addButton(allTimeBtn);

        // set default
        weeklyBtn.setSelected(true);

        // add buttons to panel
        buttonPanel.add(weeklyBtn);
        buttonPanel.add(monthlyBtn);
        buttonPanel.add(yearlyBtn);
        buttonPanel.add(allTimeBtn);

        add(buttonPanel, BorderLayout.SOUTH);

        // placeholder
        JPanel piePlaceholder = new JPanel(new GridBagLayout());
        piePlaceholder.setBackground(p.tileDark);

        JLabel placeholder = new JLabel("(Pie chart placeholder)", SwingConstants.CENTER);
        placeholder.setFont(Theme.BODY_FONT);
        placeholder.setForeground(new Color(255, 255, 255, 100));

        piePlaceholder.add(placeholder);
        add(piePlaceholder, BorderLayout.CENTER);
    }

    public void refresh() {
        // TODO: Aggregate spend by tags and update pie chart
    }
}
