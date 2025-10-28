/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package Assignment2.UI.Screens;

/**
 *
 * @author megan
 */

import Assignment2.UI.Template.AccentHeaderBar;
import Assignment2.UI.Template.BaseScreenPanel;
import Assignment2.UI.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.border.Border;

public class SettingsPanel extends BaseScreenPanel 
{

    
    // ----- Constructor ----- // 
    public SettingsPanel() 
    {
        super("Settings", /*showBack*/ true, /*showAdd*/ true, /*addLabel*/ "Add Item", /*backTarget*/ "dashboard");
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

        // ----- THEME SECTION ----- // 
        JLabel lblTheme = new JLabel("Theme");
        lblTheme.setFont(Theme.TITLE_FONT);
        lblTheme.setForeground(Theme.palette().textLight);
        lblTheme.setAlignmentX(Component.LEFT_ALIGNMENT);

        // FIX: Change themePanel from BorderLayout to BoxLayout for vertical stacking
        JPanel themePanel = new JPanel();
        themePanel.setLayout(new BoxLayout(themePanel, BoxLayout.Y_AXIS));
        themePanel.setOpaque(false);

        // Add Title
        themePanel.add(lblTheme);
        themePanel.add(Box.createVerticalStrut(8));

        // Add Buttons

        // Add large spacer (used to be in BorderLayout.CENTER)
        themePanel.add(Box.createVerticalStrut(60));

        // ----- ACCENT COLOUR SECTION ----- // 
        JLabel lblAccent = new JLabel("Accent Colour");
        lblAccent.setFont(Theme.TITLE_FONT);
        lblAccent.setForeground(Theme.palette().textLight);

        JPanel accentPanel = new JPanel(new BorderLayout());
        accentPanel.setOpaque(false);
        accentPanel.add(lblAccent, BorderLayout.NORTH);
        accentPanel.add(Box.createVerticalStrut(80), BorderLayout.CENTER); // space for colour buttons later

        // ----- DISPLAY + ACCOUNT SECTIONS (BOTTOM AREA) ----- // 
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setOpaque(false);

        // Display
        JLabel lblDisplay = new JLabel("Display");
        lblDisplay.setFont(Theme.TITLE_FONT);
        lblDisplay.setForeground(Theme.palette().textLight);

        JLabel lblCurrency = new JLabel("Currency Format");
        lblCurrency.setFont(Theme.TITLE_FONT.deriveFont(16f));
        lblCurrency.setForeground(Theme.palette().textDark);

        JLabel lblDate = new JLabel("Date Format");
        lblDate.setFont(Theme.TITLE_FONT.deriveFont(16f));
        lblDate.setForeground(Theme.palette().textDark);

        bottomPanel.add(lblDisplay);
        bottomPanel.add(Box.createVerticalStrut(10));
        bottomPanel.add(lblCurrency);
        bottomPanel.add(Box.createVerticalStrut(40)); // leave room for dropdowns
        bottomPanel.add(lblDate);
        bottomPanel.add(Box.createVerticalStrut(60));

        // Account
        JLabel lblAccount = new JLabel("Account");
        lblAccount.setFont(Theme.TITLE_FONT);
        lblAccount.setForeground(Theme.palette().textLight);

        bottomPanel.add(lblAccount);
        bottomPanel.add(Box.createVerticalStrut(60)); // room for buttons later

        // ----- Layout ----- // 
        content.add(themePanel, BorderLayout.NORTH);
        content.add(accentPanel, BorderLayout.CENTER);
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