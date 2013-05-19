package com.featherminecraft.regioncontrol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.featherminecraft.regioncontrol.utils.RegionUtils;
import com.featherminecraft.regioncontrol.utils.Utils;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class ServerLogic {

    //private variables
    private static FileConfiguration mainconfig;
    
    //Temp Variable. Can be removed on plugin disable.
    public static Map<CapturableRegion, List<Player>> players;
    
    //Registered Variables; to be saved on plugin disable
    public static Map<String, Faction> registeredfactions;
    public static Map<String, CapturableRegion> registeredregions;
    public static Map<CapturableRegion, CaptureTimer> capturetimers;
    public static Map<CapturableRegion, Faction> regionowners;
    public static Map<CapturableRegion, SpawnPoint> registeredspawnpoints;

    public static void init()
    {
        mainconfig = new Config().getMainConfig();
        //Faction Setup
        registeredfactions = setupFactions();

        //Region Setup
        registeredregions = setupRegions();
    }

    public static Map<String, Faction> setupFactions() {
        Set<String> configfactions = mainconfig.getConfigurationSection("factions").getKeys(false);
        Map<String, Faction> factions = new HashMap<String,Faction>();
        for(String faction : configfactions)
        {
            String permissionGroup = mainconfig.getString("factions." + faction + ".permissiongroup");
            
            String spawnworld = mainconfig.getString("factions." + faction + ".defaultspawn" + ".world");
            int spawnx = mainconfig.getInt("factions." + faction + ".defaultspawn" + ".x");
            int spawny = mainconfig.getInt("factions." + faction + ".defaultspawn" + ".y");
            int spawnz = mainconfig.getInt("factions." + faction + ".defaultspawn" + ".z");
            
            Location spawnlocation = new Location (Bukkit.getWorld(spawnworld), spawnx, spawnz, spawnz);
            
            //TODO Determine faction 'spawn' region. Not sure how this will work for multi-world.
            factions.put(faction, new Faction(faction, permissionGroup, spawnlocation));
        }
        return factions;
    }
    
    public static Map<String,CapturableRegion> setupRegions()
    {
        //TODO Implement Save Data and retrieving of this data.
        
        Set<String> regions = mainconfig.getConfigurationSection("regions").getKeys(false);
        Map<String,CapturableRegion> capturableRegions = new HashMap<String,CapturableRegion>();

        //Region Setup
        for(String regionid : regions)
        {
            String region_world = mainconfig.getString("regions." + regionid + ".world");
            String region_displayname = mainconfig.getString("regions." + regionid + ".displayname");
            
            String region_owner = new Config().getDataFile().getString("regions." + regionid + ".owner");
            Integer influence = new Config().getDataFile().getInt("regions." + regionid + ".influence");
            String region_influenceowner = new Config().getDataFile().getString("regions." + regionid + ".influenceowner");
            
            if(region_owner == null)
            {
                region_owner = new Config().getDefaultFaction();
            }
            Faction owner = registeredfactions.get(region_owner);
            Faction influenceowner = registeredfactions.get(region_influenceowner);
            
            World world = Bukkit.getWorld(region_world);
            ProtectedRegion region = Utils.getWorldGuard().getRegionManager(world).getRegion(regionid);
            
            CapturableRegion capturableregion = new CapturableRegion(region_displayname, region, world, owner, influence, influenceowner);
            capturableRegions.put(region_world + "_" + regionid, capturableregion);
        }

        //Adjacent Region Setup
        for(Entry<String, CapturableRegion> capturableregion : capturableRegions.entrySet())
        {
            List<String> region_adjacentregions = mainconfig.getStringList("regions." + capturableregion.getValue().getRegion().getId() + ".adjacentregions");
            
            List<CapturableRegion> adjacentregions = new ArrayList<CapturableRegion>();
            for(String adjacentregion : region_adjacentregions)
            {
                String adjacentRegion_world = mainconfig.getString("regions." + adjacentregion + ".world");
                World world = Bukkit.getWorld(adjacentRegion_world);
                ProtectedRegion region = Utils.getWorldGuard().getRegionManager(world).getRegion(adjacentregion);
                CapturableRegion adjacentcapturableregion = new RegionUtils().getCapturableRegion(region, world);
                adjacentregions.add(adjacentcapturableregion);
            }
            capturableregion.getValue().setAdjacentRegions(adjacentregions);
        }
        
        //ControlPoint Setup
        for(Entry<String, CapturableRegion> capturableregion : capturableRegions.entrySet())
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
        
        //Spawn Point Setup
        for(Entry<String, CapturableRegion> capturableregion : capturableRegions.entrySet())
        {
            int x = mainconfig.getInt("regions." + capturableregion.getValue().getRegion().getId() + ".spawnpoint" + ".x");
            int y = mainconfig.getInt("regions." + capturableregion.getValue().getRegion().getId() + ".spawnpoint" + ".y");
            int z = mainconfig.getInt("regions." + capturableregion.getValue().getRegion().getId() + ".spawnpoint" + ".z");
            
            Location spawnpointlocation = new Location(capturableregion.getValue().getWorld(), x, y, z);
            
            SpawnPoint spawnpoint = new SpawnPoint(capturableregion.getValue(), spawnpointlocation);
            
            capturableregion.getValue().setSpawnPoint(spawnpoint);
            
            registeredspawnpoints.put(capturableregion.getValue(), spawnpoint);
        }
        
        //Timer Setup
        for(Entry<String, CapturableRegion> capturableregion : capturableRegions.entrySet())
        {
            CaptureTimer capturetimer = new CaptureTimer(capturableregion.getValue(), capturableregion.getValue().getBaseInfluence());
            capturetimer.runTaskTimer(RegionControl.plugin, 20, 20);
            
            capturableregion.getValue().setTimer(capturetimer);
            
            capturetimers.put(capturableregion.getValue(), capturetimer);
        }
        
        //Region Owner Setup
        for(Entry<String, CapturableRegion> capturableregion : capturableRegions.entrySet())
        {
            regionowners.put(capturableregion.getValue(), capturableregion.getValue().getOwner());
        }
        return capturableRegions;
    }
    
    public static Map<CapturableRegion, SpawnPoint> setupSpawnPoints() {
        // TODO Auto-generated method stub
        return null;
    }
    
    /**
     * Registers a region into the registered regions list.
     *
     * The registered regions list is used to calculate
     * region player counts, and more.
     *
     * @param region A ProtectedRegion.
     * @param world The world which this region is located.
     */
    @Deprecated
    private static void RegisterRegion(ProtectedRegion region, World world)
    {
/*        Faction defaultfaction = new Config().getDefaultFaction();
        CapturableRegion capturableregion = new CapturableRegion(region,world, Config.getDefaultFaction());
        
        Map<String, Location> controllablepoints = new Config().getControlPointsForRegion(capturableregion);
        for(Entry<String, Location> controlpointentry : controllablepoints.entrySet())
        {
            ControlPoint controlpoint = new ControlPoint(null, capturableregion, null);
            List<ControlPoint> controlpointlist = controlpoints.get(capturableregion);
            controlpointlist.add(controlpoint);
            controlpoints.put(capturableregion, controlpointlist);
        }
        
        capturableregion.setControlPoints(controlpoints.get(capturableregion));
        
        Location location = new Config().getSpawnPointForRegion(capturableregion);
        SpawnPoint spawnpoint = new SpawnPoint(capturableregion, location);
        spawnpoints.put(capturableregion, spawnpoint);
        capturableregion.setSpawnPoint(spawnpoints.get(region));
        
        CaptureTimer capturetimer = new CaptureTimer(capturableregion, 0);
        capturetimer.runTaskTimer(RegionControl.plugin, 20, 20);
        
        registeredregions.put(capturableregion.getWorld().getName() + "_" + capturableregion.getRegion().getId(), capturableregion);
        players.put(capturableregion, null);
        capturetimers.put(capturableregion, capturetimer);
        regionowners.put(capturableregion, defaultfaction);*/
    }
}
