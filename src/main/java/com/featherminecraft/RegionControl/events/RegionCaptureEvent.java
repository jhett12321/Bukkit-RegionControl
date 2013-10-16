package com.featherminecraft.RegionControl.events;

import java.util.List;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.featherminecraft.RegionControl.Faction;
import com.featherminecraft.RegionControl.RCPlayer;
import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;
import com.featherminecraft.RegionControl.utils.RegionUtils;

//This event is triggered upon capture of a region.
public class RegionCaptureEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private CapturableRegion region;
    private List<RCPlayer> players;
    private Faction oldowner;
    private Faction newowner;

    public RegionCaptureEvent(CapturableRegion region, Faction oldowner, Faction newowner) {
        this.region = region;
        this.players = new RegionUtils().getRegionPlayerList(region);
        this.oldowner = oldowner;
        this.newowner = newowner;
        
        region.setOwner(newowner);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    public List<RCPlayer> getPlayers() {
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
