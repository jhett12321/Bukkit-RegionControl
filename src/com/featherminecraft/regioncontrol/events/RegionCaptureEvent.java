package com.featherminecraft.regioncontrol.events;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.featherminecraft.regioncontrol.ServerLogic;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class RegionCaptureEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private ProtectedRegion region;
    private List<Player> players;
    private String oldowner;
    private String newowner;

    public RegionCaptureEvent(ProtectedRegion region) {
        this.region = region;
        List Players = ServerLogic.getRegionPlayerList(region);
        this.oldowner = oldowner;
        this.newowner = newowner;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public ProtectedRegion getRegion() {
        return region;
    }
    
    public List<Player> getPlayers() {
        return players;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}
