package com.featherminecraft.RegionControl.events;

import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.featherminecraft.RegionControl.RCPlayer;
import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;

//This event is triggered upon a player changing regions.
//TODO Incomplete
public class ChangeRegionEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private CapturableRegion oldregion;
    private CapturableRegion newregion;
    private RCPlayer player;
    private World world;

    public ChangeRegionEvent(CapturableRegion newregion, CapturableRegion oldregion, RCPlayer player, World world) {
        this.oldregion = oldregion;
        this.newregion = newregion;
        this.player = player;
        this.world = world;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public CapturableRegion getOldRegion() {
        return oldregion;
    }
    
    public CapturableRegion getNewRegion() {
        return newregion;
    }

    public RCPlayer getPlayer() {
        return player;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public World getWorld() {
        return world;
    }
}
