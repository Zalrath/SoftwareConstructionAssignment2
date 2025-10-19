/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Project_p2;

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

import java.io.*;
import java.nio.file.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class SettingsManager {

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
}
