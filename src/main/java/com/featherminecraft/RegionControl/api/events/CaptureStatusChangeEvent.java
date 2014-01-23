package com.featherminecraft.RegionControl.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;

/**
 * Called when a region starts to capture, and when a region finishes capturing.
 */
public class CaptureStatusChangeEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();
    private CapturableRegion region;
    private Boolean isBeingCaptured;
    
    public CaptureStatusChangeEvent(CapturableRegion region, Boolean isbeingCaptured)
    {
        this.region = region;
        isBeingCaptured = isbeingCaptured;
    }
    
    /**
     * Returns true when the region is being captured.
     * @return whether the region is being captured
     */
    public Boolean isBeingCaptured()
    {
        return isBeingCaptured;
    }
    
    /**
     * Returns the list of event handlers for this event.
     * @return the list of event handlers.
     */
    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }
    
    /**
     * Returns the CapturableRegion associated with this event.
     * @return the CapturableRegion associated with this event.
     */
    public CapturableRegion getRegion()
    {
        return region;
    }
}