package com.featherminecraft.RegionControl;

import java.awt.Color;

import org.bukkit.World;

import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;


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
    
    public CapturableRegion getFactionSpawnRegion(World world) 
    {
        //TODO implement world checking.
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