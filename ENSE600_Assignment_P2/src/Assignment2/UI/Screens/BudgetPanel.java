/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
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

/**
 * BudgetPanel
 * Modular dashboard for income, expenses, savings, and spending.
 * 
 * Layout:
 * ┌──────────────────────────┬──────────────────────────┐
 * │ Income & Expenses        │ Budget vs Actual         │
 * ├──────────────────────────┼──────────────────────────┤
 * │ Savings                  │ Spending by Category     │
 * └──────────────────────────┴──────────────────────────┘
 */
public class BudgetPanel extends BaseScreenPanel {

    private final InventoryManager manager;

    // Sub-panels
    private IncomeExpensePanel incomeExpensePanel;
    private BudgetVsActualPanel budgetVsActualPanel;
    private SavingsPanel savingsPanel;
    private SpendingPanel spendingPanel;

    public BudgetPanel(InventoryManager manager) {
        super("Budget", true, true, "Add Item", "dashboard");
        this.manager = manager;
        buildBaseUI();
    }

    @Override
    protected JComponent createCentre() {
        Theme.Palette p = Theme.palette();

        JPanel grid = new JPanel(new GridLayout(2, 2, 10, 10));
        grid.setOpaque(true);
        grid.setBackground(p.tileDark);
        grid.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        incomeExpensePanel = new IncomeExpensePanel(manager, p);
        budgetVsActualPanel = new BudgetVsActualPanel(manager, p);
        savingsPanel = new SavingsPanel(manager, p);
        spendingPanel = new SpendingPanel(manager, p);

        grid.add(incomeExpensePanel);
        grid.add(budgetVsActualPanel);
        grid.add(savingsPanel);
        grid.add(spendingPanel);

        return grid;
    }

    @Override
    protected void onAdd() {
        // Optional "Add Item" behaviour — could open AddIncomeDialog or AddExpenseDialog
        JOptionPane.showMessageDialog(this, "Add Budget Item clicked (not yet implemented).");
    }

    public void refreshDashboard() {
        incomeExpensePanel.refresh();
        budgetVsActualPanel.refresh();
        savingsPanel.refresh();
        spendingPanel.refresh();
    }
}
