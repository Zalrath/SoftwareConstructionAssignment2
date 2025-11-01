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
import java.util.*;

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
          
            stmt.executeUpdate("""
                CREATE TABLE settings (
                    currency_format VARCHAR(20),
                    date_format VARCHAR(20) ,
                    accent_colour VARCHAR(20) 
                   
                    
                )
            
            """);
            
            stmt.executeUpdate("""
            CREATE TABLE Transactions (
                    uuid VARCHAR(36) PRIMARY KEY,
                    type VARCHAR(10),            
                    title VARCHAR(100),
                    tag VARCHAR(50),
                    amount DOUBLE,
                    frequency VARCHAR(20),
                    date DATE
                )
                     
            
            """);
       
            
            
            stmt.executeUpdate("""
                CREATE TABLE Budget (
                    weekly_budget DOUBLE,
                    monthly_budget DOUBLE,
                    yearly_budget DOUBLE,
                    all_time_budget DOUBLE,
                    savings DOUBLE,
                    income DOUBLE,
                    expenses DOUBLE,
                    budget DOUBLE,
                    actual DOUBLE
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
    
    
    public void insertDefaultSettings(Connection conn) throws SQLException{
    
        try (Statement stmt = conn.createStatement()) {
            ResultSet rsCheck = stmt.executeQuery("SELECT COUNT(*) FROM settings");
            if (rsCheck.next() && rsCheck.getInt(1) == 0) {
                stmt.executeUpdate("""
                    INSERT INTO settings (currency_format,date_format, accent_colour) 
                    VALUES ('$','dd MMM yyyy', '#48375D')
                """);
                System.out.println("Default Settings inserted successfully!");
        }
    }
    }

        public void insertDefaultBudget(Connection conn) throws SQLException {
            String checkSQL = "SELECT COUNT(*) FROM Budget";
            String insertSQL = """
                INSERT INTO Budget (
                    weekly_budget, monthly_budget, yearly_budget, all_time_budget,
                    savings, income, expenses, budget, actual
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(checkSQL)) {

                rs.next();
                int count = rs.getInt(1);

                if (count == 0) {
                    try (PreparedStatement ps = conn.prepareStatement(insertSQL)) {
                        ps.setDouble(1, 300);       // weekly_budget
                        ps.setDouble(2, 1200);      // monthly_budget
                        ps.setDouble(3, 14500);     // yearly_budget
                        ps.setDouble(4, 100000);    // all_time_budget
                        ps.setDouble(5, 100000);    // savings
                        ps.setDouble(6, 100000);    // income
                        ps.setDouble(7, 100000);    // expenses
                        ps.setDouble(8, 100000);    // budget
                        ps.setDouble(9, 100000);    // actual
                        ps.executeUpdate();
                        System.out.println("Default Budget inserted successfully!");
                    }
                }
               
            }
        }

        public void insertDefaultTransactions(Connection conn) {
        String sql = "INSERT INTO Transactions (uuid,type, title, tag, amount, frequency, date) VALUES (?,?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            UNCaddTransaction(ps, UUID.randomUUID(), "expense", "milk purchase", "dairy", 4.50, "weekly", LocalDate.of(2025, 10, 29));
            UNCaddTransaction(ps, UUID.randomUUID(),"expense", "internet bill", "utilities", 85.00, "monthly", LocalDate.of(2025, 10, 20));
            UNCaddTransaction(ps, UUID.randomUUID(),"expense", "groceries", "pantry", 60.00, "weekly", LocalDate.of(2025, 10, 15));
            UNCaddTransaction(ps, UUID.randomUUID(),"expense", "fuel", "transport", 50.00, "fortnightly", LocalDate.of(2025, 10, 10));
            UNCaddTransaction(ps, UUID.randomUUID(),"expense", "gym", "health", 30.00, "monthly", LocalDate.of(2025, 10, 1));
            UNCaddTransaction(ps, UUID.randomUUID(),"expense", "water bill", "utilities", 30.00, "monthly", LocalDate.of(2025, 10, 1));
            System.out.println("Default transactions inserted successfully!");

        } catch (SQLException e) {
            System.err.println("️ Error inserting default transactions: " + e.getMessage());
        }
    }

    private static void UNCaddTransaction(
            PreparedStatement ps,
             UUID uuid,
            String type,
            String title,
            String tag,
            double amount,
            String frequency,
            LocalDate date
    ) throws SQLException {
        ps.setString(1, uuid.toString());
        ps.setString(2, type);
        ps.setString(3, title);
        ps.setString(4, tag);
        ps.setDouble(5, amount);
        ps.setString(6, frequency);
        ps.setDate(7, java.sql.Date.valueOf(date));
        ps.executeUpdate();
    }
    
    
    public void insertDefaultItems(Connection conn) {
        String sql = "INSERT INTO Items (uuid, name, last_Purchased, current_Amount , interval_Days, tags, favorite, future) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            UNCaddItem(ps, "bc41cce8-c598-449a-9051-563591c2b1c1", "Milk", LocalDate.of(2025, 2, 23), 4.0, 0, "Groceries|dairy|fridge|staple|breakfast", false, false);
            UNCaddItem(ps, "6d00f111-d395-431d-984b-1b25f150f64e", "Bread", LocalDate.of(2025, 8, 4), 1.0, 0, "Groceries|bakery|breakfast|staple|pantry", false, false);
            UNCaddItem(ps, "87eee6d3-cb6c-4666-97b0-350ff102f0c4", "Eggs", LocalDate.of(2025, 8, 31), 8.0, 0, "Groceries|protein|fridge|breakfast|baking", false, false);
            UNCaddItem(ps, "c05fda44-c11f-476e-be98-d6eadac63a05", "Butter", LocalDate.of(2024, 10, 25), 5.0, 0, "Groceries|dairy|fridge|baking|staple", false, false);
            UNCaddItem(ps, "55656e11-7e0a-4e33-87ce-749c8f20ab62", "Cheese", LocalDate.of(2024, 11, 4), 4.0, 0, "Groceries|dairy|fridge|snack|staple", false, false);
            UNCaddItem(ps, "628ed9f6-3cae-4bc3-afc6-e396a90ff09d", "Chicken Breast", LocalDate.of(2025, 1, 31), 5.0, 0, "Groceries|meat|protein|freezer|dinner", false, false);
            UNCaddItem(ps, "b7cde652-ea9a-43d3-bfeb-774e68d384c5", "Mince", LocalDate.of(2025, 8, 29), 9.0, 0, "Groceries|meat|protein|freezer|staple", false, false);
            UNCaddItem(ps, "3717698b-ba54-4db1-87c1-8404e1c878a8", "Fish Fillets", LocalDate.of(2025, 1, 10), 2.0, 0, "Groceries|seafood|protein|freezer|dinner", false, false);
            UNCaddItem(ps, "e72178ad-de05-483e-a564-b036785b970c", "Pasta", LocalDate.of(2025, 7, 27), 1.0, 0, "Groceries|grain|pantry|staple|dinner", false, false);
            UNCaddItem(ps, "43123693-3c38-44bd-a3e9-1a6cb9d08b75", "Rice", LocalDate.of(2025, 9, 8), 1.0, 0, "Groceries|grain|pantry|side|staple", false, false);
            UNCaddItem(ps, "e456e25d-c34c-4375-ad3c-f19ef011eafa", "Apples", LocalDate.of(2024, 11, 23), 4.0, 0, "Groceries|fruit|fresh|snack|produce", false, false);
            UNCaddItem(ps, "060189f1-92e0-48c0-8cb7-e960cf135e42", "Bananas", LocalDate.of(2025, 8, 5), 5.0, 0, "Groceries|fruit|snack|breakfast|produce", false, false);
            UNCaddItem(ps, "95be1867-3551-431f-80b9-40f811a50e18", "Onions", LocalDate.of(2024, 11, 25), 5.0, 0, "Groceries|vegetable|cooking|staple|pantry", false, false);
            UNCaddItem(ps, "400f961e-77a1-46c4-8dbd-24703afc9b96", "Potatoes", LocalDate.of(2025, 6, 15), 8.0, 0, "Groceries|vegetable|staple|side|produce", false, false);
            UNCaddItem(ps, "6a0e0b76-0c67-4960-a639-0492e23fb8c1", "Tomatoes", LocalDate.of(2024, 10, 3), 1.0, 0, "Groceries|vegetable|fresh|produce|sauce", false, false);
            UNCaddItem(ps, "76cb1958-00f9-4ee8-8a57-ed1bbad8e8d5", "Lettuce", LocalDate.of(2025, 2, 20), 6.0, 0, "Groceries|vegetable|fresh|produce|salad", false, false);
            UNCaddItem(ps, "4031b990-016c-47f9-8501-b5c83ae860c1", "Carrots", LocalDate.of(2025, 5, 28), 9.0, 0, "Groceries|vegetable|fresh|produce|snack", false, false);
            UNCaddItem(ps, "fcd3c43e-b832-466e-96d2-80a7c8b675fb", "Coffee", LocalDate.of(2025, 5, 29), 3.0, 0, "Groceries|beverage|morning|pantry|stimulant", false, false);
            UNCaddItem(ps, "f133b129-f19c-4864-9fa5-da4a2ea6841c", "Sugar", LocalDate.of(2025, 7, 4), 10.0, 0, "Groceries|baking|sweetener|pantry|staple", false, false);
            UNCaddItem(ps, "ec307717-0725-4560-bc90-b21b815c9a1c", "Cooking Oil", LocalDate.of(2025, 5, 28), 9.0, 0, "Groceries|cooking|pantry|oil|staple", false, false);
            UNCaddItem(ps, "591db857-9027-4214-8d1b-7e6dc559aea0", "Olive Oil", LocalDate.of(2025, 2, 28), 5.0, 0, "Groceries|cooking|pantry|oil|healthy", false, false);
            UNCaddItem(ps, "dd1caa5e-63b7-4e8d-b784-3451cdd7384b", "Flour", LocalDate.of(2025, 1, 10), 5.0, 0, "Groceries|baking|pantry|dry|ingredient", false, false);
            UNCaddItem(ps, "103cb413-23b5-44dd-b036-49c6bb71cb70", "Baking Powder", LocalDate.of(2025, 10, 5), 7.0, 0, "Groceries|baking|ingredient|pantry|staple", false, false);
            UNCaddItem(ps, "18a9d99b-2c9c-43de-ad6c-685c4f4d4980", "Hot Sauce", LocalDate.of(2025, 6, 16), 2.0, 0, "Groceries|condiment|spicy|pantry|flavour", false, false);
            UNCaddItem(ps, "10b5a323-f47e-411f-ba55-216b2cdf2bd3", "Laundry Powder", LocalDate.of(2025, 8, 28), 5.0, 0, "Household Essentials|household|cleaning|bulk|laundry", false, false);
            UNCaddItem(ps, "fccf55de-4945-4636-9138-2924e4d47337", "Dish Soap", LocalDate.of(2025, 5, 10), 9.0, 0, "Household Essentials|household|cleaning|kitchen|bulk", false, false);
            UNCaddItem(ps, "f841e912-c7e3-4fb0-950f-bdb149681b7e", "Tissues", LocalDate.of(2025, 4, 16), 8.0, 0, "Household Essentials|household|bathroom|hygiene|bulk", false, false);
            UNCaddItem(ps, "4970b5c8-82d2-4a27-8d08-749541a704d4", "Shampoo", LocalDate.of(2024, 12, 12), 3.0, 0, "Personal Care|personalcare|bathroom|hygiene|daily", false, false);
            UNCaddItem(ps, "a8f8b02e-cd64-4c02-9cfb-4df9c19b5e30", "Toothpaste", LocalDate.of(2024, 10, 18), 1.0, 0, "Personal Care|personalcare|bathroom|hygiene|daily", false, false);
            UNCaddItem(ps, "0338eeba-4018-4314-b616-384b456e525d", "Soap", LocalDate.of(2025, 5, 29), 2.0, 0, "Personal Care|personalcare|bathroom|hygiene|daily", false, false);
            UNCaddItem(ps, "a8beb54f-fade-4bec-b5f7-cbd7d248b188", "Acrylic Paint (Red)", LocalDate.of(2025, 8, 27), 3.0, 0, "Hobby|art|paint|acrylic|supplies|hobby", false, false);
            UNCaddItem(ps, "8118d42b-a447-4a6e-bdc9-70cd70d5c251", "Acrylic Paint (Blue)", LocalDate.of(2025, 2, 3), 2.0, 0, "Hobby|art|paint|acrylic|supplies|hobby", false, false);
            UNCaddItem(ps, "355e0e78-f62d-4d7e-b0c3-98092de1b2a9", "Acrylic Paint (Yellow)", LocalDate.of(2025, 9, 3), 3.0, 0, "Hobby|art|paint|acrylic|supplies|hobby", false, false);
            UNCaddItem(ps, "6672a355-bde4-4b98-8475-c7319beff67d", "Oil Paint (White)", LocalDate.of(2024, 10, 10), 2.0, 0, "Hobby|art|paint|oil|supplies|hobby", false, false);
            UNCaddItem(ps, "9e882fe3-4670-447f-8ec8-572f4f615047", "Paint Brushes (Small)", LocalDate.of(2024, 10, 15), 1.0, 0, "Hobby|art|brush|tool|supplies|hobby", false, false);
            UNCaddItem(ps, "5f871133-c141-42e8-b071-c569e5dfbe57", "Paint Brushes (Medium)", LocalDate.of(2025, 6, 15), 3.0, 0, "Hobby|art|brush|tool|supplies|hobby", false, false);
            UNCaddItem(ps, "33986208-d2c6-4cd0-b88b-a671579bdcf0", "Canvas Pack (Small)", LocalDate.of(2024, 10, 9), 3.0, 0, "Hobby|art|canvas|surface|hobby", false, false);
            UNCaddItem(ps, "1b3b034d-2437-46df-a6df-186e9dcdd82d", "Canvas Pack (Large)", LocalDate.of(2025, 2, 16), 1.0, 0, "Hobby|art|canvas|surface|hobby", false, false);
            UNCaddItem(ps, "b2c06d14-5843-49c4-afe8-366d1a760349", "Paint Thinner", LocalDate.of(2025, 3, 13), 3.0, 0, "Hobby|art|paint|solvent|tool|hobby", false, false);
            UNCaddItem(ps, "683d3699-6bb3-4b08-977e-02a22d07ea51", "Palette Knife", LocalDate.of(2025, 1, 19), 2.0, 0, "Hobby|art|tool|paint|mixing|hobby", false, false);
            UNCaddItem(ps, "6ec93eb4-8706-4e92-bbb1-d35ccbaa818e", "Panadol", LocalDate.of(2024, 11, 29), 3.0, 0, "Medicine|medicine|pain|relief|health", false, false);
            UNCaddItem(ps, "3613b659-76eb-4a65-b621-325d30251257", "Plasters", LocalDate.of(2024, 12, 24), 3.0, 0, "Medicine|medicine|firstaid|bandage|health", false, false);

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

            UNCaddPurchases(ps, "3717698b-ba54-4db1-87c1-8404e1c878a8", 16.99, 3.0, LocalDate.of(2024, 10, 1));
            UNCaddPurchases(ps, "628ed9f6-3cae-4bc3-afc6-e396a90ff09d", 13.99, 2.0, LocalDate.of(2024, 10, 1));
            UNCaddPurchases(ps, "87eee6d3-cb6c-4666-97b0-350ff102f0c4", 6.00, 1.0, LocalDate.of(2024, 10, 1));
            UNCaddPurchases(ps, "b7cde652-ea9a-43d3-bfeb-774e68d384c5", 13.99, 2.0, LocalDate.of(2024, 10, 1));
            UNCaddPurchases(ps, "bc41cce8-c598-449a-9051-563591c2b1c1", 3.49, 3.0, LocalDate.of(2024, 10, 1));
            UNCaddPurchases(ps, "43123693-3c38-44bd-a3e9-1a6cb9d08b75", 5.49, 2.0, LocalDate.of(2024, 10, 1));
            UNCaddPurchases(ps, "c05fda44-c11f-476e-be98-d6eadac63a05", 8.00, 2.0, LocalDate.of(2024, 10, 1));
            UNCaddPurchases(ps, "6d00f111-d395-431d-984b-1b25f150f64e", 3.49, 2.0, LocalDate.of(2024, 10, 1));
            UNCaddPurchases(ps, "95be1867-3551-431f-80b9-40f811a50e18", 3.00, 2.0, LocalDate.of(2024, 10, 1));
            UNCaddPurchases(ps, "591db857-9027-4214-8d1b-7e6dc559aea0", 12.49, 1.0, LocalDate.of(2024, 10, 1));
            UNCaddPurchases(ps, "400f961e-77a1-46c4-8dbd-24703afc9b96", 6.00, 1.0, LocalDate.of(2024, 10, 1));
            UNCaddPurchases(ps, "e456e25d-c34c-4375-ad3c-f19ef011eafa", 4.99, 1.0, LocalDate.of(2024, 10, 1));
            UNCaddPurchases(ps, "76cb1958-00f9-4ee8-8a57-ed1bbad8e8d5", 4.00, 3.0, LocalDate.of(2024, 10, 4));
            UNCaddPurchases(ps, "55656e11-7e0a-4e33-87ce-749c8f20ab62", 8.00, 1.0, LocalDate.of(2024, 10, 4));
            UNCaddPurchases(ps, "4031b990-016c-47f9-8501-b5c83ae860c1", 2.49, 1.0, LocalDate.of(2024, 10, 4));
            UNCaddPurchases(ps, "591db857-9027-4214-8d1b-7e6dc559aea0", 10.99, 1.0, LocalDate.of(2024, 10, 4));
            UNCaddPurchases(ps, "628ed9f6-3cae-4bc3-afc6-e396a90ff09d", 13.49, 2.0, LocalDate.of(2024, 10, 4));
            UNCaddPurchases(ps, "103cb413-23b5-44dd-b036-49c6bb71cb70", 3.49, 1.0, LocalDate.of(2024, 10, 4));
            UNCaddPurchases(ps, "95be1867-3551-431f-80b9-40f811a50e18", 3.49, 1.0, LocalDate.of(2024, 10, 4));
            UNCaddPurchases(ps, "6a0e0b76-0c67-4960-a639-0492e23fb8c1", 6.00, 3.0, LocalDate.of(2024, 10, 4));
            UNCaddPurchases(ps, "18a9d99b-2c9c-43de-ad6c-685c4f4d4980", 4.99, 3.0, LocalDate.of(2024, 10, 4));
            UNCaddPurchases(ps, "43123693-3c38-44bd-a3e9-1a6cb9d08b75", 4.49, 1.0, LocalDate.of(2024, 10, 4));
            UNCaddPurchases(ps, "bc41cce8-c598-449a-9051-563591c2b1c1", 3.00, 2.0, LocalDate.of(2024, 10, 4));
            UNCaddPurchases(ps, "fcd3c43e-b832-466e-96d2-80a7c8b675fb", 12.49, 2.0, LocalDate.of(2024, 10, 4));
            UNCaddPurchases(ps, "c05fda44-c11f-476e-be98-d6eadac63a05", 7.49, 2.0, LocalDate.of(2024, 10, 4));
            UNCaddPurchases(ps, "591db857-9027-4214-8d1b-7e6dc559aea0", 12.49, 1.0, LocalDate.of(2024, 10, 9));
            UNCaddPurchases(ps, "76cb1958-00f9-4ee8-8a57-ed1bbad8e8d5", 3.99, 2.0, LocalDate.of(2024, 10, 9));
            UNCaddPurchases(ps, "55656e11-7e0a-4e33-87ce-749c8f20ab62", 9.49, 1.0, LocalDate.of(2024, 10, 9));
            UNCaddPurchases(ps, "18a9d99b-2c9c-43de-ad6c-685c4f4d4980", 5.00, 1.0, LocalDate.of(2024, 10, 9));
            UNCaddPurchases(ps, "dd1caa5e-63b7-4e8d-b784-3451cdd7384b", 4.49, 1.0, LocalDate.of(2024, 10, 9));
            UNCaddPurchases(ps, "400f961e-77a1-46c4-8dbd-24703afc9b96", 6.00, 1.0, LocalDate.of(2024, 10, 9));
            UNCaddPurchases(ps, "fcd3c43e-b832-466e-96d2-80a7c8b675fb", 12.49, 3.0, LocalDate.of(2024, 10, 9));
            UNCaddPurchases(ps, "4031b990-016c-47f9-8501-b5c83ae860c1", 2.49, 1.0, LocalDate.of(2024, 10, 9));
            UNCaddPurchases(ps, "3717698b-ba54-4db1-87c1-8404e1c878a8", 18.00, 2.0, LocalDate.of(2024, 10, 9));
            UNCaddPurchases(ps, "ec307717-0725-4560-bc90-b21b815c9a1c", 9.49, 3.0, LocalDate.of(2024, 10, 9));
            UNCaddPurchases(ps, "b7cde652-ea9a-43d3-bfeb-774e68d384c5", 15.00, 2.0, LocalDate.of(2024, 10, 9));
            UNCaddPurchases(ps, "95be1867-3551-431f-80b9-40f811a50e18", 2.99, 3.0, LocalDate.of(2024, 10, 9));
            UNCaddPurchases(ps, "f133b129-f19c-4864-9fa5-da4a2ea6841c", 3.49, 2.0, LocalDate.of(2024, 10, 9));
            UNCaddPurchases(ps, "87eee6d3-cb6c-4666-97b0-350ff102f0c4", 6.49, 2.0, LocalDate.of(2024, 10, 9));
            UNCaddPurchases(ps, "87eee6d3-cb6c-4666-97b0-350ff102f0c4", 6.49, 2.0, LocalDate.of(2024, 10, 12));
            UNCaddPurchases(ps, "060189f1-92e0-48c0-8cb7-e960cf135e42", 2.99, 1.0, LocalDate.of(2024, 10, 12));
            UNCaddPurchases(ps, "76cb1958-00f9-4ee8-8a57-ed1bbad8e8d5", 4.49, 1.0, LocalDate.of(2024, 10, 12));
            UNCaddPurchases(ps, "fcd3c43e-b832-466e-96d2-80a7c8b675fb", 12.99, 2.0, LocalDate.of(2024, 10, 12));
            UNCaddPurchases(ps, "591db857-9027-4214-8d1b-7e6dc559aea0", 11.99, 3.0, LocalDate.of(2024, 10, 12));
            UNCaddPurchases(ps, "e456e25d-c34c-4375-ad3c-f19ef011eafa", 5.49, 1.0, LocalDate.of(2024, 10, 12));
            UNCaddPurchases(ps, "18a9d99b-2c9c-43de-ad6c-685c4f4d4980", 4.99, 3.0, LocalDate.of(2024, 10, 12));
            UNCaddPurchases(ps, "4031b990-016c-47f9-8501-b5c83ae860c1", 2.49, 3.0, LocalDate.of(2024, 10, 12));
            UNCaddPurchases(ps, "6a0e0b76-0c67-4960-a639-0492e23fb8c1", 6.49, 3.0, LocalDate.of(2024, 10, 12));
            UNCaddPurchases(ps, "628ed9f6-3cae-4bc3-afc6-e396a90ff09d", 13.99, 1.0, LocalDate.of(2024, 10, 12));
            UNCaddPurchases(ps, "591db857-9027-4214-8d1b-7e6dc559aea0", 10.49, 1.0, LocalDate.of(2024, 10, 15));
            UNCaddPurchases(ps, "6a0e0b76-0c67-4960-a639-0492e23fb8c1", 6.00, 3.0, LocalDate.of(2024, 10, 15));
            UNCaddPurchases(ps, "bc41cce8-c598-449a-9051-563591c2b1c1", 3.49, 2.0, LocalDate.of(2024, 10, 15));
            UNCaddPurchases(ps, "400f961e-77a1-46c4-8dbd-24703afc9b96", 5.99, 2.0, LocalDate.of(2024, 10, 15));
            UNCaddPurchases(ps, "103cb413-23b5-44dd-b036-49c6bb71cb70", 3.00, 1.0, LocalDate.of(2024, 10, 15));
            UNCaddPurchases(ps, "6d00f111-d395-431d-984b-1b25f150f64e", 2.99, 1.0, LocalDate.of(2024, 10, 15));
            UNCaddPurchases(ps, "e456e25d-c34c-4375-ad3c-f19ef011eafa", 5.00, 3.0, LocalDate.of(2024, 10, 15));
            UNCaddPurchases(ps, "95be1867-3551-431f-80b9-40f811a50e18", 2.99, 1.0, LocalDate.of(2024, 10, 15));
            UNCaddPurchases(ps, "e72178ad-de05-483e-a564-b036785b970c", 3.49, 2.0, LocalDate.of(2024, 10, 15));
            UNCaddPurchases(ps, "b7cde652-ea9a-43d3-bfeb-774e68d384c5", 15.00, 3.0, LocalDate.of(2024, 10, 15));
            UNCaddPurchases(ps, "3717698b-ba54-4db1-87c1-8404e1c878a8", 16.49, 3.0, LocalDate.of(2024, 10, 15));
            UNCaddPurchases(ps, "ec307717-0725-4560-bc90-b21b815c9a1c", 10.00, 2.0, LocalDate.of(2024, 10, 15));
            UNCaddPurchases(ps, "f133b129-f19c-4864-9fa5-da4a2ea6841c", 3.49, 1.0, LocalDate.of(2024, 10, 15));
            UNCaddPurchases(ps, "591db857-9027-4214-8d1b-7e6dc559aea0", 11.99, 3.0, LocalDate.of(2024, 10, 19));
            UNCaddPurchases(ps, "b7cde652-ea9a-43d3-bfeb-774e68d384c5", 14.49, 3.0, LocalDate.of(2024, 10, 19));
            UNCaddPurchases(ps, "76cb1958-00f9-4ee8-8a57-ed1bbad8e8d5", 4.00, 2.0, LocalDate.of(2024, 10, 19));
            UNCaddPurchases(ps, "fcd3c43e-b832-466e-96d2-80a7c8b675fb", 13.49, 3.0, LocalDate.of(2024, 10, 19));
            UNCaddPurchases(ps, "628ed9f6-3cae-4bc3-afc6-e396a90ff09d", 15.49, 3.0, LocalDate.of(2024, 10, 19));
            UNCaddPurchases(ps, "18a9d99b-2c9c-43de-ad6c-685c4f4d4980", 6.00, 3.0, LocalDate.of(2024, 10, 19));
            UNCaddPurchases(ps, "55656e11-7e0a-4e33-87ce-749c8f20ab62", 9.99, 3.0, LocalDate.of(2024, 10, 19));
            UNCaddPurchases(ps, "e456e25d-c34c-4375-ad3c-f19ef011eafa", 4.99, 1.0, LocalDate.of(2024, 10, 19));
            UNCaddPurchases(ps, "6d00f111-d395-431d-984b-1b25f150f64e", 3.00, 1.0, LocalDate.of(2024, 10, 19));
            UNCaddPurchases(ps, "3717698b-ba54-4db1-87c1-8404e1c878a8", 16.49, 2.0, LocalDate.of(2024, 10, 19));
            UNCaddPurchases(ps, "103cb413-23b5-44dd-b036-49c6bb71cb70", 2.99, 2.0, LocalDate.of(2024, 10, 19));
            UNCaddPurchases(ps, "6a0e0b76-0c67-4960-a639-0492e23fb8c1", 6.00, 3.0, LocalDate.of(2024, 10, 19));
            UNCaddPurchases(ps, "87eee6d3-cb6c-4666-97b0-350ff102f0c4", 6.49, 2.0, LocalDate.of(2024, 10, 19));
            UNCaddPurchases(ps, "400f961e-77a1-46c4-8dbd-24703afc9b96", 6.00, 1.0, LocalDate.of(2024, 10, 19));
            UNCaddPurchases(ps, "fcd3c43e-b832-466e-96d2-80a7c8b675fb", 13.99, 2.0, LocalDate.of(2024, 10, 22));
            UNCaddPurchases(ps, "18a9d99b-2c9c-43de-ad6c-685c4f4d4980", 5.00, 2.0, LocalDate.of(2024, 10, 22));
            UNCaddPurchases(ps, "3717698b-ba54-4db1-87c1-8404e1c878a8", 17.00, 1.0, LocalDate.of(2024, 10, 22));
            UNCaddPurchases(ps, "6a0e0b76-0c67-4960-a639-0492e23fb8c1", 6.49, 2.0, LocalDate.of(2024, 10, 22));
            UNCaddPurchases(ps, "dd1caa5e-63b7-4e8d-b784-3451cdd7384b", 4.00, 3.0, LocalDate.of(2024, 10, 22));
            UNCaddPurchases(ps, "76cb1958-00f9-4ee8-8a57-ed1bbad8e8d5", 3.99, 3.0, LocalDate.of(2024, 10, 22));
            UNCaddPurchases(ps, "103cb413-23b5-44dd-b036-49c6bb71cb70", 3.00, 1.0, LocalDate.of(2024, 10, 22));
            UNCaddPurchases(ps, "55656e11-7e0a-4e33-87ce-749c8f20ab62", 9.49, 1.0, LocalDate.of(2024, 10, 22));
            UNCaddPurchases(ps, "400f961e-77a1-46c4-8dbd-24703afc9b96", 6.49, 3.0, LocalDate.of(2024, 10, 22));
            UNCaddPurchases(ps, "c05fda44-c11f-476e-be98-d6eadac63a05", 7.49, 2.0, LocalDate.of(2024, 10, 22));
            UNCaddPurchases(ps, "f133b129-f19c-4864-9fa5-da4a2ea6841c", 3.49, 2.0, LocalDate.of(2024, 10, 22));
            UNCaddPurchases(ps, "060189f1-92e0-48c0-8cb7-e960cf135e42", 3.49, 3.0, LocalDate.of(2024, 10, 22));
            UNCaddPurchases(ps, "4031b990-016c-47f9-8501-b5c83ae860c1", 2.49, 2.0, LocalDate.of(2024, 10, 22));
            UNCaddPurchases(ps, "ec307717-0725-4560-bc90-b21b815c9a1c", 10.49, 2.0, LocalDate.of(2024, 10, 22));
            UNCaddPurchases(ps, "591db857-9027-4214-8d1b-7e6dc559aea0", 11.49, 3.0, LocalDate.of(2024, 10, 27));
            UNCaddPurchases(ps, "dd1caa5e-63b7-4e8d-b784-3451cdd7384b", 4.00, 1.0, LocalDate.of(2024, 10, 27));
            UNCaddPurchases(ps, "4031b990-016c-47f9-8501-b5c83ae860c1", 2.49, 1.0, LocalDate.of(2024, 10, 27));
            UNCaddPurchases(ps, "c05fda44-c11f-476e-be98-d6eadac63a05", 8.49, 1.0, LocalDate.of(2024, 10, 27));
            UNCaddPurchases(ps, "6d00f111-d395-431d-984b-1b25f150f64e", 2.99, 2.0, LocalDate.of(2024, 10, 27));
            UNCaddPurchases(ps, "f133b129-f19c-4864-9fa5-da4a2ea6841c", 3.49, 3.0, LocalDate.of(2024, 10, 27));
            UNCaddPurchases(ps, "55656e11-7e0a-4e33-87ce-749c8f20ab62", 9.49, 3.0, LocalDate.of(2024, 10, 30));
            UNCaddPurchases(ps, "591db857-9027-4214-8d1b-7e6dc559aea0", 11.49, 1.0, LocalDate.of(2024, 10, 30));
            UNCaddPurchases(ps, "43123693-3c38-44bd-a3e9-1a6cb9d08b75", 4.49, 1.0, LocalDate.of(2024, 10, 30));
            UNCaddPurchases(ps, "f133b129-f19c-4864-9fa5-da4a2ea6841c", 4.00, 3.0, LocalDate.of(2024, 10, 30));
            UNCaddPurchases(ps, "76cb1958-00f9-4ee8-8a57-ed1bbad8e8d5", 4.00, 3.0, LocalDate.of(2024, 11, 2));
            UNCaddPurchases(ps, "400f961e-77a1-46c4-8dbd-24703afc9b96", 5.49, 3.0, LocalDate.of(2024, 11, 2));
            UNCaddPurchases(ps, "87eee6d3-cb6c-4666-97b0-350ff102f0c4", 6.49, 1.0, LocalDate.of(2024, 11, 2));
            UNCaddPurchases(ps, "f841e912-c7e3-4fb0-950f-bdb149681b7e", 4.99, 2.0, LocalDate.of(2024, 11, 2));
            UNCaddPurchases(ps, "9e882fe3-4670-447f-8ec8-572f4f615047", 7.99, 1.0, LocalDate.of(2024, 11, 2));
            UNCaddPurchases(ps, "6ec93eb4-8706-4e92-bbb1-d35ccbaa818e", 7.49, 1.0, LocalDate.of(2024, 11, 2));
            UNCaddPurchases(ps, "6d00f111-d395-431d-984b-1b25f150f64e", 3.49, 3.0, LocalDate.of(2024, 11, 5));
            UNCaddPurchases(ps, "18a9d99b-2c9c-43de-ad6c-685c4f4d4980", 5.49, 3.0, LocalDate.of(2024, 11, 5));
            UNCaddPurchases(ps, "591db857-9027-4214-8d1b-7e6dc559aea0", 12.49, 1.0, LocalDate.of(2024, 11, 5));
            UNCaddPurchases(ps, "6a0e0b76-0c67-4960-a639-0492e23fb8c1", 6.99, 2.0, LocalDate.of(2024, 11, 5));
            UNCaddPurchases(ps, "dd1caa5e-63b7-4e8d-b784-3451cdd7384b", 4.49, 3.0, LocalDate.of(2024, 11, 5));
            UNCaddPurchases(ps, "c05fda44-c11f-476e-be98-d6eadac63a05", 8.49, 3.0, LocalDate.of(2024, 11, 5));
            UNCaddPurchases(ps, "3717698b-ba54-4db1-87c1-8404e1c878a8", 15.99, 1.0, LocalDate.of(2024, 11, 5));
            UNCaddPurchases(ps, "103cb413-23b5-44dd-b036-49c6bb71cb70", 3.49, 3.0, LocalDate.of(2024, 11, 5));
            UNCaddPurchases(ps, "95be1867-3551-431f-80b9-40f811a50e18", 2.99, 2.0, LocalDate.of(2024, 11, 5));
            UNCaddPurchases(ps, "bc41cce8-c598-449a-9051-563591c2b1c1", 3.49, 1.0, LocalDate.of(2024, 11, 5));
            UNCaddPurchases(ps, "43123693-3c38-44bd-a3e9-1a6cb9d08b75", 4.49, 2.0, LocalDate.of(2024, 11, 5));
            UNCaddPurchases(ps, "fccf55de-4945-4636-9138-2924e4d47337", 5.00, 2.0, LocalDate.of(2024, 11, 5));
            UNCaddPurchases(ps, "ec307717-0725-4560-bc90-b21b815c9a1c", 9.49, 1.0, LocalDate.of(2024, 11, 8));
            UNCaddPurchases(ps, "e72178ad-de05-483e-a564-b036785b970c", 3.49, 3.0, LocalDate.of(2024, 11, 8));
            UNCaddPurchases(ps, "060189f1-92e0-48c0-8cb7-e960cf135e42", 3.00, 1.0, LocalDate.of(2024, 11, 8));
            UNCaddPurchases(ps, "95be1867-3551-431f-80b9-40f811a50e18", 2.99, 1.0, LocalDate.of(2024, 11, 8));
            UNCaddPurchases(ps, "591db857-9027-4214-8d1b-7e6dc559aea0", 12.49, 2.0, LocalDate.of(2024, 11, 8));
            UNCaddPurchases(ps, "400f961e-77a1-46c4-8dbd-24703afc9b96", 6.00, 1.0, LocalDate.of(2024, 11, 8));
            UNCaddPurchases(ps, "4031b990-016c-47f9-8501-b5c83ae860c1", 2.49, 3.0, LocalDate.of(2024, 11, 8));
            UNCaddPurchases(ps, "628ed9f6-3cae-4bc3-afc6-e396a90ff09d", 16.49, 3.0, LocalDate.of(2024, 11, 8));
            UNCaddPurchases(ps, "6a0e0b76-0c67-4960-a639-0492e23fb8c1", 6.49, 3.0, LocalDate.of(2024, 11, 8));
            UNCaddPurchases(ps, "f133b129-f19c-4864-9fa5-da4a2ea6841c", 3.49, 2.0, LocalDate.of(2024, 11, 8));
            UNCaddPurchases(ps, "bc41cce8-c598-449a-9051-563591c2b1c1", 3.00, 2.0, LocalDate.of(2024, 11, 8));
            UNCaddPurchases(ps, "76cb1958-00f9-4ee8-8a57-ed1bbad8e8d5", 3.99, 2.0, LocalDate.of(2024, 11, 8));
            UNCaddPurchases(ps, "87eee6d3-cb6c-4666-97b0-350ff102f0c4", 6.00, 2.0, LocalDate.of(2024, 11, 8));
            UNCaddPurchases(ps, "43123693-3c38-44bd-a3e9-1a6cb9d08b75", 5.49, 1.0, LocalDate.of(2024, 11, 8));
            UNCaddPurchases(ps, "f133b129-f19c-4864-9fa5-da4a2ea6841c", 3.49, 1.0, LocalDate.of(2024, 11, 11));
            UNCaddPurchases(ps, "55656e11-7e0a-4e33-87ce-749c8f20ab62", 8.49, 3.0, LocalDate.of(2024, 11, 11));
            UNCaddPurchases(ps, "400f961e-77a1-46c4-8dbd-24703afc9b96", 5.49, 2.0, LocalDate.of(2024, 11, 11));
            UNCaddPurchases(ps, "76cb1958-00f9-4ee8-8a57-ed1bbad8e8d5", 4.49, 1.0, LocalDate.of(2024, 11, 11));
            UNCaddPurchases(ps, "6a0e0b76-0c67-4960-a639-0492e23fb8c1", 6.00, 1.0, LocalDate.of(2024, 11, 11));
            UNCaddPurchases(ps, "18a9d99b-2c9c-43de-ad6c-685c4f4d4980", 5.99, 3.0, LocalDate.of(2024, 11, 11));
            UNCaddPurchases(ps, "628ed9f6-3cae-4bc3-afc6-e396a90ff09d", 13.99, 2.0, LocalDate.of(2024, 11, 11));
            UNCaddPurchases(ps, "4031b990-016c-47f9-8501-b5c83ae860c1", 2.49, 2.0, LocalDate.of(2024, 11, 11));
            UNCaddPurchases(ps, "103cb413-23b5-44dd-b036-49c6bb71cb70", 3.49, 3.0, LocalDate.of(2024, 11, 11));
            UNCaddPurchases(ps, "6d00f111-d395-431d-984b-1b25f150f64e", 3.49, 3.0, LocalDate.of(2024, 11, 11));
            UNCaddPurchases(ps, "95be1867-3551-431f-80b9-40f811a50e18", 2.99, 3.0, LocalDate.of(2024, 11, 11));
            UNCaddPurchases(ps, "dd1caa5e-63b7-4e8d-b784-3451cdd7384b", 4.49, 3.0, LocalDate.of(2024, 11, 11));
            UNCaddPurchases(ps, "ec307717-0725-4560-bc90-b21b815c9a1c", 9.99, 2.0, LocalDate.of(2024, 11, 14));
            UNCaddPurchases(ps, "bc41cce8-c598-449a-9051-563591c2b1c1", 3.49, 2.0, LocalDate.of(2024, 11, 14));
            UNCaddPurchases(ps, "628ed9f6-3cae-4bc3-afc6-e396a90ff09d", 14.49, 1.0, LocalDate.of(2024, 11, 14));
            UNCaddPurchases(ps, "c05fda44-c11f-476e-be98-d6eadac63a05", 8.49, 1.0, LocalDate.of(2024, 11, 14));
            UNCaddPurchases(ps, "591db857-9027-4214-8d1b-7e6dc559aea0", 10.49, 2.0, LocalDate.of(2024, 11, 14));
            UNCaddPurchases(ps, "95be1867-3551-431f-80b9-40f811a50e18", 2.99, 3.0, LocalDate.of(2024, 11, 14));
            UNCaddPurchases(ps, "55656e11-7e0a-4e33-87ce-749c8f20ab62", 8.99, 2.0, LocalDate.of(2024, 11, 19));
            UNCaddPurchases(ps, "b7cde652-ea9a-43d3-bfeb-774e68d384c5", 15.00, 3.0, LocalDate.of(2024, 11, 19));
            UNCaddPurchases(ps, "591db857-9027-4214-8d1b-7e6dc559aea0", 11.49, 1.0, LocalDate.of(2024, 11, 19));
            UNCaddPurchases(ps, "76cb1958-00f9-4ee8-8a57-ed1bbad8e8d5", 4.00, 2.0, LocalDate.of(2024, 11, 19));
            UNCaddPurchases(ps, "ec307717-0725-4560-bc90-b21b815c9a1c", 9.49, 3.0, LocalDate.of(2024, 11, 22));
            UNCaddPurchases(ps, "4031b990-016c-47f9-8501-b5c83ae860c1", 2.49, 2.0, LocalDate.of(2024, 11, 22));
            UNCaddPurchases(ps, "76cb1958-00f9-4ee8-8a57-ed1bbad8e8d5", 4.00, 2.0, LocalDate.of(2024, 11, 22));
            UNCaddPurchases(ps, "f133b129-f19c-4864-9fa5-da4a2ea6841c", 4.00, 3.0, LocalDate.of(2024, 11, 22));
            UNCaddPurchases(ps, "060189f1-92e0-48c0-8cb7-e960cf135e42", 2.99, 1.0, LocalDate.of(2024, 11, 22));
            UNCaddPurchases(ps, "55656e11-7e0a-4e33-87ce-749c8f20ab62", 9.49, 2.0, LocalDate.of(2024, 11, 22));
            UNCaddPurchases(ps, "dd1caa5e-63b7-4e8d-b784-3451cdd7384b", 4.49, 3.0, LocalDate.of(2024, 11, 22));
            UNCaddPurchases(ps, "95be1867-3551-431f-80b9-40f811a50e18", 3.00, 2.0, LocalDate.of(2024, 11, 22));
            UNCaddPurchases(ps, "e72178ad-de05-483e-a564-b036785b970c", 3.49, 3.0, LocalDate.of(2024, 11, 22));
            UNCaddPurchases(ps, "b7cde652-ea9a-43d3-bfeb-774e68d384c5", 14.49, 1.0, LocalDate.of(2024, 11, 22));
            UNCaddPurchases(ps, "c05fda44-c11f-476e-be98-d6eadac63a05", 8.00, 2.0, LocalDate.of(2024, 11, 26));
            UNCaddPurchases(ps, "dd1caa5e-63b7-4e8d-b784-3451cdd7384b", 4.49, 2.0, LocalDate.of(2024, 11, 26));
            UNCaddPurchases(ps, "591db857-9027-4214-8d1b-7e6dc559aea0", 10.49, 1.0, LocalDate.of(2024, 11, 26));
            UNCaddPurchases(ps, "e456e25d-c34c-4375-ad3c-f19ef011eafa", 4.49, 3.0, LocalDate.of(2024, 11, 26));
            UNCaddPurchases(ps, "18a9d99b-2c9c-43de-ad6c-685c4f4d4980", 5.49, 1.0, LocalDate.of(2024, 11, 26));
            UNCaddPurchases(ps, "87eee6d3-cb6c-4666-97b0-350ff102f0c4", 5.99, 2.0, LocalDate.of(2024, 11, 26));
            UNCaddPurchases(ps, "43123693-3c38-44bd-a3e9-1a6cb9d08b75", 4.99, 1.0, LocalDate.of(2024, 11, 26));
            UNCaddPurchases(ps, "f133b129-f19c-4864-9fa5-da4a2ea6841c", 3.99, 1.0, LocalDate.of(2024, 11, 26));
            UNCaddPurchases(ps, "3717698b-ba54-4db1-87c1-8404e1c878a8", 18.00, 2.0, LocalDate.of(2024, 11, 26));
            UNCaddPurchases(ps, "ec307717-0725-4560-bc90-b21b815c9a1c", 9.49, 2.0, LocalDate.of(2024, 11, 26));
            UNCaddPurchases(ps, "b7cde652-ea9a-43d3-bfeb-774e68d384c5", 15.00, 2.0, LocalDate.of(2024, 11, 26));
            UNCaddPurchases(ps, "ec307717-0725-4560-bc90-b21b815c9a1c", 10.49, 2.0, LocalDate.of(2024, 12, 1));
            UNCaddPurchases(ps, "103cb413-23b5-44dd-b036-49c6bb71cb70", 3.00, 2.0, LocalDate.of(2024, 12, 1));
            UNCaddPurchases(ps, "b7cde652-ea9a-43d3-bfeb-774e68d384c5", 12.99, 2.0, LocalDate.of(2024, 12, 1));
            UNCaddPurchases(ps, "43123693-3c38-44bd-a3e9-1a6cb9d08b75", 4.49, 1.0, LocalDate.of(2024, 12, 1));
            UNCaddPurchases(ps, "18a9d99b-2c9c-43de-ad6c-685c4f4d4980", 5.49, 3.0, LocalDate.of(2024, 12, 5));
            UNCaddPurchases(ps, "3717698b-ba54-4db1-87c1-8404e1c878a8", 16.00, 2.0, LocalDate.of(2024, 12, 5));
            UNCaddPurchases(ps, "4031b990-016c-47f9-8501-b5c83ae860c1", 2.49, 1.0, LocalDate.of(2024, 12, 5));
            UNCaddPurchases(ps, "591db857-9027-4214-8d1b-7e6dc559aea0", 10.49, 2.0, LocalDate.of(2024, 12, 5));
            UNCaddPurchases(ps, "400f961e-77a1-46c4-8dbd-24703afc9b96", 6.00, 2.0, LocalDate.of(2024, 12, 5));
            UNCaddPurchases(ps, "c05fda44-c11f-476e-be98-d6eadac63a05", 8.99, 1.0, LocalDate.of(2024, 12, 5));
            UNCaddPurchases(ps, "fcd3c43e-b832-466e-96d2-80a7c8b675fb", 13.49, 1.0, LocalDate.of(2024, 12, 9));
            UNCaddPurchases(ps, "dd1caa5e-63b7-4e8d-b784-3451cdd7384b", 4.99, 2.0, LocalDate.of(2024, 12, 9));
            UNCaddPurchases(ps, "76cb1958-00f9-4ee8-8a57-ed1bbad8e8d5", 4.00, 3.0, LocalDate.of(2024, 12, 9));
            UNCaddPurchases(ps, "fccf55de-4945-4636-9138-2924e4d47337", 4.49, 1.0, LocalDate.of(2024, 12, 9));
            UNCaddPurchases(ps, "33986208-d2c6-4cd0-b88b-a671579bdcf0", 16.00, 2.0, LocalDate.of(2024, 12, 9));
            UNCaddPurchases(ps, "f841e912-c7e3-4fb0-950f-bdb149681b7e", 4.99, 1.0, LocalDate.of(2024, 12, 9));
            UNCaddPurchases(ps, "18a9d99b-2c9c-43de-ad6c-685c4f4d4980", 5.99, 2.0, LocalDate.of(2024, 12, 13));
            UNCaddPurchases(ps, "fcd3c43e-b832-466e-96d2-80a7c8b675fb", 13.99, 1.0, LocalDate.of(2024, 12, 13));
            UNCaddPurchases(ps, "43123693-3c38-44bd-a3e9-1a6cb9d08b75", 5.00, 1.0, LocalDate.of(2024, 12, 13));
            UNCaddPurchases(ps, "e72178ad-de05-483e-a564-b036785b970c", 3.49, 3.0, LocalDate.of(2024, 12, 16));
            UNCaddPurchases(ps, "dd1caa5e-63b7-4e8d-b784-3451cdd7384b", 4.00, 2.0, LocalDate.of(2024, 12, 16));
            UNCaddPurchases(ps, "6a0e0b76-0c67-4960-a639-0492e23fb8c1", 6.99, 2.0, LocalDate.of(2024, 12, 16));
            UNCaddPurchases(ps, "55656e11-7e0a-4e33-87ce-749c8f20ab62", 9.99, 3.0, LocalDate.of(2024, 12, 19));
            UNCaddPurchases(ps, "b7cde652-ea9a-43d3-bfeb-774e68d384c5", 14.00, 2.0, LocalDate.of(2024, 12, 19));
            UNCaddPurchases(ps, "591db857-9027-4214-8d1b-7e6dc559aea0", 10.99, 2.0, LocalDate.of(2024, 12, 19));
            UNCaddPurchases(ps, "1b3b034d-2437-46df-a6df-186e9dcdd82d", 22.99, 2.0, LocalDate.of(2024, 12, 19));
            UNCaddPurchases(ps, "0338eeba-4018-4314-b616-384b456e525d", 3.99, 2.0, LocalDate.of(2024, 12, 19));
            UNCaddPurchases(ps, "103cb413-23b5-44dd-b036-49c6bb71cb70", 3.00, 3.0, LocalDate.of(2024, 12, 22));
            UNCaddPurchases(ps, "dd1caa5e-63b7-4e8d-b784-3451cdd7384b", 4.00, 1.0, LocalDate.of(2024, 12, 22));
            UNCaddPurchases(ps, "628ed9f6-3cae-4bc3-afc6-e396a90ff09d", 16.49, 2.0, LocalDate.of(2024, 12, 22));
            UNCaddPurchases(ps, "fcd3c43e-b832-466e-96d2-80a7c8b675fb", 12.00, 2.0, LocalDate.of(2024, 12, 22));
            UNCaddPurchases(ps, "ec307717-0725-4560-bc90-b21b815c9a1c", 9.99, 3.0, LocalDate.of(2024, 12, 22));
            UNCaddPurchases(ps, "a8beb54f-fade-4bec-b5f7-cbd7d248b188", 9.99, 1.0, LocalDate.of(2024, 12, 22));
            UNCaddPurchases(ps, "b2c06d14-5843-49c4-afe8-366d1a760349", 13.49, 1.0, LocalDate.of(2024, 12, 22));
            UNCaddPurchases(ps, "33986208-d2c6-4cd0-b88b-a671579bdcf0", 13.99, 1.0, LocalDate.of(2024, 12, 22));
            UNCaddPurchases(ps, "3717698b-ba54-4db1-87c1-8404e1c878a8", 15.99, 3.0, LocalDate.of(2024, 12, 26));
            UNCaddPurchases(ps, "b7cde652-ea9a-43d3-bfeb-774e68d384c5", 14.00, 2.0, LocalDate.of(2024, 12, 26));
            UNCaddPurchases(ps, "591db857-9027-4214-8d1b-7e6dc559aea0", 11.99, 2.0, LocalDate.of(2024, 12, 26));
            UNCaddPurchases(ps, "43123693-3c38-44bd-a3e9-1a6cb9d08b75", 5.49, 3.0, LocalDate.of(2024, 12, 26));
            UNCaddPurchases(ps, "400f961e-77a1-46c4-8dbd-24703afc9b96", 5.49, 2.0, LocalDate.of(2024, 12, 26));
            UNCaddPurchases(ps, "4031b990-016c-47f9-8501-b5c83ae860c1", 2.49, 1.0, LocalDate.of(2024, 12, 26));
            UNCaddPurchases(ps, "6d00f111-d395-431d-984b-1b25f150f64e", 3.49, 3.0, LocalDate.of(2024, 12, 26));
            UNCaddPurchases(ps, "c05fda44-c11f-476e-be98-d6eadac63a05", 8.00, 1.0, LocalDate.of(2024, 12, 26));
            UNCaddPurchases(ps, "95be1867-3551-431f-80b9-40f811a50e18", 3.49, 1.0, LocalDate.of(2024, 12, 26));
            UNCaddPurchases(ps, "e456e25d-c34c-4375-ad3c-f19ef011eafa", 4.49, 1.0, LocalDate.of(2024, 12, 26));
            UNCaddPurchases(ps, "400f961e-77a1-46c4-8dbd-24703afc9b96", 6.00, 2.0, LocalDate.of(2024, 12, 31));
            UNCaddPurchases(ps, "4031b990-016c-47f9-8501-b5c83ae860c1", 2.49, 2.0, LocalDate.of(2024, 12, 31));
            UNCaddPurchases(ps, "628ed9f6-3cae-4bc3-afc6-e396a90ff09d", 15.99, 1.0, LocalDate.of(2024, 12, 31));
            UNCaddPurchases(ps, "e456e25d-c34c-4375-ad3c-f19ef011eafa", 5.49, 2.0, LocalDate.of(2024, 12, 31));
            UNCaddPurchases(ps, "6a0e0b76-0c67-4960-a639-0492e23fb8c1", 6.99, 2.0, LocalDate.of(2024, 12, 31));
            UNCaddPurchases(ps, "fcd3c43e-b832-466e-96d2-80a7c8b675fb", 12.00, 3.0, LocalDate.of(2024, 12, 31));
            UNCaddPurchases(ps, "060189f1-92e0-48c0-8cb7-e960cf135e42", 3.49, 3.0, LocalDate.of(2024, 12, 31));
            UNCaddPurchases(ps, "e72178ad-de05-483e-a564-b036785b970c", 3.99, 1.0, LocalDate.of(2024, 12, 31));
            UNCaddPurchases(ps, "591db857-9027-4214-8d1b-7e6dc559aea0", 12.49, 1.0, LocalDate.of(2024, 12, 31));
            UNCaddPurchases(ps, "55656e11-7e0a-4e33-87ce-749c8f20ab62", 9.49, 1.0, LocalDate.of(2024, 12, 31));
            UNCaddPurchases(ps, "76cb1958-00f9-4ee8-8a57-ed1bbad8e8d5", 4.00, 1.0, LocalDate.of(2024, 12, 31));
            UNCaddPurchases(ps, "43123693-3c38-44bd-a3e9-1a6cb9d08b75", 5.49, 2.0, LocalDate.of(2024, 12, 31));
            UNCaddPurchases(ps, "87eee6d3-cb6c-4666-97b0-350ff102f0c4", 6.99, 1.0, LocalDate.of(2024, 12, 31));
            UNCaddPurchases(ps, "18a9d99b-2c9c-43de-ad6c-685c4f4d4980", 5.99, 1.0, LocalDate.of(2024, 12, 31));
            UNCaddPurchases(ps, "4031b990-016c-47f9-8501-b5c83ae860c1", 2.49, 3.0, LocalDate.of(2025, 1, 5));
            UNCaddPurchases(ps, "dd1caa5e-63b7-4e8d-b784-3451cdd7384b", 4.49, 1.0, LocalDate.of(2025, 1, 5));
            UNCaddPurchases(ps, "6d00f111-d395-431d-984b-1b25f150f64e", 3.00, 1.0, LocalDate.of(2025, 1, 5));
            UNCaddPurchases(ps, "103cb413-23b5-44dd-b036-49c6bb71cb70", 3.49, 2.0, LocalDate.of(2025, 1, 5));
            UNCaddPurchases(ps, "55656e11-7e0a-4e33-87ce-749c8f20ab62", 8.49, 1.0, LocalDate.of(2025, 1, 8));
            UNCaddPurchases(ps, "bc41cce8-c598-449a-9051-563591c2b1c1", 3.99, 3.0, LocalDate.of(2025, 1, 8));
            UNCaddPurchases(ps, "43123693-3c38-44bd-a3e9-1a6cb9d08b75", 4.99, 2.0, LocalDate.of(2025, 1, 8));
            UNCaddPurchases(ps, "b7cde652-ea9a-43d3-bfeb-774e68d384c5", 13.49, 3.0, LocalDate.of(2025, 1, 8));
            UNCaddPurchases(ps, "18a9d99b-2c9c-43de-ad6c-685c4f4d4980", 5.99, 3.0, LocalDate.of(2025, 1, 8));
            UNCaddPurchases(ps, "400f961e-77a1-46c4-8dbd-24703afc9b96", 6.49, 3.0, LocalDate.of(2025, 1, 8));
            UNCaddPurchases(ps, "b7cde652-ea9a-43d3-bfeb-774e68d384c5", 15.49, 3.0, LocalDate.of(2025, 1, 13));
            UNCaddPurchases(ps, "4031b990-016c-47f9-8501-b5c83ae860c1", 2.49, 2.0, LocalDate.of(2025, 1, 13));
            UNCaddPurchases(ps, "3717698b-ba54-4db1-87c1-8404e1c878a8", 16.00, 2.0, LocalDate.of(2025, 1, 13));
            UNCaddPurchases(ps, "95be1867-3551-431f-80b9-40f811a50e18", 2.99, 3.0, LocalDate.of(2025, 1, 13));
            UNCaddPurchases(ps, "103cb413-23b5-44dd-b036-49c6bb71cb70", 3.49, 2.0, LocalDate.of(2025, 1, 13));
            UNCaddPurchases(ps, "6a0e0b76-0c67-4960-a639-0492e23fb8c1", 6.00, 3.0, LocalDate.of(2025, 1, 13));
            UNCaddPurchases(ps, "55656e11-7e0a-4e33-87ce-749c8f20ab62", 9.00, 2.0, LocalDate.of(2025, 1, 13));
            UNCaddPurchases(ps, "400f961e-77a1-46c4-8dbd-24703afc9b96", 6.00, 1.0, LocalDate.of(2025, 1, 13));
            UNCaddPurchases(ps, "43123693-3c38-44bd-a3e9-1a6cb9d08b75", 4.49, 1.0, LocalDate.of(2025, 1, 13));
            UNCaddPurchases(ps, "dd1caa5e-63b7-4e8d-b784-3451cdd7384b", 4.49, 2.0, LocalDate.of(2025, 1, 13));
            UNCaddPurchases(ps, "3613b659-76eb-4a65-b621-325d30251257", 6.00, 2.0, LocalDate.of(2025, 1, 13));
            UNCaddPurchases(ps, "0338eeba-4018-4314-b616-384b456e525d", 3.49, 2.0, LocalDate.of(2025, 1, 13));
            UNCaddPurchases(ps, "9e882fe3-4670-447f-8ec8-572f4f615047", 7.49, 1.0, LocalDate.of(2025, 1, 13));
            UNCaddPurchases(ps, "f133b129-f19c-4864-9fa5-da4a2ea6841c", 3.99, 1.0, LocalDate.of(2025, 1, 18));
            UNCaddPurchases(ps, "591db857-9027-4214-8d1b-7e6dc559aea0", 11.00, 1.0, LocalDate.of(2025, 1, 18));
            UNCaddPurchases(ps, "4031b990-016c-47f9-8501-b5c83ae860c1", 2.49, 1.0, LocalDate.of(2025, 1, 18));
            UNCaddPurchases(ps, "fcd3c43e-b832-466e-96d2-80a7c8b675fb", 13.99, 2.0, LocalDate.of(2025, 1, 18));
            UNCaddPurchases(ps, "b7cde652-ea9a-43d3-bfeb-774e68d384c5", 14.99, 1.0, LocalDate.of(2025, 1, 18));
            UNCaddPurchases(ps, "6d00f111-d395-431d-984b-1b25f150f64e", 2.99, 3.0, LocalDate.of(2025, 1, 18));
            UNCaddPurchases(ps, "76cb1958-00f9-4ee8-8a57-ed1bbad8e8d5", 4.00, 1.0, LocalDate.of(2025, 1, 18));
            UNCaddPurchases(ps, "c05fda44-c11f-476e-be98-d6eadac63a05", 7.49, 1.0, LocalDate.of(2025, 1, 18));
            UNCaddPurchases(ps, "ec307717-0725-4560-bc90-b21b815c9a1c", 10.49, 1.0, LocalDate.of(2025, 1, 18));
            UNCaddPurchases(ps, "103cb413-23b5-44dd-b036-49c6bb71cb70", 3.49, 1.0, LocalDate.of(2025, 1, 18));
            UNCaddPurchases(ps, "dd1caa5e-63b7-4e8d-b784-3451cdd7384b", 4.49, 1.0, LocalDate.of(2025, 1, 22));
            UNCaddPurchases(ps, "e456e25d-c34c-4375-ad3c-f19ef011eafa", 4.99, 2.0, LocalDate.of(2025, 1, 22));
            UNCaddPurchases(ps, "87eee6d3-cb6c-4666-97b0-350ff102f0c4", 6.49, 1.0, LocalDate.of(2025, 1, 22));
            UNCaddPurchases(ps, "591db857-9027-4214-8d1b-7e6dc559aea0", 10.49, 2.0, LocalDate.of(2025, 1, 22));
            UNCaddPurchases(ps, "060189f1-92e0-48c0-8cb7-e960cf135e42", 3.49, 1.0, LocalDate.of(2025, 1, 22));
            UNCaddPurchases(ps, "628ed9f6-3cae-4bc3-afc6-e396a90ff09d", 15.00, 1.0, LocalDate.of(2025, 1, 22));
            UNCaddPurchases(ps, "bc41cce8-c598-449a-9051-563591c2b1c1", 3.99, 1.0, LocalDate.of(2025, 1, 22));
            UNCaddPurchases(ps, "3717698b-ba54-4db1-87c1-8404e1c878a8", 16.99, 1.0, LocalDate.of(2025, 1, 22));
            UNCaddPurchases(ps, "c05fda44-c11f-476e-be98-d6eadac63a05", 8.49, 1.0, LocalDate.of(2025, 1, 22));
            UNCaddPurchases(ps, "76cb1958-00f9-4ee8-8a57-ed1bbad8e8d5", 4.49, 1.0, LocalDate.of(2025, 1, 22));
            UNCaddPurchases(ps, "400f961e-77a1-46c4-8dbd-24703afc9b96", 5.49, 2.0, LocalDate.of(2025, 1, 25));
            UNCaddPurchases(ps, "103cb413-23b5-44dd-b036-49c6bb71cb70", 3.00, 1.0, LocalDate.of(2025, 1, 25));
            UNCaddPurchases(ps, "fcd3c43e-b832-466e-96d2-80a7c8b675fb", 12.00, 2.0, LocalDate.of(2025, 1, 25));
            UNCaddPurchases(ps, "b7cde652-ea9a-43d3-bfeb-774e68d384c5", 13.00, 3.0, LocalDate.of(2025, 1, 25));
            UNCaddPurchases(ps, "87eee6d3-cb6c-4666-97b0-350ff102f0c4", 5.99, 3.0, LocalDate.of(2025, 1, 25));
            UNCaddPurchases(ps, "f133b129-f19c-4864-9fa5-da4a2ea6841c", 3.99, 2.0, LocalDate.of(2025, 1, 25));
            UNCaddPurchases(ps, "c05fda44-c11f-476e-be98-d6eadac63a05", 8.49, 1.0, LocalDate.of(2025, 1, 25));
            UNCaddPurchases(ps, "43123693-3c38-44bd-a3e9-1a6cb9d08b75", 5.00, 2.0, LocalDate.of(2025, 1, 25));
            UNCaddPurchases(ps, "060189f1-92e0-48c0-8cb7-e960cf135e42", 3.49, 1.0, LocalDate.of(2025, 1, 25));
            UNCaddPurchases(ps, "6d00f111-d395-431d-984b-1b25f150f64e", 3.49, 3.0, LocalDate.of(2025, 1, 25));
            UNCaddPurchases(ps, "e72178ad-de05-483e-a564-b036785b970c", 3.49, 3.0, LocalDate.of(2025, 1, 25));
            UNCaddPurchases(ps, "dd1caa5e-63b7-4e8d-b784-3451cdd7384b", 4.49, 1.0, LocalDate.of(2025, 1, 25));
            UNCaddPurchases(ps, "4031b990-016c-47f9-8501-b5c83ae860c1", 2.49, 1.0, LocalDate.of(2025, 1, 25));
            UNCaddPurchases(ps, "6a0e0b76-0c67-4960-a639-0492e23fb8c1", 6.99, 3.0, LocalDate.of(2025, 1, 25));
            UNCaddPurchases(ps, "b2c06d14-5843-49c4-afe8-366d1a760349", 13.49, 2.0, LocalDate.of(2025, 1, 25));
            UNCaddPurchases(ps, "9e882fe3-4670-447f-8ec8-572f4f615047", 8.00, 1.0, LocalDate.of(2025, 1, 25));
            UNCaddPurchases(ps, "591db857-9027-4214-8d1b-7e6dc559aea0", 10.49, 1.0, LocalDate.of(2025, 1, 28));
            UNCaddPurchases(ps, "4031b990-016c-47f9-8501-b5c83ae860c1", 2.49, 1.0, LocalDate.of(2025, 1, 28));
            UNCaddPurchases(ps, "95be1867-3551-431f-80b9-40f811a50e18", 3.49, 2.0, LocalDate.of(2025, 1, 28));
            UNCaddPurchases(ps, "6a0e0b76-0c67-4960-a639-0492e23fb8c1", 6.00, 2.0, LocalDate.of(2025, 2, 1));
            UNCaddPurchases(ps, "76cb1958-00f9-4ee8-8a57-ed1bbad8e8d5", 4.00, 1.0, LocalDate.of(2025, 2, 1));
            UNCaddPurchases(ps, "bc41cce8-c598-449a-9051-563591c2b1c1", 3.49, 1.0, LocalDate.of(2025, 2, 1));
            UNCaddPurchases(ps, "43123693-3c38-44bd-a3e9-1a6cb9d08b75", 4.49, 3.0, LocalDate.of(2025, 2, 1));
            UNCaddPurchases(ps, "95be1867-3551-431f-80b9-40f811a50e18", 2.99, 3.0, LocalDate.of(2025, 2, 1));
            UNCaddPurchases(ps, "400f961e-77a1-46c4-8dbd-24703afc9b96", 6.49, 2.0, LocalDate.of(2025, 2, 1));
            UNCaddPurchases(ps, "e456e25d-c34c-4375-ad3c-f19ef011eafa", 4.99, 1.0, LocalDate.of(2025, 2, 5));
            UNCaddPurchases(ps, "e72178ad-de05-483e-a564-b036785b970c", 3.99, 2.0, LocalDate.of(2025, 2, 5));
            UNCaddPurchases(ps, "76cb1958-00f9-4ee8-8a57-ed1bbad8e8d5", 4.49, 2.0, LocalDate.of(2025, 2, 5));
            UNCaddPurchases(ps, "55656e11-7e0a-4e33-87ce-749c8f20ab62", 9.99, 3.0, LocalDate.of(2025, 2, 5));
            UNCaddPurchases(ps, "f133b129-f19c-4864-9fa5-da4a2ea6841c", 3.99, 3.0, LocalDate.of(2025, 2, 5));
            UNCaddPurchases(ps, "628ed9f6-3cae-4bc3-afc6-e396a90ff09d", 16.49, 1.0, LocalDate.of(2025, 2, 5));
            UNCaddPurchases(ps, "6a0e0b76-0c67-4960-a639-0492e23fb8c1", 6.49, 2.0, LocalDate.of(2025, 2, 5));
            UNCaddPurchases(ps, "103cb413-23b5-44dd-b036-49c6bb71cb70", 3.49, 1.0, LocalDate.of(2025, 2, 5));
            UNCaddPurchases(ps, "060189f1-92e0-48c0-8cb7-e960cf135e42", 3.00, 2.0, LocalDate.of(2025, 2, 5));
            UNCaddPurchases(ps, "43123693-3c38-44bd-a3e9-1a6cb9d08b75", 5.00, 3.0, LocalDate.of(2025, 2, 5));
            UNCaddPurchases(ps, "b7cde652-ea9a-43d3-bfeb-774e68d384c5", 14.99, 3.0, LocalDate.of(2025, 2, 5));
            UNCaddPurchases(ps, "18a9d99b-2c9c-43de-ad6c-685c4f4d4980", 5.49, 2.0, LocalDate.of(2025, 2, 5));
            UNCaddPurchases(ps, "ec307717-0725-4560-bc90-b21b815c9a1c", 9.49, 3.0, LocalDate.of(2025, 2, 5));
            UNCaddPurchases(ps, "c05fda44-c11f-476e-be98-d6eadac63a05", 7.49, 3.0, LocalDate.of(2025, 2, 5));
            UNCaddPurchases(ps, "33986208-d2c6-4cd0-b88b-a671579bdcf0", 15.99, 1.0, LocalDate.of(2025, 2, 5));
            UNCaddPurchases(ps, "683d3699-6bb3-4b08-977e-02a22d07ea51", 10.49, 1.0, LocalDate.of(2025, 2, 5));
            UNCaddPurchases(ps, "6a0e0b76-0c67-4960-a639-0492e23fb8c1", 6.00, 3.0, LocalDate.of(2025, 2, 9));
            UNCaddPurchases(ps, "e456e25d-c34c-4375-ad3c-f19ef011eafa", 5.00, 1.0, LocalDate.of(2025, 2, 9));
            UNCaddPurchases(ps, "bc41cce8-c598-449a-9051-563591c2b1c1", 3.49, 2.0, LocalDate.of(2025, 2, 9));
            UNCaddPurchases(ps, "dd1caa5e-63b7-4e8d-b784-3451cdd7384b", 4.99, 1.0, LocalDate.of(2025, 2, 9));
            UNCaddPurchases(ps, "43123693-3c38-44bd-a3e9-1a6cb9d08b75", 5.00, 1.0, LocalDate.of(2025, 2, 9));
            UNCaddPurchases(ps, "c05fda44-c11f-476e-be98-d6eadac63a05", 8.49, 3.0, LocalDate.of(2025, 2, 9));
            UNCaddPurchases(ps, "103cb413-23b5-44dd-b036-49c6bb71cb70", 3.49, 2.0, LocalDate.of(2025, 2, 9));
            UNCaddPurchases(ps, "400f961e-77a1-46c4-8dbd-24703afc9b96", 6.49, 2.0, LocalDate.of(2025, 2, 9));
            UNCaddPurchases(ps, "76cb1958-00f9-4ee8-8a57-ed1bbad8e8d5", 4.49, 2.0, LocalDate.of(2025, 2, 9));
            UNCaddPurchases(ps, "628ed9f6-3cae-4bc3-afc6-e396a90ff09d", 13.99, 1.0, LocalDate.of(2025, 2, 9));
            UNCaddPurchases(ps, "fcd3c43e-b832-466e-96d2-80a7c8b675fb", 12.49, 3.0, LocalDate.of(2025, 2, 9));
            UNCaddPurchases(ps, "b7cde652-ea9a-43d3-bfeb-774e68d384c5", 14.00, 1.0, LocalDate.of(2025, 2, 9));
            UNCaddPurchases(ps, "10b5a323-f47e-411f-ba55-216b2cdf2bd3", 11.00, 2.0, LocalDate.of(2025, 2, 9));
            UNCaddPurchases(ps, "fccf55de-4945-4636-9138-2924e4d47337", 4.99, 2.0, LocalDate.of(2025, 2, 9));
            UNCaddPurchases(ps, "bc41cce8-c598-449a-9051-563591c2b1c1", 3.99, 3.0, LocalDate.of(2025, 2, 12));
            UNCaddPurchases(ps, "c05fda44-c11f-476e-be98-d6eadac63a05", 7.49, 1.0, LocalDate.of(2025, 2, 12));
            UNCaddPurchases(ps, "e456e25d-c34c-4375-ad3c-f19ef011eafa", 5.00, 3.0, LocalDate.of(2025, 2, 12));
            UNCaddPurchases(ps, "b7cde652-ea9a-43d3-bfeb-774e68d384c5", 14.00, 3.0, LocalDate.of(2025, 2, 12));
            UNCaddPurchases(ps, "18a9d99b-2c9c-43de-ad6c-685c4f4d4980", 5.49, 1.0, LocalDate.of(2025, 2, 12));
            UNCaddPurchases(ps, "3717698b-ba54-4db1-87c1-8404e1c878a8", 15.99, 3.0, LocalDate.of(2025, 2, 12));
            UNCaddPurchases(ps, "76cb1958-00f9-4ee8-8a57-ed1bbad8e8d5", 4.49, 3.0, LocalDate.of(2025, 2, 12));
            UNCaddPurchases(ps, "400f961e-77a1-46c4-8dbd-24703afc9b96", 5.99, 1.0, LocalDate.of(2025, 2, 12));
            UNCaddPurchases(ps, "dd1caa5e-63b7-4e8d-b784-3451cdd7384b", 4.49, 1.0, LocalDate.of(2025, 2, 12));
            UNCaddPurchases(ps, "103cb413-23b5-44dd-b036-49c6bb71cb70", 3.00, 1.0, LocalDate.of(2025, 2, 12));
            UNCaddPurchases(ps, "55656e11-7e0a-4e33-87ce-749c8f20ab62", 9.49, 2.0, LocalDate.of(2025, 2, 12));
            UNCaddPurchases(ps, "628ed9f6-3cae-4bc3-afc6-e396a90ff09d", 15.49, 3.0, LocalDate.of(2025, 2, 12));
            UNCaddPurchases(ps, "6a0e0b76-0c67-4960-a639-0492e23fb8c1", 6.00, 2.0, LocalDate.of(2025, 2, 12));
            UNCaddPurchases(ps, "103cb413-23b5-44dd-b036-49c6bb71cb70", 3.00, 3.0, LocalDate.of(2025, 2, 15));
            UNCaddPurchases(ps, "c05fda44-c11f-476e-be98-d6eadac63a05", 7.49, 2.0, LocalDate.of(2025, 2, 15));
            UNCaddPurchases(ps, "628ed9f6-3cae-4bc3-afc6-e396a90ff09d", 15.99, 1.0, LocalDate.of(2025, 2, 15));
            UNCaddPurchases(ps, "ec307717-0725-4560-bc90-b21b815c9a1c", 9.49, 3.0, LocalDate.of(2025, 2, 15));
            UNCaddPurchases(ps, "e72178ad-de05-483e-a564-b036785b970c", 3.99, 1.0, LocalDate.of(2025, 2, 18));
            UNCaddPurchases(ps, "591db857-9027-4214-8d1b-7e6dc559aea0", 12.49, 3.0, LocalDate.of(2025, 2, 18));
            UNCaddPurchases(ps, "103cb413-23b5-44dd-b036-49c6bb71cb70", 3.49, 1.0, LocalDate.of(2025, 2, 18));
            UNCaddPurchases(ps, "060189f1-92e0-48c0-8cb7-e960cf135e42", 3.49, 2.0, LocalDate.of(2025, 2, 18));
            UNCaddPurchases(ps, "dd1caa5e-63b7-4e8d-b784-3451cdd7384b", 4.99, 2.0, LocalDate.of(2025, 2, 18));
            UNCaddPurchases(ps, "b7cde652-ea9a-43d3-bfeb-774e68d384c5", 13.49, 3.0, LocalDate.of(2025, 2, 18));
            UNCaddPurchases(ps, "18a9d99b-2c9c-43de-ad6c-685c4f4d4980", 5.00, 3.0, LocalDate.of(2025, 2, 18));
            UNCaddPurchases(ps, "4031b990-016c-47f9-8501-b5c83ae860c1", 2.49, 3.0, LocalDate.of(2025, 2, 18));
            UNCaddPurchases(ps, "e456e25d-c34c-4375-ad3c-f19ef011eafa", 5.49, 1.0, LocalDate.of(2025, 2, 18));
            UNCaddPurchases(ps, "628ed9f6-3cae-4bc3-afc6-e396a90ff09d", 14.00, 3.0, LocalDate.of(2025, 2, 18));
            UNCaddPurchases(ps, "6a0e0b76-0c67-4960-a639-0492e23fb8c1", 7.00, 2.0, LocalDate.of(2025, 2, 18));
            UNCaddPurchases(ps, "55656e11-7e0a-4e33-87ce-749c8f20ab62", 8.99, 2.0, LocalDate.of(2025, 2, 18));
            UNCaddPurchases(ps, "f133b129-f19c-4864-9fa5-da4a2ea6841c", 3.49, 2.0, LocalDate.of(2025, 2, 21));
            UNCaddPurchases(ps, "ec307717-0725-4560-bc90-b21b815c9a1c", 9.99, 1.0, LocalDate.of(2025, 2, 21));
            UNCaddPurchases(ps, "87eee6d3-cb6c-4666-97b0-350ff102f0c4", 6.49, 2.0, LocalDate.of(2025, 2, 21));
            UNCaddPurchases(ps, "400f961e-77a1-46c4-8dbd-24703afc9b96", 6.49, 3.0, LocalDate.of(2025, 2, 21));
            UNCaddPurchases(ps, "dd1caa5e-63b7-4e8d-b784-3451cdd7384b", 5.00, 3.0, LocalDate.of(2025, 2, 21));
            UNCaddPurchases(ps, "6a0e0b76-0c67-4960-a639-0492e23fb8c1", 6.49, 3.0, LocalDate.of(2025, 2, 21));
            UNCaddPurchases(ps, "76cb1958-00f9-4ee8-8a57-ed1bbad8e8d5", 4.49, 3.0, LocalDate.of(2025, 2, 21));
            UNCaddPurchases(ps, "3717698b-ba54-4db1-87c1-8404e1c878a8", 15.49, 1.0, LocalDate.of(2025, 2, 21));
            UNCaddPurchases(ps, "103cb413-23b5-44dd-b036-49c6bb71cb70", 3.00, 3.0, LocalDate.of(2025, 2, 21));
            UNCaddPurchases(ps, "e72178ad-de05-483e-a564-b036785b970c", 3.99, 3.0, LocalDate.of(2025, 2, 21));
            UNCaddPurchases(ps, "c05fda44-c11f-476e-be98-d6eadac63a05", 8.49, 2.0, LocalDate.of(2025, 2, 21));
            UNCaddPurchases(ps, "628ed9f6-3cae-4bc3-afc6-e396a90ff09d", 15.99, 1.0, LocalDate.of(2025, 2, 26));
            UNCaddPurchases(ps, "c05fda44-c11f-476e-be98-d6eadac63a05", 8.00, 1.0, LocalDate.of(2025, 2, 26));
            UNCaddPurchases(ps, "18a9d99b-2c9c-43de-ad6c-685c4f4d4980", 5.99, 1.0, LocalDate.of(2025, 2, 26));
            UNCaddPurchases(ps, "ec307717-0725-4560-bc90-b21b815c9a1c", 10.49, 3.0, LocalDate.of(2025, 2, 26));
            UNCaddPurchases(ps, "fcd3c43e-b832-466e-96d2-80a7c8b675fb", 13.00, 1.0, LocalDate.of(2025, 3, 2));
            UNCaddPurchases(ps, "103cb413-23b5-44dd-b036-49c6bb71cb70", 3.00, 1.0, LocalDate.of(2025, 3, 2));
            UNCaddPurchases(ps, "ec307717-0725-4560-bc90-b21b815c9a1c", 10.99, 1.0, LocalDate.of(2025, 3, 2));
            UNCaddPurchases(ps, "76cb1958-00f9-4ee8-8a57-ed1bbad8e8d5", 3.99, 2.0, LocalDate.of(2025, 3, 2));
            UNCaddPurchases(ps, "55656e11-7e0a-4e33-87ce-749c8f20ab62", 9.49, 3.0, LocalDate.of(2025, 3, 2));
            UNCaddPurchases(ps, "5f871133-c141-42e8-b071-c569e5dfbe57", 8.99, 1.0, LocalDate.of(2025, 3, 2));
            UNCaddPurchases(ps, "683d3699-6bb3-4b08-977e-02a22d07ea51", 11.49, 2.0, LocalDate.of(2025, 3, 2));
            UNCaddPurchases(ps, "a8f8b02e-cd64-4c02-9cfb-4df9c19b5e30", 6.00, 1.0, LocalDate.of(2025, 3, 2));
            UNCaddPurchases(ps, "591db857-9027-4214-8d1b-7e6dc559aea0", 12.00, 3.0, LocalDate.of(2025, 3, 5));
            UNCaddPurchases(ps, "95be1867-3551-431f-80b9-40f811a50e18", 2.99, 1.0, LocalDate.of(2025, 3, 5));
            UNCaddPurchases(ps, "55656e11-7e0a-4e33-87ce-749c8f20ab62", 8.99, 2.0, LocalDate.of(2025, 3, 5));
            UNCaddPurchases(ps, "bc41cce8-c598-449a-9051-563591c2b1c1", 3.49, 2.0, LocalDate.of(2025, 3, 5));
            UNCaddPurchases(ps, "87eee6d3-cb6c-4666-97b0-350ff102f0c4", 6.00, 2.0, LocalDate.of(2025, 3, 5));
            UNCaddPurchases(ps, "6a0e0b76-0c67-4960-a639-0492e23fb8c1", 7.00, 2.0, LocalDate.of(2025, 3, 5));
            UNCaddPurchases(ps, "628ed9f6-3cae-4bc3-afc6-e396a90ff09d", 15.49, 1.0, LocalDate.of(2025, 3, 5));
            UNCaddPurchases(ps, "fcd3c43e-b832-466e-96d2-80a7c8b675fb", 14.49, 2.0, LocalDate.of(2025, 3, 5));
            UNCaddPurchases(ps, "4031b990-016c-47f9-8501-b5c83ae860c1", 2.49, 3.0, LocalDate.of(2025, 3, 5));
            UNCaddPurchases(ps, "dd1caa5e-63b7-4e8d-b784-3451cdd7384b", 4.99, 1.0, LocalDate.of(2025, 3, 5));
            UNCaddPurchases(ps, "103cb413-23b5-44dd-b036-49c6bb71cb70", 3.00, 3.0, LocalDate.of(2025, 3, 5));
            UNCaddPurchases(ps, "6ec93eb4-8706-4e92-bbb1-d35ccbaa818e", 7.49, 2.0, LocalDate.of(2025, 3, 5));
            UNCaddPurchases(ps, "4031b990-016c-47f9-8501-b5c83ae860c1", 2.49, 3.0, LocalDate.of(2025, 3, 10));
            UNCaddPurchases(ps, "bc41cce8-c598-449a-9051-563591c2b1c1", 3.49, 2.0, LocalDate.of(2025, 3, 10));
            UNCaddPurchases(ps, "6a0e0b76-0c67-4960-a639-0492e23fb8c1", 6.99, 3.0, LocalDate.of(2025, 3, 10));
            UNCaddPurchases(ps, "e72178ad-de05-483e-a564-b036785b970c", 3.49, 2.0, LocalDate.of(2025, 3, 13));
            UNCaddPurchases(ps, "95be1867-3551-431f-80b9-40f811a50e18", 2.99, 3.0, LocalDate.of(2025, 3, 13));
            UNCaddPurchases(ps, "628ed9f6-3cae-4bc3-afc6-e396a90ff09d", 13.99, 3.0, LocalDate.of(2025, 3, 13));
            UNCaddPurchases(ps, "6d00f111-d395-431d-984b-1b25f150f64e", 2.99, 2.0, LocalDate.of(2025, 3, 13));
            UNCaddPurchases(ps, "591db857-9027-4214-8d1b-7e6dc559aea0", 12.49, 2.0, LocalDate.of(2025, 3, 13));
            UNCaddPurchases(ps, "95be1867-3551-431f-80b9-40f811a50e18", 3.49, 2.0, LocalDate.of(2025, 3, 18));
            UNCaddPurchases(ps, "628ed9f6-3cae-4bc3-afc6-e396a90ff09d", 14.49, 1.0, LocalDate.of(2025, 3, 18));
            UNCaddPurchases(ps, "76cb1958-00f9-4ee8-8a57-ed1bbad8e8d5", 4.00, 2.0, LocalDate.of(2025, 3, 18));
            UNCaddPurchases(ps, "b7cde652-ea9a-43d3-bfeb-774e68d384c5", 13.99, 2.0, LocalDate.of(2025, 3, 21));
            UNCaddPurchases(ps, "76cb1958-00f9-4ee8-8a57-ed1bbad8e8d5", 4.49, 2.0, LocalDate.of(2025, 3, 21));
            UNCaddPurchases(ps, "43123693-3c38-44bd-a3e9-1a6cb9d08b75", 5.00, 3.0, LocalDate.of(2025, 3, 21));
            UNCaddPurchases(ps, "060189f1-92e0-48c0-8cb7-e960cf135e42", 3.00, 3.0, LocalDate.of(2025, 3, 21));
            UNCaddPurchases(ps, "4031b990-016c-47f9-8501-b5c83ae860c1", 2.49, 1.0, LocalDate.of(2025, 3, 21));
            UNCaddPurchases(ps, "628ed9f6-3cae-4bc3-afc6-e396a90ff09d", 14.00, 2.0, LocalDate.of(2025, 3, 21));
            UNCaddPurchases(ps, "dd1caa5e-63b7-4e8d-b784-3451cdd7384b", 4.49, 3.0, LocalDate.of(2025, 3, 21));
            UNCaddPurchases(ps, "6d00f111-d395-431d-984b-1b25f150f64e", 3.00, 3.0, LocalDate.of(2025, 3, 21));
            UNCaddPurchases(ps, "18a9d99b-2c9c-43de-ad6c-685c4f4d4980", 5.49, 3.0, LocalDate.of(2025, 3, 21));
            UNCaddPurchases(ps, "fcd3c43e-b832-466e-96d2-80a7c8b675fb", 13.99, 3.0, LocalDate.of(2025, 3, 21));
            UNCaddPurchases(ps, "f133b129-f19c-4864-9fa5-da4a2ea6841c", 3.99, 3.0, LocalDate.of(2025, 3, 21));
            UNCaddPurchases(ps, "95be1867-3551-431f-80b9-40f811a50e18", 3.49, 2.0, LocalDate.of(2025, 3, 21));
            UNCaddPurchases(ps, "e72178ad-de05-483e-a564-b036785b970c", 3.49, 3.0, LocalDate.of(2025, 3, 21));
            UNCaddPurchases(ps, "f133b129-f19c-4864-9fa5-da4a2ea6841c", 4.00, 3.0, LocalDate.of(2025, 3, 25));
            UNCaddPurchases(ps, "4031b990-016c-47f9-8501-b5c83ae860c1", 2.49, 1.0, LocalDate.of(2025, 3, 25));
            UNCaddPurchases(ps, "87eee6d3-cb6c-4666-97b0-350ff102f0c4", 6.49, 1.0, LocalDate.of(2025, 3, 25));
            UNCaddPurchases(ps, "55656e11-7e0a-4e33-87ce-749c8f20ab62", 8.49, 1.0, LocalDate.of(2025, 3, 25));
            UNCaddPurchases(ps, "103cb413-23b5-44dd-b036-49c6bb71cb70", 3.00, 1.0, LocalDate.of(2025, 3, 30));
            UNCaddPurchases(ps, "95be1867-3551-431f-80b9-40f811a50e18", 3.00, 1.0, LocalDate.of(2025, 3, 30));
            UNCaddPurchases(ps, "4031b990-016c-47f9-8501-b5c83ae860c1", 2.49, 1.0, LocalDate.of(2025, 3, 30));
            UNCaddPurchases(ps, "87eee6d3-cb6c-4666-97b0-350ff102f0c4", 6.99, 1.0, LocalDate.of(2025, 3, 30));
            UNCaddPurchases(ps, "6a0e0b76-0c67-4960-a639-0492e23fb8c1", 6.49, 1.0, LocalDate.of(2025, 4, 2));
            UNCaddPurchases(ps, "76cb1958-00f9-4ee8-8a57-ed1bbad8e8d5", 3.99, 2.0, LocalDate.of(2025, 4, 2));
            UNCaddPurchases(ps, "591db857-9027-4214-8d1b-7e6dc559aea0", 12.99, 1.0, LocalDate.of(2025, 4, 2));
            UNCaddPurchases(ps, "6d00f111-d395-431d-984b-1b25f150f64e", 3.00, 3.0, LocalDate.of(2025, 4, 2));
            UNCaddPurchases(ps, "628ed9f6-3cae-4bc3-afc6-e396a90ff09d", 14.00, 3.0, LocalDate.of(2025, 4, 2));
            UNCaddPurchases(ps, "3717698b-ba54-4db1-87c1-8404e1c878a8", 17.99, 3.0, LocalDate.of(2025, 4, 2));
            UNCaddPurchases(ps, "e456e25d-c34c-4375-ad3c-f19ef011eafa", 4.99, 1.0, LocalDate.of(2025, 4, 6));
            UNCaddPurchases(ps, "4031b990-016c-47f9-8501-b5c83ae860c1", 2.49, 1.0, LocalDate.of(2025, 4, 6));
            UNCaddPurchases(ps, "6a0e0b76-0c67-4960-a639-0492e23fb8c1", 6.49, 2.0, LocalDate.of(2025, 4, 6));
            UNCaddPurchases(ps, "628ed9f6-3cae-4bc3-afc6-e396a90ff09d", 14.99, 2.0, LocalDate.of(2025, 4, 6));
            UNCaddPurchases(ps, "43123693-3c38-44bd-a3e9-1a6cb9d08b75", 4.99, 1.0, LocalDate.of(2025, 4, 6));
            UNCaddPurchases(ps, "fcd3c43e-b832-466e-96d2-80a7c8b675fb", 13.99, 2.0, LocalDate.of(2025, 4, 10));
            UNCaddPurchases(ps, "6a0e0b76-0c67-4960-a639-0492e23fb8c1", 6.00, 3.0, LocalDate.of(2025, 4, 10));
            UNCaddPurchases(ps, "e72178ad-de05-483e-a564-b036785b970c", 3.49, 3.0, LocalDate.of(2025, 4, 10));
            UNCaddPurchases(ps, "e456e25d-c34c-4375-ad3c-f19ef011eafa", 5.49, 2.0, LocalDate.of(2025, 4, 10));
            UNCaddPurchases(ps, "87eee6d3-cb6c-4666-97b0-350ff102f0c4", 6.99, 2.0, LocalDate.of(2025, 4, 10));
            UNCaddPurchases(ps, "bc41cce8-c598-449a-9051-563591c2b1c1", 3.49, 3.0, LocalDate.of(2025, 4, 10));
            UNCaddPurchases(ps, "591db857-9027-4214-8d1b-7e6dc559aea0", 11.00, 2.0, LocalDate.of(2025, 4, 10));
            UNCaddPurchases(ps, "060189f1-92e0-48c0-8cb7-e960cf135e42", 3.49, 2.0, LocalDate.of(2025, 4, 10));
            UNCaddPurchases(ps, "b7cde652-ea9a-43d3-bfeb-774e68d384c5", 14.49, 2.0, LocalDate.of(2025, 4, 10));
            UNCaddPurchases(ps, "628ed9f6-3cae-4bc3-afc6-e396a90ff09d", 14.00, 3.0, LocalDate.of(2025, 4, 10));
            UNCaddPurchases(ps, "4031b990-016c-47f9-8501-b5c83ae860c1", 2.49, 2.0, LocalDate.of(2025, 4, 10));
            UNCaddPurchases(ps, "f133b129-f19c-4864-9fa5-da4a2ea6841c", 3.49, 2.0, LocalDate.of(2025, 4, 15));
            UNCaddPurchases(ps, "87eee6d3-cb6c-4666-97b0-350ff102f0c4", 6.49, 2.0, LocalDate.of(2025, 4, 15));
            UNCaddPurchases(ps, "103cb413-23b5-44dd-b036-49c6bb71cb70", 3.00, 1.0, LocalDate.of(2025, 4, 15));
            UNCaddPurchases(ps, "dd1caa5e-63b7-4e8d-b784-3451cdd7384b", 5.00, 3.0, LocalDate.of(2025, 4, 20));
            UNCaddPurchases(ps, "95be1867-3551-431f-80b9-40f811a50e18", 2.99, 3.0, LocalDate.of(2025, 4, 20));
            UNCaddPurchases(ps, "76cb1958-00f9-4ee8-8a57-ed1bbad8e8d5", 4.49, 1.0, LocalDate.of(2025, 4, 20));
            UNCaddPurchases(ps, "591db857-9027-4214-8d1b-7e6dc559aea0", 12.00, 1.0, LocalDate.of(2025, 4, 20));
            UNCaddPurchases(ps, "400f961e-77a1-46c4-8dbd-24703afc9b96", 6.49, 3.0, LocalDate.of(2025, 4, 20));
            UNCaddPurchases(ps, "4031b990-016c-47f9-8501-b5c83ae860c1", 2.99, 3.0, LocalDate.of(2025, 4, 20));
            UNCaddPurchases(ps, "6a0e0b76-0c67-4960-a639-0492e23fb8c1", 6.49, 3.0, LocalDate.of(2025, 4, 20));
            UNCaddPurchases(ps, "e456e25d-c34c-4375-ad3c-f19ef011eafa", 4.99, 3.0, LocalDate.of(2025, 4, 20));
            UNCaddPurchases(ps, "060189f1-92e0-48c0-8cb7-e960cf135e42", 3.49, 2.0, LocalDate.of(2025, 4, 20));
            UNCaddPurchases(ps, "c05fda44-c11f-476e-be98-d6eadac63a05", 7.49, 1.0, LocalDate.of(2025, 4, 20));
            UNCaddPurchases(ps, "b7cde652-ea9a-43d3-bfeb-774e68d384c5", 15.49, 2.0, LocalDate.of(2025, 4, 20));
            UNCaddPurchases(ps, "87eee6d3-cb6c-4666-97b0-350ff102f0c4", 6.99, 2.0, LocalDate.of(2025, 4, 20));
            UNCaddPurchases(ps, "f133b129-f19c-4864-9fa5-da4a2ea6841c", 3.99, 1.0, LocalDate.of(2025, 4, 24));
            UNCaddPurchases(ps, "4031b990-016c-47f9-8501-b5c83ae860c1", 2.49, 3.0, LocalDate.of(2025, 4, 24));
            UNCaddPurchases(ps, "76cb1958-00f9-4ee8-8a57-ed1bbad8e8d5", 3.49, 2.0, LocalDate.of(2025, 4, 24));
            UNCaddPurchases(ps, "3717698b-ba54-4db1-87c1-8404e1c878a8", 17.00, 2.0, LocalDate.of(2025, 4, 24));
            UNCaddPurchases(ps, "e456e25d-c34c-4375-ad3c-f19ef011eafa", 5.00, 1.0, LocalDate.of(2025, 4, 24));
            UNCaddPurchases(ps, "628ed9f6-3cae-4bc3-afc6-e396a90ff09d", 16.49, 1.0, LocalDate.of(2025, 4, 24));
            UNCaddPurchases(ps, "18a9d99b-2c9c-43de-ad6c-685c4f4d4980", 5.49, 3.0, LocalDate.of(2025, 4, 27));
            UNCaddPurchases(ps, "95be1867-3551-431f-80b9-40f811a50e18", 3.00, 1.0, LocalDate.of(2025, 4, 27));
            UNCaddPurchases(ps, "e456e25d-c34c-4375-ad3c-f19ef011eafa", 5.49, 1.0, LocalDate.of(2025, 4, 27));
            UNCaddPurchases(ps, "ec307717-0725-4560-bc90-b21b815c9a1c", 9.49, 3.0, LocalDate.of(2025, 4, 27));
            UNCaddPurchases(ps, "3717698b-ba54-4db1-87c1-8404e1c878a8", 18.99, 1.0, LocalDate.of(2025, 4, 27));
            UNCaddPurchases(ps, "bc41cce8-c598-449a-9051-563591c2b1c1", 3.49, 3.0, LocalDate.of(2025, 4, 27));
            UNCaddPurchases(ps, "f133b129-f19c-4864-9fa5-da4a2ea6841c", 3.49, 1.0, LocalDate.of(2025, 4, 30));
            UNCaddPurchases(ps, "fcd3c43e-b832-466e-96d2-80a7c8b675fb", 12.99, 3.0, LocalDate.of(2025, 4, 30));
            UNCaddPurchases(ps, "18a9d99b-2c9c-43de-ad6c-685c4f4d4980", 5.99, 1.0, LocalDate.of(2025, 4, 30));
            UNCaddPurchases(ps, "43123693-3c38-44bd-a3e9-1a6cb9d08b75", 5.00, 3.0, LocalDate.of(2025, 4, 30));
            UNCaddPurchases(ps, "b7cde652-ea9a-43d3-bfeb-774e68d384c5", 14.99, 3.0, LocalDate.of(2025, 4, 30));
            UNCaddPurchases(ps, "dd1caa5e-63b7-4e8d-b784-3451cdd7384b", 4.49, 1.0, LocalDate.of(2025, 5, 4));
            UNCaddPurchases(ps, "e456e25d-c34c-4375-ad3c-f19ef011eafa", 5.49, 3.0, LocalDate.of(2025, 5, 4));
            UNCaddPurchases(ps, "591db857-9027-4214-8d1b-7e6dc559aea0", 11.49, 2.0, LocalDate.of(2025, 5, 4));
            UNCaddPurchases(ps, "18a9d99b-2c9c-43de-ad6c-685c4f4d4980", 5.00, 1.0, LocalDate.of(2025, 5, 8));
            UNCaddPurchases(ps, "e72178ad-de05-483e-a564-b036785b970c", 3.49, 3.0, LocalDate.of(2025, 5, 8));
            UNCaddPurchases(ps, "6a0e0b76-0c67-4960-a639-0492e23fb8c1", 7.00, 3.0, LocalDate.of(2025, 5, 8));
            UNCaddPurchases(ps, "ec307717-0725-4560-bc90-b21b815c9a1c", 11.00, 1.0, LocalDate.of(2025, 5, 8));
            UNCaddPurchases(ps, "400f961e-77a1-46c4-8dbd-24703afc9b96", 5.99, 2.0, LocalDate.of(2025, 5, 8));
            UNCaddPurchases(ps, "95be1867-3551-431f-80b9-40f811a50e18", 3.00, 3.0, LocalDate.of(2025, 5, 8));
            UNCaddPurchases(ps, "4031b990-016c-47f9-8501-b5c83ae860c1", 2.49, 2.0, LocalDate.of(2025, 5, 8));
            UNCaddPurchases(ps, "dd1caa5e-63b7-4e8d-b784-3451cdd7384b", 4.99, 2.0, LocalDate.of(2025, 5, 8));
            UNCaddPurchases(ps, "628ed9f6-3cae-4bc3-afc6-e396a90ff09d", 16.49, 1.0, LocalDate.of(2025, 5, 8));
            UNCaddPurchases(ps, "e456e25d-c34c-4375-ad3c-f19ef011eafa", 5.49, 1.0, LocalDate.of(2025, 5, 8));
            UNCaddPurchases(ps, "b7cde652-ea9a-43d3-bfeb-774e68d384c5", 15.99, 1.0, LocalDate.of(2025, 5, 8));
            UNCaddPurchases(ps, "f133b129-f19c-4864-9fa5-da4a2ea6841c", 3.99, 3.0, LocalDate.of(2025, 5, 8));
            UNCaddPurchases(ps, "bc41cce8-c598-449a-9051-563591c2b1c1", 3.49, 3.0, LocalDate.of(2025, 5, 8));
            UNCaddPurchases(ps, "e456e25d-c34c-4375-ad3c-f19ef011eafa", 5.00, 2.0, LocalDate.of(2025, 5, 11));
            UNCaddPurchases(ps, "3717698b-ba54-4db1-87c1-8404e1c878a8", 18.49, 3.0, LocalDate.of(2025, 5, 11));
            UNCaddPurchases(ps, "6a0e0b76-0c67-4960-a639-0492e23fb8c1", 6.00, 3.0, LocalDate.of(2025, 5, 11));
            UNCaddPurchases(ps, "060189f1-92e0-48c0-8cb7-e960cf135e42", 3.49, 3.0, LocalDate.of(2025, 5, 11));
            UNCaddPurchases(ps, "43123693-3c38-44bd-a3e9-1a6cb9d08b75", 4.99, 3.0, LocalDate.of(2025, 5, 11));
            UNCaddPurchases(ps, "a8beb54f-fade-4bec-b5f7-cbd7d248b188", 10.00, 1.0, LocalDate.of(2025, 5, 11));
            UNCaddPurchases(ps, "f841e912-c7e3-4fb0-950f-bdb149681b7e", 5.49, 2.0, LocalDate.of(2025, 5, 11));
            UNCaddPurchases(ps, "55656e11-7e0a-4e33-87ce-749c8f20ab62", 8.49, 2.0, LocalDate.of(2025, 5, 14));
            UNCaddPurchases(ps, "76cb1958-00f9-4ee8-8a57-ed1bbad8e8d5", 3.99, 3.0, LocalDate.of(2025, 5, 14));
            UNCaddPurchases(ps, "e456e25d-c34c-4375-ad3c-f19ef011eafa", 5.49, 3.0, LocalDate.of(2025, 5, 14));
            UNCaddPurchases(ps, "400f961e-77a1-46c4-8dbd-24703afc9b96", 6.49, 1.0, LocalDate.of(2025, 5, 14));
            UNCaddPurchases(ps, "f133b129-f19c-4864-9fa5-da4a2ea6841c", 3.49, 3.0, LocalDate.of(2025, 5, 14));
            UNCaddPurchases(ps, "6d00f111-d395-431d-984b-1b25f150f64e", 2.99, 3.0, LocalDate.of(2025, 5, 14));
            UNCaddPurchases(ps, "591db857-9027-4214-8d1b-7e6dc559aea0", 12.49, 1.0, LocalDate.of(2025, 5, 17));
            UNCaddPurchases(ps, "e72178ad-de05-483e-a564-b036785b970c", 3.99, 2.0, LocalDate.of(2025, 5, 17));
            UNCaddPurchases(ps, "dd1caa5e-63b7-4e8d-b784-3451cdd7384b", 4.49, 2.0, LocalDate.of(2025, 5, 17));
            UNCaddPurchases(ps, "18a9d99b-2c9c-43de-ad6c-685c4f4d4980", 5.49, 2.0, LocalDate.of(2025, 5, 17));
            UNCaddPurchases(ps, "4970b5c8-82d2-4a27-8d08-749541a704d4", 8.00, 1.0, LocalDate.of(2025, 5, 17));
            UNCaddPurchases(ps, "dd1caa5e-63b7-4e8d-b784-3451cdd7384b", 4.49, 1.0, LocalDate.of(2025, 5, 21));
            UNCaddPurchases(ps, "3717698b-ba54-4db1-87c1-8404e1c878a8", 17.99, 1.0, LocalDate.of(2025, 5, 21));
            UNCaddPurchases(ps, "ec307717-0725-4560-bc90-b21b815c9a1c", 9.49, 2.0, LocalDate.of(2025, 5, 21));
            UNCaddPurchases(ps, "628ed9f6-3cae-4bc3-afc6-e396a90ff09d", 15.49, 2.0, LocalDate.of(2025, 5, 21));
            UNCaddPurchases(ps, "43123693-3c38-44bd-a3e9-1a6cb9d08b75", 4.49, 2.0, LocalDate.of(2025, 5, 21));
            UNCaddPurchases(ps, "43123693-3c38-44bd-a3e9-1a6cb9d08b75", 4.99, 2.0, LocalDate.of(2025, 5, 25));
            UNCaddPurchases(ps, "e456e25d-c34c-4375-ad3c-f19ef011eafa", 4.99, 2.0, LocalDate.of(2025, 5, 25));
            UNCaddPurchases(ps, "76cb1958-00f9-4ee8-8a57-ed1bbad8e8d5", 4.00, 3.0, LocalDate.of(2025, 5, 25));
            UNCaddPurchases(ps, "87eee6d3-cb6c-4666-97b0-350ff102f0c4", 7.49, 2.0, LocalDate.of(2025, 5, 25));
            UNCaddPurchases(ps, "55656e11-7e0a-4e33-87ce-749c8f20ab62", 9.99, 1.0, LocalDate.of(2025, 5, 25));
            UNCaddPurchases(ps, "400f961e-77a1-46c4-8dbd-24703afc9b96", 5.99, 3.0, LocalDate.of(2025, 5, 25));
            UNCaddPurchases(ps, "060189f1-92e0-48c0-8cb7-e960cf135e42", 3.00, 3.0, LocalDate.of(2025, 5, 25));
            UNCaddPurchases(ps, "dd1caa5e-63b7-4e8d-b784-3451cdd7384b", 4.99, 2.0, LocalDate.of(2025, 5, 25));
            UNCaddPurchases(ps, "18a9d99b-2c9c-43de-ad6c-685c4f4d4980", 6.00, 2.0, LocalDate.of(2025, 5, 25));
            UNCaddPurchases(ps, "fcd3c43e-b832-466e-96d2-80a7c8b675fb", 13.99, 1.0, LocalDate.of(2025, 5, 25));
            UNCaddPurchases(ps, "e72178ad-de05-483e-a564-b036785b970c", 3.49, 3.0, LocalDate.of(2025, 5, 25));
            UNCaddPurchases(ps, "591db857-9027-4214-8d1b-7e6dc559aea0", 12.00, 1.0, LocalDate.of(2025, 5, 25));
            UNCaddPurchases(ps, "4031b990-016c-47f9-8501-b5c83ae860c1", 2.49, 1.0, LocalDate.of(2025, 5, 25));
            UNCaddPurchases(ps, "b7cde652-ea9a-43d3-bfeb-774e68d384c5", 15.49, 2.0, LocalDate.of(2025, 5, 25));
            UNCaddPurchases(ps, "e456e25d-c34c-4375-ad3c-f19ef011eafa", 5.49, 1.0, LocalDate.of(2025, 5, 30));
            UNCaddPurchases(ps, "628ed9f6-3cae-4bc3-afc6-e396a90ff09d", 16.99, 1.0, LocalDate.of(2025, 5, 30));
            UNCaddPurchases(ps, "f133b129-f19c-4864-9fa5-da4a2ea6841c", 3.49, 3.0, LocalDate.of(2025, 5, 30));
            UNCaddPurchases(ps, "103cb413-23b5-44dd-b036-49c6bb71cb70", 3.49, 3.0, LocalDate.of(2025, 5, 30));
            UNCaddPurchases(ps, "6a0e0b76-0c67-4960-a639-0492e23fb8c1", 7.00, 3.0, LocalDate.of(2025, 5, 30));
            UNCaddPurchases(ps, "fcd3c43e-b832-466e-96d2-80a7c8b675fb", 14.49, 3.0, LocalDate.of(2025, 5, 30));
            UNCaddPurchases(ps, "76cb1958-00f9-4ee8-8a57-ed1bbad8e8d5", 4.49, 2.0, LocalDate.of(2025, 5, 30));
            UNCaddPurchases(ps, "18a9d99b-2c9c-43de-ad6c-685c4f4d4980", 6.00, 3.0, LocalDate.of(2025, 5, 30));
            UNCaddPurchases(ps, "ec307717-0725-4560-bc90-b21b815c9a1c", 10.99, 1.0, LocalDate.of(2025, 5, 30));
            UNCaddPurchases(ps, "060189f1-92e0-48c0-8cb7-e960cf135e42", 3.00, 1.0, LocalDate.of(2025, 5, 30));
            UNCaddPurchases(ps, "400f961e-77a1-46c4-8dbd-24703afc9b96", 6.49, 1.0, LocalDate.of(2025, 5, 30));
            UNCaddPurchases(ps, "55656e11-7e0a-4e33-87ce-749c8f20ab62", 9.00, 1.0, LocalDate.of(2025, 5, 30));
            UNCaddPurchases(ps, "87eee6d3-cb6c-4666-97b0-350ff102f0c4", 6.99, 1.0, LocalDate.of(2025, 5, 30));
            UNCaddPurchases(ps, "e72178ad-de05-483e-a564-b036785b970c", 3.49, 3.0, LocalDate.of(2025, 5, 30));
            UNCaddPurchases(ps, "6a0e0b76-0c67-4960-a639-0492e23fb8c1", 6.49, 3.0, LocalDate.of(2025, 6, 2));
            UNCaddPurchases(ps, "3717698b-ba54-4db1-87c1-8404e1c878a8", 17.00, 2.0, LocalDate.of(2025, 6, 2));
            UNCaddPurchases(ps, "95be1867-3551-431f-80b9-40f811a50e18", 3.00, 2.0, LocalDate.of(2025, 6, 2));
            UNCaddPurchases(ps, "e72178ad-de05-483e-a564-b036785b970c", 3.49, 2.0, LocalDate.of(2025, 6, 2));
            UNCaddPurchases(ps, "628ed9f6-3cae-4bc3-afc6-e396a90ff09d", 14.00, 2.0, LocalDate.of(2025, 6, 2));
            UNCaddPurchases(ps, "18a9d99b-2c9c-43de-ad6c-685c4f4d4980", 5.49, 3.0, LocalDate.of(2025, 6, 2));
            UNCaddPurchases(ps, "400f961e-77a1-46c4-8dbd-24703afc9b96", 6.00, 1.0, LocalDate.of(2025, 6, 2));
            UNCaddPurchases(ps, "e456e25d-c34c-4375-ad3c-f19ef011eafa", 4.99, 1.0, LocalDate.of(2025, 6, 2));
            UNCaddPurchases(ps, "dd1caa5e-63b7-4e8d-b784-3451cdd7384b", 4.49, 2.0, LocalDate.of(2025, 6, 2));
            UNCaddPurchases(ps, "ec307717-0725-4560-bc90-b21b815c9a1c", 11.00, 1.0, LocalDate.of(2025, 6, 2));
            UNCaddPurchases(ps, "103cb413-23b5-44dd-b036-49c6bb71cb70", 3.49, 2.0, LocalDate.of(2025, 6, 2));
            UNCaddPurchases(ps, "87eee6d3-cb6c-4666-97b0-350ff102f0c4", 7.00, 2.0, LocalDate.of(2025, 6, 2));
            UNCaddPurchases(ps, "f133b129-f19c-4864-9fa5-da4a2ea6841c", 4.00, 1.0, LocalDate.of(2025, 6, 2));
            UNCaddPurchases(ps, "4031b990-016c-47f9-8501-b5c83ae860c1", 2.49, 2.0, LocalDate.of(2025, 6, 2));
            UNCaddPurchases(ps, "6672a355-bde4-4b98-8475-c7319beff67d", 13.49, 1.0, LocalDate.of(2025, 6, 2));
            UNCaddPurchases(ps, "a8beb54f-fade-4bec-b5f7-cbd7d248b188", 9.49, 1.0, LocalDate.of(2025, 6, 2));
            UNCaddPurchases(ps, "b7cde652-ea9a-43d3-bfeb-774e68d384c5", 13.99, 3.0, LocalDate.of(2025, 6, 6));
            UNCaddPurchases(ps, "f133b129-f19c-4864-9fa5-da4a2ea6841c", 4.00, 3.0, LocalDate.of(2025, 6, 6));
            UNCaddPurchases(ps, "43123693-3c38-44bd-a3e9-1a6cb9d08b75", 4.49, 2.0, LocalDate.of(2025, 6, 6));
            UNCaddPurchases(ps, "3717698b-ba54-4db1-87c1-8404e1c878a8", 17.99, 3.0, LocalDate.of(2025, 6, 6));
            UNCaddPurchases(ps, "e456e25d-c34c-4375-ad3c-f19ef011eafa", 5.49, 2.0, LocalDate.of(2025, 6, 6));
            UNCaddPurchases(ps, "18a9d99b-2c9c-43de-ad6c-685c4f4d4980", 6.00, 1.0, LocalDate.of(2025, 6, 6));
            UNCaddPurchases(ps, "c05fda44-c11f-476e-be98-d6eadac63a05", 9.00, 1.0, LocalDate.of(2025, 6, 6));
            UNCaddPurchases(ps, "060189f1-92e0-48c0-8cb7-e960cf135e42", 3.00, 2.0, LocalDate.of(2025, 6, 6));
            UNCaddPurchases(ps, "55656e11-7e0a-4e33-87ce-749c8f20ab62", 9.00, 3.0, LocalDate.of(2025, 6, 6));
            UNCaddPurchases(ps, "591db857-9027-4214-8d1b-7e6dc559aea0", 11.49, 1.0, LocalDate.of(2025, 6, 6));
            UNCaddPurchases(ps, "dd1caa5e-63b7-4e8d-b784-3451cdd7384b", 4.49, 3.0, LocalDate.of(2025, 6, 6));
            UNCaddPurchases(ps, "87eee6d3-cb6c-4666-97b0-350ff102f0c4", 6.49, 2.0, LocalDate.of(2025, 6, 9));
            UNCaddPurchases(ps, "3717698b-ba54-4db1-87c1-8404e1c878a8", 17.49, 3.0, LocalDate.of(2025, 6, 9));
            UNCaddPurchases(ps, "6d00f111-d395-431d-984b-1b25f150f64e", 3.00, 2.0, LocalDate.of(2025, 6, 9));
            UNCaddPurchases(ps, "f133b129-f19c-4864-9fa5-da4a2ea6841c", 3.49, 3.0, LocalDate.of(2025, 6, 9));
            UNCaddPurchases(ps, "55656e11-7e0a-4e33-87ce-749c8f20ab62", 9.99, 3.0, LocalDate.of(2025, 6, 9));
            UNCaddPurchases(ps, "76cb1958-00f9-4ee8-8a57-ed1bbad8e8d5", 4.49, 2.0, LocalDate.of(2025, 6, 9));
            UNCaddPurchases(ps, "bc41cce8-c598-449a-9051-563591c2b1c1", 3.49, 1.0, LocalDate.of(2025, 6, 9));
            UNCaddPurchases(ps, "ec307717-0725-4560-bc90-b21b815c9a1c", 10.49, 2.0, LocalDate.of(2025, 6, 9));
            UNCaddPurchases(ps, "c05fda44-c11f-476e-be98-d6eadac63a05", 8.49, 1.0, LocalDate.of(2025, 6, 9));
            UNCaddPurchases(ps, "95be1867-3551-431f-80b9-40f811a50e18", 3.00, 3.0, LocalDate.of(2025, 6, 9));
            UNCaddPurchases(ps, "fcd3c43e-b832-466e-96d2-80a7c8b675fb", 14.00, 2.0, LocalDate.of(2025, 6, 13));
            UNCaddPurchases(ps, "103cb413-23b5-44dd-b036-49c6bb71cb70", 3.49, 2.0, LocalDate.of(2025, 6, 13));
            UNCaddPurchases(ps, "e72178ad-de05-483e-a564-b036785b970c", 3.49, 1.0, LocalDate.of(2025, 6, 13));
            UNCaddPurchases(ps, "ec307717-0725-4560-bc90-b21b815c9a1c", 10.00, 3.0, LocalDate.of(2025, 6, 13));
            UNCaddPurchases(ps, "c05fda44-c11f-476e-be98-d6eadac63a05", 8.99, 1.0, LocalDate.of(2025, 6, 13));
            UNCaddPurchases(ps, "bc41cce8-c598-449a-9051-563591c2b1c1", 3.49, 3.0, LocalDate.of(2025, 6, 18));
            UNCaddPurchases(ps, "18a9d99b-2c9c-43de-ad6c-685c4f4d4980", 5.49, 2.0, LocalDate.of(2025, 6, 18));
            UNCaddPurchases(ps, "3717698b-ba54-4db1-87c1-8404e1c878a8", 18.00, 1.0, LocalDate.of(2025, 6, 18));
            UNCaddPurchases(ps, "fcd3c43e-b832-466e-96d2-80a7c8b675fb", 14.99, 2.0, LocalDate.of(2025, 6, 18));
            UNCaddPurchases(ps, "4031b990-016c-47f9-8501-b5c83ae860c1", 2.49, 3.0, LocalDate.of(2025, 6, 18));
            UNCaddPurchases(ps, "103cb413-23b5-44dd-b036-49c6bb71cb70", 3.49, 2.0, LocalDate.of(2025, 6, 18));
            UNCaddPurchases(ps, "060189f1-92e0-48c0-8cb7-e960cf135e42", 3.49, 2.0, LocalDate.of(2025, 6, 18));
            UNCaddPurchases(ps, "f133b129-f19c-4864-9fa5-da4a2ea6841c", 4.00, 3.0, LocalDate.of(2025, 6, 18));
            UNCaddPurchases(ps, "6a0e0b76-0c67-4960-a639-0492e23fb8c1", 6.99, 2.0, LocalDate.of(2025, 6, 18));
            UNCaddPurchases(ps, "e456e25d-c34c-4375-ad3c-f19ef011eafa", 5.49, 1.0, LocalDate.of(2025, 6, 18));
            UNCaddPurchases(ps, "55656e11-7e0a-4e33-87ce-749c8f20ab62", 8.49, 2.0, LocalDate.of(2025, 6, 18));
            UNCaddPurchases(ps, "591db857-9027-4214-8d1b-7e6dc559aea0", 10.99, 3.0, LocalDate.of(2025, 6, 18));
            UNCaddPurchases(ps, "76cb1958-00f9-4ee8-8a57-ed1bbad8e8d5", 4.49, 1.0, LocalDate.of(2025, 6, 18));
            UNCaddPurchases(ps, "e456e25d-c34c-4375-ad3c-f19ef011eafa", 5.49, 2.0, LocalDate.of(2025, 6, 22));
            UNCaddPurchases(ps, "c05fda44-c11f-476e-be98-d6eadac63a05", 7.49, 1.0, LocalDate.of(2025, 6, 22));
            UNCaddPurchases(ps, "87eee6d3-cb6c-4666-97b0-350ff102f0c4", 6.49, 3.0, LocalDate.of(2025, 6, 22));
            UNCaddPurchases(ps, "bc41cce8-c598-449a-9051-563591c2b1c1", 3.49, 2.0, LocalDate.of(2025, 6, 22));
            UNCaddPurchases(ps, "55656e11-7e0a-4e33-87ce-749c8f20ab62", 8.99, 3.0, LocalDate.of(2025, 6, 26));
            UNCaddPurchases(ps, "4031b990-016c-47f9-8501-b5c83ae860c1", 2.49, 3.0, LocalDate.of(2025, 6, 26));
            UNCaddPurchases(ps, "3717698b-ba54-4db1-87c1-8404e1c878a8", 19.00, 2.0, LocalDate.of(2025, 6, 26));
            UNCaddPurchases(ps, "43123693-3c38-44bd-a3e9-1a6cb9d08b75", 5.49, 1.0, LocalDate.of(2025, 6, 26));
            UNCaddPurchases(ps, "060189f1-92e0-48c0-8cb7-e960cf135e42", 3.49, 2.0, LocalDate.of(2025, 7, 1));
            UNCaddPurchases(ps, "bc41cce8-c598-449a-9051-563591c2b1c1", 3.49, 2.0, LocalDate.of(2025, 7, 1));
            UNCaddPurchases(ps, "dd1caa5e-63b7-4e8d-b784-3451cdd7384b", 4.99, 1.0, LocalDate.of(2025, 7, 1));
            UNCaddPurchases(ps, "76cb1958-00f9-4ee8-8a57-ed1bbad8e8d5", 4.00, 3.0, LocalDate.of(2025, 7, 1));
            UNCaddPurchases(ps, "43123693-3c38-44bd-a3e9-1a6cb9d08b75", 4.99, 3.0, LocalDate.of(2025, 7, 1));
            UNCaddPurchases(ps, "fcd3c43e-b832-466e-96d2-80a7c8b675fb", 12.49, 3.0, LocalDate.of(2025, 7, 1));
            UNCaddPurchases(ps, "103cb413-23b5-44dd-b036-49c6bb71cb70", 3.49, 1.0, LocalDate.of(2025, 7, 1));
            UNCaddPurchases(ps, "e456e25d-c34c-4375-ad3c-f19ef011eafa", 5.49, 2.0, LocalDate.of(2025, 7, 1));
            UNCaddPurchases(ps, "6d00f111-d395-431d-984b-1b25f150f64e", 3.00, 2.0, LocalDate.of(2025, 7, 1));
            UNCaddPurchases(ps, "400f961e-77a1-46c4-8dbd-24703afc9b96", 6.49, 2.0, LocalDate.of(2025, 7, 1));
            UNCaddPurchases(ps, "18a9d99b-2c9c-43de-ad6c-685c4f4d4980", 6.00, 2.0, LocalDate.of(2025, 7, 1));
            UNCaddPurchases(ps, "4031b990-016c-47f9-8501-b5c83ae860c1", 2.99, 1.0, LocalDate.of(2025, 7, 1));
            UNCaddPurchases(ps, "683d3699-6bb3-4b08-977e-02a22d07ea51", 11.49, 2.0, LocalDate.of(2025, 7, 1));
            UNCaddPurchases(ps, "f841e912-c7e3-4fb0-950f-bdb149681b7e", 4.99, 1.0, LocalDate.of(2025, 7, 1));
            UNCaddPurchases(ps, "e456e25d-c34c-4375-ad3c-f19ef011eafa", 5.00, 1.0, LocalDate.of(2025, 7, 4));
            UNCaddPurchases(ps, "4031b990-016c-47f9-8501-b5c83ae860c1", 2.49, 2.0, LocalDate.of(2025, 7, 4));
            UNCaddPurchases(ps, "c05fda44-c11f-476e-be98-d6eadac63a05", 7.49, 3.0, LocalDate.of(2025, 7, 4));
            UNCaddPurchases(ps, "fcd3c43e-b832-466e-96d2-80a7c8b675fb", 12.99, 1.0, LocalDate.of(2025, 7, 8));
            UNCaddPurchases(ps, "55656e11-7e0a-4e33-87ce-749c8f20ab62", 10.00, 3.0, LocalDate.of(2025, 7, 8));
            UNCaddPurchases(ps, "4031b990-016c-47f9-8501-b5c83ae860c1", 2.49, 1.0, LocalDate.of(2025, 7, 8));
            UNCaddPurchases(ps, "628ed9f6-3cae-4bc3-afc6-e396a90ff09d", 16.99, 3.0, LocalDate.of(2025, 7, 8));
            UNCaddPurchases(ps, "dd1caa5e-63b7-4e8d-b784-3451cdd7384b", 5.00, 3.0, LocalDate.of(2025, 7, 8));
            UNCaddPurchases(ps, "b7cde652-ea9a-43d3-bfeb-774e68d384c5", 15.00, 1.0, LocalDate.of(2025, 7, 8));
            UNCaddPurchases(ps, "6d00f111-d395-431d-984b-1b25f150f64e", 2.99, 2.0, LocalDate.of(2025, 7, 8));
            UNCaddPurchases(ps, "e72178ad-de05-483e-a564-b036785b970c", 3.49, 2.0, LocalDate.of(2025, 7, 8));
            UNCaddPurchases(ps, "6a0e0b76-0c67-4960-a639-0492e23fb8c1", 6.00, 3.0, LocalDate.of(2025, 7, 8));
            UNCaddPurchases(ps, "f133b129-f19c-4864-9fa5-da4a2ea6841c", 3.99, 1.0, LocalDate.of(2025, 7, 8));
            UNCaddPurchases(ps, "060189f1-92e0-48c0-8cb7-e960cf135e42", 3.99, 3.0, LocalDate.of(2025, 7, 8));
            UNCaddPurchases(ps, "3717698b-ba54-4db1-87c1-8404e1c878a8", 18.99, 2.0, LocalDate.of(2025, 7, 8));
            UNCaddPurchases(ps, "591db857-9027-4214-8d1b-7e6dc559aea0", 12.00, 3.0, LocalDate.of(2025, 7, 8));
            UNCaddPurchases(ps, "ec307717-0725-4560-bc90-b21b815c9a1c", 10.49, 2.0, LocalDate.of(2025, 7, 8));
            UNCaddPurchases(ps, "0338eeba-4018-4314-b616-384b456e525d", 3.49, 2.0, LocalDate.of(2025, 7, 8));
            UNCaddPurchases(ps, "4031b990-016c-47f9-8501-b5c83ae860c1", 2.49, 2.0, LocalDate.of(2025, 7, 12));
            UNCaddPurchases(ps, "95be1867-3551-431f-80b9-40f811a50e18", 3.49, 3.0, LocalDate.of(2025, 7, 12));
            UNCaddPurchases(ps, "fcd3c43e-b832-466e-96d2-80a7c8b675fb", 12.49, 2.0, LocalDate.of(2025, 7, 12));
            UNCaddPurchases(ps, "e72178ad-de05-483e-a564-b036785b970c", 3.49, 1.0, LocalDate.of(2025, 7, 12));
            UNCaddPurchases(ps, "87eee6d3-cb6c-4666-97b0-350ff102f0c4", 6.49, 3.0, LocalDate.of(2025, 7, 12));
            UNCaddPurchases(ps, "f133b129-f19c-4864-9fa5-da4a2ea6841c", 3.49, 1.0, LocalDate.of(2025, 7, 12));
            UNCaddPurchases(ps, "76cb1958-00f9-4ee8-8a57-ed1bbad8e8d5", 4.49, 3.0, LocalDate.of(2025, 7, 12));
            UNCaddPurchases(ps, "bc41cce8-c598-449a-9051-563591c2b1c1", 3.99, 2.0, LocalDate.of(2025, 7, 12));
            UNCaddPurchases(ps, "591db857-9027-4214-8d1b-7e6dc559aea0", 12.49, 2.0, LocalDate.of(2025, 7, 12));
            UNCaddPurchases(ps, "c05fda44-c11f-476e-be98-d6eadac63a05", 7.99, 1.0, LocalDate.of(2025, 7, 12));
            UNCaddPurchases(ps, "b7cde652-ea9a-43d3-bfeb-774e68d384c5", 13.49, 1.0, LocalDate.of(2025, 7, 12));
            UNCaddPurchases(ps, "060189f1-92e0-48c0-8cb7-e960cf135e42", 3.49, 1.0, LocalDate.of(2025, 7, 12));
            UNCaddPurchases(ps, "6a0e0b76-0c67-4960-a639-0492e23fb8c1", 7.49, 1.0, LocalDate.of(2025, 7, 12));
            UNCaddPurchases(ps, "55656e11-7e0a-4e33-87ce-749c8f20ab62", 9.49, 2.0, LocalDate.of(2025, 7, 12));
            UNCaddPurchases(ps, "f133b129-f19c-4864-9fa5-da4a2ea6841c", 4.00, 3.0, LocalDate.of(2025, 7, 15));
            UNCaddPurchases(ps, "400f961e-77a1-46c4-8dbd-24703afc9b96", 6.49, 3.0, LocalDate.of(2025, 7, 15));
            UNCaddPurchases(ps, "bc41cce8-c598-449a-9051-563591c2b1c1", 3.99, 2.0, LocalDate.of(2025, 7, 15));
            UNCaddPurchases(ps, "4031b990-016c-47f9-8501-b5c83ae860c1", 2.99, 2.0, LocalDate.of(2025, 7, 15));
            UNCaddPurchases(ps, "bc41cce8-c598-449a-9051-563591c2b1c1", 3.49, 2.0, LocalDate.of(2025, 7, 19));
            UNCaddPurchases(ps, "4031b990-016c-47f9-8501-b5c83ae860c1", 2.49, 1.0, LocalDate.of(2025, 7, 19));
            UNCaddPurchases(ps, "87eee6d3-cb6c-4666-97b0-350ff102f0c4", 6.99, 3.0, LocalDate.of(2025, 7, 19));
            UNCaddPurchases(ps, "e456e25d-c34c-4375-ad3c-f19ef011eafa", 4.49, 1.0, LocalDate.of(2025, 7, 19));
            UNCaddPurchases(ps, "c05fda44-c11f-476e-be98-d6eadac63a05", 7.49, 2.0, LocalDate.of(2025, 7, 19));
            UNCaddPurchases(ps, "f133b129-f19c-4864-9fa5-da4a2ea6841c", 3.99, 3.0, LocalDate.of(2025, 7, 19));
            UNCaddPurchases(ps, "18a9d99b-2c9c-43de-ad6c-685c4f4d4980", 5.49, 3.0, LocalDate.of(2025, 7, 22));
            UNCaddPurchases(ps, "b7cde652-ea9a-43d3-bfeb-774e68d384c5", 14.49, 2.0, LocalDate.of(2025, 7, 22));
            UNCaddPurchases(ps, "95be1867-3551-431f-80b9-40f811a50e18", 2.99, 2.0, LocalDate.of(2025, 7, 22));
            UNCaddPurchases(ps, "87eee6d3-cb6c-4666-97b0-350ff102f0c4", 6.99, 2.0, LocalDate.of(2025, 7, 22));
            UNCaddPurchases(ps, "103cb413-23b5-44dd-b036-49c6bb71cb70", 3.49, 2.0, LocalDate.of(2025, 7, 22));
            UNCaddPurchases(ps, "591db857-9027-4214-8d1b-7e6dc559aea0", 11.00, 3.0, LocalDate.of(2025, 7, 22));
            UNCaddPurchases(ps, "95be1867-3551-431f-80b9-40f811a50e18", 3.00, 2.0, LocalDate.of(2025, 7, 26));
            UNCaddPurchases(ps, "f133b129-f19c-4864-9fa5-da4a2ea6841c", 3.99, 3.0, LocalDate.of(2025, 7, 26));
            UNCaddPurchases(ps, "fcd3c43e-b832-466e-96d2-80a7c8b675fb", 13.49, 1.0, LocalDate.of(2025, 7, 26));
            UNCaddPurchases(ps, "76cb1958-00f9-4ee8-8a57-ed1bbad8e8d5", 4.49, 2.0, LocalDate.of(2025, 7, 26));
            UNCaddPurchases(ps, "b7cde652-ea9a-43d3-bfeb-774e68d384c5", 14.99, 1.0, LocalDate.of(2025, 7, 26));
            UNCaddPurchases(ps, "e72178ad-de05-483e-a564-b036785b970c", 3.49, 2.0, LocalDate.of(2025, 7, 30));
            UNCaddPurchases(ps, "55656e11-7e0a-4e33-87ce-749c8f20ab62", 9.99, 3.0, LocalDate.of(2025, 7, 30));
            UNCaddPurchases(ps, "bc41cce8-c598-449a-9051-563591c2b1c1", 3.99, 1.0, LocalDate.of(2025, 7, 30));
            UNCaddPurchases(ps, "628ed9f6-3cae-4bc3-afc6-e396a90ff09d", 14.00, 3.0, LocalDate.of(2025, 7, 30));
            UNCaddPurchases(ps, "55656e11-7e0a-4e33-87ce-749c8f20ab62", 9.00, 3.0, LocalDate.of(2025, 8, 2));
            UNCaddPurchases(ps, "fcd3c43e-b832-466e-96d2-80a7c8b675fb", 14.49, 3.0, LocalDate.of(2025, 8, 2));
            UNCaddPurchases(ps, "ec307717-0725-4560-bc90-b21b815c9a1c", 9.99, 2.0, LocalDate.of(2025, 8, 2));
            UNCaddPurchases(ps, "c05fda44-c11f-476e-be98-d6eadac63a05", 8.49, 2.0, LocalDate.of(2025, 8, 2));
            UNCaddPurchases(ps, "95be1867-3551-431f-80b9-40f811a50e18", 3.00, 2.0, LocalDate.of(2025, 8, 2));
            UNCaddPurchases(ps, "e72178ad-de05-483e-a564-b036785b970c", 3.49, 1.0, LocalDate.of(2025, 8, 2));
            UNCaddPurchases(ps, "18a9d99b-2c9c-43de-ad6c-685c4f4d4980", 6.00, 1.0, LocalDate.of(2025, 8, 2));
            UNCaddPurchases(ps, "628ed9f6-3cae-4bc3-afc6-e396a90ff09d", 15.00, 1.0, LocalDate.of(2025, 8, 2));
            UNCaddPurchases(ps, "400f961e-77a1-46c4-8dbd-24703afc9b96", 6.00, 3.0, LocalDate.of(2025, 8, 2));
            UNCaddPurchases(ps, "060189f1-92e0-48c0-8cb7-e960cf135e42", 3.49, 1.0, LocalDate.of(2025, 8, 2));
            UNCaddPurchases(ps, "6a0e0b76-0c67-4960-a639-0492e23fb8c1", 6.49, 1.0, LocalDate.of(2025, 8, 2));
            UNCaddPurchases(ps, "591db857-9027-4214-8d1b-7e6dc559aea0", 13.00, 2.0, LocalDate.of(2025, 8, 2));
            UNCaddPurchases(ps, "b7cde652-ea9a-43d3-bfeb-774e68d384c5", 14.00, 2.0, LocalDate.of(2025, 8, 2));
            UNCaddPurchases(ps, "87eee6d3-cb6c-4666-97b0-350ff102f0c4", 6.49, 2.0, LocalDate.of(2025, 8, 2));
            UNCaddPurchases(ps, "6672a355-bde4-4b98-8475-c7319beff67d", 12.00, 1.0, LocalDate.of(2025, 8, 2));
            UNCaddPurchases(ps, "f133b129-f19c-4864-9fa5-da4a2ea6841c", 3.99, 3.0, LocalDate.of(2025, 8, 5));
            UNCaddPurchases(ps, "6a0e0b76-0c67-4960-a639-0492e23fb8c1", 6.99, 1.0, LocalDate.of(2025, 8, 5));
            UNCaddPurchases(ps, "628ed9f6-3cae-4bc3-afc6-e396a90ff09d", 16.49, 2.0, LocalDate.of(2025, 8, 5));
            UNCaddPurchases(ps, "43123693-3c38-44bd-a3e9-1a6cb9d08b75", 5.49, 2.0, LocalDate.of(2025, 8, 5));
            UNCaddPurchases(ps, "76cb1958-00f9-4ee8-8a57-ed1bbad8e8d5", 3.99, 3.0, LocalDate.of(2025, 8, 5));
            UNCaddPurchases(ps, "33986208-d2c6-4cd0-b88b-a671579bdcf0", 15.99, 1.0, LocalDate.of(2025, 8, 5));
            UNCaddPurchases(ps, "a8beb54f-fade-4bec-b5f7-cbd7d248b188", 10.00, 1.0, LocalDate.of(2025, 8, 5));
            UNCaddPurchases(ps, "18a9d99b-2c9c-43de-ad6c-685c4f4d4980", 5.99, 3.0, LocalDate.of(2025, 8, 9));
            UNCaddPurchases(ps, "55656e11-7e0a-4e33-87ce-749c8f20ab62", 10.00, 2.0, LocalDate.of(2025, 8, 9));
            UNCaddPurchases(ps, "4031b990-016c-47f9-8501-b5c83ae860c1", 2.99, 2.0, LocalDate.of(2025, 8, 9));
            UNCaddPurchases(ps, "43123693-3c38-44bd-a3e9-1a6cb9d08b75", 4.49, 1.0, LocalDate.of(2025, 8, 9));
            UNCaddPurchases(ps, "dd1caa5e-63b7-4e8d-b784-3451cdd7384b", 4.49, 3.0, LocalDate.of(2025, 8, 9));
            UNCaddPurchases(ps, "ec307717-0725-4560-bc90-b21b815c9a1c", 11.00, 3.0, LocalDate.of(2025, 8, 9));
            UNCaddPurchases(ps, "a8f8b02e-cd64-4c02-9cfb-4df9c19b5e30", 6.49, 1.0, LocalDate.of(2025, 8, 9));
            UNCaddPurchases(ps, "0338eeba-4018-4314-b616-384b456e525d", 3.49, 2.0, LocalDate.of(2025, 8, 9));
            UNCaddPurchases(ps, "8118d42b-a447-4a6e-bdc9-70cd70d5c251", 9.49, 2.0, LocalDate.of(2025, 8, 9));
            UNCaddPurchases(ps, "bc41cce8-c598-449a-9051-563591c2b1c1", 3.99, 1.0, LocalDate.of(2025, 8, 13));
            UNCaddPurchases(ps, "e72178ad-de05-483e-a564-b036785b970c", 3.49, 1.0, LocalDate.of(2025, 8, 13));
            UNCaddPurchases(ps, "fcd3c43e-b832-466e-96d2-80a7c8b675fb", 13.99, 1.0, LocalDate.of(2025, 8, 13));
            UNCaddPurchases(ps, "ec307717-0725-4560-bc90-b21b815c9a1c", 10.00, 1.0, LocalDate.of(2025, 8, 13));
            UNCaddPurchases(ps, "6d00f111-d395-431d-984b-1b25f150f64e", 3.49, 2.0, LocalDate.of(2025, 8, 13));
            UNCaddPurchases(ps, "c05fda44-c11f-476e-be98-d6eadac63a05", 8.00, 3.0, LocalDate.of(2025, 8, 13));
            UNCaddPurchases(ps, "3717698b-ba54-4db1-87c1-8404e1c878a8", 19.00, 2.0, LocalDate.of(2025, 8, 18));
            UNCaddPurchases(ps, "400f961e-77a1-46c4-8dbd-24703afc9b96", 6.49, 2.0, LocalDate.of(2025, 8, 18));
            UNCaddPurchases(ps, "060189f1-92e0-48c0-8cb7-e960cf135e42", 3.99, 3.0, LocalDate.of(2025, 8, 18));
            UNCaddPurchases(ps, "55656e11-7e0a-4e33-87ce-749c8f20ab62", 9.00, 2.0, LocalDate.of(2025, 8, 18));
            UNCaddPurchases(ps, "6d00f111-d395-431d-984b-1b25f150f64e", 3.00, 2.0, LocalDate.of(2025, 8, 18));
            UNCaddPurchases(ps, "e456e25d-c34c-4375-ad3c-f19ef011eafa", 5.00, 3.0, LocalDate.of(2025, 8, 23));
            UNCaddPurchases(ps, "c05fda44-c11f-476e-be98-d6eadac63a05", 7.99, 1.0, LocalDate.of(2025, 8, 23));
            UNCaddPurchases(ps, "103cb413-23b5-44dd-b036-49c6bb71cb70", 3.49, 1.0, LocalDate.of(2025, 8, 23));
            UNCaddPurchases(ps, "6d00f111-d395-431d-984b-1b25f150f64e", 2.99, 1.0, LocalDate.of(2025, 8, 23));
            UNCaddPurchases(ps, "55656e11-7e0a-4e33-87ce-749c8f20ab62", 9.49, 3.0, LocalDate.of(2025, 8, 23));
            UNCaddPurchases(ps, "b7cde652-ea9a-43d3-bfeb-774e68d384c5", 15.49, 2.0, LocalDate.of(2025, 8, 23));
            UNCaddPurchases(ps, "628ed9f6-3cae-4bc3-afc6-e396a90ff09d", 16.49, 1.0, LocalDate.of(2025, 8, 23));
            UNCaddPurchases(ps, "18a9d99b-2c9c-43de-ad6c-685c4f4d4980", 6.49, 3.0, LocalDate.of(2025, 8, 23));
            UNCaddPurchases(ps, "bc41cce8-c598-449a-9051-563591c2b1c1", 3.49, 1.0, LocalDate.of(2025, 8, 23));
            UNCaddPurchases(ps, "76cb1958-00f9-4ee8-8a57-ed1bbad8e8d5", 4.00, 1.0, LocalDate.of(2025, 8, 23));
            UNCaddPurchases(ps, "6a0e0b76-0c67-4960-a639-0492e23fb8c1", 7.49, 2.0, LocalDate.of(2025, 8, 23));
            UNCaddPurchases(ps, "dd1caa5e-63b7-4e8d-b784-3451cdd7384b", 4.49, 1.0, LocalDate.of(2025, 8, 23));
            UNCaddPurchases(ps, "060189f1-92e0-48c0-8cb7-e960cf135e42", 3.49, 1.0, LocalDate.of(2025, 8, 23));
            UNCaddPurchases(ps, "43123693-3c38-44bd-a3e9-1a6cb9d08b75", 5.00, 2.0, LocalDate.of(2025, 8, 23));
            UNCaddPurchases(ps, "103cb413-23b5-44dd-b036-49c6bb71cb70", 3.00, 3.0, LocalDate.of(2025, 8, 28));
            UNCaddPurchases(ps, "e72178ad-de05-483e-a564-b036785b970c", 3.99, 3.0, LocalDate.of(2025, 8, 28));
            UNCaddPurchases(ps, "400f961e-77a1-46c4-8dbd-24703afc9b96", 6.99, 3.0, LocalDate.of(2025, 8, 28));
            UNCaddPurchases(ps, "55656e11-7e0a-4e33-87ce-749c8f20ab62", 10.00, 1.0, LocalDate.of(2025, 8, 28));
            UNCaddPurchases(ps, "6a0e0b76-0c67-4960-a639-0492e23fb8c1", 6.49, 2.0, LocalDate.of(2025, 8, 28));
            UNCaddPurchases(ps, "87eee6d3-cb6c-4666-97b0-350ff102f0c4", 7.00, 1.0, LocalDate.of(2025, 8, 28));
            UNCaddPurchases(ps, "c05fda44-c11f-476e-be98-d6eadac63a05", 8.00, 2.0, LocalDate.of(2025, 8, 28));
            UNCaddPurchases(ps, "18a9d99b-2c9c-43de-ad6c-685c4f4d4980", 6.00, 2.0, LocalDate.of(2025, 8, 28));
            UNCaddPurchases(ps, "4031b990-016c-47f9-8501-b5c83ae860c1", 2.49, 1.0, LocalDate.of(2025, 8, 28));
            UNCaddPurchases(ps, "fcd3c43e-b832-466e-96d2-80a7c8b675fb", 13.99, 3.0, LocalDate.of(2025, 8, 28));
            UNCaddPurchases(ps, "dd1caa5e-63b7-4e8d-b784-3451cdd7384b", 4.49, 2.0, LocalDate.of(2025, 8, 28));
            UNCaddPurchases(ps, "43123693-3c38-44bd-a3e9-1a6cb9d08b75", 4.99, 1.0, LocalDate.of(2025, 8, 28));
            UNCaddPurchases(ps, "76cb1958-00f9-4ee8-8a57-ed1bbad8e8d5", 3.99, 1.0, LocalDate.of(2025, 8, 28));
            UNCaddPurchases(ps, "591db857-9027-4214-8d1b-7e6dc559aea0", 11.99, 1.0, LocalDate.of(2025, 8, 28));
            UNCaddPurchases(ps, "683d3699-6bb3-4b08-977e-02a22d07ea51", 11.00, 2.0, LocalDate.of(2025, 8, 28));
            UNCaddPurchases(ps, "400f961e-77a1-46c4-8dbd-24703afc9b96", 6.49, 1.0, LocalDate.of(2025, 8, 31));
            UNCaddPurchases(ps, "18a9d99b-2c9c-43de-ad6c-685c4f4d4980", 5.49, 2.0, LocalDate.of(2025, 8, 31));
            UNCaddPurchases(ps, "060189f1-92e0-48c0-8cb7-e960cf135e42", 3.49, 2.0, LocalDate.of(2025, 8, 31));
            UNCaddPurchases(ps, "e72178ad-de05-483e-a564-b036785b970c", 3.49, 2.0, LocalDate.of(2025, 8, 31));
            UNCaddPurchases(ps, "4031b990-016c-47f9-8501-b5c83ae860c1", 2.49, 3.0, LocalDate.of(2025, 8, 31));
            UNCaddPurchases(ps, "b7cde652-ea9a-43d3-bfeb-774e68d384c5", 15.00, 3.0, LocalDate.of(2025, 9, 4));
            UNCaddPurchases(ps, "103cb413-23b5-44dd-b036-49c6bb71cb70", 3.49, 2.0, LocalDate.of(2025, 9, 4));
            UNCaddPurchases(ps, "dd1caa5e-63b7-4e8d-b784-3451cdd7384b", 5.00, 2.0, LocalDate.of(2025, 9, 4));
            UNCaddPurchases(ps, "591db857-9027-4214-8d1b-7e6dc559aea0", 12.00, 3.0, LocalDate.of(2025, 9, 4));
            UNCaddPurchases(ps, "fcd3c43e-b832-466e-96d2-80a7c8b675fb", 13.99, 3.0, LocalDate.of(2025, 9, 8));
            UNCaddPurchases(ps, "18a9d99b-2c9c-43de-ad6c-685c4f4d4980", 5.99, 2.0, LocalDate.of(2025, 9, 8));
            UNCaddPurchases(ps, "f133b129-f19c-4864-9fa5-da4a2ea6841c", 4.49, 1.0, LocalDate.of(2025, 9, 8));
            UNCaddPurchases(ps, "355e0e78-f62d-4d7e-b0c3-98092de1b2a9", 10.49, 2.0, LocalDate.of(2025, 9, 8));
            UNCaddPurchases(ps, "1b3b034d-2437-46df-a6df-186e9dcdd82d", 26.00, 1.0, LocalDate.of(2025, 9, 8));
            UNCaddPurchases(ps, "b7cde652-ea9a-43d3-bfeb-774e68d384c5", 13.49, 1.0, LocalDate.of(2025, 9, 12));
            UNCaddPurchases(ps, "87eee6d3-cb6c-4666-97b0-350ff102f0c4", 7.49, 1.0, LocalDate.of(2025, 9, 12));
            UNCaddPurchases(ps, "f133b129-f19c-4864-9fa5-da4a2ea6841c", 4.49, 3.0, LocalDate.of(2025, 9, 12));
            UNCaddPurchases(ps, "dd1caa5e-63b7-4e8d-b784-3451cdd7384b", 4.49, 3.0, LocalDate.of(2025, 9, 12));
            UNCaddPurchases(ps, "e72178ad-de05-483e-a564-b036785b970c", 3.49, 3.0, LocalDate.of(2025, 9, 12));
            UNCaddPurchases(ps, "76cb1958-00f9-4ee8-8a57-ed1bbad8e8d5", 4.49, 1.0, LocalDate.of(2025, 9, 12));
            UNCaddPurchases(ps, "43123693-3c38-44bd-a3e9-1a6cb9d08b75", 4.99, 3.0, LocalDate.of(2025, 9, 12));
            UNCaddPurchases(ps, "95be1867-3551-431f-80b9-40f811a50e18", 3.49, 1.0, LocalDate.of(2025, 9, 12));
            UNCaddPurchases(ps, "400f961e-77a1-46c4-8dbd-24703afc9b96", 5.49, 2.0, LocalDate.of(2025, 9, 12));
            UNCaddPurchases(ps, "c05fda44-c11f-476e-be98-d6eadac63a05", 8.49, 3.0, LocalDate.of(2025, 9, 12));
            UNCaddPurchases(ps, "4031b990-016c-47f9-8501-b5c83ae860c1", 2.49, 2.0, LocalDate.of(2025, 9, 12));
            UNCaddPurchases(ps, "a8f8b02e-cd64-4c02-9cfb-4df9c19b5e30", 6.49, 2.0, LocalDate.of(2025, 9, 12));
            UNCaddPurchases(ps, "5f871133-c141-42e8-b071-c569e5dfbe57", 9.00, 2.0, LocalDate.of(2025, 9, 12));
            UNCaddPurchases(ps, "0338eeba-4018-4314-b616-384b456e525d", 3.49, 1.0, LocalDate.of(2025, 9, 12));
            UNCaddPurchases(ps, "400f961e-77a1-46c4-8dbd-24703afc9b96", 6.49, 2.0, LocalDate.of(2025, 9, 16));
            UNCaddPurchases(ps, "95be1867-3551-431f-80b9-40f811a50e18", 2.99, 3.0, LocalDate.of(2025, 9, 16));
            UNCaddPurchases(ps, "e456e25d-c34c-4375-ad3c-f19ef011eafa", 5.49, 1.0, LocalDate.of(2025, 9, 16));
            UNCaddPurchases(ps, "060189f1-92e0-48c0-8cb7-e960cf135e42", 3.49, 2.0, LocalDate.of(2025, 9, 16));
            UNCaddPurchases(ps, "f133b129-f19c-4864-9fa5-da4a2ea6841c", 3.99, 3.0, LocalDate.of(2025, 9, 16));
            UNCaddPurchases(ps, "103cb413-23b5-44dd-b036-49c6bb71cb70", 3.00, 2.0, LocalDate.of(2025, 9, 16));
            UNCaddPurchases(ps, "fcd3c43e-b832-466e-96d2-80a7c8b675fb", 13.99, 1.0, LocalDate.of(2025, 9, 16));
            UNCaddPurchases(ps, "76cb1958-00f9-4ee8-8a57-ed1bbad8e8d5", 4.49, 3.0, LocalDate.of(2025, 9, 16));
            UNCaddPurchases(ps, "3717698b-ba54-4db1-87c1-8404e1c878a8", 17.49, 2.0, LocalDate.of(2025, 9, 16));
            UNCaddPurchases(ps, "ec307717-0725-4560-bc90-b21b815c9a1c", 9.49, 3.0, LocalDate.of(2025, 9, 16));
            UNCaddPurchases(ps, "4031b990-016c-47f9-8501-b5c83ae860c1", 2.49, 2.0, LocalDate.of(2025, 9, 16));
            UNCaddPurchases(ps, "87eee6d3-cb6c-4666-97b0-350ff102f0c4", 7.49, 1.0, LocalDate.of(2025, 9, 16));
            UNCaddPurchases(ps, "103cb413-23b5-44dd-b036-49c6bb71cb70", 3.49, 1.0, LocalDate.of(2025, 9, 19));
            UNCaddPurchases(ps, "400f961e-77a1-46c4-8dbd-24703afc9b96", 6.49, 2.0, LocalDate.of(2025, 9, 19));
            UNCaddPurchases(ps, "76cb1958-00f9-4ee8-8a57-ed1bbad8e8d5", 3.99, 1.0, LocalDate.of(2025, 9, 19));
            UNCaddPurchases(ps, "95be1867-3551-431f-80b9-40f811a50e18", 2.99, 1.0, LocalDate.of(2025, 9, 19));
            UNCaddPurchases(ps, "55656e11-7e0a-4e33-87ce-749c8f20ab62", 9.49, 1.0, LocalDate.of(2025, 9, 19));
            UNCaddPurchases(ps, "6a0e0b76-0c67-4960-a639-0492e23fb8c1", 7.49, 1.0, LocalDate.of(2025, 9, 19));
            UNCaddPurchases(ps, "e72178ad-de05-483e-a564-b036785b970c", 4.00, 2.0, LocalDate.of(2025, 9, 19));
            UNCaddPurchases(ps, "e456e25d-c34c-4375-ad3c-f19ef011eafa", 4.99, 2.0, LocalDate.of(2025, 9, 19));
            UNCaddPurchases(ps, "fcd3c43e-b832-466e-96d2-80a7c8b675fb", 14.00, 2.0, LocalDate.of(2025, 9, 19));
            UNCaddPurchases(ps, "43123693-3c38-44bd-a3e9-1a6cb9d08b75", 5.49, 3.0, LocalDate.of(2025, 9, 19));
            UNCaddPurchases(ps, "4970b5c8-82d2-4a27-8d08-749541a704d4", 8.49, 1.0, LocalDate.of(2025, 9, 19));
            UNCaddPurchases(ps, "10b5a323-f47e-411f-ba55-216b2cdf2bd3", 11.49, 2.0, LocalDate.of(2025, 9, 19));
            UNCaddPurchases(ps, "628ed9f6-3cae-4bc3-afc6-e396a90ff09d", 15.49, 1.0, LocalDate.of(2025, 9, 22));
            UNCaddPurchases(ps, "87eee6d3-cb6c-4666-97b0-350ff102f0c4", 7.00, 3.0, LocalDate.of(2025, 9, 22));
            UNCaddPurchases(ps, "fcd3c43e-b832-466e-96d2-80a7c8b675fb", 13.49, 3.0, LocalDate.of(2025, 9, 22));
            UNCaddPurchases(ps, "4031b990-016c-47f9-8501-b5c83ae860c1", 2.99, 1.0, LocalDate.of(2025, 9, 22));
            UNCaddPurchases(ps, "b7cde652-ea9a-43d3-bfeb-774e68d384c5", 15.99, 1.0, LocalDate.of(2025, 9, 22));
            UNCaddPurchases(ps, "76cb1958-00f9-4ee8-8a57-ed1bbad8e8d5", 4.49, 1.0, LocalDate.of(2025, 9, 22));
            UNCaddPurchases(ps, "591db857-9027-4214-8d1b-7e6dc559aea0", 12.49, 3.0, LocalDate.of(2025, 9, 22));
            UNCaddPurchases(ps, "95be1867-3551-431f-80b9-40f811a50e18", 3.49, 1.0, LocalDate.of(2025, 9, 22));
            UNCaddPurchases(ps, "e72178ad-de05-483e-a564-b036785b970c", 3.49, 3.0, LocalDate.of(2025, 9, 22));
            UNCaddPurchases(ps, "060189f1-92e0-48c0-8cb7-e960cf135e42", 3.49, 1.0, LocalDate.of(2025, 9, 22));
            UNCaddPurchases(ps, "103cb413-23b5-44dd-b036-49c6bb71cb70", 3.49, 3.0, LocalDate.of(2025, 9, 22));
            UNCaddPurchases(ps, "6d00f111-d395-431d-984b-1b25f150f64e", 3.00, 1.0, LocalDate.of(2025, 9, 22));
            UNCaddPurchases(ps, "18a9d99b-2c9c-43de-ad6c-685c4f4d4980", 5.49, 1.0, LocalDate.of(2025, 9, 22));
            UNCaddPurchases(ps, "3717698b-ba54-4db1-87c1-8404e1c878a8", 17.49, 3.0, LocalDate.of(2025, 9, 22));
            UNCaddPurchases(ps, "8118d42b-a447-4a6e-bdc9-70cd70d5c251", 9.49, 1.0, LocalDate.of(2025, 9, 22));
            UNCaddPurchases(ps, "33986208-d2c6-4cd0-b88b-a671579bdcf0", 16.49, 1.0, LocalDate.of(2025, 9, 22));
            UNCaddPurchases(ps, "10b5a323-f47e-411f-ba55-216b2cdf2bd3", 12.99, 2.0, LocalDate.of(2025, 9, 22));
            UNCaddPurchases(ps, "4031b990-016c-47f9-8501-b5c83ae860c1", 2.49, 3.0, LocalDate.of(2025, 9, 26));
            UNCaddPurchases(ps, "b7cde652-ea9a-43d3-bfeb-774e68d384c5", 14.00, 2.0, LocalDate.of(2025, 9, 26));
            UNCaddPurchases(ps, "6d00f111-d395-431d-984b-1b25f150f64e", 3.00, 2.0, LocalDate.of(2025, 9, 26));
            UNCaddPurchases(ps, "591db857-9027-4214-8d1b-7e6dc559aea0", 12.00, 1.0, LocalDate.of(2025, 10, 1));
            UNCaddPurchases(ps, "95be1867-3551-431f-80b9-40f811a50e18", 3.49, 1.0, LocalDate.of(2025, 10, 1));
            UNCaddPurchases(ps, "e456e25d-c34c-4375-ad3c-f19ef011eafa", 5.00, 3.0, LocalDate.of(2025, 10, 1));
            UNCaddPurchases(ps, "628ed9f6-3cae-4bc3-afc6-e396a90ff09d", 16.99, 2.0, LocalDate.of(2025, 10, 1));
            UNCaddPurchases(ps, "43123693-3c38-44bd-a3e9-1a6cb9d08b75", 5.99, 1.0, LocalDate.of(2025, 10, 1));
            UNCaddPurchases(ps, "c05fda44-c11f-476e-be98-d6eadac63a05", 7.49, 2.0, LocalDate.of(2025, 10, 1));
            UNCaddPurchases(ps, "95be1867-3551-431f-80b9-40f811a50e18", 3.49, 2.0, LocalDate.of(2025, 10, 6));
            UNCaddPurchases(ps, "87eee6d3-cb6c-4666-97b0-350ff102f0c4", 6.49, 1.0, LocalDate.of(2025, 10, 6));
            UNCaddPurchases(ps, "4031b990-016c-47f9-8501-b5c83ae860c1", 2.49, 3.0, LocalDate.of(2025, 10, 6));
            UNCaddPurchases(ps, "18a9d99b-2c9c-43de-ad6c-685c4f4d4980", 6.00, 1.0, LocalDate.of(2025, 10, 6));
            UNCaddPurchases(ps, "fcd3c43e-b832-466e-96d2-80a7c8b675fb", 12.99, 3.0, LocalDate.of(2025, 10, 6));
            UNCaddPurchases(ps, "b7cde652-ea9a-43d3-bfeb-774e68d384c5", 15.99, 1.0, LocalDate.of(2025, 10, 6));
            UNCaddPurchases(ps, "400f961e-77a1-46c4-8dbd-24703afc9b96", 6.00, 3.0, LocalDate.of(2025, 10, 6));
            UNCaddPurchases(ps, "e456e25d-c34c-4375-ad3c-f19ef011eafa", 5.49, 2.0, LocalDate.of(2025, 10, 6));
            UNCaddPurchases(ps, "103cb413-23b5-44dd-b036-49c6bb71cb70", 3.49, 2.0, LocalDate.of(2025, 10, 6));
            UNCaddPurchases(ps, "591db857-9027-4214-8d1b-7e6dc559aea0", 12.00, 2.0, LocalDate.of(2025, 10, 6));
            UNCaddPurchases(ps, "6d00f111-d395-431d-984b-1b25f150f64e", 2.99, 3.0, LocalDate.of(2025, 10, 6));
            UNCaddPurchases(ps, "c05fda44-c11f-476e-be98-d6eadac63a05", 8.49, 2.0, LocalDate.of(2025, 10, 6));
            UNCaddPurchases(ps, "43123693-3c38-44bd-a3e9-1a6cb9d08b75", 5.49, 2.0, LocalDate.of(2025, 10, 6));
            UNCaddPurchases(ps, "060189f1-92e0-48c0-8cb7-e960cf135e42", 3.99, 2.0, LocalDate.of(2025, 10, 6));
            UNCaddPurchases(ps, "bc41cce8-c598-449a-9051-563591c2b1c1", 3.99, 2.0, LocalDate.of(2025, 10, 11));
            UNCaddPurchases(ps, "f133b129-f19c-4864-9fa5-da4a2ea6841c", 4.49, 1.0, LocalDate.of(2025, 10, 11));
            UNCaddPurchases(ps, "e456e25d-c34c-4375-ad3c-f19ef011eafa", 5.49, 3.0, LocalDate.of(2025, 10, 11));
            UNCaddPurchases(ps, "400f961e-77a1-46c4-8dbd-24703afc9b96", 6.99, 2.0, LocalDate.of(2025, 10, 11));
            UNCaddPurchases(ps, "87eee6d3-cb6c-4666-97b0-350ff102f0c4", 7.49, 2.0, LocalDate.of(2025, 10, 11));
            UNCaddPurchases(ps, "95be1867-3551-431f-80b9-40f811a50e18", 3.49, 1.0, LocalDate.of(2025, 10, 11));
            UNCaddPurchases(ps, "b7cde652-ea9a-43d3-bfeb-774e68d384c5", 13.49, 2.0, LocalDate.of(2025, 10, 11));
            UNCaddPurchases(ps, "76cb1958-00f9-4ee8-8a57-ed1bbad8e8d5", 4.49, 2.0, LocalDate.of(2025, 10, 11));
            UNCaddPurchases(ps, "628ed9f6-3cae-4bc3-afc6-e396a90ff09d", 14.49, 1.0, LocalDate.of(2025, 10, 11));
            UNCaddPurchases(ps, "3717698b-ba54-4db1-87c1-8404e1c878a8", 19.49, 1.0, LocalDate.of(2025, 10, 11));
            UNCaddPurchases(ps, "4031b990-016c-47f9-8501-b5c83ae860c1", 2.49, 1.0, LocalDate.of(2025, 10, 11));
            UNCaddPurchases(ps, "fcd3c43e-b832-466e-96d2-80a7c8b675fb", 14.99, 1.0, LocalDate.of(2025, 10, 11));
            UNCaddPurchases(ps, "103cb413-23b5-44dd-b036-49c6bb71cb70", 3.49, 3.0, LocalDate.of(2025, 10, 11));
            UNCaddPurchases(ps, "bc41cce8-c598-449a-9051-563591c2b1c1", 4.00, 3.0, LocalDate.of(2025, 10, 16));
            UNCaddPurchases(ps, "e72178ad-de05-483e-a564-b036785b970c", 3.99, 2.0, LocalDate.of(2025, 10, 16));
            UNCaddPurchases(ps, "c05fda44-c11f-476e-be98-d6eadac63a05", 8.49, 3.0, LocalDate.of(2025, 10, 16));
            UNCaddPurchases(ps, "b2c06d14-5843-49c4-afe8-366d1a760349", 13.49, 2.0, LocalDate.of(2025, 10, 16));
            UNCaddPurchases(ps, "4970b5c8-82d2-4a27-8d08-749541a704d4", 8.00, 2.0, LocalDate.of(2025, 10, 16));
            UNCaddPurchases(ps, "95be1867-3551-431f-80b9-40f811a50e18", 2.99, 2.0, LocalDate.of(2025, 10, 20));
            UNCaddPurchases(ps, "76cb1958-00f9-4ee8-8a57-ed1bbad8e8d5", 4.49, 3.0, LocalDate.of(2025, 10, 20));
            UNCaddPurchases(ps, "b7cde652-ea9a-43d3-bfeb-774e68d384c5", 13.99, 3.0, LocalDate.of(2025, 10, 20));
            UNCaddPurchases(ps, "e456e25d-c34c-4375-ad3c-f19ef011eafa", 4.99, 3.0, LocalDate.of(2025, 10, 24));
            UNCaddPurchases(ps, "591db857-9027-4214-8d1b-7e6dc559aea0", 12.00, 3.0, LocalDate.of(2025, 10, 24));
            UNCaddPurchases(ps, "dd1caa5e-63b7-4e8d-b784-3451cdd7384b", 5.00, 3.0, LocalDate.of(2025, 10, 24));
            UNCaddPurchases(ps, "6d00f111-d395-431d-984b-1b25f150f64e", 2.99, 1.0, LocalDate.of(2025, 10, 24));
            UNCaddPurchases(ps, "628ed9f6-3cae-4bc3-afc6-e396a90ff09d", 15.49, 1.0, LocalDate.of(2025, 10, 24));
            UNCaddPurchases(ps, "e72178ad-de05-483e-a564-b036785b970c", 3.49, 2.0, LocalDate.of(2025, 10, 24));
            UNCaddPurchases(ps, "18a9d99b-2c9c-43de-ad6c-685c4f4d4980", 5.49, 1.0, LocalDate.of(2025, 10, 24));
            UNCaddPurchases(ps, "43123693-3c38-44bd-a3e9-1a6cb9d08b75", 5.49, 2.0, LocalDate.of(2025, 10, 24));
            UNCaddPurchases(ps, "b7cde652-ea9a-43d3-bfeb-774e68d384c5", 13.49, 1.0, LocalDate.of(2025, 10, 24));
            UNCaddPurchases(ps, "87eee6d3-cb6c-4666-97b0-350ff102f0c4", 6.49, 3.0, LocalDate.of(2025, 10, 24));
            UNCaddPurchases(ps, "f841e912-c7e3-4fb0-950f-bdb149681b7e", 5.99, 1.0, LocalDate.of(2025, 10, 24));
            UNCaddPurchases(ps, "e72178ad-de05-483e-a564-b036785b970c", 3.49, 1.0, LocalDate.of(2025, 10, 27));
            UNCaddPurchases(ps, "6d00f111-d395-431d-984b-1b25f150f64e", 3.00, 1.0, LocalDate.of(2025, 10, 27));
            UNCaddPurchases(ps, "6a0e0b76-0c67-4960-a639-0492e23fb8c1", 6.99, 1.0, LocalDate.of(2025, 10, 27));
            UNCaddPurchases(ps, "bc41cce8-c598-449a-9051-563591c2b1c1", 3.49, 3.0, LocalDate.of(2025, 10, 27));
            UNCaddPurchases(ps, "4031b990-016c-47f9-8501-b5c83ae860c1", 2.49, 3.0, LocalDate.of(2025, 10, 27));
            UNCaddPurchases(ps, "87eee6d3-cb6c-4666-97b0-350ff102f0c4", 7.00, 2.0, LocalDate.of(2025, 10, 27));

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
