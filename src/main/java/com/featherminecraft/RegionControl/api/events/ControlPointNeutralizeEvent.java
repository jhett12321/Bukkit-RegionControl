package com.featherminecraft.RegionControl.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.featherminecraft.RegionControl.Faction;
import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;
import com.featherminecraft.RegionControl.capturableregion.ControlPoint;

/**
 * This event is called when a ControlPoint is neutralized.
 */
public class ControlPointNeutralizeEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();
    private CapturableRegion region;
    private Faction owner;
    private ControlPoint controlPoint;
    
    public ControlPointNeutralizeEvent(ControlPoint controlPoint, CapturableRegion region, Faction owner)
    {
        this.region = region;
        this.owner = owner;
        this.controlPoint = controlPoint;
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
     * Returns the owner of the ControlPoint.
     * 
     * @return the owner
     */
    public Faction getOwner()
    {
        return owner;
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