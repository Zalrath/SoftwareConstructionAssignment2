/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Project;

/**
 *
 * @author megan
 */

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;


public class viewTab // to display the inventory and sort through the data
{
    // need to figure out the minimums 
    
    
    

    
    
    
    
    
    
    public static void printView(InventoryManager manager)
    {
        Formatting.printBar();                             // Top border (full length)
        Formatting.printBorder(3);                         // Side borders
        Formatting.printCenteredMessage("View");           // Print tab name message
        Formatting.printBorder(2);                         // Side borders
        Formatting.printSplitMessage("Default View:","x"); // Print split text info
        
        
        
        int defaultColumnFlags = ITEM_BIT | NAME_BIT | CATEGORY_BIT | QUANTITY_BIT | COST_BIT |DATE_BIT |TOTAL_COST_BIT; // only show item, name, and quantity
        decodeDefaultColumnFlags(defaultColumnFlags); // Sets all columns booleans accordingly (excluding tags as a column)
        
        // Determine column widths
        calculateColumnWidths();

        // Header
        printTableHeader();
        
        List<Item> allItems = new ArrayList<>(manager.getAllItems());
        printTableRows(allItems, manager);

        
    }
    
    
    public static void printTableHeader() 
    {
        System.out.print("|");  // Start border

        if (showItem) 
        {
            printColumn("Item", itemWidth);
        }
        
        if (showName) 
        {
            printColumn("Name", nameWidth);
        }
        
        if (showCategory) 
        {
            printColumn("Category", categoryWidth);
        }
        
        if (showQuantity) 
        {
            printColumn("Qty", quantityWidth);
        }
        
        if (showCost) 
        {
            printColumn("Cost", costWidth);
        }
        
        if (showDate) 
        {
            printColumn("Date", dateWidth);
        }
        
        if (showTotalCost) 
        {
            printColumn("Sum Cost", totalCostWidth);
        }

        System.out.println(); // Move to the next line

        // print underline
        System.out.print("|");
        if (showItem) 
        {
            printUnderline(itemWidth);
        }
        
        if (showName) 
        {
            printUnderline(nameWidth);
        }
        
        if (showCategory) 
        {
            printUnderline(categoryWidth);
        }
        
        if (showQuantity) 
        {
            printUnderline(quantityWidth);
        }
        
        if (showCost) 
        {
            printUnderline(costWidth);
        }
        
        if (showDate) 
        {
            printUnderline(dateWidth);
        }
        
        if (showTotalCost) 
        {
            printUnderline(totalCostWidth);
        }
        
        System.out.println();
    }
    
    private static void printColumn(String title, int width) 
    {
        // int totalWidth = Formatting.padding * 2 + width;
        String padded = String.format("%" + Formatting.padding + "s%-" + width + "s%" + "s", "", title, "");
        System.out.printf("%s|", padded);
    }

    private static void printUnderline(int width) 
    {
        int totalWidth = Formatting.padding + width;
        String line = "-".repeat(totalWidth);
        System.out.printf("%s|", line);
    }
    
    private static void printCell(String text, int width) {
        String padded = String.format("%" + Formatting.padding + "s%-" + width + "s", "", text);

        System.out.printf("%s|", padded);
    }
    
    
    private static int getItemIndex(Item item, InventoryManager manager) {
        int i = 1;
        for (Item it : manager.getAllItems()) {
            if (it.getUuid().equals(item.getUuid())) {
                return i;
            }
            i++;
        }
        return -1;
    }
   
    public static void printTableRows(Collection<Item> items, InventoryManager manager) {
        for (Item item : items) {
            System.out.print("|");

            if (showItem) {
                printCell(String.valueOf(getItemIndex(item, manager)), itemWidth);
            }

            if (showName) {
                printCell(item.getName(), nameWidth);
            }

            if (showCategory) {
                printCell(String.join("|", item.getTags()), categoryWidth);
            }

            if (showQuantity) {
                double quantity = manager.getLatestQuantity(item.getUuid());
                printCell(String.format("%.0f", quantity), quantityWidth);
            }

            if (showCost) {
                double cost = manager.getLatestPrice(item.getUuid());
                printCell(String.format("$%.2f", cost), costWidth);
            }

            if (showDate) {
                String formattedDate = item.getLastPurchased().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yy"));
                printCell(formattedDate, dateWidth);
            }

            if (showTotalCost) {
                double total = manager.getTotalSpent(item.getUuid());
                printCell(String.format("$%.2f", total), totalCostWidth);
            }

            System.out.println();
        }
    }
    
    
    
    // ----- Decode column byte flag ----- //
   public static void decodeDefaultColumnFlags(int flags) 
   {
       showItem       = (flags & ITEM_BIT)       != 0;
       showName       = (flags & NAME_BIT)       != 0;
       showCategory   = (flags & CATEGORY_BIT)   != 0;
       showQuantity   = (flags & QUANTITY_BIT)   != 0;
       showCost       = (flags & COST_BIT)       != 0;
       showDate       = (flags & DATE_BIT)       != 0;
       showTotalCost  = (flags & TOTAL_COST_BIT) != 0;
    }
    
    // ----- Column enable booleans ----- // 
    static boolean showItem;
    static boolean showName;
    static boolean showCategory;
    static boolean showQuantity;
    static boolean showCost;
    static boolean showDate;
    static boolean showTotalCost;
    
    // ----- Defining column bit position flags ----- //
    static final int ITEM_BIT       = 1 << 0;
    static final int NAME_BIT       = 1 << 1;
    static final int CATEGORY_BIT   = 1 << 2;
    static final int QUANTITY_BIT   = 1 << 3;
    static final int COST_BIT       = 1 << 4;
    static final int DATE_BIT       = 1 << 5;
    static final int TOTAL_COST_BIT = 1 << 6;
    
    // ----- Column Widths ----- // 
    static int itemWidth;
    static int nameWidth;
    static int categoryWidth;
    static int quantityWidth;
    static int costWidth;
    static int dateWidth;
    static int totalCostWidth;
    
    
    
    
    
    
    public static void calculateColumnWidths() 
    {
        // Count how many columns are shown
        int activeColumns = 0;
        if (showItem)       activeColumns++;
        if (showName)       activeColumns++;
        if (showCategory)   activeColumns++;
        if (showQuantity)   activeColumns++;
        if (showCost)       activeColumns++;
        if (showDate)       activeColumns++;
        if (showTotalCost)  activeColumns++;

        if (activeColumns == 0) return; // nothing to show

        // Calculate total padding space between columns
        int totalPadding = (activeColumns - 1) + (Formatting.padding * activeColumns);

        // Configurable lengths 
        int currencyPresentationLength = 9; // ( $9999.00)
        int datePresentationLength = 8; // (01/01/25) // maybe date style can be changed in settings later
        int uniqueItemPresentationLength = 5; // 1 - 9999
        int maxItemQuantity = 4; // 999
        
        // Add in fixed width columns if enabled
        int reservedFixedWidths = 0;
        if (showItem)       reservedFixedWidths += uniqueItemPresentationLength;
        if (showCost)       reservedFixedWidths += currencyPresentationLength;
        if (showDate)       reservedFixedWidths += datePresentationLength;
        if (showTotalCost)  reservedFixedWidths += currencyPresentationLength;
        if (showQuantity)   reservedFixedWidths += maxItemQuantity;

        
        // Remaining space for flexible columns
        int flexibleColumns = 0;
        if (showName)       flexibleColumns++;
        if (showCategory)   flexibleColumns++;

        // Remaining space after padding and configurable column
        int remainingWidth = Formatting.workableWidth - totalPadding - reservedFixedWidths;

        // Base width per column
        int baseWidth = (flexibleColumns > 0) ? remainingWidth / flexibleColumns : 0;

        // Assign column widths
        if (showItem)       itemWidth = uniqueItemPresentationLength;
        if (showName)       nameWidth = baseWidth;
        if (showCategory)   categoryWidth = baseWidth;
        if (showQuantity)   quantityWidth = maxItemQuantity;
        if (showCost)       costWidth = currencyPresentationLength; 
        if (showDate)       dateWidth = datePresentationLength; 
        if (showTotalCost)  totalCostWidth = currencyPresentationLength;
    }
     
}
