package com.featherminecraft.RegionControl;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.World;

import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;

public class Faction
{
    private String displayName;
    private Map<World, CapturableRegion> spawnRegion = new HashMap<World, CapturableRegion>();
    private String permissionGroup;
    private String id;
    private FactionColor factionColor;
    
    Faction(String id, String displayName, String permissionGroup, String color)
    {
        this.id = id;
        this.displayName = displayName;
        this.permissionGroup = permissionGroup;
        
        factionColor = new FactionColor(this, color);
    }
    
    protected void addFactionSpawnRegion(CapturableRegion spawnRegion)
    {
        this.spawnRegion.put(spawnRegion.getWorld(), spawnRegion);
    }
    
    public String getDisplayName()
    {
        return displayName;
    }
    
    public FactionColor getFactionColor()
    {
        return factionColor;
    }
    
    public CapturableRegion getFactionSpawnRegion(World world)
    {
        return spawnRegion.get(world);
    }
    
    public String getId()
    {
        return id;
    }
    
    public String getPermissionGroup()
    {
        return permissionGroup;
    }
}