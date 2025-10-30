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

import javax.swing.*;
import java.awt.*;

public class SpendingPanel extends JPanel
{
    private final InventoryManager manager;
    private final Theme.Palette palette; 
    
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
        
        // ----- pie chart placeholder on the right ----- //
        JPanel piePlaceholder = createPiePlaceholder();
        
        // add both to the main content area
        mainContent.add(buttonColumn, BorderLayout.WEST);
        mainContent.add(piePlaceholder, BorderLayout.CENTER);
        
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
        
        // filter by"header block
        JPanel filterHeader = new JPanel(new BorderLayout());
        filterHeader.setBackground(palette.accent);
        filterHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28)); 
        filterHeader.setAlignmentX(Component.CENTER_ALIGNMENT); 
        
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
    
    private JPanel createPiePlaceholder()
    {
        JPanel piePlaceholder = new JPanel(new GridBagLayout());
        piePlaceholder.setBackground(palette.tileDark);

        JLabel placeholder = new JLabel("(pie chart placeholder)", SwingConstants.CENTER);
        placeholder.setFont(Theme.BODY_FONT);
        placeholder.setForeground(new Color(255, 255, 255, 100));
        piePlaceholder.add(placeholder);
        
        return piePlaceholder;
    }
    
    // ----- external refresh ----- //
    public void refresh()
    {
        // todo: aggregate spend by tags and update pie chart
    }
}