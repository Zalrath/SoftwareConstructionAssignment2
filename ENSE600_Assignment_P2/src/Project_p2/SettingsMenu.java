/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Project_p2;

/**
 *
 * @author corin
 *
**/
import java.io.IOException;
import java.util.Scanner;

/*
        SettingsManager settingsmanager = new SettingsManager();
        SettingsMenu setmenu = new SettingsMenu(settingsmanager);


        File itemFile = new File("items.txt");
        File logFile = new File("purchases.txt");
        File settingsFile = new File("settings.txt"); 
        
        // if the files exist load their contents
        if (itemFile.exists()) manager.loadItems("items.txt");
        if (logFile.exists()) manager.loadPurchases("purchases.txt");
        if (settingsFile.exists()) settingsmanager.loadSettings("settings.txt");
        

        setmenu.showMenu();

*/




public class SettingsMenu {

    private final Scanner input = new Scanner(System.in);
    private final SettingsManager settings;

    public SettingsMenu(SettingsManager settings) {
        this.settings = settings;
    }

    public void showMenu() {
        boolean running = true;
        Formatting.printBar();
        Formatting.printBorder(3);
        Formatting.printCenteredMessage("Settings Menu");
        Formatting.printBorder(2);

        Formatting.printLeftAlignedMessage("1: Change screen width");
        Formatting.printLeftAlignedMessage("2: Change screen height");
        Formatting.printLeftAlignedMessage("3: Change date format");
        Formatting.printLeftAlignedMessage("4: View all settings");
        Formatting.printLeftAlignedMessage("5: Save settings");
        Formatting.printLeftAlignedMessage("6: Return to Home");
        
        while (running) {
            
            Formatting.printInputLine();

            String choice = input.nextLine().trim();

            switch (choice) {
                case "1" -> changeScreenWidth();
                case "2" -> changeScreenHeight();
                case "3" -> changeDateFormat();
                case "4" -> viewSettings();
                case "5" -> saveSettings();
                case "6" -> running = false;
                default -> Formatting.printLeftAlignedMessage("Invalid choice, try again.");
            }
            //choice = null;
        }
    }


    
    
    private void changeScreenWidth() {
        Formatting.printLeftAlignedMessage("Enter new screen width(Minium 80):");
        Formatting.printInputLine();
        String width = input.nextLine().trim();
        

        Formatting.printLeftAlignedMessage("Is this correct? " + width + " (Y/N), (X) to cancel");
        Formatting.printInputLine();
        boolean YNX = true;
        String yn = input.nextLine().trim().toUpperCase();

        while(YNX){
            if (yn.equals("Y")) {
                if(Integer.parseInt(width) < 80){
                    width = "80";    
                }
                settings.setSetting("screenWidth", width);
                Formatting.printLeftAlignedMessage("Screen width set to " + width);
                YNX = false;
                
            }
            else if (yn.equals("X")) {
                Formatting.printLeftAlignedMessage("Cancelled change.");
                YNX = false;
            } 
            else if (yn.equals("N")) {
                YNX = false;
                changeScreenWidth();
                
            }
            else {
                Formatting.printLeftAlignedMessage("Invalid selection, Y to confirm, N to reinput and X to cancel.");
            }
        }
  
        
    }

    private void changeScreenHeight() {
        Formatting.printLeftAlignedMessage("Enter new screen height:");
        Formatting.printInputLine();
        String height = input.nextLine().trim();
        
        
        
        Formatting.printLeftAlignedMessage("Is this correct? " + height + " (Y/N), (X) to cancel");
        Formatting.printInputLine();
        boolean YNX = true;
        String yn = input.nextLine().trim().toUpperCase();

        while(YNX){
            if (yn.equals("Y")) {
                
                settings.setSetting("screenHeight", height);
                Formatting.printLeftAlignedMessage("Screen height set to " + height);
        
                YNX = false;
                
            }
            else if (yn.equals("X")) {
                Formatting.printLeftAlignedMessage("Cancelled change.");
                YNX = false;
            } 
            else if (yn.equals("N")) {
                YNX = false;
                changeScreenWidth();
                
            }
            else {
                Formatting.printLeftAlignedMessage("Invalid selection, Y to confirm, N to reinput and X to cancel.");
            }
        }
        
        
        
        
        
        
    }

    private void changeDateFormat() {
        Formatting.printLeftAlignedMessage("Enter new date format (e.g., yyyy-MM-dd, dd/MM/yyyy):");
        Formatting.printInputLine();
        String format = input.nextLine().trim();
        
        Formatting.printLeftAlignedMessage("Is this correct? " + format + " (Y/N), (X) to cancel");
        Formatting.printInputLine();
        boolean YNX = true;
        String yn = input.nextLine().trim().toUpperCase();

        while(YNX){
            if (yn.equals("Y")) {
                
                settings.setSetting("dateFormat", format);
                Formatting.printLeftAlignedMessage("Date format set to " + format);
        
                YNX = false;
                
            }
            else if (yn.equals("X")) {
                Formatting.printLeftAlignedMessage("Cancelled change.");
                YNX = false;
            } 
            else if (yn.equals("N")) {
                YNX = false;
                changeScreenWidth();
                
            }
            else {
                Formatting.printLeftAlignedMessage("Invalid selection, Y to confirm, N to reinput and X to cancel.");
            }
        }
        
        
    }

    

    private void viewSettings() {
        Formatting.printLeftAlignedMessage("Current settings:");
        
        
        for (String key : settings.getAllSettings().keySet()) {
            Formatting.printLeftAlignedMessage(" - " + key + " = " + settings.getSetting(key));
        }
    }

    private void saveSettings() {
        try {
            settings.saveSettings("settings.txt");
            Formatting.printLeftAlignedMessage("Settings saved successfully.");
            Formatting.init(settings);
        } catch (IOException e) {
            Formatting.printLeftAlignedMessage("Error saving settings: " + e.getMessage());
        }
    }
}

