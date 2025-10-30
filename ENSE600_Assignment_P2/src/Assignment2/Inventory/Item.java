/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Assignment2.Inventory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.*;

public class Item {
    private UUID uuid;
    private String name;
    private LocalDate lastPurchased;
    private double currentAmount;
    private int estimatedIntervalDays;
    private LocalDate nextExpectedPurchase;
    private boolean fav;
    private boolean future;
    
    private ArrayList<String> tags = new ArrayList<>();
    
    

    public Item() {
}

    public Item(String name) {
        
        this.uuid = UUID.randomUUID();
        this.name = name;
       
    }

    public void updateNextExpectedPurchase() {
        if (lastPurchased != null && estimatedIntervalDays > 0) {
            this.nextExpectedPurchase = lastPurchased.plusDays(estimatedIntervalDays);
        }
    }

    public boolean needsReplenishment(LocalDate today) {
        return nextExpectedPurchase != null && !today.isBefore(nextExpectedPurchase);
    }

    // Getters and setters
    public UUID getUuid() { return uuid; }
    public void setUuid(UUID uuid) { this.uuid = uuid; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDate getLastPurchased() { return lastPurchased; }
    public void setLastPurchased(LocalDate lastPurchased) { this.lastPurchased = lastPurchased; }

    public double getCurrentAmount() { return currentAmount; }
    public void setCurrentAmount(double currentAmount) { this.currentAmount = currentAmount; }
    
    
    public int getEstimatedIntervalDays() { return estimatedIntervalDays; }
    public void setEstimatedIntervalDays(int estimatedIntervalDays) { this.estimatedIntervalDays = estimatedIntervalDays; }

    public LocalDate getNextExpectedPurchase() { return nextExpectedPurchase; }
    public void setNextExpectedPurchase(LocalDate nextExpectedPurchase) { this.nextExpectedPurchase = nextExpectedPurchase; }
     
    public ArrayList<String> getTags() {return tags; }

    public void setTags(ArrayList<String> tags) {this.tags = tags; }

    public void addTag(String tag) {
        if (!tags.contains(tag)) {
            tags.add(tag);
        }
    }
    public void removeTag(String tag) {tags.remove(tag); }
    
    public boolean getFavorite() { return fav; }
    public void setFavorite(boolean fav) { this.fav = fav; }
    
    public boolean getFuture() { return future; }
    public void setFuture(boolean future) { this.future = future; }
    
    
}
