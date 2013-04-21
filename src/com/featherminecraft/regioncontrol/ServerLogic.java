package com.featherminecraft.regioncontrol;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class ServerLogic {
    static Map<ProtectedRegion, ProtectedRegion> registeredregions;
    static Map<ProtectedRegion, List<Player>> players;

    public static void init()
    {
        Utils.getWorldGuard();
        Map<String, ProtectedRegion> regions = null;
//            regions = worldguard.getRegionManager(world).getRegions();
        regions = Config.getRegions();
        for(Entry<String, ProtectedRegion> region : regions.entrySet())
        {
            RegisterRegion(region.getValue());
        }
    }

    /**
     * Registers a region into the registered regions list.
     *
     * The registered regions list is used to calculate
     * region player counts, and more.
     *
     * @param keystring A String associated with the map of this region.
     * @param region A ProtectedRegion.
     */
    public static void RegisterRegion(ProtectedRegion region)
    {
        registeredregions.put(region, region);
        players.put(region, null);
    }

    public static void addPlayerToRegion(Player player, ProtectedRegion region)
    {
        List<Player> currentplayers = players.get(region);
        currentplayers.add(player);
        players.put(region, currentplayers);
    }

    public static void removePlayerFromRegion(Player player, ProtectedRegion region)
    {
        List<Player> currentplayers = players.get(region);
        currentplayers.remove(player);
    }
    
    public static int getRegionPlayerCount(ProtectedRegion region)
    {
        return players.get(region).size();
    }
    
    public static List<Player> getRegionPlayerList(ProtectedRegion region)
    {
        return players.get(region);
    }
}
