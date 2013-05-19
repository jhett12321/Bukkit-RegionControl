package com.featherminecraft.regioncontrol;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import com.featherminecraft.regioncontrol.events.RegionCaptureEvent;
import com.featherminecraft.regioncontrol.events.RegionDefendEvent;

public class CaptureTimer extends BukkitRunnable {
    
    //Constructor Vars:
    private List<ControlPoint> controlpoints;
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
        this.baseinfluenceamount = baseinfluenceamount; //Capture Time, from neutral to ownership, in seconds with 50 - 75% ownership.. Maybe times this by 2 to get capture time from owner to owner?
        
        Map<String, Faction> factions = ServerLogic.registeredfactions;
        for(Entry<String, Faction> faction : factions.entrySet())
        {
            influence.put(faction.getValue(), 0);
        }
        
        if(region.getInfluence() != null)
        {
            this.influence.put(region.getInfluenceOwner(), region.getInfluence());
        }
    }
    
    @Override
    public void run()
    {
        Integer currentinfluencerate = influencerate;
        
        for(ControlPoint controlpoint : this.controlpoints)
        {
            if(!controlpoint.isCapturing())
            ownedcontrolpoints.put(controlpoint.getOwner(), ownedcontrolpoints.get(controlpoint.getOwner()) + 1);
        }
        
        int totalcontrolpoints = this.ownedcontrolpoints.values().size();
        
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
            if(influence.getValue() > 0)
            {
                factionwithinfluence = influence.getKey();
                break;
            }
        }
        
        if(this.majoritycontroller != null)
        {
            for(Entry<Faction,Float> percentage : this.percentageowned.entrySet())
            {
               //Take influence away from the current Faction with Influence.
                if(factionwithinfluence != percentage.getKey())
                {
                    int remaininginfluence = this.influence.get(factionwithinfluence);
                    
                    Float percentageagainst = (float) 0;
                    for(Entry<Faction,Float> factionpercentage : this.percentageowned.entrySet())
                    {
                        if(factionpercentage.getKey() != factionwithinfluence)
                        {
                            percentageagainst = percentageagainst + factionpercentage.getValue();
                        }
                    }
                    
                    if(percentageagainst == 1)
                    {
                        //Sets influence to 0 even if the influence somehow gets into the negatives
                        if(remaininginfluence < 3)
                        {
                            this.influence.put(factionwithinfluence, 0);
                            this.influencerate = 3;
                            if(influencerate != currentinfluencerate);
                            {
                                region.setExpectedCaptureTime(System.currentTimeMillis() + (baseinfluenceamount / 3 * 1000));
                            }
                        }
                        else
                        {
                            this.influence.put(factionwithinfluence, this.influence.get(factionwithinfluence) - 3);
                            this.influencerate = 3;
                            if(influencerate != currentinfluencerate);
                            {
                                region.setExpectedCaptureTime(System.currentTimeMillis() + (((this.influence.get(factionwithinfluence) + baseinfluenceamount) / 3) * 1000));
                            }
                        }
                    }
                    
                    else if(percentageagainst >= 0.75 && percentage.getValue() < 1)
                    {
                        if(remaininginfluence < 2)
                        {
                            this.influence.put(factionwithinfluence, 0);
                            this.influencerate = 2;
                            if(influencerate != currentinfluencerate);
                            {
                                region.setExpectedCaptureTime(System.currentTimeMillis() + (baseinfluenceamount / 2 * 1000));
                            }
                        }
                        else
                        {
                            this.influence.put(factionwithinfluence, this.influence.get(factionwithinfluence) - 2);
                            this.influencerate = 2;
                            if(influencerate != currentinfluencerate);
                            {
                                region.setExpectedCaptureTime(System.currentTimeMillis() + (((this.influence.get(factionwithinfluence) + baseinfluenceamount) / 2) * 1000));
                            }
                        }
                    }
                    
                    else if(percentageagainst > 0.5 && percentage.getValue() < 0.75 && remaininginfluence >= 1)
                    {
                        this.influence.put(factionwithinfluence, this.influence.get(factionwithinfluence) - 1);
                        this.influencerate = 1;
                        if(influencerate != currentinfluencerate);
                        {
                            region.setExpectedCaptureTime(System.currentTimeMillis() + ((this.influence.get(factionwithinfluence) + baseinfluenceamount) * 1000));
                        }
                    }
                    continue;
                }

                //Add influence to the current capturers, if the current captures are the one with influence.
                else if(factionwithinfluence == percentage.getKey())
                {
                    int currentinfluence = influence.get(factionwithinfluence);
                    
                    int remaininginfluence = baseinfluenceamount - this.influence.get(factionwithinfluence);
                    
                    if (percentage.getValue() == 1)
                    {
                        if(remaininginfluence < 3)
                        {
                            this.influence.put(factionwithinfluence, baseinfluenceamount);
                            this.influencerate = 3;
                            if(influencerate != currentinfluencerate);
                            {
                                region.setExpectedCaptureTime(System.currentTimeMillis());
                            }
                        }
                        else
                        {
                            this.influence.put(factionwithinfluence, this.influence.get(percentage.getKey()) + 3);
                            this.influencerate = 3;
                            if(influencerate != currentinfluencerate);
                            {
                                region.setExpectedCaptureTime(System.currentTimeMillis() + (((baseinfluenceamount - this.influence.get(factionwithinfluence)) / 3) * 1000));
                            }
                        }
                    }
                    
                    else if (percentage.getValue() >= 0.75 && percentage.getValue() < 1)
                    {
                        if(remaininginfluence < 2)
                        {
                            this.influence.put(factionwithinfluence, baseinfluenceamount);
                            this.influencerate = 2;
                            if(influencerate != currentinfluencerate);
                            {
                                region.setExpectedCaptureTime(System.currentTimeMillis());
                            }
                        }
                        else
                        {
                            this.influence.put(factionwithinfluence, this.influence.get(percentage.getKey()) + 2);
                            this.influencerate = 2;
                            if(influencerate != currentinfluencerate);
                            {
                                region.setExpectedCaptureTime(System.currentTimeMillis() + (((baseinfluenceamount - this.influence.get(factionwithinfluence)) / 2) * 1000));
                            }
                        }
                    }
                    
                    else if (percentage.getValue() > 0.5 && percentage.getValue() < 0.75 && remaininginfluence >= 1)
                    {
                        this.influence.put(factionwithinfluence, this.influence.get(percentage.getKey()) + 1);
                        this.influencerate = 1;
                        if(influencerate != currentinfluencerate);
                        {
                            region.setExpectedCaptureTime(System.currentTimeMillis() + ((baseinfluenceamount - this.influence.get(factionwithinfluence)) * 1000));
                        }
                    }
                    
                    if(percentage.getValue() > 0.5 && influence.get(factionwithinfluence) >= baseinfluenceamount && currentinfluence != influence.get(factionwithinfluence))
                    {
                        if(factionwithinfluence != region.getOwner())
                        {
                            Bukkit.getServer().getPluginManager().callEvent( new RegionCaptureEvent(region, region.getOwner(), factionwithinfluence) );
                        }
                        
                        else if(factionwithinfluence == region.getOwner())
                        {
                            Bukkit.getServer().getPluginManager().callEvent( new RegionDefendEvent(region, region.getOwner()) );
                        }
                    }
                    continue;
                }
                
               //If No-one has influence, add influence to the current capturers.
                else if(factionwithinfluence == null)
                {
                    if (percentage.getValue() == 1)
                    {
                        this.influence.put(percentage.getKey(), this.influence.get(percentage.getKey()) + 3);
                        this.influencerate = 3;
                        if(influencerate != currentinfluencerate);
                        {
                            region.setExpectedCaptureTime(System.currentTimeMillis() + (((baseinfluenceamount - this.influence.get(factionwithinfluence)) / 3) * 1000));
                        }
                    }
                    
                    else if (percentage.getValue() >= 0.75 && percentage.getValue() < 1)
                    {
                        this.influence.put(percentage.getKey(), this.influence.get(percentage.getKey()) + 2);
                        this.influencerate = 2;
                        if(influencerate != currentinfluencerate);
                        {
                            region.setExpectedCaptureTime(System.currentTimeMillis() + (((baseinfluenceamount - this.influence.get(factionwithinfluence)) / 2) * 1000));
                        }
                    }
                    
                    else if (percentage.getValue() > 0.5 && percentage.getValue() < 0.75)
                    {
                        this.influence.put(percentage.getKey(), this.influence.get(percentage.getKey()) + 2);
                        this.influencerate = 1;
                        if(influencerate != currentinfluencerate);
                        {
                            region.setExpectedCaptureTime(System.currentTimeMillis() + ((baseinfluenceamount - this.influence.get(factionwithinfluence)) * 1000));
                        }
                    }
                    continue;
                }
            }
        }
    }
    
    public Integer getInfluenceRate()
    {
        return influencerate;
    }
}
