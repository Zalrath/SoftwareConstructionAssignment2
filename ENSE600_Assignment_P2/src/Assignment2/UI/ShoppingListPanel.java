/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package Assignment2.UI;

/**
 *
 * @author megan
 */

import Assignment2.UI.Template.BaseScreenPanel;

import javax.swing.*;
import java.awt.*;

public class ShoppingListPanel extends BaseScreenPanel 
{
    // ----- Constructor ----- // 
    public ShoppingListPanel() 
    {
        super("ShoppingList", /*showBack*/ true, /*showAdd*/ true, /*addLabel*/ "Add Item", /*backTarget*/ "dashboard");
    }
    
    // ----- Initialise Content ----- // 
    @Override
    protected JComponent createCentre() 
    {
        // placeholder for content
        JTextArea area = new JTextArea("ShoppingList\n\n(Replace with your real UI)");
        
        // theme
        Theme.Palette palette = Theme.palette();
        
        area.setFont(Theme.BODY_FONT);
        area.setForeground(palette.textPrimary);
        area.setBackground(palette.surface);
        area.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        
        JScrollPane sp = new JScrollPane(area);
        sp.setBorder(null);
        
        JPanel card = new JPanel(new BorderLayout());
        card.setOpaque(false);
        card.add(sp, BorderLayout.CENTER);
        return card;
    }
    
    // ----- Actions ----- // 
    @Override
    protected void onAdd() 
    {
        super.onAdd();
    }
}