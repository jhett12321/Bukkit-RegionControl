package com.featherminecraft.RegionControl.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;

public class RegionCaptureStatusChangeEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private CapturableRegion region;
    private Boolean isBeingCaptured;
    
    public RegionCaptureStatusChangeEvent(CapturableRegion region, Boolean isbeingCaptured) 
    {
        this.region = region;
        this.isBeingCaptured = isbeingCaptured;
    }

    public CapturableRegion getRegion() {
        return region;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public Boolean getCaptureStatus() {
        return isBeingCaptured;
    }
}
