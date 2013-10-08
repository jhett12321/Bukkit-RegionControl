package com.featherminecraft.RegionControl;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;
import com.featherminecraft.RegionControl.capturableregion.ControlPoint;
import com.featherminecraft.RegionControl.capturableregion.SpawnPoint;
import com.featherminecraft.RegionControl.utils.ConfigUtils;
import com.featherminecraft.RegionControl.utils.Utils;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class ServerLogic {

    //private variables
    private static FileConfiguration mainconfig;
    private static FileConfiguration datafile;
    
    //Temp Variable. Can be removed on plugin disable.
    public static Map<String, RCPlayer> players = new HashMap<String,RCPlayer>();
    
    //Registered Variables; to be saved on plugin disable
    public static Map<String, Faction> factions = new HashMap<String, Faction>();
    public static Map<String, CapturableRegion> capturableRegions = new HashMap<String,CapturableRegion>();

    public static Boolean init()
    {
        mainconfig = new Config().getMainConfig();
        datafile = new Config().getDataFile();

        //Faction Setup
        setupFactions();

        //Capturable Region Setup
        if(!setupRegions())
        {
            return false;
        }
        
        //Spawn Region Setup
        setupSpawnRegions();
        
        return true;
    }

    private static void setupFactions() {
        Set<String> configfactions = mainconfig.getConfigurationSection("factions").getKeys(false);
        for(String faction : configfactions)
        {
            String permissionGroup = mainconfig.getString("factions." + faction + ".permissiongroup");
            RegionControl.plugin.getLogger().log(Level.INFO, "DEBUG: " + faction + "'s Permission Group is: " + permissionGroup);
            
            int red = mainconfig.getInt("factions." + faction + ".color" + ".red");
            int green = mainconfig.getInt("factions." + faction + ".color" + ".green");
            int blue = mainconfig.getInt("factions." + faction + ".color" + ".blue");
            
            Color factioncolor = new Color(red, green, blue);
            
            Faction factionObject = new Faction(faction, permissionGroup, factioncolor);
            
            factions.put(permissionGroup, factionObject);
        }
    }
    
    private static Boolean setupRegions()
    {
        //Utilities Begin
        ConfigUtils configUtils = new ConfigUtils();
        //Utilities End
        
        //TODO Consider accessing config via Config Utilities
        Set<String> worlds = mainconfig.getConfigurationSection("worlds").getKeys(false);
        
        //Region Setup
        for(String configWorld : worlds)
        {
            RegionControl.plugin.getLogger().log(Level.INFO, "DEBUG: Loading Regions for: " + configWorld);
            Set<String> regions = mainconfig.getConfigurationSection("worlds." + configWorld + ".regions").getKeys(false);
            for(String configRegion : regions)
            {
                if(configRegion == null)
                {
                    continue;
                }
                String regionDisplayname = mainconfig.getString("worlds." + configWorld + ".regions." + configRegion + ".displayname"); //Display Name
                
                //Owner Begin
                String configOwner = datafile.getString("worlds." + configWorld + ".regions." + configRegion + ".owner");
                if(configOwner == null)
                {
                    configOwner = configUtils.getDefaultFaction();
                }
                Faction owner = factions.get(configOwner);
                //Owner End
                
                World world = Bukkit.getWorld(configWorld); //World
                ProtectedRegion region = Utils.getWorldGuard().getRegionManager(world).getRegion(configRegion); //Region
                
                //ControlPoint List Begin
                Set<String> configControlPoints = mainconfig.getConfigurationSection("worlds." + configWorld + ".regions." + configRegion + ".controlpoints").getKeys(false);
                List<ControlPoint> controlPoints = new ArrayList<ControlPoint>();
                for(String configControlPoint : configControlPoints)
                {
                    int x = mainconfig.getInt("worlds." + configWorld + ".regions." + configRegion + ".controlpoints." + configControlPoint + ".x");
                    int y = mainconfig.getInt("worlds." + configWorld + ".regions." + configRegion + ".controlpoints." + configControlPoint + ".y");
                    int z = mainconfig.getInt("worlds." + configWorld + ".regions." + configRegion + ".controlpoints." + configControlPoint + ".z");
                    Location controlpointlocation = new Location(world, x, y, z);
                    
                    int captureRadius = mainconfig.getInt("worlds." + configWorld + ".regions." + configRegion + ".controlpoints." + configControlPoint + ".captureradius");
                    Float baseInfluence = ((Integer) mainconfig.getInt("worlds." + configWorld + ".regions." + configRegion + ".controlpoints." + configControlPoint + ".baseinfluence")).floatValue();
                    Float influence = ((Integer) datafile.getInt("worlds." + configWorld + ".regions." + configRegion + ".controlpoints." + configControlPoint + ".influence")).floatValue();
                    
                    Faction controlPointOwner = factions.get(datafile.get("worlds." + configWorld + ".regions." + configRegion + ".controlpoints." + configControlPoint + ".owner"));
                    Faction influenceOwner = factions.get(datafile.get("worlds." + configWorld + ".regions." + configRegion + ".controlpoints." + configControlPoint + ".influenceowner"));
                    
                    ControlPoint controlPoint = new ControlPoint(configControlPoint,
                            controlPointOwner, 
                            controlpointlocation,
                            captureRadius,
                            baseInfluence,
                            influence,
                            influenceOwner);
                    controlPoints.add(controlPoint);
                }
                //ControlPoint List End
                
                //SpawnPoint Begin
                int x = mainconfig.getInt("worlds." + configWorld + ".regions." + configRegion + ".spawnpoint" + ".x");
                int y = mainconfig.getInt("worlds." + configWorld + ".regions." + configRegion + ".spawnpoint" + ".y");
                int z = mainconfig.getInt("worlds." + configWorld + ".regions." + configRegion + ".spawnpoint" + ".z");
                Location spawnpointlocation = new Location(world, x, y, z);
                SpawnPoint spawnPoint = new SpawnPoint(spawnpointlocation); 
                //SpawnPoint End
                
                Float baseInfluence = ((Integer) mainconfig.getInt("worlds." + configWorld + ".regions." + configRegion + ".baseinfluence")).floatValue(); //Base Influence
                Float influence = ((Integer) datafile.getInt("worlds." + configWorld + ".regions." + configRegion + ".influence")).floatValue(); //Influence
                Faction influenceOwner = factions.get(datafile.get("worlds." + configWorld + ".regions." + configRegion + ".influenceowner")); //Influence Owner
                
                CapturableRegion capturableregion = new CapturableRegion(regionDisplayname,
                        configRegion,
                        owner,
                        region,
                        world,
                        controlPoints,
                        spawnPoint,
                        baseInfluence,
                        influence,
                        influenceOwner);
                
                capturableRegions.put(configWorld + "_" + configRegion, capturableregion);
                
                //Define ControlPoint Region.
                List<ControlPoint> controlPointList = capturableregion.getControlPoints();
                for(ControlPoint controlpoint : controlPointList)
                {
                    controlpoint.setRegion(capturableregion);
                }
            }
        }

        //Adjacent Region Setup
        for(String configWorld : worlds)
        {
            Set<String> regions = mainconfig.getConfigurationSection("worlds." + configWorld + ".regions").getKeys(false);
            for(String configRegion : regions)
            {
                List<String> configAdjacentRegions = mainconfig.getStringList("worlds." + configWorld + ".regions." + configRegion + ".adjacentregions");
                
                List<CapturableRegion> adjacentregions = new ArrayList<CapturableRegion>();
                for(String configAdjacentRegion : configAdjacentRegions)
                {
                    //TODO Future Implementation: Cross-world adjacent regions.
                    //World world = Bukkit.getWorld(configWorld);
                    
                    CapturableRegion adjacentRegion = capturableRegions.get(configWorld + "_" + configAdjacentRegion);
                    adjacentregions.add(adjacentRegion);
                }
                capturableRegions.get(configWorld + "_" + configRegion).setAdjacentRegions(adjacentregions);
            }
        }
        return true;
    }
    
    private static void setupSpawnRegions()
    {
        Set<String> configFactions = mainconfig.getConfigurationSection("factions").getKeys(false);
        for(String configFaction : configFactions)
        {
            RegionControl.plugin.getLogger().log(Level.INFO, "DEBUG: Setting up Default Spawns for: " + configFaction);
            Map<String, Object> regionWorlds = mainconfig.getConfigurationSection("factions." + configFaction + ".defaultspawn").getValues(false);
            Map<World,CapturableRegion> spawnRegions = new HashMap<World,CapturableRegion>();
            String permissionGroup = mainconfig.getString("factions." + configFaction + ".permissiongroup");
            Faction faction = factions.get(permissionGroup);
            
            for(Entry<String, Object> configWorld : regionWorlds.entrySet())
            {
                CapturableRegion region = capturableRegions.get(configWorld.getKey() + "_" + configWorld.getValue().toString());
                World world = Bukkit.getWorld(configWorld.getKey());
                spawnRegions.put(world,region);
                faction.addFactionSpawnRegion(region);
            }
        }
    }
    
}
