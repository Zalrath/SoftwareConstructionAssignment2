package Project_p2;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author corin
 */

import java.sql.*;
import java.time.LocalDate;

public class DatabaseUtil {
    
       
    public static Connection connectToDatabase() throws ClassNotFoundException {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            String url = "jdbc:derby:InventoryDB;create=true";
            Connection conn = DriverManager.getConnection(url);
            System.out.println("Connected to Embedded DB");
            return conn;
        } catch (SQLException e) {
            return null;
        }
    }
 
    public static void disconnectFromDatabase(Connection conn) {
    try {
        if (conn != null && !conn.isClosed()) {
            conn.close();
            System.out.println("Disconnected from Embedded DB");
        } else {
            System.out.println("No active database connection to close.");
        }
    } catch (SQLException e) {
    }
}


    // Create Tables
    public void createTables(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("""
                CREATE TABLE Items (
                    uuid VARCHAR(36) PRIMARY KEY,
                    name VARCHAR(255),
                    last_Purchased DATE,
                    Interval_Days INT,
                    tags VARCHAR(1000)
                )
            """);

            stmt.executeUpdate("""
                CREATE TABLE Purchases (
                    itemUUID VARCHAR(36),
                    price DOUBLE,
                    quantity DOUBLE,
                    purchaseDate DATE
                )
            """);

            System.out.println("Tables created");
        } catch (SQLException e) {
            if (!"X0Y32".equals(e.getSQLState())) {} // Ignores the "table already exists" error
        }
    }
    
    
    
    // View whats in the Items Table
    public void printItemsFromDB(Connection conn) {
        String sql = "SELECT * FROM Items";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("=== Items in Database ===");
            while (rs.next()) {
                String uuid = rs.getString("uuid");
                String name = rs.getString("name");
                LocalDate lastPurchased = rs.getDate("last_Purchased").toLocalDate();
                int interval = rs.getInt("Interval_Days");
                String tags = rs.getString("tags");

                System.out.printf("UUID: %s | Name: %s | Last Purchased: %s | Interval: %d | Tags: %s%n",
                        uuid, name, lastPurchased, interval, tags);
            }

        } catch (SQLException e) { }
    }
    
    // View whats in the Purchase Table
    public void printPurchasesFromDB(Connection conn) {
        String sql = "SELECT * FROM Purchases";
        System.out.println("ahauhdsanjdfklj");
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("=== Purchases in Database ===");
            while (rs.next()) {
                String uuid = rs.getString("itemUUID");
                String price = rs.getString("price");
                String quantity = rs.getString("quantity");
                LocalDate purchaseDate = rs.getDate("purchaseDate").toLocalDate();
                

                System.out.printf("UUID: %s | Price: %s | Quantity %s | Purchase Date: %s%n",
                        uuid, price, quantity, purchaseDate);
            }

        } catch (SQLException e) { }
    }
   
    
    
    
    
    
    // Delete the given table
    public void dropTable(Connection conn, String tableName) {
        String sql = "DROP TABLE " + tableName;
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("Table '" + tableName + "' dropped successfully.");
        } catch (SQLException e) {
            if ("42Y55".equals(e.getSQLState())) {
                System.out.println("Table '" + tableName + "' does not exist.");
            } else { }
        }
    }
    
    
    // Print the Columns of all tables in the DB
    public void printTableColumns(Connection conn) {
        try {
            DatabaseMetaData metaData = conn.getMetaData();
            try (ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"})) {
                while (tables.next()) {
                    String tableName = tables.getString("TABLE_NAME");
                    System.out.println("\n=== Columns in " + tableName + " ===");
                    
                    try (ResultSet columns = metaData.getColumns(null, null, tableName, null)) {
                        while (columns.next()) {
                            String columnName = columns.getString("COLUMN_NAME");
                            String typeName = columns.getString("TYPE_NAME");
                            int size = columns.getInt("COLUMN_SIZE");
                            
                            System.out.printf(" %s (%s, size: %d)%n", columnName, typeName, size);
                        }
                    }
                }
            }
        } catch (SQLException e) { }
    }
    
    
}
