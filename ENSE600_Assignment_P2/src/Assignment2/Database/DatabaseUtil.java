package Assignment2.Database;

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
     private static Connection connectionInstance;
       
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
    
   

    public static Connection getConnection() {
        try {
            if (connectionInstance == null || connectionInstance.isClosed()) {
                connectionInstance = connectToDatabase();
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error getting database connection: " + e.getMessage());
        }
        return connectionInstance;
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

            UNCaddItem(ps, "550e8400-e29b-41d4-a716-446655440000", "Milk", LocalDate.of(2025, 10, 20), 7.0, 0, "dairy|fridge|weekly", false, false);
            UNCaddItem(ps, "660e8400-e29b-41d4-a716-446655440001", "Bread", LocalDate.of(2025, 10, 18), 3.0, 0, "bakery|breakfast|daily", false, false);
            UNCaddItem(ps, "770e8400-e29b-41d4-a716-446655440002", "Eggs", LocalDate.of(2025, 10, 15), 10.0, 0, "protein|breakfast|fridge", false, false);
            UNCaddItem(ps, "880e8400-e29b-41d4-a716-446655440003", "Apples", LocalDate.of(2025, 10, 12), 14.0, 0, "fruit|snack|fresh", false, false);
            UNCaddItem(ps, "990e8400-e29b-41d4-a716-446655440004", "Toilet Paper", LocalDate.of(2025, 9, 30), 30.0, 0, "household|bulk|monthly", false, false);

            UNCaddItem(ps, "a10e8400-e29b-41d4-a716-446655440005", "Chicken Breast", LocalDate.of(2025, 10, 22), 5.0, 0, "meat|protein|freezer", false, false);
            UNCaddItem(ps, "a20e8400-e29b-41d4-a716-446655440006", "Cheese", LocalDate.of(2025, 10, 10), 2.0, 0, "dairy|fridge|snack", false, false);
            UNCaddItem(ps, "a30e8400-e29b-41d4-a716-446655440007", "Pasta", LocalDate.of(2025, 9, 25), 4.0, 0, "pantry|dinner|dry", false, false);
            UNCaddItem(ps, "a40e8400-e29b-41d4-a716-446655440008", "Tomato Sauce", LocalDate.of(2025, 9, 26), 3.0, 0, "pantry|sauce|dinner", false, false);
            UNCaddItem(ps, "a50e8400-e29b-41d4-a716-446655440009", "Rice", LocalDate.of(2025, 8, 30), 10.0, 0, "pantry|grain|staple", false, false);

            UNCaddItem(ps, "a60e8400-e29b-41d4-a716-446655440010", "Coffee", LocalDate.of(2025, 10, 5), 1.0, 0, "beverage|morning|pantry", false, false);
            UNCaddItem(ps, "a70e8400-e29b-41d4-a716-446655440011", "Toothpaste", LocalDate.of(2025, 9, 20), 2.0, 0, "personal|bathroom|monthly", false, false);
            UNCaddItem(ps, "a80e8400-e29b-41d4-a716-446655440012", "Laundry Detergent", LocalDate.of(2025, 9, 10), 3.0, 0, "household|cleaning|bulk", false, false);
            UNCaddItem(ps, "a90e8400-e29b-41d4-a716-446655440013", "Shampoo", LocalDate.of(2025, 9, 15), 1.0, 0, "personal|bathroom|monthly", false, false);
            UNCaddItem(ps, "b00e8400-e29b-41d4-a716-446655440014", "Butter", LocalDate.of(2025, 10, 17), 2.0, 0, "dairy|fridge|baking", false, false);

            UNCaddItem(ps, "b10e8400-e29b-41d4-a716-446655440015", "Yogurt", LocalDate.of(2025, 10, 21), 6.0, 0, "dairy|snack|fridge", false, false);
            UNCaddItem(ps, "b20e8400-e29b-41d4-a716-446655440016", "Carrots", LocalDate.of(2025, 10, 8), 12.0, 0, "vegetable|fresh|snack", false, false);
            UNCaddItem(ps, "b30e8400-e29b-41d4-a716-446655440017", "Cereal", LocalDate.of(2025, 10, 14), 2.0, 0, "breakfast|pantry|snack", false, false);
            UNCaddItem(ps, "b40e8400-e29b-41d4-a716-446655440018", "Juice", LocalDate.of(2025, 10, 19), 4.0, 0, "beverage|fridge|breakfast", false, false);
            UNCaddItem(ps, "b50e8400-e29b-41d4-a716-446655440019", "Soap", LocalDate.of(2025, 9, 28), 5.0, 0, "personal|bathroom|cleaning", false, false);

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
            
            /*
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
            */
            
            // Generated purchase history dataset

            // --- Milk ---
            UNCaddPurchases(ps, "550e8400-e29b-41d4-a716-446655440000", 4.50, 1.0, LocalDate.of(2023, 10, 29));
            UNCaddPurchases(ps, "550e8400-e29b-41d4-a716-446655440000", 4.51, 1.0, LocalDate.of(2023, 11, 28));
            UNCaddPurchases(ps, "550e8400-e29b-41d4-a716-446655440000", 4.52, 1.0, LocalDate.of(2024, 1, 27));
            UNCaddPurchases(ps, "550e8400-e29b-41d4-a716-446655440000", 4.53, 1.0, LocalDate.of(2024, 3, 27));
            UNCaddPurchases(ps, "550e8400-e29b-41d4-a716-446655440000", 4.54, 1.0, LocalDate.of(2024, 4, 26));
            UNCaddPurchases(ps, "550e8400-e29b-41d4-a716-446655440000", 4.55, 1.0, LocalDate.of(2024, 5, 3));
            UNCaddPurchases(ps, "550e8400-e29b-41d4-a716-446655440000", 4.56, 1.0, LocalDate.of(2024, 5, 6));
            UNCaddPurchases(ps, "550e8400-e29b-41d4-a716-446655440000", 4.56, 1.0, LocalDate.of(2024, 6, 5));
            UNCaddPurchases(ps, "550e8400-e29b-41d4-a716-446655440000", 4.58, 1.0, LocalDate.of(2024, 6, 12));
            UNCaddPurchases(ps, "550e8400-e29b-41d4-a716-446655440000", 4.59, 1.0, LocalDate.of(2024, 7, 12));
            UNCaddPurchases(ps, "550e8400-e29b-41d4-a716-446655440000", 4.61, 1.0, LocalDate.of(2024, 8, 11));
            UNCaddPurchases(ps, "550e8400-e29b-41d4-a716-446655440000", 4.61, 1.0, LocalDate.of(2024, 8, 18));
            UNCaddPurchases(ps, "550e8400-e29b-41d4-a716-446655440000", 4.62, 1.0, LocalDate.of(2024, 8, 25));
            UNCaddPurchases(ps, "550e8400-e29b-41d4-a716-446655440000", 4.63, 1.0, LocalDate.of(2024, 10, 24));
            UNCaddPurchases(ps, "550e8400-e29b-41d4-a716-446655440000", 4.64, 1.0, LocalDate.of(2024, 10, 27));
            UNCaddPurchases(ps, "550e8400-e29b-41d4-a716-446655440000", 4.65, 1.0, LocalDate.of(2024, 10, 30));
            UNCaddPurchases(ps, "550e8400-e29b-41d4-a716-446655440000", 4.66, 1.0, LocalDate.of(2024, 11, 29));
            UNCaddPurchases(ps, "550e8400-e29b-41d4-a716-446655440000", 4.67, 1.0, LocalDate.of(2024, 12, 29));
            UNCaddPurchases(ps, "550e8400-e29b-41d4-a716-446655440000", 4.68, 1.0, LocalDate.of(2025, 2, 27));
            UNCaddPurchases(ps, "550e8400-e29b-41d4-a716-446655440000", 4.69, 1.0, LocalDate.of(2025, 4, 28));
            UNCaddPurchases(ps, "550e8400-e29b-41d4-a716-446655440000", 4.69, 1.0, LocalDate.of(2025, 5, 1));
            UNCaddPurchases(ps, "550e8400-e29b-41d4-a716-446655440000", 4.70, 1.0, LocalDate.of(2025, 5, 31));
            UNCaddPurchases(ps, "550e8400-e29b-41d4-a716-446655440000", 4.72, 1.0, LocalDate.of(2025, 6, 7));
            UNCaddPurchases(ps, "550e8400-e29b-41d4-a716-446655440000", 4.73, 1.0, LocalDate.of(2025, 7, 7));
            UNCaddPurchases(ps, "550e8400-e29b-41d4-a716-446655440000", 4.74, 1.0, LocalDate.of(2025, 9, 5));
            UNCaddPurchases(ps, "550e8400-e29b-41d4-a716-446655440000", 4.75, 1.0, LocalDate.of(2025, 10, 5));

            // --- Bread ---
            UNCaddPurchases(ps, "660e8400-e29b-41d4-a716-446655440001", 3.00, 1.0, LocalDate.of(2023, 10, 29));
            UNCaddPurchases(ps, "660e8400-e29b-41d4-a716-446655440001", 3.01, 1.0, LocalDate.of(2023, 12, 28));
            UNCaddPurchases(ps, "660e8400-e29b-41d4-a716-446655440001", 3.02, 1.0, LocalDate.of(2024, 1, 4));
            UNCaddPurchases(ps, "660e8400-e29b-41d4-a716-446655440001", 3.03, 1.0, LocalDate.of(2024, 2, 3));
            UNCaddPurchases(ps, "660e8400-e29b-41d4-a716-446655440001", 3.04, 1.0, LocalDate.of(2024, 2, 10));
            UNCaddPurchases(ps, "660e8400-e29b-41d4-a716-446655440001", 3.05, 1.0, LocalDate.of(2024, 2, 13));
            UNCaddPurchases(ps, "660e8400-e29b-41d4-a716-446655440001", 3.06, 1.0, LocalDate.of(2024, 3, 14));
            UNCaddPurchases(ps, "660e8400-e29b-41d4-a716-446655440001", 3.06, 1.0, LocalDate.of(2024, 3, 17));
            UNCaddPurchases(ps, "660e8400-e29b-41d4-a716-446655440001", 3.07, 1.0, LocalDate.of(2024, 3, 20));
            UNCaddPurchases(ps, "660e8400-e29b-41d4-a716-446655440001", 3.07, 1.0, LocalDate.of(2024, 5, 19));
            UNCaddPurchases(ps, "660e8400-e29b-41d4-a716-446655440001", 3.08, 1.0, LocalDate.of(2024, 5, 26));
            UNCaddPurchases(ps, "660e8400-e29b-41d4-a716-446655440001", 3.09, 1.0, LocalDate.of(2024, 5, 29));
            UNCaddPurchases(ps, "660e8400-e29b-41d4-a716-446655440001", 3.10, 1.0, LocalDate.of(2024, 6, 28));
            UNCaddPurchases(ps, "660e8400-e29b-41d4-a716-446655440001", 3.11, 1.0, LocalDate.of(2024, 8, 27));
            UNCaddPurchases(ps, "660e8400-e29b-41d4-a716-446655440001", 3.12, 1.0, LocalDate.of(2024, 9, 26));
            UNCaddPurchases(ps, "660e8400-e29b-41d4-a716-446655440001", 3.12, 1.0, LocalDate.of(2024, 11, 25));
            UNCaddPurchases(ps, "660e8400-e29b-41d4-a716-446655440001", 3.13, 1.0, LocalDate.of(2025, 1, 24));
            UNCaddPurchases(ps, "660e8400-e29b-41d4-a716-446655440001", 3.14, 1.0, LocalDate.of(2025, 2, 23));
            UNCaddPurchases(ps, "660e8400-e29b-41d4-a716-446655440001", 3.14, 1.0, LocalDate.of(2025, 3, 25));
            UNCaddPurchases(ps, "660e8400-e29b-41d4-a716-446655440001", 3.15, 1.0, LocalDate.of(2025, 3, 28));
            UNCaddPurchases(ps, "660e8400-e29b-41d4-a716-446655440001", 3.16, 1.0, LocalDate.of(2025, 3, 31));
            UNCaddPurchases(ps, "660e8400-e29b-41d4-a716-446655440001", 3.17, 1.0, LocalDate.of(2025, 5, 30));
            UNCaddPurchases(ps, "660e8400-e29b-41d4-a716-446655440001", 3.17, 1.0, LocalDate.of(2025, 6, 29));
            UNCaddPurchases(ps, "660e8400-e29b-41d4-a716-446655440001", 3.18, 1.0, LocalDate.of(2025, 7, 6));
            UNCaddPurchases(ps, "660e8400-e29b-41d4-a716-446655440001", 3.19, 1.0, LocalDate.of(2025, 9, 4));
            UNCaddPurchases(ps, "660e8400-e29b-41d4-a716-446655440001", 3.19, 1.0, LocalDate.of(2025, 9, 7));
            UNCaddPurchases(ps, "660e8400-e29b-41d4-a716-446655440001", 3.21, 1.0, LocalDate.of(2025, 9, 10));
            UNCaddPurchases(ps, "660e8400-e29b-41d4-a716-446655440001", 3.22, 1.0, LocalDate.of(2025, 9, 13));
            UNCaddPurchases(ps, "660e8400-e29b-41d4-a716-446655440001", 3.23, 1.0, LocalDate.of(2025, 9, 16));

            // --- Eggs ---
            UNCaddPurchases(ps, "770e8400-e29b-41d4-a716-446655440002", 7.50, 1.0, LocalDate.of(2023, 10, 29));
            UNCaddPurchases(ps, "770e8400-e29b-41d4-a716-446655440002", 7.52, 1.0, LocalDate.of(2023, 12, 28));
            UNCaddPurchases(ps, "770e8400-e29b-41d4-a716-446655440002", 7.53, 1.0, LocalDate.of(2024, 1, 27));
            UNCaddPurchases(ps, "770e8400-e29b-41d4-a716-446655440002", 7.56, 1.0, LocalDate.of(2024, 3, 27));
            UNCaddPurchases(ps, "770e8400-e29b-41d4-a716-446655440002", 7.58, 1.0, LocalDate.of(2024, 4, 3));
            UNCaddPurchases(ps, "770e8400-e29b-41d4-a716-446655440002", 7.60, 1.0, LocalDate.of(2024, 4, 6));
            UNCaddPurchases(ps, "770e8400-e29b-41d4-a716-446655440002", 7.61, 1.0, LocalDate.of(2024, 5, 6));
            UNCaddPurchases(ps, "770e8400-e29b-41d4-a716-446655440002", 7.63, 1.0, LocalDate.of(2024, 7, 5));
            UNCaddPurchases(ps, "770e8400-e29b-41d4-a716-446655440002", 7.65, 1.0, LocalDate.of(2024, 7, 12));
            UNCaddPurchases(ps, "770e8400-e29b-41d4-a716-446655440002", 7.67, 1.0, LocalDate.of(2024, 7, 19));
            UNCaddPurchases(ps, "770e8400-e29b-41d4-a716-446655440002", 7.70, 1.0, LocalDate.of(2024, 7, 26));
            UNCaddPurchases(ps, "770e8400-e29b-41d4-a716-446655440002", 7.73, 1.0, LocalDate.of(2024, 8, 2));
            UNCaddPurchases(ps, "770e8400-e29b-41d4-a716-446655440002", 7.76, 1.0, LocalDate.of(2024, 10, 1));
            UNCaddPurchases(ps, "770e8400-e29b-41d4-a716-446655440002", 7.77, 1.0, LocalDate.of(2024, 10, 31));
            UNCaddPurchases(ps, "770e8400-e29b-41d4-a716-446655440002", 7.80, 1.0, LocalDate.of(2024, 11, 7));
            UNCaddPurchases(ps, "770e8400-e29b-41d4-a716-446655440002", 7.82, 1.0, LocalDate.of(2024, 12, 7));
            UNCaddPurchases(ps, "770e8400-e29b-41d4-a716-446655440002", 7.83, 1.0, LocalDate.of(2025, 2, 5));
            UNCaddPurchases(ps, "770e8400-e29b-41d4-a716-446655440002", 7.87, 1.0, LocalDate.of(2025, 3, 7));
            UNCaddPurchases(ps, "770e8400-e29b-41d4-a716-446655440002", 7.90, 1.0, LocalDate.of(2025, 3, 10));
            UNCaddPurchases(ps, "770e8400-e29b-41d4-a716-446655440002", 7.92, 1.0, LocalDate.of(2025, 5, 9));
            UNCaddPurchases(ps, "770e8400-e29b-41d4-a716-446655440002", 7.93, 1.0, LocalDate.of(2025, 6, 8));
            UNCaddPurchases(ps, "770e8400-e29b-41d4-a716-446655440002", 7.95, 1.0, LocalDate.of(2025, 8, 7));
            UNCaddPurchases(ps, "770e8400-e29b-41d4-a716-446655440002", 7.96, 1.0, LocalDate.of(2025, 8, 14));
            UNCaddPurchases(ps, "770e8400-e29b-41d4-a716-446655440002", 7.99, 1.0, LocalDate.of(2025, 9, 13));

            // --- Apples ---
            UNCaddPurchases(ps, "880e8400-e29b-41d4-a716-446655440003", 4.00, 1.0, LocalDate.of(2023, 10, 29));
            UNCaddPurchases(ps, "880e8400-e29b-41d4-a716-446655440003", 4.01, 1.0, LocalDate.of(2023, 12, 28));
            UNCaddPurchases(ps, "880e8400-e29b-41d4-a716-446655440003", 4.02, 1.0, LocalDate.of(2024, 1, 4));
            UNCaddPurchases(ps, "880e8400-e29b-41d4-a716-446655440003", 4.03, 1.0, LocalDate.of(2024, 1, 11));
            UNCaddPurchases(ps, "880e8400-e29b-41d4-a716-446655440003", 4.04, 1.0, LocalDate.of(2024, 1, 14));
            UNCaddPurchases(ps, "880e8400-e29b-41d4-a716-446655440003", 4.04, 1.0, LocalDate.of(2024, 3, 14));
            UNCaddPurchases(ps, "880e8400-e29b-41d4-a716-446655440003", 4.06, 1.0, LocalDate.of(2024, 3, 21));
            UNCaddPurchases(ps, "880e8400-e29b-41d4-a716-446655440003", 4.07, 1.0, LocalDate.of(2024, 4, 20));
            UNCaddPurchases(ps, "880e8400-e29b-41d4-a716-446655440003", 4.08, 1.0, LocalDate.of(2024, 4, 23));
            UNCaddPurchases(ps, "880e8400-e29b-41d4-a716-446655440003", 4.08, 1.0, LocalDate.of(2024, 6, 22));
            UNCaddPurchases(ps, "880e8400-e29b-41d4-a716-446655440003", 4.08, 1.0, LocalDate.of(2024, 8, 21));
            UNCaddPurchases(ps, "880e8400-e29b-41d4-a716-446655440003", 4.09, 1.0, LocalDate.of(2024, 8, 24));
            UNCaddPurchases(ps, "880e8400-e29b-41d4-a716-446655440003", 4.11, 1.0, LocalDate.of(2024, 9, 23));
            UNCaddPurchases(ps, "880e8400-e29b-41d4-a716-446655440003", 4.11, 1.0, LocalDate.of(2024, 11, 22));
            UNCaddPurchases(ps, "880e8400-e29b-41d4-a716-446655440003", 4.13, 1.0, LocalDate.of(2024, 11, 29));
            UNCaddPurchases(ps, "880e8400-e29b-41d4-a716-446655440003", 4.14, 1.0, LocalDate.of(2024, 12, 6));
            UNCaddPurchases(ps, "880e8400-e29b-41d4-a716-446655440003", 4.16, 1.0, LocalDate.of(2024, 12, 13));
            UNCaddPurchases(ps, "880e8400-e29b-41d4-a716-446655440003", 4.16, 1.0, LocalDate.of(2024, 12, 16));
            UNCaddPurchases(ps, "880e8400-e29b-41d4-a716-446655440003", 4.17, 1.0, LocalDate.of(2024, 12, 23));
            UNCaddPurchases(ps, "880e8400-e29b-41d4-a716-446655440003", 4.18, 1.0, LocalDate.of(2025, 2, 21));
            UNCaddPurchases(ps, "880e8400-e29b-41d4-a716-446655440003", 4.19, 1.0, LocalDate.of(2025, 3, 23));
            UNCaddPurchases(ps, "880e8400-e29b-41d4-a716-446655440003", 4.20, 1.0, LocalDate.of(2025, 4, 22));
            UNCaddPurchases(ps, "880e8400-e29b-41d4-a716-446655440003", 4.22, 1.0, LocalDate.of(2025, 5, 22));
            UNCaddPurchases(ps, "880e8400-e29b-41d4-a716-446655440003", 4.24, 1.0, LocalDate.of(2025, 5, 29));
            UNCaddPurchases(ps, "880e8400-e29b-41d4-a716-446655440003", 4.25, 1.0, LocalDate.of(2025, 7, 28));
            UNCaddPurchases(ps, "880e8400-e29b-41d4-a716-446655440003", 4.26, 1.0, LocalDate.of(2025, 8, 4));
            UNCaddPurchases(ps, "880e8400-e29b-41d4-a716-446655440003", 4.27, 1.0, LocalDate.of(2025, 8, 7));
            UNCaddPurchases(ps, "880e8400-e29b-41d4-a716-446655440003", 4.28, 1.0, LocalDate.of(2025, 8, 14));
            UNCaddPurchases(ps, "880e8400-e29b-41d4-a716-446655440003", 4.29, 1.0, LocalDate.of(2025, 8, 17));
            UNCaddPurchases(ps, "880e8400-e29b-41d4-a716-446655440003", 4.31, 1.0, LocalDate.of(2025, 9, 16));

            // --- Toilet Paper ---
            UNCaddPurchases(ps, "990e8400-e29b-41d4-a716-446655440004", 22.00, 1.0, LocalDate.of(2023, 10, 29));
            UNCaddPurchases(ps, "990e8400-e29b-41d4-a716-446655440004", 22.07, 1.0, LocalDate.of(2023, 11, 1));
            UNCaddPurchases(ps, "990e8400-e29b-41d4-a716-446655440004", 22.13, 1.0, LocalDate.of(2023, 12, 1));
            UNCaddPurchases(ps, "990e8400-e29b-41d4-a716-446655440004", 22.21, 1.0, LocalDate.of(2023, 12, 4));
            UNCaddPurchases(ps, "990e8400-e29b-41d4-a716-446655440004", 22.30, 1.0, LocalDate.of(2024, 1, 3));
            UNCaddPurchases(ps, "990e8400-e29b-41d4-a716-446655440004", 22.32, 1.0, LocalDate.of(2024, 2, 2));
            UNCaddPurchases(ps, "990e8400-e29b-41d4-a716-446655440004", 22.38, 1.0, LocalDate.of(2024, 2, 9));
            UNCaddPurchases(ps, "990e8400-e29b-41d4-a716-446655440004", 22.42, 1.0, LocalDate.of(2024, 4, 9));
            UNCaddPurchases(ps, "990e8400-e29b-41d4-a716-446655440004", 22.47, 1.0, LocalDate.of(2024, 4, 16));
            UNCaddPurchases(ps, "990e8400-e29b-41d4-a716-446655440004", 22.50, 1.0, LocalDate.of(2024, 4, 19));
            UNCaddPurchases(ps, "990e8400-e29b-41d4-a716-446655440004", 22.55, 1.0, LocalDate.of(2024, 4, 26));
            UNCaddPurchases(ps, "990e8400-e29b-41d4-a716-446655440004", 22.63, 1.0, LocalDate.of(2024, 4, 29));
            UNCaddPurchases(ps, "990e8400-e29b-41d4-a716-446655440004", 22.65, 1.0, LocalDate.of(2024, 6, 28));
            UNCaddPurchases(ps, "990e8400-e29b-41d4-a716-446655440004", 22.70, 1.0, LocalDate.of(2024, 7, 1));
            UNCaddPurchases(ps, "990e8400-e29b-41d4-a716-446655440004", 22.75, 1.0, LocalDate.of(2024, 7, 4));
            UNCaddPurchases(ps, "990e8400-e29b-41d4-a716-446655440004", 22.83, 1.0, LocalDate.of(2024, 7, 7));
            UNCaddPurchases(ps, "990e8400-e29b-41d4-a716-446655440004", 22.89, 1.0, LocalDate.of(2024, 9, 5));
            UNCaddPurchases(ps, "990e8400-e29b-41d4-a716-446655440004", 22.97, 1.0, LocalDate.of(2024, 11, 4));
            UNCaddPurchases(ps, "990e8400-e29b-41d4-a716-446655440004", 23.01, 1.0, LocalDate.of(2024, 11, 11));
            UNCaddPurchases(ps, "990e8400-e29b-41d4-a716-446655440004", 23.08, 1.0, LocalDate.of(2024, 11, 14));
            UNCaddPurchases(ps, "990e8400-e29b-41d4-a716-446655440004", 23.13, 1.0, LocalDate.of(2024, 12, 14));
            UNCaddPurchases(ps, "990e8400-e29b-41d4-a716-446655440004", 23.20, 1.0, LocalDate.of(2025, 2, 12));
            UNCaddPurchases(ps, "990e8400-e29b-41d4-a716-446655440004", 23.24, 1.0, LocalDate.of(2025, 2, 15));
            UNCaddPurchases(ps, "990e8400-e29b-41d4-a716-446655440004", 23.29, 1.0, LocalDate.of(2025, 4, 16));
            UNCaddPurchases(ps, "990e8400-e29b-41d4-a716-446655440004", 23.35, 1.0, LocalDate.of(2025, 5, 16));
            UNCaddPurchases(ps, "990e8400-e29b-41d4-a716-446655440004", 23.43, 1.0, LocalDate.of(2025, 5, 19));
            UNCaddPurchases(ps, "990e8400-e29b-41d4-a716-446655440004", 23.47, 1.0, LocalDate.of(2025, 5, 22));
            UNCaddPurchases(ps, "990e8400-e29b-41d4-a716-446655440004", 23.56, 1.0, LocalDate.of(2025, 5, 25));
            UNCaddPurchases(ps, "990e8400-e29b-41d4-a716-446655440004", 23.63, 1.0, LocalDate.of(2025, 5, 28));
            UNCaddPurchases(ps, "990e8400-e29b-41d4-a716-446655440004", 23.69, 1.0, LocalDate.of(2025, 7, 27));
            UNCaddPurchases(ps, "990e8400-e29b-41d4-a716-446655440004", 23.71, 1.0, LocalDate.of(2025, 7, 30));
            UNCaddPurchases(ps, "990e8400-e29b-41d4-a716-446655440004", 23.74, 1.0, LocalDate.of(2025, 8, 29));
            UNCaddPurchases(ps, "990e8400-e29b-41d4-a716-446655440004", 23.79, 1.0, LocalDate.of(2025, 10, 28));

            // --- Cheese ---
            UNCaddPurchases(ps, "aa0e8400-e29b-41d4-a716-446655440005", 8.00, 1.0, LocalDate.of(2023, 10, 29));
            UNCaddPurchases(ps, "aa0e8400-e29b-41d4-a716-446655440005", 8.01, 1.0, LocalDate.of(2023, 12, 28));
            UNCaddPurchases(ps, "aa0e8400-e29b-41d4-a716-446655440005", 8.02, 1.0, LocalDate.of(2024, 1, 27));
            UNCaddPurchases(ps, "aa0e8400-e29b-41d4-a716-446655440005", 8.05, 1.0, LocalDate.of(2024, 1, 30));
            UNCaddPurchases(ps, "aa0e8400-e29b-41d4-a716-446655440005", 8.08, 1.0, LocalDate.of(2024, 2, 2));
            UNCaddPurchases(ps, "aa0e8400-e29b-41d4-a716-446655440005", 8.10, 1.0, LocalDate.of(2024, 4, 2));
            UNCaddPurchases(ps, "aa0e8400-e29b-41d4-a716-446655440005", 8.13, 1.0, LocalDate.of(2024, 4, 5));
            UNCaddPurchases(ps, "aa0e8400-e29b-41d4-a716-446655440005", 8.16, 1.0, LocalDate.of(2024, 4, 12));
            UNCaddPurchases(ps, "aa0e8400-e29b-41d4-a716-446655440005", 8.17, 1.0, LocalDate.of(2024, 6, 11));
            UNCaddPurchases(ps, "aa0e8400-e29b-41d4-a716-446655440005", 8.19, 1.0, LocalDate.of(2024, 8, 10));
            UNCaddPurchases(ps, "aa0e8400-e29b-41d4-a716-446655440005", 8.20, 1.0, LocalDate.of(2024, 10, 9));
            UNCaddPurchases(ps, "aa0e8400-e29b-41d4-a716-446655440005", 8.21, 1.0, LocalDate.of(2024, 11, 8));
            UNCaddPurchases(ps, "aa0e8400-e29b-41d4-a716-446655440005", 8.22, 1.0, LocalDate.of(2024, 11, 15));
            UNCaddPurchases(ps, "aa0e8400-e29b-41d4-a716-446655440005", 8.25, 1.0, LocalDate.of(2024, 12, 15));
            UNCaddPurchases(ps, "aa0e8400-e29b-41d4-a716-446655440005", 8.28, 1.0, LocalDate.of(2025, 1, 14));
            UNCaddPurchases(ps, "aa0e8400-e29b-41d4-a716-446655440005", 8.29, 1.0, LocalDate.of(2025, 1, 17));
            UNCaddPurchases(ps, "aa0e8400-e29b-41d4-a716-446655440005", 8.31, 1.0, LocalDate.of(2025, 2, 16));
            UNCaddPurchases(ps, "aa0e8400-e29b-41d4-a716-446655440005", 8.34, 1.0, LocalDate.of(2025, 2, 19));
            UNCaddPurchases(ps, "aa0e8400-e29b-41d4-a716-446655440005", 8.36, 1.0, LocalDate.of(2025, 3, 21));
            UNCaddPurchases(ps, "aa0e8400-e29b-41d4-a716-446655440005", 8.38, 1.0, LocalDate.of(2025, 4, 20));
            UNCaddPurchases(ps, "aa0e8400-e29b-41d4-a716-446655440005", 8.39, 1.0, LocalDate.of(2025, 5, 20));
            UNCaddPurchases(ps, "aa0e8400-e29b-41d4-a716-446655440005", 8.41, 1.0, LocalDate.of(2025, 7, 19));
            UNCaddPurchases(ps, "aa0e8400-e29b-41d4-a716-446655440005", 8.44, 1.0, LocalDate.of(2025, 8, 18));
            UNCaddPurchases(ps, "aa0e8400-e29b-41d4-a716-446655440005", 8.47, 1.0, LocalDate.of(2025, 8, 21));
            UNCaddPurchases(ps, "aa0e8400-e29b-41d4-a716-446655440005", 8.50, 1.0, LocalDate.of(2025, 8, 24));
            UNCaddPurchases(ps, "aa0e8400-e29b-41d4-a716-446655440005", 8.51, 1.0, LocalDate.of(2025, 8, 27));
            UNCaddPurchases(ps, "aa0e8400-e29b-41d4-a716-446655440005", 8.53, 1.0, LocalDate.of(2025, 8, 30));
            UNCaddPurchases(ps, "aa0e8400-e29b-41d4-a716-446655440005", 8.55, 1.0, LocalDate.of(2025, 9, 2));

            // --- Chicken ---
            UNCaddPurchases(ps, "bb0e8400-e29b-41d4-a716-446655440006", 12.00, 1.0, LocalDate.of(2023, 10, 29));
            UNCaddPurchases(ps, "bb0e8400-e29b-41d4-a716-446655440006", 12.01, 1.0, LocalDate.of(2023, 12, 28));
            UNCaddPurchases(ps, "bb0e8400-e29b-41d4-a716-446655440006", 12.05, 1.0, LocalDate.of(2024, 2, 26));
            UNCaddPurchases(ps, "bb0e8400-e29b-41d4-a716-446655440006", 12.08, 1.0, LocalDate.of(2024, 3, 4));
            UNCaddPurchases(ps, "bb0e8400-e29b-41d4-a716-446655440006", 12.12, 1.0, LocalDate.of(2024, 3, 11));
            UNCaddPurchases(ps, "bb0e8400-e29b-41d4-a716-446655440006", 12.14, 1.0, LocalDate.of(2024, 3, 14));
            UNCaddPurchases(ps, "bb0e8400-e29b-41d4-a716-446655440006", 12.16, 1.0, LocalDate.of(2024, 3, 17));
            UNCaddPurchases(ps, "bb0e8400-e29b-41d4-a716-446655440006", 12.19, 1.0, LocalDate.of(2024, 3, 24));
            UNCaddPurchases(ps, "bb0e8400-e29b-41d4-a716-446655440006", 12.22, 1.0, LocalDate.of(2024, 4, 23));
            UNCaddPurchases(ps, "bb0e8400-e29b-41d4-a716-446655440006", 12.26, 1.0, LocalDate.of(2024, 4, 30));
            UNCaddPurchases(ps, "bb0e8400-e29b-41d4-a716-446655440006", 12.30, 1.0, LocalDate.of(2024, 5, 30));
            UNCaddPurchases(ps, "bb0e8400-e29b-41d4-a716-446655440006", 12.33, 1.0, LocalDate.of(2024, 6, 2));
            UNCaddPurchases(ps, "bb0e8400-e29b-41d4-a716-446655440006", 12.34, 1.0, LocalDate.of(2024, 6, 5));
            UNCaddPurchases(ps, "bb0e8400-e29b-41d4-a716-446655440006", 12.36, 1.0, LocalDate.of(2024, 7, 5));
            UNCaddPurchases(ps, "bb0e8400-e29b-41d4-a716-446655440006", 12.37, 1.0, LocalDate.of(2024, 8, 4));
            UNCaddPurchases(ps, "bb0e8400-e29b-41d4-a716-446655440006", 12.41, 1.0, LocalDate.of(2024, 8, 7));
            UNCaddPurchases(ps, "bb0e8400-e29b-41d4-a716-446655440006", 12.46, 1.0, LocalDate.of(2024, 10, 6));
            UNCaddPurchases(ps, "bb0e8400-e29b-41d4-a716-446655440006", 12.49, 1.0, LocalDate.of(2024, 11, 5));
            UNCaddPurchases(ps, "bb0e8400-e29b-41d4-a716-446655440006", 12.51, 1.0, LocalDate.of(2024, 11, 12));
            UNCaddPurchases(ps, "bb0e8400-e29b-41d4-a716-446655440006", 12.55, 1.0, LocalDate.of(2024, 12, 12));
            UNCaddPurchases(ps, "bb0e8400-e29b-41d4-a716-446655440006", 12.58, 1.0, LocalDate.of(2025, 1, 11));
            UNCaddPurchases(ps, "bb0e8400-e29b-41d4-a716-446655440006", 12.62, 1.0, LocalDate.of(2025, 1, 18));
            UNCaddPurchases(ps, "bb0e8400-e29b-41d4-a716-446655440006", 12.66, 1.0, LocalDate.of(2025, 3, 19));
            UNCaddPurchases(ps, "bb0e8400-e29b-41d4-a716-446655440006", 12.69, 1.0, LocalDate.of(2025, 5, 18));
            UNCaddPurchases(ps, "bb0e8400-e29b-41d4-a716-446655440006", 12.72, 1.0, LocalDate.of(2025, 7, 17));
            UNCaddPurchases(ps, "bb0e8400-e29b-41d4-a716-446655440006", 12.74, 1.0, LocalDate.of(2025, 8, 16));
            UNCaddPurchases(ps, "bb0e8400-e29b-41d4-a716-446655440006", 12.78, 1.0, LocalDate.of(2025, 8, 19));
            UNCaddPurchases(ps, "bb0e8400-e29b-41d4-a716-446655440006", 12.81, 1.0, LocalDate.of(2025, 9, 18));
            UNCaddPurchases(ps, "bb0e8400-e29b-41d4-a716-446655440006", 12.85, 1.0, LocalDate.of(2025, 9, 21));
            UNCaddPurchases(ps, "bb0e8400-e29b-41d4-a716-446655440006", 12.89, 1.0, LocalDate.of(2025, 9, 24));
            UNCaddPurchases(ps, "bb0e8400-e29b-41d4-a716-446655440006", 12.91, 1.0, LocalDate.of(2025, 9, 27));
            UNCaddPurchases(ps, "bb0e8400-e29b-41d4-a716-446655440006", 12.94, 1.0, LocalDate.of(2025, 10, 27));

            // --- Rice ---
            UNCaddPurchases(ps, "cc0e8400-e29b-41d4-a716-446655440007", 18.00, 2.0, LocalDate.of(2023, 10, 29));
            UNCaddPurchases(ps, "cc0e8400-e29b-41d4-a716-446655440007", 18.03, 2.0, LocalDate.of(2023, 12, 28));
            UNCaddPurchases(ps, "cc0e8400-e29b-41d4-a716-446655440007", 18.08, 2.0, LocalDate.of(2023, 12, 31));
            UNCaddPurchases(ps, "cc0e8400-e29b-41d4-a716-446655440007", 18.11, 2.0, LocalDate.of(2024, 1, 3));
            UNCaddPurchases(ps, "cc0e8400-e29b-41d4-a716-446655440007", 18.13, 2.0, LocalDate.of(2024, 3, 3));
            UNCaddPurchases(ps, "cc0e8400-e29b-41d4-a716-446655440007", 18.16, 2.0, LocalDate.of(2024, 4, 2));
            UNCaddPurchases(ps, "cc0e8400-e29b-41d4-a716-446655440007", 18.21, 2.0, LocalDate.of(2024, 4, 5));
            UNCaddPurchases(ps, "cc0e8400-e29b-41d4-a716-446655440007", 18.28, 2.0, LocalDate.of(2024, 5, 5));
            UNCaddPurchases(ps, "cc0e8400-e29b-41d4-a716-446655440007", 18.33, 2.0, LocalDate.of(2024, 5, 12));
            UNCaddPurchases(ps, "cc0e8400-e29b-41d4-a716-446655440007", 18.36, 2.0, LocalDate.of(2024, 5, 15));
            UNCaddPurchases(ps, "cc0e8400-e29b-41d4-a716-446655440007", 18.41, 2.0, LocalDate.of(2024, 5, 22));
            UNCaddPurchases(ps, "cc0e8400-e29b-41d4-a716-446655440007", 18.48, 2.0, LocalDate.of(2024, 5, 29));
            UNCaddPurchases(ps, "cc0e8400-e29b-41d4-a716-446655440007", 18.52, 2.0, LocalDate.of(2024, 7, 28));
            UNCaddPurchases(ps, "cc0e8400-e29b-41d4-a716-446655440007", 18.57, 2.0, LocalDate.of(2024, 8, 4));
            UNCaddPurchases(ps, "cc0e8400-e29b-41d4-a716-446655440007", 18.60, 2.0, LocalDate.of(2024, 10, 3));
            UNCaddPurchases(ps, "cc0e8400-e29b-41d4-a716-446655440007", 18.66, 2.0, LocalDate.of(2024, 10, 10));
            UNCaddPurchases(ps, "cc0e8400-e29b-41d4-a716-446655440007", 18.73, 2.0, LocalDate.of(2024, 10, 17));
            UNCaddPurchases(ps, "cc0e8400-e29b-41d4-a716-446655440007", 18.77, 2.0, LocalDate.of(2024, 10, 20));
            UNCaddPurchases(ps, "cc0e8400-e29b-41d4-a716-446655440007", 18.81, 2.0, LocalDate.of(2024, 11, 19));
            UNCaddPurchases(ps, "cc0e8400-e29b-41d4-a716-446655440007", 18.88, 2.0, LocalDate.of(2024, 11, 26));
            UNCaddPurchases(ps, "cc0e8400-e29b-41d4-a716-446655440007", 18.92, 2.0, LocalDate.of(2024, 12, 3));
            UNCaddPurchases(ps, "cc0e8400-e29b-41d4-a716-446655440007", 18.98, 2.0, LocalDate.of(2025, 1, 2));
            UNCaddPurchases(ps, "cc0e8400-e29b-41d4-a716-446655440007", 19.02, 2.0, LocalDate.of(2025, 3, 3));
            UNCaddPurchases(ps, "cc0e8400-e29b-41d4-a716-446655440007", 19.06, 2.0, LocalDate.of(2025, 5, 2));
            UNCaddPurchases(ps, "cc0e8400-e29b-41d4-a716-446655440007", 19.09, 2.0, LocalDate.of(2025, 7, 1));
            UNCaddPurchases(ps, "cc0e8400-e29b-41d4-a716-446655440007", 19.15, 2.0, LocalDate.of(2025, 7, 4));
            UNCaddPurchases(ps, "cc0e8400-e29b-41d4-a716-446655440007", 19.19, 2.0, LocalDate.of(2025, 8, 3));
            UNCaddPurchases(ps, "cc0e8400-e29b-41d4-a716-446655440007", 19.21, 2.0, LocalDate.of(2025, 8, 6));
            UNCaddPurchases(ps, "cc0e8400-e29b-41d4-a716-446655440007", 19.26, 2.0, LocalDate.of(2025, 9, 5));
            UNCaddPurchases(ps, "cc0e8400-e29b-41d4-a716-446655440007", 19.31, 2.0, LocalDate.of(2025, 10, 5));

            // --- Pasta ---
            UNCaddPurchases(ps, "dd0e8400-e29b-41d4-a716-446655440008", 6.00, 1.0, LocalDate.of(2023, 10, 29));
            UNCaddPurchases(ps, "dd0e8400-e29b-41d4-a716-446655440008", 6.02, 1.0, LocalDate.of(2023, 12, 28));
            UNCaddPurchases(ps, "dd0e8400-e29b-41d4-a716-446655440008", 6.05, 1.0, LocalDate.of(2024, 1, 27));
            UNCaddPurchases(ps, "dd0e8400-e29b-41d4-a716-446655440008", 6.07, 1.0, LocalDate.of(2024, 3, 27));
            UNCaddPurchases(ps, "dd0e8400-e29b-41d4-a716-446655440008", 6.08, 1.0, LocalDate.of(2024, 4, 26));
            UNCaddPurchases(ps, "dd0e8400-e29b-41d4-a716-446655440008", 6.11, 1.0, LocalDate.of(2024, 5, 26));
            UNCaddPurchases(ps, "dd0e8400-e29b-41d4-a716-446655440008", 6.13, 1.0, LocalDate.of(2024, 6, 25));
            UNCaddPurchases(ps, "dd0e8400-e29b-41d4-a716-446655440008", 6.15, 1.0, LocalDate.of(2024, 7, 25));
            UNCaddPurchases(ps, "dd0e8400-e29b-41d4-a716-446655440008", 6.17, 1.0, LocalDate.of(2024, 7, 28));
            UNCaddPurchases(ps, "dd0e8400-e29b-41d4-a716-446655440008", 6.18, 1.0, LocalDate.of(2024, 8, 4));
            UNCaddPurchases(ps, "dd0e8400-e29b-41d4-a716-446655440008", 6.19, 1.0, LocalDate.of(2024, 8, 11));
            UNCaddPurchases(ps, "dd0e8400-e29b-41d4-a716-446655440008", 6.20, 1.0, LocalDate.of(2024, 9, 10));
            UNCaddPurchases(ps, "dd0e8400-e29b-41d4-a716-446655440008", 6.22, 1.0, LocalDate.of(2024, 10, 10));
            UNCaddPurchases(ps, "dd0e8400-e29b-41d4-a716-446655440008", 6.23, 1.0, LocalDate.of(2024, 11, 9));
            UNCaddPurchases(ps, "dd0e8400-e29b-41d4-a716-446655440008", 6.24, 1.0, LocalDate.of(2024, 11, 12));
            UNCaddPurchases(ps, "dd0e8400-e29b-41d4-a716-446655440008", 6.25, 1.0, LocalDate.of(2024, 11, 19));
            UNCaddPurchases(ps, "dd0e8400-e29b-41d4-a716-446655440008", 6.26, 1.0, LocalDate.of(2024, 11, 26));
            UNCaddPurchases(ps, "dd0e8400-e29b-41d4-a716-446655440008", 6.28, 1.0, LocalDate.of(2024, 12, 26));
            UNCaddPurchases(ps, "dd0e8400-e29b-41d4-a716-446655440008", 6.29, 1.0, LocalDate.of(2024, 12, 29));
            UNCaddPurchases(ps, "dd0e8400-e29b-41d4-a716-446655440008", 6.31, 1.0, LocalDate.of(2025, 1, 28));
            UNCaddPurchases(ps, "dd0e8400-e29b-41d4-a716-446655440008", 6.33, 1.0, LocalDate.of(2025, 2, 4));
            UNCaddPurchases(ps, "dd0e8400-e29b-41d4-a716-446655440008", 6.35, 1.0, LocalDate.of(2025, 2, 7));
            UNCaddPurchases(ps, "dd0e8400-e29b-41d4-a716-446655440008", 6.36, 1.0, LocalDate.of(2025, 4, 8));
            UNCaddPurchases(ps, "dd0e8400-e29b-41d4-a716-446655440008", 6.37, 1.0, LocalDate.of(2025, 6, 7));
            UNCaddPurchases(ps, "dd0e8400-e29b-41d4-a716-446655440008", 6.38, 1.0, LocalDate.of(2025, 8, 6));
            UNCaddPurchases(ps, "dd0e8400-e29b-41d4-a716-446655440008", 6.40, 1.0, LocalDate.of(2025, 9, 5));

            // --- Tomatoes ---
            UNCaddPurchases(ps, "ee0e8400-e29b-41d4-a716-446655440009", 5.00, 1.0, LocalDate.of(2023, 10, 29));
            UNCaddPurchases(ps, "ee0e8400-e29b-41d4-a716-446655440009", 5.01, 1.0, LocalDate.of(2023, 11, 1));
            UNCaddPurchases(ps, "ee0e8400-e29b-41d4-a716-446655440009", 5.02, 1.0, LocalDate.of(2023, 11, 8));
            UNCaddPurchases(ps, "ee0e8400-e29b-41d4-a716-446655440009", 5.03, 1.0, LocalDate.of(2023, 11, 11));
            UNCaddPurchases(ps, "ee0e8400-e29b-41d4-a716-446655440009", 5.05, 1.0, LocalDate.of(2023, 12, 11));
            UNCaddPurchases(ps, "ee0e8400-e29b-41d4-a716-446655440009", 5.06, 1.0, LocalDate.of(2024, 1, 10));
            UNCaddPurchases(ps, "ee0e8400-e29b-41d4-a716-446655440009", 5.07, 1.0, LocalDate.of(2024, 2, 9));
            UNCaddPurchases(ps, "ee0e8400-e29b-41d4-a716-446655440009", 5.08, 1.0, LocalDate.of(2024, 4, 9));
            UNCaddPurchases(ps, "ee0e8400-e29b-41d4-a716-446655440009", 5.09, 1.0, LocalDate.of(2024, 5, 9));
            UNCaddPurchases(ps, "ee0e8400-e29b-41d4-a716-446655440009", 5.10, 1.0, LocalDate.of(2024, 6, 8));
            UNCaddPurchases(ps, "ee0e8400-e29b-41d4-a716-446655440009", 5.11, 1.0, LocalDate.of(2024, 6, 11));
            UNCaddPurchases(ps, "ee0e8400-e29b-41d4-a716-446655440009", 5.12, 1.0, LocalDate.of(2024, 8, 10));
            UNCaddPurchases(ps, "ee0e8400-e29b-41d4-a716-446655440009", 5.13, 1.0, LocalDate.of(2024, 10, 9));
            UNCaddPurchases(ps, "ee0e8400-e29b-41d4-a716-446655440009", 5.14, 1.0, LocalDate.of(2024, 10, 12));
            UNCaddPurchases(ps, "ee0e8400-e29b-41d4-a716-446655440009", 5.16, 1.0, LocalDate.of(2024, 12, 11));
            UNCaddPurchases(ps, "ee0e8400-e29b-41d4-a716-446655440009", 5.17, 1.0, LocalDate.of(2024, 12, 18));
            UNCaddPurchases(ps, "ee0e8400-e29b-41d4-a716-446655440009", 5.18, 1.0, LocalDate.of(2025, 1, 17));
            UNCaddPurchases(ps, "ee0e8400-e29b-41d4-a716-446655440009", 5.20, 1.0, LocalDate.of(2025, 1, 24));
            UNCaddPurchases(ps, "ee0e8400-e29b-41d4-a716-446655440009", 5.21, 1.0, LocalDate.of(2025, 1, 27));
            UNCaddPurchases(ps, "ee0e8400-e29b-41d4-a716-446655440009", 5.22, 1.0, LocalDate.of(2025, 3, 28));
            UNCaddPurchases(ps, "ee0e8400-e29b-41d4-a716-446655440009", 5.23, 1.0, LocalDate.of(2025, 3, 31));
            UNCaddPurchases(ps, "ee0e8400-e29b-41d4-a716-446655440009", 5.24, 1.0, LocalDate.of(2025, 5, 30));
            UNCaddPurchases(ps, "ee0e8400-e29b-41d4-a716-446655440009", 5.26, 1.0, LocalDate.of(2025, 6, 6));
            UNCaddPurchases(ps, "ee0e8400-e29b-41d4-a716-446655440009", 5.28, 1.0, LocalDate.of(2025, 6, 13));
            UNCaddPurchases(ps, "ee0e8400-e29b-41d4-a716-446655440009", 5.29, 1.0, LocalDate.of(2025, 8, 12));
            UNCaddPurchases(ps, "ee0e8400-e29b-41d4-a716-446655440009", 5.31, 1.0, LocalDate.of(2025, 8, 19));
            UNCaddPurchases(ps, "ee0e8400-e29b-41d4-a716-446655440009", 5.32, 1.0, LocalDate.of(2025, 10, 18));
            UNCaddPurchases(ps, "ee0e8400-e29b-41d4-a716-446655440009", 5.34, 1.0, LocalDate.of(2025, 10, 21));
            UNCaddPurchases(ps, "ee0e8400-e29b-41d4-a716-446655440009", 5.35, 1.0, LocalDate.of(2025, 10, 28));

            // --- Coffee ---
            UNCaddPurchases(ps, "ff0e8400-e29b-41d4-a716-446655440010", 15.00, 1.0, LocalDate.of(2023, 10, 29));
            UNCaddPurchases(ps, "ff0e8400-e29b-41d4-a716-446655440010", 15.04, 1.0, LocalDate.of(2023, 11, 5));
            UNCaddPurchases(ps, "ff0e8400-e29b-41d4-a716-446655440010", 15.10, 1.0, LocalDate.of(2023, 11, 8));
            UNCaddPurchases(ps, "ff0e8400-e29b-41d4-a716-446655440010", 15.15, 1.0, LocalDate.of(2024, 1, 7));
            UNCaddPurchases(ps, "ff0e8400-e29b-41d4-a716-446655440010", 15.21, 1.0, LocalDate.of(2024, 1, 10));
            UNCaddPurchases(ps, "ff0e8400-e29b-41d4-a716-446655440010", 15.27, 1.0, LocalDate.of(2024, 3, 10));
            UNCaddPurchases(ps, "ff0e8400-e29b-41d4-a716-446655440010", 15.32, 1.0, LocalDate.of(2024, 3, 17));
            UNCaddPurchases(ps, "ff0e8400-e29b-41d4-a716-446655440010", 15.36, 1.0, LocalDate.of(2024, 4, 16));
            UNCaddPurchases(ps, "ff0e8400-e29b-41d4-a716-446655440010", 15.42, 1.0, LocalDate.of(2024, 4, 23));
            UNCaddPurchases(ps, "ff0e8400-e29b-41d4-a716-446655440010", 15.47, 1.0, LocalDate.of(2024, 4, 30));
            UNCaddPurchases(ps, "ff0e8400-e29b-41d4-a716-446655440010", 15.52, 1.0, LocalDate.of(2024, 6, 29));
            UNCaddPurchases(ps, "ff0e8400-e29b-41d4-a716-446655440010", 15.55, 1.0, LocalDate.of(2024, 7, 2));
            UNCaddPurchases(ps, "ff0e8400-e29b-41d4-a716-446655440010", 15.59, 1.0, LocalDate.of(2024, 7, 5));
            UNCaddPurchases(ps, "ff0e8400-e29b-41d4-a716-446655440010", 15.63, 1.0, LocalDate.of(2024, 7, 12));
            UNCaddPurchases(ps, "ff0e8400-e29b-41d4-a716-446655440010", 15.67, 1.0, LocalDate.of(2024, 7, 15));
            UNCaddPurchases(ps, "ff0e8400-e29b-41d4-a716-446655440010", 15.72, 1.0, LocalDate.of(2024, 8, 14));
            UNCaddPurchases(ps, "ff0e8400-e29b-41d4-a716-446655440010", 15.76, 1.0, LocalDate.of(2024, 10, 13));
            UNCaddPurchases(ps, "ff0e8400-e29b-41d4-a716-446655440010", 15.83, 1.0, LocalDate.of(2024, 10, 20));
            UNCaddPurchases(ps, "ff0e8400-e29b-41d4-a716-446655440010", 15.87, 1.0, LocalDate.of(2024, 10, 23));
            UNCaddPurchases(ps, "ff0e8400-e29b-41d4-a716-446655440010", 15.93, 1.0, LocalDate.of(2024, 12, 22));
            UNCaddPurchases(ps, "ff0e8400-e29b-41d4-a716-446655440010", 15.97, 1.0, LocalDate.of(2025, 2, 20));
            UNCaddPurchases(ps, "ff0e8400-e29b-41d4-a716-446655440010", 15.99, 1.0, LocalDate.of(2025, 4, 21));
            UNCaddPurchases(ps, "ff0e8400-e29b-41d4-a716-446655440010", 16.05, 1.0, LocalDate.of(2025, 4, 28));
            UNCaddPurchases(ps, "ff0e8400-e29b-41d4-a716-446655440010", 16.12, 1.0, LocalDate.of(2025, 5, 28));
            UNCaddPurchases(ps, "ff0e8400-e29b-41d4-a716-446655440010", 16.17, 1.0, LocalDate.of(2025, 5, 31));
            UNCaddPurchases(ps, "ff0e8400-e29b-41d4-a716-446655440010", 16.19, 1.0, LocalDate.of(2025, 6, 30));
            UNCaddPurchases(ps, "ff0e8400-e29b-41d4-a716-446655440010", 16.25, 1.0, LocalDate.of(2025, 7, 3));
            UNCaddPurchases(ps, "ff0e8400-e29b-41d4-a716-446655440010", 16.31, 1.0, LocalDate.of(2025, 7, 10));
            UNCaddPurchases(ps, "ff0e8400-e29b-41d4-a716-446655440010", 16.33, 1.0, LocalDate.of(2025, 7, 17));
            UNCaddPurchases(ps, "ff0e8400-e29b-41d4-a716-446655440010", 16.37, 1.0, LocalDate.of(2025, 9, 15));
            UNCaddPurchases(ps, "ff0e8400-e29b-41d4-a716-446655440010", 16.39, 1.0, LocalDate.of(2025, 9, 18));
            UNCaddPurchases(ps, "ff0e8400-e29b-41d4-a716-446655440010", 16.44, 1.0, LocalDate.of(2025, 10, 18));
            UNCaddPurchases(ps, "ff0e8400-e29b-41d4-a716-446655440010", 16.48, 1.0, LocalDate.of(2025, 10, 21));
            UNCaddPurchases(ps, "ff0e8400-e29b-41d4-a716-446655440010", 16.54, 1.0, LocalDate.of(2025, 10, 28));

            // --- Tea ---
            UNCaddPurchases(ps, "aa1e8400-e29b-41d4-a716-446655440011", 7.00, 1.0, LocalDate.of(2023, 10, 29));
            UNCaddPurchases(ps, "aa1e8400-e29b-41d4-a716-446655440011", 7.02, 1.0, LocalDate.of(2023, 12, 28));
            UNCaddPurchases(ps, "aa1e8400-e29b-41d4-a716-446655440011", 7.03, 1.0, LocalDate.of(2024, 1, 27));
            UNCaddPurchases(ps, "aa1e8400-e29b-41d4-a716-446655440011", 7.05, 1.0, LocalDate.of(2024, 3, 27));
            UNCaddPurchases(ps, "aa1e8400-e29b-41d4-a716-446655440011", 7.08, 1.0, LocalDate.of(2024, 5, 26));
            UNCaddPurchases(ps, "aa1e8400-e29b-41d4-a716-446655440011", 7.09, 1.0, LocalDate.of(2024, 7, 25));
            UNCaddPurchases(ps, "aa1e8400-e29b-41d4-a716-446655440011", 7.09, 1.0, LocalDate.of(2024, 8, 24));
            UNCaddPurchases(ps, "aa1e8400-e29b-41d4-a716-446655440011", 7.11, 1.0, LocalDate.of(2024, 8, 27));
            UNCaddPurchases(ps, "aa1e8400-e29b-41d4-a716-446655440011", 7.12, 1.0, LocalDate.of(2024, 9, 3));
            UNCaddPurchases(ps, "aa1e8400-e29b-41d4-a716-446655440011", 7.13, 1.0, LocalDate.of(2024, 11, 2));
            UNCaddPurchases(ps, "aa1e8400-e29b-41d4-a716-446655440011", 7.15, 1.0, LocalDate.of(2025, 1, 1));
            UNCaddPurchases(ps, "aa1e8400-e29b-41d4-a716-446655440011", 7.17, 1.0, LocalDate.of(2025, 1, 4));
            UNCaddPurchases(ps, "aa1e8400-e29b-41d4-a716-446655440011", 7.18, 1.0, LocalDate.of(2025, 2, 3));
            UNCaddPurchases(ps, "aa1e8400-e29b-41d4-a716-446655440011", 7.20, 1.0, LocalDate.of(2025, 2, 6));
            UNCaddPurchases(ps, "aa1e8400-e29b-41d4-a716-446655440011", 7.21, 1.0, LocalDate.of(2025, 3, 8));
            UNCaddPurchases(ps, "aa1e8400-e29b-41d4-a716-446655440011", 7.23, 1.0, LocalDate.of(2025, 3, 15));
            UNCaddPurchases(ps, "aa1e8400-e29b-41d4-a716-446655440011", 7.25, 1.0, LocalDate.of(2025, 4, 14));
            UNCaddPurchases(ps, "aa1e8400-e29b-41d4-a716-446655440011", 7.28, 1.0, LocalDate.of(2025, 4, 17));
            UNCaddPurchases(ps, "aa1e8400-e29b-41d4-a716-446655440011", 7.30, 1.0, LocalDate.of(2025, 4, 20));
            UNCaddPurchases(ps, "aa1e8400-e29b-41d4-a716-446655440011", 7.31, 1.0, LocalDate.of(2025, 6, 19));
            UNCaddPurchases(ps, "aa1e8400-e29b-41d4-a716-446655440011", 7.33, 1.0, LocalDate.of(2025, 6, 22));
            UNCaddPurchases(ps, "aa1e8400-e29b-41d4-a716-446655440011", 7.34, 1.0, LocalDate.of(2025, 8, 21));
            UNCaddPurchases(ps, "aa1e8400-e29b-41d4-a716-446655440011", 7.35, 1.0, LocalDate.of(2025, 8, 28));
            UNCaddPurchases(ps, "aa1e8400-e29b-41d4-a716-446655440011", 7.36, 1.0, LocalDate.of(2025, 9, 27));
            UNCaddPurchases(ps, "aa1e8400-e29b-41d4-a716-446655440011", 7.39, 1.0, LocalDate.of(2025, 10, 4));

            // --- Sugar ---
            UNCaddPurchases(ps, "bb1e8400-e29b-41d4-a716-446655440012", 3.50, 1.0, LocalDate.of(2023, 10, 29));
            UNCaddPurchases(ps, "bb1e8400-e29b-41d4-a716-446655440012", 3.51, 1.0, LocalDate.of(2023, 11, 1));
            UNCaddPurchases(ps, "bb1e8400-e29b-41d4-a716-446655440012", 3.52, 1.0, LocalDate.of(2023, 11, 8));
            UNCaddPurchases(ps, "bb1e8400-e29b-41d4-a716-446655440012", 3.53, 1.0, LocalDate.of(2023, 11, 15));
            UNCaddPurchases(ps, "bb1e8400-e29b-41d4-a716-446655440012", 3.53, 1.0, LocalDate.of(2024, 1, 14));
            UNCaddPurchases(ps, "bb1e8400-e29b-41d4-a716-446655440012", 3.54, 1.0, LocalDate.of(2024, 1, 21));
            UNCaddPurchases(ps, "bb1e8400-e29b-41d4-a716-446655440012", 3.56, 1.0, LocalDate.of(2024, 1, 24));
            UNCaddPurchases(ps, "bb1e8400-e29b-41d4-a716-446655440012", 3.56, 1.0, LocalDate.of(2024, 2, 23));
            UNCaddPurchases(ps, "bb1e8400-e29b-41d4-a716-446655440012", 3.58, 1.0, LocalDate.of(2024, 2, 26));
            UNCaddPurchases(ps, "bb1e8400-e29b-41d4-a716-446655440012", 3.59, 1.0, LocalDate.of(2024, 3, 4));
            UNCaddPurchases(ps, "bb1e8400-e29b-41d4-a716-446655440012", 3.60, 1.0, LocalDate.of(2024, 3, 11));
            UNCaddPurchases(ps, "bb1e8400-e29b-41d4-a716-446655440012", 3.61, 1.0, LocalDate.of(2024, 3, 18));
            UNCaddPurchases(ps, "bb1e8400-e29b-41d4-a716-446655440012", 3.62, 1.0, LocalDate.of(2024, 3, 25));
            UNCaddPurchases(ps, "bb1e8400-e29b-41d4-a716-446655440012", 3.63, 1.0, LocalDate.of(2024, 4, 24));
            UNCaddPurchases(ps, "bb1e8400-e29b-41d4-a716-446655440012", 3.65, 1.0, LocalDate.of(2024, 5, 1));
            UNCaddPurchases(ps, "bb1e8400-e29b-41d4-a716-446655440012", 3.65, 1.0, LocalDate.of(2024, 6, 30));
            UNCaddPurchases(ps, "bb1e8400-e29b-41d4-a716-446655440012", 3.66, 1.0, LocalDate.of(2024, 7, 3));
            UNCaddPurchases(ps, "bb1e8400-e29b-41d4-a716-446655440012", 3.67, 1.0, LocalDate.of(2024, 9, 1));
            UNCaddPurchases(ps, "bb1e8400-e29b-41d4-a716-446655440012", 3.68, 1.0, LocalDate.of(2024, 9, 8));
            UNCaddPurchases(ps, "bb1e8400-e29b-41d4-a716-446655440012", 3.68, 1.0, LocalDate.of(2024, 10, 8));
            UNCaddPurchases(ps, "bb1e8400-e29b-41d4-a716-446655440012", 3.69, 1.0, LocalDate.of(2024, 10, 15));
            UNCaddPurchases(ps, "bb1e8400-e29b-41d4-a716-446655440012", 3.69, 1.0, LocalDate.of(2024, 10, 18));
            UNCaddPurchases(ps, "bb1e8400-e29b-41d4-a716-446655440012", 3.70, 1.0, LocalDate.of(2024, 12, 17));
            UNCaddPurchases(ps, "bb1e8400-e29b-41d4-a716-446655440012", 3.71, 1.0, LocalDate.of(2024, 12, 20));
            UNCaddPurchases(ps, "bb1e8400-e29b-41d4-a716-446655440012", 3.72, 1.0, LocalDate.of(2024, 12, 27));
            UNCaddPurchases(ps, "bb1e8400-e29b-41d4-a716-446655440012", 3.74, 1.0, LocalDate.of(2025, 1, 26));
            UNCaddPurchases(ps, "bb1e8400-e29b-41d4-a716-446655440012", 3.75, 1.0, LocalDate.of(2025, 2, 2));
            UNCaddPurchases(ps, "bb1e8400-e29b-41d4-a716-446655440012", 3.76, 1.0, LocalDate.of(2025, 2, 9));
            UNCaddPurchases(ps, "bb1e8400-e29b-41d4-a716-446655440012", 3.77, 1.0, LocalDate.of(2025, 2, 12));
            UNCaddPurchases(ps, "bb1e8400-e29b-41d4-a716-446655440012", 3.78, 1.0, LocalDate.of(2025, 3, 14));
            UNCaddPurchases(ps, "bb1e8400-e29b-41d4-a716-446655440012", 3.79, 1.0, LocalDate.of(2025, 3, 17));
            UNCaddPurchases(ps, "bb1e8400-e29b-41d4-a716-446655440012", 3.79, 1.0, LocalDate.of(2025, 4, 16));
            UNCaddPurchases(ps, "bb1e8400-e29b-41d4-a716-446655440012", 3.81, 1.0, LocalDate.of(2025, 6, 15));
            UNCaddPurchases(ps, "bb1e8400-e29b-41d4-a716-446655440012", 3.82, 1.0, LocalDate.of(2025, 8, 14));
            UNCaddPurchases(ps, "bb1e8400-e29b-41d4-a716-446655440012", 3.83, 1.0, LocalDate.of(2025, 9, 13));
            UNCaddPurchases(ps, "bb1e8400-e29b-41d4-a716-446655440012", 3.84, 1.0, LocalDate.of(2025, 9, 16));
            UNCaddPurchases(ps, "bb1e8400-e29b-41d4-a716-446655440012", 3.85, 1.0, LocalDate.of(2025, 10, 16));

            // --- Salt ---
            UNCaddPurchases(ps, "cc1e8400-e29b-41d4-a716-446655440013", 2.50, 1.0, LocalDate.of(2023, 10, 29));
            UNCaddPurchases(ps, "cc1e8400-e29b-41d4-a716-446655440013", 2.51, 1.0, LocalDate.of(2023, 11, 1));
            UNCaddPurchases(ps, "cc1e8400-e29b-41d4-a716-446655440013", 2.51, 1.0, LocalDate.of(2023, 11, 8));
            UNCaddPurchases(ps, "cc1e8400-e29b-41d4-a716-446655440013", 2.52, 1.0, LocalDate.of(2023, 12, 8));
            UNCaddPurchases(ps, "cc1e8400-e29b-41d4-a716-446655440013", 2.52, 1.0, LocalDate.of(2023, 12, 15));
            UNCaddPurchases(ps, "cc1e8400-e29b-41d4-a716-446655440013", 2.53, 1.0, LocalDate.of(2024, 2, 13));
            UNCaddPurchases(ps, "cc1e8400-e29b-41d4-a716-446655440013", 2.53, 1.0, LocalDate.of(2024, 3, 14));
            UNCaddPurchases(ps, "cc1e8400-e29b-41d4-a716-446655440013", 2.54, 1.0, LocalDate.of(2024, 5, 13));
            UNCaddPurchases(ps, "cc1e8400-e29b-41d4-a716-446655440013", 2.54, 1.0, LocalDate.of(2024, 5, 16));
            UNCaddPurchases(ps, "cc1e8400-e29b-41d4-a716-446655440013", 2.55, 1.0, LocalDate.of(2024, 6, 15));
            UNCaddPurchases(ps, "cc1e8400-e29b-41d4-a716-446655440013", 2.55, 1.0, LocalDate.of(2024, 7, 15));
            UNCaddPurchases(ps, "cc1e8400-e29b-41d4-a716-446655440013", 2.56, 1.0, LocalDate.of(2024, 9, 13));
            UNCaddPurchases(ps, "cc1e8400-e29b-41d4-a716-446655440013", 2.57, 1.0, LocalDate.of(2024, 9, 16));
            UNCaddPurchases(ps, "cc1e8400-e29b-41d4-a716-446655440013", 2.57, 1.0, LocalDate.of(2024, 11, 15));
            UNCaddPurchases(ps, "cc1e8400-e29b-41d4-a716-446655440013", 2.58, 1.0, LocalDate.of(2025, 1, 14));
            UNCaddPurchases(ps, "cc1e8400-e29b-41d4-a716-446655440013", 2.59, 1.0, LocalDate.of(2025, 3, 15));
            UNCaddPurchases(ps, "cc1e8400-e29b-41d4-a716-446655440013", 2.60, 1.0, LocalDate.of(2025, 4, 14));
            UNCaddPurchases(ps, "cc1e8400-e29b-41d4-a716-446655440013", 2.60, 1.0, LocalDate.of(2025, 4, 17));
            UNCaddPurchases(ps, "cc1e8400-e29b-41d4-a716-446655440013", 2.61, 1.0, LocalDate.of(2025, 4, 20));
            UNCaddPurchases(ps, "cc1e8400-e29b-41d4-a716-446655440013", 2.62, 1.0, LocalDate.of(2025, 6, 19));
            UNCaddPurchases(ps, "cc1e8400-e29b-41d4-a716-446655440013", 2.62, 1.0, LocalDate.of(2025, 8, 18));
            UNCaddPurchases(ps, "cc1e8400-e29b-41d4-a716-446655440013", 2.63, 1.0, LocalDate.of(2025, 9, 17));
            UNCaddPurchases(ps, "cc1e8400-e29b-41d4-a716-446655440013", 2.64, 1.0, LocalDate.of(2025, 9, 20));

            // --- Butter ---
            UNCaddPurchases(ps, "dd1e8400-e29b-41d4-a716-446655440014", 6.50, 1.0, LocalDate.of(2023, 10, 29));
            UNCaddPurchases(ps, "dd1e8400-e29b-41d4-a716-446655440014", 6.51, 1.0, LocalDate.of(2023, 11, 1));
            UNCaddPurchases(ps, "dd1e8400-e29b-41d4-a716-446655440014", 6.53, 1.0, LocalDate.of(2023, 12, 31));
            UNCaddPurchases(ps, "dd1e8400-e29b-41d4-a716-446655440014", 6.55, 1.0, LocalDate.of(2024, 2, 29));
            UNCaddPurchases(ps, "dd1e8400-e29b-41d4-a716-446655440014", 6.57, 1.0, LocalDate.of(2024, 3, 7));
            UNCaddPurchases(ps, "dd1e8400-e29b-41d4-a716-446655440014", 6.60, 1.0, LocalDate.of(2024, 3, 14));
            UNCaddPurchases(ps, "dd1e8400-e29b-41d4-a716-446655440014", 6.62, 1.0, LocalDate.of(2024, 3, 17));
            UNCaddPurchases(ps, "dd1e8400-e29b-41d4-a716-446655440014", 6.64, 1.0, LocalDate.of(2024, 3, 20));
            UNCaddPurchases(ps, "dd1e8400-e29b-41d4-a716-446655440014", 6.66, 1.0, LocalDate.of(2024, 3, 27));
            UNCaddPurchases(ps, "dd1e8400-e29b-41d4-a716-446655440014", 6.67, 1.0, LocalDate.of(2024, 4, 3));
            UNCaddPurchases(ps, "dd1e8400-e29b-41d4-a716-446655440014", 6.68, 1.0, LocalDate.of(2024, 6, 2));
            UNCaddPurchases(ps, "dd1e8400-e29b-41d4-a716-446655440014", 6.70, 1.0, LocalDate.of(2024, 7, 2));
            UNCaddPurchases(ps, "dd1e8400-e29b-41d4-a716-446655440014", 6.71, 1.0, LocalDate.of(2024, 7, 9));
            UNCaddPurchases(ps, "dd1e8400-e29b-41d4-a716-446655440014", 6.73, 1.0, LocalDate.of(2024, 9, 7));
            UNCaddPurchases(ps, "dd1e8400-e29b-41d4-a716-446655440014", 6.75, 1.0, LocalDate.of(2024, 9, 14));
            UNCaddPurchases(ps, "dd1e8400-e29b-41d4-a716-446655440014", 6.76, 1.0, LocalDate.of(2024, 9, 17));
            UNCaddPurchases(ps, "dd1e8400-e29b-41d4-a716-446655440014", 6.79, 1.0, LocalDate.of(2024, 9, 20));
            UNCaddPurchases(ps, "dd1e8400-e29b-41d4-a716-446655440014", 6.80, 1.0, LocalDate.of(2024, 10, 20));
            UNCaddPurchases(ps, "dd1e8400-e29b-41d4-a716-446655440014", 6.81, 1.0, LocalDate.of(2024, 10, 27));
            UNCaddPurchases(ps, "dd1e8400-e29b-41d4-a716-446655440014", 6.83, 1.0, LocalDate.of(2024, 11, 3));
            UNCaddPurchases(ps, "dd1e8400-e29b-41d4-a716-446655440014", 6.85, 1.0, LocalDate.of(2024, 12, 3));
            UNCaddPurchases(ps, "dd1e8400-e29b-41d4-a716-446655440014", 6.87, 1.0, LocalDate.of(2024, 12, 6));
            UNCaddPurchases(ps, "dd1e8400-e29b-41d4-a716-446655440014", 6.89, 1.0, LocalDate.of(2025, 1, 5));
            UNCaddPurchases(ps, "dd1e8400-e29b-41d4-a716-446655440014", 6.91, 1.0, LocalDate.of(2025, 2, 4));
            UNCaddPurchases(ps, "dd1e8400-e29b-41d4-a716-446655440014", 6.94, 1.0, LocalDate.of(2025, 2, 11));
            UNCaddPurchases(ps, "dd1e8400-e29b-41d4-a716-446655440014", 6.95, 1.0, LocalDate.of(2025, 4, 12));
            UNCaddPurchases(ps, "dd1e8400-e29b-41d4-a716-446655440014", 6.96, 1.0, LocalDate.of(2025, 4, 15));
            UNCaddPurchases(ps, "dd1e8400-e29b-41d4-a716-446655440014", 6.98, 1.0, LocalDate.of(2025, 5, 15));
            UNCaddPurchases(ps, "dd1e8400-e29b-41d4-a716-446655440014", 6.99, 1.0, LocalDate.of(2025, 6, 14));
            UNCaddPurchases(ps, "dd1e8400-e29b-41d4-a716-446655440014", 7.01, 1.0, LocalDate.of(2025, 6, 17));
            UNCaddPurchases(ps, "dd1e8400-e29b-41d4-a716-446655440014", 7.03, 1.0, LocalDate.of(2025, 6, 20));
            UNCaddPurchases(ps, "dd1e8400-e29b-41d4-a716-446655440014", 7.05, 1.0, LocalDate.of(2025, 6, 23));
            UNCaddPurchases(ps, "dd1e8400-e29b-41d4-a716-446655440014", 7.08, 1.0, LocalDate.of(2025, 6, 30));
            UNCaddPurchases(ps, "dd1e8400-e29b-41d4-a716-446655440014", 7.10, 1.0, LocalDate.of(2025, 7, 7));
            UNCaddPurchases(ps, "dd1e8400-e29b-41d4-a716-446655440014", 7.11, 1.0, LocalDate.of(2025, 8, 6));
            UNCaddPurchases(ps, "dd1e8400-e29b-41d4-a716-446655440014", 7.14, 1.0, LocalDate.of(2025, 8, 9));
            UNCaddPurchases(ps, "dd1e8400-e29b-41d4-a716-446655440014", 7.15, 1.0, LocalDate.of(2025, 9, 8));

            // --- Orange Juice ---
            UNCaddPurchases(ps, "ee1e8400-e29b-41d4-a716-446655440015", 5.00, 1.0, LocalDate.of(2023, 10, 29));
            UNCaddPurchases(ps, "ee1e8400-e29b-41d4-a716-446655440015", 5.01, 1.0, LocalDate.of(2023, 12, 28));
            UNCaddPurchases(ps, "ee1e8400-e29b-41d4-a716-446655440015", 5.03, 1.0, LocalDate.of(2024, 1, 27));
            UNCaddPurchases(ps, "ee1e8400-e29b-41d4-a716-446655440015", 5.04, 1.0, LocalDate.of(2024, 1, 30));
            UNCaddPurchases(ps, "ee1e8400-e29b-41d4-a716-446655440015", 5.06, 1.0, LocalDate.of(2024, 2, 6));
            UNCaddPurchases(ps, "ee1e8400-e29b-41d4-a716-446655440015", 5.07, 1.0, LocalDate.of(2024, 4, 6));
            UNCaddPurchases(ps, "ee1e8400-e29b-41d4-a716-446655440015", 5.09, 1.0, LocalDate.of(2024, 4, 9));
            UNCaddPurchases(ps, "ee1e8400-e29b-41d4-a716-446655440015", 5.10, 1.0, LocalDate.of(2024, 6, 8));
            UNCaddPurchases(ps, "ee1e8400-e29b-41d4-a716-446655440015", 5.12, 1.0, LocalDate.of(2024, 8, 7));
            UNCaddPurchases(ps, "ee1e8400-e29b-41d4-a716-446655440015", 5.14, 1.0, LocalDate.of(2024, 8, 10));
            UNCaddPurchases(ps, "ee1e8400-e29b-41d4-a716-446655440015", 5.15, 1.0, LocalDate.of(2024, 8, 13));
            UNCaddPurchases(ps, "ee1e8400-e29b-41d4-a716-446655440015", 5.17, 1.0, LocalDate.of(2024, 9, 12));
            UNCaddPurchases(ps, "ee1e8400-e29b-41d4-a716-446655440015", 5.19, 1.0, LocalDate.of(2024, 11, 11));
            UNCaddPurchases(ps, "ee1e8400-e29b-41d4-a716-446655440015", 5.20, 1.0, LocalDate.of(2024, 12, 11));
            UNCaddPurchases(ps, "ee1e8400-e29b-41d4-a716-446655440015", 5.21, 1.0, LocalDate.of(2024, 12, 14));
            UNCaddPurchases(ps, "ee1e8400-e29b-41d4-a716-446655440015", 5.22, 1.0, LocalDate.of(2024, 12, 17));
            UNCaddPurchases(ps, "ee1e8400-e29b-41d4-a716-446655440015", 5.24, 1.0, LocalDate.of(2025, 1, 16));
            UNCaddPurchases(ps, "ee1e8400-e29b-41d4-a716-446655440015", 5.25, 1.0, LocalDate.of(2025, 1, 19));
            UNCaddPurchases(ps, "ee1e8400-e29b-41d4-a716-446655440015", 5.27, 1.0, LocalDate.of(2025, 3, 20));
            UNCaddPurchases(ps, "ee1e8400-e29b-41d4-a716-446655440015", 5.28, 1.0, LocalDate.of(2025, 3, 27));
            UNCaddPurchases(ps, "ee1e8400-e29b-41d4-a716-446655440015", 5.30, 1.0, LocalDate.of(2025, 3, 30));
            UNCaddPurchases(ps, "ee1e8400-e29b-41d4-a716-446655440015", 5.31, 1.0, LocalDate.of(2025, 4, 6));
            UNCaddPurchases(ps, "ee1e8400-e29b-41d4-a716-446655440015", 5.33, 1.0, LocalDate.of(2025, 4, 9));
            UNCaddPurchases(ps, "ee1e8400-e29b-41d4-a716-446655440015", 5.35, 1.0, LocalDate.of(2025, 5, 9));
            UNCaddPurchases(ps, "ee1e8400-e29b-41d4-a716-446655440015", 5.37, 1.0, LocalDate.of(2025, 5, 16));
            UNCaddPurchases(ps, "ee1e8400-e29b-41d4-a716-446655440015", 5.38, 1.0, LocalDate.of(2025, 5, 23));
            UNCaddPurchases(ps, "ee1e8400-e29b-41d4-a716-446655440015", 5.39, 1.0, LocalDate.of(2025, 7, 22));
            UNCaddPurchases(ps, "ee1e8400-e29b-41d4-a716-446655440015", 5.40, 1.0, LocalDate.of(2025, 9, 20));
            UNCaddPurchases(ps, "ee1e8400-e29b-41d4-a716-446655440015", 5.42, 1.0, LocalDate.of(2025, 9, 23));
            UNCaddPurchases(ps, "ee1e8400-e29b-41d4-a716-446655440015", 5.44, 1.0, LocalDate.of(2025, 10, 23));

            // --- Cereal ---
            UNCaddPurchases(ps, "ff1e8400-e29b-41d4-a716-446655440016", 8.00, 1.0, LocalDate.of(2023, 10, 29));
            UNCaddPurchases(ps, "ff1e8400-e29b-41d4-a716-446655440016", 8.01, 1.0, LocalDate.of(2023, 11, 28));
            UNCaddPurchases(ps, "ff1e8400-e29b-41d4-a716-446655440016", 8.02, 1.0, LocalDate.of(2023, 12, 28));
            UNCaddPurchases(ps, "ff1e8400-e29b-41d4-a716-446655440016", 8.04, 1.0, LocalDate.of(2023, 12, 31));
            UNCaddPurchases(ps, "ff1e8400-e29b-41d4-a716-446655440016", 8.06, 1.0, LocalDate.of(2024, 2, 29));
            UNCaddPurchases(ps, "ff1e8400-e29b-41d4-a716-446655440016", 8.08, 1.0, LocalDate.of(2024, 3, 7));
            UNCaddPurchases(ps, "ff1e8400-e29b-41d4-a716-446655440016", 8.10, 1.0, LocalDate.of(2024, 3, 10));
            UNCaddPurchases(ps, "ff1e8400-e29b-41d4-a716-446655440016", 8.12, 1.0, LocalDate.of(2024, 3, 17));
            UNCaddPurchases(ps, "ff1e8400-e29b-41d4-a716-446655440016", 8.14, 1.0, LocalDate.of(2024, 4, 16));
            UNCaddPurchases(ps, "ff1e8400-e29b-41d4-a716-446655440016", 8.15, 1.0, LocalDate.of(2024, 5, 16));
            UNCaddPurchases(ps, "ff1e8400-e29b-41d4-a716-446655440016", 8.16, 1.0, LocalDate.of(2024, 7, 15));
            UNCaddPurchases(ps, "ff1e8400-e29b-41d4-a716-446655440016", 8.18, 1.0, LocalDate.of(2024, 8, 14));
            UNCaddPurchases(ps, "ff1e8400-e29b-41d4-a716-446655440016", 8.20, 1.0, LocalDate.of(2024, 9, 13));
            UNCaddPurchases(ps, "ff1e8400-e29b-41d4-a716-446655440016", 8.21, 1.0, LocalDate.of(2024, 11, 12));
            UNCaddPurchases(ps, "ff1e8400-e29b-41d4-a716-446655440016", 8.24, 1.0, LocalDate.of(2024, 11, 15));
            UNCaddPurchases(ps, "ff1e8400-e29b-41d4-a716-446655440016", 8.26, 1.0, LocalDate.of(2024, 11, 22));
            UNCaddPurchases(ps, "ff1e8400-e29b-41d4-a716-446655440016", 8.27, 1.0, LocalDate.of(2025, 1, 21));
            UNCaddPurchases(ps, "ff1e8400-e29b-41d4-a716-446655440016", 8.31, 1.0, LocalDate.of(2025, 1, 28));
            UNCaddPurchases(ps, "ff1e8400-e29b-41d4-a716-446655440016", 8.33, 1.0, LocalDate.of(2025, 2, 27));
            UNCaddPurchases(ps, "ff1e8400-e29b-41d4-a716-446655440016", 8.35, 1.0, LocalDate.of(2025, 4, 28));
            UNCaddPurchases(ps, "ff1e8400-e29b-41d4-a716-446655440016", 8.36, 1.0, LocalDate.of(2025, 5, 5));
            UNCaddPurchases(ps, "ff1e8400-e29b-41d4-a716-446655440016", 8.39, 1.0, LocalDate.of(2025, 5, 12));
            UNCaddPurchases(ps, "ff1e8400-e29b-41d4-a716-446655440016", 8.42, 1.0, LocalDate.of(2025, 5, 15));
            UNCaddPurchases(ps, "ff1e8400-e29b-41d4-a716-446655440016", 8.44, 1.0, LocalDate.of(2025, 5, 22));
            UNCaddPurchases(ps, "ff1e8400-e29b-41d4-a716-446655440016", 8.47, 1.0, LocalDate.of(2025, 5, 29));
            UNCaddPurchases(ps, "ff1e8400-e29b-41d4-a716-446655440016", 8.48, 1.0, LocalDate.of(2025, 7, 28));
            UNCaddPurchases(ps, "ff1e8400-e29b-41d4-a716-446655440016", 8.51, 1.0, LocalDate.of(2025, 7, 31));
            UNCaddPurchases(ps, "ff1e8400-e29b-41d4-a716-446655440016", 8.54, 1.0, LocalDate.of(2025, 9, 29));
            UNCaddPurchases(ps, "ff1e8400-e29b-41d4-a716-446655440016", 8.57, 1.0, LocalDate.of(2025, 10, 2));
            UNCaddPurchases(ps, "ff1e8400-e29b-41d4-a716-446655440016", 8.58, 1.0, LocalDate.of(2025, 10, 9));

            // --- Yogurt ---
            UNCaddPurchases(ps, "aa2e8400-e29b-41d4-a716-446655440017", 4.50, 1.0, LocalDate.of(2023, 10, 29));
            UNCaddPurchases(ps, "aa2e8400-e29b-41d4-a716-446655440017", 4.52, 1.0, LocalDate.of(2023, 11, 1));
            UNCaddPurchases(ps, "aa2e8400-e29b-41d4-a716-446655440017", 4.53, 1.0, LocalDate.of(2023, 11, 8));
            UNCaddPurchases(ps, "aa2e8400-e29b-41d4-a716-446655440017", 4.54, 1.0, LocalDate.of(2023, 12, 8));
            UNCaddPurchases(ps, "aa2e8400-e29b-41d4-a716-446655440017", 4.54, 1.0, LocalDate.of(2024, 1, 7));
            UNCaddPurchases(ps, "aa2e8400-e29b-41d4-a716-446655440017", 4.56, 1.0, LocalDate.of(2024, 1, 10));
            UNCaddPurchases(ps, "aa2e8400-e29b-41d4-a716-446655440017", 4.56, 1.0, LocalDate.of(2024, 2, 9));
            UNCaddPurchases(ps, "aa2e8400-e29b-41d4-a716-446655440017", 4.57, 1.0, LocalDate.of(2024, 4, 9));
            UNCaddPurchases(ps, "aa2e8400-e29b-41d4-a716-446655440017", 4.59, 1.0, LocalDate.of(2024, 4, 12));
            UNCaddPurchases(ps, "aa2e8400-e29b-41d4-a716-446655440017", 4.59, 1.0, LocalDate.of(2024, 6, 11));
            UNCaddPurchases(ps, "aa2e8400-e29b-41d4-a716-446655440017", 4.60, 1.0, LocalDate.of(2024, 6, 14));
            UNCaddPurchases(ps, "aa2e8400-e29b-41d4-a716-446655440017", 4.62, 1.0, LocalDate.of(2024, 6, 17));
            UNCaddPurchases(ps, "aa2e8400-e29b-41d4-a716-446655440017", 4.63, 1.0, LocalDate.of(2024, 8, 16));
            UNCaddPurchases(ps, "aa2e8400-e29b-41d4-a716-446655440017", 4.63, 1.0, LocalDate.of(2024, 10, 15));
            UNCaddPurchases(ps, "aa2e8400-e29b-41d4-a716-446655440017", 4.64, 1.0, LocalDate.of(2024, 10, 18));
            UNCaddPurchases(ps, "aa2e8400-e29b-41d4-a716-446655440017", 4.65, 1.0, LocalDate.of(2024, 10, 25));
            UNCaddPurchases(ps, "aa2e8400-e29b-41d4-a716-446655440017", 4.65, 1.0, LocalDate.of(2024, 11, 24));
            UNCaddPurchases(ps, "aa2e8400-e29b-41d4-a716-446655440017", 4.67, 1.0, LocalDate.of(2024, 12, 24));
            UNCaddPurchases(ps, "aa2e8400-e29b-41d4-a716-446655440017", 4.68, 1.0, LocalDate.of(2024, 12, 31));
            UNCaddPurchases(ps, "aa2e8400-e29b-41d4-a716-446655440017", 4.69, 1.0, LocalDate.of(2025, 1, 30));
            UNCaddPurchases(ps, "aa2e8400-e29b-41d4-a716-446655440017", 4.71, 1.0, LocalDate.of(2025, 2, 6));
            UNCaddPurchases(ps, "aa2e8400-e29b-41d4-a716-446655440017", 4.72, 1.0, LocalDate.of(2025, 4, 7));
            UNCaddPurchases(ps, "aa2e8400-e29b-41d4-a716-446655440017", 4.74, 1.0, LocalDate.of(2025, 5, 7));
            UNCaddPurchases(ps, "aa2e8400-e29b-41d4-a716-446655440017", 4.75, 1.0, LocalDate.of(2025, 5, 14));
            UNCaddPurchases(ps, "aa2e8400-e29b-41d4-a716-446655440017", 4.76, 1.0, LocalDate.of(2025, 6, 13));
            UNCaddPurchases(ps, "aa2e8400-e29b-41d4-a716-446655440017", 4.77, 1.0, LocalDate.of(2025, 7, 13));
            UNCaddPurchases(ps, "aa2e8400-e29b-41d4-a716-446655440017", 4.78, 1.0, LocalDate.of(2025, 8, 12));
            UNCaddPurchases(ps, "aa2e8400-e29b-41d4-a716-446655440017", 4.79, 1.0, LocalDate.of(2025, 10, 11));
            UNCaddPurchases(ps, "aa2e8400-e29b-41d4-a716-446655440017", 4.81, 1.0, LocalDate.of(2025, 10, 14));
            UNCaddPurchases(ps, "aa2e8400-e29b-41d4-a716-446655440017", 4.82, 1.0, LocalDate.of(2025, 10, 21));

            // --- Soap ---
            UNCaddPurchases(ps, "bb2e8400-e29b-41d4-a716-446655440018", 3.50, 1.0, LocalDate.of(2023, 10, 29));
            UNCaddPurchases(ps, "bb2e8400-e29b-41d4-a716-446655440018", 3.51, 1.0, LocalDate.of(2023, 11, 1));
            UNCaddPurchases(ps, "bb2e8400-e29b-41d4-a716-446655440018", 3.52, 1.0, LocalDate.of(2023, 11, 8));
            UNCaddPurchases(ps, "bb2e8400-e29b-41d4-a716-446655440018", 3.53, 1.0, LocalDate.of(2023, 11, 11));
            UNCaddPurchases(ps, "bb2e8400-e29b-41d4-a716-446655440018", 3.54, 1.0, LocalDate.of(2024, 1, 10));
            UNCaddPurchases(ps, "bb2e8400-e29b-41d4-a716-446655440018", 3.55, 1.0, LocalDate.of(2024, 3, 10));
            UNCaddPurchases(ps, "bb2e8400-e29b-41d4-a716-446655440018", 3.55, 1.0, LocalDate.of(2024, 4, 9));
            UNCaddPurchases(ps, "bb2e8400-e29b-41d4-a716-446655440018", 3.56, 1.0, LocalDate.of(2024, 5, 9));
            UNCaddPurchases(ps, "bb2e8400-e29b-41d4-a716-446655440018", 3.57, 1.0, LocalDate.of(2024, 5, 12));
            UNCaddPurchases(ps, "bb2e8400-e29b-41d4-a716-446655440018", 3.58, 1.0, LocalDate.of(2024, 7, 11));
            UNCaddPurchases(ps, "bb2e8400-e29b-41d4-a716-446655440018", 3.58, 1.0, LocalDate.of(2024, 8, 10));
            UNCaddPurchases(ps, "bb2e8400-e29b-41d4-a716-446655440018", 3.59, 1.0, LocalDate.of(2024, 8, 17));
            UNCaddPurchases(ps, "bb2e8400-e29b-41d4-a716-446655440018", 3.60, 1.0, LocalDate.of(2024, 10, 16));
            UNCaddPurchases(ps, "bb2e8400-e29b-41d4-a716-446655440018", 3.61, 1.0, LocalDate.of(2024, 12, 15));
            UNCaddPurchases(ps, "bb2e8400-e29b-41d4-a716-446655440018", 3.61, 1.0, LocalDate.of(2025, 1, 14));
            UNCaddPurchases(ps, "bb2e8400-e29b-41d4-a716-446655440018", 3.62, 1.0, LocalDate.of(2025, 2, 13));
            UNCaddPurchases(ps, "bb2e8400-e29b-41d4-a716-446655440018", 3.63, 1.0, LocalDate.of(2025, 2, 20));
            UNCaddPurchases(ps, "bb2e8400-e29b-41d4-a716-446655440018", 3.64, 1.0, LocalDate.of(2025, 4, 21));
            UNCaddPurchases(ps, "bb2e8400-e29b-41d4-a716-446655440018", 3.65, 1.0, LocalDate.of(2025, 4, 28));
            UNCaddPurchases(ps, "bb2e8400-e29b-41d4-a716-446655440018", 3.66, 1.0, LocalDate.of(2025, 6, 27));
            UNCaddPurchases(ps, "bb2e8400-e29b-41d4-a716-446655440018", 3.67, 1.0, LocalDate.of(2025, 6, 30));
            UNCaddPurchases(ps, "bb2e8400-e29b-41d4-a716-446655440018", 3.67, 1.0, LocalDate.of(2025, 7, 3));
            UNCaddPurchases(ps, "bb2e8400-e29b-41d4-a716-446655440018", 3.69, 1.0, LocalDate.of(2025, 7, 10));
            UNCaddPurchases(ps, "bb2e8400-e29b-41d4-a716-446655440018", 3.69, 1.0, LocalDate.of(2025, 8, 9));
            UNCaddPurchases(ps, "bb2e8400-e29b-41d4-a716-446655440018", 3.70, 1.0, LocalDate.of(2025, 8, 12));
            UNCaddPurchases(ps, "bb2e8400-e29b-41d4-a716-446655440018", 3.71, 1.0, LocalDate.of(2025, 9, 11));
            UNCaddPurchases(ps, "bb2e8400-e29b-41d4-a716-446655440018", 3.72, 1.0, LocalDate.of(2025, 10, 11));
            UNCaddPurchases(ps, "bb2e8400-e29b-41d4-a716-446655440018", 3.73, 1.0, LocalDate.of(2025, 10, 14));

            // --- Laundry Detergent ---
            UNCaddPurchases(ps, "cc2e8400-e29b-41d4-a716-446655440019", 14.00, 1.0, LocalDate.of(2023, 10, 29));
            UNCaddPurchases(ps, "cc2e8400-e29b-41d4-a716-446655440019", 14.03, 1.0, LocalDate.of(2023, 11, 1));
            UNCaddPurchases(ps, "cc2e8400-e29b-41d4-a716-446655440019", 14.05, 1.0, LocalDate.of(2023, 12, 1));
            UNCaddPurchases(ps, "cc2e8400-e29b-41d4-a716-446655440019", 14.10, 1.0, LocalDate.of(2023, 12, 8));
            UNCaddPurchases(ps, "cc2e8400-e29b-41d4-a716-446655440019", 14.13, 1.0, LocalDate.of(2023, 12, 15));
            UNCaddPurchases(ps, "cc2e8400-e29b-41d4-a716-446655440019", 14.18, 1.0, LocalDate.of(2024, 1, 14));
            UNCaddPurchases(ps, "cc2e8400-e29b-41d4-a716-446655440019", 14.22, 1.0, LocalDate.of(2024, 3, 14));
            UNCaddPurchases(ps, "cc2e8400-e29b-41d4-a716-446655440019", 14.26, 1.0, LocalDate.of(2024, 3, 21));
            UNCaddPurchases(ps, "cc2e8400-e29b-41d4-a716-446655440019", 14.31, 1.0, LocalDate.of(2024, 3, 28));
            UNCaddPurchases(ps, "cc2e8400-e29b-41d4-a716-446655440019", 14.33, 1.0, LocalDate.of(2024, 4, 4));
            UNCaddPurchases(ps, "cc2e8400-e29b-41d4-a716-446655440019", 14.38, 1.0, LocalDate.of(2024, 6, 3));
            UNCaddPurchases(ps, "cc2e8400-e29b-41d4-a716-446655440019", 14.43, 1.0, LocalDate.of(2024, 7, 3));
            UNCaddPurchases(ps, "cc2e8400-e29b-41d4-a716-446655440019", 14.45, 1.0, LocalDate.of(2024, 8, 2));
            UNCaddPurchases(ps, "cc2e8400-e29b-41d4-a716-446655440019", 14.49, 1.0, LocalDate.of(2024, 9, 1));
            UNCaddPurchases(ps, "cc2e8400-e29b-41d4-a716-446655440019", 14.53, 1.0, LocalDate.of(2024, 9, 8));
            UNCaddPurchases(ps, "cc2e8400-e29b-41d4-a716-446655440019", 14.56, 1.0, LocalDate.of(2024, 11, 7));
            UNCaddPurchases(ps, "cc2e8400-e29b-41d4-a716-446655440019", 14.59, 1.0, LocalDate.of(2024, 12, 7));
            UNCaddPurchases(ps, "cc2e8400-e29b-41d4-a716-446655440019", 14.62, 1.0, LocalDate.of(2024, 12, 14));
            UNCaddPurchases(ps, "cc2e8400-e29b-41d4-a716-446655440019", 14.63, 1.0, LocalDate.of(2024, 12, 21));
            UNCaddPurchases(ps, "cc2e8400-e29b-41d4-a716-446655440019", 14.68, 1.0, LocalDate.of(2025, 1, 20));
            UNCaddPurchases(ps, "cc2e8400-e29b-41d4-a716-446655440019", 14.73, 1.0, LocalDate.of(2025, 3, 21));
            UNCaddPurchases(ps, "cc2e8400-e29b-41d4-a716-446655440019", 14.75, 1.0, LocalDate.of(2025, 3, 24));
            UNCaddPurchases(ps, "cc2e8400-e29b-41d4-a716-446655440019", 14.79, 1.0, LocalDate.of(2025, 3, 27));
            UNCaddPurchases(ps, "cc2e8400-e29b-41d4-a716-446655440019", 14.83, 1.0, LocalDate.of(2025, 4, 3));
            UNCaddPurchases(ps, "cc2e8400-e29b-41d4-a716-446655440019", 14.88, 1.0, LocalDate.of(2025, 6, 2));
            UNCaddPurchases(ps, "cc2e8400-e29b-41d4-a716-446655440019", 14.92, 1.0, LocalDate.of(2025, 6, 9));
            UNCaddPurchases(ps, "cc2e8400-e29b-41d4-a716-446655440019", 14.94, 1.0, LocalDate.of(2025, 6, 16));
            UNCaddPurchases(ps, "cc2e8400-e29b-41d4-a716-446655440019", 14.98, 1.0, LocalDate.of(2025, 6, 23));
            UNCaddPurchases(ps, "cc2e8400-e29b-41d4-a716-446655440019", 15.02, 1.0, LocalDate.of(2025, 6, 26));
            UNCaddPurchases(ps, "cc2e8400-e29b-41d4-a716-446655440019", 15.08, 1.0, LocalDate.of(2025, 7, 3));
            UNCaddPurchases(ps, "cc2e8400-e29b-41d4-a716-446655440019", 15.13, 1.0, LocalDate.of(2025, 7, 6));
            UNCaddPurchases(ps, "cc2e8400-e29b-41d4-a716-446655440019", 15.15, 1.0, LocalDate.of(2025, 7, 13));
            UNCaddPurchases(ps, "cc2e8400-e29b-41d4-a716-446655440019", 15.21, 1.0, LocalDate.of(2025, 8, 12));
            UNCaddPurchases(ps, "cc2e8400-e29b-41d4-a716-446655440019", 15.25, 1.0, LocalDate.of(2025, 10, 11));

            
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
