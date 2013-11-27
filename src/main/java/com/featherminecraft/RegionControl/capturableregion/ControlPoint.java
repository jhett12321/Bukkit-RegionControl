package com.featherminecraft.RegionControl.capturableregion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;

import com.featherminecraft.RegionControl.Faction;
import com.featherminecraft.RegionControl.RCPlayer;
import com.featherminecraft.RegionControl.ServerLogic;
import com.featherminecraft.RegionControl.events.ControlPointCaptureEvent;
import com.featherminecraft.RegionControl.events.ControlPointDefendEvent;
import com.featherminecraft.RegionControl.events.ControlPointNeutraliseEvent;
import com.featherminecraft.RegionControl.events.ControlPointPlayerInfluenceChangeEvent;

public class ControlPoint
{
    
    // Region Variable
    private CapturableRegion region;
    
    // Control Point Info
    private String identifier;
    private Location location;
    private Double captureRadius;
    
    // Capture Influence
    private Map<Faction, Float> influenceMap = new HashMap<Faction, Float>();
    private Faction influenceOwner;
    private Faction majorityPopulation;
    private Float baseInfluence;
    private Float captureRate;
    
    // Public Variables
    private Faction owner;
    private boolean capturing;
    
    private List<RCPlayer> influentialPlayers = new ArrayList<RCPlayer>();
    
    public ControlPoint(String identifier, Faction owner, Location location, Double captureRadius, Float baseInfluence, Float influence, Faction influenceOwner)
    
    {
        this.identifier = identifier;
        this.owner = owner;
        this.location = location;
        this.captureRadius = captureRadius;
        this.baseInfluence = baseInfluence;
        
        this.influenceOwner = influenceOwner;
        for(Entry<String, Faction> faction : ServerLogic.factions.entrySet())
        {
            if(faction.getValue() == influenceOwner && influenceOwner != null)
            {
                influenceMap.put(influenceOwner, baseInfluence);
            }
            
            else if(faction.getValue() != null)
            {
                influenceMap.put(faction.getValue(), 0F);
            }
        }
        
        this.location.getBlock().setTypeId(85, false);
        this.location.setY(location.getY() + 1);
        this.location.getBlock().setTypeId(85, false);
        this.location.setY(location.getY() + 1);
        
        if(influenceMap.get(influenceOwner) == baseInfluence)
        {
            this.location.getBlock().setTypeIdAndData(Material.WOOL.getId(), DyeColor.getByColor(owner.getFactionColor()).getWoolData(), false);
            capturing = false;
        }
        else
        {
            this.location.getBlock().setTypeIdAndData(Material.WOOL.getId(), DyeColor.getByColor(Color.WHITE).getWoolData(), false);
            capturing = true;
        }
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
    
    private Faction CalculateMajorityPopulation()
    {
        /*
         * Majority Population on Point Calculations
         */
        
        List<RCPlayer> players = region.getPlayers();
        Map<Faction, Integer> factionInfluence = new HashMap<Faction, Integer>();
        
        for(Entry<String, Faction> faction : ServerLogic.factions.entrySet())
        {
            factionInfluence.put(faction.getValue(), 0);
        }
        
        List<RCPlayer> influentialPlayers = new ArrayList<RCPlayer>();
        
        for(RCPlayer player : players)
        {
            if(!player.getBukkitPlayer().isDead() && player.getBukkitPlayer().getLocation().distanceSquared(location) <= captureRadius * captureRadius)
            {
                influentialPlayers.add(player);
                Faction playersFaction = player.getFaction();
                factionInfluence.put(playersFaction, factionInfluence.get(playersFaction) + 1);
            }
        }
        
        if(influentialPlayers != this.influentialPlayers)
        {
            List<RCPlayer> playersRemoved = new ArrayList<RCPlayer>();
            List<RCPlayer> playersAdded = new ArrayList<RCPlayer>();
            
            for(RCPlayer player : this.influentialPlayers)
            {
                if(!influentialPlayers.contains(player))
                {
                    playersRemoved.add(player);
                }
            }
            
            for(RCPlayer player : influentialPlayers)
            {
                if(!this.influentialPlayers.contains(player))
                {
                    playersAdded.add(player);
                }
            }
            
            if(playersRemoved.size() > 0 || playersAdded.size() > 0)
            {
                if(playersAdded.size() > 0)
                {
                    for(RCPlayer player : playersAdded)
                    {
                        this.influentialPlayers.add(player);
                    }
                }
                
                if(playersRemoved.size() > 0)
                {
                    for(RCPlayer player : playersRemoved)
                    {
                        this.influentialPlayers.remove(player);
                    }
                }
                
                Bukkit.getServer().getPluginManager().callEvent(new ControlPointPlayerInfluenceChangeEvent(region, this, this.influentialPlayers, playersAdded, playersRemoved));
            }
        }
        
        int majorityPopulationAmount = 0;
        Faction majorityPopulation = null;
        for(Entry<Faction, Integer> faction : factionInfluence.entrySet())
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
            for(Entry<Faction, Integer> faction : factionInfluence.entrySet())
            {
                if(faction.getKey() != majorityPopulation)
                {
                    populationAgainstAmount += faction.getValue();
                }
            }
            
            if(majorityPopulationAmount - populationAgainstAmount > 0)
            {
                captureRate = ((Integer) (majorityPopulationAmount - populationAgainstAmount)).floatValue();
            }
        }
        
        return majorityPopulation;
    }
    
    public Float getBaseInfluence()
    {
        return baseInfluence;
    }
    
    public String getIdentifier()
    {
        return identifier;
    }
    
    public Map<Faction, Float> getInfluenceMap()
    {
        return influenceMap;
    }
    
    public Faction getInfluenceOwner()
    {
        return influenceOwner;
    }
    
    public Faction getMajorityPopulation()
    {
        return majorityPopulation;
    }
    
    public Faction getOwner()
    {
        return owner;
    }
    
    public CapturableRegion getRegion()
    {
        return region;
    }
    
    public boolean isCapturing()
    {
        return capturing;
    }
    
    public void Runnable()
    {
        Faction majorityPopulation = CalculateMajorityPopulation();
        this.majorityPopulation = majorityPopulation;
        Faction influenceOwner = CalculateInfluenceOwner();
        this.influenceOwner = influenceOwner;
        
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
                }
                
                else
                {
                    influenceMap.put(influenceOwner, influenceMap.get(influenceOwner) - captureRate);
                }
            }
        }
        
        else if(influenceOwner == majorityPopulation)
        {
            if(majorityPopulation != null && captureRate != null && captureRate != 0F && influenceMap.get(influenceOwner) != baseInfluence)
            {
                if(influenceMap.get(influenceOwner) + captureRate >= baseInfluence)
                {
                    influenceMap.put(majorityPopulation, baseInfluence);
                    location.getBlock().setTypeIdAndData(Material.WOOL.getId(), DyeColor.getByColor(influenceOwner.getFactionColor()).getWoolData(), false);
                    
                    if(owner != influenceOwner)
                    {
                        owner = influenceOwner;
                        capturing = false;
                        Bukkit.getServer().getPluginManager().callEvent(new ControlPointCaptureEvent(region, influenceOwner, this));
                    }
                    else
                    {
                        capturing = false;
                        Bukkit.getServer().getPluginManager().callEvent(new ControlPointDefendEvent(region, influenceOwner, this));
                    }
                }
                
                else
                {
                    influenceMap.put(influenceOwner, influenceMap.get(influenceOwner) + captureRate);
                }
            }
        }
        
        if(influenceMap.get(influenceOwner) != baseInfluence && !capturing)
        {
            capturing = true;
            location.getBlock().setTypeIdAndData(Material.WOOL.getId(), DyeColor.getByColor(Color.WHITE).getWoolData(), false);
            Bukkit.getServer().getPluginManager().callEvent(new ControlPointNeutraliseEvent(region, influenceOwner, this));
        }
    }
    
    public void setBaseInfluence(Float baseInfluence)
    {
        this.baseInfluence = baseInfluence;
    }
    
    public void setCapturing(boolean capturing)
    {
        this.capturing = capturing;
    }
    
    public void setIdentifier(String identifier)
    {
        this.identifier = identifier;
    }
    
    public void setInfluenceMap(Map<Faction, Float> influenceMap)
    {
        this.influenceMap = influenceMap;
    }
    
    public void setInfluenceOwner(Faction influenceOwner)
    {
        this.influenceOwner = influenceOwner;
    }
    
    public void setMajorityPopulation(Faction majorityPopulation)
    {
        this.majorityPopulation = majorityPopulation;
    }
    
    public void setOwner(Faction owner)
    {
        this.owner = owner;
        capturing = false;
        for(Entry<Faction, Float> faction : influenceMap.entrySet())
        {
            if(faction.getKey() == owner)
            {
                influenceMap.put(owner, baseInfluence);
            }
            else
            {
                influenceMap.put(faction.getKey(), 0F);
            }
        }
        location.getBlock().setTypeIdAndData(Material.WOOL.getId(), DyeColor.getByColor(owner.getFactionColor()).getWoolData(), false);
        Bukkit.getServer().getPluginManager().callEvent(new ControlPointCaptureEvent(region, influenceOwner, this));
    }
    
    public void setRegion(CapturableRegion region)
    {
        this.region = region;
    }
}
