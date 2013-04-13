package com.featherminecraft.regioncontrol.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class ChangeRegionEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private ProtectedRegion oldregion;
    private ProtectedRegion newregion;
    private Player player;

    public ChangeRegionEvent(ProtectedRegion newregion, ProtectedRegion oldregion,  Player player) {
        this.oldregion = oldregion;
        this.newregion = newregion;
        this.player = player;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public ProtectedRegion getOldRegion() {
        return oldregion;
    }
    
    public ProtectedRegion getNewRegion() {
        return newregion;
    }

    public Player getPlayer() {
        return player;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}
