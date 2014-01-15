package com.featherminecraft.RegionControl.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;

public class InfluenceOwnerChangeEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();
    
    public static HandlerList getHandlerList()
    {
        return handlers;
    }
    
    private CapturableRegion region;
    
    public InfluenceOwnerChangeEvent(CapturableRegion region)
    {
        this.region = region;
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
    
}