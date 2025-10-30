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

public class SavingsPanel extends JPanel
{

    private final InventoryManager manager;
    private final Theme.Palette palette; // renamed 'p' to 'palette'

    // ----- constructor ----- //
    public SavingsPanel(InventoryManager manager, Theme.Palette palette)
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
        JLabel header = new JLabel("savings", SwingConstants.CENTER);
        header.setFont(Theme.TITLE_FONT.deriveFont(20f));
        header.setForeground(palette.textLight);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, palette.accent));

        // placeholder for graph
        JPanel barPlaceholder = new JPanel();
        barPlaceholder.setBackground(palette.tileDark);
        
        // placeholder label
        JLabel placeholderLabel = new JLabel("(savings bar chart placeholder)", SwingConstants.CENTER);
        placeholderLabel.setForeground(palette.textLight.darker());
        barPlaceholder.add(placeholderLabel);

        add(header, BorderLayout.NORTH);
        add(barPlaceholder, BorderLayout.CENTER);
    }

    // ----- external refresh ----- //
    public void refresh()
    {
        // todo: query purchase history or total saved values from manager
    }
}