/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Assignment2.Database;


import Assignment2.Inventory.Transaction;
import java.sql.*;
import java.util.*;




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
    public void saveBudgetsToDB() {
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

        try (PreparedStatement ps = DatabaseUtil.getConnection().prepareStatement(sql)) {
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
    public void loadBudgetsFromDB() {
        String sql = "SELECT * FROM Budget";

        try (Statement stmt = DatabaseUtil.getConnection().createStatement();
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
    // GETS AND SETS
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
    

    private final HashMap<UUID, Transaction> transactions = new HashMap<>();

    // Load all transactions from DB into HashMap
    public void loadTransactions() {
        String sql = "SELECT * FROM Transactions";

        try (Statement stmt = DatabaseUtil.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            transactions.clear(); // clear in-memory cache before reloading

            while (rs.next()) {
                UUID id = UUID.fromString(rs.getString("uuid"));
                String type = rs.getString("type");
                String title = rs.getString("title");
                String tag = rs.getString("tag");
                double amount = rs.getDouble("amount");
                String frequency = rs.getString("frequency");
                String date = rs.getDate("date").toString();

                Transaction t = new Transaction(id, type, title, tag, amount, frequency, date);
                transactions.put(id, t);
            }
            System.out.println("Transactions loaded: " + transactions.size());

        } catch (SQLException e) {
            System.err.println("Error loading transactions: " + e.getMessage());
        }
    }
    

    // Save a single transaction to the DB and map
    public void addTransaction( Transaction t) {
        String sql = """
            INSERT INTO Transactions (uuid, type, title, tag, amount, frequency, date)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = DatabaseUtil.getConnection().prepareStatement(sql)) {
            ps.setString(1, t.getId().toString());
            ps.setString(2, t.getType());
            ps.setString(3, t.getTitle());
            ps.setString(4, t.getTag());
            ps.setDouble(5, t.getAmount());
            ps.setString(6, t.getFrequency());
            ps.setDate(7, java.sql.Date.valueOf(t.getDate()));

            ps.executeUpdate();
            transactions.put(t.getId(), t);

            System.out.println("Transaction added: " + t.getTitle());

        } catch (SQLException e) {
            System.err.println("Error adding transaction: " + e.getMessage());
        }
    }

    // Update existing transaction
    public void updateTransaction(Transaction t) {
        String sql = """
            UPDATE Transactions 
            SET type = ?, title = ?, tag = ?, amount = ?, frequency = ?, date = ?
            WHERE id = ?
        """;

        try (PreparedStatement ps = DatabaseUtil.getConnection().prepareStatement(sql)) {
            ps.setString(1, t.getType());
            ps.setString(2, t.getTitle());
            ps.setString(3, t.getTag());
            ps.setDouble(4, t.getAmount());
            ps.setString(5, t.getFrequency());
            ps.setDate(6, java.sql.Date.valueOf(t.getDate()));
            ps.setString(7, t.getId().toString());

            int rows = ps.executeUpdate();

            if (rows > 0) {
                transactions.put(t.getId(), t);
                System.out.println("Transaction updated: " + t.getTitle());
            } else {
                System.out.println("No transaction found for update: " + t.getId());
            }

        } catch (SQLException e) {
            System.err.println("Error updating transaction: " + e.getMessage());
        }
    }

    // Delete a transaction
    public void deleteTransaction(UUID id) {
        String sql = "DELETE FROM Transactions WHERE id = ?";

        try (PreparedStatement ps = DatabaseUtil.getConnection().prepareStatement(sql)) {
            ps.setString(1, id.toString());
            int rows = ps.executeUpdate();

            if (rows > 0) {
                transactions.remove(id);
                System.out.println("Transaction deleted: " + id);
            } else {
                System.out.println("No transaction found with ID: " + id);
            }

        } catch (SQLException e) {
            System.err.println("Error deleting transaction: " + e.getMessage());
        }
    }

    // Clear all transactions (DB + HashMap)
    public void clearAllTransactions(Connection conn) {
        String sql = "DELETE FROM Transactions";

        try (Statement stmt = conn.createStatement()) {
            int count = stmt.executeUpdate(sql);
            transactions.clear();
            System.out.println("All transactions cleared (" + count + " deleted).");

        } catch (SQLException e) {
            System.err.println("Error clearing transactions: " + e.getMessage());
        }
    }

   
    public HashMap<UUID, Transaction> getTransactions() {
        return transactions;
    }
    
    public double getTotalByType(String type) {
        String sql = "SELECT SUM(amount) FROM Transactions WHERE type = ?";
        try (PreparedStatement ps = DatabaseUtil.getConnection().prepareStatement(sql)) {
            ps.setString(1, type);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
}

 