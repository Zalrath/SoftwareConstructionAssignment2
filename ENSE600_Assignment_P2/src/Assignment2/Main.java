/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Assignment2;

/**
 *
 * @author megan
 */


import Assignment2.Account.UserAuthenticator;

import Assignment2.Database.BudgetManager;

import Assignment2.UI.HomeScreen;

import Assignment2.Database.DatabaseUtil;

import Assignment2.Inventory.InventoryManager;
import Assignment2.Inventory.Item;
import Assignment2.Inventory.SettingsManager;
import Assignment2.UI.Theme;

import java.awt.Color;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.SwingUtilities;

import java.sql.*;

public class Main 
{
    public static void main(String[] args) throws Exception 
    {
        
        
        ////////////////////////////////////////////////
        // Connecting to the database
            Connection conn = DatabaseUtil.connectToDatabase();
            if (conn == null) {
                System.out.println("Failed to connect to DB");
                return;
            }
        ////////////////////////////////////////////////
        
        
        DatabaseUtil dataUtil = new DatabaseUtil();
        InventoryManager manager = new InventoryManager();
        SettingsManager settings = new SettingsManager();
        BudgetManager budget = new BudgetManager();

        
        
          
         dataUtil.dropTable(conn,"Purchases");
         dataUtil.dropTable(conn,"ITEMS");
         dataUtil.dropTable(conn,"settings");
         dataUtil.dropTable(conn,"Transactions");
         dataUtil.dropTable(conn, "budget");
         
         dataUtil.createTables(conn);
        
         dataUtil.insertDefaultPurchases(conn);
         dataUtil.insertDefaultItems(conn);
         dataUtil.insertDefaultSettings(conn);  
         dataUtil.insertDefaultTransactions(conn);
         dataUtil.insertDefaultBudget(conn);
                            
        budget.loadBudgetsFromDB();
        budget.loadTransactions();
        
        
        manager.loadItemsFromDB(conn);
        
        try (Statement stmt = conn.createStatement()) 
        {
            stmt.executeUpdate("DELETE FROM Items");
            System.out.println(" Items table cleared.");
        } catch (SQLException e) {}
        
        
        //-------------------------------
        
        
        manager.loadPurchasesFromDB(conn);
        
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM Purchases");
            System.out.println(" Purchases table cleared.");
        } catch (SQLException e) {}
        
        manager.savePurchasesToDB(conn);
        
        ////////////////////////////////////

        
        // ----- Apply Theme ----- // 

        SettingsManager.loadFromDatabase();

        Color dbselected = SettingsManager.getAccentColor();
        

        Theme.setAccent(dbselected);        // updates all components
        
        // ----- Launch GUI ----- // 

        UserAuthenticator auth = new UserAuthenticator(conn);
        SwingUtilities.invokeLater(() -> new HomeScreen(manager, settings, auth ,budget).setVisible(true));


        /////////////////////////////////////////////////////////////////////////////////
        // testing // 
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> 
        {
            try 
            {
                

                
                manager.saveItemsToDB(conn);

                conn.close();
                System.out.println("db disconnected");
                
            }
            catch (Exception e) 
            {
                e.printStackTrace();
            }
        }));
    }
}