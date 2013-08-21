package com.featherminecraft.regioncontrol;

import java.awt.Color;

import org.bukkit.Location;

import com.featherminecraft.regioncontrol.capturableregion.CapturableRegion;
import com.featherminecraft.regioncontrol.capturableregion.SpawnPoint;

public class Faction {

    private String name;
    private CapturableRegion spawnRegion;
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
    
    public CapturableRegion getFactionSpawnRegion() {
        return spawnRegion;
    }
    
    public void setFactionSpawnRegion(CapturableRegion spawnRegion) {
       this.spawnRegion = spawnRegion;
    }
    
    public String getPermissionGroup() {
        return permissionGroup;
    }
    
    public Color getFactionColor()
    {
        return factionColor;
    }
}
