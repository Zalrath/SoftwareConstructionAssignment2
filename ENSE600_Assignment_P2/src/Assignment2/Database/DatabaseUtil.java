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

            UNCaddItem(ps, "fe10d506-1e42-4791-b7f9-094a81f1b50d", "Milk", LocalDate.of(2025, 10, 26), 3.0, 0, "dairy|fridge|staple|breakfast", false, false);
            UNCaddItem(ps, "4d9a5e43-0c98-4956-aac5-d206fbb1c600", "Bread", LocalDate.of(2025, 5, 29), 5.0, 0, "bakery|breakfast|staple|pantry", false, false);
            UNCaddItem(ps, "31bdcd6b-a116-4eaf-a0bc-9a8e7287976b", "Eggs", LocalDate.of(2025, 2, 28), 2.0, 0, "protein|fridge|breakfast|baking", false, false);
            UNCaddItem(ps, "738d56b4-952f-4748-8e63-966c30d23265", "Butter", LocalDate.of(2024, 11, 8), 8.0, 0, "dairy|fridge|baking|staple", false, false);
            UNCaddItem(ps, "98d7bd17-3fa7-403a-ae36-c25db7a72c39", "Cheese", LocalDate.of(2025, 9, 7), 8.0, 0, "dairy|fridge|snack|staple", false, false);
            UNCaddItem(ps, "e1685c2b-aab5-4b1c-87da-fb75c257fd76", "Chicken Breast", LocalDate.of(2024, 10, 28), 7.0, 0, "meat|protein|freezer|dinner", false, false);
            UNCaddItem(ps, "a8d397b0-e695-4b50-99b1-b2eb57277a79", "Mince", LocalDate.of(2025, 7, 23), 7.0, 0, "meat|protein|freezer|staple", false, false);
            UNCaddItem(ps, "f9809697-3a71-4d52-8347-f0950ab7c5d6", "Fish Fillets", LocalDate.of(2025, 4, 4), 5.0, 0, "seafood|protein|freezer|dinner", false, false);
            UNCaddItem(ps, "0667113c-7559-4629-b577-e9741f43ef5a", "Pasta", LocalDate.of(2025, 7, 7), 3.0, 0, "grain|pantry|staple|dinner", false, false);
            UNCaddItem(ps, "fe156ac3-7131-4188-830b-d6a4fa030e8f", "Rice", LocalDate.of(2025, 6, 9), 9.0, 0, "grain|pantry|side|staple", false, false);
            UNCaddItem(ps, "6ca597ad-f42f-4ed0-8c4e-81572e58246a", "Apples", LocalDate.of(2025, 1, 5), 3.0, 0, "fruit|fresh|snack|produce", false, false);
            UNCaddItem(ps, "ccd80ec8-614b-4968-8aec-04a1fa6ce68d", "Bananas", LocalDate.of(2025, 1, 13), 10.0, 0, "fruit|snack|breakfast|produce", false, false);
            UNCaddItem(ps, "b9840c75-9807-49ea-8325-75c92e5a902f", "Onions", LocalDate.of(2025, 8, 19), 1.0, 0, "vegetable|cooking|staple|pantry", false, false);
            UNCaddItem(ps, "43ae807b-0938-4b7a-b1df-c62308ee283e", "Potatoes", LocalDate.of(2025, 4, 16), 10.0, 0, "vegetable|staple|side|produce", false, false);
            UNCaddItem(ps, "6a52d8e3-7cb0-45a5-ad58-f9a10e836baf", "Tomatoes", LocalDate.of(2025, 6, 29), 3.0, 0, "vegetable|fresh|produce|sauce", false, false);
            UNCaddItem(ps, "8e0ca1ee-df41-411b-8591-9be7e4b34125", "Lettuce", LocalDate.of(2025, 8, 5), 2.0, 0, "vegetable|fresh|produce|salad", false, false);
            UNCaddItem(ps, "578e3ad4-097a-453e-aab1-d5abd951f25a", "Carrots", LocalDate.of(2025, 6, 23), 1.0, 0, "vegetable|fresh|produce|snack", false, false);
            UNCaddItem(ps, "ac926a1e-780e-48e5-8dcd-06fe9d204c5c", "Coffee", LocalDate.of(2025, 10, 24), 1.0, 0, "beverage|morning|pantry|stimulant", false, false);
            UNCaddItem(ps, "dd143324-3031-40f1-b8f7-b90f946e2b99", "Sugar", LocalDate.of(2025, 4, 16), 4.0, 0, "baking|sweetener|pantry|staple", false, false);
            UNCaddItem(ps, "c7c4c1e4-a960-4db7-bab9-15b8521157b9", "Cooking Oil", LocalDate.of(2025, 8, 7), 10.0, 0, "cooking|pantry|oil|staple", false, false);
            UNCaddItem(ps, "abefa615-4f4d-4a4e-b882-4a6c33eb1198", "Laundry Powder", LocalDate.of(2025, 1, 18), 10.0, 0, "household|cleaning|bulk|laundry", false, false);
            UNCaddItem(ps, "9a9c7178-7fa9-4114-92db-3e08dbb21026", "Dish Soap", LocalDate.of(2025, 2, 10), 4.0, 0, "household|cleaning|kitchen|bulk", false, false);
            UNCaddItem(ps, "d23f242b-91ac-446f-b38b-a4b0214baefa", "Olive Oil", LocalDate.of(2025, 1, 7), 9.0, 0, "cooking|pantry|oil|healthy", false, false);
            UNCaddItem(ps, "3e7d1add-bf1f-40a5-9de6-45a3bf0045c2", "Flour", LocalDate.of(2025, 4, 14), 7.0, 0, "baking|pantry|dry|ingredient", false, false);
            UNCaddItem(ps, "ec86defe-7548-4031-8a76-d9ce3c551c9b", "Baking Powder", LocalDate.of(2025, 2, 5), 2.0, 0, "baking|ingredient|pantry|staple", false, false);
            UNCaddItem(ps, "9350a653-d57c-45d2-ba03-0c4835c7a31d", "Hot Sauce", LocalDate.of(2025, 3, 29), 7.0, 0, "condiment|spicy|pantry|flavour", false, false);
            UNCaddItem(ps, "e631be2c-9959-40f8-9695-c7bbd8966e2d", "Tissues", LocalDate.of(2025, 7, 18), 5.0, 0, "household|bathroom|hygiene|bulk", false, false);
            UNCaddItem(ps, "2f4c9492-0947-44b0-8f6c-d1bae3d523e6", "Acrylic Paint (Red)", LocalDate.of(2025, 3, 31), 3.0, 0, "art|paint|acrylic|supplies|hobby", false, false);
            UNCaddItem(ps, "0953287f-0a6d-4196-8e34-e5aa7c151266", "Acrylic Paint (Blue)", LocalDate.of(2025, 9, 7), 1.0, 0, "art|paint|acrylic|supplies|hobby", false, false);
            UNCaddItem(ps, "beb1504d-c67c-481f-8bb2-27961dc36946", "Acrylic Paint (Yellow)", LocalDate.of(2025, 6, 7), 2.0, 0, "art|paint|acrylic|supplies|hobby", false, false);
            UNCaddItem(ps, "81f475a9-0579-43be-85cc-15de4fe6a714", "Oil Paint (White)", LocalDate.of(2024, 11, 5), 3.0, 0, "art|paint|oil|supplies|hobby", false, false);
            UNCaddItem(ps, "a3e279ea-080d-4762-814f-9ba4c4293397", "Paint Brushes (Small)", LocalDate.of(2025, 3, 1), 3.0, 0, "art|brush|tool|supplies|hobby", false, false);
            UNCaddItem(ps, "eb38da48-390e-4673-a0de-a99a37bed2da", "Paint Brushes (Medium)", LocalDate.of(2024, 10, 26), 2.0, 0, "art|brush|tool|supplies|hobby", false, false);
            UNCaddItem(ps, "a018eda9-831e-4515-b806-2631ca138c98", "Canvas Pack (Small)", LocalDate.of(2025, 4, 23), 1.0, 0, "art|canvas|surface|hobby", false, false);
            UNCaddItem(ps, "d4b7d16d-ddb2-4c96-981b-95d01b973355", "Canvas Pack (Large)", LocalDate.of(2025, 5, 23), 2.0, 0, "art|canvas|surface|hobby", false, false);
            UNCaddItem(ps, "9035c1bc-7a0d-44d4-a06a-934beca69276", "Paint Thinner", LocalDate.of(2024, 10, 23), 2.0, 0, "art|paint|solvent|tool|hobby", false, false);
            UNCaddItem(ps, "7fc847da-44b1-4817-9a0a-79703cff2a1c", "Palette Knife", LocalDate.of(2025, 10, 7), 2.0, 0, "art|tool|paint|mixing|hobby", false, false);

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

            UNCaddPurchases(ps, "ccd80ec8-614b-4968-8aec-04a1fa6ce68d", 3.49, 2.0, LocalDate.of(2024, 10, 1));
            UNCaddPurchases(ps, "6ca597ad-f42f-4ed0-8c4e-81572e58246a", 5.49, 1.0, LocalDate.of(2024, 10, 1));
            UNCaddPurchases(ps, "43ae807b-0938-4b7a-b1df-c62308ee283e", 6.49, 2.0, LocalDate.of(2024, 10, 1));
            UNCaddPurchases(ps, "d23f242b-91ac-446f-b38b-a4b0214baefa", 12.00, 1.0, LocalDate.of(2024, 10, 1));
            UNCaddPurchases(ps, "e1685c2b-aab5-4b1c-87da-fb75c257fd76", 16.00, 3.0, LocalDate.of(2024, 10, 1));
            UNCaddPurchases(ps, "dd143324-3031-40f1-b8f7-b90f946e2b99", 3.99, 3.0, LocalDate.of(2024, 10, 1));
            UNCaddPurchases(ps, "abefa615-4f4d-4a4e-b882-4a6c33eb1198", 11.99, 2.0, LocalDate.of(2024, 10, 1));
            UNCaddPurchases(ps, "578e3ad4-097a-453e-aab1-d5abd951f25a", 2.49, 1.0, LocalDate.of(2024, 10, 1));
            UNCaddPurchases(ps, "b9840c75-9807-49ea-8325-75c92e5a902f", 3.00, 3.0, LocalDate.of(2024, 10, 1));
            UNCaddPurchases(ps, "9a9c7178-7fa9-4114-92db-3e08dbb21026", 4.49, 1.0, LocalDate.of(2024, 10, 1));
            UNCaddPurchases(ps, "0667113c-7559-4629-b577-e9741f43ef5a", 3.49, 2.0, LocalDate.of(2024, 10, 1));
            UNCaddPurchases(ps, "ac926a1e-780e-48e5-8dcd-06fe9d204c5c", 12.99, 1.0, LocalDate.of(2024, 10, 1));
            UNCaddPurchases(ps, "4d9a5e43-0c98-4956-aac5-d206fbb1c600", 3.00, 1.0, LocalDate.of(2024, 10, 1));
            UNCaddPurchases(ps, "31bdcd6b-a116-4eaf-a0bc-9a8e7287976b", 6.49, 3.0, LocalDate.of(2024, 10, 5));
            UNCaddPurchases(ps, "0667113c-7559-4629-b577-e9741f43ef5a", 3.49, 1.0, LocalDate.of(2024, 10, 5));
            UNCaddPurchases(ps, "9350a653-d57c-45d2-ba03-0c4835c7a31d", 5.99, 1.0, LocalDate.of(2024, 10, 5));
            UNCaddPurchases(ps, "f9809697-3a71-4d52-8347-f0950ab7c5d6", 16.99, 3.0, LocalDate.of(2024, 10, 5));
            UNCaddPurchases(ps, "d23f242b-91ac-446f-b38b-a4b0214baefa", 10.49, 3.0, LocalDate.of(2024, 10, 5));
            UNCaddPurchases(ps, "b9840c75-9807-49ea-8325-75c92e5a902f", 3.00, 3.0, LocalDate.of(2024, 10, 8));
            UNCaddPurchases(ps, "ac926a1e-780e-48e5-8dcd-06fe9d204c5c", 11.99, 1.0, LocalDate.of(2024, 10, 8));
            UNCaddPurchases(ps, "ccd80ec8-614b-4968-8aec-04a1fa6ce68d", 3.00, 3.0, LocalDate.of(2024, 10, 8));
            UNCaddPurchases(ps, "e1685c2b-aab5-4b1c-87da-fb75c257fd76", 13.99, 1.0, LocalDate.of(2024, 10, 8));
            UNCaddPurchases(ps, "31bdcd6b-a116-4eaf-a0bc-9a8e7287976b", 6.49, 1.0, LocalDate.of(2024, 10, 8));
            UNCaddPurchases(ps, "d23f242b-91ac-446f-b38b-a4b0214baefa", 11.00, 2.0, LocalDate.of(2024, 10, 12));
            UNCaddPurchases(ps, "ccd80ec8-614b-4968-8aec-04a1fa6ce68d", 3.00, 2.0, LocalDate.of(2024, 10, 12));
            UNCaddPurchases(ps, "fe156ac3-7131-4188-830b-d6a4fa030e8f", 5.49, 3.0, LocalDate.of(2024, 10, 12));
            UNCaddPurchases(ps, "dd143324-3031-40f1-b8f7-b90f946e2b99", 3.49, 3.0, LocalDate.of(2024, 10, 12));
            UNCaddPurchases(ps, "8e0ca1ee-df41-411b-8591-9be7e4b34125", 3.99, 3.0, LocalDate.of(2024, 10, 12));
            UNCaddPurchases(ps, "e631be2c-9959-40f8-9695-c7bbd8966e2d", 4.49, 1.0, LocalDate.of(2024, 10, 15));
            UNCaddPurchases(ps, "e1685c2b-aab5-4b1c-87da-fb75c257fd76", 16.00, 2.0, LocalDate.of(2024, 10, 15));
            UNCaddPurchases(ps, "738d56b4-952f-4748-8e63-966c30d23265", 8.49, 1.0, LocalDate.of(2024, 10, 15));
            UNCaddPurchases(ps, "ac926a1e-780e-48e5-8dcd-06fe9d204c5c", 13.49, 3.0, LocalDate.of(2024, 10, 15));
            UNCaddPurchases(ps, "8e0ca1ee-df41-411b-8591-9be7e4b34125", 3.99, 2.0, LocalDate.of(2024, 10, 15));
            UNCaddPurchases(ps, "fe156ac3-7131-4188-830b-d6a4fa030e8f", 5.49, 1.0, LocalDate.of(2024, 10, 15));
            UNCaddPurchases(ps, "7fc847da-44b1-4817-9a0a-79703cff2a1c", 10.49, 1.0, LocalDate.of(2024, 10, 15));
            UNCaddPurchases(ps, "a3e279ea-080d-4762-814f-9ba4c4293397", 6.99, 1.0, LocalDate.of(2024, 10, 15));
            UNCaddPurchases(ps, "e631be2c-9959-40f8-9695-c7bbd8966e2d", 5.00, 3.0, LocalDate.of(2024, 10, 20));
            UNCaddPurchases(ps, "31bdcd6b-a116-4eaf-a0bc-9a8e7287976b", 6.49, 2.0, LocalDate.of(2024, 10, 20));
            UNCaddPurchases(ps, "0667113c-7559-4629-b577-e9741f43ef5a", 3.49, 3.0, LocalDate.of(2024, 10, 20));
            UNCaddPurchases(ps, "fe10d506-1e42-4791-b7f9-094a81f1b50d", 3.49, 3.0, LocalDate.of(2024, 10, 20));
            UNCaddPurchases(ps, "abefa615-4f4d-4a4e-b882-4a6c33eb1198", 11.49, 3.0, LocalDate.of(2024, 10, 20));
            UNCaddPurchases(ps, "578e3ad4-097a-453e-aab1-d5abd951f25a", 2.49, 2.0, LocalDate.of(2024, 10, 20));
            UNCaddPurchases(ps, "b9840c75-9807-49ea-8325-75c92e5a902f", 3.00, 3.0, LocalDate.of(2024, 10, 20));
            UNCaddPurchases(ps, "6ca597ad-f42f-4ed0-8c4e-81572e58246a", 4.99, 2.0, LocalDate.of(2024, 10, 20));
            UNCaddPurchases(ps, "ac926a1e-780e-48e5-8dcd-06fe9d204c5c", 12.99, 2.0, LocalDate.of(2024, 10, 20));
            UNCaddPurchases(ps, "738d56b4-952f-4748-8e63-966c30d23265", 8.49, 1.0, LocalDate.of(2024, 10, 20));
            UNCaddPurchases(ps, "9350a653-d57c-45d2-ba03-0c4835c7a31d", 5.49, 2.0, LocalDate.of(2024, 10, 23));
            UNCaddPurchases(ps, "e631be2c-9959-40f8-9695-c7bbd8966e2d", 5.49, 1.0, LocalDate.of(2024, 10, 23));
            UNCaddPurchases(ps, "6a52d8e3-7cb0-45a5-ad58-f9a10e836baf", 6.49, 3.0, LocalDate.of(2024, 10, 23));
            UNCaddPurchases(ps, "fe156ac3-7131-4188-830b-d6a4fa030e8f", 5.49, 2.0, LocalDate.of(2024, 10, 23));
            UNCaddPurchases(ps, "e1685c2b-aab5-4b1c-87da-fb75c257fd76", 14.99, 1.0, LocalDate.of(2024, 10, 23));
            UNCaddPurchases(ps, "3e7d1add-bf1f-40a5-9de6-45a3bf0045c2", 4.49, 3.0, LocalDate.of(2024, 10, 23));
            UNCaddPurchases(ps, "ccd80ec8-614b-4968-8aec-04a1fa6ce68d", 3.00, 2.0, LocalDate.of(2024, 10, 23));
            UNCaddPurchases(ps, "31bdcd6b-a116-4eaf-a0bc-9a8e7287976b", 6.49, 1.0, LocalDate.of(2024, 10, 23));
            UNCaddPurchases(ps, "f9809697-3a71-4d52-8347-f0950ab7c5d6", 18.49, 1.0, LocalDate.of(2024, 10, 23));
            UNCaddPurchases(ps, "0667113c-7559-4629-b577-e9741f43ef5a", 3.00, 2.0, LocalDate.of(2024, 10, 23));
            UNCaddPurchases(ps, "b9840c75-9807-49ea-8325-75c92e5a902f", 3.49, 3.0, LocalDate.of(2024, 10, 23));
            UNCaddPurchases(ps, "ec86defe-7548-4031-8a76-d9ce3c551c9b", 3.00, 2.0, LocalDate.of(2024, 10, 23));
            UNCaddPurchases(ps, "6ca597ad-f42f-4ed0-8c4e-81572e58246a", 5.00, 3.0, LocalDate.of(2024, 10, 28));
            UNCaddPurchases(ps, "f9809697-3a71-4d52-8347-f0950ab7c5d6", 16.99, 2.0, LocalDate.of(2024, 10, 28));
            UNCaddPurchases(ps, "d23f242b-91ac-446f-b38b-a4b0214baefa", 10.49, 3.0, LocalDate.of(2024, 10, 28));
            UNCaddPurchases(ps, "43ae807b-0938-4b7a-b1df-c62308ee283e", 6.00, 3.0, LocalDate.of(2024, 10, 28));
            UNCaddPurchases(ps, "31bdcd6b-a116-4eaf-a0bc-9a8e7287976b", 6.99, 3.0, LocalDate.of(2024, 10, 28));
            UNCaddPurchases(ps, "98d7bd17-3fa7-403a-ae36-c25db7a72c39", 8.99, 2.0, LocalDate.of(2024, 10, 28));
            UNCaddPurchases(ps, "dd143324-3031-40f1-b8f7-b90f946e2b99", 3.49, 1.0, LocalDate.of(2024, 10, 28));
            UNCaddPurchases(ps, "e1685c2b-aab5-4b1c-87da-fb75c257fd76", 14.49, 2.0, LocalDate.of(2024, 10, 28));
            UNCaddPurchases(ps, "0667113c-7559-4629-b577-e9741f43ef5a", 3.99, 2.0, LocalDate.of(2024, 10, 28));
            UNCaddPurchases(ps, "ccd80ec8-614b-4968-8aec-04a1fa6ce68d", 3.49, 1.0, LocalDate.of(2024, 10, 28));
            UNCaddPurchases(ps, "8e0ca1ee-df41-411b-8591-9be7e4b34125", 4.00, 1.0, LocalDate.of(2024, 10, 28));
            UNCaddPurchases(ps, "738d56b4-952f-4748-8e63-966c30d23265", 7.49, 3.0, LocalDate.of(2024, 10, 28));
            UNCaddPurchases(ps, "4d9a5e43-0c98-4956-aac5-d206fbb1c600", 2.99, 1.0, LocalDate.of(2024, 10, 31));
            UNCaddPurchases(ps, "c7c4c1e4-a960-4db7-bab9-15b8521157b9", 10.49, 1.0, LocalDate.of(2024, 10, 31));
            UNCaddPurchases(ps, "e1685c2b-aab5-4b1c-87da-fb75c257fd76", 15.49, 2.0, LocalDate.of(2024, 10, 31));
            UNCaddPurchases(ps, "0667113c-7559-4629-b577-e9741f43ef5a", 3.49, 3.0, LocalDate.of(2024, 10, 31));
            UNCaddPurchases(ps, "578e3ad4-097a-453e-aab1-d5abd951f25a", 2.49, 1.0, LocalDate.of(2024, 10, 31));
            UNCaddPurchases(ps, "e631be2c-9959-40f8-9695-c7bbd8966e2d", 4.99, 1.0, LocalDate.of(2024, 10, 31));
            UNCaddPurchases(ps, "31bdcd6b-a116-4eaf-a0bc-9a8e7287976b", 6.99, 2.0, LocalDate.of(2024, 10, 31));
            UNCaddPurchases(ps, "d23f242b-91ac-446f-b38b-a4b0214baefa", 12.00, 1.0, LocalDate.of(2024, 10, 31));
            UNCaddPurchases(ps, "43ae807b-0938-4b7a-b1df-c62308ee283e", 6.49, 2.0, LocalDate.of(2024, 10, 31));
            UNCaddPurchases(ps, "98d7bd17-3fa7-403a-ae36-c25db7a72c39", 9.49, 3.0, LocalDate.of(2024, 10, 31));
            UNCaddPurchases(ps, "8e0ca1ee-df41-411b-8591-9be7e4b34125", 3.49, 2.0, LocalDate.of(2024, 10, 31));
            UNCaddPurchases(ps, "ac926a1e-780e-48e5-8dcd-06fe9d204c5c", 11.99, 1.0, LocalDate.of(2024, 11, 5));
            UNCaddPurchases(ps, "6ca597ad-f42f-4ed0-8c4e-81572e58246a", 5.00, 1.0, LocalDate.of(2024, 11, 5));
            UNCaddPurchases(ps, "9350a653-d57c-45d2-ba03-0c4835c7a31d", 5.00, 3.0, LocalDate.of(2024, 11, 5));
            UNCaddPurchases(ps, "b9840c75-9807-49ea-8325-75c92e5a902f", 3.00, 1.0, LocalDate.of(2024, 11, 5));
            UNCaddPurchases(ps, "eb38da48-390e-4673-a0de-a99a37bed2da", 10.49, 2.0, LocalDate.of(2024, 11, 5));
            UNCaddPurchases(ps, "9035c1bc-7a0d-44d4-a06a-934beca69276", 13.00, 2.0, LocalDate.of(2024, 11, 5));
            UNCaddPurchases(ps, "dd143324-3031-40f1-b8f7-b90f946e2b99", 3.49, 2.0, LocalDate.of(2024, 11, 8));
            UNCaddPurchases(ps, "4d9a5e43-0c98-4956-aac5-d206fbb1c600", 2.99, 1.0, LocalDate.of(2024, 11, 8));
            UNCaddPurchases(ps, "43ae807b-0938-4b7a-b1df-c62308ee283e", 6.49, 3.0, LocalDate.of(2024, 11, 8));
            UNCaddPurchases(ps, "d23f242b-91ac-446f-b38b-a4b0214baefa", 12.00, 1.0, LocalDate.of(2024, 11, 8));
            UNCaddPurchases(ps, "3e7d1add-bf1f-40a5-9de6-45a3bf0045c2", 4.49, 3.0, LocalDate.of(2024, 11, 8));
            UNCaddPurchases(ps, "738d56b4-952f-4748-8e63-966c30d23265", 8.00, 3.0, LocalDate.of(2024, 11, 8));
            UNCaddPurchases(ps, "6ca597ad-f42f-4ed0-8c4e-81572e58246a", 5.00, 2.0, LocalDate.of(2024, 11, 8));
            UNCaddPurchases(ps, "578e3ad4-097a-453e-aab1-d5abd951f25a", 2.49, 3.0, LocalDate.of(2024, 11, 8));
            UNCaddPurchases(ps, "8e0ca1ee-df41-411b-8591-9be7e4b34125", 4.00, 1.0, LocalDate.of(2024, 11, 8));
            UNCaddPurchases(ps, "31bdcd6b-a116-4eaf-a0bc-9a8e7287976b", 6.49, 3.0, LocalDate.of(2024, 11, 8));
            UNCaddPurchases(ps, "31bdcd6b-a116-4eaf-a0bc-9a8e7287976b", 6.99, 3.0, LocalDate.of(2024, 11, 12));
            UNCaddPurchases(ps, "43ae807b-0938-4b7a-b1df-c62308ee283e", 5.49, 2.0, LocalDate.of(2024, 11, 12));
            UNCaddPurchases(ps, "f9809697-3a71-4d52-8347-f0950ab7c5d6", 15.49, 3.0, LocalDate.of(2024, 11, 12));
            UNCaddPurchases(ps, "ccd80ec8-614b-4968-8aec-04a1fa6ce68d", 3.00, 2.0, LocalDate.of(2024, 11, 12));
            UNCaddPurchases(ps, "9a9c7178-7fa9-4114-92db-3e08dbb21026", 4.99, 3.0, LocalDate.of(2024, 11, 12));
            UNCaddPurchases(ps, "ec86defe-7548-4031-8a76-d9ce3c551c9b", 3.49, 2.0, LocalDate.of(2024, 11, 12));
            UNCaddPurchases(ps, "8e0ca1ee-df41-411b-8591-9be7e4b34125", 3.49, 3.0, LocalDate.of(2024, 11, 12));
            UNCaddPurchases(ps, "b9840c75-9807-49ea-8325-75c92e5a902f", 2.49, 2.0, LocalDate.of(2024, 11, 12));
            UNCaddPurchases(ps, "578e3ad4-097a-453e-aab1-d5abd951f25a", 2.49, 1.0, LocalDate.of(2024, 11, 12));
            UNCaddPurchases(ps, "3e7d1add-bf1f-40a5-9de6-45a3bf0045c2", 4.00, 1.0, LocalDate.of(2024, 11, 12));
            UNCaddPurchases(ps, "e631be2c-9959-40f8-9695-c7bbd8966e2d", 4.49, 3.0, LocalDate.of(2024, 11, 12));
            UNCaddPurchases(ps, "9350a653-d57c-45d2-ba03-0c4835c7a31d", 5.49, 2.0, LocalDate.of(2024, 11, 12));
            UNCaddPurchases(ps, "c7c4c1e4-a960-4db7-bab9-15b8521157b9", 9.49, 1.0, LocalDate.of(2024, 11, 12));
            UNCaddPurchases(ps, "fe156ac3-7131-4188-830b-d6a4fa030e8f", 4.99, 3.0, LocalDate.of(2024, 11, 12));
            UNCaddPurchases(ps, "0953287f-0a6d-4196-8e34-e5aa7c151266", 8.99, 1.0, LocalDate.of(2024, 11, 12));
            UNCaddPurchases(ps, "98d7bd17-3fa7-403a-ae36-c25db7a72c39", 8.49, 1.0, LocalDate.of(2024, 11, 16));
            UNCaddPurchases(ps, "d23f242b-91ac-446f-b38b-a4b0214baefa", 11.49, 2.0, LocalDate.of(2024, 11, 16));
            UNCaddPurchases(ps, "31bdcd6b-a116-4eaf-a0bc-9a8e7287976b", 6.49, 3.0, LocalDate.of(2024, 11, 16));
            UNCaddPurchases(ps, "f9809697-3a71-4d52-8347-f0950ab7c5d6", 15.99, 1.0, LocalDate.of(2024, 11, 16));
            UNCaddPurchases(ps, "6a52d8e3-7cb0-45a5-ad58-f9a10e836baf", 6.00, 1.0, LocalDate.of(2024, 11, 16));
            UNCaddPurchases(ps, "8e0ca1ee-df41-411b-8591-9be7e4b34125", 4.00, 2.0, LocalDate.of(2024, 11, 16));
            UNCaddPurchases(ps, "ac926a1e-780e-48e5-8dcd-06fe9d204c5c", 13.49, 2.0, LocalDate.of(2024, 11, 16));
            UNCaddPurchases(ps, "4d9a5e43-0c98-4956-aac5-d206fbb1c600", 3.00, 1.0, LocalDate.of(2024, 11, 16));
            UNCaddPurchases(ps, "b9840c75-9807-49ea-8325-75c92e5a902f", 2.99, 2.0, LocalDate.of(2024, 11, 16));
            UNCaddPurchases(ps, "9a9c7178-7fa9-4114-92db-3e08dbb21026", 4.99, 2.0, LocalDate.of(2024, 11, 16));
            UNCaddPurchases(ps, "0667113c-7559-4629-b577-e9741f43ef5a", 3.49, 1.0, LocalDate.of(2024, 11, 16));
            UNCaddPurchases(ps, "e1685c2b-aab5-4b1c-87da-fb75c257fd76", 14.49, 3.0, LocalDate.of(2024, 11, 16));
            UNCaddPurchases(ps, "dd143324-3031-40f1-b8f7-b90f946e2b99", 3.99, 2.0, LocalDate.of(2024, 11, 19));
            UNCaddPurchases(ps, "d23f242b-91ac-446f-b38b-a4b0214baefa", 11.99, 1.0, LocalDate.of(2024, 11, 19));
            UNCaddPurchases(ps, "9350a653-d57c-45d2-ba03-0c4835c7a31d", 5.00, 3.0, LocalDate.of(2024, 11, 19));
            UNCaddPurchases(ps, "43ae807b-0938-4b7a-b1df-c62308ee283e", 6.49, 1.0, LocalDate.of(2024, 11, 19));
            UNCaddPurchases(ps, "b9840c75-9807-49ea-8325-75c92e5a902f", 2.99, 2.0, LocalDate.of(2024, 11, 19));
            UNCaddPurchases(ps, "6ca597ad-f42f-4ed0-8c4e-81572e58246a", 5.00, 3.0, LocalDate.of(2024, 11, 19));
            UNCaddPurchases(ps, "ec86defe-7548-4031-8a76-d9ce3c551c9b", 3.00, 1.0, LocalDate.of(2024, 11, 19));
            UNCaddPurchases(ps, "578e3ad4-097a-453e-aab1-d5abd951f25a", 2.49, 1.0, LocalDate.of(2024, 11, 19));
            UNCaddPurchases(ps, "8e0ca1ee-df41-411b-8591-9be7e4b34125", 4.49, 2.0, LocalDate.of(2024, 11, 19));
            UNCaddPurchases(ps, "e631be2c-9959-40f8-9695-c7bbd8966e2d", 4.49, 1.0, LocalDate.of(2024, 11, 19));
            UNCaddPurchases(ps, "ac926a1e-780e-48e5-8dcd-06fe9d204c5c", 12.99, 2.0, LocalDate.of(2024, 11, 19));
            UNCaddPurchases(ps, "6a52d8e3-7cb0-45a5-ad58-f9a10e836baf", 6.49, 1.0, LocalDate.of(2024, 11, 19));
            UNCaddPurchases(ps, "ec86defe-7548-4031-8a76-d9ce3c551c9b", 3.00, 1.0, LocalDate.of(2024, 11, 24));
            UNCaddPurchases(ps, "ac926a1e-780e-48e5-8dcd-06fe9d204c5c", 12.00, 1.0, LocalDate.of(2024, 11, 24));
            UNCaddPurchases(ps, "fe156ac3-7131-4188-830b-d6a4fa030e8f", 4.49, 3.0, LocalDate.of(2024, 11, 24));
            UNCaddPurchases(ps, "8e0ca1ee-df41-411b-8591-9be7e4b34125", 4.00, 3.0, LocalDate.of(2024, 11, 24));
            UNCaddPurchases(ps, "dd143324-3031-40f1-b8f7-b90f946e2b99", 3.49, 2.0, LocalDate.of(2024, 11, 24));
            UNCaddPurchases(ps, "d23f242b-91ac-446f-b38b-a4b0214baefa", 11.00, 3.0, LocalDate.of(2024, 11, 28));
            UNCaddPurchases(ps, "98d7bd17-3fa7-403a-ae36-c25db7a72c39", 8.99, 1.0, LocalDate.of(2024, 11, 28));
            UNCaddPurchases(ps, "6a52d8e3-7cb0-45a5-ad58-f9a10e836baf", 6.49, 1.0, LocalDate.of(2024, 11, 28));
            UNCaddPurchases(ps, "9a9c7178-7fa9-4114-92db-3e08dbb21026", 4.99, 1.0, LocalDate.of(2024, 11, 28));
            UNCaddPurchases(ps, "738d56b4-952f-4748-8e63-966c30d23265", 8.49, 3.0, LocalDate.of(2024, 11, 28));
            UNCaddPurchases(ps, "0667113c-7559-4629-b577-e9741f43ef5a", 3.49, 3.0, LocalDate.of(2024, 11, 28));
            UNCaddPurchases(ps, "abefa615-4f4d-4a4e-b882-4a6c33eb1198", 12.49, 3.0, LocalDate.of(2024, 11, 28));
            UNCaddPurchases(ps, "8e0ca1ee-df41-411b-8591-9be7e4b34125", 3.49, 3.0, LocalDate.of(2024, 11, 28));
            UNCaddPurchases(ps, "43ae807b-0938-4b7a-b1df-c62308ee283e", 5.49, 2.0, LocalDate.of(2024, 11, 28));
            UNCaddPurchases(ps, "9350a653-d57c-45d2-ba03-0c4835c7a31d", 5.00, 3.0, LocalDate.of(2024, 11, 28));
            UNCaddPurchases(ps, "dd143324-3031-40f1-b8f7-b90f946e2b99", 3.99, 2.0, LocalDate.of(2024, 11, 28));
            UNCaddPurchases(ps, "6ca597ad-f42f-4ed0-8c4e-81572e58246a", 5.00, 2.0, LocalDate.of(2024, 11, 28));
            UNCaddPurchases(ps, "31bdcd6b-a116-4eaf-a0bc-9a8e7287976b", 6.49, 2.0, LocalDate.of(2024, 11, 28));
            UNCaddPurchases(ps, "fe156ac3-7131-4188-830b-d6a4fa030e8f", 4.99, 2.0, LocalDate.of(2024, 11, 28));
            UNCaddPurchases(ps, "0953287f-0a6d-4196-8e34-e5aa7c151266", 9.49, 1.0, LocalDate.of(2024, 11, 28));
            UNCaddPurchases(ps, "9035c1bc-7a0d-44d4-a06a-934beca69276", 12.49, 2.0, LocalDate.of(2024, 11, 28));
            UNCaddPurchases(ps, "578e3ad4-097a-453e-aab1-d5abd951f25a", 2.49, 3.0, LocalDate.of(2024, 12, 1));
            UNCaddPurchases(ps, "d23f242b-91ac-446f-b38b-a4b0214baefa", 10.99, 1.0, LocalDate.of(2024, 12, 1));
            UNCaddPurchases(ps, "ec86defe-7548-4031-8a76-d9ce3c551c9b", 3.49, 3.0, LocalDate.of(2024, 12, 1));
            UNCaddPurchases(ps, "fe10d506-1e42-4791-b7f9-094a81f1b50d", 3.49, 1.0, LocalDate.of(2024, 12, 1));
            UNCaddPurchases(ps, "6a52d8e3-7cb0-45a5-ad58-f9a10e836baf", 6.49, 2.0, LocalDate.of(2024, 12, 1));
            UNCaddPurchases(ps, "a8d397b0-e695-4b50-99b1-b2eb57277a79", 14.00, 3.0, LocalDate.of(2024, 12, 1));
            UNCaddPurchases(ps, "a8d397b0-e695-4b50-99b1-b2eb57277a79", 14.00, 2.0, LocalDate.of(2024, 12, 5));
            UNCaddPurchases(ps, "0667113c-7559-4629-b577-e9741f43ef5a", 3.49, 2.0, LocalDate.of(2024, 12, 5));
            UNCaddPurchases(ps, "6a52d8e3-7cb0-45a5-ad58-f9a10e836baf", 6.00, 1.0, LocalDate.of(2024, 12, 5));
            UNCaddPurchases(ps, "9a9c7178-7fa9-4114-92db-3e08dbb21026", 4.49, 2.0, LocalDate.of(2024, 12, 5));
            UNCaddPurchases(ps, "578e3ad4-097a-453e-aab1-d5abd951f25a", 2.49, 1.0, LocalDate.of(2024, 12, 5));
            UNCaddPurchases(ps, "4d9a5e43-0c98-4956-aac5-d206fbb1c600", 3.00, 2.0, LocalDate.of(2024, 12, 8));
            UNCaddPurchases(ps, "43ae807b-0938-4b7a-b1df-c62308ee283e", 5.49, 1.0, LocalDate.of(2024, 12, 8));
            UNCaddPurchases(ps, "a8d397b0-e695-4b50-99b1-b2eb57277a79", 13.99, 1.0, LocalDate.of(2024, 12, 8));
            UNCaddPurchases(ps, "31bdcd6b-a116-4eaf-a0bc-9a8e7287976b", 6.49, 2.0, LocalDate.of(2024, 12, 8));
            UNCaddPurchases(ps, "f9809697-3a71-4d52-8347-f0950ab7c5d6", 15.49, 1.0, LocalDate.of(2024, 12, 8));
            UNCaddPurchases(ps, "578e3ad4-097a-453e-aab1-d5abd951f25a", 2.49, 2.0, LocalDate.of(2024, 12, 8));
            UNCaddPurchases(ps, "6a52d8e3-7cb0-45a5-ad58-f9a10e836baf", 7.00, 1.0, LocalDate.of(2024, 12, 8));
            UNCaddPurchases(ps, "abefa615-4f4d-4a4e-b882-4a6c33eb1198", 12.99, 1.0, LocalDate.of(2024, 12, 8));
            UNCaddPurchases(ps, "3e7d1add-bf1f-40a5-9de6-45a3bf0045c2", 4.49, 2.0, LocalDate.of(2024, 12, 8));
            UNCaddPurchases(ps, "ec86defe-7548-4031-8a76-d9ce3c551c9b", 3.49, 3.0, LocalDate.of(2024, 12, 8));
            UNCaddPurchases(ps, "ccd80ec8-614b-4968-8aec-04a1fa6ce68d", 3.49, 2.0, LocalDate.of(2024, 12, 8));
            UNCaddPurchases(ps, "8e0ca1ee-df41-411b-8591-9be7e4b34125", 4.00, 2.0, LocalDate.of(2024, 12, 11));
            UNCaddPurchases(ps, "6a52d8e3-7cb0-45a5-ad58-f9a10e836baf", 6.49, 2.0, LocalDate.of(2024, 12, 11));
            UNCaddPurchases(ps, "31bdcd6b-a116-4eaf-a0bc-9a8e7287976b", 6.00, 1.0, LocalDate.of(2024, 12, 11));
            UNCaddPurchases(ps, "ccd80ec8-614b-4968-8aec-04a1fa6ce68d", 3.49, 2.0, LocalDate.of(2024, 12, 11));
            UNCaddPurchases(ps, "98d7bd17-3fa7-403a-ae36-c25db7a72c39", 8.49, 2.0, LocalDate.of(2024, 12, 11));
            UNCaddPurchases(ps, "9350a653-d57c-45d2-ba03-0c4835c7a31d", 5.99, 2.0, LocalDate.of(2024, 12, 14));
            UNCaddPurchases(ps, "e631be2c-9959-40f8-9695-c7bbd8966e2d", 5.49, 1.0, LocalDate.of(2024, 12, 14));
            UNCaddPurchases(ps, "6a52d8e3-7cb0-45a5-ad58-f9a10e836baf", 7.00, 3.0, LocalDate.of(2024, 12, 14));
            UNCaddPurchases(ps, "a8d397b0-e695-4b50-99b1-b2eb57277a79", 13.49, 2.0, LocalDate.of(2024, 12, 14));
            UNCaddPurchases(ps, "8e0ca1ee-df41-411b-8591-9be7e4b34125", 4.49, 3.0, LocalDate.of(2024, 12, 17));
            UNCaddPurchases(ps, "dd143324-3031-40f1-b8f7-b90f946e2b99", 3.49, 2.0, LocalDate.of(2024, 12, 17));
            UNCaddPurchases(ps, "c7c4c1e4-a960-4db7-bab9-15b8521157b9", 10.49, 2.0, LocalDate.of(2024, 12, 17));
            UNCaddPurchases(ps, "9a9c7178-7fa9-4114-92db-3e08dbb21026", 4.49, 2.0, LocalDate.of(2024, 12, 17));
            UNCaddPurchases(ps, "738d56b4-952f-4748-8e63-966c30d23265", 8.99, 2.0, LocalDate.of(2024, 12, 17));
            UNCaddPurchases(ps, "abefa615-4f4d-4a4e-b882-4a6c33eb1198", 12.99, 2.0, LocalDate.of(2024, 12, 17));
            UNCaddPurchases(ps, "31bdcd6b-a116-4eaf-a0bc-9a8e7287976b", 6.49, 3.0, LocalDate.of(2024, 12, 17));
            UNCaddPurchases(ps, "ccd80ec8-614b-4968-8aec-04a1fa6ce68d", 3.00, 1.0, LocalDate.of(2024, 12, 17));
            UNCaddPurchases(ps, "fe156ac3-7131-4188-830b-d6a4fa030e8f", 5.49, 1.0, LocalDate.of(2024, 12, 17));
            UNCaddPurchases(ps, "578e3ad4-097a-453e-aab1-d5abd951f25a", 2.49, 1.0, LocalDate.of(2024, 12, 17));
            UNCaddPurchases(ps, "d23f242b-91ac-446f-b38b-a4b0214baefa", 11.00, 2.0, LocalDate.of(2024, 12, 17));
            UNCaddPurchases(ps, "a8d397b0-e695-4b50-99b1-b2eb57277a79", 15.00, 3.0, LocalDate.of(2024, 12, 17));
            UNCaddPurchases(ps, "f9809697-3a71-4d52-8347-f0950ab7c5d6", 16.49, 3.0, LocalDate.of(2024, 12, 17));
            UNCaddPurchases(ps, "c7c4c1e4-a960-4db7-bab9-15b8521157b9", 9.99, 2.0, LocalDate.of(2024, 12, 22));
            UNCaddPurchases(ps, "43ae807b-0938-4b7a-b1df-c62308ee283e", 6.00, 3.0, LocalDate.of(2024, 12, 22));
            UNCaddPurchases(ps, "6a52d8e3-7cb0-45a5-ad58-f9a10e836baf", 6.49, 1.0, LocalDate.of(2024, 12, 22));
            UNCaddPurchases(ps, "fe156ac3-7131-4188-830b-d6a4fa030e8f", 5.49, 1.0, LocalDate.of(2024, 12, 27));
            UNCaddPurchases(ps, "6a52d8e3-7cb0-45a5-ad58-f9a10e836baf", 7.00, 3.0, LocalDate.of(2024, 12, 27));
            UNCaddPurchases(ps, "e1685c2b-aab5-4b1c-87da-fb75c257fd76", 16.49, 2.0, LocalDate.of(2024, 12, 27));
            UNCaddPurchases(ps, "6ca597ad-f42f-4ed0-8c4e-81572e58246a", 4.99, 1.0, LocalDate.of(2024, 12, 27));
            UNCaddPurchases(ps, "3e7d1add-bf1f-40a5-9de6-45a3bf0045c2", 4.49, 1.0, LocalDate.of(2024, 12, 27));
            UNCaddPurchases(ps, "9350a653-d57c-45d2-ba03-0c4835c7a31d", 5.49, 3.0, LocalDate.of(2024, 12, 27));
            UNCaddPurchases(ps, "a8d397b0-e695-4b50-99b1-b2eb57277a79", 14.00, 3.0, LocalDate.of(2024, 12, 27));
            UNCaddPurchases(ps, "ccd80ec8-614b-4968-8aec-04a1fa6ce68d", 3.49, 1.0, LocalDate.of(2024, 12, 27));
            UNCaddPurchases(ps, "fe10d506-1e42-4791-b7f9-094a81f1b50d", 3.49, 1.0, LocalDate.of(2024, 12, 27));
            UNCaddPurchases(ps, "abefa615-4f4d-4a4e-b882-4a6c33eb1198", 12.99, 1.0, LocalDate.of(2024, 12, 27));
            UNCaddPurchases(ps, "31bdcd6b-a116-4eaf-a0bc-9a8e7287976b", 6.00, 1.0, LocalDate.of(2024, 12, 27));
            UNCaddPurchases(ps, "e631be2c-9959-40f8-9695-c7bbd8966e2d", 4.49, 1.0, LocalDate.of(2024, 12, 27));
            UNCaddPurchases(ps, "ec86defe-7548-4031-8a76-d9ce3c551c9b", 3.49, 3.0, LocalDate.of(2024, 12, 27));
            UNCaddPurchases(ps, "c7c4c1e4-a960-4db7-bab9-15b8521157b9", 9.49, 3.0, LocalDate.of(2024, 12, 27));
            UNCaddPurchases(ps, "43ae807b-0938-4b7a-b1df-c62308ee283e", 6.00, 1.0, LocalDate.of(2024, 12, 31));
            UNCaddPurchases(ps, "e1685c2b-aab5-4b1c-87da-fb75c257fd76", 15.99, 3.0, LocalDate.of(2024, 12, 31));
            UNCaddPurchases(ps, "ac926a1e-780e-48e5-8dcd-06fe9d204c5c", 13.49, 3.0, LocalDate.of(2024, 12, 31));
            UNCaddPurchases(ps, "e631be2c-9959-40f8-9695-c7bbd8966e2d", 4.49, 2.0, LocalDate.of(2024, 12, 31));
            UNCaddPurchases(ps, "31bdcd6b-a116-4eaf-a0bc-9a8e7287976b", 7.00, 1.0, LocalDate.of(2025, 1, 4));
            UNCaddPurchases(ps, "9a9c7178-7fa9-4114-92db-3e08dbb21026", 5.00, 1.0, LocalDate.of(2025, 1, 4));
            UNCaddPurchases(ps, "98d7bd17-3fa7-403a-ae36-c25db7a72c39", 9.00, 1.0, LocalDate.of(2025, 1, 4));
            UNCaddPurchases(ps, "a8d397b0-e695-4b50-99b1-b2eb57277a79", 14.00, 2.0, LocalDate.of(2025, 1, 4));
            UNCaddPurchases(ps, "e1685c2b-aab5-4b1c-87da-fb75c257fd76", 15.00, 2.0, LocalDate.of(2025, 1, 4));
            UNCaddPurchases(ps, "abefa615-4f4d-4a4e-b882-4a6c33eb1198", 12.99, 2.0, LocalDate.of(2025, 1, 4));
            UNCaddPurchases(ps, "6ca597ad-f42f-4ed0-8c4e-81572e58246a", 5.49, 3.0, LocalDate.of(2025, 1, 4));
            UNCaddPurchases(ps, "dd143324-3031-40f1-b8f7-b90f946e2b99", 4.00, 3.0, LocalDate.of(2025, 1, 4));
            UNCaddPurchases(ps, "c7c4c1e4-a960-4db7-bab9-15b8521157b9", 10.00, 2.0, LocalDate.of(2025, 1, 4));
            UNCaddPurchases(ps, "8e0ca1ee-df41-411b-8591-9be7e4b34125", 4.49, 2.0, LocalDate.of(2025, 1, 4));
            UNCaddPurchases(ps, "578e3ad4-097a-453e-aab1-d5abd951f25a", 2.49, 1.0, LocalDate.of(2025, 1, 8));
            UNCaddPurchases(ps, "4d9a5e43-0c98-4956-aac5-d206fbb1c600", 2.99, 2.0, LocalDate.of(2025, 1, 8));
            UNCaddPurchases(ps, "a8d397b0-e695-4b50-99b1-b2eb57277a79", 12.99, 3.0, LocalDate.of(2025, 1, 8));
            UNCaddPurchases(ps, "f9809697-3a71-4d52-8347-f0950ab7c5d6", 18.00, 2.0, LocalDate.of(2025, 1, 8));
            UNCaddPurchases(ps, "fe10d506-1e42-4791-b7f9-094a81f1b50d", 3.49, 3.0, LocalDate.of(2025, 1, 8));
            UNCaddPurchases(ps, "d23f242b-91ac-446f-b38b-a4b0214baefa", 10.99, 2.0, LocalDate.of(2025, 1, 8));
            UNCaddPurchases(ps, "0667113c-7559-4629-b577-e9741f43ef5a", 3.49, 1.0, LocalDate.of(2025, 1, 13));
            UNCaddPurchases(ps, "ec86defe-7548-4031-8a76-d9ce3c551c9b", 3.49, 2.0, LocalDate.of(2025, 1, 13));
            UNCaddPurchases(ps, "8e0ca1ee-df41-411b-8591-9be7e4b34125", 4.00, 2.0, LocalDate.of(2025, 1, 13));
            UNCaddPurchases(ps, "f9809697-3a71-4d52-8347-f0950ab7c5d6", 18.00, 1.0, LocalDate.of(2025, 1, 13));
            UNCaddPurchases(ps, "43ae807b-0938-4b7a-b1df-c62308ee283e", 5.99, 1.0, LocalDate.of(2025, 1, 13));
            UNCaddPurchases(ps, "a8d397b0-e695-4b50-99b1-b2eb57277a79", 13.99, 1.0, LocalDate.of(2025, 1, 13));
            UNCaddPurchases(ps, "e631be2c-9959-40f8-9695-c7bbd8966e2d", 4.49, 2.0, LocalDate.of(2025, 1, 13));
            UNCaddPurchases(ps, "c7c4c1e4-a960-4db7-bab9-15b8521157b9", 10.99, 2.0, LocalDate.of(2025, 1, 13));
            UNCaddPurchases(ps, "abefa615-4f4d-4a4e-b882-4a6c33eb1198", 11.00, 1.0, LocalDate.of(2025, 1, 13));
            UNCaddPurchases(ps, "3e7d1add-bf1f-40a5-9de6-45a3bf0045c2", 4.00, 2.0, LocalDate.of(2025, 1, 13));
            UNCaddPurchases(ps, "e1685c2b-aab5-4b1c-87da-fb75c257fd76", 14.49, 2.0, LocalDate.of(2025, 1, 13));
            UNCaddPurchases(ps, "6a52d8e3-7cb0-45a5-ad58-f9a10e836baf", 6.49, 1.0, LocalDate.of(2025, 1, 16));
            UNCaddPurchases(ps, "3e7d1add-bf1f-40a5-9de6-45a3bf0045c2", 4.49, 2.0, LocalDate.of(2025, 1, 16));
            UNCaddPurchases(ps, "fe10d506-1e42-4791-b7f9-094a81f1b50d", 3.99, 2.0, LocalDate.of(2025, 1, 16));
            UNCaddPurchases(ps, "f9809697-3a71-4d52-8347-f0950ab7c5d6", 15.99, 3.0, LocalDate.of(2025, 1, 20));
            UNCaddPurchases(ps, "ec86defe-7548-4031-8a76-d9ce3c551c9b", 3.49, 2.0, LocalDate.of(2025, 1, 20));
            UNCaddPurchases(ps, "e631be2c-9959-40f8-9695-c7bbd8966e2d", 4.99, 3.0, LocalDate.of(2025, 1, 20));
            UNCaddPurchases(ps, "31bdcd6b-a116-4eaf-a0bc-9a8e7287976b", 6.00, 1.0, LocalDate.of(2025, 1, 20));
            UNCaddPurchases(ps, "3e7d1add-bf1f-40a5-9de6-45a3bf0045c2", 4.49, 3.0, LocalDate.of(2025, 1, 20));
            UNCaddPurchases(ps, "738d56b4-952f-4748-8e63-966c30d23265", 8.99, 3.0, LocalDate.of(2025, 1, 20));
            UNCaddPurchases(ps, "ccd80ec8-614b-4968-8aec-04a1fa6ce68d", 3.49, 1.0, LocalDate.of(2025, 1, 23));
            UNCaddPurchases(ps, "3e7d1add-bf1f-40a5-9de6-45a3bf0045c2", 4.49, 2.0, LocalDate.of(2025, 1, 23));
            UNCaddPurchases(ps, "9350a653-d57c-45d2-ba03-0c4835c7a31d", 5.49, 2.0, LocalDate.of(2025, 1, 23));
            UNCaddPurchases(ps, "43ae807b-0938-4b7a-b1df-c62308ee283e", 6.49, 2.0, LocalDate.of(2025, 1, 23));
            UNCaddPurchases(ps, "98d7bd17-3fa7-403a-ae36-c25db7a72c39", 9.99, 1.0, LocalDate.of(2025, 1, 23));
            UNCaddPurchases(ps, "ec86defe-7548-4031-8a76-d9ce3c551c9b", 3.49, 1.0, LocalDate.of(2025, 1, 23));
            UNCaddPurchases(ps, "31bdcd6b-a116-4eaf-a0bc-9a8e7287976b", 7.00, 1.0, LocalDate.of(2025, 1, 27));
            UNCaddPurchases(ps, "f9809697-3a71-4d52-8347-f0950ab7c5d6", 17.49, 3.0, LocalDate.of(2025, 1, 27));
            UNCaddPurchases(ps, "9a9c7178-7fa9-4114-92db-3e08dbb21026", 5.00, 1.0, LocalDate.of(2025, 1, 27));
            UNCaddPurchases(ps, "a8d397b0-e695-4b50-99b1-b2eb57277a79", 13.49, 1.0, LocalDate.of(2025, 1, 27));
            UNCaddPurchases(ps, "0667113c-7559-4629-b577-e9741f43ef5a", 3.49, 2.0, LocalDate.of(2025, 1, 27));
            UNCaddPurchases(ps, "abefa615-4f4d-4a4e-b882-4a6c33eb1198", 11.49, 1.0, LocalDate.of(2025, 1, 31));
            UNCaddPurchases(ps, "4d9a5e43-0c98-4956-aac5-d206fbb1c600", 3.49, 3.0, LocalDate.of(2025, 1, 31));
            UNCaddPurchases(ps, "e1685c2b-aab5-4b1c-87da-fb75c257fd76", 15.49, 2.0, LocalDate.of(2025, 1, 31));
            UNCaddPurchases(ps, "9a9c7178-7fa9-4114-92db-3e08dbb21026", 5.00, 2.0, LocalDate.of(2025, 2, 5));
            UNCaddPurchases(ps, "dd143324-3031-40f1-b8f7-b90f946e2b99", 3.49, 3.0, LocalDate.of(2025, 2, 5));
            UNCaddPurchases(ps, "fe10d506-1e42-4791-b7f9-094a81f1b50d", 3.99, 3.0, LocalDate.of(2025, 2, 5));
            UNCaddPurchases(ps, "b9840c75-9807-49ea-8325-75c92e5a902f", 3.00, 1.0, LocalDate.of(2025, 2, 5));
            UNCaddPurchases(ps, "6a52d8e3-7cb0-45a5-ad58-f9a10e836baf", 6.00, 3.0, LocalDate.of(2025, 2, 5));
            UNCaddPurchases(ps, "d23f242b-91ac-446f-b38b-a4b0214baefa", 10.99, 1.0, LocalDate.of(2025, 2, 5));
            UNCaddPurchases(ps, "6ca597ad-f42f-4ed0-8c4e-81572e58246a", 4.49, 1.0, LocalDate.of(2025, 2, 5));
            UNCaddPurchases(ps, "abefa615-4f4d-4a4e-b882-4a6c33eb1198", 12.49, 3.0, LocalDate.of(2025, 2, 5));
            UNCaddPurchases(ps, "3e7d1add-bf1f-40a5-9de6-45a3bf0045c2", 4.49, 1.0, LocalDate.of(2025, 2, 5));
            UNCaddPurchases(ps, "98d7bd17-3fa7-403a-ae36-c25db7a72c39", 8.99, 1.0, LocalDate.of(2025, 2, 5));
            UNCaddPurchases(ps, "43ae807b-0938-4b7a-b1df-c62308ee283e", 5.99, 1.0, LocalDate.of(2025, 2, 5));
            UNCaddPurchases(ps, "0667113c-7559-4629-b577-e9741f43ef5a", 3.49, 2.0, LocalDate.of(2025, 2, 5));
            UNCaddPurchases(ps, "ac926a1e-780e-48e5-8dcd-06fe9d204c5c", 13.00, 3.0, LocalDate.of(2025, 2, 5));
            UNCaddPurchases(ps, "9035c1bc-7a0d-44d4-a06a-934beca69276", 13.00, 2.0, LocalDate.of(2025, 2, 5));
            UNCaddPurchases(ps, "fe10d506-1e42-4791-b7f9-094a81f1b50d", 3.00, 3.0, LocalDate.of(2025, 2, 10));
            UNCaddPurchases(ps, "d23f242b-91ac-446f-b38b-a4b0214baefa", 12.00, 3.0, LocalDate.of(2025, 2, 10));
            UNCaddPurchases(ps, "578e3ad4-097a-453e-aab1-d5abd951f25a", 2.49, 3.0, LocalDate.of(2025, 2, 10));
            UNCaddPurchases(ps, "3e7d1add-bf1f-40a5-9de6-45a3bf0045c2", 4.49, 3.0, LocalDate.of(2025, 2, 10));
            UNCaddPurchases(ps, "6a52d8e3-7cb0-45a5-ad58-f9a10e836baf", 6.99, 1.0, LocalDate.of(2025, 2, 10));
            UNCaddPurchases(ps, "c7c4c1e4-a960-4db7-bab9-15b8521157b9", 10.00, 2.0, LocalDate.of(2025, 2, 10));
            UNCaddPurchases(ps, "e631be2c-9959-40f8-9695-c7bbd8966e2d", 5.49, 3.0, LocalDate.of(2025, 2, 10));
            UNCaddPurchases(ps, "9350a653-d57c-45d2-ba03-0c4835c7a31d", 5.99, 3.0, LocalDate.of(2025, 2, 10));
            UNCaddPurchases(ps, "a8d397b0-e695-4b50-99b1-b2eb57277a79", 14.99, 3.0, LocalDate.of(2025, 2, 10));
            UNCaddPurchases(ps, "31bdcd6b-a116-4eaf-a0bc-9a8e7287976b", 6.49, 1.0, LocalDate.of(2025, 2, 10));
            UNCaddPurchases(ps, "fe156ac3-7131-4188-830b-d6a4fa030e8f", 4.49, 1.0, LocalDate.of(2025, 2, 10));
            UNCaddPurchases(ps, "eb38da48-390e-4673-a0de-a99a37bed2da", 9.99, 2.0, LocalDate.of(2025, 2, 10));
            UNCaddPurchases(ps, "d4b7d16d-ddb2-4c96-981b-95d01b973355", 25.99, 2.0, LocalDate.of(2025, 2, 10));
            UNCaddPurchases(ps, "738d56b4-952f-4748-8e63-966c30d23265", 8.49, 3.0, LocalDate.of(2025, 2, 15));
            UNCaddPurchases(ps, "9a9c7178-7fa9-4114-92db-3e08dbb21026", 4.49, 3.0, LocalDate.of(2025, 2, 15));
            UNCaddPurchases(ps, "abefa615-4f4d-4a4e-b882-4a6c33eb1198", 11.49, 1.0, LocalDate.of(2025, 2, 15));
            UNCaddPurchases(ps, "a3e279ea-080d-4762-814f-9ba4c4293397", 7.00, 1.0, LocalDate.of(2025, 2, 15));
            UNCaddPurchases(ps, "43ae807b-0938-4b7a-b1df-c62308ee283e", 6.00, 3.0, LocalDate.of(2025, 2, 18));
            UNCaddPurchases(ps, "b9840c75-9807-49ea-8325-75c92e5a902f", 2.99, 1.0, LocalDate.of(2025, 2, 18));
            UNCaddPurchases(ps, "d23f242b-91ac-446f-b38b-a4b0214baefa", 11.00, 3.0, LocalDate.of(2025, 2, 18));
            UNCaddPurchases(ps, "f9809697-3a71-4d52-8347-f0950ab7c5d6", 18.49, 1.0, LocalDate.of(2025, 2, 18));
            UNCaddPurchases(ps, "abefa615-4f4d-4a4e-b882-4a6c33eb1198", 12.49, 1.0, LocalDate.of(2025, 2, 21));
            UNCaddPurchases(ps, "f9809697-3a71-4d52-8347-f0950ab7c5d6", 18.99, 2.0, LocalDate.of(2025, 2, 21));
            UNCaddPurchases(ps, "fe10d506-1e42-4791-b7f9-094a81f1b50d", 3.49, 1.0, LocalDate.of(2025, 2, 21));
            UNCaddPurchases(ps, "c7c4c1e4-a960-4db7-bab9-15b8521157b9", 10.49, 1.0, LocalDate.of(2025, 2, 26));
            UNCaddPurchases(ps, "e1685c2b-aab5-4b1c-87da-fb75c257fd76", 16.49, 3.0, LocalDate.of(2025, 2, 26));
            UNCaddPurchases(ps, "ec86defe-7548-4031-8a76-d9ce3c551c9b", 3.49, 2.0, LocalDate.of(2025, 2, 26));
            UNCaddPurchases(ps, "98d7bd17-3fa7-403a-ae36-c25db7a72c39", 9.99, 1.0, LocalDate.of(2025, 2, 26));
            UNCaddPurchases(ps, "fe10d506-1e42-4791-b7f9-094a81f1b50d", 3.49, 3.0, LocalDate.of(2025, 2, 26));
            UNCaddPurchases(ps, "7fc847da-44b1-4817-9a0a-79703cff2a1c", 10.49, 2.0, LocalDate.of(2025, 2, 26));
            UNCaddPurchases(ps, "81f475a9-0579-43be-85cc-15de4fe6a714", 11.00, 1.0, LocalDate.of(2025, 2, 26));
            UNCaddPurchases(ps, "abefa615-4f4d-4a4e-b882-4a6c33eb1198", 11.49, 1.0, LocalDate.of(2025, 3, 2));
            UNCaddPurchases(ps, "c7c4c1e4-a960-4db7-bab9-15b8521157b9", 10.49, 3.0, LocalDate.of(2025, 3, 2));
            UNCaddPurchases(ps, "fe156ac3-7131-4188-830b-d6a4fa030e8f", 5.49, 1.0, LocalDate.of(2025, 3, 2));
            UNCaddPurchases(ps, "81f475a9-0579-43be-85cc-15de4fe6a714", 12.99, 2.0, LocalDate.of(2025, 3, 2));
            UNCaddPurchases(ps, "7fc847da-44b1-4817-9a0a-79703cff2a1c", 11.99, 2.0, LocalDate.of(2025, 3, 2));
            UNCaddPurchases(ps, "578e3ad4-097a-453e-aab1-d5abd951f25a", 2.49, 1.0, LocalDate.of(2025, 3, 6));
            UNCaddPurchases(ps, "3e7d1add-bf1f-40a5-9de6-45a3bf0045c2", 4.00, 1.0, LocalDate.of(2025, 3, 6));
            UNCaddPurchases(ps, "31bdcd6b-a116-4eaf-a0bc-9a8e7287976b", 6.00, 1.0, LocalDate.of(2025, 3, 6));
            UNCaddPurchases(ps, "9a9c7178-7fa9-4114-92db-3e08dbb21026", 4.49, 1.0, LocalDate.of(2025, 3, 6));
            UNCaddPurchases(ps, "abefa615-4f4d-4a4e-b882-4a6c33eb1198", 12.49, 1.0, LocalDate.of(2025, 3, 6));
            UNCaddPurchases(ps, "98d7bd17-3fa7-403a-ae36-c25db7a72c39", 8.49, 3.0, LocalDate.of(2025, 3, 6));
            UNCaddPurchases(ps, "dd143324-3031-40f1-b8f7-b90f946e2b99", 4.00, 3.0, LocalDate.of(2025, 3, 11));
            UNCaddPurchases(ps, "738d56b4-952f-4748-8e63-966c30d23265", 8.49, 3.0, LocalDate.of(2025, 3, 11));
            UNCaddPurchases(ps, "4d9a5e43-0c98-4956-aac5-d206fbb1c600", 2.99, 2.0, LocalDate.of(2025, 3, 11));
            UNCaddPurchases(ps, "578e3ad4-097a-453e-aab1-d5abd951f25a", 2.49, 1.0, LocalDate.of(2025, 3, 11));
            UNCaddPurchases(ps, "8e0ca1ee-df41-411b-8591-9be7e4b34125", 3.99, 2.0, LocalDate.of(2025, 3, 11));
            UNCaddPurchases(ps, "ac926a1e-780e-48e5-8dcd-06fe9d204c5c", 12.49, 2.0, LocalDate.of(2025, 3, 11));
            UNCaddPurchases(ps, "c7c4c1e4-a960-4db7-bab9-15b8521157b9", 10.00, 3.0, LocalDate.of(2025, 3, 11));
            UNCaddPurchases(ps, "b9840c75-9807-49ea-8325-75c92e5a902f", 3.49, 1.0, LocalDate.of(2025, 3, 11));
            UNCaddPurchases(ps, "f9809697-3a71-4d52-8347-f0950ab7c5d6", 16.49, 1.0, LocalDate.of(2025, 3, 11));
            UNCaddPurchases(ps, "abefa615-4f4d-4a4e-b882-4a6c33eb1198", 12.49, 2.0, LocalDate.of(2025, 3, 11));
            UNCaddPurchases(ps, "6a52d8e3-7cb0-45a5-ad58-f9a10e836baf", 6.00, 1.0, LocalDate.of(2025, 3, 11));
            UNCaddPurchases(ps, "9350a653-d57c-45d2-ba03-0c4835c7a31d", 5.49, 1.0, LocalDate.of(2025, 3, 11));
            UNCaddPurchases(ps, "6ca597ad-f42f-4ed0-8c4e-81572e58246a", 5.49, 2.0, LocalDate.of(2025, 3, 11));
            UNCaddPurchases(ps, "abefa615-4f4d-4a4e-b882-4a6c33eb1198", 12.99, 1.0, LocalDate.of(2025, 3, 14));
            UNCaddPurchases(ps, "43ae807b-0938-4b7a-b1df-c62308ee283e", 6.00, 1.0, LocalDate.of(2025, 3, 14));
            UNCaddPurchases(ps, "98d7bd17-3fa7-403a-ae36-c25db7a72c39", 8.49, 1.0, LocalDate.of(2025, 3, 14));
            UNCaddPurchases(ps, "b9840c75-9807-49ea-8325-75c92e5a902f", 3.00, 1.0, LocalDate.of(2025, 3, 14));
            UNCaddPurchases(ps, "6a52d8e3-7cb0-45a5-ad58-f9a10e836baf", 6.49, 2.0, LocalDate.of(2025, 3, 14));
            UNCaddPurchases(ps, "fe10d506-1e42-4791-b7f9-094a81f1b50d", 3.49, 2.0, LocalDate.of(2025, 3, 14));
            UNCaddPurchases(ps, "fe10d506-1e42-4791-b7f9-094a81f1b50d", 3.49, 3.0, LocalDate.of(2025, 3, 18));
            UNCaddPurchases(ps, "b9840c75-9807-49ea-8325-75c92e5a902f", 2.99, 2.0, LocalDate.of(2025, 3, 18));
            UNCaddPurchases(ps, "c7c4c1e4-a960-4db7-bab9-15b8521157b9", 10.00, 1.0, LocalDate.of(2025, 3, 18));
            UNCaddPurchases(ps, "a3e279ea-080d-4762-814f-9ba4c4293397", 7.99, 1.0, LocalDate.of(2025, 3, 18));
            UNCaddPurchases(ps, "eb38da48-390e-4673-a0de-a99a37bed2da", 9.99, 2.0, LocalDate.of(2025, 3, 18));
            UNCaddPurchases(ps, "8e0ca1ee-df41-411b-8591-9be7e4b34125", 3.99, 3.0, LocalDate.of(2025, 3, 23));
            UNCaddPurchases(ps, "6ca597ad-f42f-4ed0-8c4e-81572e58246a", 4.99, 3.0, LocalDate.of(2025, 3, 23));
            UNCaddPurchases(ps, "f9809697-3a71-4d52-8347-f0950ab7c5d6", 18.49, 1.0, LocalDate.of(2025, 3, 23));
            UNCaddPurchases(ps, "4d9a5e43-0c98-4956-aac5-d206fbb1c600", 2.99, 1.0, LocalDate.of(2025, 3, 23));
            UNCaddPurchases(ps, "9a9c7178-7fa9-4114-92db-3e08dbb21026", 4.49, 2.0, LocalDate.of(2025, 3, 23));
            UNCaddPurchases(ps, "c7c4c1e4-a960-4db7-bab9-15b8521157b9", 10.00, 1.0, LocalDate.of(2025, 3, 23));
            UNCaddPurchases(ps, "fe10d506-1e42-4791-b7f9-094a81f1b50d", 3.49, 2.0, LocalDate.of(2025, 3, 23));
            UNCaddPurchases(ps, "fe156ac3-7131-4188-830b-d6a4fa030e8f", 4.99, 3.0, LocalDate.of(2025, 3, 23));
            UNCaddPurchases(ps, "9350a653-d57c-45d2-ba03-0c4835c7a31d", 5.00, 2.0, LocalDate.of(2025, 3, 23));
            UNCaddPurchases(ps, "dd143324-3031-40f1-b8f7-b90f946e2b99", 3.99, 3.0, LocalDate.of(2025, 3, 23));
            UNCaddPurchases(ps, "31bdcd6b-a116-4eaf-a0bc-9a8e7287976b", 6.00, 1.0, LocalDate.of(2025, 3, 23));
            UNCaddPurchases(ps, "a8d397b0-e695-4b50-99b1-b2eb57277a79", 14.49, 3.0, LocalDate.of(2025, 3, 23));
            UNCaddPurchases(ps, "3e7d1add-bf1f-40a5-9de6-45a3bf0045c2", 4.99, 3.0, LocalDate.of(2025, 3, 23));
            UNCaddPurchases(ps, "578e3ad4-097a-453e-aab1-d5abd951f25a", 2.49, 3.0, LocalDate.of(2025, 3, 23));
            UNCaddPurchases(ps, "beb1504d-c67c-481f-8bb2-27961dc36946", 8.99, 1.0, LocalDate.of(2025, 3, 23));
            UNCaddPurchases(ps, "eb38da48-390e-4673-a0de-a99a37bed2da", 8.99, 2.0, LocalDate.of(2025, 3, 23));
            UNCaddPurchases(ps, "e1685c2b-aab5-4b1c-87da-fb75c257fd76", 16.49, 1.0, LocalDate.of(2025, 3, 27));
            UNCaddPurchases(ps, "738d56b4-952f-4748-8e63-966c30d23265", 8.00, 2.0, LocalDate.of(2025, 3, 27));
            UNCaddPurchases(ps, "8e0ca1ee-df41-411b-8591-9be7e4b34125", 4.00, 2.0, LocalDate.of(2025, 3, 27));
            UNCaddPurchases(ps, "0953287f-0a6d-4196-8e34-e5aa7c151266", 9.99, 1.0, LocalDate.of(2025, 3, 27));
            UNCaddPurchases(ps, "d4b7d16d-ddb2-4c96-981b-95d01b973355", 26.99, 2.0, LocalDate.of(2025, 3, 27));
            UNCaddPurchases(ps, "ec86defe-7548-4031-8a76-d9ce3c551c9b", 3.49, 3.0, LocalDate.of(2025, 3, 31));
            UNCaddPurchases(ps, "0667113c-7559-4629-b577-e9741f43ef5a", 3.99, 2.0, LocalDate.of(2025, 3, 31));
            UNCaddPurchases(ps, "fe156ac3-7131-4188-830b-d6a4fa030e8f", 5.49, 1.0, LocalDate.of(2025, 3, 31));
            UNCaddPurchases(ps, "ccd80ec8-614b-4968-8aec-04a1fa6ce68d", 3.49, 1.0, LocalDate.of(2025, 3, 31));
            UNCaddPurchases(ps, "8e0ca1ee-df41-411b-8591-9be7e4b34125", 3.99, 2.0, LocalDate.of(2025, 3, 31));
            UNCaddPurchases(ps, "6ca597ad-f42f-4ed0-8c4e-81572e58246a", 5.49, 2.0, LocalDate.of(2025, 3, 31));
            UNCaddPurchases(ps, "738d56b4-952f-4748-8e63-966c30d23265", 8.49, 3.0, LocalDate.of(2025, 4, 5));
            UNCaddPurchases(ps, "ccd80ec8-614b-4968-8aec-04a1fa6ce68d", 3.49, 3.0, LocalDate.of(2025, 4, 5));
            UNCaddPurchases(ps, "a8d397b0-e695-4b50-99b1-b2eb57277a79", 15.00, 2.0, LocalDate.of(2025, 4, 5));
            UNCaddPurchases(ps, "6ca597ad-f42f-4ed0-8c4e-81572e58246a", 4.99, 2.0, LocalDate.of(2025, 4, 5));
            UNCaddPurchases(ps, "fe10d506-1e42-4791-b7f9-094a81f1b50d", 3.49, 2.0, LocalDate.of(2025, 4, 5));
            UNCaddPurchases(ps, "dd143324-3031-40f1-b8f7-b90f946e2b99", 4.00, 2.0, LocalDate.of(2025, 4, 8));
            UNCaddPurchases(ps, "abefa615-4f4d-4a4e-b882-4a6c33eb1198", 11.49, 3.0, LocalDate.of(2025, 4, 8));
            UNCaddPurchases(ps, "8e0ca1ee-df41-411b-8591-9be7e4b34125", 4.00, 2.0, LocalDate.of(2025, 4, 8));
            UNCaddPurchases(ps, "c7c4c1e4-a960-4db7-bab9-15b8521157b9", 10.49, 3.0, LocalDate.of(2025, 4, 8));
            UNCaddPurchases(ps, "578e3ad4-097a-453e-aab1-d5abd951f25a", 2.49, 1.0, LocalDate.of(2025, 4, 8));
            UNCaddPurchases(ps, "9350a653-d57c-45d2-ba03-0c4835c7a31d", 5.00, 1.0, LocalDate.of(2025, 4, 12));
            UNCaddPurchases(ps, "98d7bd17-3fa7-403a-ae36-c25db7a72c39", 9.99, 3.0, LocalDate.of(2025, 4, 12));
            UNCaddPurchases(ps, "0667113c-7559-4629-b577-e9741f43ef5a", 3.99, 2.0, LocalDate.of(2025, 4, 12));
            UNCaddPurchases(ps, "738d56b4-952f-4748-8e63-966c30d23265", 8.00, 1.0, LocalDate.of(2025, 4, 15));
            UNCaddPurchases(ps, "98d7bd17-3fa7-403a-ae36-c25db7a72c39", 9.49, 3.0, LocalDate.of(2025, 4, 15));
            UNCaddPurchases(ps, "ccd80ec8-614b-4968-8aec-04a1fa6ce68d", 3.49, 3.0, LocalDate.of(2025, 4, 15));
            UNCaddPurchases(ps, "a8d397b0-e695-4b50-99b1-b2eb57277a79", 14.99, 1.0, LocalDate.of(2025, 4, 15));
            UNCaddPurchases(ps, "ac926a1e-780e-48e5-8dcd-06fe9d204c5c", 13.49, 2.0, LocalDate.of(2025, 4, 15));
            UNCaddPurchases(ps, "f9809697-3a71-4d52-8347-f0950ab7c5d6", 16.49, 1.0, LocalDate.of(2025, 4, 15));
            UNCaddPurchases(ps, "e1685c2b-aab5-4b1c-87da-fb75c257fd76", 15.99, 1.0, LocalDate.of(2025, 4, 15));
            UNCaddPurchases(ps, "b9840c75-9807-49ea-8325-75c92e5a902f", 2.99, 2.0, LocalDate.of(2025, 4, 15));
            UNCaddPurchases(ps, "0667113c-7559-4629-b577-e9741f43ef5a", 3.49, 2.0, LocalDate.of(2025, 4, 15));
            UNCaddPurchases(ps, "8e0ca1ee-df41-411b-8591-9be7e4b34125", 3.99, 1.0, LocalDate.of(2025, 4, 15));
            UNCaddPurchases(ps, "7fc847da-44b1-4817-9a0a-79703cff2a1c", 10.49, 1.0, LocalDate.of(2025, 4, 15));
            UNCaddPurchases(ps, "d4b7d16d-ddb2-4c96-981b-95d01b973355", 25.00, 2.0, LocalDate.of(2025, 4, 15));
            UNCaddPurchases(ps, "a8d397b0-e695-4b50-99b1-b2eb57277a79", 15.00, 1.0, LocalDate.of(2025, 4, 18));
            UNCaddPurchases(ps, "ec86defe-7548-4031-8a76-d9ce3c551c9b", 3.49, 1.0, LocalDate.of(2025, 4, 18));
            UNCaddPurchases(ps, "31bdcd6b-a116-4eaf-a0bc-9a8e7287976b", 7.49, 1.0, LocalDate.of(2025, 4, 18));
            UNCaddPurchases(ps, "c7c4c1e4-a960-4db7-bab9-15b8521157b9", 10.49, 2.0, LocalDate.of(2025, 4, 18));
            UNCaddPurchases(ps, "738d56b4-952f-4748-8e63-966c30d23265", 7.49, 1.0, LocalDate.of(2025, 4, 18));
            UNCaddPurchases(ps, "6ca597ad-f42f-4ed0-8c4e-81572e58246a", 5.49, 2.0, LocalDate.of(2025, 4, 18));
            UNCaddPurchases(ps, "9035c1bc-7a0d-44d4-a06a-934beca69276", 12.49, 1.0, LocalDate.of(2025, 4, 18));
            UNCaddPurchases(ps, "43ae807b-0938-4b7a-b1df-c62308ee283e", 6.49, 2.0, LocalDate.of(2025, 4, 21));
            UNCaddPurchases(ps, "9a9c7178-7fa9-4114-92db-3e08dbb21026", 5.00, 2.0, LocalDate.of(2025, 4, 21));
            UNCaddPurchases(ps, "0667113c-7559-4629-b577-e9741f43ef5a", 3.00, 3.0, LocalDate.of(2025, 4, 21));
            UNCaddPurchases(ps, "fe156ac3-7131-4188-830b-d6a4fa030e8f", 5.49, 2.0, LocalDate.of(2025, 4, 21));
            UNCaddPurchases(ps, "ccd80ec8-614b-4968-8aec-04a1fa6ce68d", 3.49, 2.0, LocalDate.of(2025, 4, 21));
            UNCaddPurchases(ps, "6ca597ad-f42f-4ed0-8c4e-81572e58246a", 5.00, 3.0, LocalDate.of(2025, 4, 26));
            UNCaddPurchases(ps, "a8d397b0-e695-4b50-99b1-b2eb57277a79", 14.49, 1.0, LocalDate.of(2025, 4, 26));
            UNCaddPurchases(ps, "9a9c7178-7fa9-4114-92db-3e08dbb21026", 5.00, 3.0, LocalDate.of(2025, 4, 26));
            UNCaddPurchases(ps, "c7c4c1e4-a960-4db7-bab9-15b8521157b9", 9.49, 1.0, LocalDate.of(2025, 4, 26));
            UNCaddPurchases(ps, "fe156ac3-7131-4188-830b-d6a4fa030e8f", 4.49, 2.0, LocalDate.of(2025, 4, 26));
            UNCaddPurchases(ps, "6a52d8e3-7cb0-45a5-ad58-f9a10e836baf", 6.49, 1.0, LocalDate.of(2025, 4, 26));
            UNCaddPurchases(ps, "ec86defe-7548-4031-8a76-d9ce3c551c9b", 3.49, 2.0, LocalDate.of(2025, 5, 1));
            UNCaddPurchases(ps, "e631be2c-9959-40f8-9695-c7bbd8966e2d", 4.49, 1.0, LocalDate.of(2025, 5, 1));
            UNCaddPurchases(ps, "fe156ac3-7131-4188-830b-d6a4fa030e8f", 5.49, 2.0, LocalDate.of(2025, 5, 1));
            UNCaddPurchases(ps, "98d7bd17-3fa7-403a-ae36-c25db7a72c39", 9.99, 3.0, LocalDate.of(2025, 5, 1));
            UNCaddPurchases(ps, "31bdcd6b-a116-4eaf-a0bc-9a8e7287976b", 6.49, 2.0, LocalDate.of(2025, 5, 1));
            UNCaddPurchases(ps, "6a52d8e3-7cb0-45a5-ad58-f9a10e836baf", 7.00, 1.0, LocalDate.of(2025, 5, 6));
            UNCaddPurchases(ps, "b9840c75-9807-49ea-8325-75c92e5a902f", 3.00, 1.0, LocalDate.of(2025, 5, 6));
            UNCaddPurchases(ps, "6ca597ad-f42f-4ed0-8c4e-81572e58246a", 5.49, 1.0, LocalDate.of(2025, 5, 6));
            UNCaddPurchases(ps, "0667113c-7559-4629-b577-e9741f43ef5a", 3.49, 1.0, LocalDate.of(2025, 5, 6));
            UNCaddPurchases(ps, "ccd80ec8-614b-4968-8aec-04a1fa6ce68d", 3.00, 1.0, LocalDate.of(2025, 5, 6));
            UNCaddPurchases(ps, "f9809697-3a71-4d52-8347-f0950ab7c5d6", 17.00, 1.0, LocalDate.of(2025, 5, 6));
            UNCaddPurchases(ps, "98d7bd17-3fa7-403a-ae36-c25db7a72c39", 8.49, 3.0, LocalDate.of(2025, 5, 6));
            UNCaddPurchases(ps, "31bdcd6b-a116-4eaf-a0bc-9a8e7287976b", 6.49, 1.0, LocalDate.of(2025, 5, 6));
            UNCaddPurchases(ps, "dd143324-3031-40f1-b8f7-b90f946e2b99", 3.49, 3.0, LocalDate.of(2025, 5, 6));
            UNCaddPurchases(ps, "fe10d506-1e42-4791-b7f9-094a81f1b50d", 3.99, 3.0, LocalDate.of(2025, 5, 6));
            UNCaddPurchases(ps, "abefa615-4f4d-4a4e-b882-4a6c33eb1198", 11.49, 2.0, LocalDate.of(2025, 5, 6));
            UNCaddPurchases(ps, "3e7d1add-bf1f-40a5-9de6-45a3bf0045c2", 4.99, 2.0, LocalDate.of(2025, 5, 6));
            UNCaddPurchases(ps, "9a9c7178-7fa9-4114-92db-3e08dbb21026", 5.00, 2.0, LocalDate.of(2025, 5, 9));
            UNCaddPurchases(ps, "9350a653-d57c-45d2-ba03-0c4835c7a31d", 5.99, 3.0, LocalDate.of(2025, 5, 9));
            UNCaddPurchases(ps, "e631be2c-9959-40f8-9695-c7bbd8966e2d", 4.99, 3.0, LocalDate.of(2025, 5, 9));
            UNCaddPurchases(ps, "43ae807b-0938-4b7a-b1df-c62308ee283e", 6.49, 2.0, LocalDate.of(2025, 5, 13));
            UNCaddPurchases(ps, "a8d397b0-e695-4b50-99b1-b2eb57277a79", 15.49, 2.0, LocalDate.of(2025, 5, 13));
            UNCaddPurchases(ps, "e1685c2b-aab5-4b1c-87da-fb75c257fd76", 15.49, 1.0, LocalDate.of(2025, 5, 13));
            UNCaddPurchases(ps, "ec86defe-7548-4031-8a76-d9ce3c551c9b", 3.49, 3.0, LocalDate.of(2025, 5, 13));
            UNCaddPurchases(ps, "6ca597ad-f42f-4ed0-8c4e-81572e58246a", 4.99, 2.0, LocalDate.of(2025, 5, 13));
            UNCaddPurchases(ps, "ccd80ec8-614b-4968-8aec-04a1fa6ce68d", 3.00, 1.0, LocalDate.of(2025, 5, 13));
            UNCaddPurchases(ps, "4d9a5e43-0c98-4956-aac5-d206fbb1c600", 3.00, 2.0, LocalDate.of(2025, 5, 13));
            UNCaddPurchases(ps, "3e7d1add-bf1f-40a5-9de6-45a3bf0045c2", 4.99, 3.0, LocalDate.of(2025, 5, 13));
            UNCaddPurchases(ps, "abefa615-4f4d-4a4e-b882-4a6c33eb1198", 12.99, 3.0, LocalDate.of(2025, 5, 13));
            UNCaddPurchases(ps, "31bdcd6b-a116-4eaf-a0bc-9a8e7287976b", 6.99, 1.0, LocalDate.of(2025, 5, 13));
            UNCaddPurchases(ps, "ac926a1e-780e-48e5-8dcd-06fe9d204c5c", 12.99, 3.0, LocalDate.of(2025, 5, 13));
            UNCaddPurchases(ps, "9350a653-d57c-45d2-ba03-0c4835c7a31d", 5.00, 2.0, LocalDate.of(2025, 5, 13));
            UNCaddPurchases(ps, "b9840c75-9807-49ea-8325-75c92e5a902f", 3.00, 3.0, LocalDate.of(2025, 5, 13));
            UNCaddPurchases(ps, "3e7d1add-bf1f-40a5-9de6-45a3bf0045c2", 4.49, 2.0, LocalDate.of(2025, 5, 18));
            UNCaddPurchases(ps, "dd143324-3031-40f1-b8f7-b90f946e2b99", 3.49, 3.0, LocalDate.of(2025, 5, 18));
            UNCaddPurchases(ps, "f9809697-3a71-4d52-8347-f0950ab7c5d6", 16.49, 1.0, LocalDate.of(2025, 5, 18));
            UNCaddPurchases(ps, "e1685c2b-aab5-4b1c-87da-fb75c257fd76", 15.49, 2.0, LocalDate.of(2025, 5, 18));
            UNCaddPurchases(ps, "31bdcd6b-a116-4eaf-a0bc-9a8e7287976b", 7.00, 3.0, LocalDate.of(2025, 5, 18));
            UNCaddPurchases(ps, "9350a653-d57c-45d2-ba03-0c4835c7a31d", 5.00, 3.0, LocalDate.of(2025, 5, 18));
            UNCaddPurchases(ps, "9a9c7178-7fa9-4114-92db-3e08dbb21026", 4.49, 3.0, LocalDate.of(2025, 5, 18));
            UNCaddPurchases(ps, "a8d397b0-e695-4b50-99b1-b2eb57277a79", 14.00, 2.0, LocalDate.of(2025, 5, 18));
            UNCaddPurchases(ps, "abefa615-4f4d-4a4e-b882-4a6c33eb1198", 13.00, 2.0, LocalDate.of(2025, 5, 18));
            UNCaddPurchases(ps, "4d9a5e43-0c98-4956-aac5-d206fbb1c600", 3.00, 2.0, LocalDate.of(2025, 5, 18));
            UNCaddPurchases(ps, "ac926a1e-780e-48e5-8dcd-06fe9d204c5c", 13.49, 2.0, LocalDate.of(2025, 5, 18));
            UNCaddPurchases(ps, "738d56b4-952f-4748-8e63-966c30d23265", 8.49, 2.0, LocalDate.of(2025, 5, 18));
            UNCaddPurchases(ps, "9035c1bc-7a0d-44d4-a06a-934beca69276", 12.49, 1.0, LocalDate.of(2025, 5, 18));
            UNCaddPurchases(ps, "fe156ac3-7131-4188-830b-d6a4fa030e8f", 5.49, 2.0, LocalDate.of(2025, 5, 22));
            UNCaddPurchases(ps, "4d9a5e43-0c98-4956-aac5-d206fbb1c600", 3.00, 2.0, LocalDate.of(2025, 5, 22));
            UNCaddPurchases(ps, "3e7d1add-bf1f-40a5-9de6-45a3bf0045c2", 4.00, 3.0, LocalDate.of(2025, 5, 22));
            UNCaddPurchases(ps, "b9840c75-9807-49ea-8325-75c92e5a902f", 3.49, 1.0, LocalDate.of(2025, 5, 22));
            UNCaddPurchases(ps, "738d56b4-952f-4748-8e63-966c30d23265", 7.99, 2.0, LocalDate.of(2025, 5, 22));
            UNCaddPurchases(ps, "43ae807b-0938-4b7a-b1df-c62308ee283e", 6.49, 2.0, LocalDate.of(2025, 5, 22));
            UNCaddPurchases(ps, "2f4c9492-0947-44b0-8f6c-d1bae3d523e6", 8.99, 1.0, LocalDate.of(2025, 5, 22));
            UNCaddPurchases(ps, "b9840c75-9807-49ea-8325-75c92e5a902f", 2.99, 2.0, LocalDate.of(2025, 5, 27));
            UNCaddPurchases(ps, "9350a653-d57c-45d2-ba03-0c4835c7a31d", 5.00, 2.0, LocalDate.of(2025, 5, 27));
            UNCaddPurchases(ps, "c7c4c1e4-a960-4db7-bab9-15b8521157b9", 10.49, 1.0, LocalDate.of(2025, 5, 27));
            UNCaddPurchases(ps, "ac926a1e-780e-48e5-8dcd-06fe9d204c5c", 12.99, 2.0, LocalDate.of(2025, 5, 27));
            UNCaddPurchases(ps, "6a52d8e3-7cb0-45a5-ad58-f9a10e836baf", 7.49, 2.0, LocalDate.of(2025, 6, 1));
            UNCaddPurchases(ps, "a8d397b0-e695-4b50-99b1-b2eb57277a79", 13.00, 2.0, LocalDate.of(2025, 6, 1));
            UNCaddPurchases(ps, "ccd80ec8-614b-4968-8aec-04a1fa6ce68d", 3.49, 2.0, LocalDate.of(2025, 6, 1));
            UNCaddPurchases(ps, "8e0ca1ee-df41-411b-8591-9be7e4b34125", 4.00, 1.0, LocalDate.of(2025, 6, 1));
            UNCaddPurchases(ps, "578e3ad4-097a-453e-aab1-d5abd951f25a", 2.49, 2.0, LocalDate.of(2025, 6, 1));
            UNCaddPurchases(ps, "3e7d1add-bf1f-40a5-9de6-45a3bf0045c2", 4.49, 2.0, LocalDate.of(2025, 6, 1));
            UNCaddPurchases(ps, "81f475a9-0579-43be-85cc-15de4fe6a714", 12.99, 2.0, LocalDate.of(2025, 6, 1));
            UNCaddPurchases(ps, "abefa615-4f4d-4a4e-b882-4a6c33eb1198", 13.49, 2.0, LocalDate.of(2025, 6, 4));
            UNCaddPurchases(ps, "3e7d1add-bf1f-40a5-9de6-45a3bf0045c2", 4.49, 2.0, LocalDate.of(2025, 6, 4));
            UNCaddPurchases(ps, "9a9c7178-7fa9-4114-92db-3e08dbb21026", 5.00, 2.0, LocalDate.of(2025, 6, 4));
            UNCaddPurchases(ps, "fe10d506-1e42-4791-b7f9-094a81f1b50d", 3.49, 2.0, LocalDate.of(2025, 6, 8));
            UNCaddPurchases(ps, "4d9a5e43-0c98-4956-aac5-d206fbb1c600", 2.99, 3.0, LocalDate.of(2025, 6, 8));
            UNCaddPurchases(ps, "738d56b4-952f-4748-8e63-966c30d23265", 9.00, 1.0, LocalDate.of(2025, 6, 8));
            UNCaddPurchases(ps, "3e7d1add-bf1f-40a5-9de6-45a3bf0045c2", 4.99, 3.0, LocalDate.of(2025, 6, 8));
            UNCaddPurchases(ps, "578e3ad4-097a-453e-aab1-d5abd951f25a", 2.49, 3.0, LocalDate.of(2025, 6, 8));
            UNCaddPurchases(ps, "43ae807b-0938-4b7a-b1df-c62308ee283e", 6.49, 1.0, LocalDate.of(2025, 6, 8));
            UNCaddPurchases(ps, "6a52d8e3-7cb0-45a5-ad58-f9a10e836baf", 6.49, 2.0, LocalDate.of(2025, 6, 8));
            UNCaddPurchases(ps, "fe156ac3-7131-4188-830b-d6a4fa030e8f", 5.49, 2.0, LocalDate.of(2025, 6, 8));
            UNCaddPurchases(ps, "ec86defe-7548-4031-8a76-d9ce3c551c9b", 3.49, 2.0, LocalDate.of(2025, 6, 8));
            UNCaddPurchases(ps, "c7c4c1e4-a960-4db7-bab9-15b8521157b9", 10.49, 2.0, LocalDate.of(2025, 6, 8));
            UNCaddPurchases(ps, "a8d397b0-e695-4b50-99b1-b2eb57277a79", 13.00, 2.0, LocalDate.of(2025, 6, 8));
            UNCaddPurchases(ps, "e1685c2b-aab5-4b1c-87da-fb75c257fd76", 16.99, 3.0, LocalDate.of(2025, 6, 8));
            UNCaddPurchases(ps, "0667113c-7559-4629-b577-e9741f43ef5a", 3.49, 2.0, LocalDate.of(2025, 6, 8));
            UNCaddPurchases(ps, "ec86defe-7548-4031-8a76-d9ce3c551c9b", 3.00, 2.0, LocalDate.of(2025, 6, 11));
            UNCaddPurchases(ps, "ac926a1e-780e-48e5-8dcd-06fe9d204c5c", 12.49, 2.0, LocalDate.of(2025, 6, 11));
            UNCaddPurchases(ps, "e631be2c-9959-40f8-9695-c7bbd8966e2d", 4.49, 1.0, LocalDate.of(2025, 6, 11));
            UNCaddPurchases(ps, "738d56b4-952f-4748-8e63-966c30d23265", 8.00, 2.0, LocalDate.of(2025, 6, 15));
            UNCaddPurchases(ps, "578e3ad4-097a-453e-aab1-d5abd951f25a", 2.49, 2.0, LocalDate.of(2025, 6, 15));
            UNCaddPurchases(ps, "abefa615-4f4d-4a4e-b882-4a6c33eb1198", 12.00, 3.0, LocalDate.of(2025, 6, 15));
            UNCaddPurchases(ps, "e1685c2b-aab5-4b1c-87da-fb75c257fd76", 16.49, 2.0, LocalDate.of(2025, 6, 15));
            UNCaddPurchases(ps, "98d7bd17-3fa7-403a-ae36-c25db7a72c39", 9.99, 1.0, LocalDate.of(2025, 6, 15));
            UNCaddPurchases(ps, "4d9a5e43-0c98-4956-aac5-d206fbb1c600", 3.00, 3.0, LocalDate.of(2025, 6, 15));
            UNCaddPurchases(ps, "fe10d506-1e42-4791-b7f9-094a81f1b50d", 3.49, 3.0, LocalDate.of(2025, 6, 15));
            UNCaddPurchases(ps, "e631be2c-9959-40f8-9695-c7bbd8966e2d", 5.00, 2.0, LocalDate.of(2025, 6, 15));
            UNCaddPurchases(ps, "9350a653-d57c-45d2-ba03-0c4835c7a31d", 5.49, 3.0, LocalDate.of(2025, 6, 15));
            UNCaddPurchases(ps, "ac926a1e-780e-48e5-8dcd-06fe9d204c5c", 13.99, 3.0, LocalDate.of(2025, 6, 15));
            UNCaddPurchases(ps, "a8d397b0-e695-4b50-99b1-b2eb57277a79", 14.49, 1.0, LocalDate.of(2025, 6, 15));
            UNCaddPurchases(ps, "6ca597ad-f42f-4ed0-8c4e-81572e58246a", 4.49, 3.0, LocalDate.of(2025, 6, 15));
            UNCaddPurchases(ps, "c7c4c1e4-a960-4db7-bab9-15b8521157b9", 9.99, 3.0, LocalDate.of(2025, 6, 15));
            UNCaddPurchases(ps, "3e7d1add-bf1f-40a5-9de6-45a3bf0045c2", 4.49, 3.0, LocalDate.of(2025, 6, 15));
            UNCaddPurchases(ps, "81f475a9-0579-43be-85cc-15de4fe6a714", 11.00, 2.0, LocalDate.of(2025, 6, 15));
            UNCaddPurchases(ps, "31bdcd6b-a116-4eaf-a0bc-9a8e7287976b", 6.49, 3.0, LocalDate.of(2025, 6, 18));
            UNCaddPurchases(ps, "fe156ac3-7131-4188-830b-d6a4fa030e8f", 5.49, 3.0, LocalDate.of(2025, 6, 18));
            UNCaddPurchases(ps, "98d7bd17-3fa7-403a-ae36-c25db7a72c39", 9.49, 2.0, LocalDate.of(2025, 6, 18));
            UNCaddPurchases(ps, "4d9a5e43-0c98-4956-aac5-d206fbb1c600", 3.00, 1.0, LocalDate.of(2025, 6, 18));
            UNCaddPurchases(ps, "9a9c7178-7fa9-4114-92db-3e08dbb21026", 4.99, 2.0, LocalDate.of(2025, 6, 21));
            UNCaddPurchases(ps, "6a52d8e3-7cb0-45a5-ad58-f9a10e836baf", 6.49, 2.0, LocalDate.of(2025, 6, 21));
            UNCaddPurchases(ps, "e1685c2b-aab5-4b1c-87da-fb75c257fd76", 15.00, 2.0, LocalDate.of(2025, 6, 21));
            UNCaddPurchases(ps, "ec86defe-7548-4031-8a76-d9ce3c551c9b", 3.00, 2.0, LocalDate.of(2025, 6, 24));
            UNCaddPurchases(ps, "3e7d1add-bf1f-40a5-9de6-45a3bf0045c2", 4.99, 3.0, LocalDate.of(2025, 6, 24));
            UNCaddPurchases(ps, "fe10d506-1e42-4791-b7f9-094a81f1b50d", 3.49, 1.0, LocalDate.of(2025, 6, 24));
            UNCaddPurchases(ps, "4d9a5e43-0c98-4956-aac5-d206fbb1c600", 3.00, 1.0, LocalDate.of(2025, 6, 24));
            UNCaddPurchases(ps, "abefa615-4f4d-4a4e-b882-4a6c33eb1198", 12.99, 1.0, LocalDate.of(2025, 6, 24));
            UNCaddPurchases(ps, "ccd80ec8-614b-4968-8aec-04a1fa6ce68d", 3.49, 3.0, LocalDate.of(2025, 6, 24));
            UNCaddPurchases(ps, "f9809697-3a71-4d52-8347-f0950ab7c5d6", 17.49, 2.0, LocalDate.of(2025, 6, 24));
            UNCaddPurchases(ps, "9350a653-d57c-45d2-ba03-0c4835c7a31d", 5.49, 3.0, LocalDate.of(2025, 6, 24));
            UNCaddPurchases(ps, "e631be2c-9959-40f8-9695-c7bbd8966e2d", 4.99, 2.0, LocalDate.of(2025, 6, 24));
            UNCaddPurchases(ps, "d23f242b-91ac-446f-b38b-a4b0214baefa", 11.00, 3.0, LocalDate.of(2025, 6, 24));
            UNCaddPurchases(ps, "6a52d8e3-7cb0-45a5-ad58-f9a10e836baf", 7.49, 1.0, LocalDate.of(2025, 6, 24));
            UNCaddPurchases(ps, "6ca597ad-f42f-4ed0-8c4e-81572e58246a", 4.99, 3.0, LocalDate.of(2025, 6, 24));
            UNCaddPurchases(ps, "a8d397b0-e695-4b50-99b1-b2eb57277a79", 14.00, 3.0, LocalDate.of(2025, 6, 24));
            UNCaddPurchases(ps, "8e0ca1ee-df41-411b-8591-9be7e4b34125", 4.49, 1.0, LocalDate.of(2025, 6, 28));
            UNCaddPurchases(ps, "578e3ad4-097a-453e-aab1-d5abd951f25a", 2.49, 1.0, LocalDate.of(2025, 6, 28));
            UNCaddPurchases(ps, "c7c4c1e4-a960-4db7-bab9-15b8521157b9", 9.49, 3.0, LocalDate.of(2025, 6, 28));
            UNCaddPurchases(ps, "fe10d506-1e42-4791-b7f9-094a81f1b50d", 3.99, 1.0, LocalDate.of(2025, 6, 28));
            UNCaddPurchases(ps, "a3e279ea-080d-4762-814f-9ba4c4293397", 7.00, 2.0, LocalDate.of(2025, 6, 28));
            UNCaddPurchases(ps, "beb1504d-c67c-481f-8bb2-27961dc36946", 8.99, 2.0, LocalDate.of(2025, 6, 28));
            UNCaddPurchases(ps, "3e7d1add-bf1f-40a5-9de6-45a3bf0045c2", 4.49, 2.0, LocalDate.of(2025, 7, 3));
            UNCaddPurchases(ps, "0667113c-7559-4629-b577-e9741f43ef5a", 3.49, 3.0, LocalDate.of(2025, 7, 3));
            UNCaddPurchases(ps, "b9840c75-9807-49ea-8325-75c92e5a902f", 3.49, 2.0, LocalDate.of(2025, 7, 3));
            UNCaddPurchases(ps, "f9809697-3a71-4d52-8347-f0950ab7c5d6", 18.49, 2.0, LocalDate.of(2025, 7, 3));
            UNCaddPurchases(ps, "abefa615-4f4d-4a4e-b882-4a6c33eb1198", 12.99, 2.0, LocalDate.of(2025, 7, 3));
            UNCaddPurchases(ps, "ac926a1e-780e-48e5-8dcd-06fe9d204c5c", 13.99, 2.0, LocalDate.of(2025, 7, 3));
            UNCaddPurchases(ps, "6a52d8e3-7cb0-45a5-ad58-f9a10e836baf", 6.49, 2.0, LocalDate.of(2025, 7, 3));
            UNCaddPurchases(ps, "9a9c7178-7fa9-4114-92db-3e08dbb21026", 4.99, 3.0, LocalDate.of(2025, 7, 3));
            UNCaddPurchases(ps, "e1685c2b-aab5-4b1c-87da-fb75c257fd76", 14.49, 2.0, LocalDate.of(2025, 7, 3));
            UNCaddPurchases(ps, "98d7bd17-3fa7-403a-ae36-c25db7a72c39", 9.49, 2.0, LocalDate.of(2025, 7, 3));
            UNCaddPurchases(ps, "d23f242b-91ac-446f-b38b-a4b0214baefa", 12.49, 1.0, LocalDate.of(2025, 7, 3));
            UNCaddPurchases(ps, "ccd80ec8-614b-4968-8aec-04a1fa6ce68d", 3.49, 1.0, LocalDate.of(2025, 7, 3));
            UNCaddPurchases(ps, "738d56b4-952f-4748-8e63-966c30d23265", 7.99, 1.0, LocalDate.of(2025, 7, 3));
            UNCaddPurchases(ps, "fe10d506-1e42-4791-b7f9-094a81f1b50d", 3.99, 3.0, LocalDate.of(2025, 7, 3));
            UNCaddPurchases(ps, "a3e279ea-080d-4762-814f-9ba4c4293397", 8.49, 1.0, LocalDate.of(2025, 7, 3));
            UNCaddPurchases(ps, "d23f242b-91ac-446f-b38b-a4b0214baefa", 12.00, 1.0, LocalDate.of(2025, 7, 7));
            UNCaddPurchases(ps, "ac926a1e-780e-48e5-8dcd-06fe9d204c5c", 12.99, 1.0, LocalDate.of(2025, 7, 7));
            UNCaddPurchases(ps, "ec86defe-7548-4031-8a76-d9ce3c551c9b", 3.49, 3.0, LocalDate.of(2025, 7, 7));
            UNCaddPurchases(ps, "6ca597ad-f42f-4ed0-8c4e-81572e58246a", 5.49, 2.0, LocalDate.of(2025, 7, 7));
            UNCaddPurchases(ps, "b9840c75-9807-49ea-8325-75c92e5a902f", 2.99, 2.0, LocalDate.of(2025, 7, 7));
            UNCaddPurchases(ps, "8e0ca1ee-df41-411b-8591-9be7e4b34125", 3.99, 2.0, LocalDate.of(2025, 7, 7));
            UNCaddPurchases(ps, "a3e279ea-080d-4762-814f-9ba4c4293397", 8.49, 1.0, LocalDate.of(2025, 7, 7));
            UNCaddPurchases(ps, "eb38da48-390e-4673-a0de-a99a37bed2da", 9.99, 2.0, LocalDate.of(2025, 7, 7));
            UNCaddPurchases(ps, "abefa615-4f4d-4a4e-b882-4a6c33eb1198", 12.00, 1.0, LocalDate.of(2025, 7, 11));
            UNCaddPurchases(ps, "fe156ac3-7131-4188-830b-d6a4fa030e8f", 5.49, 1.0, LocalDate.of(2025, 7, 11));
            UNCaddPurchases(ps, "e1685c2b-aab5-4b1c-87da-fb75c257fd76", 15.49, 3.0, LocalDate.of(2025, 7, 11));
            UNCaddPurchases(ps, "ac926a1e-780e-48e5-8dcd-06fe9d204c5c", 14.49, 3.0, LocalDate.of(2025, 7, 11));
            UNCaddPurchases(ps, "738d56b4-952f-4748-8e63-966c30d23265", 7.99, 1.0, LocalDate.of(2025, 7, 16));
            UNCaddPurchases(ps, "8e0ca1ee-df41-411b-8591-9be7e4b34125", 4.49, 2.0, LocalDate.of(2025, 7, 16));
            UNCaddPurchases(ps, "ccd80ec8-614b-4968-8aec-04a1fa6ce68d", 3.49, 2.0, LocalDate.of(2025, 7, 16));
            UNCaddPurchases(ps, "dd143324-3031-40f1-b8f7-b90f946e2b99", 4.49, 2.0, LocalDate.of(2025, 7, 16));
            UNCaddPurchases(ps, "6ca597ad-f42f-4ed0-8c4e-81572e58246a", 4.49, 2.0, LocalDate.of(2025, 7, 16));
            UNCaddPurchases(ps, "fe10d506-1e42-4791-b7f9-094a81f1b50d", 3.99, 2.0, LocalDate.of(2025, 7, 16));
            UNCaddPurchases(ps, "f9809697-3a71-4d52-8347-f0950ab7c5d6", 17.00, 2.0, LocalDate.of(2025, 7, 16));
            UNCaddPurchases(ps, "d23f242b-91ac-446f-b38b-a4b0214baefa", 12.49, 3.0, LocalDate.of(2025, 7, 16));
            UNCaddPurchases(ps, "31bdcd6b-a116-4eaf-a0bc-9a8e7287976b", 7.49, 3.0, LocalDate.of(2025, 7, 16));
            UNCaddPurchases(ps, "6a52d8e3-7cb0-45a5-ad58-f9a10e836baf", 6.99, 3.0, LocalDate.of(2025, 7, 16));
            UNCaddPurchases(ps, "abefa615-4f4d-4a4e-b882-4a6c33eb1198", 12.49, 2.0, LocalDate.of(2025, 7, 16));
            UNCaddPurchases(ps, "4d9a5e43-0c98-4956-aac5-d206fbb1c600", 3.00, 3.0, LocalDate.of(2025, 7, 16));
            UNCaddPurchases(ps, "a8d397b0-e695-4b50-99b1-b2eb57277a79", 13.99, 1.0, LocalDate.of(2025, 7, 16));
            UNCaddPurchases(ps, "9a9c7178-7fa9-4114-92db-3e08dbb21026", 5.49, 1.0, LocalDate.of(2025, 7, 16));
            UNCaddPurchases(ps, "a018eda9-831e-4515-b806-2631ca138c98", 17.00, 2.0, LocalDate.of(2025, 7, 16));
            UNCaddPurchases(ps, "81f475a9-0579-43be-85cc-15de4fe6a714", 13.00, 1.0, LocalDate.of(2025, 7, 16));
            UNCaddPurchases(ps, "ec86defe-7548-4031-8a76-d9ce3c551c9b", 3.00, 2.0, LocalDate.of(2025, 7, 20));
            UNCaddPurchases(ps, "738d56b4-952f-4748-8e63-966c30d23265", 7.99, 3.0, LocalDate.of(2025, 7, 20));
            UNCaddPurchases(ps, "b9840c75-9807-49ea-8325-75c92e5a902f", 2.99, 2.0, LocalDate.of(2025, 7, 20));
            UNCaddPurchases(ps, "d23f242b-91ac-446f-b38b-a4b0214baefa", 11.49, 2.0, LocalDate.of(2025, 7, 20));
            UNCaddPurchases(ps, "fe156ac3-7131-4188-830b-d6a4fa030e8f", 4.99, 3.0, LocalDate.of(2025, 7, 20));
            UNCaddPurchases(ps, "f9809697-3a71-4d52-8347-f0950ab7c5d6", 18.49, 3.0, LocalDate.of(2025, 7, 20));
            UNCaddPurchases(ps, "ac926a1e-780e-48e5-8dcd-06fe9d204c5c", 12.49, 1.0, LocalDate.of(2025, 7, 20));
            UNCaddPurchases(ps, "3e7d1add-bf1f-40a5-9de6-45a3bf0045c2", 5.00, 3.0, LocalDate.of(2025, 7, 20));
            UNCaddPurchases(ps, "dd143324-3031-40f1-b8f7-b90f946e2b99", 4.00, 2.0, LocalDate.of(2025, 7, 20));
            UNCaddPurchases(ps, "0667113c-7559-4629-b577-e9741f43ef5a", 3.49, 1.0, LocalDate.of(2025, 7, 20));
            UNCaddPurchases(ps, "578e3ad4-097a-453e-aab1-d5abd951f25a", 2.49, 1.0, LocalDate.of(2025, 7, 20));
            UNCaddPurchases(ps, "8e0ca1ee-df41-411b-8591-9be7e4b34125", 4.49, 1.0, LocalDate.of(2025, 7, 20));
            UNCaddPurchases(ps, "fe10d506-1e42-4791-b7f9-094a81f1b50d", 3.49, 3.0, LocalDate.of(2025, 7, 20));
            UNCaddPurchases(ps, "6ca597ad-f42f-4ed0-8c4e-81572e58246a", 5.00, 1.0, LocalDate.of(2025, 7, 20));
            UNCaddPurchases(ps, "9035c1bc-7a0d-44d4-a06a-934beca69276", 14.99, 2.0, LocalDate.of(2025, 7, 20));
            UNCaddPurchases(ps, "beb1504d-c67c-481f-8bb2-27961dc36946", 9.49, 1.0, LocalDate.of(2025, 7, 20));
            UNCaddPurchases(ps, "d23f242b-91ac-446f-b38b-a4b0214baefa", 12.99, 2.0, LocalDate.of(2025, 7, 24));
            UNCaddPurchases(ps, "9a9c7178-7fa9-4114-92db-3e08dbb21026", 4.49, 3.0, LocalDate.of(2025, 7, 24));
            UNCaddPurchases(ps, "f9809697-3a71-4d52-8347-f0950ab7c5d6", 19.00, 1.0, LocalDate.of(2025, 7, 24));
            UNCaddPurchases(ps, "fe156ac3-7131-4188-830b-d6a4fa030e8f", 5.00, 2.0, LocalDate.of(2025, 7, 24));
            UNCaddPurchases(ps, "ccd80ec8-614b-4968-8aec-04a1fa6ce68d", 3.49, 2.0, LocalDate.of(2025, 7, 24));
            UNCaddPurchases(ps, "4d9a5e43-0c98-4956-aac5-d206fbb1c600", 3.00, 1.0, LocalDate.of(2025, 7, 24));
            UNCaddPurchases(ps, "9350a653-d57c-45d2-ba03-0c4835c7a31d", 6.00, 1.0, LocalDate.of(2025, 7, 24));
            UNCaddPurchases(ps, "8e0ca1ee-df41-411b-8591-9be7e4b34125", 4.00, 3.0, LocalDate.of(2025, 7, 24));
            UNCaddPurchases(ps, "ac926a1e-780e-48e5-8dcd-06fe9d204c5c", 12.49, 3.0, LocalDate.of(2025, 7, 24));
            UNCaddPurchases(ps, "e631be2c-9959-40f8-9695-c7bbd8966e2d", 5.49, 1.0, LocalDate.of(2025, 7, 24));
            UNCaddPurchases(ps, "43ae807b-0938-4b7a-b1df-c62308ee283e", 5.99, 3.0, LocalDate.of(2025, 7, 24));
            UNCaddPurchases(ps, "738d56b4-952f-4748-8e63-966c30d23265", 7.49, 2.0, LocalDate.of(2025, 7, 28));
            UNCaddPurchases(ps, "dd143324-3031-40f1-b8f7-b90f946e2b99", 3.99, 2.0, LocalDate.of(2025, 7, 28));
            UNCaddPurchases(ps, "fe10d506-1e42-4791-b7f9-094a81f1b50d", 3.99, 1.0, LocalDate.of(2025, 7, 28));
            UNCaddPurchases(ps, "4d9a5e43-0c98-4956-aac5-d206fbb1c600", 3.00, 2.0, LocalDate.of(2025, 7, 28));
            UNCaddPurchases(ps, "ccd80ec8-614b-4968-8aec-04a1fa6ce68d", 3.49, 1.0, LocalDate.of(2025, 7, 28));
            UNCaddPurchases(ps, "ac926a1e-780e-48e5-8dcd-06fe9d204c5c", 13.00, 1.0, LocalDate.of(2025, 7, 28));
            UNCaddPurchases(ps, "d23f242b-91ac-446f-b38b-a4b0214baefa", 12.49, 1.0, LocalDate.of(2025, 7, 28));
            UNCaddPurchases(ps, "e631be2c-9959-40f8-9695-c7bbd8966e2d", 4.99, 2.0, LocalDate.of(2025, 7, 28));
            UNCaddPurchases(ps, "31bdcd6b-a116-4eaf-a0bc-9a8e7287976b", 6.99, 1.0, LocalDate.of(2025, 7, 28));
            UNCaddPurchases(ps, "9a9c7178-7fa9-4114-92db-3e08dbb21026", 4.99, 2.0, LocalDate.of(2025, 7, 28));
            UNCaddPurchases(ps, "e1685c2b-aab5-4b1c-87da-fb75c257fd76", 16.99, 2.0, LocalDate.of(2025, 8, 1));
            UNCaddPurchases(ps, "43ae807b-0938-4b7a-b1df-c62308ee283e", 5.49, 1.0, LocalDate.of(2025, 8, 1));
            UNCaddPurchases(ps, "d23f242b-91ac-446f-b38b-a4b0214baefa", 12.99, 3.0, LocalDate.of(2025, 8, 1));
            UNCaddPurchases(ps, "6ca597ad-f42f-4ed0-8c4e-81572e58246a", 4.99, 1.0, LocalDate.of(2025, 8, 1));
            UNCaddPurchases(ps, "6a52d8e3-7cb0-45a5-ad58-f9a10e836baf", 6.99, 1.0, LocalDate.of(2025, 8, 1));
            UNCaddPurchases(ps, "fe10d506-1e42-4791-b7f9-094a81f1b50d", 3.49, 2.0, LocalDate.of(2025, 8, 1));
            UNCaddPurchases(ps, "578e3ad4-097a-453e-aab1-d5abd951f25a", 2.99, 1.0, LocalDate.of(2025, 8, 1));
            UNCaddPurchases(ps, "4d9a5e43-0c98-4956-aac5-d206fbb1c600", 2.99, 1.0, LocalDate.of(2025, 8, 1));
            UNCaddPurchases(ps, "0667113c-7559-4629-b577-e9741f43ef5a", 3.49, 1.0, LocalDate.of(2025, 8, 1));
            UNCaddPurchases(ps, "a8d397b0-e695-4b50-99b1-b2eb57277a79", 14.00, 1.0, LocalDate.of(2025, 8, 1));
            UNCaddPurchases(ps, "ec86defe-7548-4031-8a76-d9ce3c551c9b", 3.49, 1.0, LocalDate.of(2025, 8, 1));
            UNCaddPurchases(ps, "f9809697-3a71-4d52-8347-f0950ab7c5d6", 17.99, 3.0, LocalDate.of(2025, 8, 1));
            UNCaddPurchases(ps, "3e7d1add-bf1f-40a5-9de6-45a3bf0045c2", 5.00, 2.0, LocalDate.of(2025, 8, 1));
            UNCaddPurchases(ps, "9350a653-d57c-45d2-ba03-0c4835c7a31d", 5.49, 3.0, LocalDate.of(2025, 8, 1));
            UNCaddPurchases(ps, "ccd80ec8-614b-4968-8aec-04a1fa6ce68d", 3.49, 3.0, LocalDate.of(2025, 8, 6));
            UNCaddPurchases(ps, "ec86defe-7548-4031-8a76-d9ce3c551c9b", 3.99, 3.0, LocalDate.of(2025, 8, 6));
            UNCaddPurchases(ps, "fe156ac3-7131-4188-830b-d6a4fa030e8f", 4.99, 1.0, LocalDate.of(2025, 8, 6));
            UNCaddPurchases(ps, "9a9c7178-7fa9-4114-92db-3e08dbb21026", 5.00, 3.0, LocalDate.of(2025, 8, 6));
            UNCaddPurchases(ps, "dd143324-3031-40f1-b8f7-b90f946e2b99", 4.49, 1.0, LocalDate.of(2025, 8, 6));
            UNCaddPurchases(ps, "e1685c2b-aab5-4b1c-87da-fb75c257fd76", 15.49, 3.0, LocalDate.of(2025, 8, 6));
            UNCaddPurchases(ps, "4d9a5e43-0c98-4956-aac5-d206fbb1c600", 3.00, 1.0, LocalDate.of(2025, 8, 6));
            UNCaddPurchases(ps, "f9809697-3a71-4d52-8347-f0950ab7c5d6", 17.49, 1.0, LocalDate.of(2025, 8, 6));
            UNCaddPurchases(ps, "98d7bd17-3fa7-403a-ae36-c25db7a72c39", 9.99, 1.0, LocalDate.of(2025, 8, 6));
            UNCaddPurchases(ps, "d23f242b-91ac-446f-b38b-a4b0214baefa", 11.49, 3.0, LocalDate.of(2025, 8, 6));
            UNCaddPurchases(ps, "ac926a1e-780e-48e5-8dcd-06fe9d204c5c", 14.49, 1.0, LocalDate.of(2025, 8, 6));
            UNCaddPurchases(ps, "738d56b4-952f-4748-8e63-966c30d23265", 8.00, 1.0, LocalDate.of(2025, 8, 6));
            UNCaddPurchases(ps, "98d7bd17-3fa7-403a-ae36-c25db7a72c39", 9.00, 3.0, LocalDate.of(2025, 8, 10));
            UNCaddPurchases(ps, "fe10d506-1e42-4791-b7f9-094a81f1b50d", 3.49, 3.0, LocalDate.of(2025, 8, 10));
            UNCaddPurchases(ps, "ccd80ec8-614b-4968-8aec-04a1fa6ce68d", 3.49, 2.0, LocalDate.of(2025, 8, 10));
            UNCaddPurchases(ps, "a8d397b0-e695-4b50-99b1-b2eb57277a79", 14.49, 1.0, LocalDate.of(2025, 8, 10));
            UNCaddPurchases(ps, "e1685c2b-aab5-4b1c-87da-fb75c257fd76", 16.99, 3.0, LocalDate.of(2025, 8, 10));
            UNCaddPurchases(ps, "9a9c7178-7fa9-4114-92db-3e08dbb21026", 5.00, 2.0, LocalDate.of(2025, 8, 10));
            UNCaddPurchases(ps, "9350a653-d57c-45d2-ba03-0c4835c7a31d", 5.49, 3.0, LocalDate.of(2025, 8, 10));
            UNCaddPurchases(ps, "4d9a5e43-0c98-4956-aac5-d206fbb1c600", 3.49, 1.0, LocalDate.of(2025, 8, 10));
            UNCaddPurchases(ps, "abefa615-4f4d-4a4e-b882-4a6c33eb1198", 11.99, 3.0, LocalDate.of(2025, 8, 10));
            UNCaddPurchases(ps, "fe156ac3-7131-4188-830b-d6a4fa030e8f", 5.00, 3.0, LocalDate.of(2025, 8, 10));
            UNCaddPurchases(ps, "b9840c75-9807-49ea-8325-75c92e5a902f", 3.00, 1.0, LocalDate.of(2025, 8, 13));
            UNCaddPurchases(ps, "d23f242b-91ac-446f-b38b-a4b0214baefa", 12.49, 1.0, LocalDate.of(2025, 8, 13));
            UNCaddPurchases(ps, "fe10d506-1e42-4791-b7f9-094a81f1b50d", 3.49, 3.0, LocalDate.of(2025, 8, 13));
            UNCaddPurchases(ps, "e1685c2b-aab5-4b1c-87da-fb75c257fd76", 15.00, 3.0, LocalDate.of(2025, 8, 18));
            UNCaddPurchases(ps, "d23f242b-91ac-446f-b38b-a4b0214baefa", 11.49, 3.0, LocalDate.of(2025, 8, 18));
            UNCaddPurchases(ps, "578e3ad4-097a-453e-aab1-d5abd951f25a", 2.49, 1.0, LocalDate.of(2025, 8, 18));
            UNCaddPurchases(ps, "9a9c7178-7fa9-4114-92db-3e08dbb21026", 5.00, 3.0, LocalDate.of(2025, 8, 18));
            UNCaddPurchases(ps, "738d56b4-952f-4748-8e63-966c30d23265", 8.99, 2.0, LocalDate.of(2025, 8, 18));
            UNCaddPurchases(ps, "ccd80ec8-614b-4968-8aec-04a1fa6ce68d", 3.49, 2.0, LocalDate.of(2025, 8, 18));
            UNCaddPurchases(ps, "a8d397b0-e695-4b50-99b1-b2eb57277a79", 14.49, 1.0, LocalDate.of(2025, 8, 18));
            UNCaddPurchases(ps, "98d7bd17-3fa7-403a-ae36-c25db7a72c39", 9.00, 1.0, LocalDate.of(2025, 8, 18));
            UNCaddPurchases(ps, "c7c4c1e4-a960-4db7-bab9-15b8521157b9", 11.49, 3.0, LocalDate.of(2025, 8, 18));
            UNCaddPurchases(ps, "31bdcd6b-a116-4eaf-a0bc-9a8e7287976b", 6.99, 1.0, LocalDate.of(2025, 8, 18));
            UNCaddPurchases(ps, "4d9a5e43-0c98-4956-aac5-d206fbb1c600", 3.49, 1.0, LocalDate.of(2025, 8, 18));
            UNCaddPurchases(ps, "7fc847da-44b1-4817-9a0a-79703cff2a1c", 10.49, 2.0, LocalDate.of(2025, 8, 18));
            UNCaddPurchases(ps, "ac926a1e-780e-48e5-8dcd-06fe9d204c5c", 13.00, 3.0, LocalDate.of(2025, 8, 22));
            UNCaddPurchases(ps, "8e0ca1ee-df41-411b-8591-9be7e4b34125", 3.99, 3.0, LocalDate.of(2025, 8, 22));
            UNCaddPurchases(ps, "e631be2c-9959-40f8-9695-c7bbd8966e2d", 4.99, 3.0, LocalDate.of(2025, 8, 22));
            UNCaddPurchases(ps, "6a52d8e3-7cb0-45a5-ad58-f9a10e836baf", 6.00, 3.0, LocalDate.of(2025, 8, 22));
            UNCaddPurchases(ps, "ccd80ec8-614b-4968-8aec-04a1fa6ce68d", 3.49, 1.0, LocalDate.of(2025, 8, 22));
            UNCaddPurchases(ps, "9a9c7178-7fa9-4114-92db-3e08dbb21026", 4.49, 1.0, LocalDate.of(2025, 8, 22));
            UNCaddPurchases(ps, "abefa615-4f4d-4a4e-b882-4a6c33eb1198", 12.49, 3.0, LocalDate.of(2025, 8, 22));
            UNCaddPurchases(ps, "6ca597ad-f42f-4ed0-8c4e-81572e58246a", 5.49, 2.0, LocalDate.of(2025, 8, 22));
            UNCaddPurchases(ps, "738d56b4-952f-4748-8e63-966c30d23265", 8.49, 2.0, LocalDate.of(2025, 8, 22));
            UNCaddPurchases(ps, "3e7d1add-bf1f-40a5-9de6-45a3bf0045c2", 4.49, 2.0, LocalDate.of(2025, 8, 22));
            UNCaddPurchases(ps, "f9809697-3a71-4d52-8347-f0950ab7c5d6", 19.49, 3.0, LocalDate.of(2025, 8, 22));
            UNCaddPurchases(ps, "3e7d1add-bf1f-40a5-9de6-45a3bf0045c2", 4.99, 1.0, LocalDate.of(2025, 8, 27));
            UNCaddPurchases(ps, "8e0ca1ee-df41-411b-8591-9be7e4b34125", 3.99, 2.0, LocalDate.of(2025, 8, 27));
            UNCaddPurchases(ps, "578e3ad4-097a-453e-aab1-d5abd951f25a", 2.49, 3.0, LocalDate.of(2025, 8, 27));
            UNCaddPurchases(ps, "9a9c7178-7fa9-4114-92db-3e08dbb21026", 5.00, 1.0, LocalDate.of(2025, 8, 27));
            UNCaddPurchases(ps, "c7c4c1e4-a960-4db7-bab9-15b8521157b9", 10.00, 2.0, LocalDate.of(2025, 8, 27));
            UNCaddPurchases(ps, "43ae807b-0938-4b7a-b1df-c62308ee283e", 5.99, 3.0, LocalDate.of(2025, 9, 1));
            UNCaddPurchases(ps, "c7c4c1e4-a960-4db7-bab9-15b8521157b9", 11.00, 1.0, LocalDate.of(2025, 9, 1));
            UNCaddPurchases(ps, "31bdcd6b-a116-4eaf-a0bc-9a8e7287976b", 6.49, 1.0, LocalDate.of(2025, 9, 1));
            UNCaddPurchases(ps, "a8d397b0-e695-4b50-99b1-b2eb57277a79", 13.49, 2.0, LocalDate.of(2025, 9, 1));
            UNCaddPurchases(ps, "dd143324-3031-40f1-b8f7-b90f946e2b99", 3.99, 1.0, LocalDate.of(2025, 9, 1));
            UNCaddPurchases(ps, "ac926a1e-780e-48e5-8dcd-06fe9d204c5c", 14.00, 3.0, LocalDate.of(2025, 9, 1));
            UNCaddPurchases(ps, "4d9a5e43-0c98-4956-aac5-d206fbb1c600", 3.00, 1.0, LocalDate.of(2025, 9, 1));
            UNCaddPurchases(ps, "f9809697-3a71-4d52-8347-f0950ab7c5d6", 17.49, 1.0, LocalDate.of(2025, 9, 1));
            UNCaddPurchases(ps, "3e7d1add-bf1f-40a5-9de6-45a3bf0045c2", 4.49, 2.0, LocalDate.of(2025, 9, 1));
            UNCaddPurchases(ps, "9350a653-d57c-45d2-ba03-0c4835c7a31d", 5.99, 3.0, LocalDate.of(2025, 9, 1));
            UNCaddPurchases(ps, "9035c1bc-7a0d-44d4-a06a-934beca69276", 12.99, 1.0, LocalDate.of(2025, 9, 1));
            UNCaddPurchases(ps, "e631be2c-9959-40f8-9695-c7bbd8966e2d", 4.99, 2.0, LocalDate.of(2025, 9, 4));
            UNCaddPurchases(ps, "43ae807b-0938-4b7a-b1df-c62308ee283e", 6.00, 1.0, LocalDate.of(2025, 9, 4));
            UNCaddPurchases(ps, "fe10d506-1e42-4791-b7f9-094a81f1b50d", 3.49, 2.0, LocalDate.of(2025, 9, 4));
            UNCaddPurchases(ps, "a8d397b0-e695-4b50-99b1-b2eb57277a79", 13.99, 1.0, LocalDate.of(2025, 9, 8));
            UNCaddPurchases(ps, "0667113c-7559-4629-b577-e9741f43ef5a", 3.99, 3.0, LocalDate.of(2025, 9, 8));
            UNCaddPurchases(ps, "9a9c7178-7fa9-4114-92db-3e08dbb21026", 4.49, 1.0, LocalDate.of(2025, 9, 8));
            UNCaddPurchases(ps, "d23f242b-91ac-446f-b38b-a4b0214baefa", 11.00, 1.0, LocalDate.of(2025, 9, 8));
            UNCaddPurchases(ps, "e1685c2b-aab5-4b1c-87da-fb75c257fd76", 15.49, 1.0, LocalDate.of(2025, 9, 8));
            UNCaddPurchases(ps, "f9809697-3a71-4d52-8347-f0950ab7c5d6", 19.49, 2.0, LocalDate.of(2025, 9, 8));
            UNCaddPurchases(ps, "98d7bd17-3fa7-403a-ae36-c25db7a72c39", 9.99, 1.0, LocalDate.of(2025, 9, 8));
            UNCaddPurchases(ps, "738d56b4-952f-4748-8e63-966c30d23265", 8.49, 2.0, LocalDate.of(2025, 9, 8));
            UNCaddPurchases(ps, "578e3ad4-097a-453e-aab1-d5abd951f25a", 2.49, 2.0, LocalDate.of(2025, 9, 8));
            UNCaddPurchases(ps, "31bdcd6b-a116-4eaf-a0bc-9a8e7287976b", 6.99, 1.0, LocalDate.of(2025, 9, 8));
            UNCaddPurchases(ps, "31bdcd6b-a116-4eaf-a0bc-9a8e7287976b", 6.49, 3.0, LocalDate.of(2025, 9, 12));
            UNCaddPurchases(ps, "8e0ca1ee-df41-411b-8591-9be7e4b34125", 3.99, 3.0, LocalDate.of(2025, 9, 12));
            UNCaddPurchases(ps, "dd143324-3031-40f1-b8f7-b90f946e2b99", 4.00, 2.0, LocalDate.of(2025, 9, 12));
            UNCaddPurchases(ps, "0667113c-7559-4629-b577-e9741f43ef5a", 3.99, 3.0, LocalDate.of(2025, 9, 12));
            UNCaddPurchases(ps, "d23f242b-91ac-446f-b38b-a4b0214baefa", 13.00, 1.0, LocalDate.of(2025, 9, 15));
            UNCaddPurchases(ps, "3e7d1add-bf1f-40a5-9de6-45a3bf0045c2", 4.49, 3.0, LocalDate.of(2025, 9, 15));
            UNCaddPurchases(ps, "9350a653-d57c-45d2-ba03-0c4835c7a31d", 5.49, 3.0, LocalDate.of(2025, 9, 15));
            UNCaddPurchases(ps, "e631be2c-9959-40f8-9695-c7bbd8966e2d", 4.99, 1.0, LocalDate.of(2025, 9, 15));
            UNCaddPurchases(ps, "dd143324-3031-40f1-b8f7-b90f946e2b99", 3.99, 1.0, LocalDate.of(2025, 9, 15));
            UNCaddPurchases(ps, "8e0ca1ee-df41-411b-8591-9be7e4b34125", 4.00, 1.0, LocalDate.of(2025, 9, 15));
            UNCaddPurchases(ps, "f9809697-3a71-4d52-8347-f0950ab7c5d6", 16.49, 1.0, LocalDate.of(2025, 9, 15));
            UNCaddPurchases(ps, "ccd80ec8-614b-4968-8aec-04a1fa6ce68d", 3.00, 1.0, LocalDate.of(2025, 9, 15));
            UNCaddPurchases(ps, "ac926a1e-780e-48e5-8dcd-06fe9d204c5c", 13.00, 3.0, LocalDate.of(2025, 9, 15));
            UNCaddPurchases(ps, "9a9c7178-7fa9-4114-92db-3e08dbb21026", 4.99, 3.0, LocalDate.of(2025, 9, 15));
            UNCaddPurchases(ps, "81f475a9-0579-43be-85cc-15de4fe6a714", 13.49, 1.0, LocalDate.of(2025, 9, 15));
            UNCaddPurchases(ps, "a3e279ea-080d-4762-814f-9ba4c4293397", 7.49, 2.0, LocalDate.of(2025, 9, 15));
            UNCaddPurchases(ps, "738d56b4-952f-4748-8e63-966c30d23265", 8.49, 1.0, LocalDate.of(2025, 9, 19));
            UNCaddPurchases(ps, "ec86defe-7548-4031-8a76-d9ce3c551c9b", 3.49, 3.0, LocalDate.of(2025, 9, 19));
            UNCaddPurchases(ps, "d23f242b-91ac-446f-b38b-a4b0214baefa", 11.49, 2.0, LocalDate.of(2025, 9, 19));
            UNCaddPurchases(ps, "c7c4c1e4-a960-4db7-bab9-15b8521157b9", 10.99, 2.0, LocalDate.of(2025, 9, 19));
            UNCaddPurchases(ps, "fe10d506-1e42-4791-b7f9-094a81f1b50d", 3.49, 1.0, LocalDate.of(2025, 9, 19));
            UNCaddPurchases(ps, "6ca597ad-f42f-4ed0-8c4e-81572e58246a", 5.00, 1.0, LocalDate.of(2025, 9, 22));
            UNCaddPurchases(ps, "ccd80ec8-614b-4968-8aec-04a1fa6ce68d", 3.99, 3.0, LocalDate.of(2025, 9, 22));
            UNCaddPurchases(ps, "3e7d1add-bf1f-40a5-9de6-45a3bf0045c2", 4.99, 1.0, LocalDate.of(2025, 9, 22));
            UNCaddPurchases(ps, "fe10d506-1e42-4791-b7f9-094a81f1b50d", 4.00, 1.0, LocalDate.of(2025, 9, 22));
            UNCaddPurchases(ps, "738d56b4-952f-4748-8e63-966c30d23265", 8.49, 2.0, LocalDate.of(2025, 9, 22));
            UNCaddPurchases(ps, "a8d397b0-e695-4b50-99b1-b2eb57277a79", 14.00, 2.0, LocalDate.of(2025, 9, 22));
            UNCaddPurchases(ps, "dd143324-3031-40f1-b8f7-b90f946e2b99", 3.49, 2.0, LocalDate.of(2025, 9, 22));
            UNCaddPurchases(ps, "43ae807b-0938-4b7a-b1df-c62308ee283e", 6.49, 3.0, LocalDate.of(2025, 9, 22));
            UNCaddPurchases(ps, "e1685c2b-aab5-4b1c-87da-fb75c257fd76", 15.99, 3.0, LocalDate.of(2025, 9, 22));
            UNCaddPurchases(ps, "ec86defe-7548-4031-8a76-d9ce3c551c9b", 3.49, 1.0, LocalDate.of(2025, 9, 22));
            UNCaddPurchases(ps, "f9809697-3a71-4d52-8347-f0950ab7c5d6", 19.00, 3.0, LocalDate.of(2025, 9, 22));
            UNCaddPurchases(ps, "578e3ad4-097a-453e-aab1-d5abd951f25a", 2.99, 2.0, LocalDate.of(2025, 9, 22));
            UNCaddPurchases(ps, "4d9a5e43-0c98-4956-aac5-d206fbb1c600", 3.49, 1.0, LocalDate.of(2025, 9, 22));
            UNCaddPurchases(ps, "fe156ac3-7131-4188-830b-d6a4fa030e8f", 5.49, 3.0, LocalDate.of(2025, 9, 22));
            UNCaddPurchases(ps, "31bdcd6b-a116-4eaf-a0bc-9a8e7287976b", 6.99, 1.0, LocalDate.of(2025, 9, 27));
            UNCaddPurchases(ps, "6ca597ad-f42f-4ed0-8c4e-81572e58246a", 5.49, 3.0, LocalDate.of(2025, 9, 27));
            UNCaddPurchases(ps, "43ae807b-0938-4b7a-b1df-c62308ee283e", 5.49, 3.0, LocalDate.of(2025, 9, 27));
            UNCaddPurchases(ps, "3e7d1add-bf1f-40a5-9de6-45a3bf0045c2", 5.00, 1.0, LocalDate.of(2025, 9, 27));
            UNCaddPurchases(ps, "6a52d8e3-7cb0-45a5-ad58-f9a10e836baf", 6.99, 1.0, LocalDate.of(2025, 9, 27));
            UNCaddPurchases(ps, "fe156ac3-7131-4188-830b-d6a4fa030e8f", 5.00, 1.0, LocalDate.of(2025, 9, 27));
            UNCaddPurchases(ps, "738d56b4-952f-4748-8e63-966c30d23265", 8.49, 2.0, LocalDate.of(2025, 9, 27));
            UNCaddPurchases(ps, "9350a653-d57c-45d2-ba03-0c4835c7a31d", 5.99, 2.0, LocalDate.of(2025, 9, 27));
            UNCaddPurchases(ps, "a8d397b0-e695-4b50-99b1-b2eb57277a79", 13.49, 1.0, LocalDate.of(2025, 9, 27));
            UNCaddPurchases(ps, "ac926a1e-780e-48e5-8dcd-06fe9d204c5c", 14.49, 2.0, LocalDate.of(2025, 9, 27));
            UNCaddPurchases(ps, "fe10d506-1e42-4791-b7f9-094a81f1b50d", 3.49, 1.0, LocalDate.of(2025, 9, 27));
            UNCaddPurchases(ps, "9a9c7178-7fa9-4114-92db-3e08dbb21026", 5.00, 2.0, LocalDate.of(2025, 9, 27));
            UNCaddPurchases(ps, "4d9a5e43-0c98-4956-aac5-d206fbb1c600", 3.00, 1.0, LocalDate.of(2025, 9, 27));
            UNCaddPurchases(ps, "7fc847da-44b1-4817-9a0a-79703cff2a1c", 12.00, 1.0, LocalDate.of(2025, 9, 27));
            UNCaddPurchases(ps, "eb38da48-390e-4673-a0de-a99a37bed2da", 9.99, 1.0, LocalDate.of(2025, 9, 27));
            UNCaddPurchases(ps, "6a52d8e3-7cb0-45a5-ad58-f9a10e836baf", 7.49, 3.0, LocalDate.of(2025, 9, 30));
            UNCaddPurchases(ps, "31bdcd6b-a116-4eaf-a0bc-9a8e7287976b", 7.49, 1.0, LocalDate.of(2025, 9, 30));
            UNCaddPurchases(ps, "ec86defe-7548-4031-8a76-d9ce3c551c9b", 3.99, 3.0, LocalDate.of(2025, 9, 30));
            UNCaddPurchases(ps, "578e3ad4-097a-453e-aab1-d5abd951f25a", 2.99, 2.0, LocalDate.of(2025, 9, 30));
            UNCaddPurchases(ps, "43ae807b-0938-4b7a-b1df-c62308ee283e", 5.99, 3.0, LocalDate.of(2025, 9, 30));
            UNCaddPurchases(ps, "f9809697-3a71-4d52-8347-f0950ab7c5d6", 18.99, 1.0, LocalDate.of(2025, 9, 30));
            UNCaddPurchases(ps, "abefa615-4f4d-4a4e-b882-4a6c33eb1198", 12.00, 1.0, LocalDate.of(2025, 9, 30));
            UNCaddPurchases(ps, "0667113c-7559-4629-b577-e9741f43ef5a", 3.49, 1.0, LocalDate.of(2025, 9, 30));
            UNCaddPurchases(ps, "e1685c2b-aab5-4b1c-87da-fb75c257fd76", 16.49, 2.0, LocalDate.of(2025, 9, 30));
            UNCaddPurchases(ps, "8e0ca1ee-df41-411b-8591-9be7e4b34125", 4.00, 3.0, LocalDate.of(2025, 9, 30));
            UNCaddPurchases(ps, "b9840c75-9807-49ea-8325-75c92e5a902f", 3.49, 2.0, LocalDate.of(2025, 9, 30));
            UNCaddPurchases(ps, "9a9c7178-7fa9-4114-92db-3e08dbb21026", 4.49, 3.0, LocalDate.of(2025, 10, 4));
            UNCaddPurchases(ps, "d23f242b-91ac-446f-b38b-a4b0214baefa", 11.49, 1.0, LocalDate.of(2025, 10, 4));
            UNCaddPurchases(ps, "a8d397b0-e695-4b50-99b1-b2eb57277a79", 15.49, 3.0, LocalDate.of(2025, 10, 4));
            UNCaddPurchases(ps, "31bdcd6b-a116-4eaf-a0bc-9a8e7287976b", 6.99, 3.0, LocalDate.of(2025, 10, 4));
            UNCaddPurchases(ps, "f9809697-3a71-4d52-8347-f0950ab7c5d6", 18.00, 2.0, LocalDate.of(2025, 10, 4));
            UNCaddPurchases(ps, "6ca597ad-f42f-4ed0-8c4e-81572e58246a", 5.49, 3.0, LocalDate.of(2025, 10, 4));
            UNCaddPurchases(ps, "fe10d506-1e42-4791-b7f9-094a81f1b50d", 3.49, 2.0, LocalDate.of(2025, 10, 4));
            UNCaddPurchases(ps, "738d56b4-952f-4748-8e63-966c30d23265", 9.00, 1.0, LocalDate.of(2025, 10, 4));
            UNCaddPurchases(ps, "578e3ad4-097a-453e-aab1-d5abd951f25a", 2.49, 1.0, LocalDate.of(2025, 10, 4));
            UNCaddPurchases(ps, "ccd80ec8-614b-4968-8aec-04a1fa6ce68d", 3.49, 2.0, LocalDate.of(2025, 10, 4));
            UNCaddPurchases(ps, "fe156ac3-7131-4188-830b-d6a4fa030e8f", 5.49, 1.0, LocalDate.of(2025, 10, 8));
            UNCaddPurchases(ps, "4d9a5e43-0c98-4956-aac5-d206fbb1c600", 3.49, 2.0, LocalDate.of(2025, 10, 8));
            UNCaddPurchases(ps, "abefa615-4f4d-4a4e-b882-4a6c33eb1198", 12.99, 3.0, LocalDate.of(2025, 10, 8));
            UNCaddPurchases(ps, "e1685c2b-aab5-4b1c-87da-fb75c257fd76", 15.49, 2.0, LocalDate.of(2025, 10, 8));
            UNCaddPurchases(ps, "0667113c-7559-4629-b577-e9741f43ef5a", 3.49, 1.0, LocalDate.of(2025, 10, 8));
            UNCaddPurchases(ps, "738d56b4-952f-4748-8e63-966c30d23265", 8.00, 1.0, LocalDate.of(2025, 10, 8));
            UNCaddPurchases(ps, "b9840c75-9807-49ea-8325-75c92e5a902f", 2.99, 3.0, LocalDate.of(2025, 10, 8));
            UNCaddPurchases(ps, "578e3ad4-097a-453e-aab1-d5abd951f25a", 2.49, 1.0, LocalDate.of(2025, 10, 8));
            UNCaddPurchases(ps, "d23f242b-91ac-446f-b38b-a4b0214baefa", 12.49, 3.0, LocalDate.of(2025, 10, 8));
            UNCaddPurchases(ps, "3e7d1add-bf1f-40a5-9de6-45a3bf0045c2", 4.49, 1.0, LocalDate.of(2025, 10, 8));
            UNCaddPurchases(ps, "8e0ca1ee-df41-411b-8591-9be7e4b34125", 4.00, 2.0, LocalDate.of(2025, 10, 8));
            UNCaddPurchases(ps, "c7c4c1e4-a960-4db7-bab9-15b8521157b9", 11.49, 2.0, LocalDate.of(2025, 10, 8));
            UNCaddPurchases(ps, "ac926a1e-780e-48e5-8dcd-06fe9d204c5c", 14.99, 1.0, LocalDate.of(2025, 10, 8));
            UNCaddPurchases(ps, "9a9c7178-7fa9-4114-92db-3e08dbb21026", 5.49, 2.0, LocalDate.of(2025, 10, 13));
            UNCaddPurchases(ps, "f9809697-3a71-4d52-8347-f0950ab7c5d6", 18.00, 3.0, LocalDate.of(2025, 10, 13));
            UNCaddPurchases(ps, "3e7d1add-bf1f-40a5-9de6-45a3bf0045c2", 4.49, 2.0, LocalDate.of(2025, 10, 13));
            UNCaddPurchases(ps, "e631be2c-9959-40f8-9695-c7bbd8966e2d", 5.49, 1.0, LocalDate.of(2025, 10, 13));
            UNCaddPurchases(ps, "ac926a1e-780e-48e5-8dcd-06fe9d204c5c", 14.49, 1.0, LocalDate.of(2025, 10, 13));
            UNCaddPurchases(ps, "b9840c75-9807-49ea-8325-75c92e5a902f", 3.00, 2.0, LocalDate.of(2025, 10, 13));
            UNCaddPurchases(ps, "abefa615-4f4d-4a4e-b882-4a6c33eb1198", 11.99, 3.0, LocalDate.of(2025, 10, 16));
            UNCaddPurchases(ps, "c7c4c1e4-a960-4db7-bab9-15b8521157b9", 11.49, 2.0, LocalDate.of(2025, 10, 16));
            UNCaddPurchases(ps, "8e0ca1ee-df41-411b-8591-9be7e4b34125", 3.99, 3.0, LocalDate.of(2025, 10, 16));
            UNCaddPurchases(ps, "dd143324-3031-40f1-b8f7-b90f946e2b99", 4.00, 3.0, LocalDate.of(2025, 10, 16));
            UNCaddPurchases(ps, "ac926a1e-780e-48e5-8dcd-06fe9d204c5c", 12.49, 3.0, LocalDate.of(2025, 10, 16));
            UNCaddPurchases(ps, "6a52d8e3-7cb0-45a5-ad58-f9a10e836baf", 6.49, 2.0, LocalDate.of(2025, 10, 16));
            UNCaddPurchases(ps, "e631be2c-9959-40f8-9695-c7bbd8966e2d", 5.49, 3.0, LocalDate.of(2025, 10, 16));
            UNCaddPurchases(ps, "b9840c75-9807-49ea-8325-75c92e5a902f", 2.99, 3.0, LocalDate.of(2025, 10, 16));
            UNCaddPurchases(ps, "6ca597ad-f42f-4ed0-8c4e-81572e58246a", 5.49, 2.0, LocalDate.of(2025, 10, 16));
            UNCaddPurchases(ps, "578e3ad4-097a-453e-aab1-d5abd951f25a", 2.99, 2.0, LocalDate.of(2025, 10, 16));
            UNCaddPurchases(ps, "0667113c-7559-4629-b577-e9741f43ef5a", 3.49, 2.0, LocalDate.of(2025, 10, 16));
            UNCaddPurchases(ps, "f9809697-3a71-4d52-8347-f0950ab7c5d6", 17.99, 2.0, LocalDate.of(2025, 10, 16));
            UNCaddPurchases(ps, "ccd80ec8-614b-4968-8aec-04a1fa6ce68d", 3.49, 3.0, LocalDate.of(2025, 10, 16));
            UNCaddPurchases(ps, "0953287f-0a6d-4196-8e34-e5aa7c151266", 10.49, 2.0, LocalDate.of(2025, 10, 16));
            UNCaddPurchases(ps, "7fc847da-44b1-4817-9a0a-79703cff2a1c", 10.99, 1.0, LocalDate.of(2025, 10, 16));
            UNCaddPurchases(ps, "a8d397b0-e695-4b50-99b1-b2eb57277a79", 13.99, 3.0, LocalDate.of(2025, 10, 21));
            UNCaddPurchases(ps, "e1685c2b-aab5-4b1c-87da-fb75c257fd76", 14.99, 3.0, LocalDate.of(2025, 10, 21));
            UNCaddPurchases(ps, "4d9a5e43-0c98-4956-aac5-d206fbb1c600", 3.49, 1.0, LocalDate.of(2025, 10, 21));
            UNCaddPurchases(ps, "3e7d1add-bf1f-40a5-9de6-45a3bf0045c2", 4.49, 3.0, LocalDate.of(2025, 10, 21));
            UNCaddPurchases(ps, "31bdcd6b-a116-4eaf-a0bc-9a8e7287976b", 6.00, 1.0, LocalDate.of(2025, 10, 21));
            UNCaddPurchases(ps, "fe156ac3-7131-4188-830b-d6a4fa030e8f", 5.00, 2.0, LocalDate.of(2025, 10, 21));
            UNCaddPurchases(ps, "d23f242b-91ac-446f-b38b-a4b0214baefa", 13.00, 3.0, LocalDate.of(2025, 10, 21));
            UNCaddPurchases(ps, "0667113c-7559-4629-b577-e9741f43ef5a", 3.99, 1.0, LocalDate.of(2025, 10, 21));
            UNCaddPurchases(ps, "dd143324-3031-40f1-b8f7-b90f946e2b99", 4.00, 2.0, LocalDate.of(2025, 10, 21));
            UNCaddPurchases(ps, "ac926a1e-780e-48e5-8dcd-06fe9d204c5c", 14.00, 3.0, LocalDate.of(2025, 10, 21));
            UNCaddPurchases(ps, "abefa615-4f4d-4a4e-b882-4a6c33eb1198", 12.49, 3.0, LocalDate.of(2025, 10, 21));
            UNCaddPurchases(ps, "fe10d506-1e42-4791-b7f9-094a81f1b50d", 3.49, 2.0, LocalDate.of(2025, 10, 21));
            UNCaddPurchases(ps, "31bdcd6b-a116-4eaf-a0bc-9a8e7287976b", 7.49, 1.0, LocalDate.of(2025, 10, 25));
            UNCaddPurchases(ps, "f9809697-3a71-4d52-8347-f0950ab7c5d6", 17.99, 2.0, LocalDate.of(2025, 10, 25));
            UNCaddPurchases(ps, "4d9a5e43-0c98-4956-aac5-d206fbb1c600", 3.49, 3.0, LocalDate.of(2025, 10, 25));
            UNCaddPurchases(ps, "ec86defe-7548-4031-8a76-d9ce3c551c9b", 3.49, 1.0, LocalDate.of(2025, 10, 25));
            UNCaddPurchases(ps, "b9840c75-9807-49ea-8325-75c92e5a902f", 3.49, 1.0, LocalDate.of(2025, 10, 25));
            UNCaddPurchases(ps, "fe156ac3-7131-4188-830b-d6a4fa030e8f", 5.49, 3.0, LocalDate.of(2025, 10, 25));
            UNCaddPurchases(ps, "a8d397b0-e695-4b50-99b1-b2eb57277a79", 14.49, 3.0, LocalDate.of(2025, 10, 25));
            UNCaddPurchases(ps, "43ae807b-0938-4b7a-b1df-c62308ee283e", 6.99, 2.0, LocalDate.of(2025, 10, 25));
            UNCaddPurchases(ps, "dd143324-3031-40f1-b8f7-b90f946e2b99", 3.99, 1.0, LocalDate.of(2025, 10, 25));
            UNCaddPurchases(ps, "fe10d506-1e42-4791-b7f9-094a81f1b50d", 3.49, 1.0, LocalDate.of(2025, 10, 25));
            UNCaddPurchases(ps, "738d56b4-952f-4748-8e63-966c30d23265", 7.99, 1.0, LocalDate.of(2025, 10, 25));
            UNCaddPurchases(ps, "9350a653-d57c-45d2-ba03-0c4835c7a31d", 5.49, 1.0, LocalDate.of(2025, 10, 25));
            UNCaddPurchases(ps, "578e3ad4-097a-453e-aab1-d5abd951f25a", 2.49, 3.0, LocalDate.of(2025, 10, 25));
            UNCaddPurchases(ps, "ccd80ec8-614b-4968-8aec-04a1fa6ce68d", 3.00, 2.0, LocalDate.of(2025, 10, 25));
            UNCaddPurchases(ps, "0667113c-7559-4629-b577-e9741f43ef5a", 4.00, 1.0, LocalDate.of(2025, 10, 28));
            UNCaddPurchases(ps, "d23f242b-91ac-446f-b38b-a4b0214baefa", 12.99, 1.0, LocalDate.of(2025, 10, 28));
            UNCaddPurchases(ps, "fe10d506-1e42-4791-b7f9-094a81f1b50d", 3.49, 2.0, LocalDate.of(2025, 10, 28));

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
