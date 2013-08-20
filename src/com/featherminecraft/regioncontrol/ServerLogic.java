package com.featherminecraft.regioncontrol;

import java.awt.Color;
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

import com.featherminecraft.regioncontrol.capturableregion.CapturableRegion;
import com.featherminecraft.regioncontrol.capturableregion.ControlPoint;
import com.featherminecraft.regioncontrol.capturableregion.SpawnPoint;
import com.featherminecraft.regioncontrol.utils.RegionUtils;
import com.featherminecraft.regioncontrol.utils.Utils;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class ServerLogic {

    //private variables
    private static FileConfiguration mainconfig;
    private static FileConfiguration datafile;
    
    //Temp Variable. Can be removed on plugin disable.
    public static Map<CapturableRegion, List<Player>> players = new HashMap<CapturableRegion, List<Player>>();
    
    //Registered Variables; to be saved on plugin disable
    public static Map<String, Faction> factions = new HashMap<String, Faction>();
    public static Map<String, CapturableRegion> capturableRegions = new HashMap<String,CapturableRegion>();

    public static void init()
    {
        mainconfig = new Config().getMainConfig();
        datafile = new Config().getDataFile();

        //Faction Setup
        factions = setupFactions();

        //Capturable Region Setup
        setupRegions();
    }

    public static Map<String, Faction> setupFactions() {
        //TODO Not Complete
        Set<String> configfactions = mainconfig.getConfigurationSection("factions").getKeys(false);
        Map<String, Faction> factions = new HashMap<String,Faction>();
        for(String faction : configfactions)
        {
            String permissionGroup = mainconfig.getString("factions." + faction + ".permissiongroup");
            
            String spawnworld = mainconfig.getString("factions." + faction + ".defaultspawn" + ".world");
            int spawnx = mainconfig.getInt("factions." + faction + ".defaultspawn" + ".x");
            int spawny = mainconfig.getInt("factions." + faction + ".defaultspawn" + ".y");
            int spawnz = mainconfig.getInt("factions." + faction + ".defaultspawn" + ".z");
            
            Location spawnlocation = new Location (Bukkit.getWorld(spawnworld), spawnx, spawny, spawnz);
            
            int red = mainconfig.getInt("factions." + faction + ".color" + ".red");
            int green = mainconfig.getInt("factions." + faction + ".color" + ".green");
            int blue = mainconfig.getInt("factions." + faction + ".color" + ".blue");
            
            Color factioncolor = new Color(red, green, blue);
            
            //TODO Determine faction 'spawn' region. Not sure how this will work for multi-world.
            factions.put(faction, new Faction(faction, permissionGroup,factioncolor, spawnlocation));
        }
        return factions;
    }
    
    public static void setupRegions()
    {
        //TODO Implement Save Data and retrieving of this data.
        
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
                    
                    Faction controlpointowner = factions.get(datafile.get(configWorld + ".regions." + configRegion + ".controlpoints." + configControlPoint + ".owner"));
                    ControlPoint controlPoint = new ControlPoint(configControlPoint, controlpointlocation, controlpointowner);
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
                
                CapturableRegion capturableregion = new CapturableRegion(regionDisplayname, owner, region, world, controlPoints, spawnPoint, baseInfluence, influence, influenceOwner);
                capturableRegions.put(configWorld + "_" + configRegion, capturableregion);
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
}
