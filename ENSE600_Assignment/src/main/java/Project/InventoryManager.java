/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Project;

/**
 *
 * @author corin
 * 
 * Need to add 
 * Amount/ Quantity 
 * Tags 
 * cost
 * 
 * 
 */
import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class InventoryManager {
    private Map<UUID, Item> items = new HashMap<>();
    private Map<UUID, List<PurchaseLog>> purchaseHistory = new HashMap<>();

    
    
    
    
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
            String[] tagParts = parts[4].split("\\|"); // throws an error? changed the 4 to 3 and it didnt do that anymore
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



///////////////////////////////////////////////////////////////////////////////////////////////////

        /* missing invalid input check and 
           case sensitive yn and also 
           if x is pressured during the confirmation it should end the input and go back to whatever default view
        */
    
    /*
    private final Scanner itemInput = new Scanner(System.in);
        
    public void addPurchaseFunc() {
        
        List<UUID> UUIDlist = new ArrayList<>();
        int index = 0;
        for (Item item : getAllItems()) 
        {
            System.out.println("Index: " + index);
            System.out.println("Item: " + item.getName());
            //System.out.println("  UUID: " + item.getUuid());
            UUIDlist.add(item.getUuid());
            // System.out.println("  Last Purchased: " + item.getLastPurchased());
            //System.out.println("  Estimated Interval: " + item.getEstimatedIntervalDays() + " days");
            // System.out.println("  Next Expected Purchase: " + item.getNextExpectedPurchase());
            System.out.println();
            index++;
         }
        
        System.out.println("Which item do you want to log a Purchase(use the index number):");
        String Indexchoice = itemInput.nextLine();
        
        LocalDate date = getConfirmedDate();
        Double Price = getConfirmedPrice();
        Double Quantity = getConfirmedQuantity();

        logPurchase(UUIDlist.get(Integer.parseInt(Indexchoice)),Price,Quantity, date);

    }
    
    public Item addItemFunc() {
        
        String name = getConfirmedName();
        LocalDate date = getConfirmedDate();
        ArrayList<String> tags = getConfirmedTags();
        Double Price = getConfirmedPrice();
        Double Quantity = getConfirmedQuantity();
        Item newItem = new Item(name);
        newItem.setLastPurchased(date);
        newItem.setTags(tags);
        
        
        System.out.println("Item added: " + name);
        
        addItem(newItem);
        
        logPurchase(newItem.getUuid(),Price,Quantity, date);
        
        return newItem;
        
    }
    
    private Double getConfirmedPrice() {
        
        System.out.print("Input the Price of the item: ");
        String Price = itemInput.nextLine().trim();
        
      
        
        System.out.println("Is this correct? " + Price + " (Y/N)");
        String yn = itemInput.nextLine().trim().toUpperCase();

        
        if (yn.equals("Y")) {
            return Double.valueOf(Price);
        } else {
            return getConfirmedPrice(); 
        }
    
    }
    
    private Double getConfirmedQuantity() {
        
        System.out.print("Input the Quantity of the item: ");
        String Quantity = itemInput.nextLine().trim();

        
        System.out.println("Is this correct? " + Quantity + " (Y/N)");
        String yn = itemInput.nextLine().trim().toUpperCase();

        
        if (yn.equals("Y")) {
            return Double.valueOf(Quantity);
        } else {
            return getConfirmedQuantity();  
        }
    
    }
    
    
    
    
    private String getConfirmedName() {
        
        System.out.print("Input the name of the item: ");
        String name = itemInput.nextLine().trim();

        
        System.out.println("Is this correct? " + name + " (Y/N)");
        String yn = itemInput.nextLine().trim().toUpperCase();

        
        if (yn.equals("Y")) {
            return name;
        } else {
            return getConfirmedName(); 
        }
    
    }

    
    private LocalDate getConfirmedDate() {
    
        System.out.print("Enter last purchased date (yyyy-mm-dd) or leave empty: ");
        
        String dateInput = itemInput.nextLine().trim();

        LocalDate date;
        if (!dateInput.isEmpty()) {
            try {
                date = LocalDate.parse(dateInput);
            } catch (Exception e) {
                System.out.println("Invalid date format, try again.");
                return getConfirmedDate(); 
            }
        } else {
            date = LocalDate.now();
        }

        System.out.println("Is this correct? " + date + " (Y/N)");
        String yn = itemInput.nextLine().trim().toUpperCase();

        if (yn.equals("Y")) {
            return date;
        } else {
            return getConfirmedDate();
        }
    }

    
    private ArrayList<String> getConfirmedTags() {
        System.out.print("Enter tags (comma separated) or leave empty: ");
        String tagInput = itemInput.nextLine().trim();

        ArrayList<String> tags = new ArrayList<>();
        if (!tagInput.isEmpty()) {
            for (String tag : tagInput.split(",")) {
                tags.add(tag.trim());
            }
        }

        System.out.println("Tags entered: " + tags);
        System.out.println("Is this correct? (Y/N)");
        String yn = itemInput.nextLine().trim().toUpperCase();

        if (yn.equals("Y")) {
            return tags;
        } else {
            return getConfirmedTags();
        }
    }
    
    */
    
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
        
        

        ----Im just planning in here----
    
  
    */
