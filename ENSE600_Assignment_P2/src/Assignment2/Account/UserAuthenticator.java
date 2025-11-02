/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Assignment2.Account;

/**
 *
 * @author megan
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserAuthenticator 
{
    // we storing the passwords without a hash - crime
    private final Connection conn;
    
    public UserAuthenticator(Connection conn) 
    {
        this.conn = conn;
        createTableIfMissing();
        createAccount("admin", "admin"); 
    }
    
    private void createTableIfMissing() 
    {
        try (Statement st = conn.createStatement()) 
        {
            st.executeUpdate("""
                CREATE TABLE Users (
                    username VARCHAR(64) PRIMARY KEY,
                    password VARCHAR(64)
                )
                """);
        }
        catch (SQLException ignored) 
        {
            // probably should figure something out for here
        }
    }
    
    public boolean authenticate(String username, String password) 
    {
        String sql = "SELECT password FROM Users WHERE username = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) 
        {
            ps.setString(1, username);
            
            try (ResultSet rs = ps.executeQuery()) 
            {
                return rs.next() && password.equals(rs.getString("password"));
            }
        }
        catch (SQLException e) 
        {
            System.err.println("Authentication failed for user: " + username);
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean createAccount(String username, String password) 
    {
        String sql = "INSERT INTO Users (username, password) VALUES (?, ?)";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) 
        {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.executeUpdate();
            return true;
        }
        catch (SQLException e) 
        {
            return false; // duplicate or something idk
        }
    }
}