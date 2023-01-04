package com.github.imdabigboss.geyserplaceholders;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class GeyserPlaceholders extends JavaPlugin {

    @Override
    public void onEnable() {
        if (Bukkit.getPluginManager().getPlugin("Geyser-Spigot") == null) {
            getLogger().warning("Geyser not installed. Disabling plugin");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            getLogger().warning("PlaceholderAPI not installed. Disabling plugin");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        saveDefaultConfig();

        new Placeholder(this).register();
    }
}
