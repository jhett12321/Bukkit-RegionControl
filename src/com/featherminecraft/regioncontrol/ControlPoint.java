package com.featherminecraft.regioncontrol;

import org.bukkit.Location;
import org.bukkit.World;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class ControlPoint {

    private String controlpointname;
    private Location location;
    private Integer radius; //TODO
    private Boolean capturing;
    private Faction owner;
    private ProtectedRegion region;
    private World world;
    
    public ControlPoint(String controlpointname, ProtectedRegion region, World world, Location location)
    {
        this.controlpointname = controlpointname;
        this.location = location;
        this.region = region;
        this.world = world;
        
        for(;;)
        {
            if (world.getBlockAt(this.location).getTypeId() == 0)
            {
                world.getBlockAt(this.location).setTypeId(76, false);
            }
            else if(this.capturing && world.getBlockAt(this.location).getTypeId() == 76)
            {
                world.getBlockAt(this.location).setTypeId(75, false);
            }
            else if(!capturing && world.getBlockAt(this.location).getTypeId() == 75)
            {
                world.getBlockAt(this.location).setTypeId(76, false);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                
            }
        }
    }

    public String getControlpointname() {
        return controlpointname;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public ProtectedRegion getRegion() {
        return region;
    }

    public World getWorld() {
        return world;
    }

    public Integer getRadius() {
        return radius;
    }

    public void setRadius(Integer radius) {
        this.radius = radius;
    }

    public Faction getOwner() {
        return owner;
    }

    public void setOwner(Faction owner) {
        this.owner = owner;
    }
}
