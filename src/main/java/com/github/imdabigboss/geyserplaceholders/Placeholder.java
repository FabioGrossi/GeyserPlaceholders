package com.github.imdabigboss.geyserplaceholders;

import com.nukkitx.protocol.bedrock.BedrockPacketCodec;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.util.DeviceOs;
import org.geysermc.floodgate.util.InputMode;
import org.geysermc.geyser.GeyserImpl;
import org.geysermc.geyser.network.MinecraftProtocol;
import org.geysermc.geyser.session.GeyserSession;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Placeholder extends PlaceholderExpansion {

    private final GeyserPlaceholders plugin;

    private final Map<String, String> deviceOsDictionary = new HashMap<>();
    private final Map<String, String> inputModeDictionary = new HashMap<>();

    public Placeholder(GeyserPlaceholders plugin) {
        this.plugin = plugin;

        generateDictionaries();
    }

    private void generateDictionaries() {
        FileConfiguration configuration = plugin.getConfig();
        ConfigurationSection platformSection = configuration.getConfigurationSection("placeholder-dictionary.platform");
        ConfigurationSection inputModeSection = configuration.getConfigurationSection("placeholder-dictionary.input-mode");

        if (platformSection == null || inputModeSection == null) {
            plugin.getLogger().warning("Configuration file corrupted. Try deleting it and restarting the server. Disabling the plugin");
            plugin.getPluginLoader().disablePlugin(plugin);
            return;
        }

        platformSection.getKeys(false).forEach(key -> deviceOsDictionary.put(key.toLowerCase(), platformSection.getString(key)));

        inputModeSection.getKeys(false).forEach(key -> inputModeDictionary.put(key.toLowerCase(), platformSection.getString(key)));

    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public @NotNull String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "geyser";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null) {
            return "";
        }

        return switch (identifier) {
            case "java_version" -> getJavaVersion();
            case "bedrock_version" -> getBedrockVersion();
            case "version" -> GeyserImpl.VERSION;
            case "git_version" -> GeyserImpl.GIT_VERSION;
            case "platform" -> getPlayerPlatform(player);
            case "game_version" -> getPlayerGameVersion(player);
            case "input_mode" -> getPlayerInputMode(player);
            default -> null;
        };

    }

    /**
     * Get the supported java version
     *
     * @return The supported java version
     */
    public String getJavaVersion() {
        String javaVersions;
        List<String> supportedJavaVersions = MinecraftProtocol.getJavaVersions();
        if (supportedJavaVersions.size() > 1) {
            javaVersions = supportedJavaVersions.get(0) + " - " + supportedJavaVersions.get(supportedJavaVersions.size() - 1);
        } else {
            javaVersions = supportedJavaVersions.get(0);
        }

        return javaVersions;
    }

    /**
     * Get the supported bedrock version
     *
     * @return The supported bedrock version
     */
    public String getBedrockVersion() {
        String bedrockVersions;
        List<BedrockPacketCodec> supportedCodecs = MinecraftProtocol.SUPPORTED_BEDROCK_CODECS;
        if (supportedCodecs.size() > 1) {
            bedrockVersions = supportedCodecs.get(0).getMinecraftVersion() + " - " + supportedCodecs.get(supportedCodecs.size() - 1).getMinecraftVersion();
        } else {
            bedrockVersions = MinecraftProtocol.SUPPORTED_BEDROCK_CODECS.get(0).getMinecraftVersion();
        }

        return bedrockVersions;
    }

    /**
     * Get the platform of the specified player
     *
     * @param player The player
     * @return The player's platform version
     */
    public String getPlayerPlatform(Player player) {
        GeyserSession geyserPlayer = GeyserImpl.getInstance().connectionByUuid(player.getUniqueId());

        if (geyserPlayer == null) return deviceOsDictionary.get("java");

        DeviceOs deviceOs = geyserPlayer.getClientData().getDeviceOs();

        return deviceOsDictionary.get(deviceOs.name().toLowerCase());
    }

    /**
     * Get the game version of the specified player
     *
     * @param player The player
     * @return The player's game version
     */
    public String getPlayerGameVersion(Player player) {
        GeyserSession geyserPlayer = GeyserImpl.getInstance().connectionByUuid(player.getUniqueId());

        if (geyserPlayer == null) return "";

        return geyserPlayer.getClientData().getGameVersion();
    }

    /**
     * Get the input mode of the specified player
     *
     * @param player The player
     * @return The player's input mode
     */
    public String getPlayerInputMode(Player player) {
        GeyserSession geyserPlayer = GeyserImpl.getInstance().connectionByUuid(player.getUniqueId());

        if (geyserPlayer == null) return "";

        InputMode inputMode = geyserPlayer.getClientData().getCurrentInputMode();

        return inputModeDictionary.get(inputMode.name().toLowerCase());
    }
}