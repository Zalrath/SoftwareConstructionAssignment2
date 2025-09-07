/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Project;

/**
 *
 * @author corin
 */

/*
ok there are 4 main areas for marking
    
    15% Labs (just here to make up the full 100)

    15% User Interface (CUI) - is pretty much is the CUI usable and easy to use 

    20% File I/O and Collections - is there more than 3 (so 4 or more) Read and Write functions it also has the Collections - 
        We have 2 of each (Read and Write) rn we need 4 of each for full marks
        Two kinds of collections (List, Map, Set) are properly applied to the programs. - I have used Hashmaps and an arraylist so we good for collections
    
    20% Software functionality and usability - is about runtime errors and bad user inputs, so being able to process any bad inputs  
    
    30% Software design & implementation - More classes and comments as well probs
        The project can be compiled without any errors. There are more than 9 reasonable classes (10+) with reasonable methods. 
        The relationships among the classes are well presented.
        
        All the OOP concepts are applied, including abstraction, encapsulation, inheritance, and polymorphism

        Comments of methods are given, and the codes are easy to read

*/



















import java.io.File;
import java.time.LocalDate;
//import java.time.LocalDateTime; could be useful if we want to track time of purchase as well as day 
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        InventoryManager manager = new InventoryManager();
        AddItemMenu addmenu = new AddItemMenu(manager);

        File itemFile = new File("items.txt");
        File logFile = new File("purchases.txt");

        if (itemFile.exists()) manager.loadItems("items.txt");
        if (logFile.exists()) manager.loadPurchases("purchases.txt");

        
  
        
        
        // added 
        // Item bread = new Item("Bread")   
        // Item milk = new Item("Milk 2L"); 
        // milk.addTag("breakfast");
        // bread.addTag("breakfast");
        // me messing around a bit
        
        //manager.addItem(milk);
        //manager.addItem(bread);
        
        //manager.logPurchase(milk.getUuid(),4.0,1, LocalDate.of(2025, 7, 25));
        
        
         //manager.logPurchase(bread.getUuid(),5.0,2, LocalDate.now());

        
        List<Item> toBuy = manager.getItemsToReplenish(LocalDate.of(2025, 8, 4));
        for (Item i : toBuy) {
            System.out.println("Needs restocking: " + i.getName());
        }

        manager.saveItems("items.txt");
        manager.savePurchases("purchases.txt");
        /*
        for (Item item : manager.getAllItems()) 
        {
            System.out.println("Item: " + item.getName());
            System.out.println("  UUID: " + item.getUuid());
            System.out.println("  Last Purchased: " + item.getLastPurchased());
            System.out.println("  Estimated Interval: " + item.getEstimatedIntervalDays() + " days");
            System.out.println("  Next Expected Purchase: " + item.getNextExpectedPurchase());
            System.out.println();
        }
        */
        
        
        
        // @author megan 
        // ----- Main Menu ----- // 
        Scanner input = new Scanner(System.in); // Scanner to take user input
       
 
        // Variables
        int screenState = 0; // Track screen state
        int menuSelected = 0;
        
        // Print homescreen
        Formatting.printWelcome();
        
        
        // Menu selection logic
        while (true) // Infinite loop until 'x' is entered
        {
            if (screenState == 0) // No menu selection has been made
            {
                String menuInput = input.nextLine().trim();     // Take user input and store it as menuInput
                
                if (menuInput.equalsIgnoreCase("x"))        // Quit condition -> prompt to save?
                {
                    System.out.println("Program terminated.");
                    System.exit(0);
                }
                
                // Try catch to validate user input
                try
                {
                    menuSelected = Integer.parseInt(menuInput); // Convert string to integer check for input
                    if (menuSelected >= 1 && menuSelected <= mainMenuInstructions.length - 1) // Skip 1st line prompt (message not instruction)
                    {
                       screenState = menuSelected; // If conversion successful change the screen state (valid selection made)
                       

                        //input.close();  // close scanner  ---- undid closing scanner as it interfered with the additem function
                       
                       
                    }
                    else // Invalid number was entered
                    {
                        System.out.printf("| Invalid option. Please select a number between 1 and %d:%n", (mainMenuInstructions.length - 1));
                        Formatting.printInputLine();
                        menuSelected = 0;
                    }
                }
                catch(NumberFormatException e) // Catch if anything but an integer or "x" was entered
                {
                    System.out.printf("| %s%n", "Invalid input. Please enter a number or 'x' to exit.");
                    
                    Formatting.printInputLine();
                    menuSelected = 0;
                }
                
                switch(menuSelected) // Switch case to handle tab changing using screenState
                {
                    case  1:  // View
                        screenState = 1;
                        //viewTab.printView(screenWidth);
                        viewTab.printView(manager);
                        break;        
                    case  2:  // Add items
                        screenState = 2;
                        // if problems comment all of this ---------------------
                            addmenu.additemMenu();
                        
                        // -----------------------------------------------------
                        break;
                        
                    case  3:  // Budget
                        screenState = 3;
                        
                        
                        
                        break;
                    case  4:  // Settings
                        screenState = 4;
                        break;    
                }
            }       
        }
        // ------ ------ //   
    } 
    
    static String[] mainMenuInstructions = 
    {
        "What would you like to do?",
        "1: View inventory",
        "2: Add new items",
        "3: Budget",
        "4: Settings"
    };
}