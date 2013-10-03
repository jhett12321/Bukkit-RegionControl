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
    
    public void setFactionSpawnRegion(CapturableRegion spawnRegion) {
       this.spawnRegion.put(spawnRegion.getWorld(), spawnRegion);
    }
    
    public String getPermissionGroup() {
        return permissionGroup;
    }
    
    public Color getFactionColor()
    {
        return factionColor;
    }
}
