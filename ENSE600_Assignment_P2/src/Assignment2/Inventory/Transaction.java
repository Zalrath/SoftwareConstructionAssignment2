/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Assignment2.Inventory;

import java.time.LocalDate;
import java.util.UUID;

/**
 *
 * @author corin
 */

public class Transaction {
    private UUID id;
    private String type;
    private String title;
    private String tag;
    private double amount;
    private String frequency;
    private String date;

    public Transaction(UUID id, String type, String title, String tag, double amount, String frequency, String date) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.tag = tag;
        this.amount = amount;
        this.frequency = frequency;
        this.date = date;
    }

    public Transaction(String type, String title, String tag, double amount, String frequency, String date) {
        this(UUID.randomUUID(), type, title, tag, amount, frequency, date);
    }

    // Getters and setters...
    
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }
    
    

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    
    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }
    
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

 
    
}