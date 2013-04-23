package com.featherminecraft.regioncontrol;

import java.util.List;

import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.plugin.SpoutPlugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class Utils {
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
        ConfigUtils configutils = new ConfigUtils();
        if(!configutils.getConfigBoolean("useSpout")) {
        	//Spout is not enabled in the config
        	return false;
        } else if (spoutplugin == null) {
            //Spout is not installed
            return false;
        } else if (!spoutplugin.isEnabled()) {
            //Spout is not enabled
            return false;
        } else {
            return true;
        }
    }

    public static SpoutPlugin getSpoutPlugin() {
        Plugin spoutplugin = RegionControl.plugin.getServer().getPluginManager().getPlugin("SpoutPlugin");
     
        if(SpoutAvailable())
            return (SpoutPlugin) spoutplugin;
        else
            return null;
    }
}
