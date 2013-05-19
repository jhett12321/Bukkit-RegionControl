package com.featherminecraft.regioncontrol;

import java.util.List;
import org.bukkit.World;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class CapturableRegion {

    private ProtectedRegion region;
    private World world;
    private Faction owner;
    private List<ControlPoint> controlpoints;
    private SpawnPoint spawnpoint;
    private Long expectedcapturetime;
    private String displayname;
    private List<CapturableRegion> adjacentregions;
    private CaptureTimer timer;
    private Integer influence;
    private Faction influenceowner;
    
    public CapturableRegion(String displayname, ProtectedRegion region, World world, Faction owner, Integer influence, Faction influenceowner)
    {
        this.displayname = displayname;
        this.region = region;
        this.world = world;
        this.owner = owner;
        this.influence = influence;
        this.influenceowner = influenceowner;
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

    public Long getExpectedCaptureTime() {
        return expectedcapturetime;
    }

    public void setExpectedCaptureTime(Long expectedcapturetime) {
        this.expectedcapturetime = expectedcapturetime;
    }

    public String getDisplayname() {
        return displayname;
    }

    public void setAdjacentRegions(List<CapturableRegion> capturableregions) {
        this.adjacentregions = capturableregions;
    }

    public List<CapturableRegion> getAdjacentregions() {
        return adjacentregions;
    }

    public CaptureTimer getTimer() {
        return timer;
    }

    public void setTimer(CaptureTimer timer) {
        this.timer = timer;
    }

    public Integer getInfluence() {
        return influence;
    }

    public void setInfluence(Integer influence) {
        this.influence = influence;
    }

    public Faction getInfluenceOwner() {
        return influenceowner;
    }

    public void setInfluenceOwner(Faction influenceowner) {
        this.influenceowner = influenceowner;
    }

    public int getBaseInfluence() {
        // TODO Auto-generated method stub
        return 0;
    }
}
