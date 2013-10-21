package com.featherminecraft.RegionControl.capturableregion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;

import com.featherminecraft.RegionControl.Faction;
import com.featherminecraft.RegionControl.RCPlayer;
import com.featherminecraft.RegionControl.RegionControl;
import com.featherminecraft.RegionControl.ServerLogic;
import com.featherminecraft.RegionControl.events.ControlPointCaptureEvent;
import com.featherminecraft.RegionControl.events.ControlPointNeutraliseEvent;

public class ControlPoint {

    //Region Variable
    private CapturableRegion region;
    
    //Control Point Info
    private String identifier;
    private Location location;
    private Double captureRadius;
    
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
            Faction owner,
            Location location,
            Double captureRadius,
            Float baseInfluence,
            Float influence,
            Faction influenceOwner)
    
    {
        this.identifier = identifier;
        this.owner = owner;
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
        
        this.location.getBlock().setTypeId(85,false);
        this.location.setY(location.getY() + 1);
        this.location.getBlock().setTypeId(85,false);
        this.location.setY(location.getY() + 1);
        
        if(influenceMap.get(influenceOwner) == baseInfluence)
        {
            this.location.getBlock().setTypeIdAndData(Material.WOOL.getId(),DyeColor.getByColor(owner.getFactionColor()).getWoolData(),false);
        }
        else
        {
            this.location.getBlock().setTypeIdAndData(Material.WOOL.getId(),DyeColor.getByColor(Color.WHITE).getWoolData(),false);
        }
        }
    
    public void Runnable()
    {
        Faction majorityPopulation = CalculateMajorityPopulation();
        Faction influenceOwner = CalculateInfluenceOwner();
        
        if(influenceOwner == null)
        {
            if(majorityPopulation != null && captureRate != null && captureRate != 0F)
            {
                influenceMap.put(majorityPopulation, captureRate);
            }
        }
        
        else if(influenceOwner != majorityPopulation)
        {
            if(majorityPopulation != null && captureRate != null && captureRate != 0F)
            {
                if(influenceMap.get(influenceOwner) - captureRate <= 0F)
                {
                    influenceMap.put(influenceOwner, 0F);
                    influenceMap.put(majorityPopulation, 1F);
                }
                
                else
                {
                    influenceMap.put(influenceOwner, influenceMap.get(influenceOwner) - captureRate);
                }
            }
        }
        
        else if(influenceOwner == majorityPopulation)
        {
            if(majorityPopulation != null && captureRate != null && captureRate != 0F)
            {
                if(influenceMap.get(influenceOwner) + captureRate >= this.baseInfluence)
                {
                    influenceMap.put(majorityPopulation, this.baseInfluence);
                    Bukkit.getServer().getPluginManager().callEvent(new ControlPointCaptureEvent(region, influenceOwner, this));
                    //TODO Migrate to listener.
                    this.owner = influenceOwner;
                }
                
                else
                {
                    influenceMap.put(influenceOwner, influenceMap.get(influenceOwner) + captureRate);
                }
            }
        }
        
        if(influenceMap.get(influenceOwner) == this.baseInfluence && capturing == true)
        {
            this.location.getBlock().setTypeIdAndData(Material.WOOL.getId(),DyeColor.getByColor(this.owner.getFactionColor()).getWoolData(),false);
            capturing = false;
        }
        
        else if(influenceMap.get(influenceOwner) != this.baseInfluence && capturing == false)
        {
            this.location.getBlock().setTypeIdAndData(Material.WOOL.getId(),DyeColor.getByColor(Color.WHITE).getWoolData(),false);
            Bukkit.getServer().getPluginManager().callEvent(new ControlPointNeutraliseEvent(region, influenceOwner, this));
            capturing = true;
        }
    }
    
    private Faction CalculateMajorityPopulation()
    {
        /*
         * Majority Population on Point Calculations
         */

        List<RCPlayer> players = region.getPlayers();
        Map<Faction,Integer> factionInfluence = new HashMap<Faction,Integer>();
        
        for (Entry<String, Faction> faction : ServerLogic.factions.entrySet())
        {
            factionInfluence.put(faction.getValue(), 0);
        }
        
        for (RCPlayer player : players) 
        {
            if(player.getBukkitPlayer().getLocation().distanceSquared(location) <= captureRadius*captureRadius)
            {
                Faction playersFaction = player.getFaction();
                factionInfluence.put(playersFaction, factionInfluence.get(playersFaction) + 1);
            }
        }
        
        int majorityPopulationAmount = 0;
        Faction majorityPopulation = null;
        for (Entry<Faction, Integer> faction : factionInfluence.entrySet())
        {
            if(faction.getValue() > majorityPopulationAmount)
            {
                majorityPopulation = faction.getKey();
                majorityPopulationAmount = faction.getValue().intValue();
            }
            
            else if(faction.getValue() == majorityPopulationAmount)
            {
                majorityPopulation = null;
            }
        }
        
        if(majorityPopulation != null)
        {
            int populationAgainstAmount = 0;
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
        
        return majorityPopulation;
    }
    

    private Faction CalculateInfluenceOwner()
    {
        /*
         * Influence Owner Calculations
         */
        
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

    public CapturableRegion getRegion() {
        return region;
    }

    public void setRegion(CapturableRegion region) {
        this.region = region;
    }
    
    public Map<Faction, Float> getInfluenceMap() {
        return influenceMap;
    }

    public void setInfluenceMap(Map<Faction, Float> influenceMap) {
        this.influenceMap = influenceMap;
    }

    public Faction getInfluenceOwner() {
        return influenceOwner;
    }

    public void setInfluenceOwner(Faction influenceOwner) {
        this.influenceOwner = influenceOwner;
    }

    public Float getBaseInfluence() {
        return baseInfluence;
    }

    public void setBaseInfluence(Float baseInfluence) {
        this.baseInfluence = baseInfluence;
    }

    public Faction getMajorityPopulation() {
        return majorityPopulation;
    }

    public void setMajorityPopulation(Faction majorityPopulation) {
        this.majorityPopulation = majorityPopulation;
    }
}
