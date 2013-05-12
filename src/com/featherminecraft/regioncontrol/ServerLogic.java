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
    public static Map<String, ControlPoint> controlpoints;

    public static void init()
    {
        //Faction Setup
        Map<String, String/*Insert Permission Group Data Type*/> factions = Config.getFactions();
        for(Entry<String, String> faction : factions.entrySet())
        {
            RegisterFaction(faction.getKey(), faction.getValue());
        }
        //Region Setup
        Map<String, ProtectedRegion> regions = null;
        List<World> worlds = Config.getWorlds();
        for(World world : worlds)
        {
            regions = Config.getRegionsForWorld(world);
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
        for(Entry<String, Location> controlpoint : controllablepoints.entrySet())
        {
            RegisterControlPoint(controlpoint.getKey(), controlpoint.getValue(), capturableregion);
        }
        
        capturableregion.setControlPoints(controlpoints);
        
        CaptureTimer capturetimer = new CaptureTimer(capturableregion, 0);
        capturetimer.runTaskTimer(RegionControl.plugin, 20, 20);
        
        registeredregions.put(capturableregion.getWorld() + "_" + capturableregion.getRegion(), capturableregion);
        players.put(capturableregion, null);
        capturetimers.put(capturableregion, capturetimer);
        regionowners.put(capturableregion, defaultfaction);
    }
    
    private static void RegisterFaction(String factionname,String permissiongroup)
    {
        Faction faction = new Faction(factionname,permissiongroup);
        factions.put(factionname,faction);
    }
    
    /**
     * Registers a control point.
     * @param controlpointid A string, consisting of either "a", "b", "c" or "d"
     * @param location A Location where the control point is located.
     * @param region The region which this control point is located.
     * @param world The world which this control point is located.
     */
    private static void RegisterControlPoint(String controlpointname, Location location, CapturableRegion region)
    {
        ControlPoint controlpoint = new ControlPoint(controlpointname, region, location);
        controlpoints.put(region.getWorld().getName() + region.getRegion().getId() + controlpointname, controlpoint);
    }
}
