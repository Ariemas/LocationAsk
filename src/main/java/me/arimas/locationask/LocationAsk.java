package me.arimas.locationask;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class LocationAsk extends JavaPlugin {

    private LocationCommandExecutor locationCommandExecutor;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        FileConfiguration config = getConfig();
        int requestTimeout = config.getInt("request-timeout", 60);
        int requestCooldown = config.getInt("request-cooldown", 10);

        locationCommandExecutor = new LocationCommandExecutor(this, requestTimeout, requestCooldown);
        getCommand("requestlocation").setExecutor(locationCommandExecutor);
        getCommand("locationaccept").setExecutor(locationCommandExecutor);
    }
}