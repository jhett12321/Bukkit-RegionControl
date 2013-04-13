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
    Map<String, ProtectedRegion> registeredregions;
    Map<ProtectedRegion, Integer> playercount;
    public void init()
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

    public void RegisterRegion(String keystring, ProtectedRegion region)
    {
        registeredregions.put(keystring, region);
        playercount.put(region, 0);
    }

    public void addPlayer(Player player, ProtectedRegion region)
    {
        int currentplayercount = playercount.get(region);
        currentplayercount = currentplayercount + 1;
        playercount.put(region, currentplayercount);
    }

    public void removePlayer(Player player, ProtectedRegion region)
    {
        int currentplayercount = playercount.get(region);
        currentplayercount = currentplayercount - 1;
        playercount.put(region, currentplayercount);
    }
}
