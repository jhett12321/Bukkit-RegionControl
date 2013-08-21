package com.featherminecraft.regioncontrol;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.featherminecraft.regioncontrol.capturableregion.CapturableRegion;
import com.featherminecraft.regioncontrol.capturableregion.ControlPoint;
import com.featherminecraft.regioncontrol.capturableregion.SpawnPoint;
import com.featherminecraft.regioncontrol.utils.Utils;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class ServerLogic {

    //private variables
    private static FileConfiguration mainconfig;
    private static FileConfiguration datafile;
    
    //Temp Variable. Can be removed on plugin disable.
    public static Map<CapturableRegion, List<Player>> players = new HashMap<CapturableRegion, List<Player>>(); //TODO Check safety of this map.
    
    //Registered Variables; to be saved on plugin disable
    public static Map<String, Faction> factions = new HashMap<String, Faction>();
    public static Map<String, CapturableRegion> capturableRegions = new HashMap<String,CapturableRegion>();

    public static void init()
    {
        //TODO Implement Save Data and retrieving of this data.
        mainconfig = new Config().getMainConfig();
        datafile = new Config().getDataFile();

        //Faction Setup
        setupFactions();

        //Capturable Region Setup
        setupRegions();
        
        //Spawn Region Setup
        setupSpawnRegions();
    }

    public static void setupFactions() {
        Set<String> configfactions = mainconfig.getConfigurationSection("factions").getKeys(false);
        Map<String, Faction> factions = new HashMap<String,Faction>();
        for(String faction : configfactions)
        {
            String permissionGroup = mainconfig.getString("factions." + faction + ".permissiongroup");
            
            int red = mainconfig.getInt("factions." + faction + ".color" + ".red");
            int green = mainconfig.getInt("factions." + faction + ".color" + ".green");
            int blue = mainconfig.getInt("factions." + faction + ".color" + ".blue");
            
            Color factioncolor = new Color(red, green, blue);
            
            factions.put(faction, new Faction(faction, permissionGroup, factioncolor));
        }
        ServerLogic.factions = factions;
    }
    
    public static void setupRegions()
    {
        Set<String> worlds = mainconfig.getConfigurationSection("worlds").getKeys(false);

        //Region Setup
        for(String configWorld : worlds)
        {
            Set<String> regions = mainconfig.getConfigurationSection(configWorld + ".regions").getKeys(false);
            for(String configRegion : regions)
            {
                String regionDisplayname = mainconfig.getString(configWorld + ".regions." + configRegion + ".displayname"); //Display Name
                
                //Owner Begin
                String configOwner = datafile.getString(configWorld + ".regions." + configRegion + ".owner");
                if(configOwner == null)
                {
                    configOwner = new Config().getDefaultFaction();
                }
                Faction owner = factions.get(configOwner);
                //Owner End
                
                World world = Bukkit.getWorld(configWorld); //World
                ProtectedRegion region = Utils.getWorldGuard().getRegionManager(world).getRegion(configRegion); //Region
                
                //ControlPoint List Begin
                Set<String> configControlPoints = mainconfig.getConfigurationSection(configWorld + ".regions." + configRegion + ".controlpoints").getKeys(false);
                List<ControlPoint> controlPoints = new ArrayList<ControlPoint>();
                for(String configControlPoint : configControlPoints)
                {
                    int x = mainconfig.getInt(configWorld + ".regions." + configRegion + ".controlpoints." + configControlPoint + ".x");
                    int y = mainconfig.getInt(configWorld + ".regions." + configRegion + ".controlpoints." + configControlPoint + ".y");
                    int z = mainconfig.getInt(configWorld + ".regions." + configRegion + ".controlpoints." + configControlPoint + ".z");
                    Location controlpointlocation = new Location(world, x, y, z);
                    
                    int captureRadius = mainconfig.getInt(configWorld + ".regions." + configRegion + ".controlpoints." + configControlPoint + ".captureradius");
                    Float baseInfluence = ((Integer) mainconfig.getInt(configWorld + ".regions." + configRegion + ".controlpoints." + configControlPoint + ".baseinfluence")).floatValue();
                    Float influence = ((Integer) datafile.getInt(configWorld + ".regions." + configRegion + ".controlpoints." + configControlPoint + ".influence")).floatValue();
                    
                    Faction controlPointOwner = factions.get(datafile.get(configWorld + ".regions." + configRegion + ".controlpoints." + configControlPoint + ".owner"));
                    Faction influenceOwner = factions.get(datafile.get(configWorld + ".regions." + configRegion + ".controlpoints." + configControlPoint + ".influenceowner"));
                    
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
                int x = mainconfig.getInt(configWorld + ".regions." + configRegion + ".spawnpoint" + ".x");
                int y = mainconfig.getInt(configWorld + ".regions." + configRegion + ".spawnpoint" + ".y");
                int z = mainconfig.getInt(configWorld + ".regions." + configRegion + ".spawnpoint" + ".z");
                Location spawnpointlocation = new Location(world, x, y, z);
                SpawnPoint spawnPoint = new SpawnPoint(spawnpointlocation); 
                //SpawnPoint End
                
                Float baseInfluence = ((Integer) mainconfig.getInt(configWorld + ".regions." + configRegion + ".baseinfluence")).floatValue(); //Base Influence
                Float influence = ((Integer) datafile.getInt(configWorld + ".regions." + configRegion + ".influence")).floatValue(); //Influence
                Faction influenceOwner = factions.get(datafile.get(configWorld + ".regions." + configRegion + ".influenceowner")); //Influence Owner
                
                CapturableRegion capturableregion = new CapturableRegion(regionDisplayname,
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
            Set<String> regions = mainconfig.getConfigurationSection(configWorld + ".regions").getKeys(false);
            for(String configRegion : regions)
            {
                List<String> configAdjacentRegions = mainconfig.getStringList(configWorld + ".regions." + configRegion + ".adjacentregions");
                
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
    }
    
    public static void setupSpawnRegions()
    {
        Set<String> configFactions = mainconfig.getConfigurationSection("factions").getKeys(false);
        for(String configFaction : configFactions)
        {
            String regionWorld = mainconfig.getString("factions." + configFaction + ".defaultspawn" + ".world");
            String regionName = mainconfig.getString("factions." + configFaction + ".defaultspawn" + ".region");
            
            Faction faction = factions.get(configFaction);
            faction.setFactionSpawnRegion(capturableRegions.get(regionWorld + "_" + regionName));
        }
    }
}
