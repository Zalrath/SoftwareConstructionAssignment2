/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Assignment2.UI.Template;

/**
 *
 * @author megan
 */

import Assignment2.UI.AddItemDialog;
import Assignment2.UI.HomeScreen;
import Assignment2.UI.Theme;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public abstract class BaseScreenPanel extends JPanel 
{
    // ----- Config ----- //
    private final String titleText;
    private final boolean showBack;
    private final boolean showAdd;
    private final String addLabel;
    private final String backTargetCard;
    
    // ----- Components ----- //
    protected JLabel titleLabel;
    protected AccentHeaderBar headerBar;
    
    // ----- Constructor ----- //
    protected BaseScreenPanel(String titleText, boolean showBack, boolean showAdd, String addLabel, String backTargetCard)
    {
        this.titleText = titleText;
        this.showBack = showBack;
        this.showAdd = showAdd;
        this.addLabel = (addLabel != null ? addLabel : "Add");
        this.backTargetCard = backTargetCard;
    }
    
    protected final void buildBaseUI() 
    {
        // layout, background and border
        configurePanel();

        // title
        headerBar = new AccentHeaderBar(titleText);
        add(headerBar, BorderLayout.NORTH);

        // middle
        add(createCenterContent(), BorderLayout.CENTER);

        // bottom
        add(createSouthPanel(), BorderLayout.SOUTH);
    }
    
    private void configurePanel() 
    {
        setLayout(new BorderLayout(12, 12));
        setBackground(Theme.palette().background);
        setBorder(createPanelBorder());
    }
    
    private Border createPanelBorder() 
    {
        return Theme.PAGE_PADDING; 
    }
    
    private JPanel createTopPanel() 
    {
        JPanel north = new JPanel(new BorderLayout());
        north.setOpaque(false);
        
        // title
        titleLabel = new JLabel(titleText);
        titleLabel.setFont(Theme.TITLE_FONT);
        // titleLabel.setForeground(Theme.palette().textPrimary);
        north.add(titleLabel, BorderLayout.NORTH);
        
        // extra
        JComponent extra = createNorthExtra();
        if (extra != null) 
        {
            extra.setOpaque(false);
            north.add(extra, BorderLayout.SOUTH);
        }
        
        return north;
    }
    
    private JComponent createCenterContent() 
    {
        JComponent center = createCentre();
        return (center != null) ? center : defaultCenter();
    }
    
    private JPanel createSouthPanel() 
    {
        JPanel south = new JPanel(new BorderLayout());
        south.setOpaque(false);
        
        // back button
        if (showBack) 
        {
            JButton btnBack = new JButton("Back");
            Theme.styleSecondaryButton(btnBack); 
            btnBack.addActionListener(e -> navigateToCard(backTargetCard));
            
            JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            left.setOpaque(false);
            left.add(btnBack);
            south.add(left, BorderLayout.WEST);
        }
        
        // add button
        if (showAdd) 
        {
            JButton btnAdd = new JButton(addLabel);
            Theme.stylePrimaryButton(btnAdd); 
            btnAdd.addActionListener(e -> onAdd());
            
            JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            right.setOpaque(false);
            right.add(btnAdd);
            south.add(right, BorderLayout.EAST);
        }
        return south;
    }
    
    // ----- Extras ----- //
    // extra row under title
    protected JComponent createNorthExtra() { return null; }
    
    // create centre content
    protected abstract JComponent createCentre();

    // text area
    protected JComponent defaultCenter() 
    {
        JTextArea area = new JTextArea();
        
        // theme
        area.setFont(Theme.BODY_FONT);
        area.setForeground(Theme.palette().textLight);
        area.setBackground(Theme.palette().background);
        
        area.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        
        JScrollPane sp = new JScrollPane(area);
        sp.setBorder(null);
        
        JPanel card = new JPanel(new BorderLayout());
        card.setOpaque(false);
        card.add(sp, BorderLayout.CENTER);
        return card;
    }
    
    // open add item dialog
    protected void onAdd() 
    {
        AddItemDialog.show(SwingUtilities.getWindowAncestor(this)).ifPresent(data -> JOptionPane.showMessageDialog(this, "Added: " + data, "Added", JOptionPane.INFORMATION_MESSAGE));
    }
    
    protected void navigateToCard(String card) 
    {
        if (card == null) return;
        Window w = SwingUtilities.getWindowAncestor(this);    
        
        if (w instanceof HomeScreen) 
        {
            ((HomeScreen) w).showScreen(card);
        }   
    }
}
