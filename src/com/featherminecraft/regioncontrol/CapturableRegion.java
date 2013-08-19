package com.featherminecraft.regioncontrol;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class CapturableRegion {

    //Runnable
    private BukkitTask runnable;
    
    //Influence
    private Float baseInfluence;
    private InfluenceManager influenceManager;
    private Map<Faction,Float> influenceMap = new HashMap<Faction,Float>();
    private Float influenceRate = 0F;

    private Faction influenceOwner;
    private Faction majorityController;
    
    //Capture Timer
    private CaptureTimer captureTimer;
    private int minutesToCapture;
    private int secondsToCapture;
    
    public CapturableRegion(String displayname, ProtectedRegion region, World world, Faction owner, Float influence, Faction influenceowner) {
        //TODO Ensure that the influence map is initialized for all factions.
        //TODO Define "Base Influence".
        
        this.influenceMap.put(influenceowner, influence);
        captureTimer = new CaptureTimer(this);
        influenceManager = new InfluenceManager(this);
        
        this.baseInfluence = baseInfluence;
        RegionRunnables();
    }
    
    public void RegionRunnables()
    {
        runnable = new BukkitRunnable() {
            
            @Override
            public void run() {
                influenceManager.Runnable();
                captureTimer.Runnable();
                
            }
        }.runTaskTimer(RegionControl.plugin, 20, 20);
    }
    
    
    /*
     * Influence Manager Begin
     */
    
    public InfluenceManager getInfluenceManager() {
        return influenceManager;
    }

    public void setInfluenceManager(InfluenceManager influenceManager) {
        this.influenceManager = influenceManager;
    }
    
    public Map<Faction, Float> getInfluenceMap() {
        return influenceMap;
    }

    public void setInfluenceMap(Map<Faction, Float> influenceMap) {
        this.influenceMap = influenceMap;
    }
    
    public Faction getInfluenceOwner() {
        return influenceOwner;
    }

    public void setInfluenceOwner(Faction influenceOwner) {
        this.influenceOwner = influenceOwner;
    }
    
    public Float getInfluenceRate() {
        return influenceRate;
    }

    public void setInfluenceRate(Float influenceRate) {
        this.influenceRate = influenceRate;
    }

    public Faction getMajorityController() {
        return majorityController;
    }

    public void setMajorityController(Faction majorityController) {
        this.majorityController = majorityController;
    }
    
    /*
     * Influence End
     */
    
    /*
     * Capture Timer Begin
     */
    
    public int getMinutesToCapture() {
        return minutesToCapture;
    }

    public void setMinutesToCapture(int minutesToCapture) {
        this.minutesToCapture = minutesToCapture;
    }

    public int getSecondsToCapture() {
        return secondsToCapture;
    }

    public void setSecondsToCapture(int secondsToCapture) {
        this.secondsToCapture = secondsToCapture;
    }
    
    public Float getBaseInfluence() {
        return baseInfluence;
    }

    public void setBaseInfluence(Float baseInfluence) {
        this.baseInfluence = baseInfluence;
    }

}
