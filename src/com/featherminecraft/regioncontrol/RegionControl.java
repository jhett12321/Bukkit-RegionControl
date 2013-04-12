package com.featherminecraft.regioncontrol;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public final class RegionControl extends JavaPlugin {

private PlayerListener playerlistener;
private static RegionControl plugin;

    @Override
    public void onEnable() {
        RegionControl.plugin = this;

        PluginManager pm = getServer().getPluginManager();
        if(!WorldGuardAvailable())
        {
            setEnabled(false);
        }
//Server Setup
        ServerLogic serverlogic = new ServerLogic();
        serverlogic.init();
        if(SpoutAvailable())
        {
            
        } else {
            playerlistener = new PlayerListener();
            pm.registerEvents(playerlistener, this);
        }
    }

    @Override
    public void onDisable() {
    }

    public static boolean WorldGuardAvailable()
    {
        Plugin worldguard = RegionControl.plugin.getServer().getPluginManager().getPlugin("WorldGuard");

        if (worldguard == null) {
            //Worldguard is not installed
            return false;
        } else if (!worldguard.isEnabled()) {
            //Worldguard is not enabled
            return false;
        } else {
            return true;
        }
    }

    public static WorldGuardPlugin getWorldGuard() {
        Plugin plugin = RegionControl.plugin.getServer().getPluginManager().getPlugin("WorldGuard");
     
        if(WorldGuardAvailable())
            return (WorldGuardPlugin) plugin;
        else
            return null;
    }

    public static boolean SpoutAvailable()
    {
        Plugin spoutplugin = RegionControl.plugin.getServer().getPluginManager().getPlugin("SpoutPlugin");

        if (spoutplugin == null) {
            //Spout is not installed
            return false;
        } else if (!spoutplugin.isEnabled()) {
            //Spout is not enabled
            return false;
        } else {
            return true;
        }
    }
}
