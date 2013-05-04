package com.featherminecraft.regioncontrol.events;

import java.util.List;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.featherminecraft.regioncontrol.Faction;
import com.featherminecraft.regioncontrol.utils.ServerUtils;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class RegionCaptureEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private ProtectedRegion region;
    private List<Player> players;
    private Faction oldowner;
    private Faction newowner;
    private World world;

    public RegionCaptureEvent(ProtectedRegion region, World world, Faction oldowner, Faction newowner) {
        this.region = region;
        this.players = ServerUtils.getRegionPlayerList(region, world);
        this.oldowner = oldowner;
        this.newowner = newowner;
        this.world = world;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    public List<Player> getPlayers() {
        return players;
    }

    public ProtectedRegion getRegion() {
        return region;
    }
    
    public World getWorld() {
        return world;
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
