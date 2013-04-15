package com.featherminecraft.regioncontrol;

import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class ServerLogic {
    static Map<String, ProtectedRegion> registeredregions;
    static Map<ProtectedRegion, Integer> playercount;

    public static void init()
    {
        Utils.getWorldGuard();
        Map<String, ProtectedRegion> regions = null;
//            regions = worldguard.getRegionManager(world).getRegions();
        regions = Config.getRegions();
        for(Entry<String, ProtectedRegion> region : regions.entrySet())
        {
            RegisterRegion(region.getKey(), region.getValue());
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
