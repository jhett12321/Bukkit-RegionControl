package com.featherminecraft.regioncontrol.capturableregion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.featherminecraft.regioncontrol.Faction;
import com.featherminecraft.regioncontrol.ServerLogic;

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
        CalculateMajorityController();
        CalculateInfluenceOwner();
        CalculateInfluenceRate();
        
        Faction majorityController = region.getMajorityController();
        Faction influenceOwner = region.getInfluenceOwner();
        Map<Faction, Float> influenceMap = region.getInfluenceMap();
        Float influenceRate = region.getInfluenceRate();
        
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
                        //Run Region Defend Event
                    }
                    
                    else if(region.getOwner() != influenceOwner)
                    {
                        //Run Region Capture Event
                    }
                }
                
                else
                {
                    influenceMap.put(majorityController, influenceMap.get(influenceOwner) + influenceRate);
                }
            }
        }
    }
    
    public void CalculateMajorityController()
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
                ownedControlPoints.put(controlPoint.getOwner(), ownedControlPoints.get(controlPoint) + 1);
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
        
        region.setMajorityController(majorityController);
    }
    
    public void CalculateInfluenceOwner()
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
        
        region.setInfluenceOwner(influenceOwner);
    }
    
    public void CalculateInfluenceRate()
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
        
        Float percentageDifference = percentageOwned.get(region.getMajorityController()) - percentageAgainst;
        Float influenceRate = 0F;
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
        
        region.setInfluenceRate(influenceRate);
    }
}
