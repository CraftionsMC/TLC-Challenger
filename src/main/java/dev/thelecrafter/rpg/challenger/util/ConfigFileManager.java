package dev.thelecrafter.rpg.challenger.util;

import dev.thelecrafter.rpg.challenger.ChallengerPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class ConfigFileManager {

    private static File file;
    private static FileConfiguration customFile;

    // Finds or generates the custom file
    public static void setup() {
        file = new File(ChallengerPlugin.INSTANCE.getDataFolder(), "config.yml");

        if(!file.exists()) {
            try {
                URLReader.copyURLtoFile(new URL("https://raw.githubusercontent.com/CraftionsMC/TLC-Challenger/master/src/main/resources/defaultConfig.yml"), file);
            } catch (IOException e) {
                // Just a catch lol
            }
        }

        customFile = YamlConfiguration.loadConfiguration(file);

    }

    // Get the file configuration
    public static FileConfiguration get() {
        return customFile;
    }

    // Save the file configuration
    public static void save() {
        try {
            customFile.save(file);
        } catch (IOException e) {
            System.out.println("Failed to save the config file!");
        }
    }

    // Reload the file configuration
    public static void reload() {
        customFile = YamlConfiguration.loadConfiguration(file);
    }

}
