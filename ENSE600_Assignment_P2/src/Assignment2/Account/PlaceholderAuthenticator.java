/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Assignment2.Account;



/**
 *
 * @author megan
 */

// temporary for testing
public class PlaceholderAuthenticator implements LoginAuthenticator 
{
    // hard coded test account info
    private static final String TEMP_USER = "admin";
    private static final String TEMP_PASS = "admin";
    
    @Override
    public boolean verify(String username, String password) 
    {
        return TEMP_USER.equals(username) && TEMP_PASS.equals(password);
    }
}
