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
    private static Mode currentMode = Mode.DARK;                    // default theme mode = dark
    private static Color accentColour = Color.decode("#3B3158");    // default theme accent colour
    
    // ----- Font / Spacing ----- //
    public static final Font TITLE_FONT  = new Font("SansSerif", Font.BOLD, 24);
    public static final Font BODY_FONT   = new Font("SansSerif", Font.PLAIN, 16);
    public static final Font HEADER_FONT = new Font("SansSerif", Font.BOLD, 30);
    
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
    private static void rebuildTheme() 
    {
        if (currentMode == Mode.DARK) 
        {
            colorPalette = createDarkThemePalette();
        }
        else 
        {
            colorPalette = createLightThemePalette();
        }
        refreshUI();
    }
    
    // ----- Dark Mode ----- //
    private static Palette createDarkThemePalette() 
    {
        Color s = new Color(28, 29, 33);
        
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
        return new Palette(
            Color.WHITE,                    // surface
            new Color(244, 244, 244),       // background
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
    public static Image loadThemedImage(String filename) 
    {
    String modeFolder = (Theme.getMode() == Theme.Mode.DARK) ? "dark" : "light";
    String path = String.format("/Assignment2/images/%s/%s", modeFolder, filename);
    return new ImageIcon(Theme.class.getResource(path)).getImage();
    }
    
    // ----- Force Refresh ----- //
    private static void refreshUI()                 // update colours / fonts
    {
        for (Window w : Window.getWindows()) 
        {
            SwingUtilities.updateComponentTreeUI(w);
            w.repaint();
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