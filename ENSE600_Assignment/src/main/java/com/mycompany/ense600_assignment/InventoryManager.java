/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ense600_assignment;

/**
 *
 * @author corin
 */
import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class InventoryManager {
    private Map<UUID, Item> items = new HashMap<>();
    private Map<UUID, List<PurchaseLog>> purchaseHistory = new HashMap<>();

    // Load from text files
    public void loadItems(String path) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(path));
        lines.remove(0); // skip header

        for (String line : lines) {
            String[] parts = line.split(",");
            UUID uuid = UUID.fromString(parts[0]);
            String name = parts[1];
            LocalDate lastPurchased = LocalDate.parse(parts[2]);
            int interval = Integer.parseInt(parts[3]);

            Item item = new Item();
            item.setUuid(uuid);
            item.setName(name);
            item.setLastPurchased(lastPurchased);
            item.setEstimatedIntervalDays(interval);
            item.updateNextExpectedPurchase();

            items.put(uuid, item);
        }
    }

    public void loadPurchases(String path) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(path));
        lines.remove(0);

        for (String line : lines) {
            String[] parts = line.split(",");
            UUID uuid = UUID.fromString(parts[0]);
            LocalDate date = LocalDate.parse(parts[1]);

            purchaseHistory.computeIfAbsent(uuid, k -> new ArrayList<>())
                .add(new PurchaseLog(uuid, date));
        }
    }

    // Save to text files
    public void saveItems(String path) throws IOException {
        BufferedWriter writer = Files.newBufferedWriter(Paths.get(path));
        writer.write("uuid,name,lastPurchased,estimatedIntervalDays\n");
        for (Item item : items.values()) {
            writer.write(String.format("%s,%s,%s,%d\n",
                item.getUuid(),
                item.getName(),
                item.getLastPurchased(),
                item.getEstimatedIntervalDays()));
        }
        writer.close();
    }

    public void savePurchases(String path) throws IOException {
        BufferedWriter writer = Files.newBufferedWriter(Paths.get(path));
        writer.write("itemId,purchaseDate\n");
        for (List<PurchaseLog> logs : purchaseHistory.values()) {
            for (PurchaseLog log : logs) {
                writer.write(String.format("%s,%s\n",
                    log.getItemId(),
                    log.getPurchaseDate()));
            }
        }
        writer.close();
    }

    public void addItem(Item item) {
        items.put(item.getUuid(), item);
    }

    public void logPurchase(UUID itemId, LocalDate date) {
        purchaseHistory.computeIfAbsent(itemId, k -> new ArrayList<>())
            .add(new PurchaseLog(itemId, date));

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
}
