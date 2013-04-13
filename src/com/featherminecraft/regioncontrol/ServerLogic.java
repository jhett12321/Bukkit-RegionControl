package com.featherminecraft.regioncontrol;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class ServerLogic {
    static Map<String, ProtectedRegion> registeredregions;
    static Map<ProtectedRegion, Integer> playercount;

    public static void init()
    {
        List<World> worlds = Bukkit.getWorlds();
        WorldGuardPlugin worldguard = RegionControl.getWorldGuard();
        Map<String, ProtectedRegion> regions = null;
        for(World world : worlds)
        {
            regions = worldguard.getRegionManager(world).getRegions(); //TODO replace with config file for player to state controllable regions instead of loading all regions.
            for(Entry<String, ProtectedRegion> region : regions.entrySet())
            {
                RegisterRegion(region.getKey(), region.getValue());
            }
        }
    }

    /**
     * Registers a region into the registered regions list.
     *
     * The registered regions list is used to calculate
     * region player counts.
     *
     * @param keystring A String associated with the map of this region.
     * @param region A ProtectedRegion.
     */
    public static void RegisterRegion(String keystring, ProtectedRegion region)
    {
        registeredregions.put(keystring, region);
        playercount.put(region, 0);
    }

    public static void addPlayerToRegion(Player player, ProtectedRegion region)
    {
        int currentplayercount = playercount.get(region);
        currentplayercount = currentplayercount + 1;
        playercount.put(region, currentplayercount);
    }

    public static void removePlayerFromRegion(Player player, ProtectedRegion region)
    {
        int currentplayercount = playercount.get(region);
        currentplayercount = currentplayercount - 1;
        playercount.put(region, currentplayercount);
    }
    
    public static int getRegionalPlayerCount(ProtectedRegion region)
    {
        return playercount.get(region);
    }
}
