package com.featherminecraft.regioncontrol.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.featherminecraft.regioncontrol.CapturableRegion;
import com.featherminecraft.regioncontrol.CaptureTimer;

public class CaptureTimeChangeEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private CapturableRegion region;
    private CaptureTimer timer;
    private Long expectedCaptureTime;
    
    public CaptureTimeChangeEvent(CapturableRegion region, Long expectedTime) {
        this.region = region;
        this.timer = region.getTimer();
        this.expectedCaptureTime = expectedTime;
    }

    public CapturableRegion getRegion() {
        return region;
    }

    public CaptureTimer getTimer() {
        return timer;
    }

    public Long getExpectedCaptureTime() {
        return expectedCaptureTime;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
