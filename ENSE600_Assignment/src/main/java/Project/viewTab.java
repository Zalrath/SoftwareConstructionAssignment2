/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Project;

/**
 *
 * @author megan
 */
public class viewTab 
{
    
    // Column enable booleans
    boolean showItem = true;
    boolean showName = true;
    boolean showCategory = true;
    boolean showQuantity = true;
    boolean showCost = true;
    boolean showDate = true;
    boolean showTotalCost = true;
    
    
    public static void printView(int screenWidth) 
    {
        printBar(screenWidth); 
        printRow(screenWidth);       
    }
    
    public static void printRow(int screenWidth) 
    {
        int itemWidth = 6;
        int quantityWidth = 10;
        int costWidth = 5;
        int pipeCount = 7; // |Item|Name|Quantity|Cost|
        int pipeWidth = pipeCount; // each pipe takes 1 character, no added spaces

        // remaining space is for "Name" column
        int nameWidth = screenWidth - (itemWidth + quantityWidth + costWidth + pipeWidth) +1;

        // Header row with no extra spacing
        System.out.printf("\n|%-"+itemWidth+"s|%-"+nameWidth+"s|%-"+quantityWidth+"s|%-"+costWidth+"s|",
        " Item ", " Name ", " Quantity ", " Cost ");
    }
    
    public static void printBar(int screenWidth)
    {
        for(int i = 0; i < screenWidth; i++)
        {
            System.out.print("_");
        }
    }
    
}

