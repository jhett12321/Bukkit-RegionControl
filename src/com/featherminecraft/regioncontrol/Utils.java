package com.featherminecraft.regioncontrol;

import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.plugin.SpoutPlugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

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
        Utils utils = new Utils();
        if(!utils.getConfigBoolean("useSpout")) {
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

    private List<String> values;
    
    public String getConfigValue(String option)
    {
        String value = RegionControl.plugin.getConfig().getString(option);
        return value;
    }

    public boolean getConfigBoolean(String option)
    {
        boolean value = RegionControl.plugin.getConfig().getBoolean(option);
        return value;
    }
    
    public List<String> getConfigValues(String option)
    {
        values = RegionControl.plugin.getConfig().getStringList(option);
        return values;
    }

    public List<String> getConfigSectionValues (String sectionname)
    {
        ConfigurationSection section = RegionControl.plugin.getConfig().getConfigurationSection(sectionname);
        values.addAll(section.getKeys(false));
        return values;
    }
    
    public void setConfigValue(String option, String value)
    {
        RegionControl.plugin.getConfig().set(option, value);
    }
    
    public void setConfigValues(String option, List<String> values)
    {
        RegionControl.plugin.getConfig().set(option, values);
    }
}
