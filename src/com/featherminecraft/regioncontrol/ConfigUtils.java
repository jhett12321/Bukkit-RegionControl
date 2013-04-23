package com.featherminecraft.regioncontrol;

import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class ConfigUtils {
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
