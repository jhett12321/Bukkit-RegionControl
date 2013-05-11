package com.featherminecraft.regioncontrol;

import java.util.List;

import org.bukkit.World;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class CapturableRegion {

    private ProtectedRegion region;
    private Faction owner;
    private List<ControlPoint> controlpoints;
    private World world;
    
    public CapturableRegion(ProtectedRegion region, World world, Faction owner)
    {
        this.region = region;
        this.world = world;
        this.owner = owner;
    }

    public Faction getOwner() {
        return owner;
    }

    public void setOwner(Faction owner) {
        this.owner = owner;
    }

    public ProtectedRegion getRegion() {
        return region;
    }
    
    public World getWorld()
    {
        return world;
    }

    public List<ControlPoint> getControlpoints() {
        return controlpoints;
    }
}
