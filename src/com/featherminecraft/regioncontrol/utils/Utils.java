package com.featherminecraft.regioncontrol.utils;

import java.util.List;
import java.util.Set;

import org.bukkit.plugin.Plugin;
import com.featherminecraft.regioncontrol.RegionControl;
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

    public String getOwner(ProtectedRegion region) {
        ConfigUtils configutils = new ConfigUtils();
        Set<String> owners = region.getOwners().getPlayers();
        List<String> factions = configutils.getConfigValues("factions");
        for( String owner : owners)
        {
            for( String faction : factions)
            {
                if(owner == faction)
                    return owner;
            }
        }
        //Shouldn't Get Here.
        return null;
    }
}
