package com.featherminecraft.regioncontrol;

import org.bukkit.Location;

public class SpawnPoint {

    private Location location;
    private CapturableRegion region;
    
    public SpawnPoint(CapturableRegion region, Location location)
    {
        this.location = location;
        this.region = region;
    }
    
    public Location getLocation() {
        return location;
    }
    
    public CapturableRegion getRegion() {
        return region;
    }

}
