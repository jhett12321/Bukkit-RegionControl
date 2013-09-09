package com.featherminecraft.RegionControl.capturableregion;

import org.bukkit.Location;


public class SpawnPoint {

    private Location location;
    
    public SpawnPoint(Location location)
    {
        this.location = location;
    }
    
    public Location getLocation() {
        return location;
    }

}
