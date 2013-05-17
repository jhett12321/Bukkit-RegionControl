package com.featherminecraft.regioncontrol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import com.featherminecraft.regioncontrol.utils.ServerUtils;
import com.featherminecraft.regioncontrol.utils.Utils;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;


//TODO: Incomplete.
public class Config {
    private static File data = null;
    private static FileConfiguration mainconfig;
    
    public static void createConfigFiles()
    {
        //W.I.P
        mainconfig = RegionControl.plugin.getConfig();
        data = new File(RegionControl.plugin.getDataFolder(), "data.yml");
    }
    
    public void getSavedData()
    {
        
    }
    
    public void saveRegionData()
    {
        
    }

    public static Map<String,CapturableRegion> setupRegions()
    {
        //TODO Implement Save Data and retrieving of this data.
        
        Set<String> regions = mainconfig.getConfigurationSection("regions").getKeys(false);
        Map<String,CapturableRegion> capturableregions = new HashMap<String,CapturableRegion>();

        //Region Setup
        for(String regionid : regions)
        {
            String region_world = mainconfig.getString("regions." + regionid + ".world");
            String region_displayname = mainconfig.getString("regions." + regionid + ".displayname");
            
            World world = Bukkit.getWorld(region_world);
            ProtectedRegion region = Utils.getWorldGuard().getRegionManager(world).getRegion(regionid);
            
            CapturableRegion capturableregion = new CapturableRegion(region_displayname, region, world, new Config().getDefaultFaction());
            capturableregions.put(region_world + "_" + regionid, capturableregion);
        }

        //Adjacent Region Setup
        for(Entry<String, CapturableRegion> capturableregion : capturableregions.entrySet())
        {
            List<String> region_adjacentregions = mainconfig.getStringList("regions." + capturableregion.getValue().getRegion().getId() + ".adjacentregions");
            
            List<CapturableRegion> adjacentregions = new ArrayList<CapturableRegion>();
            for(String adjacentregion : region_adjacentregions)
            {
                String adjacentregion_world = mainconfig.getString("regions." + adjacentregion + ".world");
                World world = Bukkit.getWorld(adjacentregion_world);
                ProtectedRegion region = Utils.getWorldGuard().getRegionManager(world).getRegion(adjacentregion);
                CapturableRegion adjacentcapturableregion = new ServerUtils().getCapturableRegion(region, world);
                adjacentregions.add(adjacentcapturableregion);
            }
            capturableregion.getValue().setAdjacentRegions(adjacentregions);
        }
        
        //ControlPoint Setup
        for(Entry<String, CapturableRegion> capturableregion : capturableregions.entrySet())
        {
            Set<String> region_controlpoints = mainconfig.getConfigurationSection("regions." + capturableregion.getValue().getRegion().getId() + ".controlpoints.").getKeys(false);
            
            List<ControlPoint> controlpoints = new ArrayList<ControlPoint>();
            for(String controlpointentry : region_controlpoints)
            {
                int x = mainconfig.getInt("regions." + capturableregion.getValue().getRegion().getId() + ".controlpoints." + controlpointentry + ".x");
                int y = mainconfig.getInt("regions." + capturableregion.getValue().getRegion().getId() + ".controlpoints." + controlpointentry + ".y");
                int z = mainconfig.getInt("regions." + capturableregion.getValue().getRegion().getId() + ".controlpoints." + controlpointentry + ".z");
                Location controlpointlocation = new Location(capturableregion.getValue().getWorld(), x, y, z);
                ControlPoint controlpoint = new ControlPoint(controlpointentry, capturableregion.getValue(), controlpointlocation);
                controlpoints.add(controlpoint);
            }
            capturableregion.getValue().setControlPoints(controlpoints);
        }
        
        //Timer Setup
        
        return capturableregions;
    }

    public static Map<String, Faction> setupFactions() {
        // TODO Auto-generated method stub
        return null;
    }
    
    public static Map<CapturableRegion, SpawnPoint> setupSpawnPoints() {
        // TODO Auto-generated method stub
        return null;
    }

    public Map<String, Faction> getFactions() {
        Set<String> factions = mainconfig.getConfigurationSection("factions").getKeys(false);
        for(String faction : factions)
        {
            
        }
        return null;
    }

    public Faction getDefaultFaction() {
        Set<String> factions = mainconfig.getConfigurationSection("factions").getKeys(false);
        for(String faction : factions)
        {
            if(mainconfig.getBoolean("factions." + faction + ".default"))
            {
                
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
}
