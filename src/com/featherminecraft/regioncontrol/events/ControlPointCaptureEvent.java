package com.featherminecraft.regioncontrol.events;

import java.util.List;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.featherminecraft.regioncontrol.ControlPoint;
import com.featherminecraft.regioncontrol.Faction;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class ControlPointCaptureEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private ProtectedRegion region;
    private World world;
    private Faction oldowner;
    private Faction newowner;
    private ControlPoint controlpoint;
    private List<Player> players;

    public ControlPointCaptureEvent(ProtectedRegion region, World world, Faction oldowner, Faction newowner, ControlPoint controlpoint, List<Player> players) {
        this.region = region;
        this.players = players;
        this.world = world;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public World getWorld() {
        return world;
    }
}
