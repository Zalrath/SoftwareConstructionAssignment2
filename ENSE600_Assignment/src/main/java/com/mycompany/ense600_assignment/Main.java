/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ense600_assignment;

/**
 *
 * @author corin
 */
import java.io.File;
import java.time.LocalDate;
//import java.time.LocalDateTime; could be useful if we want to track time of purchase as well as day 
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        InventoryManager manager = new InventoryManager();

        File itemFile = new File("items.txt");
        File logFile = new File("purchases.txt");

        if (itemFile.exists()) manager.loadItems("items.txt");
        if (logFile.exists()) manager.loadPurchases("purchases.txt");

        // add capabilty for use to inpit names if logged items         
        // add capabilty to check for duplcut names
  
        
        Item milk = new Item("Milk 2L"); 
        // me messing around a bit
        manager.addItem(milk);
//        manager.logPurchase(milk.getUuid(), LocalDate.of(2025, 7, 1));
//        manager.logPurchase(milk.getUuid(), LocalDate.of(2025, 7, 8));
//        manager.logPurchase(milk.getUuid(), LocalDate.of(2025, 7, 25));
        
         manager.logPurchase(milk.getUuid(), LocalDate.now());

        
        List<Item> toBuy = manager.getItemsToReplenish(LocalDate.of(2025, 8, 4));
        for (Item i : toBuy) {
            System.out.println("Needs restocking: " + i.getName());
        }

        manager.saveItems("items.txt");
        manager.savePurchases("purchases.txt");
        
        for (Item item : manager.getAllItems()) {
            System.out.println("Item: " + item.getName());
            System.out.println("  UUID: " + item.getUuid());
            System.out.println("  Last Purchased: " + item.getLastPurchased());
            System.out.println("  Estimated Interval: " + item.getEstimatedIntervalDays() + " days");
            System.out.println("  Next Expected Purchase: " + item.getNextExpectedPurchase());
            System.out.println();
        }
        
    }
}


// checking Git