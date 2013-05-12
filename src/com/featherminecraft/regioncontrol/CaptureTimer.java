package com.featherminecraft.regioncontrol;

import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import com.featherminecraft.regioncontrol.events.RegionCaptureEvent;
import com.featherminecraft.regioncontrol.events.RegionDefendEvent;

public class CaptureTimer extends BukkitRunnable {
    
    //Constructor Vars:
    private Map<String, ControlPoint> controlpoints;
    private CapturableRegion region;
    private Integer baseinfluenceamount;

    //Generated Vars:
    private Map<Faction,Integer> influence;
    private Integer influencerate;
    private Map<Faction,Integer> ownedcontrolpoints;
    private Map<Faction,Float> percentageowned;
    private Faction majoritycontroller;

    public CaptureTimer(CapturableRegion region, int baseinfluenceamount)
    {
        this.controlpoints = region.getControlpoints();
        this.region = region;
        this.baseinfluenceamount = baseinfluenceamount; //Capture Time, in seconds from neutral to ownership. Maybe divide this by 2 to get capture time from owner to owner?
    }
    
    @Override
    public void run()
    {
        int currentinfluence = influence.get(region.getOwner());
        
        if(currentinfluence == 0)
        {
            for(Entry<Faction,Integer> attackerinfluence : influence.entrySet())
            {
                if(attackerinfluence.getValue() > 0)
                {
                    currentinfluence = attackerinfluence.getValue();
                }
            }
        }
        
        for(Entry<String,ControlPoint> controlpoint : this.controlpoints.entrySet())
        {
            if(!controlpoint.getValue().isCapturing())
            ownedcontrolpoints.put(controlpoint.getValue().getOwner(), ownedcontrolpoints.get(controlpoint.getValue().getOwner()) + 1);
        }
        
        int totalcontrolpoints = 0;
        for(@SuppressWarnings("unused") Entry<Faction,Integer> total : this.ownedcontrolpoints.entrySet())
        {
            totalcontrolpoints = totalcontrolpoints + 1;
        }
        
        for(Entry<Faction,Integer> faction : this.ownedcontrolpoints.entrySet())
        {
            percentageowned.put(faction.getKey(), (float) (ownedcontrolpoints.get(faction) / totalcontrolpoints));
        }
        
        Float mostcontrolpercentage = (float) 0;
        
        for(Entry<Faction,Float> percentage : this.percentageowned.entrySet())
        {
            if(percentage.getValue() != 0 && percentage.getValue() > mostcontrolpercentage)
            {
                this.majoritycontroller = percentage.getKey();
                mostcontrolpercentage = percentage.getValue();
            }
            
            else if(percentage.getValue() != 0 && percentage.getValue() == mostcontrolpercentage)
            {
                this.majoritycontroller = null;
            }
        }
        
        Faction factionwithinfluence = null;
        
        for(Entry<Faction,Integer> influence : this.influence.entrySet())
        {
            if(influence.getValue() != 0)
            {
                factionwithinfluence = influence.getKey();
                break;
            }
        }
        
        if(this.majoritycontroller != null)
        {
            for(Entry<Faction,Float> percentage : this.percentageowned.entrySet())
            {
                if(influence.get(percentage.getKey()) != baseinfluenceamount)
                {
                    Float percentageagainst = (float) 0;
                    for(Entry<Faction,Float> factionpercentage : this.percentageowned.entrySet())
                    {
                        if(factionpercentage.getKey() != factionwithinfluence)
                        {
                            percentageagainst = percentageagainst + factionpercentage.getValue();
                        }
                    }
                    //Take influence away from the current Faction with Influence.
                    if(percentageagainst == 1 && factionwithinfluence != percentage.getKey())
                    {
                        this.influence.put(factionwithinfluence, this.influence.get(factionwithinfluence) - 3);
                        this.influencerate = 3;
                        continue;
                    }
                    
                    if(percentageagainst >= 0.75 && percentage.getValue() < 1 && factionwithinfluence != percentage.getKey())
                    {
                        this.influence.put(factionwithinfluence, this.influence.get(factionwithinfluence) - 2);
                        this.influencerate = 2;
                        continue;
                    }
                    
                    if(percentageagainst > 0.5 && percentage.getValue() < 0.75 && factionwithinfluence != percentage.getKey())
                    {
                        this.influence.put(factionwithinfluence, this.influence.get(factionwithinfluence) - 1);
                        this.influencerate = 1;
                        continue;
                    }
                    
                    //Add influence to the current capturers.
                    if (percentage.getValue() == 1 && factionwithinfluence == percentage.getKey())
                    {
                        this.influence.put(factionwithinfluence, this.influence.get(percentage.getKey()) + 3);
                        this.influencerate = 3;
                        continue;
                    }
                    
                    if (percentage.getValue() >= 0.75 && percentage.getValue() < 1 && factionwithinfluence == percentage.getKey())
                    {
                        this.influence.put(factionwithinfluence, this.influence.get(percentage.getKey()) + 2);
                        this.influencerate = 2;
                        continue;
                    }
                    
                    if (percentage.getValue() > 0.5 && percentage.getValue() < 0.75 && factionwithinfluence == percentage.getKey())
                    {
                        this.influence.put(factionwithinfluence, this.influence.get(percentage.getKey()) + 2);
                        this.influencerate = 1;
                        continue;
                    }
                    
                    //If No-one has influence, add influence to the current capturers.
                    if (percentage.getValue() == 1 && factionwithinfluence == null)
                    {
                        this.influence.put(percentage.getKey(), this.influence.get(percentage.getKey()) + 3);
                        this.influencerate = 3;
                        continue;
                    }
                    
                    if (percentage.getValue() >= 0.75 && percentage.getValue() < 1 && factionwithinfluence == null)
                    {
                        this.influence.put(percentage.getKey(), this.influence.get(percentage.getKey()) + 2);
                        this.influencerate = 2;
                        continue;
                    }
                    
                    if (percentage.getValue() > 0.5 && percentage.getValue() < 0.75 && factionwithinfluence == null)
                    {
                        this.influence.put(percentage.getKey(), this.influence.get(percentage.getKey()) + 2);
                        this.influencerate = 1;
                        continue;
                    }
                }
            }
        }
        
        if(factionwithinfluence == region.getOwner() && influence.get(factionwithinfluence) != currentinfluence && influence.get(factionwithinfluence) == baseinfluenceamount)
        {
            RegionDefendEvent regiondefendevent = new RegionDefendEvent(region, null);
            Bukkit.getServer().getPluginManager().callEvent(regiondefendevent);
        }
        
        else if(factionwithinfluence != region.getOwner() && influence.get(factionwithinfluence) != currentinfluence && influence.get(factionwithinfluence) == baseinfluenceamount)
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
