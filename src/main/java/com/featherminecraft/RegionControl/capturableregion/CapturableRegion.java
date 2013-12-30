package com.featherminecraft.RegionControl.capturableregion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import com.featherminecraft.RegionControl.Faction;
import com.featherminecraft.RegionControl.RCPlayer;
import com.featherminecraft.RegionControl.RegionControl;
import com.featherminecraft.RegionControl.ServerLogic;

public class CapturableRegion
{
    // RegionControl Object
    private final CapturableRegion cregion = this;
    
    // Region Info
    private String displayName;
    private String regionId;
    private Faction owner;
    private List<CapturableRegion> adjacentRegions;
    private boolean isBeingCaptured;
    private boolean isSpawnRegion;
    
    // WorldGuard Region Info
    private ProtectedRegion region;
    private World world;
    
    // Region Objects
    private List<ControlPoint> controlPoints;
    private SpawnPoint spawnPoint;
    private List<RCPlayer> players = new ArrayList<RCPlayer>();
    
    // Runnable
    private BukkitTask runnable;
    
    // Influence
    private Float baseInfluence;
    private InfluenceManager influenceManager;
    private Map<Faction, Float> influenceMap = new HashMap<Faction, Float>();
    private Float influenceRate = 0F;
    
    private Faction influenceOwner;
    private Faction majorityController;
    
    // Capture Timer
    private CaptureTimer captureTimer;
    private int minutesToCapture;
    private int secondsToCapture;
    
    /**
     * The CapturableRegion Constructor
     * 
     * @param displayName
     *            A display name for the region, displayed on HUD Elements.
     * @param regionId
     *            The configuration value of this region.
     * @param owner
     *            A Faction object who currently owns this region
     * @param region
     *            A WorldGuard Protected Region that this CapturableRegion
     *            belongs to
     * @param world
     *            The World which this region resides.
     * @param controlpoints
     *            A list of ControlPoint objects that are within this region
     * @param spawnPoint
     *            The SpawnPoint object that belongs in this region
     * @param baseInfluence
     *            The minimum influence required to own this region
     * @param influence
     *            Influence of the current influence owner
     * @param influenceOwner
     *            A Faction Object representing the current owner of this
     *            region.
     * @param isSpawnRegion
     */
    public CapturableRegion(String displayName, String regionId, Faction owner, ProtectedRegion region, World world, List<ControlPoint> controlpoints, SpawnPoint spawnPoint, Float baseInfluence, Float influence, Faction influenceOwner, boolean isSpawnRegion)
    {
        
        this.displayName = displayName;
        this.regionId = regionId;
        this.owner = owner;
        this.region = region;
        this.world = world;
        controlPoints = controlpoints;
        this.spawnPoint = spawnPoint;
        this.baseInfluence = baseInfluence;
        this.isSpawnRegion = isSpawnRegion;
        
        if(!isSpawnRegion)
        {
            for(Entry<String, Faction> faction : ServerLogic.factions.entrySet())
            {
                if(faction.getValue() != null)
                {
                    influenceMap.put(faction.getValue(), 0F);
                }
            }
            
            influenceMap.put(influenceOwner, influence);
            captureTimer = new CaptureTimer(this);
            influenceManager = new InfluenceManager(this);
            
            this.baseInfluence = baseInfluence;
            
            for(ControlPoint controlPoint : controlPoints)
            {
                controlPoint.setRegion(this);
            }
            
            runnable = new BukkitRunnable()
            {
                
                @Override
                public void run()
                {
                    for(ControlPoint controlPoint : controlPoints)
                    {
                        controlPoint.Runnable();
                    }
                    influenceManager.Runnable();
                    captureTimer.Runnable();
                }
            }.runTaskTimer(RegionControl.plugin, 20, 20);
        }
        
        else
        {
            isBeingCaptured = false;
        }
    }
    
    public List<CapturableRegion> getAdjacentRegions()
    {
        return adjacentRegions;
    }
    
    public Float getBaseInfluence()
    {
        return baseInfluence;
    }
    
    /*
     * Region Info Begin
     */
    public CapturableRegion getCapturableRegion()
    {
        return cregion;
    }
    
    public List<ControlPoint> getControlPoints()
    {
        return controlPoints;
    }
    
    public String getDisplayName()
    {
        return displayName;
    }
    
    public InfluenceManager getInfluenceManager()
    {
        return influenceManager;
    }
    
    public Map<Faction, Float> getInfluenceMap()
    {
        return influenceMap;
    }
    
    public Faction getInfluenceOwner()
    {
        return influenceOwner;
    }
    
    public Float getInfluenceRate()
    {
        return influenceRate;
    }
    
    public Faction getMajorityController()
    {
        return majorityController;
    }
    
    public int getMinutesToCapture()
    {
        return minutesToCapture;
    }
    
    public Faction getOwner()
    {
        return owner;
    }
    
    public List<RCPlayer> getPlayers()
    {
        return players;
    }
    
    public ProtectedRegion getRegion()
    {
        return region;
    }
    
    public String getRegionId()
    {
        return regionId;
    }
    
    /*
     * Region Info End
     */
    
    /*
     * Region Objects Begin
     */
    
    // Runnable
    public BukkitTask getRunnable()
    {
        return runnable;
    }
    
    public int getSecondsToCapture()
    {
        return secondsToCapture;
    }
    
    public SpawnPoint getSpawnPoint()
    {
        return spawnPoint;
    }
    
    public World getWorld()
    {
        return world;
    }
    
    /*
     * Region Objects End
     */
    
    public boolean isBeingCaptured()
    {
        if(isSpawnRegion())
        {
            return false;
        }
        return isBeingCaptured;
    }
    
    /*
     * Influence Manager Begin
     */
    
    public boolean isSpawnRegion()
    {
        return isSpawnRegion;
    }
    
    public void setAdjacentRegions(List<CapturableRegion> adjacentRegions)
    {
        this.adjacentRegions = adjacentRegions;
    }
    
    public void setBaseInfluence(Float baseInfluence)
    {
        this.baseInfluence = baseInfluence;
    }
    
    public void setBeingCaptured(boolean isBeingCaptured)
    {
        this.isBeingCaptured = isBeingCaptured;
    }
    
    public void setControlPoints(List<ControlPoint> controlPoints)
    {
        this.controlPoints = controlPoints;
    }
    
    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
    }
    
    public void setInfluenceManager(InfluenceManager influenceManager)
    {
        this.influenceManager = influenceManager;
    }
    
    public void setInfluenceMap(Map<Faction, Float> influenceMap)
    {
        this.influenceMap = influenceMap;
    }
    
    public void setInfluenceOwner(Faction influenceOwner)
    {
        this.influenceOwner = influenceOwner;
    }
    
    public void setInfluenceRate(Float influenceRate)
    {
        this.influenceRate = influenceRate;
    }
    
    /*
     * Influence End
     */
    
    /*
     * Capture Timer Begin
     */
    
    public void setMajorityController(Faction majorityController)
    {
        this.majorityController = majorityController;
    }
    
    public void setMinutesToCapture(int minutesToCapture)
    {
        this.minutesToCapture = minutesToCapture;
    }
    
    public void setOwner(Faction owner)
    {
        this.owner = owner;
    }
    
    public void setPlayers(List<RCPlayer> players)
    {
        this.players = players;
    }
    
    public void setRegion(ProtectedRegion region)
    {
        this.region = region;
    }
    
    public void setRegionId(String regionId)
    {
        this.regionId = regionId;
    }
    
    public void setSecondsToCapture(int secondsToCapture)
    {
        this.secondsToCapture = secondsToCapture;
    }
    
    public void setSpawnPoint(SpawnPoint spawnPoint)
    {
        this.spawnPoint = spawnPoint;
    }
    
    public void setSpawnRegion(boolean isSpawnRegion)
    {
        this.isSpawnRegion = isSpawnRegion;
    }
    
    public void setWorld(World world)
    {
        this.world = world;
    }
}