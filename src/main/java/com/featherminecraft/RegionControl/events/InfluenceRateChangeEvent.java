package com.featherminecraft.RegionControl.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;

//This event is triggered upon a change in influence rate, or influence changing direction.
public class InfluenceRateChangeEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();
    private CapturableRegion region;
    private Float oldInfluenceRate;
    private Float newInfluenceRate;

    public InfluenceRateChangeEvent(CapturableRegion region, Float oldInfluenceRate, Float newInfluenceRate)
    {
        this.region = region;
        this.oldInfluenceRate = oldInfluenceRate;
        this.newInfluenceRate = newInfluenceRate;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
    
    public CapturableRegion getRegion()
    {
        return region;
    }
    
    public Float getNewInfluenceRate()
    {
        return newInfluenceRate;
    }
    
    public Float getOldInfluenceRate()
    {
        return oldInfluenceRate;
    }

}
