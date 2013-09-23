package com.featherminecraft.RegionControl.events;

import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;
import com.featherminecraft.RegionControl.capturableregion.ControlPoint;
import com.featherminecraft.RegionControl.Faction;

//This event is triggered upon a Control Point Capture.
public class ControlPointCaptureEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private CapturableRegion region;
    private World world;
    private Faction owner;
    private ControlPoint controlpoint;

    public ControlPointCaptureEvent(CapturableRegion region, Faction owner, ControlPoint controlpoint) {
        this.region = region;
        this.owner = owner;
        this.controlpoint = controlpoint;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public CapturableRegion getRegion()
    {
        return region;
    }

    public World getWorld() {
        return world;
    }

    public Faction getOwner() {
        return owner;
    }

    public ControlPoint getControlpoint() {
        return controlpoint;
    }
}
