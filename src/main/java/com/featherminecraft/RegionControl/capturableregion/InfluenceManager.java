package com.featherminecraft.RegionControl.capturableregion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;

import com.featherminecraft.RegionControl.Faction;
import com.featherminecraft.RegionControl.ServerLogic;
import com.featherminecraft.RegionControl.api.events.CaptureStatusChangeEvent;
import com.featherminecraft.RegionControl.api.events.InfluenceOwnerChangeEvent;
import com.featherminecraft.RegionControl.api.events.RegionCaptureEvent;
import com.featherminecraft.RegionControl.api.events.RegionDefendEvent;
import com.featherminecraft.RegionControl.api.events.RegionInfluenceRateChangeEvent;
import com.featherminecraft.RegionControl.data.Config;

public class InfluenceManager
{
    private CapturableRegion region;
    private HashMap<Faction, Float> ownedControlPoints = new HashMap<Faction, Float>();
    
    InfluenceManager(CapturableRegion cregion)
    {
        region = cregion;
    }
    
    private Faction CalculateInfluenceOwner()
    {
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
    
    private float CalculateInfluenceRate(Faction majorityController)
    {
        float baseControlPointCount = ownedControlPoints.remove(majorityController);
        float controlPointCountPenalty = 0;
        for(Entry<Faction,Float> ownedPoints : ownedControlPoints.entrySet())
        {
            if(Config.getMainConfig().getInt("regions.captureMode") == 0 && majorityController != null)
            {
                if(ownedPoints.getValue() > controlPointCountPenalty)
                {
                    controlPointCountPenalty = ownedPoints.getValue();
                }
            }
            else if(Config.getMainConfig().getInt("regions.captureMode") == 1 && majorityController != null)
            {
                if(ownedPoints.getKey() != majorityController)
                {
                    controlPointCountPenalty += ownedPoints.getValue();
                }
            }
        }
        
        float effectiveControlPointCount = baseControlPointCount - controlPointCountPenalty;
        float percentageOwned = effectiveControlPointCount / ((Integer) region.getControlPoints().size()).floatValue();
        
        float influenceRate = 0F;
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
        return influenceRate;
    }
    
    private Faction CalculateMajorityController()
    {
        List<ControlPoint> controlPoints = region.getControlPoints();
        ownedControlPoints.clear();
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
        
        if(influenceOwner == null && majorityController != null)
        {
            influenceOwner = majorityController;
            region.setInfluenceOwner(influenceOwner);
            Bukkit.getServer().getPluginManager().callEvent(new InfluenceOwnerChangeEvent(region));
        }
        else if(influenceOwner != null && majorityController != null)
        {
            region.setInfluenceOwner(influenceOwner);
        }
        
        float influenceRate = CalculateInfluenceRate(majorityController);
        float oldInfluenceRate = region.getInfluenceRate();
        region.setInfluenceRate(influenceRate);
        
        if(influenceOwner != majorityController)
        {
            if(majorityController != null && influenceRate != 0F)
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
            if(region.isBeingCaptured() && majorityController != null && influenceRate != 0F)
            {
                if(region.getInfluenceMap().get(influenceOwner) >= region.getBaseInfluence())
                {
                    if(region.getOwner() == influenceOwner && influenceRate == 4F)
                    {
                        Bukkit.getServer().getPluginManager().callEvent(new RegionDefendEvent(region, influenceOwner));
                        Bukkit.getServer().getPluginManager().callEvent(new CaptureStatusChangeEvent(region, false));
                        return;
                    }
                    
                    else if(region.getOwner() != influenceOwner)
                    {
                        Bukkit.getServer().getPluginManager().callEvent(new RegionCaptureEvent(region, region.getOwner(), influenceOwner));
                        Bukkit.getServer().getPluginManager().callEvent(new CaptureStatusChangeEvent(region, false));
                        return;
                    }
                }
                
                else if(region.getInfluenceMap().get(influenceOwner) + influenceRate >= region.getBaseInfluence())
                {
                    region.getInfluenceMap().put(influenceOwner, region.getBaseInfluence());
                }
                
                else
                {
                    region.getInfluenceMap().put(influenceOwner, region.getInfluenceMap().get(influenceOwner) + influenceRate);
                }
            }
        }
        
        if((region.getInfluenceMap().get(influenceOwner) < region.getBaseInfluence() && !region.isBeingCaptured()) || (region.getInfluenceRate() < 4F && !region.isBeingCaptured()))
        {
            Bukkit.getServer().getPluginManager().callEvent(new CaptureStatusChangeEvent(region, true));
        }
        
        if(region.getInfluenceRate() != oldInfluenceRate)
        {
            Bukkit.getServer().getPluginManager().callEvent(new RegionInfluenceRateChangeEvent(region, oldInfluenceRate, influenceRate));
        }
    }
}