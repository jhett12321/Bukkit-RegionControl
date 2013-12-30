package com.featherminecraft.RegionControl.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.featherminecraft.RegionControl.Faction;
import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;
import com.featherminecraft.RegionControl.capturableregion.ControlPoint;

public class ControlPointNeutraliseEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();
    
    public static HandlerList getHandlerList()
    {
        return handlers;
    }
    
    private CapturableRegion region;
    private Faction oldOwner;
    
    private ControlPoint controlPoint;
    
    public ControlPointNeutraliseEvent(CapturableRegion region, Faction oldowner, ControlPoint controlpoint)
    {
        this.region = region;
        oldOwner = oldowner;
        controlPoint = controlpoint;
    }
    
    public ControlPoint getControlPoint()
    {
        return controlPoint;
    }
    
    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }
    
    public Faction getOldOwner()
    {
        return oldOwner;
    }
    
    public CapturableRegion getRegion()
    {
        return region;
    }
}