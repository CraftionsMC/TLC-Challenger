package dev.thelecrafter.rpg.challenger;

import dev.thelecrafter.rpg.challenger.util.ConfigFileManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ChallengerPlugin extends JavaPlugin {

    public static Plugin INSTANCE = null;
    public static Connection DATABASE_CONNECTION = null;

    @Override
    public void onEnable() {
        INSTANCE = getProvidingPlugin(this.getClass());
        initConfig();
        initDatabase();
    }

    @Override
    public void onDisable() {
        if (DATABASE_CONNECTION != null) {
            try {
                System.out.println("Closing database connection...");
                DATABASE_CONNECTION.close();
            } catch (SQLException throwables) {
                System.out.println("There was an error closing the connection!");
                throwables.printStackTrace();
            }
        }
    }

    private void initConfig() {
        ConfigFileManager.setup();
        BufferedReader reader = null;
        try {
            URL url = new URL("https://raw.githubusercontent.com/CraftionsMC/TLC-Challenger/master/src/main/resources/defaultConfig.yml");
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (reader == null) {
            System.out.println("There was an error opening the BufferedReader of the default config! Disabling...");
            Bukkit.getPluginManager().disablePlugin(INSTANCE);
        }
        YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(reader);
        for (String key : defaultConfig.getKeys(true)) {
            if (!ConfigFileManager.get().contains(key)) {
                System.out.println("Couldn't find key '" + key + "' in your config, so it has been reset.");
                ConfigFileManager.get().set(key, defaultConfig.get(key));
                ConfigFileManager.save();
            }
        }
    }

    private void initDatabase() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Couldn't find PostgreSQL driver! Disabling...");
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(INSTANCE);
        }
        String jdbcURL = "jdbc:postgresql://" + ConfigFileManager.get().getString("postgres.host") + ":" +
                ConfigFileManager.get().getString("postgres.port") + "/" +
                ConfigFileManager.get().getString("postgres.database");
        String username = ConfigFileManager.get().getString("postgres.username");
        String password = ConfigFileManager.get().getString("postgres.password");
        try {
            DATABASE_CONNECTION = DriverManager.getConnection(jdbcURL, username, password);
            System.out.println("Successfully connected to PostgreSQL!");
        } catch (SQLException throwables) {
            System.out.println("There was an error while trying to connect to PostgreSQL! Disabling plugin...");
            throwables.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(INSTANCE);
        }
    }
}
