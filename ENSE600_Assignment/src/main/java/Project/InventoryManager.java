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
    // added tags 
    
    
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



    public void addItemFunc() {
    
        
        boolean confirmed_name = false;
        boolean confirmed_date = false;
        boolean confirmed_tag = false;
        String name = null;
        String dateInput = null;
        LocalDate date = null;
        
        
        
        
        Scanner itemInput = new Scanner(System.in);
        
        while(!confirmed_name){
            
            System.out.println("input the name of the item");
            
            while(itemInput.hasNextLine())
            name = itemInput.nextLine();
    

            //name = itemInput.next();
            System.out.println("is this the correct input" + name + "Y/N");
            String yn = itemInput.next();
            if(yn.equals("Y"))
            {
                confirmed_name = true;
                
    
    
            }
            else{}
        }

        
        
        Item newItem = new Item(name);



        while(!confirmed_date){
            

            

            System.out.print("Enter last purchased date (yyyy-mm-dd) or leave empty: ");
            dateInput = itemInput.nextLine();

            if (!dateInput.isEmpty()) {
               try{
                   
                   date  =  LocalDate.parse(dateInput); 
                   dateInput = date.toString();
                   
                   //dateInput =  LocalDate.parse(dateInput);      // newItem.setLastPurchased(LocalDate.parse(dateInput));
                }
                catch(Exception e) {
                    System.out.println("invaild input");
                } 
            }
            else{
               
               date  =  LocalDate.now(); 
               dateInput = date.toString();
               
            }

            System.out.println("is this the correct input" + dateInput + "Y/N");

            String yn = itemInput.next();
            if(yn.equals("Y"))
            {
                confirmed_date = true;
                newItem.setLastPurchased(date);
    
            }
            else{}
        }    
    

        while(!confirmed_tag){


            ArrayList<String> tags = new ArrayList<>();
            System.out.print("Enter tags (comma separated) or leave empty: ");
            String tagInput = itemInput.nextLine();
            if (!tagInput.isEmpty()) {
                for (String tag : tagInput.split(",")) {
                    tags.add(tag.trim());
                }
            }
            
            
            
            System.out.println(tags);

            
            System.out.println("is this the correct input Y/N");
            String yn = itemInput.next();
            if(yn.equals("Y"))
                {
                    confirmed_tag = true;
                    // Add item to inventory
                    newItem.setTags(tags);
                   // manager.addItem(newItem);
                    addItem(newItem);
                    System.out.println("✅ Item added: " + name);
                }
                else{}
        }    



    }



}



    /*
        
        old main code

         // Item bread = new Item("Bread")   
        // Item milk = new Item("Milk 2L"); 
        // milk.addTag("breakfast");
        // bread.addTag("breakfast");
        // me messing around a bit
        
        //manager.addItem(milk);
        //manager.addItem(bread);
        
        //manager.logPurchase(milk.getUuid(),4.0,1, LocalDate.of(2025, 7, 25));
        
        
         //manager.logPurchase(bread.getUuid(),5.0,2, LocalDate.now());


        

        ----Im just planning in here----
    
        
        



                
       
    
    
    
    public void addItemFunc() {
    
        
        boolean confirmed_name = false;
        boolean confirmed_date = false;
        boolean confirmed_tag = false;

        while(!confirmed_name){
            
            System.out.println("input the name of the item")
            String name = scannerobj.nextLine();
            System.out.println("is this the correct input" + name + "Y/N")
            yn = scannerobj.next();
            if(yn.isEqual("Y"))
            {
                confirmed_name = true;
                Item newItem = new Item(name);
    
    
            }
            else{}
        }





        while(!confirmed_date){
            

            String dateInput;

            System.out.print("Enter last purchased date (yyyy-mm-dd) or leave empty: ");
            dateInput = scannerobj.nextLine();

            if (!dateInput.isEmpty()) {
               try{
                dateInput =  LocalDate.parse(dateInput);      // newItem.setLastPurchased(LocalDate.parse(dateInput));
                }
                catch(Exception e) {
                    System.out.println("invaild input")
                } 
            }
            else{
               dateInput =  LocalDate.now()
            }

            System.out.println("is this the correct input" + dateInput + "Y/N")

            yn = scannerobj.next();
            if(yn.isEqual("Y"))
            {
                confirmed_date = true;
                newItem.setLastPurchased(dateInput);
    
            }
            else{}
        }    
    

        while(!confirmed_tag){


            ArrayList<String> tags = new ArrayList<>();
            System.out.print("Enter tags (comma separated) or leave empty: ");
            String tagInput = scanner.nextLine();
            if (!tagInput.isEmpty()) {
                for (String tag : tagInput.split(",")) {
                    tags.add(tag.trim());
                }
            }
            System.out.prinln(tags)

            
            
    
    
            System.out.println("is this the correct input" + dateInput + "Y/N")

            if(yn.isEqual("Y"))
                {
                    confirmed_tag = true;
                    // Add item to inventory
                    newItem.setTags(tags);
                    manager.addItem(newItem);
                `   System.out.println("✅ Item added: " + name);
                }
                else{}
        }    


    }
    
    
    

 
           
           



    */
