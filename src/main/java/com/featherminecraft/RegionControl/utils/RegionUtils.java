package com.featherminecraft.RegionControl.utils;

import java.util.List;

import org.bukkit.World;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import com.featherminecraft.RegionControl.RCPlayer;
import com.featherminecraft.RegionControl.ServerLogic;
import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;

public class RegionUtils
{
    public void addPlayerToRegion(RCPlayer player, CapturableRegion region)
    {
        List<RCPlayer> currentplayers = region.getPlayers();
        currentplayers.add(player);
        region.setPlayers(currentplayers);
    }
    
    /**
     * Gets a CapturableRegion from a WorldGuard ProtectedRegion and a World.
     * 
     * @param region
     * @param world
     * @return returns a RegionControl WorldGuard Region, or null if region or
     *         world are invalid.
     */
    public CapturableRegion getCapturableRegionFromWorldGuardRegion(ProtectedRegion region, World world)
    {
        try
        {
            return ServerLogic.capturableRegions.get(world.getName() + "_" + region.getId());
        }
        catch(Exception e)
        {
            return null;
        }
    }
    
    public int getRegionPlayerCount(CapturableRegion region)
    {
        return region.getPlayers().size();
    }
    
    public List<RCPlayer> getRegionPlayerList(CapturableRegion region)
    {
        return region.getPlayers();
    }
    
    public void removePlayerFromRegion(RCPlayer player, CapturableRegion region)
    {
        List<RCPlayer> currentplayers = region.getPlayers();
        currentplayers.remove(player);
        region.setPlayers(currentplayers);
    }
}
