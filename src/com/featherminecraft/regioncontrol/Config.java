package com.featherminecraft.regioncontrol;

import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import java.io.File;
import java.io.IOException;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;


//TODO: Incomplete.
public class Config {
    private static FileConfiguration mainconfig;
    private static FileConfiguration data;
    private static File mainconfigfile;
    private static File dataFile;
    
    public void reloadMainConfig()
    {
        mainconfigfile = new File(RegionControl.plugin.getDataFolder(), "config.yml");
        if (!mainconfigfile.exists())
        {
            RegionControl.plugin.saveResource("mainconfig.yml", false);
        }
        mainconfig = YamlConfiguration.loadConfiguration(mainconfigfile);
    }

    public void reloadDataFile()
    {
        dataFile = new File(RegionControl.plugin.getDataFolder(), "Data/data.yml");
        dataFile.getParentFile().mkdirs();
        if (!dataFile.exists())
        {
            RegionControl.plugin.saveResource("Data/data.yml", false);
        }
        data = YamlConfiguration.loadConfiguration(dataFile);
    }

    public FileConfiguration getDataFile() {
        if ( data == null )
        {
            this.reloadDataFile();
        }
        return data;
    }

    public FileConfiguration getMainConfig() {
        if ( mainconfig == null )
        {
            this.reloadDataFile();
        }
        return mainconfig;
    }

    public void saveDataFile()
    {
        if (data == null || dataFile == null) {
            return;
            }
        try {
            getDataFile().save(dataFile);
        } catch (IOException ex) {
            RegionControl.plugin.getLogger().log(Level.SEVERE, "Could not save data config", ex);
        }
    }

    public void saveMainConfig()
    {
        if (data == null || dataFile == null) {
            return;
            }
        try {
            getDataFile().save(dataFile);
        } catch (IOException ex) {
            RegionControl.plugin.getLogger().log(Level.SEVERE, "Could not save data config", ex);
        }
    }

    public String getDefaultFaction() {
        Set<String> factions = mainconfig.getConfigurationSection("factions").getKeys(false);
        for(String faction : factions)
        {
            if(mainconfig.getBoolean("factions." + faction + ".default"))
            {
                return faction;
            }
        }
        return null;
    }

    public Map<String, Location> getControlPointsForRegion(CapturableRegion capturableregion) {
        // TODO Auto-generated method stub
        return null;
    }

    public Location getSpawnPointForRegion(CapturableRegion capturableregion) {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean getConfigBoolean(String string) {
        // TODO Auto-generated method stub
        return false;
    }
}
