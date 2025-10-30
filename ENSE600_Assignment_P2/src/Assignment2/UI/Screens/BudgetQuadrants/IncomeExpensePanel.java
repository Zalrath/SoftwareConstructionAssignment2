/*
 * click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
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

public class IncomeExpensePanel extends JPanel
{

    private final InventoryManager manager;
    private final Theme.Palette palette; // renamed 'p' to 'palette'

    // ----- constructor ----- //
    public IncomeExpensePanel(InventoryManager manager, Theme.Palette palette)
    {
        this.manager = manager;
        this.palette = palette;
        buildUI();
    }

    // ----- initialise ui ----- //
    private void buildUI()
    {
        setLayout(new GridLayout(1, 2, 10, 0));
        setBackground(palette.tileMediumDark);
        setBorder(BorderFactory.createLineBorder(palette.tileDark, 2));

        // income side
        JPanel income = createSection("income");
        JPanel expenses = createSection("expenses");

        add(income);
        add(expenses);
    }

    // ----- section builder ----- //
    private JPanel createSection(String title)
    {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(palette.tileDark);

        JLabel header = new JLabel(title, SwingConstants.CENTER);
        header.setFont(Theme.TITLE_FONT.deriveFont(20f));
        header.setForeground(palette.textLight);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, palette.accent));

        JTextArea area = new JTextArea();
        area.setText("program here");
        area.setBackground(palette.tileDark);
        area.setForeground(palette.textLight);
        area.setFont(Theme.BODY_FONT.deriveFont(14f));
        area.setEditable(false);
        area.setMargin(new Insets(10, 10, 10, 10));
        
        // hide the scrollpane border for a cleaner look
        JScrollPane scrollPane = new JScrollPane(area);
        scrollPane.setBorder(null); 

        panel.add(header, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    // ----- external refresh ----- //
    public void refresh()
    {
        // todo: load new income/expense data from manager
    }
}