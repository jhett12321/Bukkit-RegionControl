package com.featherminecraft.regioncontrol;

import java.util.HashMap;
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
    private String displayname;
    private List<CapturableRegion> adjacentregions;
    private CaptureTimer captureTimer;
    public Map<Faction,Float> influence = new HashMap<Faction,Float>();
    private Faction influenceowner;
    private Boolean beingcaptured = false;
    private Faction majorityController;
    private float influenceRate;
    
    public CapturableRegion(String displayname, ProtectedRegion region, World world, Faction owner, Float influence, Faction influenceowner) {
        this.displayname = displayname;
        this.region = region;
        this.world = world;
        this.owner = owner;
        this.influence.put(influenceowner, influence);
        this.influenceowner = influenceowner;
        
        captureTimer = new CaptureTimer(this);
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
        return captureTimer;
    }

    public void setTimer(CaptureTimer captureTimer) {
        this.captureTimer = captureTimer;
    }

    public Faction getInfluenceOwner() {
        return influenceowner;
    }

    public void setInfluenceOwner(Faction influenceowner) {
        this.influenceowner = influenceowner;
    }

    public int getBaseInfluence() {
        return new Config().getMainConfig().getInt("regions." + this.region.getId() + ".influence");
    }
    
    public Boolean isCapturable(Faction faction)
    {
        List<CapturableRegion> regions = adjacentregions;
        regions.add(this);
        
        for(CapturableRegion region : regions)
        {
            if(region.getOwner() == faction)
            {
                return true;
            }
        }
        return false;
    }
    
    public Boolean isBeingCaptured()
    {
        return beingcaptured;
    }

    public void setIsBeingCaptured(Boolean capturing) {
        beingcaptured = capturing;
    }

    public Faction getMajorityController() {
        return majorityController;
    }

    public void setMajorityController(Faction majorityController) {
        this.majorityController = majorityController;
    }

    public float getInfluenceRate() {
        return influenceRate;
    }

    public void setInfluenceRate(float influenceRate) {
        this.influenceRate = influenceRate;
    }
}
