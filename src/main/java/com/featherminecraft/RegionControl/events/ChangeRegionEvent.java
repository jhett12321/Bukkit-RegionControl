package com.featherminecraft.RegionControl.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.featherminecraft.RegionControl.RCPlayer;
import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;

//This event is triggered upon a player changing regions.
public class ChangeRegionEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private CapturableRegion oldregion;
    private CapturableRegion newregion;

    private RCPlayer player;

    public ChangeRegionEvent(CapturableRegion newregion,
            CapturableRegion oldregion, RCPlayer player) {
        this.oldregion = oldregion;
        this.newregion = newregion;
        this.player = player;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public CapturableRegion getNewRegion() {
        return this.newregion;
    }

    public CapturableRegion getOldRegion() {
        return this.oldregion;
    }

    public RCPlayer getPlayer() {
        return this.player;
    }
}
