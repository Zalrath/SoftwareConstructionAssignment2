/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Assignment2.Account;

/**
 *
 * @author megan
 */

import Assignment2.Account.LoginAuthenticator;
import Assignment2.UI.Template.BaseThemedDialog;
import Assignment2.UI.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

// LoginDialog dlg = new LoginDialog(this, true, new PlaceholderAuthenticator());

public class LoginDialog extends BaseThemedDialog 
{
    // ----- Login Authentication ----- // 
    private final LoginAuthenticator authenticator;
    private boolean authenticated = false;
    
    // ----- UI ----- // 
    private final JTextField txtUser = new JTextField(15);
    private final JPasswordField txtPass = new JPasswordField(15);
    private final JLabel msg = new JLabel(" ");
    
    /**
     * @param owner - owning window
     * @param modal - dialog
     * @param authenticator - dependency for login verification
     */
    
    public LoginDialog(Frame owner, boolean modal, LoginAuthenticator authenticator) 
    {
        super(owner, "Login", modal ? ModalityType.APPLICATION_MODAL : ModalityType.MODELESS);
        
        this.authenticator = authenticator;
        
        setSize(400, 200);
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
        JButton btnLogin = new JButton("Login");
        JButton btnCancel = new JButton("Cancel");
        Theme.stylePrimaryButton(btnLogin);
        Theme.styleSecondaryButton(btnCancel);
        
        // ----- Actions ----- // 
        btnLogin.addActionListener(this::onLogin);
        btnCancel.addActionListener(e -> dispose());
        
        // ----- Form ----- //
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Theme.palette().surface);
        
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 6, 6, 6);
        gc.anchor = GridBagConstraints.WEST;
        
        // ----- Labels ----- // 
        JLabel lblUser = makeLabel("Username:");
        JLabel lblPass = makeLabel("Password:");
        
        // username
        gc.gridx = 0; gc.gridy = 0; form.add(lblUser, gc);
        gc.gridx = 1; form.add(txtUser, gc);
        
        // password
        gc.gridx = 0; gc.gridy = 1; form.add(lblPass, gc);
        gc.gridx = 1; form.add(txtPass, gc);
        
        // message area
        gc.gridx = 0; gc.gridy = 2; gc.gridwidth = 2; form.add(msg, gc);
        
        // ----- Button Layout ----- // 
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.setBackground(Theme.palette().surface);
        buttons.add(btnCancel);
        buttons.add(btnLogin);
        
        // set default button to login
        getRootPane().setDefaultButton(btnLogin);
        
        // ----- Layout ----- // 
        setLayout(new BorderLayout());
        add(form, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
    }

    // ----- Actions ----- //
    private void onLogin(ActionEvent e) 
    {
        String u = txtUser.getText().trim();
        String p = new String(txtPass.getPassword()); 

        // authenticate
        if (authenticator.verify(u, p)) 
        {
            authenticated = true;
            dispose();
        }
        else 
        {
            msg.setText("Invalid username or password.");
        }
    }
    
    public boolean isAuthenticated() 
    {
        return authenticated;
    }
    
    public String getUsername() 
    {
        return txtUser.getText().trim();
    }
}