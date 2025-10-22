/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Assignment2.Account;

/**
 *
 * @author megan
 */

interface LoginAuthenticator 
{
    // ----- Authenticates login ----- //
    boolean verify(String username, String password);   
    
    // username -> string
    // password -> string
    // return true if successful - false if failed
}
