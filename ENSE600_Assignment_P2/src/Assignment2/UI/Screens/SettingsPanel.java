/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package Assignment2.UI.Screens;

/**
 *
 * @author megan
 */

import Assignment2.Account.LoginDialog;
import Assignment2.Account.UserAuthenticator;
import Assignment2.Inventory.InventoryManager;
import Assignment2.Inventory.SettingsManager;
import Assignment2.UI.HomeScreen;
import Assignment2.UI.Template.AccentHeaderBar;
import Assignment2.UI.Template.BaseScreenPanel;
import Assignment2.UI.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.border.Border;

public class SettingsPanel extends BaseScreenPanel 
{
    private final UserAuthenticator auth;
    private final InventoryManager manager;
    private final SettingsManager settings;
    
    // ----- Constructor ----- // 
    public SettingsPanel(InventoryManager manager, SettingsManager settings, UserAuthenticator auth) 
    {
        super("Settings", /*showBack*/ true, /*showAdd*/ false, /*addLabel*/ "Add Item", /*backTarget*/ "dashboard");
        this.auth = auth; 
        this.manager = manager;
        this.settings = settings; 
        buildBaseUI();
    }
    
    // ----- Initialise Content ----- // 
    @Override
    protected JComponent createCentre() 
    {
        JPanel container = new JPanel(new BorderLayout(12, 12)); // ← adds 12px spacing between children
        container.setOpaque(false);
        
        // ----- Left sidebar ----- //
        JPanel sidebar = createSidebar();
        container.add(sidebar, BorderLayout.WEST);
        
        // ----- Main content ----- //
        JPanel content = createContentArea();
        container.add(content, BorderLayout.CENTER);

        return container;
    }
    
    private JPanel createSidebar() 
    {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(Theme.palette().tileDark);
    
        // set sidebar size
        sidebar.setPreferredSize(new Dimension(240, 0));
    
        JComponent vLabel = new JComponent() 
        {
            final String text = "ENSE600";
            final Font font = Theme.CHONK_FONT.deriveFont(200f);
        
            @Override
            public Dimension getPreferredSize() 
            {
                BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = img.createGraphics();
                g2.setFont(font);
                FontMetrics fm = g2.getFontMetrics();
                int textW = fm.stringWidth(text);
                int textH = fm.getAscent();
                g2.dispose();
                return new Dimension(textH + 16, textW + 16);
            }
        
            @Override
            protected void paintComponent(Graphics g) 
            {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            
                int w = getWidth();
                int h = getHeight();
                
                g2.setFont(font);
                g2.setColor(Theme.palette().tileMediumDark);
                FontMetrics fm = g2.getFontMetrics();
                int textW = fm.stringWidth(text);
                int textH = fm.getAscent();
            
                // rotate about centre
                g2.rotate(-Math.PI / 2, w / 2.0, h / 2.0);
            
                // centre within sidebar 
                int x = (w - textW) / 2;
                int y = (int) ((h + textH) / 2 - 0.5 * fm.getDescent());
            
                g2.drawString(text, x, y);
                g2.dispose();
            }
        };

        sidebar.add(vLabel, BorderLayout.CENTER);
        return sidebar;
    }
    
    private JPanel createContentArea() 
    {
        JPanel content = new JPanel(new BorderLayout(0, 20)); // vertical gap between sections
        content.setBackground(Theme.palette().tileMedium);
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

       

        // ----- ACCENT COLOUR SECTION ----- // 
        JLabel lblAccent = new JLabel("Accent Colour");
        lblAccent.setFont(Theme.TITLE_FONT);
        lblAccent.setForeground(Theme.palette().textLight);
        lblAccent.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        
        JPanel colorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        colorPanel.setOpaque(false);



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


        for (Color c : accentColors) {
            JButton colorButton = new JButton();
            colorButton.setPreferredSize(new Dimension(30, 30));
            colorButton.setBackground(c);
            colorButton.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
            colorButton.addActionListener(e -> {
                //Theme.setAccent(c); 
                SettingsManager.saveAccentColor(c);
                System.out.println(SettingsManager.getAccentColor());
                System.out.println("Accent set to: " + c);
            });
            colorPanel.add(colorButton);
        }

        JPanel accentPanel = new JPanel();
        accentPanel.setLayout(new BoxLayout(accentPanel, BoxLayout.Y_AXIS));
        accentPanel.setOpaque(false);
        
        
        lblAccent.setAlignmentX(Component.LEFT_ALIGNMENT);
        colorPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        accentPanel.add(lblAccent);
        accentPanel.add(Box.createVerticalStrut(8));
        accentPanel.add(colorPanel);
        accentPanel.add(Box.createVerticalGlue());
        

        // ----- DISPLAY + ACCOUNT SECTIONS (BOTTOM AREA) ----- // 

        
        String[] dateFormats = {
            "dd MMM yyyy",
            "yyyy-MM-dd",
            "dd/MM/yyyy",
            "MMM dd, yyyy"  
        };
        
        String[] currencyFormats = {"$", "€", "£", "¥"};
        
        
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setOpaque(false);

        
        // Display Section
        JLabel lblDisplay = new JLabel("Display");
        lblDisplay.setFont(Theme.TITLE_FONT);
        lblDisplay.setForeground(Theme.palette().textLight);
        lblDisplay.setAlignmentX(Component.LEFT_ALIGNMENT);

        
        bottomPanel.add(lblDisplay);
        bottomPanel.add(Box.createVerticalStrut(10));

        
        
        // Currency Format
        JLabel lblCurrency = new JLabel("Currency Format");
        lblCurrency.setFont(Theme.TITLE_FONT.deriveFont(16f));
        lblCurrency.setForeground(Theme.palette().textLight);
        lblCurrency.setAlignmentX(Component.LEFT_ALIGNMENT);

        
        
        JComboBox<String> currencyFormatBox = new JComboBox<>(currencyFormats);
        currencyFormatBox.setMaximumSize(new Dimension(200, 30));
        currencyFormatBox.setPreferredSize(new Dimension(200, 30));
        currencyFormatBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        currencyFormatBox.setSelectedIndex(0);
        currencyFormatBox.addActionListener(e -> {
            String selected = (String) currencyFormatBox.getSelectedItem();
            SettingsManager.saveCurrencyFormat(selected);
            System.out.println("Currency format saved: " + selected);
        });

        
        
        bottomPanel.add(lblCurrency);
        bottomPanel.add(Box.createVerticalStrut(5)); // small spacing between label and dropdown
        bottomPanel.add(currencyFormatBox);
        bottomPanel.add(Box.createVerticalStrut(20)); // spacing before next section

        

        
        // Date Format
        JLabel lblDate = new JLabel("Date Format");
        lblDate.setFont(Theme.TITLE_FONT.deriveFont(16f));
        lblDate.setForeground(Theme.palette().textLight);
        lblDate.setAlignmentX(Component.LEFT_ALIGNMENT);

        JComboBox<String> dateFormatBox = new JComboBox<>(dateFormats);
        dateFormatBox.setMaximumSize(new Dimension(200, 30));
        dateFormatBox.setPreferredSize(new Dimension(200, 30));
        dateFormatBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        dateFormatBox.setSelectedItem(SettingsManager.getDateFormatDB());
        dateFormatBox.addActionListener(e -> {
            String selected = (String) dateFormatBox.getSelectedItem();
            SettingsManager.saveDateFormat(selected);
            System.out.println("Date format saved: " + SettingsManager.getDateFormatDB());
        });

        bottomPanel.add(lblDate);
        bottomPanel.add(Box.createVerticalStrut(5)); // small spacing between label and dropdown
        bottomPanel.add(dateFormatBox);
        bottomPanel.add(Box.createVerticalStrut(20)); // space before next section

        
        
        Color accent = SettingsManager.getAccentColor();

        // addes accent color to combo boxes
        currencyFormatBox.setBackground(accent);
        currencyFormatBox.setForeground(Color.WHITE); // text color
        currencyFormatBox.setOpaque(true);

        dateFormatBox.setBackground(accent);
        dateFormatBox.setForeground(Color.WHITE);
        dateFormatBox.setOpaque(true);

        //addes accent color for selection
        currencyFormatBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    setBackground(accent);
                    setForeground(Color.WHITE);
                } else {
                    setBackground(Color.WHITE);
                    setForeground(Color.BLACK);
                }
                return this;
            }
        });

        dateFormatBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    setBackground(accent);
                    setForeground(Color.WHITE);
                } else {
                    setBackground(Color.WHITE);
                    setForeground(Color.BLACK);
                }
                return this;
            }
        });
        
        
        
        
        
        
        
        
        
        
        
                   
        // Account Section
        JLabel lblAccount = new JLabel("Account");
        lblAccount.setFont(Theme.TITLE_FONT);
        lblAccount.setForeground(Theme.palette().textLight);
        lblAccount.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnLogout = new JButton("Sign out");

        btnLogout.setPreferredSize(new Dimension(300, 60));
        btnLogout.setFocusPainted(false);
        btnLogout.setContentAreaFilled(true);
        btnLogout.setOpaque(true);
       
        btnLogout.setBackground(accent);
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFont(Theme.TITLE_FONT);
       
        btnLogout.addActionListener(e -> {
            System.out.println("LOGOUT");
            

            int confirm = JOptionPane.showConfirmDialog(
                null,
                "Are you sure you want to sign out?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                JFrame homeFrame = (JFrame) SwingUtilities.getWindowAncestor(btnLogout);
                homeFrame.dispose();
                
                SettingsManager.loadFromDatabase();
                System.out.println(SettingsManager.getDateFormatDB());
                Color dbselected = SettingsManager.getAccentColor();
                Theme.setAccent(dbselected);  
                
                
                SwingUtilities.invokeLater(() -> {
                    
                    new HomeScreen(manager, settings, auth).setVisible(true);
                   
                    // shouldn't be needed
                    /*
                    PlaceholderAuthenticator authenticator = new PlaceholderAuthenticator();
                    LoginDialog login = new LoginDialog(null, true, authenticator);
                    login.setVisible(true);

                    if (login.isAuthenticated()) {
                        new HomeScreen(manager, settings).setVisible(true);
                    }
                    */
                });
            }
        });
     
        
        
        
        bottomPanel.add(lblAccount);
        bottomPanel.add(btnLogout);
        bottomPanel.add(Box.createVerticalStrut(60)); // space for future buttons

        // ----- Layout ----- // 
        content.add(accentPanel, BorderLayout.NORTH);
        content.add(bottomPanel, BorderLayout.SOUTH);

        return content;
    }
    
    // ----- Helpers ------ //
    
    
    
    
    

    
    
    
    
    
    
    // ----- Actions ----- // 
    @Override
    protected void onAdd() 
    {
        super.onAdd();
    }
}