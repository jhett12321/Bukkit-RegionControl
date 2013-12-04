package com.featherminecraft.RegionControl.capturableregion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;

import com.featherminecraft.RegionControl.Faction;
import com.featherminecraft.RegionControl.ServerLogic;
import com.featherminecraft.RegionControl.events.RegionCaptureEvent;
import com.featherminecraft.RegionControl.events.RegionCaptureStatusChangeEvent;
import com.featherminecraft.RegionControl.events.RegionDefendEvent;
import com.featherminecraft.RegionControl.events.RegionInfluenceRateChangeEvent;

public class InfluenceManager
{
    
    // Region Variable
    private CapturableRegion region;
    
    // Private Vars
    // private HashMap<Faction, Float> percentageOwned;
    
    public InfluenceManager(CapturableRegion cregion)
    {
        region = cregion;
        region.setInfluenceManager(this);
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
        
        return influenceOwner;
    }
    
    public Float CalculateInfluenceRate()
    {
        /*
         * Influence Rate Calculations, Take away influence for this loop. Rate
         * 1: 1% Diff. + Rate 2: 33% Diff. + Rate 3: 66% Diff. + Rate 4: 100%
         * Diff.
         */
        List<ControlPoint> controlPoints = region.getControlPoints();
        float effectiveControlPointCount = 0F;
        for(ControlPoint controlPoint : controlPoints)
        {
            if(controlPoint.getOwner() == region.getMajorityController() && !controlPoint.isCapturing())
            {
                effectiveControlPointCount += 1F;
            }
            else if(controlPoint.getOwner() != region.getMajorityController() && !controlPoint.isCapturing())
            {
                effectiveControlPointCount -= 1F;
            }
        }
        
        float percentageOwned = effectiveControlPointCount / ((Integer) controlPoints.size()).floatValue();
        
        Float influenceRate = 0F;
        if(region.getMajorityController() != null)
        {
            if(percentageOwned >= 1F)
            {
                influenceRate = 4F;
            }
            
            else if(percentageOwned > 0.66)
            {
                influenceRate = 3F;
            }
            
            else if(percentageOwned > 0.33)
            {
                influenceRate = 2F;
            }
            
            else if(percentageOwned > 0.01F)
            {
                influenceRate = 1F;
            }
        }
        return influenceRate;
    }
    
    public Faction CalculateMajorityController()
    {
        /*
         * Majority Controller Calculations
         */
        List<ControlPoint> controlPoints = region.getControlPoints();
        Map<Faction, Float> ownedControlPoints = new HashMap<Faction, Float>();
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
        
        Faction majorityController = null;
        Float majorityAmount = 0F;
        for(Entry<Faction, Float> faction : ownedControlPoints.entrySet())
        {
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
    
    public void Runnable()
    {
        Faction majorityController = CalculateMajorityController();
        region.setMajorityController(majorityController);
        
        Faction influenceOwner = CalculateInfluenceOwner();
        region.setInfluenceOwner(influenceOwner);
        
        Float influenceRate = CalculateInfluenceRate();
        Float oldInfluenceRate = region.getInfluenceRate();
        region.setInfluenceRate(influenceRate);
        
        if(influenceOwner == null)
        {
            if(majorityController != null && influenceRate != null && influenceRate != 0F)
            {
                region.getInfluenceMap().put(majorityController, influenceRate);
                Bukkit.getServer().getPluginManager().callEvent(new RegionInfluenceRateChangeEvent(region, oldInfluenceRate, influenceRate));
            }
        }
        
        else if(influenceOwner != majorityController)
        {
            if(majorityController != null && influenceRate != null && influenceRate != 0F)
            {
                if(region.getInfluenceMap().get(influenceOwner) - influenceRate <= 0F)
                {
                    region.getInfluenceMap().put(influenceOwner, 0F);
                }
                
                else
                {
                    region.getInfluenceMap().put(influenceOwner, region.getInfluenceMap().get(influenceOwner) - influenceRate);
                }
            }
        }
        
        else if(influenceOwner == majorityController)
        {
            if(region.getInfluenceMap().get(influenceOwner) != region.getBaseInfluence() && majorityController != null && influenceRate != null && influenceRate != 0F)
            {
                if(region.getInfluenceMap().get(influenceOwner) + influenceRate >= region.getBaseInfluence())
                {
                    region.getInfluenceMap().put(influenceOwner, region.getBaseInfluence());
                    if(region.getOwner() == influenceOwner && influenceRate == 4F)
                    {
                        region.setBeingCaptured(false);
                        Bukkit.getServer().getPluginManager().callEvent(new RegionCaptureStatusChangeEvent(region, false));
                        Bukkit.getServer().getPluginManager().callEvent(new RegionDefendEvent(region, influenceOwner));
                    }
                    
                    else if(region.getOwner() != influenceOwner)
                    {
                        region.setOwner(influenceOwner);
                        region.setInfluenceRate(4F);
                        for(ControlPoint controlPoint : region.getControlPoints())
                        {
                            for(Entry<Faction, Float> influence : controlPoint.getInfluenceMap().entrySet())
                            {
                                controlPoint.getInfluenceMap().put(influence.getKey(), 0F);
                            }
                            controlPoint.getInfluenceMap().put(influenceOwner, controlPoint.getBaseInfluence());
                            controlPoint.setInfluenceOwner(influenceOwner);
                            controlPoint.setOwner(influenceOwner);
                        }
                        region.setBeingCaptured(false);
                        Bukkit.getServer().getPluginManager().callEvent(new RegionCaptureStatusChangeEvent(region, false));
                        Bukkit.getServer().getPluginManager().callEvent(new RegionCaptureEvent(region, region.getOwner(), influenceOwner));
                    }
                }
                
                else
                {
                    region.getInfluenceMap().put(influenceOwner, region.getInfluenceMap().get(influenceOwner) + influenceRate);
                }
            }
            
            else if(majorityController != null && influenceRate == 4F && region.getInfluenceMap().get(influenceOwner) + influenceRate >= region.getBaseInfluence() && region.getOwner() == influenceOwner && region.isBeingCaptured())
            {
                region.setBeingCaptured(false);
                Bukkit.getServer().getPluginManager().callEvent(new RegionCaptureStatusChangeEvent(region, false));
                Bukkit.getServer().getPluginManager().callEvent(new RegionDefendEvent(region, influenceOwner));
            }
        }
        
        // Capture Status Change events run here.
        if((region.getInfluenceMap().get(influenceOwner) != region.getBaseInfluence() && !region.isBeingCaptured()) || (region.getInfluenceRate() < 4F && !region.isBeingCaptured()))
        {
            region.setBeingCaptured(true);
            Bukkit.getServer().getPluginManager().callEvent(new RegionCaptureStatusChangeEvent(region, true));
        }
        
        if(!region.getInfluenceRate().equals(oldInfluenceRate))
        {
            Bukkit.getServer().getPluginManager().callEvent(new RegionInfluenceRateChangeEvent(region, oldInfluenceRate, influenceRate));
        }
    }
}
