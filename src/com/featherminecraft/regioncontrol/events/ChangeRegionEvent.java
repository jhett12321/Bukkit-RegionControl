package com.featherminecraft.regioncontrol.events;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.featherminecraft.regioncontrol.CapturableRegion;

public class ChangeRegionEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private CapturableRegion oldregion;
    private CapturableRegion newregion;
    private Player player;
    private World world;

    public ChangeRegionEvent(CapturableRegion newregion, CapturableRegion oldregion,  Player player, World world) {
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

    public Player getPlayer() {
        return player;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public World getWorld() {
        return world;
    }
}
