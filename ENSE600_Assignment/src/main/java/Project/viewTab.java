/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Project;

/**
 *
 * @author megan
 */
public class viewTab // to display the inventory and sort through the data
{
    // need to figure out the minimums 
    
    

    
    
    
    
    
    
    public static void printView(int screenWidth) 
    {
        Formatting.printBar();              // Top border (full length)
        Formatting.printBorder(3);        // Side borders
        Formatting.printCenteredMessage("View");       // Print tab name message
        Formatting.printBorder(2);        // Side borders
        Formatting.printSplitMessage("Default View:","x");
        
        
        int columnFlags = ITEM_BIT | NAME_BIT | CATEGORY_BIT |DATE_BIT |TOTAL_COST_BIT| QUANTITY_BIT; // only show item, name, and quantity
        decodeColumnFlags(columnFlags); // Sets all columns booleans accordingly
        
        // Determine column widths
        calculateColumnWidths();

        // Header
        printTableHeader();
        
        
        
        // Rows
        
        System.out.print("_____ VIEW ______ ");
        
        //int columnFlags = ITEM_BIT | NAME_BIT | QUANTITY_BIT; // only show item, name, and quantity

        
    }
    
    public static void printTableRow(int screenWidth) 
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
    
    // ----- Decode column byte flag ----- //
    public static void decodeColumnFlags(int flags)
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
        int totalPadding = activeColumns * (Formatting.padding * 2);

        // Configurable lengths 
        int currencyPresentationLength = 9; // ( $9999.00)
        int datePresentationLength = 8; // (01/01/25) // maybe date style can be changed in settings later
        int uniqueItemPresentationLength = 5; // 1 - 9999
        
        // Add in fixed width columns if enabled
        int reservedFixedWidths = 0;
        if (showItem)       reservedFixedWidths += uniqueItemPresentationLength;
        if (showCost)       reservedFixedWidths += currencyPresentationLength;
        if (showDate)       reservedFixedWidths += datePresentationLength;
        if (showTotalCost)  reservedFixedWidths += currencyPresentationLength;

        
        // Remaining space for flexible columns
        int flexibleColumns = 0;
        if (showName)       flexibleColumns++;
        if (showCategory)   flexibleColumns++;
        if (showQuantity)   flexibleColumns++;

        // Remaining space after padding and configurable column
        int remainingWidth = Formatting.screenWidth - totalPadding - reservedFixedWidths;

        // Base width per column
        int baseWidth = (flexibleColumns > 0) ? remainingWidth / flexibleColumns : 0;

        // Assign column widths
        if (showItem)       itemWidth = uniqueItemPresentationLength;
        if (showName)       nameWidth = baseWidth;
        if (showCategory)   categoryWidth = baseWidth;
        if (showQuantity)   quantityWidth = baseWidth;
        if (showCost)       costWidth = currencyPresentationLength; 
        if (showDate)       dateWidth = datePresentationLength; 
        if (showTotalCost)  totalCostWidth = currencyPresentationLength;
    }
     
    public static void printTableHeader() 
    {
        System.out.print("|");  // Start border

        if (showItem) 
        {
            System.out.printf("%" + Formatting.padding + "s%-" + itemWidth + "s%"  + "s|", "", "Item", "");
        }
        if (showName) 
        {
            System.out.printf("%" + Formatting.padding + "s%-" + nameWidth + "s%" + "s|", "", "Name", "");
        }
        if (showCategory) 
        {
            System.out.printf("%" + Formatting.padding + "s%-" + categoryWidth + "s%" + "s|", "", "Category", "");
        }
        if (showQuantity) 
        {
            System.out.printf("%" + Formatting.padding + "s%-" + quantityWidth + "s%" +  "s|", "", "Qty", "");
        }
        if (showCost) 
        {
            System.out.printf("%" + Formatting.padding + "s%-" + costWidth + "s%" +  "s|", "", "Cost", "");
        }
        if (showDate) 
        {
            System.out.printf("%" + Formatting.padding + "s%-" + dateWidth + "s%" + "s|", "", "Date", "");
        }
        if (showTotalCost) 
        {
            System.out.printf("%" + Formatting.padding + "s%-" + totalCostWidth + "s%" + "s|", "", "Sum Cost", "");
        }

        System.out.println(); // new line after the full row
    }
    
    
//    
//    public static void printTableHeader() 
//    {
//        System.out.print("|");  // Start border
//
//        if (showItem) 
//        {
//            System.out.printf("%" + Formatting.padding + "s%-" + itemWidth + "s%" + Formatting.padding + "s|", "", "Item", "");
//        }
//        if (showName) 
//        {
//            System.out.printf("%" + Formatting.padding + "s%-" + nameWidth + "s%" + Formatting.padding + "s|", "", "Name", "");
//        }
//        if (showCategory) 
//        {
//            System.out.printf("%" + Formatting.padding + "s%-" + categoryWidth + "s%" + Formatting.padding + "s|", "", "Category", "");
//        }
//        if (showQuantity) 
//        {
//            System.out.printf("%" + Formatting.padding + "s%-" + quantityWidth + "s%" + Formatting.padding + "s|", "", "Qty", "");
//        }
//        if (showCost) 
//        {
//            System.out.printf("%" + Formatting.padding + "s%-" + costWidth + "s%" + Formatting.padding + "s|", "", "Cost", "");
//        }
//        if (showDate) 
//        {
//            System.out.printf("%" + Formatting.padding + "s%-" + dateWidth + "s%" + Formatting.padding + "s|", "", "Date", "");
//        }
//        if (showTotalCost) 
//        {
//            System.out.printf("%" + Formatting.padding + "s%-" + totalCostWidth + "s%" + Formatting.padding + "s|", "", "Sum Cost", "");
//        }
//
//        System.out.println(); // new line after the full row
//    }
    
}

