package com.featherminecraft.regioncontrol;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.scheduler.BukkitRunnable;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class CaptureTimer extends BukkitRunnable {
    
    private Integer influence;
    private List<ControlPoint> controlpoints;
    private Map<Faction,Integer> ownedcontrolpoints;
    private Map<Faction,Float> percentageowned;

    public CaptureTimer(ProtectedRegion region)
    {
        for(Entry<String, ControlPoint> controlpoint : ServerLogic.controlpoints.entrySet())
        {
            if(controlpoint.getValue().getRegion() == region)
            {
                controlpoints.add(controlpoint.getValue());
            }
        }
    }
    
    @Override
    public void run()
    {
        for(ControlPoint controlpoint : this.controlpoints)
        {
            ownedcontrolpoints.put(controlpoint.getOwner(), ownedcontrolpoints.get(controlpoint.getOwner()) + 1);
        }
        
        int totalownedcontrolpoints = 0;
        for(Entry<Faction,Integer> total : this.ownedcontrolpoints.entrySet())
        {
            totalownedcontrolpoints = totalownedcontrolpoints + 1;
        }
        
        for(Entry<Faction,Integer> faction : this.ownedcontrolpoints.entrySet())
        {
            percentageowned.put(faction.getKey(), (float) (ownedcontrolpoints.get(faction) / totalownedcontrolpoints));
        }
        
        for(Entry<Faction,Float> percentage : this.percentageowned.entrySet())
        {
            if(percentage.getValue() > 0.75)
            {
                
            }
        }
    }
}
