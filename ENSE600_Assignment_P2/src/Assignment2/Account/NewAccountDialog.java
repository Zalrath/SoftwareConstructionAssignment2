/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Assignment2.Account;

/**
 *
 * @author megan
 */

import Assignment2.UI.Template.BaseThemedDialog;
import Assignment2.UI.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class NewAccountDialog extends BaseThemedDialog 
{
    // ----- Creation Authentication ----- // 
    private final UserAuthenticator authenticator;
    private boolean created = false;
    
    // ----- UI ----- // 
    private final JTextField txtUser = new JTextField(15);
    private final JPasswordField txtPass = new JPasswordField(15);
    private final JPasswordField txtPass2 = new JPasswordField(15);
    private final JLabel msg = new JLabel(" ");
    
    /**
     * @param owner - owning window
     * @param modal - dialog
     * @param creator - checks existence & creation
     */
    
    public NewAccountDialog(Frame owner, boolean modal, UserAuthenticator authenticator) 
    {
        super(owner, "Create Account",  modal ? ModalityType.APPLICATION_MODAL : ModalityType.MODELESS);
        
        this.authenticator = authenticator;
        
        setSize(420, 240);
        setLocationRelativeTo(owner);
        
        buildUI();
        
        pack();
        setLocationRelativeTo(owner);
    }
    
    private void buildUI() 
    {
        // ----- Styling ----- // 
        msg.setForeground(Color.RED);
        msg.setFont(Theme.BODY_FONT);
        
        // ----- Buttons ----- //
        JButton btnCreate = new JButton("Create");
        JButton btnCancel = new JButton("Cancel");
        Theme.stylePrimaryButton(btnCreate);
        Theme.styleSecondaryButton(btnCancel);
        
        // ----- Actions ----- //
        btnCreate.addActionListener(this::onCreate);
        btnCancel.addActionListener(e -> dispose());
        
        // ----- Form ----- //
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Theme.palette().tileDark);
        
        form.setBorder(BorderFactory.createEmptyBorder(16, 16, 8, 16));
        
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 6, 6, 6);
        gc.anchor = GridBagConstraints.WEST;
        
        // ----- Labels ----- //
        JLabel lblUser = makeLabel("Username:");
        JLabel lblPass = makeLabel("Password:");
        JLabel lblPass2 = makeLabel("Confirm Password:");
        
        // username
        gc.gridx = 0; gc.gridy = 0; form.add(lblUser, gc);
        gc.gridx = 1; form.add(txtUser, gc);
        
        // password
        gc.gridx = 0; gc.gridy = 1; form.add(lblPass, gc);
        gc.gridx = 1; form.add(txtPass, gc);
        
        // password confrim
        gc.gridx = 0; gc.gridy = 2; form.add(lblPass2, gc);
        gc.gridx = 1; form.add(txtPass2, gc);
        
        // message area
        gc.gridx = 0; gc.gridy = 3; gc.gridwidth = 2; form.add(msg, gc);
        
        // ----- Button Layout ----- // 
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.setBackground(Theme.palette().tileDark);
        buttons.add(btnCancel);
        buttons.add(btnCreate);
        
        // set default button to create
        getRootPane().setDefaultButton(btnCreate);

        // ----- Layout ----- // 
        setLayout(new BorderLayout());
        add(form, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
    }
    
    // ----- Actions ----- // 
    private void onCreate(ActionEvent e) 
    {
        String u = txtUser.getText().trim();
        String p1 = new String(txtPass.getPassword());
        String p2 = new String(txtPass2.getPassword());
        
        // validate
        if (u.isEmpty()) 
        {
            msg.setText("Please enter a username.");
            return;
        }
        if (p1.isEmpty() || p2.isEmpty()) 
        {
            msg.setText("Please enter and confirm your password.");
            return;
        }
        if (!p1.equals(p2)) 
        {
            msg.setText("Passwords do not match.");
            return;
        }

        // validate existing
        if (authenticator.createAccount(u, p1))
        {
            created = true;
            dispose();
        }
        else 
        {
            msg.setText("Could not create account.");
        }
    }
    
    public boolean isCreated() 
    {
        return created;
    }
    
    public String getUsername() 
    {
        return txtUser.getText().trim();
    }
}