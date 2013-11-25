package com.featherminecraft.RegionControl.events;

import java.util.List;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.featherminecraft.RegionControl.Faction;
import com.featherminecraft.RegionControl.RCPlayer;
import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;
import com.featherminecraft.RegionControl.utils.RegionUtils;

public class RegionDefendEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private CapturableRegion region;
    private List<RCPlayer> players;

    private Faction defender;

    public RegionDefendEvent(CapturableRegion region, Faction defender) {
        this.region = region;
        this.players = new RegionUtils().getRegionPlayerList(region);
        this.defender = defender;
    }

    public CapturableRegion getCapturableRegion() {
        return this.region;
    }

    public Faction getDefender() {
        return this.defender;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public List<RCPlayer> getPlayers() {
        return this.players;
    }
}
