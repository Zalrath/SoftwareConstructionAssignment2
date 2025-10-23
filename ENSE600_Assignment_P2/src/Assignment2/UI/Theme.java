/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Assignment2.UI;

/**
 *
 * @author megan
 */

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

// ----- Theme / Style ----- //
public final class Theme
{
    // ----- Mode / Accent Colour ----- //
    public enum Mode { LIGHT, DARK }
    public static Mode currentMode = Mode.DARK;                    // default theme mode = dark
    public static Color accentColour = Color.decode("#48375D");    // default theme accent colour
    
    // ----- Theme Colours ----- // 
    public static Color darkWhite = Color.decode("#D0D0D0"); 
    public static Color lightGrey = Color.decode("#646469"); 
    public static Color mediumGrey = Color.decode("#2E2E2F");
    public static Color darkGrey = Color.decode("#222222"); 
    
    
    // ----- Font / Spacing ----- //
    public static final Font BODY_FONT   = new Font("SansSerif", Font.PLAIN, 16);
    public static final Font TITLE_FONT = loadFont("Jersey25-Regular.ttf", 40f, Font.PLAIN);
    public static final Font HEADER_FONT = loadFont("Jersey25-Regular.ttf", 80f, Font.PLAIN);
    public static final Font CHONK_FONT = loadFont("Jersey25-Regular.ttf", 240f, Font.PLAIN);
    
    public static final Border PAGE_PADDING = BorderFactory.createEmptyBorder(18, 18, 18, 18);
    
    // ----- Colour Palette ----- //
    public static final class Palette
    {
        public final Color surface, background;
        public final Color textPrimary, textSecondary;
        public final Color buttonPrimaryBg, buttonPrimaryText;
        public final Color buttonSecondaryBg, buttonSecondaryText;
        
        private Palette(Color surface, Color background, Color textPrimary, Color textSecondary, Color buttonPrimaryBg, Color buttonPrimaryText, Color buttonSecondaryBg, Color buttonSecondaryText) 
        {
            this.surface = surface;
            this.background = background;
            this.textPrimary = textPrimary;
            this.textSecondary = textSecondary;
            this.buttonPrimaryBg = buttonPrimaryBg;
            this.buttonPrimaryText = buttonPrimaryText;
            this.buttonSecondaryBg = buttonSecondaryBg;
            this.buttonSecondaryText = buttonSecondaryText;
        }
    }
    
    private static Palette colorPalette; // single instance 
    
    // ----- Rebuild ----- //
    static 
    {
        rebuildTheme(); // use default values
    }
    
    // Build the palette object 
    public static void rebuildTheme() 
    {
        if (currentMode == Mode.DARK) 
        {
            colorPalette = createDarkThemePalette();
        }
        if (currentMode == Mode.LIGHT)
        {
            colorPalette = createLightThemePalette();
        }
    }
    
    // ----- Dark Mode ----- //
    private static Palette createDarkThemePalette() 
    {
        Color s = Color.decode("#1C1D21");
        
        return new Palette(
            s,                              // surface
            s,                              // background
            new Color(235, 235, 235),       // textPrimary
            new Color(160, 160, 160),       // textSecondary
            accentColour,                   // buttonPrimaryBg
            Color.WHITE,                    // buttonPrimaryText
            new Color(55, 58, 64),          // buttonSecondaryBg
            new Color(220, 220, 220)        // buttonSecondaryText
        );
    }
    
    // ----- Light Mode ----- //
    private static Palette createLightThemePalette() 
    {
        Color s = Color.decode("#D3D8DD");
        
        return new Palette(
            s,                              // surface
            s,                              // background
            new Color(20, 20, 20),          // textPrimary
            new Color(100, 100, 100),       // textSecondary
            accentColour,                   // buttonPrimaryBg
            Color.WHITE,                    // buttonPrimaryText
            new Color(240, 240, 240),       // buttonSecondaryBg
            Color.BLACK                     // buttonSecondaryText
        );
    }
    
    // ----- Getters / Setters ----- //
    public static Palette palette() { return colorPalette; }    // getter for the palette

    public static Mode getMode() {return currentMode;}          // getter for theme mode
    
    public static void setMode(Mode mode)                       // setter for theme mode
    {
        if (mode != currentMode) 
        {
            currentMode = mode;
            rebuildTheme();
            refreshUI();
        }
    }
    
    public static void toggleMode()                             // toggle mode
    {
        setMode(currentMode == Mode.DARK ? Mode.LIGHT : Mode.DARK);
    }
    
    public static Color getAccent() { return accentColour; }    // get accent colour
    
    public static void setAccent(Color newAccent)               // set accent colour
    {
        if (newAccent != null) 
        {
            accentColour = newAccent;
            rebuildTheme();
        }
    }
    
    // ----- Style Primary Button ----- //
    public static void stylePrimaryButton(AbstractButton button) 
    {
        button.setBackground(colorPalette.buttonPrimaryBg);
        button.setForeground(colorPalette.buttonPrimaryText);
        button.setFont(BODY_FONT);
        button.setFocusPainted(false);
        button.setOpaque(true);
        if (button instanceof JButton) 
        {
            ((JButton) button).setBorder(BorderFactory.createEmptyBorder(8,16,8,16));
        }
    }
    
    // ----- Style Secondary Button ----- //
    public static void styleSecondaryButton(AbstractButton button) 
    {
        button.setBackground(colorPalette.buttonSecondaryBg);
        button.setForeground(colorPalette.buttonSecondaryText);
        button.setFont(BODY_FONT);
        button.setFocusPainted(false);
        button.setOpaque(true);
        if (button instanceof JButton) 
        {
            ((JButton) button).setBorder(BorderFactory.createEmptyBorder(8,16,8,16));
        }
    }
    
    // ----- Utilities ----- //
    private static Font loadFont(String path, float size, int style) 
    {
        try 
        {
            java.net.URL fontUrl = Theme.class.getResource("/Assignment2/resources/" + path);
            if (fontUrl == null) 
            {
                System.err.println("Font not found: " + path);
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
        String modeFolder = (Theme.getMode() == Theme.Mode.DARK) ? "dark" : "light";
        String path = String.format("/Assignment2/images/%s/%s", modeFolder, filename);
        return new ImageIcon(Theme.class.getResource(path)).getImage();
    }
    
    public static void refreshUI() 
    {
        rebuildTheme();
        
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
        if (c instanceof Container cont) 
        {
            cont.setBackground(Theme.palette().surface);
            cont.setForeground(Theme.palette().textPrimary);
            
            for (Component child : cont.getComponents()) 
            {
                refreshAllChildren(child);
            }
        }
    }
    
    // ----- Darker Colour ----- //
    private static Color calculateDarkerColor(Color c, float amount)        // find a darker version of a colour
    {
        amount = Math.max(0f, Math.min(amount, 1f));
        int r = Math.round(c.getRed()  * (1f - amount));
        int g = Math.round(c.getGreen() * (1f - amount));
        int b = Math.round(c.getBlue()  * (1f - amount));
        return new Color(r, g, b, c.getAlpha());
    }

    private Theme() {}
}