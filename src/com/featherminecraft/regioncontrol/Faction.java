package com.featherminecraft.regioncontrol;

import java.awt.Color;

import org.bukkit.Location;

//W.I.P.
public class Faction {

    private String name;
    private SpawnPoint spawnpoint;
    private String permissiongroup;
    private Color factioncolor;
    
    public Faction(String name, String permissiongroup, Color factioncolor, Location spawnlocation)
    {
        this.name = name;
        this.permissiongroup = permissiongroup;
        this.factioncolor = factioncolor;
    }
    
    public String getName() {
        return name;
    }
    
    public SpawnPoint getFactionSpawnPoint() {
        return spawnpoint;
    }
    
    public String getPermissionGroup() {
        return permissiongroup;
    }
    
    public Color getFactionColor()
    {
        return factioncolor;
    }
}
