/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package Assignment2.UI;

/**
 *
 * @author megan
 */

import Assignment2.UI.Template.AccentHeaderBar;
import Assignment2.UI.Template.TileButton;
import Assignment2.UI.Theme.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class DashboardPanel extends JPanel 
{
    // ----- Fields ----- // 
    private final String username;
    private final String appName;
    
    // ----- Constructor ----- // 
    
    
    public DashboardPanel(JFrame hostFrame, String username, String appName) 
    {
        
        
        
        this.username = username;
        this.appName = appName;
        
        setLayout(new BorderLayout(18, 18));
        setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        setBackground(Theme.palette().background);
        
        // title
        JLabel lblWelcome = new JLabel("Welcome to " + appName + ", " + username + "!");
        lblWelcome.setFont(Theme.TITLE_FONT);
        lblWelcome.setForeground(Theme.palette().textLight);
        lblWelcome.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblWelcome, BorderLayout.NORTH);
        
        // tiles
        JPanel tileRow = new JPanel(new GridLayout(1, 4, 18, 18));
        tileRow.setOpaque(false);
        
        Image img1 = Theme.loadThemedImage("inventory.png");
        Image img2 = Theme.loadThemedImage("budget.png");
        Image img3 = Theme.loadThemedImage("shoppinglist.png");
        Image img4 = Theme.loadThemedImage("settings.png");
        
        TileButton b1 = new TileButton("Inventory", img1);
        TileButton b2 = new TileButton("Budget", img2);
        TileButton b3 = new TileButton("Shopping List", img3);
        TileButton b4 = new TileButton("Settings", img4);
        
        b1.addActionListener(this::onClick);
        b2.addActionListener(this::onClick);
        b3.addActionListener(this::onClick);
        b4.addActionListener(this::onClick);
        
        tileRow.add(b1);
        tileRow.add(b2);
        tileRow.add(b3);
        tileRow.add(b4);
        
        add(tileRow, BorderLayout.CENTER);
    }
    
    // ----- Actions ----- // 
    private void onClick(ActionEvent e) 
    {
        TileButton tb = (TileButton) e.getSource();
        String label = tb.getText();
        HomeScreen hs = (HomeScreen) SwingUtilities.getWindowAncestor(this);
        
        switch (label) 
        {
            case "Inventory" -> hs.showScreen("inventory");
            case "Budget" -> hs.showScreen("budget");
            case "Shopping List" -> hs.showScreen("shopping");
            case "Settings" -> hs.showScreen("settings");
            default -> JOptionPane.showMessageDialog(this, "Unknown section: " + label);
        }
    }
    
    // ----- Utilities ----- // 
    private Image loadImageOrNull(String path) 
    {
        File f = new File(path);
        if (!f.exists()) return null;
        return new ImageIcon(path).getImage();
    }
}