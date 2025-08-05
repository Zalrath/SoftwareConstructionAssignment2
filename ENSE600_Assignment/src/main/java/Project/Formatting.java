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
    static int padding = 1; // Padding gap
    static int screenWidth = 160; // Set width for printing -> need to determine minimum
    static int workableWidth = screenWidth - 2;                        // Gap between the border lines
    
    public static void printBorder(int row)
    {
        for (int i = 0; i < row; i++)
        {
            System.out.printf("\n|%-" + workableWidth + "s|", "");
        }
    }
    
    public static void printBar()
    {
        System.out.print("\n ");
        for(int i = 0; i < workableWidth; i++)
        {
            System.out.print("_");
        }
    }
    
    public static void printInputLine()
    {
        System.out.printf("|%" + padding + "s", "");
    }

    public static void printCenteredMessage(String msg) 
    {
        int msgGap = (workableWidth - msg.length()) / 2;
        int trailing = workableWidth - msg.length() - msgGap; // In case of odd spacing

        System.out.printf("%n|%" + msgGap + "s%s%" + trailing + "s|", "", msg, "");
    }
    
    public static void printSplitMessage(String left, String right) 
    {
        int spacing = workableWidth - padding * 2 - left.length() - right.length();

        // Ensure spacing is not negative
        spacing = Math.max(spacing, 0);

        System.out.printf("\n|%" + padding + "s%s%" + spacing + "s%s%" + padding + "s|%n", "", left, "", right, "");
    }
   
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
        System.out.printf("\n|%" + Formatting.padding + "s", "");
    }
    
    
    
}
