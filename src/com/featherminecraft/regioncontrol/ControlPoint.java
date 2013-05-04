package com.featherminecraft.regioncontrol;

import org.bukkit.Location;
import org.bukkit.World;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class ControlPoint {

    private Location coordinates;
    private Integer radius; //TODO
    private Boolean capturing;
    private String controlpointname;
    
    public ControlPoint(String controlpointname, ProtectedRegion region, World world, double x, double y, double z)
    {
        coordinates = new Location(world, x, y, z);
        world.getBlockAt(coordinates).setTypeId(76, false);
        for(;;)
        {
            if(capturing && world.getBlockAt(coordinates).getTypeId() == 76)
            {
                world.getBlockAt(coordinates).setTypeId(75, false);
                continue;
            }
            else if(!capturing && world.getBlockAt(coordinates).getTypeId() == 75)
            {
                world.getBlockAt(coordinates).setTypeId(76, false);
                continue;
            }
        }
    }
    
    public String getName()
    {
        return controlpointname;
    }
    
    public Location getLocation()
    {
        return coordinates;
    }

    public void setStatus(boolean isbeingcaptured)
    {
        if(isbeingcaptured)
            capturing = true;
        else
            capturing = false;
    }
    
    
    
}
