/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Assignment2.Inventory;

/**
 *
 * @author corin
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 *
 * @author corin
 * 
 * A Settings class that manages application configuration.
 * 
 * Stores screen printing size, date format, and other options.
 * Supports loading from and saving to a text file.
 * 
 */
import static Assignment2.Database.DatabaseUtil.getConnection;
import java.awt.Color;
import java.sql.*;
import java.io.*;
import java.nio.file.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class SettingsManager {

    
    
    
    
    
    private static String dateFormat = "dd MMM yyyy";
    private static String accentColor = "#48375D";
    
    /*
    public static void loadFromDatabase() {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM settings");
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {

                accentColor = rs.getString("accent_colour");
                dateFormat = rs.getString("date_format");
                System.out.println("success");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    */

    public static void loadFromDatabase() {
    try (Connection conn = getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery("SELECT * FROM settings")) {

        if (rs.next()) {
            accentColor = rs.getString("accent_colour"); // correct name
            dateFormat = rs.getString("date_format");
            System.out.println("Settings loaded successfully");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    }
    
    
    public static String getDateFormatDB() {
        return dateFormat;
    }

    
    public static Color getAccentColor() {
        return Color.decode(accentColor);
    }

    
    public static void saveDateFormat(String format) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE settings SET date_format = ?")) {
            ps.setString(1, format);
            ps.executeUpdate();
            dateFormat = format;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void saveAccentColor(Color c) {
        String hex = String.format("#%02X%02X%02X", c.getRed(), c.getGreen(), c.getBlue());
        try (Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement("UPDATE settings SET accent_colour = ?")) {
            ps.setString(1, hex);
            ps.executeUpdate();
            accentColor = hex;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
    
    
    
    
    
    
    
    //////////////////////////// OLD Stuff//////////////////////////////////////////////
    
    
    
    
    
    
    
    
    
    private Map<String, String> settings = new HashMap<>();
    
    
    
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Load from text file
    public void loadSettings(String path) throws IOException {
        settings.clear();

        List<String> lines = Files.readAllLines(Paths.get(path));
        for (String line : lines) {
            if (line.trim().isEmpty() || line.startsWith("#")) {
                continue; // skip comments/blank lines
            }
            String[] parts = line.split("=", 2);
            if (parts.length == 2) {
                settings.put(parts[0].trim(), parts[1].trim());
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Save to text file
    public void saveSettings(String path) throws IOException {
        BufferedWriter writer = Files.newBufferedWriter(Paths.get(path));
        writer.write("# Application Settings\n");
        for (Map.Entry<String, String> entry : settings.entrySet()) {
            writer.write(entry.getKey() + "=" + entry.getValue() + "\n");
        }
        writer.close();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    

    // Getters and Setters
    
    public String getSetting(String key) {
        return settings.get(key);
    }

    public void setSetting(String key, String value) {
        settings.put(key, value);
    }

    public boolean containsKey(String key) {
        return settings.containsKey(key);
    }

    public Map<String, String> getAllSettings() {
        return Collections.unmodifiableMap(settings);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// @return /
    // Specific helpers for assignment context
    
    public int getScreenWidth() {
        return getSettingAsInt("screenWidth", 80) - 2;
    }

    public int getScreenHeight() {
        return getSettingAsInt("screenHeight", 25);
    }

    public DateTimeFormatter getDateFormat() {
        String pattern = settings.getOrDefault("dateFormat", "yyyy-MM-dd");
        return DateTimeFormatter.ofPattern(pattern);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    public int getSettingAsInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(settings.getOrDefault(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public boolean getSettingAsBoolean(String key, boolean defaultValue) {
        return Boolean.parseBoolean(settings.getOrDefault(key, String.valueOf(defaultValue)));
    }

    
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Load defaults if file not found
    public void loadDefaultSettings() {
        settings.putIfAbsent("screenWidth", "180");
        settings.putIfAbsent("screenHeight", "40");
        settings.putIfAbsent("dateFormat", "yyyy-MM-dd");
       
    }
    
    
    
    
    
    
    
    
    
    /*
    
    
    
    
    
    
    */
    
    
    
    
    
}
