/*
 * click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Assignment2.UI.Template;

/**
 *
 * @author megan
 */

import java.util.HashSet;
import java.util.Set;

public class ToggleableButtonGroup
{

    private final Set<ToggleableButton> buttons = new HashSet<>();
    private ToggleableButton selectedButton = null;

    // ----- api: button management ----- //
    public void addButton(ToggleableButton btn)
    {
        buttons.add(btn);
        btn.setGrouped(true, this);
    }

    // ----- core logic ----- //
    void selectButton(ToggleableButton btn)
    {
        // deselect all buttons
        for (ToggleableButton b : buttons)
        {
            if (b.isSelected())
            {
                b.setSelected(false);
            }
        }
        
        // select the new button
        btn.setSelected(true);
        selectedButton = btn;
    }

    // ----- api retrieval ----- //
    public ToggleableButton getSelectedButton()
    {
        return selectedButton;
    }
}