package com.bossplugin;

import com.bossplugin.boss.BossManager;
import com.bossplugin.commands.BossCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class BossPlugin extends JavaPlugin {

    private static BossPlugin instance;
    private BossManager bossManager;

    @Override
    public void onEnable() {
        instance = this;
        
        // Save default config
        saveDefaultConfig();
        
        // Initialize Boss Manager
        bossManager = new BossManager(this);
        
        // Register Commands
        getCommand("boss").setExecutor(new BossCommand(this));
        getCommand("summonboss").setExecutor(new BossCommand(this));
        
        getLogger().info("BossPlugin has been enabled! Advanced boss system is ready.");
        getLogger().info("Loaded " + bossManager.getLoadedBossesCount() + " boss types.");
    }

    @Override
    public void onDisable() {
        if (bossManager != null) {
            bossManager.cleanup();
        }
        getLogger().info("BossPlugin has been disabled!");
    }

    public static BossPlugin getInstance() {
        return instance;
    }

    public BossManager getBossManager() {
        return bossManager;
    }
}
