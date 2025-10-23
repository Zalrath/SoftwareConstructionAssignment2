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
            Color.decode("#104A63"), // Deep Teal Blue
            Color.decode("#253D27"), // Dark Forest Green
            Color.decode("#3C4072"), // Indigo Blue / Slate Purple
            Color.decode("#3D8479"), // Muted Aqua Green
            Color.decode("#477D51"), // Medium Leaf Green
            Color.decode("#48375D"), // Dark Violet Gray
            Color.decode("#716994"), // Dusty Lavender
            Color.decode("#8E3E3E"), // Brick Red
            Color.decode("#8E4D6C"), // Mauve Rose
            Color.decode("#AE5E41") // Burnt Copper / Terracotta
        };
        
        
        // ----- Apply Theme ----- // 
        Color selected = accentColors[0]; // coral-red
        
         // Theme.setMode(Theme.Mode.LIGHT);  // or DARK
        // Theme.setAccent(selected);        // updates all components

        // ----- Launch GUI ----- // 
        SwingUtilities.invokeLater(() -> new HomeScreen().setVisible(true));
    }
}