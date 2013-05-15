package com.featherminecraft.regioncontrol.events;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.featherminecraft.regioncontrol.CapturableRegion;
import com.featherminecraft.regioncontrol.Faction;
import com.featherminecraft.regioncontrol.utils.ServerUtils;

public class RegionCaptureEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private CapturableRegion region;
    private List<Player> players;
    private Faction oldowner;
    private Faction newowner;

    public RegionCaptureEvent(CapturableRegion region, Faction oldowner, Faction newowner) {
        this.region = region;
        this.players = ServerUtils.getRegionPlayerList(region);
        this.oldowner = oldowner;
        this.newowner = newowner;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    public List<Player> getPlayers() {
        return players;
    }

    public CapturableRegion getCapturableRegion() {
        return region;
    }

    public Faction getOldOwner() {
        return oldowner;
    }
    
    public Faction getNewOwner() {
        return newowner;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}
