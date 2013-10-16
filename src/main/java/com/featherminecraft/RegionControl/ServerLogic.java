package com.featherminecraft.RegionControl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.World;

import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;
import com.featherminecraft.RegionControl.capturableregion.ControlPoint;
import com.featherminecraft.RegionControl.capturableregion.SpawnPoint;
import com.featherminecraft.RegionControl.utils.Utils;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class ServerLogic {

    //Temp Variable. Can be removed on plugin disable.
    public static Map<String, RCPlayer> players = new HashMap<String,RCPlayer>();
    
    //Registered Variables; to be saved on plugin disable
    public static Map<String, Faction> factions = new HashMap<String, Faction>();
    public static Map<String, CapturableRegion> capturableRegions = new HashMap<String,CapturableRegion>();

    public static Boolean init()
    {
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
        //Utilities Begin
        Config config = new Config();
        //Utilities End
        Set<String> configfactions = config.getMainConfig().getConfigurationSection("factions").getKeys(false);
        for(String faction : configfactions)
        {
            String permissionGroup = config.getMainConfig().getString("factions." + faction + ".permissiongroup");
            RegionControl.plugin.getLogger().log(Level.INFO, "DEBUG: " + faction + "'s Permission Group is: " + permissionGroup);
            
            String configColor = config.getMainConfig().getString("factions." + faction + ".color");
            
            Color factioncolor = DyeColor.valueOf(configColor.toUpperCase()).getColor();
            
            Faction factionObject = new Faction(faction, permissionGroup, factioncolor);
            
            factions.put(faction, factionObject);
        }
    }
    
    private static Boolean setupRegions()
    {
        //Utilities Begin
        Config config = new Config();
        //Utilities End
        
        Set<String> worlds = config.getMainConfig().getConfigurationSection("worlds").getKeys(false);
        
        //Region Setup
        for(String configWorld : worlds)
        {
            RegionControl.plugin.getLogger().log(Level.INFO, "DEBUG: Loading Regions for: " + configWorld);
            Set<String> regions = config.getMainConfig().getConfigurationSection("worlds." + configWorld + ".regions").getKeys(false);
            for(String configRegion : regions)
            {
                if(configRegion == null)
                {
                    continue;
                }
                String regionDisplayname = config.getMainConfig().getString("worlds." + configWorld + ".regions." + configRegion + ".displayname"); //Display Name
                
                //Owner Begin
                String configOwner = config.getDataConfig().getString("worlds." + configWorld + ".regions." + configRegion + ".owner");
                if(configOwner == null)
                {
                    configOwner = config.getDefaultFaction();
                }
                Faction owner = factions.get(configOwner);
                //Owner End
                
                World world = Bukkit.getWorld(configWorld); //World
                ProtectedRegion region = Utils.getWorldGuard().getRegionManager(world).getRegion(configRegion); //Region
                
                //ControlPoint List Begin
                Set<String> configControlPoints = config.getMainConfig().getConfigurationSection("worlds." + configWorld + ".regions." + configRegion + ".controlpoints").getKeys(false);
                List<ControlPoint> controlPoints = new ArrayList<ControlPoint>();
                for(String configControlPoint : configControlPoints)
                {
                    int x = config.getMainConfig().getInt("worlds." + configWorld + ".regions." + configRegion + ".controlpoints." + configControlPoint + ".x");
                    int y = config.getMainConfig().getInt("worlds." + configWorld + ".regions." + configRegion + ".controlpoints." + configControlPoint + ".y");
                    int z = config.getMainConfig().getInt("worlds." + configWorld + ".regions." + configRegion + ".controlpoints." + configControlPoint + ".z");
                    Location controlpointlocation = new Location(world, x, y, z);
                    
                    Double captureRadius = ((Integer) config.getMainConfig().getInt("worlds." + configWorld + ".regions." + configRegion + ".controlpoints." + configControlPoint + ".captureradius")).doubleValue();
                    Float baseInfluence = ((Integer) config.getMainConfig().getInt("worlds." + configWorld + ".regions." + configRegion + ".controlpoints." + configControlPoint + ".baseinfluence")).floatValue();
                    Float influence = ((Integer) config.getDataConfig().getInt("worlds." + configWorld + ".regions." + configRegion + ".controlpoints." + configControlPoint + ".influence")).floatValue();
                    
                    Faction controlPointOwner = factions.get(config.getDataConfig().get("worlds." + configWorld + ".regions." + configRegion + ".controlpoints." + configControlPoint + ".owner"));
                    Faction influenceOwner = factions.get(config.getDataConfig().get("worlds." + configWorld + ".regions." + configRegion + ".controlpoints." + configControlPoint + ".influenceowner"));
                    
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
                int x = config.getMainConfig().getInt("worlds." + configWorld + ".regions." + configRegion + ".spawnpoint" + ".x");
                int y = config.getMainConfig().getInt("worlds." + configWorld + ".regions." + configRegion + ".spawnpoint" + ".y");
                int z = config.getMainConfig().getInt("worlds." + configWorld + ".regions." + configRegion + ".spawnpoint" + ".z");
                Location spawnpointlocation = new Location(world, x, y, z);
                SpawnPoint spawnPoint = new SpawnPoint(spawnpointlocation); 
                //SpawnPoint End
                
                Float baseInfluence = ((Integer) config.getMainConfig().getInt("worlds." + configWorld + ".regions." + configRegion + ".baseinfluence")).floatValue(); //Base Influence
                Float influence = ((Integer) config.getDataConfig().getInt("worlds." + configWorld + ".regions." + configRegion + ".influence")).floatValue(); //Influence
                Faction influenceOwner = factions.get(config.getDataConfig().get("worlds." + configWorld + ".regions." + configRegion + ".influenceowner")); //Influence Owner
                
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
            }
        }

        //Adjacent Region Setup
        for(String configWorld : worlds)
        {
            Set<String> regions = config.getMainConfig().getConfigurationSection("worlds." + configWorld + ".regions").getKeys(false);
            for(String configRegion : regions)
            {
                List<String> configAdjacentRegions = config.getMainConfig().getStringList("worlds." + configWorld + ".regions." + configRegion + ".adjacentregions");
                
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
        //Utilities Begin
        Config config = new Config();
        //Utilities End
        Set<String> configFactions = config.getMainConfig().getConfigurationSection("factions").getKeys(false);
        for(String configFaction : configFactions)
        {
            RegionControl.plugin.getLogger().log(Level.INFO, "DEBUG: Setting up Default Spawns for: " + configFaction);
            Map<String, Object> regionWorlds = config.getMainConfig().getConfigurationSection("factions." + configFaction + ".defaultspawn").getValues(false);
            Map<World,CapturableRegion> spawnRegions = new HashMap<World,CapturableRegion>();
            Faction faction = factions.get(configFaction);
            
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
