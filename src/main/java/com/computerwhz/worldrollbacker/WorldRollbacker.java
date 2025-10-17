package com.computerwhz.worldrollbacker;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class WorldRollbacker extends JavaPlugin {

    private static WorldRollbacker instance;

    private FileConfiguration config;
    private File configFile;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        saveDefaultConfig();
        this.configFile = new File(getDataFolder(), "config.yml");
        this.config = YamlConfiguration.loadConfiguration(configFile);

        if (!config.getBoolean("enabled")){
            Bukkit.getLogger().warning("Plugin is disabled in config shutting down");
            getServer().getPluginManager().disablePlugin(this);
        }

        File source = new File(getServer().getWorldContainer(), config.getString("source-world"));
        File destination = new File(getServer().getWorldContainer(), config.getString("destination-world"));

        if (!source.exists() || !destination.exists()) {
            Bukkit.getLogger().warning("Could not get source or destination worlds check config");
            Bukkit.getPluginManager().disablePlugin(this);
        }
        WorldResetter.resetWorld(destination, source);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static WorldRollbacker getInstance(){
        return instance;
    }

}
