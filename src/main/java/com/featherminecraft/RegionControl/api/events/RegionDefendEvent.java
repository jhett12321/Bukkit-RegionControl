package com.featherminecraft.RegionControl.api.events;

import java.util.List;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.featherminecraft.RegionControl.Faction;
import com.featherminecraft.RegionControl.RCPlayer;
import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;

/**
 * This event is triggered when a region is successfully defended, <b>NOT</b> when it has been captured. <br>
 * See RegionCaptureEvent for the equivalent capture event.
 */
public class RegionDefendEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();
    private CapturableRegion region;
    private List<RCPlayer> players;
    private Faction owner;
    
    public RegionDefendEvent(CapturableRegion region, Faction owner)
    {
        this.region = region;
        players = region.getPlayers();
        this.owner = owner;
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
     * Returns the owner of the region.
     * 
     * @return the owner
     */
    public Faction getOwner()
    {
        return owner;
    }
    
    /**
     * Returns the players within the region at the time of defense.
     * 
     * @return the players within the region
     */
    public List<RCPlayer> getPlayers()
    {
        return players;
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