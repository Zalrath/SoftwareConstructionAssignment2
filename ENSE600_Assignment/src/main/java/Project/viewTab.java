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
import java.util.UUID;
import java.util.*;
import java.time.format.DateTimeFormatter;



public class viewTab // to display the inventory and sort through the data
{
    // ----- Defining column bit position flags ----- //
    static final int ITEM_BIT       = 1 << 0;
    static final int NAME_BIT       = 1 << 1;
    static final int CATEGORY_BIT   = 1 << 2;
    static final int QUANTITY_BIT   = 1 << 3;
    static final int COST_BIT       = 1 << 4;
    static final int DATE_BIT       = 1 << 5;
    static final int TOTAL_COST_BIT = 1 << 6;
    
    // ----- Column enable booleans ----- // 
    static boolean showItem;
    static boolean showName;
    static boolean showCategory;
    static boolean showQuantity;
    static boolean showCost;
    static boolean showDate;
    static boolean showTotalCost;
    
    // ----- Column Widths ----- // 
    static int itemWidth;
    static int nameWidth;
    static int categoryWidth;
    static int quantityWidth;
    static int costWidth;
    static int dateWidth;
    static int totalCostWidth;
    
    // ----- viewTab menu options ----- // 
    private enum ViewMenuOption 
    {
        EDIT_VIEW, // change table columns
        FILTER,    // filter by specific tags
        SORT,      // sort items
        HOME,      // go back to home screen
        EXIT       // save and quit 
    }
    
    // ----- Print View Main Method ----- //
    public static void printView(InventoryManager manager)
    {
        Formatting.printBar();                             // Top border (full length)
        Formatting.printBorder(3);                         // Side borders
        Formatting.printCenteredMessage("View");           // Print tab name message
        Formatting.printBorder(2);                         // Side borders
        Formatting.printSplitMessage("Default View:","x"); // Print split text info
        
        
        int defaultColumnFlags = ITEM_BIT | NAME_BIT | CATEGORY_BIT | QUANTITY_BIT | COST_BIT | DATE_BIT | TOTAL_COST_BIT;
        decodeDefaultColumnFlags(defaultColumnFlags);      // Sets all columns booleans accordingly
        
        // Determine column widths
        calculateColumnWidths();
        
        // Header
        printTableHeader();
        
        // Rows
        List<Item> allItems = new ArrayList<>(manager.getAllItems());
        printTableRows(allItems, manager);
        printDashedRow();
        
        // Totals
        printColumnTotals(allItems, manager);
        Formatting.printBorderBar();
        
        // MENU PROMPTING HERE
        viewMenuLoop(manager, allItems);
        // edit view
        // filter
        // home screen
        
        
    }
    
    // scanner
    private static final java.util.Scanner VIEW_SCANNER = new java.util.Scanner(System.in);
    
    // ----- Menu Loop ----- // 
    private static void viewMenuLoop(InventoryManager manager, List<Item> currentItems) 
    {
        while (true) 
        {
            ViewMenuOption option = promptViewMenu();
            switch (option) 
            {
                case EDIT_VIEW -> 
                {
                    handleEditView(manager);
                    // Re-render view after changes
                    printUpdatedView(manager, currentItems);
                }
                case FILTER -> 
                {
                    currentItems = handleFilter(manager); // returns filtered list
                    printUpdatedView(manager, currentItems);
                }
                case SORT ->
                {
                    currentItems = handleSort(manager, currentItems); // returns sorted list
                    printUpdatedView(manager, currentItems);
                }
                case HOME -> 
                {
                    // Return to caller (Main screen)
                    return;
                }
                case EXIT -> 
                {
                    Formatting.printLeftAlignedMessage("Saving & Quitting");
                    System.exit(0);
                }
            }
        }
    }
    
    // ----- viewTab menu prompts ----- //
    private static ViewMenuOption promptViewMenu() {
        Formatting.printLeftAlignedMessage("1: Edit view (toggle columns)");
        Formatting.printLeftAlignedMessage("2: Filter items by category");
        Formatting.printLeftAlignedMessage("3: Sort view");
        Formatting.printLeftAlignedMessage("4: Return to Home");
        Formatting.printLeftAlignedMessage("X: Exit");
        Formatting.printInputLine();
        
        String input = VIEW_SCANNER.nextLine().trim();
        
        // Allow both numeric and letter choices
        if (input.equalsIgnoreCase("x")) 
        {
            return ViewMenuOption.EXIT;
        }
        
        try 
        {
            int n = Integer.parseInt(input);
            return switch (n) 
            {
                case 1 ->
                    ViewMenuOption.EDIT_VIEW;
                case 2 ->
                    ViewMenuOption.FILTER;
                case 3 ->
                    ViewMenuOption.SORT;
                case 4 ->
                    ViewMenuOption.HOME;
                default -> 
                {
                    Formatting.printLeftAlignedMessage("Invalid choice. Please try again.");
                    yield promptViewMenu(); // re-prompt
                }
            };
        }
        
        catch (NumberFormatException ex) 
        {
            Formatting.printLeftAlignedMessage("Invalid input. Please enter 1, 2, 3, 4 or X.");
            return promptViewMenu(); // re-prompt
        }
    }
    
    //----- Print updated view ----- // 
    private static void printUpdatedView(InventoryManager manager, List<Item> items) 
    {
        
        
        Formatting.printBar();                             // Top border (full length)
        Formatting.printBorder(3);                         // Side borders
        
        Formatting.printCenteredMessage("View (Updated)");
        Formatting.printBorder(2);                         // Side borders
        System.out.println("");
        Formatting.printBorderBar();
        
        calculateColumnWidths();
        System.out.println("");
        printTableHeader();
        printTableRows(items, manager);
        printDashedRow();
        printColumnTotals(items, manager);
        Formatting.printBorderBar();
    }
    
    // ----- Toggle column visibility ----- // 
    private static void handleEditView(InventoryManager manager) 
    {
        while (true) 
        {
            // top menu block output
            Formatting.printBar();
            Formatting.printBorder(1);
            Formatting.printCenteredMessage("Edit View (Toggle Columns)");
            Formatting.printBorder(1);
            System.out.println("");
            Formatting.printBorderBar();
            
            
            // show current selection toggles and edit
            Formatting.printLeftAlignedMessage("1: Item ID      [" + (showItem ? "ON " : "OFF") + "]");
            Formatting.printLeftAlignedMessage("2: Name         [" + (showName ? "ON " : "OFF") + "]");
            Formatting.printLeftAlignedMessage("3: Catagory     [" + (showCategory ? "ON " : "OFF") + "]");
            Formatting.printLeftAlignedMessage("4: Quantity     [" + (showQuantity ? "ON " : "OFF") + "]");
            Formatting.printLeftAlignedMessage("5: Cost         [" + (showCost ? "ON " : "OFF") + "]");
            Formatting.printLeftAlignedMessage("6: Date         [" + (showDate ? "ON " : "OFF") + "]");
            Formatting.printLeftAlignedMessage("7: Sum Cost     [" + (showTotalCost ? "ON " : "OFF") + "]");
            Formatting.printLeftAlignedMessage("B: Back");
            Formatting.printBorder(1);
            Formatting.printLeftAlignedMessage("Which column would you like to toggle?");
            Formatting.printInputLine();
            
            String in = VIEW_SCANNER.nextLine().trim();
            if (in.equalsIgnoreCase("b")) 
            {
                break;
            }
            
            switch (in) {
                case "1" ->
                    showItem = !showItem;
                case "2" ->
                    showName = !showName;
                case "3" ->
                    showCategory = !showCategory;
                case "4" ->
                    showQuantity = !showQuantity;
                case "5" ->
                    showCost = !showCost;
                case "6" ->
                    showDate = !showDate;
                case "7" ->
                    showTotalCost = !showTotalCost;
                default ->
                    Formatting.printLeftAlignedMessage("Invalid choice.");
            }
        }
    }
    
    private static List<Item> handleFilter(InventoryManager manager) {
        // all items
        List<Item> base = new ArrayList<>(manager.getAllItems());
        
        // create a list of all the unique tags
        Set<String> unique = new HashSet<>();
        for (Item it : base) 
        {
            ArrayList<String> tags = (it.getTags() == null) ? new ArrayList<>() : it.getTags();
            for (String t : tags) 
            {
                if (t != null && !t.isBlank()) 
                {
                    unique.add(t.trim());
                }
            }
        }
        List<String> categories = new ArrayList<>(unique);
        categories.sort(String.CASE_INSENSITIVE_ORDER);
        
        // no tags found - show all items
        if (categories.isEmpty()) {
            Formatting.printBar();
            Formatting.printBorder(2);
            Formatting.printCenteredMessage("Filter Items");
            Formatting.printBorder(2);
            Formatting.printLeftAlignedMessage("No categories found. Showing all items.");
            return base;
        }
        
        // ----- formatting setup -----
        final int indexWidth = String.valueOf(categories.size()).length();
        // length of the longest tag
        int catWidth = 0;
        for (String c : categories) 
        {
            int len = (c == null) ? 0 : c.length();
            if (len > catWidth) {
                catWidth = len;
            }
        }
        // presentation formatting
        final String rowFmt = "%" + indexWidth + "d: %-" + catWidth + "s [%s]";
        
        // default selection is all off
        Set<String> selected = new HashSet<>();
        
        // ----- Toggle loop -----
        while (true) {
            // header
            Formatting.printBar();
            Formatting.printBorder(2);
            Formatting.printCenteredMessage("Filter Items");
            Formatting.printBorder(2);
            Formatting.printLeftAlignedMessage("Select Category to filter by (toggle 1-" + categories.size() + "):");
            
            // present tag options n state
            for (int i = 0; i < categories.size(); i++) 
            {
                String cat = categories.get(i);
                boolean on = selected.contains(cat);
                String line = String.format(rowFmt, i + 1, (cat == null ? "" : cat), on ? "ON" : "OFF");
                Formatting.printLeftAlignedMessage(line);
            }
            
            // options
            Formatting.printBorder(1);
            Formatting.printLeftAlignedMessage("A: Apply filter   |  C: Clear all   |  O: Turn all ON   |  B: Back (apply)");
            Formatting.printInputLine();
            String input = VIEW_SCANNER.nextLine().trim();
            
            // selection for options
            if (input.equalsIgnoreCase("A") || input.equalsIgnoreCase("B")) 
            {
                break; // apply current selection
            }
            else if (input.equalsIgnoreCase("C")) 
            {
                selected.clear();
                continue;
            }
            else if (input.equalsIgnoreCase("O")) 
            {
                selected.clear();
                selected.addAll(categories);
                continue;
            }
            
            // toggles
            try {
                int idx = Integer.parseInt(input);
                if (idx >= 1 && idx <= categories.size()) 
                {
                    String chosen = categories.get(idx - 1);
                    if (selected.contains(chosen)) {
                        selected.remove(chosen);
                    } else 
                    {
                        selected.add(chosen);
                    }
                }
                else 
                {
                    Formatting.printLeftAlignedMessage("Invalid number. Please choose 1-" + categories.size() + ", or A/C/O/B.");
                }
            }
            catch (NumberFormatException e) 
            {
                Formatting.printLeftAlignedMessage("Invalid input. Use 1-" + categories.size() + ", or A/C/O/B.");
            }
        }
        
        // apply the filter with all showing if no tags were selected
        if (selected.isEmpty()) 
        {
            Formatting.printLeftAlignedMessage("No categories selected. Showing all items.");
            return base;
        }
        
        List<Item> filtered = new ArrayList<>();
        for (Item it : base) 
        {
            ArrayList<String> tags = (it.getTags() == null) ? new ArrayList<>() : it.getTags();
            boolean match = false;
            for (String t : tags)
            {
                if (t != null && selected.contains(t.trim())) 
                {
                    match = true;
                    break;
                }
            }
            if (match) 
            {
                filtered.add(it);
            }
        }
        
        Formatting.printLeftAlignedMessage(String.format("Matched %d of %d items.", filtered.size(), base.size()));
        return filtered;
    }
    
    // ----- Sorting handler ----- //
    private static List<Item> handleSort(InventoryManager manager, List<Item> items) 
    {
        if (items == null) 
        {
            items = new ArrayList<>();
        }
        List<Item> working = new ArrayList<>(items); 
        
        // ----- check which column t o sort ----- // 
        Formatting.printBar();
        Formatting.printBorder(1);
        Formatting.printCenteredMessage("Sort View");
        Formatting.printBorder(1);
        Formatting.printLeftAlignedMessage("Choose a column to sort by:");
        int optIdx = 1;
        if (showName) 
        {
            Formatting.printLeftAlignedMessage((optIdx++) + ": Name");
        }
        if (showCategory) 
        {
            Formatting.printLeftAlignedMessage((optIdx++) + ": Category");
        }
        if (showQuantity) 
        {
            Formatting.printLeftAlignedMessage((optIdx++) + ": Quantity");
        }
        if (showCost) 
        {
            Formatting.printLeftAlignedMessage((optIdx++) + ": Cost");
        }
        if (showDate) 
        {
            Formatting.printLeftAlignedMessage((optIdx++) + ": Date");
        }
        if (showTotalCost) 
        {
            Formatting.printLeftAlignedMessage((optIdx++) + ": Sum Cost");
        }
        Formatting.printLeftAlignedMessage("B: Back");
        Formatting.printInputLine();
        
        String colInput = VIEW_SCANNER.nextLine().trim();
        if (colInput.equalsIgnoreCase("b")) 
        {
            return working;
        }
        
        // lists
        List<String> available = new ArrayList<>();
        if (showName) 
        {
            available.add("NAME");
        }
        if (showCategory) 
        {
            available.add("CATEGORY");
        }
        if (showQuantity) 
        {
            available.add("QUANTITY");
        }
        if (showCost) 
        {
            available.add("COST");
        }
        if (showDate) 
        {
            available.add("DATE");
        }
        if (showTotalCost) 
        {
            available.add("TOTAL");
        }
        
        int chosenIndex;
        try 
        {
            chosenIndex = Integer.parseInt(colInput);
        } 
        catch (NumberFormatException e) 
        {
            Formatting.printLeftAlignedMessage("Invalid input. Sort cancelled.");
            return working;
        }
        if (chosenIndex < 1 || chosenIndex > available.size()) 
        {
            Formatting.printLeftAlignedMessage("Invalid choice. Sort cancelled.");
            return working;
        }
        String column = available.get(chosenIndex - 1);
        
        // ----- ask sort up or down -----
        Formatting.printLeftAlignedMessage("Direction:");
        Formatting.printLeftAlignedMessage("1: Ascending");
        Formatting.printLeftAlignedMessage("2: Descending");
        Formatting.printInputLine();
        String dirInput = VIEW_SCANNER.nextLine().trim();
        boolean ascending = !"2".equals(dirInput); // default to ascending
        
        
        // ----- Build comparers  -----
        Map<String, Comparator<Item>> comparators = new HashMap<>();
        comparators.put("NAME",
                Comparator.comparing(it -> it.getName().toLowerCase(), String::compareToIgnoreCase)
        );
        comparators.put("CATEGORY",
                Comparator.comparing(it -> String.join("|", it.getTags()), String::compareToIgnoreCase)
        );
        comparators.put("QUANTITY",
                Comparator.comparingDouble(it -> manager.getLatestQuantity(it.getUuid()))
        );
        comparators.put("COST",
                Comparator.comparingDouble(it -> manager.getLatestPrice(it.getUuid()))
        );
        comparators.put("DATE",
                Comparator.comparing(Item::getLastPurchased) // assumes never null
        );
        comparators.put("TOTAL",
                Comparator.comparingDouble(it -> manager.getTotalSpent(it.getUuid()))
        );

        // ----- Lookup ----- // 
        Comparator<Item> cmp = comparators.get(column);
        if (cmp == null) 
        {
            Formatting.printLeftAlignedMessage("Unknown column. Sort cancelled.");
            return working;
        }
        if (!ascending) 
        {
            cmp = cmp.reversed();
        }
        
        working.sort(cmp);
        Formatting.printLeftAlignedMessage("Sorted by " + column + (ascending ? " (ASC)" : " (DESC)"));
        return working;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    public static void printTableHeader()
    {
        System.out.print("|");
        
        if (showItem)       printColumn("Item", itemWidth);
        if (showName)       printColumn("Name", nameWidth);
        if (showCategory)   printColumn("Category", categoryWidth);
        if (showQuantity)   printColumn("Qty", quantityWidth);
        if (showCost)       printColumn("Cost", costWidth);
        if (showDate)       printColumn("Date", dateWidth);
        if (showTotalCost)  printColumn("Sum Cost", totalCostWidth);
        
        System.out.println();
        
        System.out.print("|");
        
        if (showItem)       printUnderline(itemWidth);
        if (showName)       printUnderline(nameWidth);
        if (showCategory)   printUnderline(categoryWidth);
        if (showQuantity)   printUnderline(quantityWidth);
        if (showCost)       printUnderline(costWidth);
        if (showDate)       printUnderline(dateWidth);
        if (showTotalCost)  printUnderline(totalCostWidth);
        
        System.out.println();
    }
    
    public static void printTableRows(Collection<Item> items, InventoryManager manager)
    {
        for (Item item : items)
        {
            System.out.print("|");
            
            if (showItem)
            {
                printCell(String.valueOf(getItemIndex(item, manager)), itemWidth);
            }
            
            if (showName)
            {
                printCell(item.getName(), nameWidth);
            }
            
            if (showCategory)
            {
                printCell(String.join("|", item.getTags()), categoryWidth);
            }
            
            if (showQuantity)
            {
                double quantity = manager.getLatestQuantity(item.getUuid());
                printCell(String.format("%.0f", quantity), quantityWidth);
            }
            
            if (showCost)
            {
                double cost = manager.getLatestPrice(item.getUuid());
                printCell(String.format("$%.2f", cost), costWidth);
            }
            
            if (showDate)
            {
                String formattedDate = item.getLastPurchased().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yy"));
                printCell(formattedDate, dateWidth);
            }
            
            if (showTotalCost)
            {
                double total = manager.getTotalSpent(item.getUuid());
                printCell(String.format("$%.2f", total), totalCostWidth);
            }
            
            System.out.println();
        }
    }
    
    private static void printEmptyRow()
    {
        System.out.print("|");
        
        if (showItem)       printCell("", itemWidth);
        if (showName)       printCell("", nameWidth);
        if (showCategory)   printCell("", categoryWidth);
        if (showQuantity)   printCell("", quantityWidth);
        if (showCost)       printCell("", costWidth);
        if (showDate)       printCell("", dateWidth);
        if (showTotalCost)  printCell("", totalCostWidth);
        
    }
    
    private static void printDashedRow()
    {
        System.out.print("|");
        
        if (showItem)       printUnderline(itemWidth);
        if (showName)       printUnderline(nameWidth);
        if (showCategory)   printUnderline(categoryWidth);
        if (showQuantity)   printUnderline(quantityWidth);
        if (showCost)       printUnderline(costWidth);
        if (showDate)       printUnderline(dateWidth);
        if (showTotalCost)  printUnderline(totalCostWidth);
    }
    
    private static void printColumn(String title, int width)
    {
        String padded = String.format("%" + Formatting.padding + "s%-" + width + "s%s", "", title, "");
        System.out.printf("%s|", padded);
    }
    
    private static void printUnderline(int width)
    {
        int totalWidth = Formatting.padding + width;
        String line = "-".repeat(totalWidth);
        System.out.printf("%s|", line);
    }
    
    private static void printCell(String text, int width)
    {
        String padded = String.format("%" + Formatting.padding + "s%-" + width + "s", "", text);
        System.out.printf("%s|", padded);
    }
    
    public static void printColumnTotals(Collection<Item> items, InventoryManager manager)
    {
        double totalQuantity = 0;
        double totalCost = 0;
        double totalSumCost = 0;
        
        for (Item item : items)
        {
            UUID id = item.getUuid();
            totalQuantity += manager.getLatestQuantity(id);
            totalCost     += manager.getLatestPrice(id);
            totalSumCost  += manager.getTotalSpent(id);
        }
        
        System.out.print("\n|");
        
        if (showItem)       printCell("", itemWidth);
        if (showName)       printCell("TOTAL", nameWidth);
        if (showCategory)   printCell("", categoryWidth);
        if (showQuantity)   printCell(String.format("%.0f", totalQuantity), quantityWidth);
        if (showCost)       printCell(String.format("$%.2f", totalCost), costWidth);
        if (showDate)       printCell("", dateWidth);
        if (showTotalCost)  printCell(String.format("$%.2f", totalSumCost), totalCostWidth);
        
        System.out.println();
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
    
    // ----- Row numbers for each unique item ----- //
    private static int getItemIndex(Item item, InventoryManager manager)
    {
        int i = 1;
        for (Item it : manager.getAllItems())
        {
            if (it.getUuid().equals(item.getUuid()))
            {
                return i;
            }
            i++;
        }
        return -1;
    }
    
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
        
        if (activeColumns == 0) return;
        
        // Calculate total padding space between columns
        int totalPadding = (activeColumns - 1) + (Formatting.padding * activeColumns);
        
        // Configurable lengths
        int currencyPresentationLength     = 9; // $9999.00
        int datePresentationLength         = 8; // dd/MM/yy
        int uniqueItemPresentationLength   = 5; // 9999
        int maxItemQuantity                = 4; // 999
        
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
        
        // Remaining width to divide
        int remainingWidth = Formatting.workableWidth - totalPadding - reservedFixedWidths;
        int baseWidth = (flexibleColumns > 0) ? remainingWidth / flexibleColumns : 0;
        
        // Assign final widths
        if (showItem)       itemWidth = uniqueItemPresentationLength;
        if (showName)       nameWidth = baseWidth;
        if (showCategory)   categoryWidth = baseWidth;
        if (showQuantity)   quantityWidth = maxItemQuantity;
        if (showCost)       costWidth = currencyPresentationLength;
        if (showDate)       dateWidth = datePresentationLength;
        if (showTotalCost)  totalCostWidth = currencyPresentationLength;
    }
}