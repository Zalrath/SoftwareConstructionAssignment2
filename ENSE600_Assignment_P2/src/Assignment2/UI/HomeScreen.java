/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package Assignment2.UI;

/**
 *
 * @author megan
 */

import Assignment2.UI.Screens.InventoryPanel;
import Assignment2.UI.Screens.SettingsPanel;
import Assignment2.UI.Screens.ShoppingListPanel;
import Assignment2.UI.Screens.BudgetPanel;
import Assignment2.Account.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

public class HomeScreen extends JFrame 
{
    // ----- Fields ----- // 
    private final String appName = "_____ Manager";
    private String currentUser = "Guest";
    
    // dependencies
    private final PlaceholderAuthenticator authenticator = new PlaceholderAuthenticator();
    private final AccountCreator accountCreator = new PlaceholderCreator();
    
    // ----- Data / Managers ----- //
    private final Project_p2.InventoryManager manager = new Project_p2.InventoryManager();
    private final java.util.List<Project_p2.Item> items = new java.util.ArrayList<>();
    
    
    
    
    // layout + screen map
    private final CardLayout cards = new CardLayout();
    private final JPanel root = new JPanel(cards);
    private final Map<String, JPanel> screenMap = new HashMap<>();
    
    // ----- Constructor ----- // 
    public HomeScreen() 
    {
        setTitle("Welcome");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(520, 320);
        setLocationRelativeTo(null);
        
        setContentPane(root);
        root.setBackground(Theme.palette().tileDark);
        
        buildWelcomeCard();
        showScreen("welcome");
    }
    
    // ----- Screen Registration ----- // 
    private void registerScreen(String name, JPanel panel) 
    {
        if (!screenMap.containsKey(name)) 
        {
            panel.setName(name);
            root.add(panel, name);
            screenMap.put(name, panel);
        }
    }
    
    public void showScreen(String name) 
    {
        if (screenMap.containsKey(name)) 
        {
            cards.show(root, name);
            root.revalidate();
            root.repaint();
        } 
        else if (name.equals("welcome")) 
        {
            cards.show(root, "welcome");
        }
        
        setTitle(appName + " - " + name.substring(0, 1).toUpperCase() + name.substring(1));
    }
    
    // ----- Welcome Screen ----- // 
    private void buildWelcomeCard() 
    {
        JPanel welcome = new JPanel(new BorderLayout(10, 10));
        welcome.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        welcome.setBackground(Theme.palette().tileDark);
        
        JLabel title = new JLabel("Welcome to " + appName, SwingConstants.CENTER);
        title.setFont(Theme.TITLE_FONT);
        title.setForeground(Theme.palette().textLight);
        welcome.add(title, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        
        JButton btnLogin = new JButton("Login");
        JButton btnNew = new JButton("Create New Account");
        Theme.stylePrimaryButton(btnLogin);
        Theme.styleSecondaryButton(btnNew);
        
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnNew.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(btnLogin);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 16)));
        centerPanel.add(btnNew);
        centerPanel.add(Box.createVerticalGlue());
        
        welcome.add(centerPanel, BorderLayout.CENTER);
        
        btnLogin.addActionListener(this::onLoginClicked);
        btnNew.addActionListener(this::onNewAccountClicked);
        
        registerScreen("welcome", welcome);
    }
    
    // ----- Main Tabs ----- // 
    private void setupMainScreens() 
    {
        
        registerScreen("dashboard", new DashboardPanel(this, currentUser, appName));
        registerScreen("inventory", new InventoryPanel(manager, items));
        registerScreen("budget", new BudgetPanel());
        registerScreen("shopping", new ShoppingListPanel());
        registerScreen("settings", new SettingsPanel());
    }
    
    // ----- Actions ----- // 
    private void onLoginClicked(ActionEvent e) 
    {
        LoginDialog dlg = new LoginDialog(this, true, authenticator);
        dlg.setVisible(true);
        
        if (dlg.isAuthenticated()) 
        {
            currentUser = dlg.getUsername();
            postAuthSetup("dashboard");
        }
    }
    
    private void onNewAccountClicked(ActionEvent e) 
    {
        NewAccountDialog dlg = new NewAccountDialog(this, true, accountCreator);
        dlg.setVisible(true);
        
        if (dlg.isCreated()) 
        {
            currentUser = dlg.getUsername();
            postAuthSetup("dashboard");
        }
    }
    
    private void postAuthSetup(String targetScreen) 
    {
        setupMainScreens();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        showScreen(targetScreen);
    }
    
    // ----- Getters ----- // 
    public String getCurrentUser() { return currentUser; }
    public String getAppName() { return appName; }
}