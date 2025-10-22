/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Assignment2.Account;

/**
 *
 * @author megan
 */

public interface AccountCreator 
{
    // ----- Check if an account exists ----- // 
    boolean exists(String username);
    
    // checks username against records
    // returns true if it exists - false if it doesnt
    
    

    // ----- Create a new account ----- // 
    boolean create(String username, String password);
    // username for the new account
    // password for the new account
    // returns true if creation was successful - false if failed
}