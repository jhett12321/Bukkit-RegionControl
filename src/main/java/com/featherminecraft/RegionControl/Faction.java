package com.featherminecraft.RegionControl;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.World;

import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;

public class Faction {

    private String name;
    private Map<World,CapturableRegion> spawnRegion = new HashMap<World,CapturableRegion>();
    private String permissionGroup;
    private Color factionColor;
    private String factionIconUrl;
    
    public Faction(String name, String permissiongroup, Color factioncolor)
    {
        this.name = name;
        this.permissionGroup = permissiongroup;
        this.factionColor = factioncolor;
    }
    
    public String getName() {
        return name;
    }
    
    public CapturableRegion getFactionSpawnRegion(World world) 
    {
        return spawnRegion.get(world);
    }
    
    public void addFactionSpawnRegion(CapturableRegion spawnRegion) {
       this.spawnRegion.put(spawnRegion.getWorld(), spawnRegion);
    }
    
    public String getPermissionGroup() {
        return permissionGroup;
    }
    
    public Color getFactionColor()
    {
        return factionColor;
    }

    public String getFactionIconUrl() {
        return factionIconUrl;
    }

    public void setFactionIconUrl(String factionIconUrl) {
        this.factionIconUrl = factionIconUrl;
    }
}
