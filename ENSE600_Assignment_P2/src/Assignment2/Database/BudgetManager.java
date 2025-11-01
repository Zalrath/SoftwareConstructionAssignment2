/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Assignment2.Database;
import java.sql.*;
import com.sun.jdi.connect.spi.Connection;

/**
 *
 * @author corin
 */
public class BudgetManager {
    
          /*
        LocalDate now = LocalDate.now();

        // Example time ranges
        LocalDate startOfWeek = now.with(DayOfWeek.MONDAY);
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate startOfYear = now.withDayOfYear(1);
        LocalDate beginningOfTime = LocalDate.of(2000, 1, 1);

        // Spending
        double weeklySpending = manager.getTotalSpendingForPeriod(startOfWeek, now);
        double monthlySpending = manager.getTotalSpendingForPeriod(startOfMonth, now);
        double yearlySpending = manager.getTotalSpendingForPeriod(startOfYear, now);
        double allTimeSpending = manager.getTotalSpendingForPeriod(beginningOfTime, now);

        // Budgets (you can later pull these from settings or a BudgetManager)
        double weeklyBudget = 150.0;
        double monthlyBudget = 600.0;
        double yearlyBudget = 7500.0;
        double allTimeBudget = 10000.0;

        // Update progress bars
        weeklyPanel.setMaxDollarValue(weeklyBudget);
        weeklyPanel.updateValue((weeklySpending / weeklyBudget) * 100);

        monthlyPanel.setMaxDollarValue(monthlyBudget);
        monthlyPanel.updateValue((monthlySpending / monthlyBudget) * 100);

        yearlylPanel.setMaxDollarValue(yearlyBudget);
        yearlylPanel.updateValue((yearlySpending / yearlyBudget) * 100);

        alltimePanel.setMaxDollarValue(allTimeBudget);
        alltimePanel.updateValue((allTimeSpending / allTimeBudget) * 100);
    */
        
       private double weeklyBudget;
    private double monthlyBudget;
    private double yearlyBudget;
    private double allTimeBudget;
    private double savings;
    private double income;
    private double expenses;
    private double budget;
    private double actual;

    public BudgetManager() {
        loadBudgets();
    }

    // --- Load from DB ---
    public void loadBudgets(Connection conn) {
        try (Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery("SELECT * FROM Budget LIMIT 1");

            if (rs.next()) {
                weeklyBudget = rs.getDouble("weekly_budget");
                monthlyBudget = rs.getDouble("monthly_budget");
                yearlyBudget = rs.getDouble("yearly_budget");
                allTimeBudget = rs.getDouble("all_time_budget");
                savings = rs.getDouble("savings");
                income = rs.getDouble("income");
                expenses = rs.getDouble("expenses");
                budget = rs.getDouble("budget");
                actual = rs.getDouble("actual");
            } else {
                System.out.println("No budget record found, inserting default...");
                insertDefaultBudgets(conn);
            }

        } catch (SQLException e) {
            System.err.println("Error loading budgets: " + e.getMessage());
        }
    }

    // --- Insert a default row if table is empty ---
    private void insertDefaultBudgets(Connection conn) throws SQLException {
        String sql = """
            INSERT INTO Budget (weekly_budget, monthly_budget, yearly_budget, all_time_budget,
                                savings, income, expenses, budget, actual)
            VALUES (150, 600, 7500, 10000, 0, 0, 0, 0, 0)
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        }
    }

    // --- Save all current values ---
    public void saveBudgets() {
        String sql = """
            UPDATE Budget SET
                weekly_budget = ?,
                monthly_budget = ?,
                yearly_budget = ?,
                all_time_budget = ?,
                savings = ?,
                income = ?,
                expenses = ?,
                budget = ?,
                actual = ?
        """;

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, weeklyBudget);
            ps.setDouble(2, monthlyBudget);
            ps.setDouble(3, yearlyBudget);
            ps.setDouble(4, allTimeBudget);
            ps.setDouble(5, savings);
            ps.setDouble(6, income);
            ps.setDouble(7, expenses);
            ps.setDouble(8, budget);
            ps.setDouble(9, actual);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error saving budgets: " + e.getMessage());
        }
    }

    // --- Individual Getters/Setters ---
    public double getWeeklyBudget() { return weeklyBudget; }
    public void setWeeklyBudget(double v) { weeklyBudget = v; saveBudgets(); }

    public double getMonthlyBudget() { return monthlyBudget; }
    public void setMonthlyBudget(double v) { monthlyBudget = v; saveBudgets(); }

    public double getYearlyBudget() { return yearlyBudget; }
    public void setYearlyBudget(double v) { yearlyBudget = v; saveBudgets(); }

    public double getAllTimeBudget() { return allTimeBudget; }
    public void setAllTimeBudget(double v) { allTimeBudget = v; saveBudgets(); }

    public double getSavings() { return savings; }
    public void setSavings(double v) { savings = v; saveBudgets(); }

    public double getIncome() { return income; }
    public void setIncome(double v) { income = v; saveBudgets(); }

    public double getExpenses() { return expenses; }
    public void setExpenses(double v) { expenses = v; saveBudgets(); }

    public double getBudget() { return budget; }
    public void setBudget(double v) { budget = v; saveBudgets(); }

    public double getActual() { return actual; }
    public void setActual(double v) { actual = v; saveBudgets(); }

            
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
