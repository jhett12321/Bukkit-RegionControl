package com.featherminecraft.regioncontrol;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import com.featherminecraft.regioncontrol.events.RegionCaptureEvent;
import com.featherminecraft.regioncontrol.events.RegionDefendEvent;

public class CaptureTimer extends BukkitRunnable {
    
    private Integer baseinfluenceamount;
    private Map<Faction,Integer> influence;
    private Integer influencerate;
    private List<ControlPoint> controlpoints;
    private Map<Faction,Integer> ownedcontrolpoints;
    private Map<Faction,Float> percentageowned;
    private CapturableRegion region;
    private Faction majoritycontroller;

    public CaptureTimer(CapturableRegion region, int baseinfluenceamount)
    {
        this.controlpoints = region.getControlpoints();
        this.region = region;
        this.baseinfluenceamount = baseinfluenceamount; //Capture Time, in seconds from neutral to ownership. Maybe divide this by 2 to get capture time from owner to owner?
    }
    
    //Timeline (With base influence of 300)
    //(New Owner)300--------------------0--------------------300(Current Owner)
    //                   New Owner Map      Current Owner Map
    
    @Override
    public void run()
    {
        int currentinfluence = influence.get(region.getOwner());
        
        for(ControlPoint controlpoint : this.controlpoints)
        {
            if(!controlpoint.isCapturing())
            ownedcontrolpoints.put(controlpoint.getOwner(), ownedcontrolpoints.get(controlpoint.getOwner()) + 1);
        }
        
        int totalcontrolpoints = 0;
        for(Entry<Faction,Integer> total : this.ownedcontrolpoints.entrySet())
        {
            totalcontrolpoints = totalcontrolpoints + 1;
        }
        
        for(Entry<Faction,Integer> faction : this.ownedcontrolpoints.entrySet())
        {
            percentageowned.put(faction.getKey(), (float) (ownedcontrolpoints.get(faction) / totalcontrolpoints));
        }
        
        for(Entry<Faction,Integer> influence : this.influence.entrySet())
        {
            if(influence.getValue() != 0)
            {
                this.majoritycontroller = influence.getKey();
            }
        }
        
        for(Entry<Faction,Float> percentage : this.percentageowned.entrySet())
        {
            //Attackers have Majority Control
            if(majoritycontroller != region.getOwner())
            {
                //If Current Owner still has influence, take influence away from the current owner.
                if(percentage.getValue() >= 0.75 && influence.get(region.getOwner()) != 0)
                {
                    this.influence.put(region.getOwner(), this.influence.get(region.getOwner()) - 2);
                    this.influencerate = 2;
                    continue;
                }
                
                if(percentage.getValue() > 0.5 && percentage.getValue() < 0.75 && influence.get(region.getOwner()) != 0)
                {
                    this.influence.put(region.getOwner(), this.influence.get(region.getOwner()) - 1);
                    this.influencerate = 1;
                    continue;
                }
                
                //If Current Owner has no influence, add influence to the current capturers.
                if (percentage.getValue() >= 0.75 && influence.get(region.getOwner()) == 0 && influence.get(percentage.getKey()) != baseinfluenceamount)
                {
                    this.influence.put(percentage.getKey(), this.influence.get(percentage.getKey()) + 2);
                    this.influencerate = 2;
                    continue;
                }
                
                if (percentage.getValue() > 0.5 && percentage.getValue() < 0.75 && influence.get(region.getOwner()) == 0 && influence.get(percentage.getKey()) != baseinfluenceamount)
                {
                    this.influence.put(percentage.getKey(), this.influence.get(percentage.getKey()) + 2);
                    this.influencerate = 1;
                    continue;
                }
            }
            //Defenders have Majority Control
            else if(majoritycontroller == region.getOwner())
            {
                //If The Defenders already have influence, just add influence to the current owner.
                if(percentage.getValue() >= 0.75 && influence.get(percentage.getKey()) != baseinfluenceamount && influence.get(majoritycontroller) == 0)
                {
                    this.influence.put(percentage.getKey(), this.influence.get(percentage.getKey()) + 2);
                    this.influencerate = 2;
                    continue;
                }
                
                if(percentage.getValue() > 0.5 && percentage.getValue() < 0.75 && influence.get(percentage.getKey()) != baseinfluenceamount && influence.get(majoritycontroller) == 0)
                {
                    this.influence.put(percentage.getKey(), this.influence.get(percentage.getKey()) + 2);
                    this.influencerate = 1;
                    continue;
                }
                
                //If the defenders don't have majority control, take away their current influence
                if(percentage.getValue() >= 0.75 && influence.get(majoritycontroller) != 0)
                {
                    this.influence.put(majoritycontroller, this.influence.get(majoritycontroller) - 2);
                    this.influencerate = 2;
                    continue;
                }
                
                else if(percentage.getValue() > 0.5 && percentage.getValue() < 0.75 && influence.get(majoritycontroller) != 0)
                {
                    this.influence.put(majoritycontroller, this.influence.get(majoritycontroller) - 2);
                    this.influencerate = 1;
                    continue;
                }
            }
        }
        
        if(influence.get(region.getOwner()) != currentinfluence && influence.get(region.getOwner()) == baseinfluenceamount)
        {
            RegionDefendEvent regiondefendevent = new RegionDefendEvent(region, null);
            Bukkit.getServer().getPluginManager().callEvent(regiondefendevent);
        }
        
        if(influence.get(majoritycontroller) != currentinfluence && influence.get(majoritycontroller) == baseinfluenceamount)
        {
            RegionCaptureEvent regioncaptureevent = new RegionCaptureEvent(region,null, null, null);
            Bukkit.getServer().getPluginManager().callEvent(regioncaptureevent);
        }
    }
    
    public Integer getInfluenceRate()
    {
        return influencerate;
    }
}
