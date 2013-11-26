package com.featherminecraft.RegionControl.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;

public class RegionCaptureStatusChangeEvent extends Event
{
    
    private static final HandlerList handlers = new HandlerList();
    
    public static HandlerList getHandlerList()
    {
        return handlers;
    }
    
    private CapturableRegion region;
    
    private Boolean isBeingCaptured;
    
    public RegionCaptureStatusChangeEvent(CapturableRegion region, Boolean isbeingCaptured)
    {
        this.region = region;
        isBeingCaptured = isbeingCaptured;
    }
    
    public Boolean getCaptureStatus()
    {
        return isBeingCaptured;
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
