package com.featherminecraft.RegionControl.api.events;

import java.util.List;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.featherminecraft.RegionControl.RCPlayer;
import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;
import com.featherminecraft.RegionControl.capturableregion.ControlPoint;

/**
 * This event is fired when more, or less players are detected on the ControlPoint.
 */
public class ControlPointInfluenceRateChangeEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();
    private CapturableRegion region;
    private ControlPoint controlPoint;
    private List<RCPlayer> currentPlayers;
    private List<RCPlayer> addedPlayers;
    private List<RCPlayer> removedPlayers;
    
    public ControlPointInfluenceRateChangeEvent(CapturableRegion region, ControlPoint controlPoint, List<RCPlayer> currentPlayers, List<RCPlayer> addedPlayers, List<RCPlayer> removedPlayers)
    {
        this.region = region;
        this.controlPoint = controlPoint;
        this.currentPlayers = currentPlayers;
        this.addedPlayers = addedPlayers;
        this.removedPlayers = removedPlayers;
    }
    
    /**
     * Returns the new players on the ControlPoint.
     * 
     * @return the new players
     */
    public List<RCPlayer> getAddedPlayers()
    {
        return addedPlayers;
    }
    
    /**
     * Returns the ControlPoint associated with this event.
     * 
     * @return the ControlPoint associated with this event.
     */
    public ControlPoint getControlPoint()
    {
        return controlPoint;
    }
    
    /**
     * Returns the existing players on the ControlPoint.
     * 
     * @return the existing players
     */
    public List<RCPlayer> getCurrentPlayers()
    {
        return currentPlayers;
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
     * Returns the CapturableRegion associated with this event.
     * 
     * @return the CapturableRegion associated with this event.
     */
    public CapturableRegion getRegion()
    {
        return region;
    }
    
    /**
     * Returns the players that are no-longer on the ControlPoint.
     * 
     * @return the players no-longer on the ControlPoint.
     */
    public List<RCPlayer> getRemovedPlayers()
    {
        return removedPlayers;
    }
    
}