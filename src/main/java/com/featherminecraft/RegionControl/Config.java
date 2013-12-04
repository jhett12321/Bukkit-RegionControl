package com.featherminecraft.RegionControl;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;
import com.featherminecraft.RegionControl.capturableregion.ControlPoint;

public class Config
{
    private static FileConfiguration mainconfig;
    private static FileConfiguration dataConfig;
    private static File mainconfigfile;
    private static File dataFile;
    
    public FileConfiguration getDataConfig()
    {
        if(dataConfig == null)
        {
            reloadDataFile();
        }
        return dataConfig;
    }
    
    public String getDefaultFaction()
    {
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
    
    public FileConfiguration getMainConfig()
    {
        if(mainconfig == null)
        {
            reloadMainConfig();
        }
        return mainconfig;
    }
    
    public void reloadDataFile()
    {
        dataFile = new File(RegionControl.plugin.getDataFolder(), "data/data.yml");
        dataFile.getParentFile().mkdirs();
        if(!dataFile.exists())
        {
            RegionControl.plugin.saveResource("data/data.yml", false);
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }
    
    public void reloadMainConfig()
    {
        mainconfigfile = new File(RegionControl.plugin.getDataFolder(), "config.yml");
        if(!mainconfigfile.exists())
        {
            RegionControl.plugin.saveResource("config.yml", false);
        }
        mainconfig = YamlConfiguration.loadConfiguration(mainconfigfile);
    }
    
    public void saveAll()
    {
        RegionControl.plugin.getLogger().info("Saving Region Data...");
        // Utilities Begin
        
        // Utilities End
        
        // Begin retrieving of Region Data
        Collection<CapturableRegion> regions = ServerLogic.capturableRegions.values();
        
        for(CapturableRegion region : regions)
        {
            if(!region.isSpawnRegion())
            {
                // Not Here: DisplayName, Spawnpoint
                // Variables being set
                // Float baseInfluence = region.getBaseInfluence();
                Faction influenceOwner = region.getInfluenceOwner();
                String configInfluenceOwner = influenceOwner.getId();
                int configInfluence = region.getInfluenceMap().get(influenceOwner).intValue();
                String configOwner = region.getOwner().getId();
                
                // Key location variables
                String configWorld = region.getWorld().getName();
                String configId = region.getRegionId();
                
                // Saving of data
                // TODO Later Implementation: In-game setting of config values.
                // mainconfig.set("worlds." + configWorld + ".regions." +
                // configId + ".baseinfluence", baseInfluence.intValue());
                
                dataConfig.set("worlds." + configWorld + ".regions." + configId + ".influenceowner", configInfluenceOwner);
                dataConfig.set("worlds." + configWorld + ".regions." + configId + ".influence", configInfluence);
                dataConfig.set("worlds." + configWorld + ".regions." + configId + ".owner", configOwner);
                
                // Control Points
                List<ControlPoint> controlPoints = region.getControlPoints();
                for(ControlPoint controlPoint : controlPoints)
                {
                    Faction controlPointInfluenceOwner = controlPoint.getInfluenceOwner();
                    String configControlPointInfluenceOwner = controlPointInfluenceOwner.getId();
                    int configControlPointInfluence = controlPoint.getInfluenceMap().get(controlPointInfluenceOwner).intValue();
                    String configControlPointId = controlPoint.getIdentifier();
                    String configControlPointOwner = controlPoint.getOwner().getId();
                    
                    dataConfig.set("worlds." + configWorld + ".regions." + configId + ".controlpoints." + configControlPointId + ".influenceowner", configControlPointInfluenceOwner);
                    dataConfig.set("worlds." + configWorld + ".regions." + configId + ".controlpoints." + configControlPointId + ".influence", configControlPointInfluence);
                    dataConfig.set("worlds." + configWorld + ".regions." + configId + ".controlpoints." + configControlPointId + ".owner", configControlPointOwner);
                }
            }
            else
            {
                String configOwner = region.getOwner().getId();
                String configWorld = region.getWorld().getName();
                String configId = region.getRegionId();
                
                dataConfig.set("worlds." + configWorld + ".regions." + configId + ".owner", configOwner);
            }
        }
        
        if(saveDataFile() && saveMainConfig())
        {
            RegionControl.plugin.getLogger().info("Save Complete!");
        }
        else
        {
            RegionControl.plugin.getLogger().severe("Save Failed. Please check your plugin directory has write permissions.");
        }
    }
    
    public Boolean saveDataFile()
    {
        if(dataConfig == null || dataFile == null)
        {
            return false;
        }
        try
        {
            getDataConfig().save(dataFile);
        }
        catch(IOException ex)
        {
            RegionControl.plugin.getLogger().log(Level.SEVERE, "Could not save data config", ex);
            return false;
        }
        return true;
    }
    
    public Boolean saveMainConfig()
    {
        if(mainconfig == null || mainconfigfile == null)
        {
            return false;
        }
        try
        {
            getMainConfig().save(mainconfigfile);
        }
        catch(IOException ex)
        {
            RegionControl.plugin.getLogger().log(Level.SEVERE, "Could not save data config", ex);
            return false;
        }
        return true;
    }
}
