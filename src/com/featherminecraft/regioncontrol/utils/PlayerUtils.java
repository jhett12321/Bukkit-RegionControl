package com.featherminecraft.regioncontrol.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.entity.Player;

import com.featherminecraft.regioncontrol.CapturableRegion;
import com.featherminecraft.regioncontrol.ControlPoint;
import com.featherminecraft.regioncontrol.Faction;
import com.featherminecraft.regioncontrol.RegionControl;
import com.featherminecraft.regioncontrol.ServerLogic;
import com.featherminecraft.regioncontrol.SpawnPoint;

public class PlayerUtils {
    public Faction getPlayerFaction(Player player) {
        String group = RegionControl.permission.getPrimaryGroup(player);
        Faction faction = ServerLogic.registeredfactions.get(group);
        return faction;
    }
    
    public List<SpawnPoint> getAvailableSpawnPoints(Player player)
    {
        Map<String,CapturableRegion> capturableregions = ServerLogic.registeredregions;
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
        if(controlpoint.getCapturableRegion().getOwner() == getPlayerFaction(player))
        {
            return true;
        }
        
        for(CapturableRegion adjacentregion : controlpoint.getCapturableRegion().getAdjacentregions())
        {
            if(adjacentregion.getOwner() == getPlayerFaction(player))
            {
                return true;
            }
        }
        
        return false;
    }
}
