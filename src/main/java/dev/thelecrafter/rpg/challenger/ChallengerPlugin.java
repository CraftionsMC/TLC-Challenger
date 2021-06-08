package dev.thelecrafter.rpg.challenger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class ChallengerPlugin extends JavaPlugin {

    public static Plugin INSTANCE;

    @Override
    public void onEnable() {
        INSTANCE = getProvidingPlugin(this.getClass());
    }
}
