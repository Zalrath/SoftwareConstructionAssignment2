/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Assignment2.Utils;

/**
 *
 * @author megan
 */

import java.awt.Component;
import javax.swing.*;
import java.io.*;

// write to text file
public class SimpleTextWriter 
{
    public static void saveTextWithDialog(Component parent, String content) 
    {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save Shopping List As...");
        chooser.setSelectedFile(new File("shopping_list.txt"));
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Text Files (*.txt)", "txt"));
        
        int userSelection = chooser.showSaveDialog(parent);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) 
        {
            File file = chooser.getSelectedFile();
            
            // .txt
            if (!file.getName().toLowerCase().endsWith(".txt")) 
            {
                file = new File(file.getAbsolutePath() + ".txt");
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) 
            {
                writer.write(content);
                JOptionPane.showMessageDialog(parent,
                        "Shopping list saved successfully:\n" + file.getAbsolutePath(),
                        "Export Successful",
                        JOptionPane.INFORMATION_MESSAGE);
            } 
            catch (IOException e) 
            {
                JOptionPane.showMessageDialog(parent,
                        "Error saving file:\n" + e.getMessage(),
                        "Save Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
