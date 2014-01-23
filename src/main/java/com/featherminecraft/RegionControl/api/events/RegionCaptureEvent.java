package com.featherminecraft.RegionControl.api.events;

import java.util.List;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.featherminecraft.RegionControl.Faction;
import com.featherminecraft.RegionControl.RCPlayer;
import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;

/**
 * This event is triggered when a faction successfully captures a region, <b>NOT</b> when a region is defended.<br>
 * See RegionDefendEvent for the equivalent defend event.
 */
public class RegionCaptureEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();
    private CapturableRegion region;
    private List<RCPlayer> players;
    private Faction oldowner;
    private Faction newowner;
    
    public RegionCaptureEvent(CapturableRegion region, Faction oldowner, Faction newowner)
    {
        this.region = region;
        players = region.getPlayers();
        this.oldowner = oldowner;
        this.newowner = newowner;
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
     * Returns the new owner of the region.
     * 
     * @return the new owner
     */
    public Faction getNewOwner()
    {
        return newowner;
    }
    
    /**
     * Returns the old owner of the region.
     * 
     * @return the old owner
     */
    public Faction getOldOwner()
    {
        return oldowner;
    }
    
    /**
     * Returns the players within the region at the time of capture.
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