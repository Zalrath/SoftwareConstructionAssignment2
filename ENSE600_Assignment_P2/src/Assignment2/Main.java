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

        
        // YOU CAN NOT HAVE MULTIPLE INSTANCES OF THE MAIN RUNNING AT ONCE, IT MESSES WITH THE DB CONNECTION 
          
         dataUtil.dropTable(conn,"Purchases");
         dataUtil.dropTable(conn,"ITEMS");
         dataUtil.dropTable(conn,"settings");
         dataUtil.dropTable(conn,"Transactions");
         dataUtil.dropTable(conn, "budget");
         
        dataUtil.createTables(conn);
        
         dataUtil.insertDefaultPurchases(conn);
         dataUtil.insertDefaultItems(conn);
         dataUtil.insertDefaultSettings(conn);  
         dataUtil.insertDefaultSettings(conn);
         dataUtil.insertDefaultBudget(conn);
         
        //dataUtil.printTableColumns(conn);
        // Really fraigle ------
        
        //dataUtil.printItemsFromDB(conn);
        
        
        
        budget.loadBudgetsFromDB();
      
        
        
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
        
        
        //dataUtil.printItemsFromDB(conn);
        //dataUtil.printPurchasesFromDB(conn);
        
        
        
         // closing the connection
//         try {
//            conn.close();
//            System.out.println("Connection closed");
//        } catch (SQLException e) {}
        
        ////////////////////////////////////
        
        
        
        
        // this all needs to move out of here ngl and into the settings tab but i just havent done it yet
        
        
        // Theme accent colours -> we should probably pick some different ones 
        Color[] accentColors = 
        {
            Color.decode("#104A63"), // Deep Teal Blue
            Color.decode("#253D27"), // Dark Forest Green
            Color.decode("#3C4072"), // Indigo Blue / Slate Purple
            Color.decode("#3D8479"), // Muted Aqua Green
            Color.decode("#477D51"), // Medium Leaf Green
            Color.decode("#48375D"), // Dark Violet Gray
            Color.decode("#716994"), // Dusty Lavender
            Color.decode("#8E3E3E"), // Brick Red
            Color.decode("#8E4D6C"), // Mauve Rose
            Color.decode("#AE5E41") // Burnt Copper / Terracotta
        };
        
        
        // ----- Apply Theme ----- // 
       
        
        
        
        SettingsManager.loadFromDatabase();
        System.out.println(SettingsManager.getDateFormatDB());
        
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
                
                
                
                //manager.savePurchasesToDB(conn);
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