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
    
       //private Connection conn;
public static Connection connectToDatabase() throws ClassNotFoundException {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            String url = "jdbc:derby:InventoryDB;create=true";
            Connection conn = DriverManager.getConnection(url);
            System.out.println("Connected to Derby Embedded DB");
            return conn;
        } catch (SQLException e) {
            return null;
        }
    }

    // 🧩 Create Tables
    public void createTables(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("""
                CREATE TABLE Items (
                    uuid VARCHAR(36) PRIMARY KEY,
                    name VARCHAR(100),
                    lastPurchased DATE,
                    estimatedIntervalDays INT,
                    tags VARCHAR(255)
                )
            """);

            stmt.executeUpdate("""
                CREATE TABLE Purchases (
                    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                    itemUUID VARCHAR(36),
                    price DOUBLE,
                    quantity DOUBLE,
                    purchaseDate DATE
                )
            """);

            System.out.println("Tables created");
        } catch (SQLException e) {
            // Ignore "table already exists" error
            if (!"X0Y32".equals(e.getSQLState())) {}
        }
    }
    
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
    

}
