package com.featherminecraft.RegionControl.events;

import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.featherminecraft.RegionControl.Faction;
import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;
import com.featherminecraft.RegionControl.capturableregion.ControlPoint;

//This event is triggered upon a Successful ControlPoint Defense.
public class ControlPointDefendEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private CapturableRegion region;
    private World world;
    private Faction owner;

    private ControlPoint controlpoint;

    public ControlPointDefendEvent(CapturableRegion region, Faction owner,
            ControlPoint controlpoint) {
        this.region = region;
        this.owner = owner;
        this.controlpoint = controlpoint;
    }

    public ControlPoint getControlpoint() {
        return this.controlpoint;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public Faction getOwner() {
        return this.owner;
    }

    public CapturableRegion getRegion() {
        return this.region;
    }

    public World getWorld() {
        return this.world;
    }
}