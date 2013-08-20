package com.featherminecraft.regioncontrol.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.featherminecraft.regioncontrol.Faction;
import com.featherminecraft.regioncontrol.capturableregion.CapturableRegion;
import com.featherminecraft.regioncontrol.capturableregion.ControlPoint;

public class ControlPointNeutraliseEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private CapturableRegion region;
    private Faction oldOwner;
    private ControlPoint controlPoint;

    public ControlPointNeutraliseEvent(CapturableRegion region, Faction oldowner, ControlPoint controlpoint) {
        this.region = region;
        this.oldOwner = oldowner;
        this.controlPoint = controlpoint;
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

    public Faction getOldOwner() {
        return oldOwner;
    }

    public ControlPoint getControlPoint() {
        return controlPoint;
    }
}
