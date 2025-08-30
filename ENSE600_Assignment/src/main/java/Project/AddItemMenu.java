
package Project;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

/**
 *
 * @author corin
 */
public class AddItemMenu {
    
    
    private final Scanner itemInput = new Scanner(System.in);
    
    private final InventoryManager manager;

    public AddItemMenu(InventoryManager manager) {
        
        this.manager = manager;
    }
       
    /*
        MY PLANNING BOX - corin 
        
    
    
    
    
      */  
        
        
    public void addPurchaseFunc() {
        
        
        List<UUID> UUIDlist = new ArrayList<>();
        List<String> Namelist = new ArrayList<>();
        int index = 0;
        for (Item item : manager.getAllItems()) 
        {
            System.out.println("Index: " + index);
            System.out.println("Item: " + item.getName());
            //System.out.println("  UUID: " + item.getUuid());
            UUIDlist.add(item.getUuid());
            Namelist.add(item.getName());
            // System.out.println("  Last Purchased: " + item.getLastPurchased());
            //System.out.println("  Estimated Interval: " + item.getEstimatedIntervalDays() + " days");
            // System.out.println("  Next Expected Purchase: " + item.getNextExpectedPurchase());
            System.out.println();
            index++;
         }
        
        System.out.println("Which item do you want to log a Purchase(use the index number):");
        String Indexchoice = itemInput.nextLine();
        
        boolean carryOn = true;     // used for checking if the user wants to exit the add purchase process

        
        
        System.out.println("Is this the correct item? " + Namelist.get(Integer.parseInt(Indexchoice)) + " (Y/N),(X) to cancel adding item.");
        String yn = itemInput.nextLine().trim().toUpperCase();
        
        
        boolean YNX = true; // to loop to an input
        
        while(YNX){
            if (yn.equals("Y")) {
                YNX = false;
            } 
            if (yn.equals("N")) {
                System.out.println("Which item do you want to log a Purchase(use the index number):");
                Indexchoice = itemInput.nextLine();
            }
            if (yn.equals("X")) {
                YNX = false;
                carryOn = false;
            } 

            else {
                System.out.println("Invalid selection, Y to confirm, N to reinput and X to cancel.");           
            }
        }
        
        

        LocalDate date = null;
        Double Quantity = null;
        Double Price = null;
        
        if(carryOn){
            date = getConfirmedDate();
            if (date == null) {
                System.out.println("Cancelled adding purchase.");
                carryOn = false;
            }
        }
        
        if(carryOn){
            Price = getConfirmedPrice();
            if (Price == null) {
                System.out.println("Cancelled adding purchase.");
                carryOn = false;
            }
        }
        
        if(carryOn){
            Quantity = getConfirmedQuantity();
            if (Quantity == null) {
                System.out.println("Cancelled adding purchase.");
                carryOn = false;
            }
        }
        
        if(carryOn){ 
            manager.logPurchase(UUIDlist.get(Integer.parseInt(Indexchoice)),Price,Quantity, date);
        }
        
        
        /*
        LocalDate date = getConfirmedDate();
        Double Price = getConfirmedPrice();
        Double Quantity = getConfirmedQuantity();
        */
        
        
       

    }
    
    public Item addItemFunc() {
        /*
        String name = getConfirmedName();
        LocalDate date = getConfirmedDate();
        ArrayList<String> tags = getConfirmedTags();
        Double Price = getConfirmedPrice();
        Double Quantity = getConfirmedQuantity();
        */
        String name = getConfirmedName();
        if (name == null) { 
            System.out.println("Cancelled adding item.");
            return null;
        }

        LocalDate date = getConfirmedDate();
        if (date == null) {
            System.out.println("Cancelled adding item.");
            return null;
        }

        ArrayList<String> tags = getConfirmedTags();
        if (tags == null) {
            System.out.println("Cancelled adding item.");
            return null;
        }

        Double Price = getConfirmedPrice();
        if (Price == null) {
            System.out.println("Cancelled adding item.");
            return null;
        }

        Double Quantity = getConfirmedQuantity();
        if (Quantity == null) {
            System.out.println("Cancelled adding item.");
            return null;
        }
        
        
        Item newItem = new Item(name);
        newItem.setLastPurchased(date);
        newItem.setTags(tags);
        
        
        System.out.println("Item added: " + name);
        
        manager.addItem(newItem);
        
        manager.logPurchase(newItem.getUuid(),Price,Quantity, date);
        
        return newItem;
        
    }
    
    private Double getConfirmedPrice() {
        
        System.out.print("Input the Price of the item: ");
        String Price = itemInput.nextLine().trim();
        
      
        
        System.out.println("Is this correct? " + Price + " (Y/N),(X) to cancel adding item.");
        String yn = itemInput.nextLine().trim().toUpperCase();

        
        while(true){
            
            if (yn.equals("Y")) {
                return Double.valueOf(Price);
                
            }
            if (yn.equals("N")) {
                return getConfirmedPrice();
            }

            if (yn.equals("X")) {
                return null;
            } 
            else {
                System.out.println("Invalid selection, Y to confirm, N to reinput and X to cancel.");
            }
        }
        
        
    }
    
    private Double getConfirmedQuantity() {
        
        System.out.print("Input the Quantity of the item: ");
        String Quantity = itemInput.nextLine().trim();

        
        System.out.println("Is this correct? " + Quantity + " (Y/N),(X) to cancel adding item.");
        String yn = itemInput.nextLine().trim().toUpperCase();

       
        while(true){
            if (yn.equals("Y")) {
                return Double.valueOf(Quantity);
            } 
            if (yn.equals("N")) {
                return getConfirmedQuantity();
            }
            if (yn.equals("X")) {
                return null;
            } 

            else {
                System.out.println("Invalid selection, Y to confirm, N to reinput and X to cancel.");           
            }
        }
        
    }
    
    
    
    
    private String getConfirmedName() {
        
        System.out.print("Input the name of the item: ");
        String name = itemInput.nextLine().trim();

        
        System.out.println("Is this correct? " + name + " (Y/N), (X) to cancel adding item");
        String yn = itemInput.nextLine().trim().toUpperCase();

        
        while(true){
            if (yn.equals("Y")) {
                return name;

            }
            if (yn.equals("X")) {
                return null;
            } 
            if (yn.equals("N")) {
                return getConfirmedName(); 

            }
            else {
                System.out.println("Invalid selection, Y to confirm, N to reinput and X to cancel.");           
            }
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

        System.out.println("Is this correct? " + date + " (Y/N),(X) to cancel adding item.");
        String yn = itemInput.nextLine().trim().toUpperCase();
        
        
        while(true){
            if (yn.equals("Y")) {
                return date;
            } 
            if (yn.equals("N")) {
                return getConfirmedDate();
            }
            if (yn.equals("X")) {
                return null;
            } else {
                System.out.println("Invalid selection, Y to confirm, N to reinput and X to cancel.");
            }
        }
        
    }

    
    private ArrayList<String> getConfirmedTags() {
        System.out.print("Enter tags (separated by commas for multiple) or leave empty: ");
        String tagInput = itemInput.nextLine().trim();

        ArrayList<String> tags = new ArrayList<>();
        if (!tagInput.isEmpty()) {
            for (String tag : tagInput.split(",")) {
                tags.add(tag.trim());
            }
        }

        System.out.println("Tags entered: " + tags);
        System.out.println("Is this correct? (Y/N),(X) to cancel adding item.");
        String yn = itemInput.nextLine().trim().toUpperCase();
        
        
        while(true){
            if (yn.equals("Y")) {
                return tags;
            }
            if (yn.equals("N")) {
                return tags;
            } 
            if (yn.equals("X")) {
                return null;
            } 
            else {
                System.out.println("Invalid selection, Y to confirm, N to reinput and X to cancel.");
            }
        }
        
    }
    
    
    //////////////////////////////////////////////////////////////////////////
    
    // user input menu function
       
    public void additemMenu(){
    
        boolean addingItems = true; // continue the loop for adding items
        boolean Added = false; // checking if anything was added
        while(addingItems){
                        
            System.out.println("What would you like to do?");
            System.out.println("1: Add new item");
            System.out.println("2: Add purchase to existing Item");
            System.out.println("3: Home page");


            String choice = itemInput.nextLine();              
            switch (Integer.parseInt(choice)) {
                case 1 :                     
                    addItemFunc();                             
                    Added = true;
                break;
                case 2:
                    addPurchaseFunc();
                    Added = true;
                break;
                case 3:
                    addingItems = false;
                    //screenState = 0;
                break;
                default :
                    addingItems = false;
                    //screenState = 0;
                break;
            }

            if(Added){
                try {
                    
                    manager.saveItems("items.txt");
                    manager.savePurchases("purchases.txt");
                    
                    
                    System.out.println("Do you want to add more items/purchase? (Y/N)");
                    String yn = itemInput.nextLine().trim().toUpperCase();
                    if (yn.equals("Y")) {
                        
                    }
                    else {
                        
                    }
                } catch (IOException ex) {
                    System.getLogger(AddItemMenu.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
                }
            }
        
    
        }
    
    
    }

    
    
    
    
    
}
