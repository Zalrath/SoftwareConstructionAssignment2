
package Assignment2.Inventory;

import Assignment2.Inventory.InventoryManager;
import Assignment2.Inventory.Item;
import Assignment2.Inventory.PurchaseLog;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

import java.sql.Connection;
import org.junit.After;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;


public class InventoryManagerTest {

    private InventoryManager manager;
    private Connection conn;

    @Before
    public void setUp() throws SQLException {
        // Initialize the InventoryManager and setup an in-memory database
        manager = new InventoryManager();
        conn = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");

        // Create tables (if you haven't already done this in your code)
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS Items (uuid VARCHAR PRIMARY KEY, name VARCHAR, last_Purchased DATE, current_Amount DOUBLE, interval_Days INT, tags VARCHAR, favorite BOOLEAN, future BOOLEAN)");
            stmt.execute("CREATE TABLE IF NOT EXISTS Purchases (itemUUID VARCHAR, price DOUBLE, quantity DOUBLE, purchaseDate DATE)");
        }
    }

    @After
    public void tearDown() throws SQLException {
        // Clean up
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("DROP ALL OBJECTS");
        }
        if (conn != null) conn.close();
    }

    @Test
    public void testAddItem() {
        Item milk = new Item("Milk 2L");
        manager.addItem(milk);

        assertNotNull(manager.getItemByUUID(milk.getUuid()));
        assertEquals("Milk 2L", milk.getName());
    }

    @Test
    public void testLogPurchase() {
        Item bread = new Item("Bread");
        manager.addItem(bread);
        LocalDate purchaseDate = LocalDate.of(2025, 7, 25);

        manager.logPurchase(bread.getUuid(), 4.0, 1, purchaseDate);

        assertEquals(1, manager.getPurchaseHistory().get(bread.getUuid()).size());
        assertEquals(4.0, manager.getLatestPrice(bread.getUuid()));
        assertEquals(1, manager.getLatestQuantity(bread.getUuid()));
        assertEquals(purchaseDate, bread.getLastPurchased());
    }



    @Test
    public void testGetItemsToReplenish() {
        Item milk = new Item("Milk");
        manager.addItem(milk);
        LocalDate today = LocalDate.of(2025, 10, 1);
        milk.setLastPurchased(today.minusDays(8));  // Assuming 7 days interval for milk
        milk.setEstimatedIntervalDays(7);

        manager.logPurchase(milk.getUuid(), 3.0, 2, today.minusDays(8));

        List<Item> toReplenish = manager.getItemsToReplenish(today);
        assertTrue(toReplenish.contains(milk)); // Should return milk as needing replenishment
    }

    @Test
    public void testExtractAllTags() {
        Item milk = new Item("Milk");
        milk.addTag("Dairy");
        manager.addItem(milk);

        Item bread = new Item("Bread");
        bread.addTag("Bakery");
        manager.addItem(bread);

        Set<String> tags = manager.extractAllTags();
        assertTrue(tags.contains("Dairy"));
        assertTrue(tags.contains("Bakery"));
    }


    @Test
    public void testSaveItemsToDB() {
        Item milk = new Item("Milk");
        milk.addTag("Dairy");
        manager.addItem(milk);

        manager.saveItemsToDB(conn);

        // Load the item back from the DB to ensure it was saved
        manager.loadItemsFromDB(conn);
        assertNotNull(manager.getItemByUUID(milk.getUuid()));
    }

    @Test
    public void testSavePurchasesToDB() {
        Item bread = new Item("Bread");
        manager.addItem(bread);
        LocalDate purchaseDate = LocalDate.of(2025, 7, 25);

        manager.logPurchase(bread.getUuid(), 4.0, 1, purchaseDate);
        manager.savePurchasesToDB(conn);

        // Load purchases from DB
        manager.loadPurchasesFromDB(conn);

        List<PurchaseLog> logs = manager.getPurchaseHistory().get(bread.getUuid());
        assertNotNull(logs);
        assertEquals(1, logs.size());
        assertEquals(4.0, logs.get(0).getPrice());
        assertEquals(1, logs.get(0).getQuantity());
        assertEquals(purchaseDate, logs.get(0).getPurchaseDate());
    }

    @Test
    public void testLoadItemsFromDB() {
        Item milk = new Item("Milk");
        milk.addTag("Dairy");
        manager.addItem(milk);
        manager.saveItemsToDB(conn);

        // Load items from DB
        manager.loadItemsFromDB(conn);
        assertNotNull(manager.getItemByUUID(milk.getUuid()));
    }

    @Test
    public void testGetTotalSpent() {
        Item milk = new Item("Milk");
        manager.addItem(milk);
        LocalDate today = LocalDate.of(2025, 10, 1);
        manager.logPurchase(milk.getUuid(), 3.0, 2, today);
        manager.logPurchase(milk.getUuid(), 3.5, 1, today.minusDays(7));

        double totalSpent = manager.getTotalSpent(milk.getUuid());
        assertEquals(9.5, totalSpent, 0.01); // (3.0 * 2) + (3.5 * 1) = 9.5
    }

    @Test
    public void testGetTotalSpendingForPeriod() {
        Item bread = new Item("Bread");
        bread.addTag("Bakery");
        manager.addItem(bread);
        LocalDate today = LocalDate.of(2025, 10, 1);
        manager.logPurchase(bread.getUuid(), 2.0, 1, today);
        manager.logPurchase(bread.getUuid(), 2.5, 1, today.minusDays(10));

        double totalSpent = manager.getTotalSpendingForPeriod("month");
        assertEquals(2.0, totalSpent, 0.01); // Only the most recent purchase should count
    }
}
