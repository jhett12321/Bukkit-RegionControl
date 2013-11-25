package com.featherminecraft.RegionControl.utils;

import org.bukkit.plugin.Plugin;

import com.featherminecraft.RegionControl.Config;
import com.featherminecraft.RegionControl.RegionControl;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class Utils {
    public static WorldGuardPlugin getWorldGuard() {
        Plugin plugin = RegionControl.plugin.getServer().getPluginManager()
                .getPlugin("WorldGuard");
        if (WorldGuardAvailable()) {
            return (WorldGuardPlugin) plugin;
        } else {
            return null;
        }
    }

    public static boolean SpoutAvailable() {
        Plugin spoutplugin = RegionControl.plugin.getServer()
                .getPluginManager().getPlugin("Spout");
        if (!new Config().getMainConfig().getBoolean("spout.enabled")) {
            // Spout is not enabled in the config
            return false;
        } else if (spoutplugin == null) {
            // Spout is not installed
            return false;
        } else if (!spoutplugin.isEnabled()) {
            // Spout is not enabled
            return false;
        } else {
            return true;
        }
    }

    public static boolean VaultAvailable() {
        Plugin vault = RegionControl.plugin.getServer().getPluginManager()
                .getPlugin("Vault");

        if (vault == null) {
            // Worldguard is not installed
            return false;
        } else if (!vault.isEnabled()) {
            // Worldguard is not enabled
            return false;
        } else {
            return true;
        }
    }

    public static boolean WorldGuardAvailable() {
        Plugin worldguard = RegionControl.plugin.getServer().getPluginManager()
                .getPlugin("WorldGuard");

        if (worldguard == null) {
            // Worldguard is not installed
            return false;
        } else if (!worldguard.isEnabled()) {
            // Worldguard is not enabled
            return false;
        } else {
            return true;
        }
    }
}
