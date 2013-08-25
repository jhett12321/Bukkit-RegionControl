package com.featherminecraft.regioncontrol.utils;

import java.util.List;

import org.bukkit.World;
import org.bukkit.entity.Player;

import com.featherminecraft.regioncontrol.ServerLogic;
import com.featherminecraft.regioncontrol.capturableregion.CapturableRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class RegionUtils {
    public static void addPlayerToRegion(Player player, CapturableRegion region)
    {
        List<Player> currentplayers = ServerLogic.players.get(region);
        currentplayers.add(player);
        ServerLogic.players.put(region, currentplayers);
    }

    public static void removePlayerFromRegion(Player player, CapturableRegion region)
    {
        List<Player> currentplayers = ServerLogic.players.get(region);
        currentplayers.remove(player);
    }
    
    public int getRegionPlayerCount(CapturableRegion region)
    {
        return ServerLogic.players.get(region).size();
    }
    
    public List<Player> getRegionPlayerList(CapturableRegion region)
    {
        return ServerLogic.players.get(region);
    }

    /**
     * Gets a CapturableRegion from a WorldGuard ProtectedRegion and a World.
     * @param region
     * @param world
     * @return returns a RegionControl WorldGuard Region, or null if region or world are invalid.
     */
    public CapturableRegion getCapturableRegionFromWorldGuardRegion(ProtectedRegion region, World world)
    {
        try {
            return ServerLogic.capturableRegions.get(world.getName() + "_" + region.getId());
        } catch (Exception e) {
            return null;
        }
    }
}
