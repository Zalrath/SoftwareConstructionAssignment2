/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Project_p2;

/**
 *
 * @author corin
 * 
 * Need to add 

 * 
 * 
 */
import java.sql.*;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class InventoryManager {
    private Map<UUID, Item> items = new HashMap<>();
    private Map<UUID, List<PurchaseLog>> purchaseHistory = new HashMap<>();

    
    /*
    public InventoryManager(Settings settings) {
        this.settings = settings;
    }
    
    int width = settings.getScreenWidth();
    int height = settings.getScreenHeight();
    
    
    
    */
    
    
    
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Load from text files 
    public void loadItems(String path) throws IOException {
        
        items.clear();
       
 
        List<String> lines = Files.readAllLines(Paths.get(path));
        lines.remove(0); // skip header

        for (String line : lines) {
            String[] parts = line.split(",");
            UUID uuid = UUID.fromString(parts[0]);
            String name = parts[1];
            LocalDate lastPurchased = LocalDate.parse(parts[2]);
            int interval = Integer.parseInt(parts[3]);
            String[] tagParts = parts[4].split("\\|"); 
            ArrayList<String> tags = new ArrayList<>(Arrays.asList(tagParts));


            Item item = new Item();
            item.setUuid(uuid);
            item.setName(name);
            item.setLastPurchased(lastPurchased);
            item.setEstimatedIntervalDays(interval);
            item.updateNextExpectedPurchase();
            item.setTags(tags);

            items.put(uuid, item);
        }
        
        
        
    }

    
    
    
    
    public void loadPurchases(String path) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(path));
        lines.remove(0);

        for (String line : lines) {
            String[] parts = line.split(",");
            UUID uuid = UUID.fromString(parts[0]);
            double price = Double.parseDouble(parts[1]);
            double quantity = Double.parseDouble(parts[2]);
            LocalDate date = LocalDate.parse(parts[3]);
            
            
            purchaseHistory.computeIfAbsent(uuid, k -> new ArrayList<>())
                .add(new PurchaseLog(uuid,price ,quantity , date));
        }
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
   
    // Save to text files
    public void saveItems(String path) throws IOException {
        BufferedWriter writer = Files.newBufferedWriter(Paths.get(path));
        writer.write("UUID,  Name,  Last Purchased,  Estimated Interval Days,  Tags\n");
        
        
        for (Item item : items.values()) {
            String tagString = String.join("|", item.getTags());
            writer.write(String.format("%s,%s,%s,%d,%s%n", // add another for the tags
                item.getUuid(),
                item.getName(),
                item.getLastPurchased(),
                item.getEstimatedIntervalDays(),
                tagString));
        }
        
        
        writer.close();
    }

    public void savePurchases(String path) throws IOException {
        BufferedWriter writer = Files.newBufferedWriter(Paths.get(path));
        writer.write("Item ID,  Price,  Quantity,  Purchase Date\n");
        for (List<PurchaseLog> logs : purchaseHistory.values()) {
            for (PurchaseLog log : logs) {
                writer.write(String.format("%s,%s,%s,%s\n",
                    log.getItemId(),
                    log.getPrice(),
                    log.getQuantity(),
                    log.getPurchaseDate()));
            }
        }
        writer.close();
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    
    
    
    
    public void addItem(Item item) {
        items.put(item.getUuid(), item);
    }
 //
    public void logPurchase(UUID itemId, double price , double quantity , LocalDate date) {
        purchaseHistory.computeIfAbsent(itemId, k -> new ArrayList<>())
            .add(new PurchaseLog(itemId, price, quantity, date));

        Item item = items.get(itemId);
        if (item != null) {
            item.setLastPurchased(date);
            item.setEstimatedIntervalDays(calculateAvgInterval(itemId));
            item.updateNextExpectedPurchase();
        }
    }

    private int calculateAvgInterval(UUID itemId) {
        List<PurchaseLog> logs = purchaseHistory.get(itemId);
        if (logs == null || logs.size() < 2) return 0;

        logs.sort(Comparator.comparing(PurchaseLog::getPurchaseDate));
        long totalDays = 0;
        for (int i = 1; i < logs.size(); i++) {
            totalDays += ChronoUnit.DAYS.between(
                logs.get(i - 1).getPurchaseDate(),
                logs.get(i).getPurchaseDate()
            );
        }
        return Math.round((float) totalDays / (logs.size() - 1));
    }

    public List<Item> getItemsToReplenish(LocalDate today) {
        List<Item> needed = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.needsReplenishment(today)) {
                needed.add(item);
            }
        }
        return needed;
    }
    
    public Collection<Item> getAllItems() {
        return items.values();
    } 

    
    
    
    // ------- I AM SORRY I ADDED THIS IDK IF YOU HAVE SOMETHING BETTER --------- // 
    public Set<String> extractAllTags() 
    {
        Set<String> uniqueTags = new HashSet<>();
        for (Item item : getAllItems()) 
        {
            uniqueTags.addAll(item.getTags());
        }
    
        return uniqueTags;
    }
    
    public double getLatestPrice(UUID uuid) {
        List<PurchaseLog> logs = purchaseHistory.get(uuid);
        if (logs == null || logs.isEmpty()) {
            return 0;
        }
        return logs.get(logs.size() - 1).getPrice();
    }

    public double getLatestQuantity(UUID uuid) {
        List<PurchaseLog> logs = purchaseHistory.get(uuid);
        if (logs == null || logs.isEmpty()) {
            return 0;
        }
        return logs.get(logs.size() - 1).getQuantity();
    }

    public double getTotalSpent(UUID uuid) {
        List<PurchaseLog> logs = purchaseHistory.get(uuid);
        if (logs == null) {
            return 0;
        }
        return logs.stream().mapToDouble(l -> l.getPrice() * l.getQuantity()).sum();
    }
  
    
    
    // _______________________________
    // Data base stuff
   
    
    
    //private Connection conn;
public static Connection connectToDatabase() {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            String url = "jdbc:derby:InventoryDB;create=true";
            Connection conn = DriverManager.getConnection(url);
            System.out.println("✅ Connected to Derby Embedded DB");
            return conn;
        } catch (Exception e) {
            e.printStackTrace();
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

            System.out.println("✅ Tables created");
        } catch (SQLException e) {
            // Ignore "table already exists" error
            if (!"X0Y32".equals(e.getSQLState())) {
                e.printStackTrace();
            }
        }
    }

    // 🧩 Save Items
    public void saveItemsToDB(Connection conn) {
        String sql = "INSERT INTO Items (uuid, name, last_Purchased, Interval_Days, tags) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Item item : items.values()) {
                ps.setString(1, item.getUuid().toString());
                ps.setString(2, item.getName());

                LocalDate lp = item.getLastPurchased();
                if (lp != null) ps.setDate(3, java.sql.Date.valueOf(lp));
                else ps.setNull(3, Types.DATE);

                ps.setInt(4, item.getEstimatedIntervalDays());
                ps.setString(5, String.join("|", item.getTags()));
                ps.addBatch();
            }
            ps.executeBatch();
            System.out.println("✅ Items saved to DB");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 🧩 Save Purchases
    public void savePurchasesToDB(Connection conn) {
        String sql = "INSERT INTO Purchases (itemUUID, price, quantity, purchaseDate) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (List<PurchaseLog> logs : purchaseHistory.values()) {
                for (PurchaseLog log : logs) {
                    ps.setString(1, log.getItemId().toString());
                    ps.setDouble(2, log.getPrice());
                    ps.setDouble(3, log.getQuantity());
                    ps.setDate(4, java.sql.Date.valueOf(log.getPurchaseDate()));
                    ps.addBatch();
                }
            }
            ps.executeBatch();
            System.out.println("✅ Purchases saved to DB");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 🧩 Load Items
    public void loadItemsFromDB(Connection conn) {
        items.clear();
        String sql = "SELECT * FROM Items";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("uuid"));
                String name = rs.getString("name");
                LocalDate lastPurchased = rs.getDate("lastPurchased") != null
                        ? rs.getDate("lastPurchased").toLocalDate()
                        : null;
                int interval = rs.getInt("estimatedIntervalDays");
                String tagsStr = rs.getString("tags");
                ArrayList<String> tags = new ArrayList<>();
                if (tagsStr != null && !tagsStr.isEmpty()) {
                    tags.addAll(Arrays.asList(tagsStr.split("\\|")));
                }

                Item item = new Item(name);
                item.setUuid(uuid);
                item.setLastPurchased(lastPurchased);
                item.setEstimatedIntervalDays(interval);
                item.setTags(tags);
                item.updateNextExpectedPurchase();

                items.put(uuid, item);
            }

            System.out.println("✅ Items loaded from DB");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 🧩 Load Purchases
    public void loadPurchasesFromDB(Connection conn) {
        purchaseHistory.clear();
        String sql = "SELECT * FROM Purchases";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("itemUUID"));
                double price = rs.getDouble("price");
                double quantity = rs.getDouble("quantity");
                LocalDate date = rs.getDate("purchaseDate").toLocalDate();

                purchaseHistory
                        .computeIfAbsent(uuid, k -> new ArrayList<>())
                        .add(new PurchaseLog(uuid, price, quantity, date));
            }

            System.out.println("✅ Purchases loaded from DB");
        } catch (SQLException e) {
            e.printStackTrace();
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

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
    
}
    




    /*
        
        old main code - corins graveyard of code

         // Item bread = new Item("Bread")   
        // Item milk = new Item("Milk 2L"); 
        // milk.addTag("breakfast");
        // bread.addTag("breakfast");
        // me messing around a bit
        
        //manager.addItem(milk);
        //manager.addItem(bread);
        
        //manager.logPurchase(milk.getUuid(),4.0,1, LocalDate.of(2025, 7, 25));
        
        
         //manager.logPurchase(bread.getUuid(),5.0,2, LocalDate.now());

        
        for (Item item : manager.getAllItems()) 
        {
            System.out.println("Item: " + item.getName());
            System.out.println("  UUID: " + item.getUuid());
            System.out.println("  Last Purchased: " + item.getLastPurchased());
            System.out.println("  Estimated Interval: " + item.getEstimatedIntervalDays() + " days");
            System.out.println("  Next Expected Purchase: " + item.getNextExpectedPurchase());
            System.out.println();
        }
        
        

        
    
  
    */
