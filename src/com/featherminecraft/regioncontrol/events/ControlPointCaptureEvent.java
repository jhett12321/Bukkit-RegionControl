package com.featherminecraft.regioncontrol.events;

import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.featherminecraft.regioncontrol.Faction;
import com.featherminecraft.regioncontrol.capturableregion.CapturableRegion;
import com.featherminecraft.regioncontrol.capturableregion.ControlPoint;

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
