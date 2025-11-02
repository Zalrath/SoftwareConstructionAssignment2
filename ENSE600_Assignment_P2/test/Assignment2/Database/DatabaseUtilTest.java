/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package Assignment2.Database;

import java.sql.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author corin
 */
public class DatabaseUtilTest {
    
    public DatabaseUtilTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    
   public void testConnectToDatabase() throws Exception {
       System.out.println("connectToDatabase");
       Connection result = DatabaseUtil.connectToDatabase();
       assertNotNull("Database connection should not be null", result);
       result.close();  // Close the connection after the test.
   }

   
    @Test
    public void testGetConnection() throws Exception {
        System.out.println("getConnection");

        // Call the method to get the connection
        Connection conn = DatabaseUtil.getConnection();

        // Verify that the connection is not null
        assertNotNull("Connection should not be null", conn);

        // Verify the connection is open
        assertFalse("Connection should be open", conn.isClosed());

        // Close the connection after the test
        conn.close();
    }

       
    @Test
    public void testDisconnectFromDatabase() throws Exception {
        System.out.println("disconnectFromDatabase");
        Connection conn = DatabaseUtil.connectToDatabase();
        assertNotNull("Connection should not be null", conn);

        // Disconnect the database
        DatabaseUtil.disconnectFromDatabase(conn);
        assertTrue("Connection should be closed", conn.isClosed());  // Verify if the connection is closed.
    }

    




    



    
}
