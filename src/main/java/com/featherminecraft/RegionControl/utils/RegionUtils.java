package com.featherminecraft.RegionControl.utils;

import org.bukkit.World;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import com.featherminecraft.RegionControl.ServerLogic;
import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;

public class RegionUtils
{
    public static CapturableRegion getRegionFromWorldGuardRegion(String world, String region)
    {
        return ServerLogic.capturableRegions.get(world + "_" + region);
    }
    
    public static CapturableRegion getRegionFromWorldGuardRegion(World world, ProtectedRegion region)
    {
        return ServerLogic.capturableRegions.get(world.getName() + "_" + region.getId());
    }
}