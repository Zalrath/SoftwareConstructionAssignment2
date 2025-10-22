/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Assignment2;

/**
 *
 * @author megan
 */

import Assignment2.UI.HomeScreen;
import Assignment2.UI.Theme;
import java.awt.Color;
import javax.swing.SwingUtilities;
import java.util.Random;

public class Main 
{
    public static void main(String[] args) 
    {
        // this all needs to move out of here ngl and into the settings tab but i just havent done it yet
        
        
        // Theme accent colours -> we should probably pick some different ones 
        Color[] accentColors = 
        {
            Color.decode("#3B3158"), // Deep purple (default)
            Color.decode("#E63946"), // Coral red
            Color.decode("#457B9D"), // Ocean blue
            Color.decode("#2A9D8F"), // Teal green
            Color.decode("#F4A261"), // Sand orange
            Color.decode("#E9C46A"), // Warm yellow
            Color.decode("#1D3557"), // Navy blue
            Color.decode("#6A4C93"), // Violet
            Color.decode("#8ECAE6"), // Soft sky blue
            Color.decode("#219EBC")  // Aqua blue
        };

        // ----- Apply Theme ----- // 
        Color selected = accentColors[0]; // coral-red
        
        Theme.setMode(Theme.Mode.DARK);  // or DARK
        Theme.setAccent(selected);        // updates all components

        // ----- Launch GUI ----- // 
        SwingUtilities.invokeLater(() -> new HomeScreen().setVisible(true));
    }
}