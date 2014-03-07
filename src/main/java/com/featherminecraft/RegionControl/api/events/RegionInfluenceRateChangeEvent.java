package com.featherminecraft.RegionControl.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;

/**
 * This event is triggered when the influence rate of a region changes.
 */
public class RegionInfluenceRateChangeEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();
    private CapturableRegion region;
    private Float oldInfluenceRate;
    private Float newInfluenceRate;
    
    public RegionInfluenceRateChangeEvent(CapturableRegion region, Float oldInfluenceRate, Float newInfluenceRate)
    {
        this.region = region;
        this.oldInfluenceRate = oldInfluenceRate;
        this.newInfluenceRate = newInfluenceRate;
    }
    
    /**
     * Returns the list of event handlers for this event.
     * 
     * @return the list of event handlers
     */
    public static HandlerList getHandlerList()
    {
        return handlers;
    }
    
    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }
    
    /**
     * Returns a float between 0.0 & 4.0 indicating the new influence rate of the region.
     * 
     * @return the new influence rate of the region
     */
    public Float getNewInfluenceRate()
    {
        return newInfluenceRate;
    }
    
    /**
     * Returns a float between 0.0 & 4.0 indicating the old influence rate of the region.
     * 
     * @return the old influence rate of the region
     */
    public Float getOldInfluenceRate()
    {
        return oldInfluenceRate;
    }
    
    /**
     * Returns the CapturableRegion associated with this event.
     * 
     * @return the CapturableRegion associated with this event.
     */
    public CapturableRegion getRegion()
    {
        return region;
    }
}