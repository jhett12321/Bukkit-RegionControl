package com.featherminecraft.RegionControl.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;

// This event is triggered upon a change in influence rate, or influence
// changing direction.
public class RegionInfluenceRateChangeEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();
    
    public static HandlerList getHandlerList()
    {
        return handlers;
    }
    
    private CapturableRegion region;
    private Float oldInfluenceRate;
    
    private Float newInfluenceRate;
    
    public RegionInfluenceRateChangeEvent(CapturableRegion region, Float oldInfluenceRate, Float newInfluenceRate)
    {
        this.region = region;
        this.oldInfluenceRate = oldInfluenceRate;
        this.newInfluenceRate = newInfluenceRate;
    }
    
    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }
    
    public Float getNewInfluenceRate()
    {
        return newInfluenceRate;
    }
    
    public Float getOldInfluenceRate()
    {
        return oldInfluenceRate;
    }
    
    public CapturableRegion getRegion()
    {
        return region;
    }
    
}