package Assignment2.Account;

import Assignment2.Database.DatabaseUtil;
import java.sql.Connection;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 * Basic integration test for the UserAuthenticator class.
 * Uses an in-memory Derby database via DatabaseUtil.
 */
public class UserAuthenticatorTest {

    private static Connection conn;
    private UserAuthenticator userAuth;

    @BeforeClass
    public static void setUpClass() throws Exception {
        // Initialize in-memory DB once for all tests
        conn = DatabaseUtil.connectToDatabase();
        assertNotNull("Database connection should not be null", conn);

        // Create table for users if it doesn’t exist
        try (var stmt = conn.createStatement()) {
            stmt.executeUpdate("""
                CREATE TABLE Users (
                    username VARCHAR(50) PRIMARY KEY,
                    password VARCHAR(50)
                )
            """);
        } catch (Exception e) {
            // Ignore "table already exists"
        }
        System.out.println("Test DB setup complete");
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        DatabaseUtil.disconnectFromDatabase(conn);
        System.out.println("Test DB closed");
    }

    @Before
    public void setUp() {
        userAuth = new UserAuthenticator(conn);
    }

    @After
    public void tearDown() throws Exception {
        // Clean up user table between tests
        try (var stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM Users");
        }
    }

    /**
     * Test account creation success
     */
    @Test
    public void testCreateAccount_Success() {
        boolean result = userAuth.createAccount("testuser", "password123");
        assertTrue("Account should be created successfully", result);
    }

    /**
     * Test duplicate account creation failure
     */
    @Test
    public void testCreateAccount_Duplicate() {
        userAuth.createAccount("duplicateUser", "pass1");
        boolean result = userAuth.createAccount("duplicateUser", "pass2");
        assertFalse("Duplicate username should not be allowed", result);
    }

    /**
     * Test successful authentication
     */
    @Test
    public void testAuthenticate_Success() {
        userAuth.createAccount("authUser", "secret");
        boolean result = userAuth.authenticate("authUser", "secret");
        assertTrue("User should authenticate successfully", result);
    }

    /**
     *  Test authentication fails with wrong password
     */
    @Test
    public void testAuthenticate_WrongPassword() {
        userAuth.createAccount("wrongPassUser", "goodpass");
        boolean result = userAuth.authenticate("wrongPassUser", "badpass");
        assertFalse("Authentication should fail with wrong password", result);
    }

    /**
     * Test authentication fails for non-existent user
     */
    @Test
    public void testAuthenticate_UserNotFound() {
        boolean result = userAuth.authenticate("ghostUser", "1234");
        assertFalse("Authentication should fail for non-existent user", result);
    }
}
