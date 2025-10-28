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
    // ----- Accent Colour ----- //
    public static Color accentColour = Color.decode("#48375D");    // default theme accent colour
    
    // ----- Theme Colours ----- // 
    // public static Color darkWhite = Color.decode("#D0D0D0"); 
    // public static Color lightGrey = Color.decode("#646469"); 
    // public static Color mediumGrey = Color.decode("#2E2E2F");
    // public static Color darkGrey = Color.decode("#222222");
    
    // ----- Font / Spacing ----- //
    public static final Font BODY_FONT   = new Font("SansSerif", Font.PLAIN, 16);
    public static final Font TITLE_FONT = loadFont("Jersey25-Regular.ttf", 40f, Font.PLAIN);
    public static final Font HEADER_FONT = loadFont("Jersey25-Regular.ttf", 80f, Font.PLAIN);
    public static final Font CHONK_FONT = loadFont("Jersey25-Regular.ttf", 240f, Font.PLAIN);
    
    public static final Border PAGE_PADDING = BorderFactory.createEmptyBorder(18, 18, 18, 18);
    
    // ----- Colour Palette ----- //
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

        private Palette() 
        {
            this.background      = Color.decode("#292929"); // Page background
            this.accent          = accentColour;            // Accent for highlights
            this.textLight       = Color.decode("#DEDEDE"); // Light text on dark surfaces
            this.textDark        = Color.decode("#1C1C1C"); // Dark text
            this.tileDark        = Color.decode("#222222"); // Deep grey tile
            this.tileMediumDark  = Color.decode("#2D2D2F"); // Slightly lighter dark tile
            this.tileMedium      = Color.decode("#3B3D40"); // Mid grey
            this.tileLight       = Color.decode("#646469"); // Pale grey / light surface
            this.hazard          = Color.decode("#502020"); // Red hazard indicator
        }
    }

    private static final Palette COLOR_PALETTE = new Palette();

    public static Palette palette() {
        return COLOR_PALETTE;
    }
    
    // ----- Accent ----- //
    public static Color getAccent() 
    {
        return accentColour;
    }
    
    public static void setAccent(Color newAccent) 
    {
        if (newAccent != null) 
        {
            accentColour = newAccent;
            refreshUI();
        }
    }
    
    // ----- Style Primary Button ----- //
    public static void stylePrimaryButton(AbstractButton button) 
    {
        button.setBackground(COLOR_PALETTE.accent);
        button.setForeground(COLOR_PALETTE.textLight);
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
        button.setBackground(COLOR_PALETTE.tileMedium);
        button.setForeground(COLOR_PALETTE.textLight);
        button.setFont(BODY_FONT);
        button.setFocusPainted(false);
        button.setOpaque(true);
        if (button instanceof JButton jButton) 
        {
            jButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        }
    }
    
    
    // ----- Util ----- //
    private static Font loadFont(String path, float size, int style) 
    {
        try {
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
        if (c instanceof Container cont) 
        {
            cont.setBackground(COLOR_PALETTE.tileDark);
            cont.setForeground(COLOR_PALETTE.textLight);
            for (Component child : cont.getComponents()) 
            {
                refreshAllChildren(child);
            }
        }
    }

    // Utility for slightly darker colors
    public static Color darker(Color c, float amount) 
    {
        amount = Math.max(0f, Math.min(amount, 1f));
        int r = Math.round(c.getRed() * (1f - amount));
        int g = Math.round(c.getGreen() * (1f - amount));
        int b = Math.round(c.getBlue() * (1f - amount));
        return new Color(r, g, b, c.getAlpha());
    }

    private Theme() {
    }
}
