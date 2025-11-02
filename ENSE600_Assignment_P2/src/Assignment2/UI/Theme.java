/*
 * click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Assignment2.UI;

/**
 * @author megan
 */

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

// ----- theme & style ----- //
public final class Theme
{
    // ----- accent colour ----- //
    public static Color accentColour = Color.decode("#48375D");
    
    // ----- font & spacing ----- //
    public static final Font BODY_FONT    = new Font("SansSerif", Font.PLAIN, 16);
    public static final Font TITLE_FONT = loadFont("Jersey25-Regular.ttf", 40f, Font.PLAIN);
    public static final Font HEADER_FONT = loadFont("Jersey25-Regular.ttf", 80f, Font.PLAIN);
    public static final Font CHONK_FONT = loadFont("Jersey25-Regular.ttf", 240f, Font.PLAIN);
    
    public static final Border PAGE_PADDING = BorderFactory.createEmptyBorder(18, 18, 18, 18);
    
    // ----- colour palette ----- //
    public static final class Palette
    {
        public final Color background;
        public final Color accent;
        
        public final Color textLight;
        public final Color textDark;
        
        public final Color tileDark;
        public final Color tileMediumDark;
        public final Color tileMedium;
        public final Color tileLight;
        
        public final Color hazard;
        
        public Palette(Color currentAccent)
        {
            this.background      = Color.decode("#292929"); // Page background
            this.accent          = currentAccent;            // Use the passed-in accent
            this.textLight       = Color.decode("#DEDEDE"); // Light text on dark surfaces
            this.textDark        = Color.decode("#1C1C1C"); // Dark text
            this.tileDark        = Color.decode("#222222"); // Deep grey tile
            this.tileMediumDark  = Color.decode("#2D2D2F"); // Slightly lighter dark tile
            this.tileMedium      = Color.decode("#3B3D40"); // Mid grey
            this.tileLight       = Color.decode("#646469"); // Pale grey / light surface
            this.hazard          = Color.decode("#502020"); // Red hazard indicator
        }
    }
    
    // non final reference 
    private static Palette COLOR_PALETTE = new Palette(accentColour);
    
    public static Palette palette() 
    {
        return COLOR_PALETTE;
    }
    
    public static Color getAccent()
    {
        return accentColour;
    }
    
    public static void setAccent(Color newAccent)
    {
        if (newAccent != null)
        {
            accentColour = newAccent;
            
            COLOR_PALETTE = new Palette(accentColour);
            
            refreshUI();
        }
    }
    
    // ----- button styling ----- //
    public static void stylePrimaryButton(AbstractButton button)
    {
        Palette current = palette();
        button.setBackground(current.accent);
        button.setForeground(current.textLight);
        button.setFont(BODY_FONT);
        button.setFocusPainted(false);
        button.setOpaque(true);
        if (button instanceof JButton jButton)
        {
            jButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        }
    }
    
    public static void styleSecondaryButton(AbstractButton button)
    {
        Palette current = palette();
        button.setBackground(current.tileMedium);
        button.setForeground(current.textLight);
        button.setFont(BODY_FONT);
        button.setFocusPainted(false);
        button.setOpaque(true);
        if (button instanceof JButton jButton)
        {
            jButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        }
    }
    
    // ----- utility methods ----- //
    private static Font loadFont(String path, float size, int style)
    {
        try 
        {
            java.net.URL fontUrl = Theme.class.getResource("/Assignment2/resources/" + path);
            if (fontUrl == null)
            {
                System.err.println("font not found: " + path);
                return new Font("SansSerif", style, (int) size);
            }
            Font base = Font.createFont(Font.TRUETYPE_FONT, fontUrl.openStream());
            return base.deriveFont(style, size);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return new Font("SansSerif", style, (int) size);
        }
    }
    
    public static Image loadThemedImage(String filename)
    {
        String path = "/Assignment2/images/dark/" + filename;
        return new ImageIcon(Theme.class.getResource(path)).getImage();
    }
    
    public static void refreshUI()
    {
        for (Window w : Window.getWindows())
        {
            SwingUtilities.updateComponentTreeUI(w);
            w.invalidate();
            w.validate();
            w.repaint();
            refreshAllChildren(w);
        }
    }
    
   
    
    private static void refreshAllChildren(Component c)
    {
        Palette current = palette();
        
        if (c instanceof Container cont)
        {
            cont.setBackground(current.tileDark);
            cont.setForeground(current.textLight);
            
            for (Component child : cont.getComponents())
            {
                refreshAllChildren(child);
            }
        }
    }
    
    
    
    public static Color darker(Color c, float amount)
    {
        amount = Math.max(0f, Math.min(amount, 1f));
        int r = Math.round(c.getRed() * (1f - amount));
        int g = Math.round(c.getGreen() * (1f - amount));
        int b = Math.round(c.getBlue() * (1f - amount));
        return new Color(r, g, b, c.getAlpha());
    }
    
    private Theme() 
    {
    }
}