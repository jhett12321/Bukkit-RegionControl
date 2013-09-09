package com.featherminecraft.RegionControl.utils;

import static com.sk89q.worldguard.bukkit.BukkitUtil.toVector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.entity.Player;


import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;
import com.featherminecraft.RegionControl.capturableregion.ControlPoint;
import com.featherminecraft.RegionControl.capturableregion.SpawnPoint;
import com.featherminecraft.RegionControl.Faction;
import com.featherminecraft.RegionControl.RegionControl;
import com.featherminecraft.RegionControl.ServerLogic;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class PlayerUtils {
    public Faction getPlayerFaction(Player player) {
        String group = RegionControl.permission.getPrimaryGroup(player);
        Faction faction = ServerLogic.factions.get(group);
        return faction;
    }
    
    public List<SpawnPoint> getAvailableSpawnPoints(Player player)
    {
        Map<String,CapturableRegion> capturableregions = ServerLogic.capturableRegions;
        List<SpawnPoint> availablespawnpoints = new ArrayList<SpawnPoint>();
        
        for(Entry<String, CapturableRegion> capturableregion : capturableregions.entrySet())
        {
            if(capturableregion.getValue().getOwner() == getPlayerFaction(player))
            {
                availablespawnpoints.add(capturableregion.getValue().getSpawnPoint());
            }
        }
        
        return availablespawnpoints;
    }
    
    /**
     * Checks whether a player can capture the selected ControlPoint.
     * @param controlpoint - A ControlPoint.
     * @param player - The Player to be checked
     * @return Boolean whether a player can capture the ControlPoint
     */
    public Boolean canCapture(ControlPoint controlpoint, Player player)
    {
        if(controlpoint.getRegion().getOwner() == getPlayerFaction(player))
        {
            return true;
        }
        
        for(CapturableRegion adjacentregion : controlpoint.getRegion().getAdjacentRegions())
        {
            if(adjacentregion.getOwner() == getPlayerFaction(player))
            {
                return true;
            }
        }
        
        return false;
    }
    
    public CapturableRegion getCurrentRegion(Player player)
    {
        CapturableRegion playerRegion = null;
        
        WorldGuardPlugin worldguard = Utils.getWorldGuard();
        RegionManager regionmanager = worldguard.getRegionManager(player.getWorld());
        Vector location = toVector(player.getLocation());
        ApplicableRegionSet currentregions = regionmanager.getApplicableRegions(location);
        
        if(currentregions != null && currentregions.size() == 1) {
            for(ProtectedRegion region : currentregions)
            {
                CapturableRegion capturableregion = new RegionUtils().getCapturableRegionFromWorldGuardRegion(region, player.getWorld());
                if(capturableregion.getRegion().contains(location))
                    playerRegion = capturableregion;
            }
        }
        
        return playerRegion;
    }
}