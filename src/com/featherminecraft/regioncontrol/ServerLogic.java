package com.featherminecraft.regioncontrol;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class ServerLogic {
    static Map<String, ProtectedRegion> registeredregions;
    public static Map<String, List<Player>> players;
    public static Map<String, Integer> capturetimers;
    public static Map<String, Faction> factions;
    public static Map<String, Faction> regionowners;
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
                    //TODO register control points for each region.
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
        registeredregions.put(world.getName() + region.getId(), region);
        players.put(world.getName() + region.getId(), null);
        capturetimers.put(world.getName() + region.getId(), 0);
        regionowners.put(world.getName() + region.getId(), defaultfaction);
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
    @SuppressWarnings("unused")
    private static void RegisterControlPoint(String controlpointid, Location location, ProtectedRegion region, World world)
    {
        String controlpointname = world.getName() + "_" + region.getId() + "_" + controlpointid;
        ControlPoint controlpoint = new ControlPoint(controlpointname, region, world, location);
        controlpoints.put(controlpointname, controlpoint);
    }
}
