package com.featherminecraft.RegionControl;

import java.util.logging.Level;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Config {
    protected static FileConfiguration mainconfig;
    protected static FileConfiguration data;
    protected static File mainconfigfile;
    protected static File dataFile;
    
    protected void reloadMainConfig()
    {
        mainconfigfile = new File(RegionControl.plugin.getDataFolder(), "config.yml");
        if (!mainconfigfile.exists())
        {
            RegionControl.plugin.saveResource("config.yml", false);
        }
        mainconfig = YamlConfiguration.loadConfiguration(mainconfigfile);
    }

    protected void reloadDataFile()
    {
        dataFile = new File(RegionControl.plugin.getDataFolder(), "data/data.yml");
        dataFile.getParentFile().mkdirs();
        if (!dataFile.exists())
        {
            RegionControl.plugin.saveResource("data/data.yml", false);
        }
        data = YamlConfiguration.loadConfiguration(dataFile);
    }

    protected FileConfiguration getDataFile() {
        if ( data == null )
        {
            this.reloadDataFile();
        }
        return data;
    }

    public FileConfiguration getMainConfig() {
        if ( mainconfig == null )
        {
            this.reloadMainConfig();
        }
        return mainconfig;
    }

    protected Boolean saveDataFile()
    {
        if (data == null || dataFile == null) {
            return false;
            }
        try {
            getDataFile().save(dataFile);
        } catch (IOException ex) {
            RegionControl.plugin.getLogger().log(Level.SEVERE, "Could not save data config", ex);
            return false;
        }
        return true;
    }

    protected Boolean saveMainConfig()
    {
        if (mainconfig == null || mainconfigfile == null) {
            return false;
            }
        try {
            getMainConfig().save(mainconfigfile);
        } catch (IOException ex) {
            RegionControl.plugin.getLogger().log(Level.SEVERE, "Could not save data config", ex);
            return false;
        }
        return true;
    }
}
