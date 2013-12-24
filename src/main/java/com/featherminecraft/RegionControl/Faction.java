package com.featherminecraft.RegionControl;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Color;
import org.bukkit.World;

import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;

public class Faction
{
    
    private String displayName;
    private Map<World, CapturableRegion> spawnRegion = new HashMap<World, CapturableRegion>();
    private String permissionGroup;
    private Color factionColor;
    private String factionIconUrl;
    private String id;
    
    public Faction(String id, String displayName, String permissionGroup, Color factionColor)
    {
        this.id = id;
        this.displayName = displayName;
        this.permissionGroup = permissionGroup;
        this.factionColor = factionColor;
    }
    
    public void addFactionSpawnRegion(CapturableRegion spawnRegion)
    {
        this.spawnRegion.put(spawnRegion.getWorld(), spawnRegion);
    }
    
    public String getDisplayName()
    {
        return displayName;
    }
    
    public Color getFactionColor()
    {
        return factionColor;
    }
    
    public String getFactionIconUrl()
    {
        return factionIconUrl;
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
    
    public void setFactionIconUrl(String factionIconUrl)
    {
        this.factionIconUrl = factionIconUrl;
    }
}
