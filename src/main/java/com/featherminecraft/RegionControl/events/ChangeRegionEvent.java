package com.featherminecraft.RegionControl.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.featherminecraft.RegionControl.RCPlayer;
import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;

//This event is triggered upon a player changing regions.
public class ChangeRegionEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private CapturableRegion oldregion;
    private CapturableRegion newregion;
    private RCPlayer player;

    public ChangeRegionEvent(CapturableRegion newregion, CapturableRegion oldregion, RCPlayer player) {
//        RegionControl.plugin.getLogger().log(Level.INFO, "DEBUG: Triggered ChangeRegionEvent");
        this.oldregion = oldregion;
        this.newregion = newregion;
        this.player = player;
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
}
