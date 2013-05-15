package com.featherminecraft.regioncontrol;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class ServerLogic {
    public static Map<String, CapturableRegion> registeredregions;
    public static Map<CapturableRegion, List<Player>> players;
    public static Map<CapturableRegion, CaptureTimer> capturetimers;
    public static Map<String, Faction> factions;
    public static Map<CapturableRegion, Faction> regionowners;
    public static Map<CapturableRegion, SpawnPoint> spawnpoints;
    public static Map<CapturableRegion, List<ControlPoint>> controlpoints;

    public static void init()
    {
        //Faction Setup
        Map<String, Faction> factions = new Config().getFactions();
        for(Entry<String, Faction> faction : factions.entrySet())
        {
            factions.put(faction.getValue().getName(),faction.getValue());
        }
        //Region Setup
        Map<String, ProtectedRegion> regions = null;
        List<World> worlds = new Config().getWorlds();
        for(World world : worlds)
        {
            regions = new Config().getRegionsForWorld(world);
            for(Entry<String, ProtectedRegion> region : regions.entrySet())
            {
                if(region.getValue().getId() != "__global__") {
                    RegisterRegion(region.getValue(), world);
                }
            }
        }
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
    private static void RegisterRegion(ProtectedRegion region, World world)
    {
        Faction defaultfaction = Config.getDefaultFaction();
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
        regionowners.put(capturableregion, defaultfaction);
    }
}
