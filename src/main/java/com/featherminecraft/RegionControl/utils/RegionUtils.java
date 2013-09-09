package com.featherminecraft.RegionControl.utils;

import java.util.List;

import org.bukkit.World;
import org.bukkit.entity.Player;


import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;
import com.featherminecraft.RegionControl.RCPlayer;
import com.featherminecraft.RegionControl.ServerLogic;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class RegionUtils {
    public static void addPlayerToRegion(RCPlayer player, CapturableRegion region)
    {
        List<RCPlayer> currentplayers = ServerLogic.players.get(region);
        currentplayers.add(player);
        ServerLogic.players.put(region, currentplayers);
    }

    public static void removePlayerFromRegion(Player player, CapturableRegion region)
    {
        List<RCPlayer> currentplayers = ServerLogic.players.get(region);
        currentplayers.remove(player);
    }
    
    public int getRegionPlayerCount(CapturableRegion region)
    {
        return ServerLogic.players.get(region).size();
    }
    
    public List<RCPlayer> getRegionPlayerList(CapturableRegion region)
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
