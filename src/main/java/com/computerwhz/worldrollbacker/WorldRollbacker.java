package com.computerwhz.worldrollbacker;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.mvplugins.multiverse.core.MultiverseCore;

public final class WorldRollbacker extends JavaPlugin {

    private static WorldRollbacker instance;
    private static MultiverseCore multiverseCore;


    @Override
    public void onEnable() {
        // Plugin startup logic

        instance = this;
        multiverseCore = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
