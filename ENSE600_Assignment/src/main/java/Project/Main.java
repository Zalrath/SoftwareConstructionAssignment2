/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Project;

/**
 *
 * @author corin
 */
import java.io.File;
import java.time.LocalDate;
//import java.time.LocalDateTime; could be useful if we want to track time of purchase as well as day 
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        InventoryManager manager = new InventoryManager();

        File itemFile = new File("items.txt");
        File logFile = new File("purchases.txt");

        if (itemFile.exists()) manager.loadItems("items.txt");
        if (logFile.exists()) manager.loadPurchases("purchases.txt");

        // add capabilty for use to inpit names if logged items         
        // add capabilty to check for duplcut names
  
        
        // Item milk = new Item("Milk 2L"); 
        // me messing around a bit
        //manager.addItem(milk);
//        manager.logPurchase(milk.getUuid(), LocalDate.of(2025, 7, 1));
//        manager.logPurchase(milk.getUuid(), LocalDate.of(2025, 7, 8));
//        manager.logPurchase(milk.getUuid(), LocalDate.of(2025, 7, 25));
        
         // manager.logPurchase(milk.getUuid(), LocalDate.now());

        
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
        
        
        
        // megan 
        // ----- Main Menu ----- // 
        Scanner input = new Scanner(System.in); // Scanner to take user input
        int screenWidth = 160; // Maximum size for printing
 
        int screenState = 0; // Track screen state
        int menuSelected = 0;
        
        
        printWelcome(screenWidth);                  // Print homescreen
        while (true) // Infinite loop until 'x' is entered
        {
            if (screenState == 0) // No menu selection
            {
                String menuInput = input.next().trim();     // Take user input and store it as menuInput
                
                if (menuInput.equalsIgnoreCase("x")) // Quit condition -> prompt to save?
                {
                    System.out.println("Program terminated.");
                    System.exit(0);
                }
                // Try catch to validate input is an integer
                try
                {
                    menuSelected = Integer.parseInt(menuInput); // Convert string to integer check for input
                    if (menuSelected >= 1 && menuSelected <= mainMenuInstructions.length - 1) // Skip 1st line prompt
                    {
                       screenState = menuSelected; // If conversion successful change the screen state
                       input.close(); // close scanner 
                    }
                    else // Invalid number was entered
                    {
                        System.out.printf("| Invalid option. Please select a number between 1 and %d:%n", (mainMenuInstructions.length - 1));
                        //System.out.println("| Invalid option please select a number between 1 and " + (mainMenuInstructions.length - 1) + ":");
                        Formatting.printInputLine(padding);
                        menuSelected = 0;
                    }
                }
                catch(NumberFormatException e) // Catch if anything but an integer was inputted
                {
                    System.out.printf("| %s%n", "Invalid input. Please enter a number or 'x' to exit.");
                    //System.out.println("| Invalid input. Please enter a number or 'x' to exit.");
                    
                    Formatting.printInputLine(padding);
                    menuSelected = 0;
                    
                }   
                switch(menuSelected) // switch case to handle tab changing
                {
                    case  1:  // View
                        screenState = 1;
                        //viewTab.printView(screenWidth);
                        System.out.print("_____ VIEW ______ ");
                        break;        
                    case  2:  // Add items
                        screenState = 2;
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
    }
    
    public static void printWelcome(int screenWidth)
    {
        String welcomeMsg = "Welcome to the ___ manager";           // Welcome msg
        
        int workableWidth = screenWidth - 2;                        // Gap between the border lines
        int msgGap = (workableWidth - welcomeMsg.length()) / 2;     // Gap calculator to center the welcome message
        
        
        // --- Print screen --- //
        
        // Print welcome
        Formatting.printBar(screenWidth); // Top border (full length)
        Formatting.printBorder(3, screenWidth);    // Side borders
        System.out.printf("\n|%" + msgGap + "s%s%" + msgGap + "s|", "", welcomeMsg, ""); // Print welcome
        Formatting.printBorder(3, screenWidth);    // Side border
        
        // Print instructions  
        for (String msg : mainMenuInstructions) 
        {
            int trailing = workableWidth - padding - msg.length();
            System.out.printf("\n|%" + padding + "s%s%" + trailing + "s|", "", msg, "");
        }
        // New line to take input
        System.out.printf("\n|%" + padding + "s", "");
    }
    
    static String[] mainMenuInstructions = 
    {
    "What would you like to do?",
    "1: View inventory",
    "2: Add new items",
    "3: Budget",
    "4: Settings"
    };
    
    static int padding = 1; // Padding gap
}