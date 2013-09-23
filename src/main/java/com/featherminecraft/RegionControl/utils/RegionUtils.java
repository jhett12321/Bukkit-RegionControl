package com.featherminecraft.RegionControl.utils;

import java.util.List;

import org.bukkit.World;
import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;
import com.featherminecraft.RegionControl.RCPlayer;
import com.featherminecraft.RegionControl.ServerLogic;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class RegionUtils {
    public void addPlayerToRegion(RCPlayer player, CapturableRegion region)
    {
        List<RCPlayer> currentplayers = ServerLogic.regionPlayers.get(region);
        currentplayers.add(player);
        ServerLogic.regionPlayers.put(region, currentplayers);
    }

    public void removePlayerFromRegion(RCPlayer player, CapturableRegion region)
    {
        List<RCPlayer> currentplayers = ServerLogic.regionPlayers.get(region);
        currentplayers.remove(player);
    }
    
    public int getRegionPlayerCount(CapturableRegion region)
    {
        return ServerLogic.regionPlayers.get(region).size();
    }
    
    public List<RCPlayer> getRegionPlayerList(CapturableRegion region)
    {
        return ServerLogic.regionPlayers.get(region);
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
