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
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;
import com.featherminecraft.RegionControl.capturableregion.ControlPoint;
import com.featherminecraft.RegionControl.capturableregion.SpawnPoint;

public class ServerLogic
{
    public static Map<String, BukkitTask> serverRunnables = new HashMap<String, BukkitTask>();
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
        
        BukkitTask runnable = new BukkitRunnable()
        {
            @Override
            public void run()
            {
                Config.saveAll(false);
            }
            
        }.runTaskTimer(RegionControl.plugin, 1200, 1200);
        
        serverRunnables.put("saveTask", runnable);
    }
    
    private static void setupFactions()
    {
        Set<String> configfactions = Config.getFactionConfig().getConfigurationSection("factions").getKeys(false);
        for(String factionId : configfactions)
        {
            String permissionGroup = Config.getFactionConfig().getString("factions." + factionId + ".permissiongroup");
            
            String configColor = Config.getFactionConfig().getString("factions." + factionId + ".color");
            
            String displayName = Config.getFactionConfig().getString("factions." + factionId + ".displayname");
            
            Color factioncolor = DyeColor.valueOf(configColor.toUpperCase()).getColor();
            
            Faction factionObject = new Faction(factionId, displayName, permissionGroup, factioncolor);
            
            factions.put(factionId, factionObject);
        }
    }
    
    private static void setupRegions()
    {
        Set<String> worlds = Config.getRegionConfigs().keySet();
        
        // Region Setup
        for(String configWorld : worlds)
        {
            FileConfiguration worldConfig = Config.getRegionConfigs().get(configWorld);
            FileConfiguration worldData = Config.getRegionData().get(configWorld);
            
            Set<String> configRegions;
            try 
            {
                configRegions = worldConfig.getConfigurationSection("regions").getKeys(false);
            }
            catch(NullPointerException e)
            {
                continue;
            }
            
            Map<String, Boolean> regions = new HashMap<String, Boolean>();
            
            for(String configRegion : configRegions)
            {
                try
                {
                    if(worldConfig.getBoolean("regions." + configRegion + ".spawnRegion"))
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
                String regionDisplayname = worldConfig.getString("regions." + configRegion.getKey() + ".displayname"); // Display Name
                
                // Owner Begin
                String configOwner = worldData.getString("regions." + configRegion.getKey() + ".owner");
                if(configOwner == null)
                {
                    configOwner = Config.getDefaultFaction();
                }
                Faction owner = factions.get(configOwner);
                // Owner End
                
                World world = Bukkit.getWorld(configWorld); // World
                ProtectedRegion region = DependencyManager.getWorldGuard().getRegionManager(world).getRegion(configRegion.getKey()); // WorldGuard Region
                
                // ControlPoint List Begin
                List<ControlPoint> controlPoints = new ArrayList<ControlPoint>();
                if(!configRegion.getValue())
                {
                    Set<String> configControlPoints = worldConfig.getConfigurationSection("regions." + configRegion.getKey() + ".controlpoints").getKeys(false);
                    for(String configControlPoint : configControlPoints)
                    {
                        int x = worldConfig.getInt("regions." + configRegion.getKey() + ".controlpoints." + configControlPoint + ".x");
                        int y = worldConfig.getInt("regions." + configRegion.getKey() + ".controlpoints." + configControlPoint + ".y");
                        int z = worldConfig.getInt("regions." + configRegion.getKey() + ".controlpoints." + configControlPoint + ".z");
                        Location controlpointlocation = new Location(world, x, y, z);
                        
                        Double captureRadius = ((Integer) worldConfig.getInt("regions." + configRegion.getKey() + ".controlpoints." + configControlPoint + ".captureradius")).doubleValue();
                        Float baseInfluence = ((Integer) worldConfig.getInt("regions." + configRegion.getKey() + ".controlpoints." + configControlPoint + ".baseinfluence")).floatValue();
                        
                        Float influence = ((Integer) worldData.getInt("regions." + configRegion.getKey() + ".controlpoints." + configControlPoint + ".influence")).floatValue();
                        Faction controlPointOwner = factions.get(worldData.get("regions." + configRegion.getKey() + ".controlpoints." + configControlPoint + ".owner"));
                        Faction influenceOwner = factions.get(worldData.get("regions." + configRegion.getKey() + ".controlpoints." + configControlPoint + ".influenceowner"));
                        
                        ControlPoint controlPoint = new ControlPoint(configControlPoint, controlPointOwner, controlpointlocation, captureRadius, baseInfluence, influence, influenceOwner);
                        controlPoints.add(controlPoint);
                    }
                }
                // ControlPoint List End
                
                // SpawnPoint Begin
                int x = worldConfig.getInt("regions." + configRegion.getKey() + ".spawnpoint" + ".x");
                int y = worldConfig.getInt("regions." + configRegion.getKey() + ".spawnpoint" + ".y");
                int z = worldConfig.getInt("regions." + configRegion.getKey() + ".spawnpoint" + ".z");
                Location spawnpointlocation = new Location(world, x, y, z);
                SpawnPoint spawnPoint = new SpawnPoint(spawnpointlocation);
                // SpawnPoint End
                
                Float baseInfluence = ((Integer) worldConfig.getInt("regions." + configRegion.getKey() + ".baseinfluence")).floatValue(); // Base Influence
                Float influence = ((Integer) worldData.getInt("regions." + configRegion.getKey() + ".influence")).floatValue(); // Influence
                Faction influenceOwner = factions.get(worldData.get("regions." + configRegion.getKey() + ".influenceowner")); // Influence Owner
                
                CapturableRegion capturableregion = new CapturableRegion(regionDisplayname, configRegion.getKey(), owner, region, world, controlPoints, spawnPoint, baseInfluence, influence, influenceOwner, configRegion.getValue());
                capturableRegions.put(configWorld + "_" + configRegion.getKey(), capturableregion);
            }
        }
        
        // Adjacent Region Setup
        for(String configWorld : worlds)
        {
            FileConfiguration worldConfig = Config.getRegionConfigs().get(configWorld);
            
            Set<String> configRegions;
            try 
            {
                configRegions = worldConfig.getConfigurationSection("regions").getKeys(false);
            }
            catch(NullPointerException e)
            {
                continue;
            }
            
            for(String configRegion : configRegions)
            {
                List<String> configAdjacentRegions = worldConfig.getStringList("regions." + configRegion + ".adjacentregions");
                
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
        Set<String> configFactions = Config.getFactionConfig().getConfigurationSection("factions").getKeys(false);
        for(String configFaction : configFactions)
        {
            Map<String, Object> regionWorlds = Config.getFactionConfig().getConfigurationSection("factions." + configFaction + ".defaultspawn").getValues(false);
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
     * CapturableRegion adjacentRegion = capturableRegions.get(worldId + "_" + adjacentRegionId);
     * adjacentregions.add(adjacentRegion);
     * adjacentRegion.getAdjacentRegions().add(capturableregion);
     * }
     * capturableRegions.get(worldId + "_" + worldId).setAdjacentRegions(adjacentregions);
     * }
     */
    
}