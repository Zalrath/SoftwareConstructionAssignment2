/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Assignment2.Account;

/**
 *
 * @author megan
 */

// temp for testing
public class PlaceholderCreator implements AccountCreator 
{
    // hard coded 
    private static final String EXISTING_USER = "admin"; 
    
    @Override
    public boolean exists(String username) 
    {
        return EXISTING_USER.equals(username);
    }
    
    @Override
    public boolean create(String username, String password) 
    {
        return !exists(username);
    }
}