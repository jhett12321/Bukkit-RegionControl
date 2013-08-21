package com.featherminecraft.regioncontrol.capturableregion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import com.featherminecraft.regioncontrol.Faction;
import com.featherminecraft.regioncontrol.ServerLogic;
import com.featherminecraft.regioncontrol.utils.PlayerUtils;

public class ControlPoint {

    //Region Variable
    private CapturableRegion region;
    
    //Control Point Info
    private String identifier;
    private Location location;
    private Integer captureRadius;
    
    //Capture Influence
    private Map<Faction,Float> influenceMap = new HashMap<Faction,Float>();
    private Faction influenceOwner;
    private Faction majorityPopulation;
    private Float baseInfluence;
    private Float captureRate;

    //Public Variables
    private Faction owner;
    private boolean capturing;

    public ControlPoint(String identifier,
            CapturableRegion region,
            Faction owner,
            Location location,
            Integer captureRadius,
            Float baseInfluence,
            Float influence,
            Faction influenceOwner)
    {
        this.region = region;
        
        this.setIdentifier(identifier);
        this.setOwner(owner);
        this.location = location;
        this.captureRadius = captureRadius;
        this.baseInfluence = baseInfluence;
        
        this.influenceOwner = influenceOwner;
        for(Entry<String, Faction> faction : ServerLogic.factions.entrySet())
        {
            if(faction.getValue() != null)
            {
                influenceMap.put(faction.getValue(), 0F);
            }
        }
        
        if(influenceOwner != null)
        {
            this.influenceMap.put(influenceOwner, baseInfluence);
        }
    }
    
    public void Runnable()
    {
        CalculateMajorityPopulation();
        CalculateInfluenceOwner();
        
        Faction majorityPopulation = this.majorityPopulation;
        Faction influenceOwner = this.influenceOwner;
        Map<Faction, Float> influenceMap = this.influenceMap;
        Float captureRate = this.captureRate;
        
        if(influenceOwner == null)
        {
            //TODO Run ControlPoint Neutralize Event
            if(majorityPopulation != null && captureRate != null && captureRate != 0)
            {
                influenceMap.put(majorityPopulation, captureRate);
            }
        }
        
        else if(influenceOwner != majorityPopulation)
        {
            if(majorityPopulation != null && captureRate != null && captureRate != 0)
            {
                if(influenceMap.get(influenceOwner) - captureRate <= 0)
                {
                    influenceMap.put(influenceOwner, 0F);
                }
                
                else
                {
                    influenceMap.put(influenceOwner, influenceMap.get(influenceOwner) - captureRate);
                }
            }
        }
        
        else if(influenceOwner == majorityPopulation)
        {
            if(majorityPopulation != null && captureRate != null && captureRate != 0)
            {
                if(influenceMap.get(influenceOwner) + captureRate >= this.baseInfluence)
                {
                    influenceMap.put(majorityPopulation, this.baseInfluence);
                    //TODO Run ControlPoint Capture Event
                }
                
                else
                {
                    influenceMap.put(majorityPopulation, influenceMap.get(influenceOwner) + captureRate);
                }
            }
        }
        
        if(influenceMap.get(influenceOwner) == this.baseInfluence)
        {
            capturing = false;
        }
        
        else
        {
            capturing = true;
        }
    }
    
    public void CalculateMajorityPopulation()
    {
        /*
         * Majority Population on Point Calculations
         */

        List<Player> players = ServerLogic.players.get(region);
        Map<Faction,Integer> factionInfluence = new HashMap<Faction,Integer>();
        PlayerUtils playerUtils = new PlayerUtils(); //Needs checking
        
        double radiusSquared = captureRadius*captureRadius;
        
        for (Player player : players) 
        {
            if(player.getLocation().distanceSquared(location) <= radiusSquared)
            {
                Faction playersFaction = playerUtils.getPlayerFaction(player);
                factionInfluence.put(playersFaction, factionInfluence.get(playersFaction) + 1);
            }
        }
        
        Integer majorityPopulationAmount = 0;
        Faction majorityPopulation = null;
        for (Entry<Faction, Integer> faction : factionInfluence.entrySet())
        {
            if(faction.getValue() > majorityPopulationAmount)
            {
                majorityPopulation = faction.getKey();
            }
            
            else if(faction.getValue() == majorityPopulationAmount)
            {
                majorityPopulation = null;
            }
        }
        
        if(majorityPopulation != null)
        {
            Integer populationAgainstAmount = 0;
            for (Entry<Faction, Integer> faction : factionInfluence.entrySet())
            {
                if(faction.getKey() != majorityPopulation)
                {
                    populationAgainstAmount += faction.getValue();
                }
            }
            
            if(majorityPopulationAmount - populationAgainstAmount > 0)
            {
                this.captureRate = ((Integer) (majorityPopulationAmount - populationAgainstAmount)).floatValue();
            }
        }
        
        this.majorityPopulation = majorityPopulation;
        
    }
    

    public void CalculateInfluenceOwner()
    {
        /*
         * Influence Owner Calculations
         */
        
        Map<Faction, Float> influenceMap = this.influenceMap;
        Faction influenceOwner = null;
        for(Entry<Faction, Float> influence : influenceMap.entrySet())
        {
            if(influence.getValue() >= 0)
            {
                influenceOwner = influence.getKey();
                break;
            }
        }
        
        this.influenceOwner = influenceOwner;
    }

    public Faction getOwner() {
        return owner;
    }

    public void setOwner(Faction owner) {
        this.owner = owner;
    }

    public boolean isCapturing() {
        return capturing;
    }

    public void setCapturing(boolean capturing) {
        this.capturing = capturing;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
}
