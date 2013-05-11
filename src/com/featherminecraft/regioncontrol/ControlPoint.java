package com.featherminecraft.regioncontrol;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.featherminecraft.regioncontrol.utils.PlayerUtils;

public class ControlPoint extends BukkitRunnable {

    private String controlpointname;
    private Location location;
    private Integer radius; //TODO
    private Boolean capturing;
    private Faction owner;
    private CapturableRegion region;
    
    public ControlPoint(String controlpointname, CapturableRegion region, Location location)
    {
        this.controlpointname = controlpointname;
        this.location = location;
        this.region = region;
        if (region.getWorld().getBlockAt(this.location).getTypeId() == 0)
        {
            region.getWorld().getBlockAt(this.location).setTypeId(76, false);
        }
    }
    
    @Override
    public void run() {
        Player[] players = RegionControl.plugin.getServer().getOnlinePlayers();
        
        double radiusSquared = radius*radius;
     
        for (Player player : players) {
            if(player.getLocation().distanceSquared(location) <= radiusSquared)
            {
                if(new PlayerUtils().getPlayerFaction(player) != this.owner)
                {
                    this.capturing = true;
                }
            }
         
        }
        
        if(this.capturing && region.getWorld().getBlockAt(this.location).getTypeId() == 76)
        {
            region.getWorld().getBlockAt(this.location).setTypeId(75, false);
        }
        else if(!capturing && region.getWorld().getBlockAt(this.location).getTypeId() == 75)
        {
            region.getWorld().getBlockAt(this.location).setTypeId(76, false);
        }
    }

    public String getControlPointName() {
        return controlpointname;
    }

    public Location getLocation() {
        return location;
    }

    public CapturableRegion getCapturableRegion() {
        return region;
    }

    public Integer getRadius() {
        return radius;
    }

    public Faction getOwner() {
        return owner;
    }

    public void setOwner(Faction owner) {
        this.owner = owner;
    }
    
    public Boolean isCapturing()
    {
        return capturing;
    }
}