package com.featherminecraft.regioncontrol;

import java.util.Map;

import org.bukkit.World;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class CapturableRegion {

    private ProtectedRegion region;
    private World world;
    private Faction owner;
    private Map<String, ControlPoint> controlpoints;
    private SpawnPoint spawnpoint;
    
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

    public Map<String, ControlPoint> getControlpoints() {
        return controlpoints;
    }
    
    public void setControlPoints(Map<String, ControlPoint> controlpoints) {
        this.controlpoints = controlpoints;
    }

    public SpawnPoint getSpawnpoint() {
        return spawnpoint;
    }

    public void setSpawnpoint(SpawnPoint spawnpoint) {
        this.spawnpoint = spawnpoint;
    }
}
