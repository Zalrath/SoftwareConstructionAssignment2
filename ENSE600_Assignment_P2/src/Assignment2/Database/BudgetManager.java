/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Assignment2.Database;

import java.sql.*;







/**
 *
 * @author corin
 */
public class BudgetManager {
    
    



    private double weeklyBudget;
    private double monthlyBudget;
    private double yearlyBudget;
    private double allTimeBudget;
    private double savings;
    private double income;
    private double expenses;
    private double budget;
    private double actual;

    // ------------------------------------
    // Save Budgets to DB
    // ------------------------------------
    public void saveBudgetsToDB(Connection conn) {
        String sql = """
            INSERT INTO Budget (
                weekly_budget,
                monthly_budget,
                yearly_budget,
                all_time_budget,
                savings,
                income,
                expenses,
                budget,
                actual
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
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
            System.out.println("Budgets saved to DB");

        } catch (SQLException e) {
            System.out.println("? Error saving Budgets: " + e.getMessage());
        }
    }

    // ------------------------------------
    // Load Budgets from DB
    // ------------------------------------
    public void loadBudgetsFromDB(Connection conn) {
        String sql = "SELECT * FROM Budget";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

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
            }

            System.out.println("Budgets loaded from DB");

        } catch (SQLException e) {
            System.out.println("? Error loading Budgets: " + e.getMessage());
        }
        
            


    
    }

    // ------------------------------------
    // Accessors
    // ------------------------------------
    public double getWeeklyBudget() { return weeklyBudget; }
    public void setWeeklyBudget(double v) { weeklyBudget = v; }

    public double getMonthlyBudget() { return monthlyBudget; }
    public void setMonthlyBudget(double v) { monthlyBudget = v; }

    public double getYearlyBudget() { return yearlyBudget; }
    public void setYearlyBudget(double v) { yearlyBudget = v; }

    public double getAllTimeBudget() { return allTimeBudget; }
    public void setAllTimeBudget(double v) { allTimeBudget = v; }

    public double getSavings() { return savings; }
    public void setSavings(double v) { savings = v; }

    public double getIncome() { return income; }
    public void setIncome(double v) { income = v; }

    public double getExpenses() { return expenses; }
    public void setExpenses(double v) { expenses = v; }

    public double getBudget() { return budget; }
    public void setBudget(double v) { budget = v; }

    public double getActual() { return actual; }
    public void setActual(double v) { actual = v; }
}
        // i need this for the panels
          /*
        LocalDate now = LocalDate.now();

        // Example time ranges
        LocalDate startOfWeek = now.with(DayOfWeek.MONDAY);

        LocalDate startOfMonth = now.withDayOfMonth(1);

        LocalDate beginningOfTime = LocalDate.of(2000, 1, 1);


        // Spending
        double weeklySpending = manager.getTotalSpendingForPeriod(startOfWeek, now);

        double monthlySpending = manager.getTotalSpendingForPeriod(startOfMonth, now);

        double yearlySpending = manager.getTotalSpendingForPeriod(startOfYear, now);

        double allTimeSpending = manager.getTotalSpendingForPeriod(beginningOfTime, now);


        // Budgets (you can later pull these from settings or a BudgetManager)
        double weeklyBudget = ;
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
        