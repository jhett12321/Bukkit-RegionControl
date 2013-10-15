package com.featherminecraft.RegionControl.capturableregion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.bukkit.Bukkit;

import com.featherminecraft.RegionControl.Faction;
import com.featherminecraft.RegionControl.RegionControl;
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
        
        if(majorityController != null & influenceOwner != null && influenceRate != null)
        {
            RegionControl.plugin.getLogger().log(Level.INFO, "DEBUG: Calculated InfluenceOwner is: " + influenceOwner.getName());
            RegionControl.plugin.getLogger().log(Level.INFO, "DEBUG: Calculated Majority Controller is: " + majorityController.getName());
            RegionControl.plugin.getLogger().log(Level.INFO, "DEBUG: Calculated InfluenceRate is: " + influenceRate.toString());
        }
        
        if(influenceOwner == null)
        {
            if(majorityController != null && influenceRate != null && influenceRate != 0F)
            {
                influenceMap.put(majorityController, influenceRate);
            }
        }
        
        else if(influenceOwner != majorityController)
        {
            if(majorityController != null && influenceRate != null && influenceRate != 0F)
            {
                if(influenceMap.get(influenceOwner) - influenceRate <= 0F)
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
            if(majorityController != null && influenceRate != null && influenceRate != 0F)
            {
                if(influenceMap.get(influenceOwner) + influenceRate >= region.getBaseInfluence())
                {
                    influenceMap.put(influenceOwner, region.getBaseInfluence());
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
                    influenceMap.put(influenceOwner, influenceMap.get(influenceOwner) + influenceRate);
                }
            }
        }
        
        region.setInfluenceMap(influenceMap);
        region.setInfluenceOwner(influenceOwner);
        region.setMajorityController(majorityController);
        region.setInfluenceRate(influenceRate);
    }
    
    public Faction CalculateMajorityController()
    {
        /*
         * Majority Controller Calculations
         */
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
                ownedControlPoints.put(controlPoint.getOwner(), ownedControlPoints.get(controlPoint.getOwner()) + 1F);
            }
        }
        
        Float totalOwnedControlPoints = ((Integer) ownedControlPoints.size()).floatValue();
        percentageOwned = new HashMap<Faction,Float>();
        for(Entry<String, Faction> faction : ServerLogic.factions.entrySet())
        {
            if(faction.getValue() != null)
            {
                percentageOwned.put(faction.getValue(), 0F);
            }
        }
        
        Faction majorityController = null;
        Float majorityAmount = 0F;
        for(Entry<Faction, Float> faction : ownedControlPoints.entrySet())
        {
            percentageOwned.put(faction.getKey(), faction.getValue() / totalOwnedControlPoints);
            if(faction.getValue() > majorityAmount)
            {
                majorityController = faction.getKey();
                majorityAmount = faction.getValue();
            }
            
            else if(faction.getValue() == majorityAmount)
            {
                majorityController = null;
            }
        }
        
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
            if(influence.getValue() > 0F)
            {
                influenceOwner = influence.getKey();
                break;
            }
        }
        
//        if(region.getInfluenceOwner() != influenceOwner && influenceOwner != null)
//        {
//            Bukkit.getServer().getPluginManager().callEvent(new InfluenceRateChangeEvent(region, region.getInfluenceRate(), region.getInfluenceRate()));
//        }
//        
//        if(region.getInfluenceOwner() != influenceOwner)
//        {
//            region.setInfluenceOwner(influenceOwner); //Migrate to Event
//        }
        
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
            if(percentageDifference >= 1F)
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
        return influenceRate;
    }
}
