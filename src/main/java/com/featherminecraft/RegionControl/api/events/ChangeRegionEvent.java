package com.featherminecraft.RegionControl.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.featherminecraft.RegionControl.RCPlayer;
import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;

/**
 * This event is fired when a player changes/enters/leaves CapturableRegions.
 */
public class ChangeRegionEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();
    private CapturableRegion oldregion;
    private CapturableRegion newregion;
    private RCPlayer player;
    
    public ChangeRegionEvent(CapturableRegion newregion, CapturableRegion oldregion, RCPlayer player)
    {
        this.oldregion = oldregion;
        this.newregion = newregion;
        this.player = player;
    }
    
    /**
     * Returns the list of event handlers for this event.
     * 
     * @return the list of event handlers
     */
    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }
    
    /**
     * Returns the player's new CapturableRegion, otherwise null if the player does not enter a new region.
     * 
     * @return the players new region
     */
    public CapturableRegion getNewRegion()
    {
        return newregion;
    }
    
    /**
     * Returns the player's old CapturableRegion, otherwise null if the player was not in a region.
     * 
     * @return the players old region
     */
    public CapturableRegion getOldRegion()
    {
        return oldregion;
    }
    
    /**
     * Returns the RCPlayer that changed regions.
     * 
     * @return the player that changed regions
     */
    public RCPlayer getPlayer()
    {
        return player;
    }
}