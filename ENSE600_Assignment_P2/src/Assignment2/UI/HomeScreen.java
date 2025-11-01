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
import Assignment2.Database.DatabaseUtil;
import Assignment2.Inventory.InventoryManager;
import Assignment2.Inventory.Item;
import Assignment2.Inventory.SettingsManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
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
    
    private final InventoryManager manager;
    private final SettingsManager settings;
    
    // layout + screen map
    private final CardLayout cards = new CardLayout();
    private final JPanel root = new JPanel(cards);
    private final Map<String, JPanel> screenMap = new HashMap<>();
  
    
    // ----- Constructor ----- // 
    public HomeScreen(InventoryManager manager, SettingsManager settings)
    {
        this.manager = manager;
        this.settings = settings;
        setTitle("Welcome");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(520, 320);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1440, 960));
        setLocationRelativeTo(null);
        
        setContentPane(root);
        root.setBackground(Theme.palette().tileDark);
        
        buildWelcomeCard();
        showScreen("welcome");
    }
    
    public InventoryManager getInventoryManager() 
    { return manager; }
    
    public SettingsManager getSettingManager() 
    { return settings; }
    
    // ----- Screen Registration ----- // 
    private void registerScreen(String name, JPanel panel) 
    {
        panel.setName(name);
        if (screenMap.containsKey(name)) 
        {
            root.remove(screenMap.get(name));
        }
        root.add(panel, name);
        screenMap.put(name, panel);
    }
    
    public void showScreen(String name) 
    {
        if (screenMap.containsKey(name)) 
        {
            cards.show(root, name);
        }
        root.revalidate();
        root.repaint();
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
        registerScreen("inventory", new InventoryPanel(manager,settings));
        registerScreen("budget",    new BudgetPanel(manager));
        registerScreen("shopping",  new ShoppingListPanel(manager));
        registerScreen("settings",  new SettingsPanel(manager,settings));
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
       // setExtendedState(JFrame.MAXIMIZED_BOTH);
        setupMainScreens();
        // set default
        showScreen(targetScreen);
    }

    // ----- Getters ----- // 
    public String getCurrentUser() { return currentUser; }
    public String getAppName() { return appName; }
    
    
    
    
    
    
    
    
    
    // ----- add item ----- // 
       public void addNewItem(AddItemDialog.Data data) 
       {
        if (data == null) 
        {
            return;
        }
        
        
        
        // create new item
        Item newItem = new Item(data.name);
        newItem.setCurrentAmount(data.quantity);
        
        ArrayList<String> tags = new ArrayList<>();
        if (data.category != null && !data.category.isEmpty()) 
        {
            tags.add(data.category.trim());
        }
        newItem.setTags(tags);
        newItem.setLastPurchased(java.time.LocalDate.now());
        newItem.setFuture(false);
        newItem.setFavorite(false);
        
         
        // add to manager
        manager.addItem(newItem);
        manager.logPurchase(
                newItem.getUuid(),
                data.unitCost,
                data.quantity,
                java.time.LocalDate.now()
        );      
        
        
        // refresh inventory panel
        JPanel p = screenMap.get("inventory");
        if (p instanceof InventoryPanel invPanel) 
        {
            invPanel.refreshTable();
        }

        // see if works
        JOptionPane.showMessageDialog(
                this,
                "Item added: " + newItem.getName(),
                "Success",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

}