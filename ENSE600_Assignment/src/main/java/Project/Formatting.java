/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Project;

import static Project.Main.mainMenuInstructions;

/**
 *
 * @author megan
 */
public class Formatting 
{
    static int padding = 1; // Padding gap between the bar | -> text
    
    
    
    static int screenWidth = 160;               // Set width for printing -> MINIMUM = 80!!!
    static int workableWidth = screenWidth - 2; // Useable space between the border bars |->workable width<-|
    

    // Prints a horizontal bar the full length of the screensize
    // -----------------------
    public static void printBar()
    {
        System.out.print("\n ");                // Gap to align the bar with the verticle border
        for(int i = 0; i < workableWidth; i++)
        {
            System.out.print("_");
        }
    }

    // Prints the edge borders with empty space
    // |                       |
    public static void printBorder(int row)
    {
        for (int i = 0; i < row; i++)
        {
            System.out.printf("\n|%-" + workableWidth + "s|", "");
        }
    }
    
    // Prints the edge borders with a line
    // |------------------------|
    public static void printBorderBar() 
    {
        System.out.print("|"); 
        for(int i = 0; i < workableWidth; i++)
        {
            System.out.print("-");
        }
        System.out.print("|"); 
    }
    
    
    
    // Prints the left edge border with a gap to take input
    // | input
    public static void printInputLine()
    {
        System.out.printf("\n| %" + "s", "");
    }

    // Prints a message centred accross the screen
    // |      msg      |
    public static void printCenteredMessage(String msg) 
    {
        int msgGap = (workableWidth - msg.length()) / 2;
        int trailing = workableWidth - msg.length() - msgGap; // In case of odd spacing

        System.out.printf("%n|%" + msgGap + "s%s%" + trailing + "s|", "", msg, "");
    }
    
    // Print left aligned message
    // | msg          |
    public static void printLeftAlignedMessage(String msg) 
    {
        int trailing = workableWidth - msg.length(); // Fill the rest with spaces
        System.out.printf("%n| %s%" + (trailing - 1) + "s|", msg, "");
    }
    
    // Prints a message alligned to the outside of the borders
    // | left              right |
    public static void printSplitMessage(String left, String right) 
    {
        int spacing = workableWidth - padding * 2 - left.length() - right.length();

        // Ensure spacing is not negative
        spacing = Math.max(spacing, 0);

        System.out.printf("\n|%" + padding + "s%s%" + spacing + "s%s%" + padding + "s|%n", "", left, "", right, "");
    }
   
    
    // Prints the welcome message
    public static void printWelcome()
    {
        String welcomeMsg = "Welcome to the ___ manager";           // Welcome msg so we can easily change it once we have a name

        // --- Print screen --- //
        
        // Print welcome
        Formatting.printBar();            // Top border (full length)
        Formatting.printBorder(3);        // Side borders
        printCenteredMessage(welcomeMsg); // Print welcome message
        Formatting.printBorder(3);        // Side border
        
        // Print instructions  
        for (String msg : mainMenuInstructions) 
        {
            int trailing = workableWidth - Formatting.padding - msg.length();
            System.out.printf("\n|%" + Formatting.padding + "s%s%" + trailing + "s|", "", msg, "");
        }
        // New line to take input
        // System.out.printf("\n|%" + Formatting.padding + "s", "");
       Formatting.printInputLine();
    }
    
    
    
}
