package com.featherminecraft.RegionControl.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.featherminecraft.RegionControl.Faction;
import com.featherminecraft.RegionControl.RegionControl;
import com.featherminecraft.RegionControl.ServerLogic;
import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;
import com.featherminecraft.RegionControl.capturableregion.ControlPoint;

public class Config
{
    /* Main Configs */
    // Main Config
    private static FileConfiguration mainConfig;
    private static File mainConfigFile;
    
    // Faction Config
    private static FileConfiguration factionConfig;
    private static File factionConfigFile;
    
    /* Region Configs */
    private static Map<String, FileConfiguration> regionConfigs;
    private static Map<String, FileConfiguration> regionData;
    
    private static Map<String, File> regionConfigFiles;
    private static Map<String, File> regionDataFiles;
    
    public static String getDefaultFaction()
    {
        Set<String> factions = factionConfig.getConfigurationSection("factions").getKeys(false);
        for(String faction : factions)
        {
            if(factionConfig.getBoolean("factions." + faction + ".default"))
            {
                return faction;
            }
        }
        return null;
    }
    
    public static FileConfiguration getFactionConfig()
    {
        if(factionConfig == null)
        {
            reloadFactionConfig();
        }
        return factionConfig;
    }
    
    public static FileConfiguration getMainConfig()
    {
        if(mainConfig == null)
        {
            reloadMainConfig();
        }
        return mainConfig;
    }
    
    public static Map<String, FileConfiguration> getRegionConfigs()
    {
        if(regionConfigs == null)
        {
            reloadRegionConfigs();
        }
        return regionConfigs;
    }
    
    public static Map<String, FileConfiguration> getRegionData()
    {
        if(regionData == null)
        {
            reloadRegionConfigs();
        }
        return regionData;
    }
    
    public static void saveAll(Boolean verbose)
    {
        if(verbose)
        {
            RegionControl.plugin.getLogger().info("Saving Region Data...");
        }
        
        // Begin retrieving of Region Data
        Collection<CapturableRegion> regions = ServerLogic.capturableRegions.values();
        boolean saveSuccessful = true;
        
        for(String configWorld : regionData.keySet())
        {
            for(CapturableRegion region : regions)
            {
                if(region.getWorld().getName() == configWorld)
                {
                    FileConfiguration dataConfig = regionData.get(configWorld);
                    File dataFile = regionDataFiles.get(configWorld);
                    
                    if(!region.isSpawnRegion())
                    {
                        // Not Here: DisplayName, Spawnpoint, Base Influence
                        // Retrieve Data from regions.
                        Faction owner = region.getOwner();
                        Faction influenceOwner = region.getInfluenceOwner();
                        if(influenceOwner == null)
                        {
                            influenceOwner = owner;
                        }
                        
                        String configOwner = owner.getId();
                        String configInfluenceOwner = influenceOwner.getId();
                        int configInfluence = region.getInfluenceMap().get(influenceOwner).intValue();
                        
                        String configId = region.getRegionId();
                        
                        // Saving of data
                        dataConfig.set("regions." + configId + ".influenceowner", configInfluenceOwner);
                        dataConfig.set("regions." + configId + ".influence", configInfluence);
                        dataConfig.set("regions." + configId + ".owner", configOwner);
                        
                        // Control Points
                        List<ControlPoint> controlPoints = region.getControlPoints();
                        for(ControlPoint controlPoint : controlPoints)
                        {
                            Faction controlPointInfluenceOwner = controlPoint.getInfluenceOwner();
                            String configControlPointInfluenceOwner = controlPointInfluenceOwner.getId();
                            int configControlPointInfluence = controlPoint.getInfluence().intValue();
                            String configControlPointId = controlPoint.getIdentifier();
                            String configControlPointOwner = controlPoint.getOwner().getId();
                            
                            dataConfig.set("regions." + configId + ".controlpoints." + configControlPointId + ".influenceowner", configControlPointInfluenceOwner);
                            dataConfig.set("regions." + configId + ".controlpoints." + configControlPointId + ".influence", configControlPointInfluence);
                            dataConfig.set("regions." + configId + ".controlpoints." + configControlPointId + ".owner", configControlPointOwner);
                        }
                    }
                    else
                    {
                        String configOwner = region.getOwner().getId();
                        String configId = region.getRegionId();
                        
                        dataConfig.set("regions." + configId + ".owner", configOwner);
                    }
                    if(!saveConfig(dataConfig, dataFile))
                    {
                        saveSuccessful = false;
                    }
                }
            }
        }
        
        if(saveSuccessful && verbose)
        {
            RegionControl.plugin.getLogger().log(Level.INFO, "Save Complete!");
        }
        else if(verbose)
        {
            RegionControl.plugin.getLogger().log(Level.SEVERE, "Save Failed. Please check your plugin directory has write permissions.");
        }
    }
    
    public static void ReloadAll()
    {
        reloadFactionConfig();
        reloadMainConfig();
        reloadRegionConfigs();
    }
    
    private static void copy(InputStream in, File file)
    {
        try
        {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while((len = in.read(buf)) > 0)
            {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        }
        catch(IOException ex)
        {
            RegionControl.plugin.getLogger().log(Level.SEVERE, "Could not save default config!", ex);
        }
    }
    
    private static boolean saveConfig(FileConfiguration config, File configFile)
    {
        try
        {
            config.save(configFile);
            return true;
        }
        catch(IOException ex)
        {
            RegionControl.plugin.getLogger().log(Level.SEVERE, "Could not save config!", ex);
            return false;
        }
    }
    
    public static void reloadFactionConfig()
    {
        if(factionConfigFile == null)
        {
            factionConfigFile = new File(RegionControl.plugin.getDataFolder(), "factions.yml");
        }
        factionConfig = YamlConfiguration.loadConfiguration(factionConfigFile);
        
        if(!factionConfigFile.exists())
        {
            factionConfigFile.getParentFile().mkdirs();
            copy(RegionControl.plugin.getResource("defaults/factions.yml"), factionConfigFile);
        }
    }
    
    private static void reloadMainConfig()
    {
        if(mainConfigFile == null)
        {
            mainConfigFile = new File(RegionControl.plugin.getDataFolder(), "config.yml");
        }
        mainConfig = YamlConfiguration.loadConfiguration(mainConfigFile);
        
        if(!mainConfigFile.exists())
        {
            mainConfigFile.getParentFile().mkdirs();
            copy(RegionControl.plugin.getResource("defaults/config.yml"), mainConfigFile);
        }
    }
    
    public static void reloadRegionConfigs()
    {
        if(regionConfigs == null || regionData == null || regionConfigFiles == null || regionDataFiles == null)
        {
            regionConfigs = new HashMap<String, FileConfiguration>();
            regionConfigFiles = new HashMap<String, File>();
            
            regionData = new HashMap<String, FileConfiguration>();
            regionDataFiles = new HashMap<String, File>();
            
            for(World world : Bukkit.getWorlds())
            {
                regionConfigs.put(world.getName(), null);
                regionConfigFiles.put(world.getName(), null);
                
                regionData.put(world.getName(), null);
                regionDataFiles.put(world.getName(), null);
            }
        }
        
        for(World world : Bukkit.getServer().getWorlds())
        {
            String worldName = world.getName();
            File regionsFile = regionConfigFiles.get(worldName);
            FileConfiguration regionsConfig = regionConfigs.get(worldName);
            
            if(regionsFile == null)
            {
                regionsFile = new File(RegionControl.plugin.getDataFolder(), "worlds/" + worldName + "/regions.yml");
            }
            regionsConfig = YamlConfiguration.loadConfiguration(regionsFile);
            
            if(!regionsFile.exists())
            {
                regionsFile.getParentFile().mkdirs();
                copy(RegionControl.plugin.getResource("defaults/regions.yml"), regionsFile);
            }
            
            File dataFile = regionDataFiles.get(worldName);
            FileConfiguration dataConfig = regionData.get(worldName);
            
            if(dataFile == null)
            {
                dataFile = new File(RegionControl.plugin.getDataFolder(), "worlds/" + worldName + "/data.yml");
            }
            dataConfig = YamlConfiguration.loadConfiguration(dataFile);
            
            if(!dataFile.exists())
            {
                dataFile.getParentFile().mkdirs();
                copy(RegionControl.plugin.getResource("defaults/data.yml"), dataFile);
            }
            
            regionConfigFiles.put(worldName, regionsFile);
            regionConfigs.put(worldName, regionsConfig);
            regionDataFiles.put(worldName, dataFile);
            regionData.put(worldName, dataConfig);
        }
        
    }
}