/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package Assignment2.UI;

/**
 *
 * @author megan
 */

import Assignment2.UI.Template.BaseThemedDialog;
import javax.swing.*;
import java.awt.*;
import java.util.Optional;

public class AddItemDialog extends BaseThemedDialog 
{ 
    // ----- Returned data  ----- //
    public static final class Data {
        public final String name;
        public final String category;
        public final int quantity;
        public final double unitCost;
        
        public Data(String name, String category, int quantity, double unitCost) 
        {
            this.name = name;
            this.category = category;
            this.quantity = quantity;
            this.unitCost = unitCost;
        }
        
        @Override
        public String toString() 
        {
            return String.format("Data{name='%s', category='%s', qty=%d, unitCost=%.2f}", name, category, quantity, unitCost);
        }
    }
    
    private boolean saved = false;
    
    // ----- UI ----- // 
    private final JTextField txtName = new JTextField(18);
    private final JTextField txtCategory = new JTextField(14);
    private final JSpinner spQty = new JSpinner(new SpinnerNumberModel(1, 0, 1_000_000, 1));
    private final JSpinner spUnitCost  = new JSpinner(new SpinnerNumberModel(0.00, 0.00, 1_000_000.00, 0.10));
    
    private final JButton btnAdd = new JButton("Add");
    private final JButton btnCancel = new JButton("Cancel");
    
    private AddItemDialog(Window owner) 
    {
        super(owner, "Add Item", ModalityType.APPLICATION_MODAL); 
        buildUI();
        pack();
        setLocationRelativeTo(getOwner());
    }
    
    private void buildUI() 
    {
        // form
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Theme.palette().surface);
        form.setBorder(BorderFactory.createEmptyBorder(16,16,8,16));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 8, 8, 8);
        gc.anchor = GridBagConstraints.WEST;
        gc.fill  = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1.0;
        
        // altered version of make label
        int y = 0;
        gc.gridx = 0; gc.gridy = y; form.add(makeLabel("Name:"), gc);
        gc.gridx = 1; form.add(txtName, gc);
        
        y++; gc.gridx = 0; gc.gridy = y; form.add(makeLabel("Category:"), gc);
        gc.gridx = 1; form.add(txtCategory, gc);
        
        y++; gc.gridx = 0; gc.gridy = y; form.add(makeLabel("Quantity:"), gc);
        gc.gridx = 1; form.add(spQty, gc);
        
        y++; gc.gridx = 0; gc.gridy = y; form.add(makeLabel("Unit Cost:"), gc);
        gc.gridx = 1; form.add(spUnitCost, gc);
        
        // buttons
        Theme.styleSecondaryButton(btnCancel);
        Theme.stylePrimaryButton(btnAdd);
        
        btnAdd.addActionListener(e -> onSave());
        btnCancel.addActionListener(e -> dispose());
        getRootPane().setDefaultButton(btnAdd);
        
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.setBackground(Theme.palette().surface);
        buttons.setBorder(BorderFactory.createEmptyBorder(0,16,16,16));
        buttons.add(btnCancel);
        buttons.add(btnAdd);
        
        setLayout(new BorderLayout());
        add(form, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
    }
    
    // temporary
    private void onSave() 
    {
        String name = txtName.getText().trim();
        if (name.isEmpty()) 
        {
            JOptionPane.showMessageDialog(this, "Please enter a name.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        saved = true;
        dispose();
    }
    
    public boolean isSaved() { return saved; }
    
    public Data getData() 
    {
        return new Data(txtName.getText().trim(), txtCategory.getText().trim(), ((Number) spQty.getValue()).intValue(), ((Number) spUnitCost.getValue()).doubleValue());
    }
    
    // returns data
    public static Optional<Data> show(Window owner) 
    {
        AddItemDialog dlg = new AddItemDialog(owner);
        dlg.setVisible(true);
        return dlg.isSaved() ? Optional.of(dlg.getData()) : Optional.empty();
    }
}