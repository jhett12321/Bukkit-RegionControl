package com.featherminecraft.regioncontrol.events;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.featherminecraft.regioncontrol.CapturableRegion;
import com.featherminecraft.regioncontrol.Faction;
import com.featherminecraft.regioncontrol.utils.ServerUtils;

public class RegionDefendEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private CapturableRegion region;
    private List<Player> players;
    private Faction defender;

    public RegionDefendEvent(CapturableRegion region, Faction defender) {
        this.region = region;
        this.players = ServerUtils.getRegionPlayerList(region);
        this.defender = defender;
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
    
    public Faction getDefender() {
        return defender;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}
