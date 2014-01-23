package com.featherminecraft.RegionControl.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.featherminecraft.RegionControl.Faction;
import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;
import com.featherminecraft.RegionControl.capturableregion.ControlPoint;

/**
 * This event is fired on a successful ControlPoint defense, <b>NOT</b> a capture.<br>
 * See ControlPointCaptureEvent for the equivalent capture event.
 * 
 */
public class ControlPointDefendEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();
    private CapturableRegion region;
    private Faction owner;
    private ControlPoint controlPoint;
    
    public ControlPointDefendEvent(ControlPoint controlPoint, CapturableRegion region, Faction owner)
    {
        this.controlPoint = controlPoint;
        this.region = region;
        this.owner = owner;
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
     * Returns the current owner of the ControlPoint.
     * 
     * @return the current owner
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