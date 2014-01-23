package com.featherminecraft.RegionControl.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.featherminecraft.RegionControl.Faction;
import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;
import com.featherminecraft.RegionControl.capturableregion.ControlPoint;

/**
 * This event is fired on a successful ControlPoint capture, <b>NOT</b> a defense.<br>
 * See ControlPointDefendEvent for the equivalent defense event.
 * 
 */
public class ControlPointCaptureEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();
    private CapturableRegion region;
    private Faction newOwner;
    private ControlPoint controlPoint;
    
    public ControlPointCaptureEvent(ControlPoint controlPoint, CapturableRegion region, Faction newOwner)
    {
        this.controlPoint = controlPoint;
        this.region = region;
        this.newOwner = newOwner;
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
    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }
    
    /**
     * Returns the new owner of this ControlPoint.
     * 
     * @return the new owner
     */
    public Faction getNewOwner()
    {
        return newOwner;
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