package com.featherminecraft.regioncontrol;

import java.util.List;
import java.util.Map;

import org.bukkit.World;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class CapturableRegion {

    private ProtectedRegion region;
    private World world;
    private Faction owner;
    private List<ControlPoint> controlpoints;
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

    public List<ControlPoint> getControlpoints() {
        return controlpoints;
    }
    
    public void setControlPoints(List<ControlPoint> regionalcontrolpoints) {
        this.controlpoints = regionalcontrolpoints;
    }

    public SpawnPoint getSpawnPoint() {
        return spawnpoint;
    }

    public void setSpawnPoint(SpawnPoint spawnpoint) {
        this.spawnpoint = spawnpoint;
    }
}
