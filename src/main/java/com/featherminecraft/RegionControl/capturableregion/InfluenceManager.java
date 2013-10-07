package com.featherminecraft.RegionControl.capturableregion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;

import com.featherminecraft.RegionControl.Faction;
import com.featherminecraft.RegionControl.ServerLogic;
import com.featherminecraft.RegionControl.events.CaptureStatusChangeEvent;
import com.featherminecraft.RegionControl.events.InfluenceRateChangeEvent;
import com.featherminecraft.RegionControl.events.RegionCaptureEvent;
import com.featherminecraft.RegionControl.events.RegionDefendEvent;

public class InfluenceManager {

    //Region Variable
    private CapturableRegion region;
    
    //Private Vars
    private HashMap<Faction, Float> percentageOwned;

    public InfluenceManager(CapturableRegion cregion)
    {
        this.region = cregion;
        region.setInfluenceManager(this);
    }
    
    public void Runnable()
    {
        Faction majorityController = CalculateMajorityController();
        Faction influenceOwner = CalculateInfluenceOwner();
        Float influenceRate = CalculateInfluenceRate();
        Map<Faction, Float> influenceMap = region.getInfluenceMap();
        
        if(influenceOwner == null)
        {
            if(majorityController != null && influenceRate != null && influenceRate != 0)
            {
                influenceMap.put(majorityController, influenceRate);
            }
        }
        
        else if(influenceOwner != majorityController)
        {
            if(majorityController != null && influenceRate != null && influenceRate != 0)
            {
                if(influenceMap.get(influenceOwner) - influenceRate <= 0)
                {
                    influenceMap.put(influenceOwner, 0F);
                }
                
                else
                {
                    influenceMap.put(influenceOwner, influenceMap.get(influenceOwner) - influenceRate);
                }
            }
        }
        
        else if(influenceOwner == majorityController)
        {
            if(majorityController != null && influenceRate != null && influenceRate != 0)
            {
                if(influenceMap.get(influenceOwner) + influenceRate >= region.getBaseInfluence())
                {
                    influenceMap.put(majorityController, region.getBaseInfluence());
                    if(region.getOwner() == influenceOwner)
                    {
                        Bukkit.getServer().getPluginManager().callEvent(new RegionDefendEvent(region, influenceOwner));
                    }
                    
                    else if(region.getOwner() != influenceOwner)
                    {
                        Bukkit.getServer().getPluginManager().callEvent(new RegionCaptureEvent(region, region.getOwner(), influenceOwner));
                    }
                }
                
                else
                {
                    influenceMap.put(majorityController, influenceMap.get(influenceOwner) + influenceRate);
                }
            }
        }
    }
    
    public Faction CalculateMajorityController()
    {
        /*
         * Majority Controller Calculations
         */
        //TODO Control Points need to be rechecked.
        List<ControlPoint> controlPoints = region.getControlPoints();
        Map<Faction,Float> ownedControlPoints = new HashMap<Faction,Float>();
        for(Entry<String, Faction> faction : ServerLogic.factions.entrySet())
        {
            if(faction.getValue() != null)
            {
                ownedControlPoints.put(faction.getValue(), 0F);
            }
        }

        for(ControlPoint controlPoint : controlPoints)
        {
            if(controlPoint.getOwner() != null && !controlPoint.isCapturing())
            {
                ownedControlPoints.put(controlPoint.getOwner(), ownedControlPoints.get(controlPoint.getOwner()) + 1);
            }
        }
        
        Float totalOwnedControlPoints = ((Integer) ownedControlPoints.size()).floatValue();
        percentageOwned = new HashMap<Faction,Float>();
        Faction majorityController = null;
        int majorityAmount = 0;
        for(Entry<Faction, Float> faction : ownedControlPoints.entrySet())
        {
            percentageOwned.put(faction.getKey(), faction.getValue() / totalOwnedControlPoints);
            if(faction.getValue() > majorityAmount)
            {
                majorityController = faction.getKey();
            }
            
            else if(faction.getValue() == majorityAmount)
            {
                majorityController = null;
            }
        }
        
        region.setMajorityController(majorityController); //TODO Migrate to event
        return majorityController;
    }
    
    public Faction CalculateInfluenceOwner()
    {
        /*
         * Influence Owner Calculations
         */
        
        Map<Faction, Float> influenceMap = region.getInfluenceMap();
        Faction influenceOwner = null;
        for(Entry<Faction, Float> influence : influenceMap.entrySet())
        {
            if(influence.getValue() >= 0)
            {
                influenceOwner = influence.getKey();
                break;
            }
        }
        
        if(region.getInfluenceOwner() != influenceOwner && influenceOwner != null)
        {
            Bukkit.getServer().getPluginManager().callEvent(new InfluenceRateChangeEvent(region, region.getInfluenceRate(), region.getInfluenceRate()));
        }
        
        if(region.getInfluenceOwner() != influenceOwner)
        {
            region.setInfluenceOwner(influenceOwner); //Migrate to Event
        }
        
        return influenceOwner;
    }
    
    public Float CalculateInfluenceRate()
    {
        /*
         * Influence Rate Calculations, Take away influence for this loop.
         * Rate 1: 1% Diff. +
         * Rate 2: 33% Diff. +
         * Rate 3: 66% Diff. +
         * Rate 4: 100% Diff.
         */
        
        Float percentageAgainst = 0F;
        
        for(Entry<Faction, Float> faction : percentageOwned.entrySet())
        {
            if(faction.getKey() != region.getMajorityController())
            {
                percentageAgainst += faction.getValue();
            }
        }
        
        Float influenceRate = 0F;
        if(region.getMajorityController() != null)
        {
            Float percentageDifference = percentageOwned.get(region.getMajorityController()) - percentageAgainst;
            if(percentageDifference == 1F)
            {
                influenceRate = 4F;
            }
            
            else if(percentageDifference > 0.66)
            {
                influenceRate = 3F;
            }
            
            else if(percentageDifference > 0.33)
            {
                influenceRate = 2F;
            }
            
            else if(percentageDifference > 0.01F)
            {
                influenceRate = 1F;
            }
            
            if(influenceRate > 0F && !region.isBeingCaptured())
            {
                Bukkit.getServer().getPluginManager().callEvent(new CaptureStatusChangeEvent(region, true));
            }
            
            if(influenceRate == 0F && region.isBeingCaptured())
            {
                Bukkit.getServer().getPluginManager().callEvent(new CaptureStatusChangeEvent(region, false));
            }
            
            if(region.getInfluenceRate() != influenceRate)
            {
                Bukkit.getServer().getPluginManager().callEvent(new InfluenceRateChangeEvent(region, region.getInfluenceRate(), influenceRate));
            }
        }
        
        region.setInfluenceRate(influenceRate); //TODO Migrate to listener.
        return influenceRate;
    }
}
