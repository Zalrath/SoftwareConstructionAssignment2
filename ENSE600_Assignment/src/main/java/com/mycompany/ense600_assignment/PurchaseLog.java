/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ense600_assignment;

import java.time.LocalDate;
import java.util.UUID;

public class PurchaseLog {
    private UUID itemId;
    private LocalDate purchaseDate;

    public PurchaseLog(UUID itemId, LocalDate date) {
        this.itemId = itemId;
        this.purchaseDate = date;
    }

    public UUID getItemId() { return itemId; }
    public LocalDate getPurchaseDate() { return purchaseDate; }
}
