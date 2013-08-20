package com.featherminecraft.regioncontrol.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.featherminecraft.regioncontrol.capturableregion.CapturableRegion;

public class CaptureStatusChangeEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private CapturableRegion region;
    private Boolean isBeingCaptured;
    
    public CaptureStatusChangeEvent(CapturableRegion region, Boolean isbeingCaptured) 
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
