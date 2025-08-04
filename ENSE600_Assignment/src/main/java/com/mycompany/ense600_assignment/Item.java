/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ense600_assignment;

import java.time.LocalDate;
import java.util.UUID;

public class Item {
    private UUID uuid;
    private String name;
    private LocalDate lastPurchased;
    private int estimatedIntervalDays;
    private LocalDate nextExpectedPurchase;

    public Item() {}

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

    public int getEstimatedIntervalDays() { return estimatedIntervalDays; }
    public void setEstimatedIntervalDays(int estimatedIntervalDays) { this.estimatedIntervalDays = estimatedIntervalDays; }

    public LocalDate getNextExpectedPurchase() { return nextExpectedPurchase; }
    public void setNextExpectedPurchase(LocalDate nextExpectedPurchase) { this.nextExpectedPurchase = nextExpectedPurchase; }
}
