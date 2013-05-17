package com.featherminecraft.regioncontrol;

import java.util.List;
import java.util.Map;
import org.bukkit.World;
import org.bukkit.entity.Player;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class ServerLogic {

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
        //Faction Setup
        registeredfactions = Config.setupFactions();

        //Region Setup
        registeredregions = Config.setupRegions();
        
        //SpawnPoint Setup
        registeredspawnpoints = Config.setupSpawnPoints();
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
