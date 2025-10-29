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
                    current_Amount DOUBLE,           
                    Interval_Days INT,
                    tags VARCHAR(1000),
                    favorite BOOLEAN,  
                    future BOOLEAN
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
                double currentAmount = rs.getDouble("current_Amount");
                int interval = rs.getInt("Interval_Days");
                String tags = rs.getString("tags");
                boolean favorite = rs.getBoolean("favorite");
                boolean future = rs.getBoolean("future");

                System.out.printf("UUID: %s | Name: %s | Last Purchased: %s |Current Amount %s| Interval: %d | Tags: %s | Favorite: %s| Future %s %n",
                        uuid, name, lastPurchased,currentAmount, interval, tags, favorite, future);
            }

        } catch (SQLException e) { }
    }
    
    // View whats in the Purchase Table
    public void printPurchasesFromDB(Connection conn) {
        String sql = "SELECT * FROM Purchases";
       
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
    
    
        public void insertDefaultItems(Connection conn) {
        String sql = "INSERT INTO Items (uuid, name, last_Purchased, current_Amount , interval_Days, tags, favorite, future) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            UNCaddItem(ps, "550e8400-e29b-41d4-a716-446655440000", "Milk", LocalDate.of(2025, 10, 20), 7.0,0, "dairy|fridge|weekly",false, false);
            UNCaddItem(ps, "660e8400-e29b-41d4-a716-446655440001", "Bread", LocalDate.of(2025, 10, 18), 3.0,0, "bakery|breakfast|daily" ,false, false);
            UNCaddItem(ps, "770e8400-e29b-41d4-a716-446655440002", "Eggs", LocalDate.of(2025, 10, 15), 10.0,0, "protein|breakfast|fridge",false, false);
            UNCaddItem(ps, "880e8400-e29b-41d4-a716-446655440003", "Apples", LocalDate.of(2025, 10, 12), 14.0,0, "fruit|snack|fresh",false, false);
            UNCaddItem(ps, "990e8400-e29b-41d4-a716-446655440004", "Toilet Paper", LocalDate.of(2025, 9, 30), 30.0,0, "household|bulk|monthly",false, false);

            System.out.println("Default items inserted successfully!");

        } catch (SQLException e) {
            System.err.println("️ Error inserting default items: " + e.getMessage());
        }
    }

    private static void UNCaddItem(PreparedStatement ps, String uuid, String name, LocalDate date, double currentAmount, int interval, String tags, boolean favorite, boolean future) throws SQLException {
        ps.setString(1, uuid);
        ps.setString(2, name);
        ps.setDate(3, java.sql.Date.valueOf(date));
        ps.setDouble(4, currentAmount);
        ps.setInt(5, interval);
        ps.setString(6, tags);
        ps.setBoolean(7, favorite);
        ps.setBoolean(8, future);
        ps.executeUpdate();
    }
    
    public void insertDefaultPurchases(Connection conn) {
        String sql = "INSERT INTO Purchases (itemUUID, price, quantity, purchaseDate) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            
            
            UNCaddPurchases(ps, "550e8400-e29b-41d4-a716-446655440000", 4.0, 2.0, LocalDate.of(2025, 10, 20));
            UNCaddPurchases(ps, "660e8400-e29b-41d4-a716-446655440001", 4.5, 1.0, LocalDate.of(2025, 10, 18));
            UNCaddPurchases(ps, "770e8400-e29b-41d4-a716-446655440002", 10.99, 1.0, LocalDate.of(2025, 10, 15));
            UNCaddPurchases(ps, "880e8400-e29b-41d4-a716-446655440003", 3.0, 1.0, LocalDate.of(2025, 10, 12));
            UNCaddPurchases(ps, "990e8400-e29b-41d4-a716-446655440004", 19.0, 1.0, LocalDate.of(2025, 9, 30));
            
            UNCaddPurchases(ps, "550e8400-e29b-41d4-a716-446655440000", 5.0, 1.0, LocalDate.of(2025, 11, 20));
            UNCaddPurchases(ps, "660e8400-e29b-41d4-a716-446655440001", 7.0, 2.0, LocalDate.of(2025, 11, 18));
            UNCaddPurchases(ps, "770e8400-e29b-41d4-a716-446655440002", 9.0, 2.0, LocalDate.of(2025, 11, 15));
            UNCaddPurchases(ps, "880e8400-e29b-41d4-a716-446655440003", 4.0, 1.0, LocalDate.of(2025, 11, 12));
            UNCaddPurchases(ps, "990e8400-e29b-41d4-a716-446655440004", 22.0, 1.0, LocalDate.of(2025, 10, 30));
            
            
            
            System.out.println("Default Purchases inserted successfully!");

        } catch (SQLException e) {
            System.err.println("️ Error inserting default Purchases: " + e.getMessage());
        }
    }

    private static void UNCaddPurchases(PreparedStatement ps, String uuid, double price , double quantity ,LocalDate date) throws SQLException {
        ps.setString(1, uuid);
        ps.setDouble(2, price);
        ps.setDouble(3, quantity);
        ps.setDate(4, java.sql.Date.valueOf(date));
        ps.executeUpdate();
    }
    
    
    
    
    
}
