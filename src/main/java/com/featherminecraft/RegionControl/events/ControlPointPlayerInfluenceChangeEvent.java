package com.featherminecraft.RegionControl.events;

import java.util.List;
import java.util.logging.Level;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.featherminecraft.RegionControl.RCPlayer;
import com.featherminecraft.RegionControl.RegionControl;
import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;
import com.featherminecraft.RegionControl.capturableregion.ControlPoint;

public class ControlPointPlayerInfluenceChangeEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();
    
    public static HandlerList getHandlerList()
    {
        return handlers;
    }
    
    private CapturableRegion region;
    private ControlPoint controlPoint;
    private List<RCPlayer> currentPlayers;
    private List<RCPlayer> addedPlayers;
    private List<RCPlayer> removedPlayers;
    
    public ControlPointPlayerInfluenceChangeEvent(CapturableRegion region, ControlPoint controlpoint, List<RCPlayer> currentPlayers, List<RCPlayer> addedPlayers, List<RCPlayer> removedPlayers)
    {
        this.region = region;
        controlPoint = controlpoint;
        this.currentPlayers = currentPlayers;
        this.addedPlayers = addedPlayers;
        this.removedPlayers = removedPlayers;
    }
    
    public List<RCPlayer> getAddedPlayers()
    {
        return addedPlayers;
    }
    
    public ControlPoint getControlPoint()
    {
        return controlPoint;
    }
    
    public List<RCPlayer> getCurrentPlayers()
    {
        return currentPlayers;
    }
    
    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }
    
    public CapturableRegion getRegion()
    {
        return region;
    }
    
    public List<RCPlayer> getRemovedPlayers()
    {
        return removedPlayers;
    }
    
}
