package com.github.imdabigboss.geyserplaceholders;

import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class GeyserPlaceholders extends JavaPlugin {

    private static final int BSTATS_PLUGIN_ID = 17347;

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

        if (getConfig().getBoolean("disable_metrics")) {
            return;
        }

        Metrics metrics = new Metrics(this, BSTATS_PLUGIN_ID);
        metrics.addCustomChart(new SimplePie("geyser_version", () -> Objects.requireNonNull(getServer().getPluginManager().getPlugin("Geyser-Spigot")).getDescription().getVersion()));
        metrics.addCustomChart(new SimplePie("placeholderapi_version", () -> Objects.requireNonNull(getServer().getPluginManager().getPlugin("PlaceholderAPI")).getDescription().getVersion()));
    }
}
