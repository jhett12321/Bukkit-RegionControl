package com.featherminecraft.RegionControl.api;

import org.bukkit.World;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import com.featherminecraft.RegionControl.ServerLogic;
import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;

public class RegionAPI
{
    /**
     * Returns the CapturableRegion represented by this world and region.
     * 
     * @param world
     *            The world name where this region is located.
     * @param region
     *            The WorldGuard ProtectedRegion id of this region.
     * @return the CapturableRegion represented by this world and region.
     */
    public static CapturableRegion getRegionFromWorldGuardRegion(String world, String region)
    {
        return ServerLogic.capturableRegions.get(world + "_" + region);
    }
    
    /**
     * Returns the CapturableRegion represented by this world and region.
     * 
     * @param world
     *            The world where this region is located.
     * @param region
     *            The WorldGuard ProtectedRegion of this region.
     * @return the CapturableRegion represented by this world and region.
     */
    public static CapturableRegion getRegionFromWorldGuardRegion(World world, ProtectedRegion region)
    {
        return ServerLogic.capturableRegions.get(world.getName() + "_" + region.getId());
    }
}