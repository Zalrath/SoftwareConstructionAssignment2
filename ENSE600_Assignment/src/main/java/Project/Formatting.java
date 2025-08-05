/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Project;

/**
 *
 * @author megan
 */
public class Formatting 
{
    public static void printBorder(int row, int screenWidth)
    {
        int workableWidth = screenWidth - 2;
        for (int i = 0; i < row; i++)
        {
            System.out.printf("\n|%-" + workableWidth + "s|", "");
        }
    }
    
    public static void printBar(int screenWidth)
    {
        int workableWidth = screenWidth - 2;
        System.out.print(" ");
        for(int i = 0; i < workableWidth; i++)
        {
            System.out.print("_");
        }
    }
    
    public static void printInputLine(int padding)
    {
        System.out.printf("|%" + padding + "s", "");
    }
}
