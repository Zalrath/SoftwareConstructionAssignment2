/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Assignment2.Inventory;

/**
 *
 * @author corin
 * 
 * Need to add 

 * 
 * 
 */

import Assignment2.Database.DatabaseUtil;
import java.sql.*;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class InventoryManager {
    private Map<UUID, Item> items = new HashMap<>();
    private Map<UUID, List<PurchaseLog>> purchaseHistory = new HashMap<>();

   

    
    
    
    
// im doing it again :( sowwy
    
    
    public Map<UUID, List<PurchaseLog>> getPurchaseHistory() 
    {
        return purchaseHistory;
    }
    
    public Item getItemByUUID(UUID id) 
    {
        return items.get(id);
    }

    

    
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
    
    public Collection<Item> getAllItems() 
    {
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
    
    

        
    
    
    
    
    
    
    public Map<String, Double> getSpendingByTag(String filter) { // changed the one in the spendingPanel so it can be used for the Vs Panel
        Map<String, Double> spendingByTag = new HashMap<>();
        LocalDate cutoff = switch (filter.toLowerCase()) {
            case "week" -> LocalDate.now().minusDays(7);
            case "month" -> LocalDate.now().minusMonths(1);
            case "year" -> LocalDate.now().minusYears(1);
            default -> null; // all time
        };

        for (var entry : purchaseHistory.entrySet()) {
            UUID itemId = entry.getKey();
            List<PurchaseLog> logs = entry.getValue();
            if (logs == null || logs.isEmpty()) continue;

            Item item = items.get(itemId);
            if (item == null) continue;

            ArrayList<String> tags = item.getTags();
            if (tags == null || tags.isEmpty()) continue;

            // Use first tag (or could loop through all if you want)
            String firstTag = tags.get(0);
            double totalForItem = 0.0;

            for (PurchaseLog log : logs) {
                if (cutoff != null && log.getPurchaseDate().isBefore(cutoff)) continue;
                totalForItem += log.getPrice() * log.getQuantity();
            }

            if (totalForItem > 0)
                spendingByTag.merge(firstTag, totalForItem, Double::sum);
        }

        return spendingByTag;
    }
    
    public double getTotalSpendingForPeriod(String filter) {
        Map<String, Double> spendingMap = getSpendingByTag(filter);
        return spendingMap.values().stream().mapToDouble(Double::doubleValue).sum();
    }
    
    // Data base functions 
    /////////////////////////////////////////////////////////////////////////////////////

    
    
    
    
    
    // Save Items to DB
    public void saveItemsToDB(Connection conn) {
        System.out.println("Trying to Save items");
        
        String sql = "INSERT INTO Items (uuid, name, last_Purchased, current_Amount , interval_Days, tags, favorite, future) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Item item : items.values()) {
                ps.setString(1, item.getUuid().toString());
                ps.setString(2, item.getName());

                LocalDate lp = item.getLastPurchased();
                if (lp != null) ps.setDate(3, java.sql.Date.valueOf(lp));
                else ps.setNull(3, Types.DATE);
                ps.setDouble(4, item.getCurrentAmount());
                ps.setInt(5, item.getEstimatedIntervalDays());
                ps.setString(6, String.join("|", item.getTags()));
                ps.setBoolean(7, item.getFavorite());
                ps.setBoolean(8, item.getFuture());
                ps.addBatch();
            }
            ps.executeBatch();
            System.out.println("Items saved to DB");
        } catch (SQLException e) {}
    }

    
    
    
    
    // Save Purchases to DB
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
            System.out.println("Purchases saved to DB");
        } catch (SQLException e) {}
    }

    
    
    
    
    //Load Items from DB
    public void loadItemsFromDB(Connection conn) {
        items.clear();
        String sql = "SELECT * FROM Items";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("uuid"));
                String name = rs.getString("name");
                LocalDate lastPurchased = rs.getDate("last_Purchased") != null
                        ? rs.getDate("last_Purchased").toLocalDate()
                        : null;
                double current = rs.getDouble("current_Amount");
                int interval = rs.getInt("interval_Days");
                String tagsStr = rs.getString("tags");
                ArrayList<String> tags = new ArrayList<>();
                if (tagsStr != null && !tagsStr.isEmpty()) {
                    tags.addAll(Arrays.asList(tagsStr.split("\\|")));
                }
                
                boolean favorite = rs.getBoolean("favorite");
                boolean future = rs.getBoolean("future");
                
                Item item = new Item(name);
                item.setUuid(uuid);
                item.setLastPurchased(lastPurchased);
                item.setCurrentAmount(current);
                item.setEstimatedIntervalDays(interval);
                item.setTags(tags);
                item.updateNextExpectedPurchase();
                item.setFavorite(favorite);  
                item.setFuture(future); 
                
                
                items.put(uuid, item);
            }

            System.out.println("Items loaded from DB");
        } catch (SQLException e) {}
    }

    
    
    
    
    // Load Purchases from DB
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

            System.out.println("Purchases loaded from DB");
        } catch (SQLException e) {}
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
            System.out.println(item);
            System.out.println();
            System.out.println();
        }
        
        for (Item item : manager.getAllItems()) 
                {
                    System.out.println("Item: " + item.getName());
                    System.out.println("  UUID: " + item.getUuid());
                    System.out.println("  Last Purchased: " + item.getLastPurchased());
                    System.out.println("  Estimated Interval: " + item.getEstimatedIntervalDays() + " days");
                    System.out.println("  Next Expected Purchase: " + item.getNextExpectedPurchase());
                    System.out.println(item.getFuture());
                    System.out.println(item.getFavorite());
                    System.out.println(item.getCurrentAmount());
                    System.out.println();
                }

        
    
  
    */
