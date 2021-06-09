package dev.thelecrafter.rpg.challenger;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.thelecrafter.rpg.challenger.util.ConfigFileManager;
import dev.thelecrafter.rpg.challenger.util.sql.DatabaseTable;
import dev.thelecrafter.rpg.challenger.util.sql.DatabaseTableType;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.*;

public final class ChallengerPlugin extends JavaPlugin implements Listener {

    public static Plugin INSTANCE = null;
    public static Connection DATABASE_CONNECTION = null;

    @Override
    public void onEnable() {
        INSTANCE = getProvidingPlugin(this.getClass());
        initConfig();
        initDatabase();
        Bukkit.getPluginManager().registerEvents(this, INSTANCE);
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
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Couldn't find MySQL driver! Disabling...");
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(INSTANCE);
        }
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + ConfigFileManager.get().getString("mysql.host") + ":" + ConfigFileManager.get().getString("mysql.port") + "/" + ConfigFileManager.get().getString("mysql.database"));
        config.setUsername(ConfigFileManager.get().getString("mysql.username"));
        config.setPassword(ConfigFileManager.get().getString("mysql.password"));
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        HikariDataSource dataSource = new HikariDataSource(config);
        try {
            DATABASE_CONNECTION = dataSource.getConnection();
            System.out.println("Successfully connected to MySQL!");
        } catch (SQLException throwables) {
            System.out.println("Couldn't connect to MySQL! Disabling...");
            throwables.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(INSTANCE);
        }
        // CREATE ALL TABLES
        try {
            Statement createZombieBossTable = DATABASE_CONNECTION.createStatement();
            createZombieBossTable.execute("CREATE TABLE IF NOT EXISTS zombie_boss (uuid uuid NOT NULL, tier_one_kills int NOT NULL, tier_two_kills int NOT NULL, tier_three_kills int NOT NULL, tier_four_kills int NOT NULL, tier_five_kills int NOT NULL)");
            createZombieBossTable.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        DatabaseTable.insertDefault(DatabaseTableType.ZOMBIE_BOSS, event.getPlayer().getUniqueId());
        Bukkit.getScheduler().scheduleSyncDelayedTask(INSTANCE, () -> {
            DatabaseTable.add(DatabaseTableType.ZOMBIE_BOSS, event.getPlayer().getUniqueId(), "tier_one_kills", 2);
            Bukkit.getScheduler().scheduleSyncDelayedTask(INSTANCE, () -> {
                try {
                    Statement statement = DATABASE_CONNECTION.createStatement();
                    statement.execute("DELETE FROM zombie_boss WHERE uuid = " + event.getPlayer().getUniqueId());
                    statement.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }, 20 * 20);
        }, 3 * 20);
    }
}
