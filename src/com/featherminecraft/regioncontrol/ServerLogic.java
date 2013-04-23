package com.featherminecraft.regioncontrol;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.World;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class ServerLogic {
    static Map<String, ProtectedRegion> registeredregions;
    static Map<String, List<Player>> players;

    public static void init()
    {
        Utils.getWorldGuard();
        Map<String, ProtectedRegion> regions = null;
        List<World> worlds = Config.getWorlds();
        for(World world : worlds)
        {
            regions = Config.getRegionsForWorld(world);
            for(Entry<String, ProtectedRegion> region : regions.entrySet())
            {
                if(region.getValue().getTypeName() != "__global__") {
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
    public static void RegisterRegion(ProtectedRegion region, World world)
    {
        registeredregions.put(world.getName() + region.getId(), region);
        players.put(world.getName() + region.getId(), null);
    }

    public static void addPlayerToRegion(Player player, ProtectedRegion region, World world)
    {
        List<Player> currentplayers = players.get(world.getName() + region.getId());
        currentplayers.add(player);
        players.put(world.getName() + region.getId(), currentplayers);
    }

    public static void removePlayerFromRegion(Player player, ProtectedRegion region, World world)
    {
        List<Player> currentplayers = players.get(world.getName() + region.getId());
        currentplayers.remove(player);
    }
    
    public static int getRegionPlayerCount(ProtectedRegion region, World world)
    {
        return players.get(world.getName() + region.getId()).size();
    }
    
    public static List<Player> getRegionPlayerList(ProtectedRegion region, World world)
    {
        return players.get(world.getName() + region.getId());
    }
}
