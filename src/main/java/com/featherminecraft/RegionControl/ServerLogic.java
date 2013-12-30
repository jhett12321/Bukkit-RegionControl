package com.featherminecraft.RegionControl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.World;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;
import com.featherminecraft.RegionControl.capturableregion.ControlPoint;
import com.featherminecraft.RegionControl.capturableregion.SpawnPoint;

public class ServerLogic
{
    // Utilities Begin
    private static Config config = new Config();
    // Utilities End
    
    public static Map<String, RCPlayer> players = new HashMap<String, RCPlayer>();
    public static Map<String, Faction> factions = new HashMap<String, Faction>();
    public static Map<String, CapturableRegion> capturableRegions = new HashMap<String, CapturableRegion>();
    
    public static void init()
    {
        // Faction Setup
        setupFactions();
        
        // Capturable Region Setup
        setupRegions();
        
        // Spawn Region Setup
        setupSpawnRegions();
    }
    
    private static void setupFactions()
    {
        Set<String> configfactions = config.getMainConfig().getConfigurationSection("factions").getKeys(false);
        for(String factionId : configfactions)
        {
            String permissionGroup = config.getMainConfig().getString("factions." + factionId + ".permissiongroup");
            
            String configColor = config.getMainConfig().getString("factions." + factionId + ".color");
            
            String displayName = config.getMainConfig().getString("factions." + factionId + ".displayname");
            
            Color factioncolor = DyeColor.valueOf(configColor.toUpperCase()).getColor();
            
            Faction factionObject = new Faction(factionId, displayName, permissionGroup, factioncolor);
            
            factions.put(factionId, factionObject);
        }
    }
    
    private static void setupRegions()
    {
        Set<String> worlds = config.getMainConfig().getConfigurationSection("worlds").getKeys(false);
        
        // Region Setup
        for(String configWorld : worlds)
        {
            Set<String> configRegions = config.getMainConfig().getConfigurationSection("worlds." + configWorld + ".regions").getKeys(false);
            Map<String, Boolean> regions = new HashMap<String, Boolean>();
            
            for(String configRegion : configRegions)
            {
                try
                {
                    if(config.getMainConfig().getBoolean("worlds." + configWorld + ".regions." + configRegion + ".spawnRegion"))
                    {
                        regions.put(configRegion, true);
                    }
                    else
                    {
                        regions.put(configRegion, false);
                    }
                }
                catch(NullPointerException e)
                {
                    regions.put(configRegion, false);
                }
            }
            
            for(Entry<String, Boolean> configRegion : regions.entrySet())
            {
                String regionDisplayname = config.getMainConfig().getString("worlds." + configWorld + ".regions." + configRegion.getKey() + ".displayname"); // Display Name
                
                // Owner Begin
                String configOwner = config.getDataConfig().getString("worlds." + configWorld + ".regions." + configRegion.getKey() + ".owner");
                if(configOwner == null)
                {
                    configOwner = config.getDefaultFaction();
                }
                Faction owner = factions.get(configOwner);
                // Owner End
                
                World world = Bukkit.getWorld(configWorld); // World
                ProtectedRegion region = DependencyManager.getWorldGuard().getRegionManager(world).getRegion(configRegion.getKey()); // WorldGuard Region
                
                // ControlPoint List Begin
                List<ControlPoint> controlPoints = new ArrayList<ControlPoint>();
                if(!configRegion.getValue())
                {
                    Set<String> configControlPoints = config.getMainConfig().getConfigurationSection("worlds." + configWorld + ".regions." + configRegion.getKey() + ".controlpoints").getKeys(false);
                    for(String configControlPoint : configControlPoints)
                    {
                        int x = config.getMainConfig().getInt("worlds." + configWorld + ".regions." + configRegion.getKey() + ".controlpoints." + configControlPoint + ".x");
                        int y = config.getMainConfig().getInt("worlds." + configWorld + ".regions." + configRegion.getKey() + ".controlpoints." + configControlPoint + ".y");
                        int z = config.getMainConfig().getInt("worlds." + configWorld + ".regions." + configRegion.getKey() + ".controlpoints." + configControlPoint + ".z");
                        Location controlpointlocation = new Location(world, x, y, z);
                        
                        Double captureRadius = ((Integer) config.getMainConfig().getInt("worlds." + configWorld + ".regions." + configRegion.getKey() + ".controlpoints." + configControlPoint + ".captureradius")).doubleValue();
                        Float baseInfluence = ((Integer) config.getMainConfig().getInt("worlds." + configWorld + ".regions." + configRegion.getKey() + ".controlpoints." + configControlPoint + ".baseinfluence")).floatValue();
                        Float influence = ((Integer) config.getDataConfig().getInt("worlds." + configWorld + ".regions." + configRegion.getKey() + ".controlpoints." + configControlPoint + ".influence")).floatValue();
                        
                        Faction controlPointOwner = factions.get(config.getDataConfig().get("worlds." + configWorld + ".regions." + configRegion.getKey() + ".controlpoints." + configControlPoint + ".owner"));
                        Faction influenceOwner = factions.get(config.getDataConfig().get("worlds." + configWorld + ".regions." + configRegion.getKey() + ".controlpoints." + configControlPoint + ".influenceowner"));
                        
                        ControlPoint controlPoint = new ControlPoint(configControlPoint, controlPointOwner, controlpointlocation, captureRadius, baseInfluence, influence, influenceOwner);
                        controlPoints.add(controlPoint);
                    }
                }
                // ControlPoint List End
                
                // SpawnPoint Begin
                int x = config.getMainConfig().getInt("worlds." + configWorld + ".regions." + configRegion.getKey() + ".spawnpoint" + ".x");
                int y = config.getMainConfig().getInt("worlds." + configWorld + ".regions." + configRegion.getKey() + ".spawnpoint" + ".y");
                int z = config.getMainConfig().getInt("worlds." + configWorld + ".regions." + configRegion.getKey() + ".spawnpoint" + ".z");
                Location spawnpointlocation = new Location(world, x, y, z);
                SpawnPoint spawnPoint = new SpawnPoint(spawnpointlocation);
                // SpawnPoint End
                
                Float baseInfluence = ((Integer) config.getMainConfig().getInt("worlds." + configWorld + ".regions." + configRegion.getKey() + ".baseinfluence")).floatValue(); // Base Influence
                Float influence = ((Integer) config.getDataConfig().getInt("worlds." + configWorld + ".regions." + configRegion.getKey() + ".influence")).floatValue(); // Influence
                Faction influenceOwner = factions.get(config.getDataConfig().get("worlds." + configWorld + ".regions." + configRegion.getKey() + ".influenceowner")); // Influence Owner
                
                CapturableRegion capturableregion = new CapturableRegion(regionDisplayname, configRegion.getKey(), owner, region, world, controlPoints, spawnPoint, baseInfluence, influence, influenceOwner, configRegion.getValue());
                
                capturableRegions.put(configWorld + "_" + configRegion.getKey(), capturableregion);
            }
        }
        
        // Adjacent Region Setup
        for(String configWorld : worlds)
        {
            Set<String> regions = config.getMainConfig().getConfigurationSection("worlds." + configWorld + ".regions").getKeys(false);
            for(String configRegion : regions)
            {
                List<String> configAdjacentRegions = config.getMainConfig().getStringList("worlds." + configWorld + ".regions." + configRegion + ".adjacentregions");
                
                List<CapturableRegion> adjacentregions = new ArrayList<CapturableRegion>();
                for(String configAdjacentRegion : configAdjacentRegions)
                {
                    // TODO Future Implementation: Cross-world adjacent regions.
                    // World world = Bukkit.getWorld(configWorld);
                    
                    CapturableRegion adjacentRegion = capturableRegions.get(configWorld + "_" + configAdjacentRegion);
                    adjacentregions.add(adjacentRegion);
                }
                capturableRegions.get(configWorld + "_" + configRegion).setAdjacentRegions(adjacentregions);
            }
        }
    }
    
    private static void setupSpawnRegions()
    {
        Set<String> configFactions = config.getMainConfig().getConfigurationSection("factions").getKeys(false);
        for(String configFaction : configFactions)
        {
            Map<String, Object> regionWorlds = config.getMainConfig().getConfigurationSection("factions." + configFaction + ".defaultspawn").getValues(false);
            Map<World, CapturableRegion> spawnRegions = new HashMap<World, CapturableRegion>();
            Faction faction = factions.get(configFaction);
            
            for(Entry<String, Object> configWorld : regionWorlds.entrySet())
            {
                CapturableRegion region = capturableRegions.get(configWorld.getKey() + "_" + configWorld.getValue().toString());
                World world = Bukkit.getWorld(configWorld.getKey());
                spawnRegions.put(world, region);
                faction.addFactionSpawnRegion(region);
            }
        }
    }
    
    // WIP - TODO for commands.
    /*
     * public static void createRegion(String worldGuardId, String worldId, String regionDisplayName, Float baseInfluence,Float influence, Faction influenceOwner, List<ControlPoint> controlPoints,
     * String ownerId, List<String> adjacentRegionIds, Location spawnPointLocation,Boolean isSpawnRegion)
     * {
     * // Region Setup
     * Faction owner = factions.get(ownerId);
     * World world = Bukkit.getWorld(worldId); // World
     * ProtectedRegion worldGuardRegion = Utils.getWorldGuard().getRegionManager(world).getRegion(worldGuardId); // Region
     * SpawnPoint spawnPoint = new SpawnPoint(spawnPointLocation);
     * CapturableRegion capturableregion = new CapturableRegion(regionDisplayName, worldGuardId, owner, worldGuardRegion, world, controlPoints, spawnPoint, baseInfluence, influence, influenceOwner,
     * isSpawnRegion);
     * capturableRegions.put(worldId + "_" + worldGuardId, capturableregion);
     * List<CapturableRegion> adjacentregions = new ArrayList<CapturableRegion>();
     * // Adjacent Region Setup
     * for(String adjacentRegionId : adjacentRegionIds)
     * {
     * // TODO Future Implementation: Cross-world adjacent regions.
     * // World world = Bukkit.getWorld(configWorld);
     * CapturableRegion adjacentRegion = capturableRegions.get(worldId + "_" + adjacentRegionId);
     * adjacentregions.add(adjacentRegion);
     * adjacentRegion.getAdjacentRegions().add(capturableregion);
     * }
     * capturableRegions.get(worldId + "_" + worldId).setAdjacentRegions(adjacentregions);
     * }
     */
    
}