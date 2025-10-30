/*
 * click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package Assignment2.UI.Screens;

/**
 *
 * @author megan
 */


import Assignment2.UI.Template.BaseScreenPanel;
import Assignment2.UI.Theme;
import Assignment2.Inventory.InventoryManager;
import Assignment2.UI.Screens.BudgetQuadrants.BudgetVsActualPanel;
import Assignment2.UI.Screens.BudgetQuadrants.IncomeExpensePanel;
import Assignment2.UI.Screens.BudgetQuadrants.SavingsPanel;
import Assignment2.UI.Screens.BudgetQuadrants.SpendingPanel;

import javax.swing.*;

import java.awt.*;

// Items top level tag options
// - Groceries
// - Household Essentials
// - Personal Care
// - Hobby
// - Medicine

// to be implemented as bills/expenses
// Housing
// Utilities
// Transport
// Misc Bills

// Pie chart catagories ^ 







public class BudgetPanel extends BaseScreenPanel
{

    private final InventoryManager manager;

    // sub-panels
    private IncomeExpensePanel incomeExpensePanel;
    private BudgetVsActualPanel budgetVsActualPanel;
    private SavingsPanel savingsPanel;
    private SpendingPanel spendingPanel;

    // ----- constructor ----- //
    public BudgetPanel(InventoryManager manager)
    {
        super("budget", true, true, "add item", "dashboard");
        this.manager = manager;
        buildBaseUI();
    }

    // ----- initialise content ----- //
    @Override
    protected JComponent createCentre()
    {
        Theme.Palette palette = Theme.palette(); // renamed 'p' to 'palette'

        // 2x2 grid layout for the quadrants
        JPanel grid = new JPanel(new GridLayout(2, 2, 10, 10));
        grid.setOpaque(true);
        grid.setBackground(palette.background);
        grid.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // initialise sub-panels
        incomeExpensePanel = new IncomeExpensePanel(manager, palette);
        budgetVsActualPanel = new BudgetVsActualPanel(manager, palette);
        savingsPanel = new SavingsPanel(manager, palette);
        spendingPanel = new SpendingPanel(manager, palette);

        // add panels to grid
        grid.add(incomeExpensePanel);  // top-left
        grid.add(budgetVsActualPanel); // top-right
        grid.add(savingsPanel);        // bottom-left
        grid.add(spendingPanel);       // bottom-right

        return grid;
    }

    // ----- actions ----- //
    @Override
    protected void onAdd()
    {
        // optional "add item" behaviour
        JOptionPane.showMessageDialog(this, "add budget item clicked (not yet implemented).");
    }

    // ----- external refresh ----- //
    public void refreshDashboard()
    {
        incomeExpensePanel.refresh();
        budgetVsActualPanel.refresh();
        savingsPanel.refresh();
        spendingPanel.refresh();
    }
}