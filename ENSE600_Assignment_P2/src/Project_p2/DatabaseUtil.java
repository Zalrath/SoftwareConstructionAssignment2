package Project_p2;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author corin
 */

import java.sql.*;

public class DatabaseUtil {
    public static Connection connect() {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            Connection conn = DriverManager.getConnection("jdbc:derby:InventoryDB;create=true");
            System.out.println("✅ Connected to Derby database");
            return conn;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
