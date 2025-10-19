/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Project_p2;

import java.time.LocalDate;
import java.util.UUID;

public class PurchaseLog {
    private UUID itemId;
    private double price;
    private double quantity; 
    private LocalDate purchaseDate;
    
    
    public PurchaseLog(UUID itemId, double price ,double quantity , LocalDate date) {
        this.itemId = itemId;
        this.price = price;
        this.quantity = quantity;
        this.purchaseDate = date;
               
    }

    public UUID getItemId() { return itemId; }
    public LocalDate getPurchaseDate() { return purchaseDate; }
    public double getPrice() { return price; }
    public double getQuantity() { return quantity; }
}
