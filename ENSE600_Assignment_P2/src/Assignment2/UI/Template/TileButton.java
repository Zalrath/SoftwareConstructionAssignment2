/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Assignment2.UI.Template;

/**
 *
 * @author megan
 */

import Assignment2.UI.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class TileButton extends JButton 
{
    // ----- Fields ----- // 
    private Image backgroundImage;
    
    // ----- Constructor ----- // 
    public TileButton(String text, Image backgroundImage) 
    {
        super(text);
        this.backgroundImage = backgroundImage;
        
        // default setup
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // appearance
        setFont(Theme.BODY_FONT);
        setForeground(Color.WHITE);
    }
    
    public void setBackgroundImage(Image img) 
    {
        this.backgroundImage = img;
        repaint();
    }
    
    // ----- Rendering ----- //
    @Override
    protected void paintComponent(Graphics g) 
    {
        Graphics2D g2 = (Graphics2D) g.create();
        
        // rendering setup
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int w = getWidth();
        int h = getHeight();
        int cornerRadius = 24;
        
        // bounds for rectangle bounds
        RoundRectangle2D bounds = new RoundRectangle2D.Float(0, 0, w, h, cornerRadius, cornerRadius);
        g2.setClip(bounds); // clip edges
        
        // background
        if (backgroundImage != null) 
        {
            drawBackgroundImage(g2, w, h);
        }
        else
        {
            tempGradient(g2, w, h);
        }
        
        // overlay
        g2.setColor(new Color(0, 0, 0, 60));
        g2.fill(bounds);
        
        // bottom section
        int stripHeight = Math.max(40, h / 8);
        g2.setColor(new Color(0, 0, 0, 140));
        g2.fillRect(0, h - stripHeight, w, stripHeight);
        
        // bottom text
        drawCenteredText(g2, w, h, stripHeight);
        
        // hover ring
        if (getModel().isRollover()) 
        {
            drawHoverRing(g2, w, h);
        }
        
        g2.dispose();
    }
    
    // ----- Painting ----- //
    private void drawBackgroundImage(Graphics2D g2, int w, int h) 
    {
        int imgW = backgroundImage.getWidth(this);
        int imgH = backgroundImage.getHeight(this);
        
        if (imgW > 0 && imgH > 0) 
        {
            // resize image
            double scale = Math.max((double) w / imgW, (double) h / imgH);
            int drawW = (int) Math.round(imgW * scale);
            int drawH = (int) Math.round(imgH * scale);
            int x = (w - drawW) / 2;
            int y = (h - drawH) / 2;
            g2.drawImage(backgroundImage, x, y, drawW, drawH, this);
        }
    }

    private void tempGradient(Graphics2D g2, int w, int h) 
    {    
        Color accent = Theme.getAccent();
        Color accentDark = calculateDarkerColor(accent, 0.25f);
        
        GradientPaint gp = new GradientPaint(0, 0, accent, 0, h, accentDark);
        g2.setPaint(gp);
        g2.fillRect(0, 0, w, h);
    }
    
    private void drawCenteredText(Graphics2D g2, int w, int h, int stripHeight) 
    {
        String text = getText();
        g2.setFont(getFont());
        FontMetrics fm = g2.getFontMetrics();
        int textW = fm.stringWidth(text);
        int textH = fm.getAscent();
        
        int xText = (w - textW) / 2;
        int yText = h - (stripHeight - textH) / 2 - (stripHeight / 10);
        
        g2.setColor(Color.WHITE);
        g2.drawString(text, xText, yText);
    }
    
    private void drawHoverRing(Graphics2D g2, int w, int h) 
    {
        // hover ring
        g2.setStroke(new BasicStroke(3f));
        g2.setColor(new Color(255, 255, 255, 120));
        
        // inset rectangle
        int inset = 2;
        int cornerRadius = 20;
        g2.drawRoundRect(inset, inset, w - 2 * inset, h - 2 * inset, cornerRadius, cornerRadius);
    }
    
    // ----- Utils ----- //
    @Override
    public Dimension getPreferredSize() 
    {
        // rectangle button shape
        return new Dimension(480, 280);
    }
    
    // darker colour
    private static Color calculateDarkerColor(Color c, float amount) 
    {
        amount = Math.max(0f, Math.min(amount, 1f));
        int r = Math.round(c.getRed()  * (1f - amount));
        int g = Math.round(c.getGreen() * (1f - amount));
        int b = Math.round(c.getBlue()  * (1f - amount));
        return new Color(r, g, b, c.getAlpha());
    }
}