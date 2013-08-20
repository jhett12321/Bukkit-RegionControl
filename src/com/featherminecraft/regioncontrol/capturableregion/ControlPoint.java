package com.featherminecraft.regioncontrol.capturableregion;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.featherminecraft.regioncontrol.Faction;
import com.featherminecraft.regioncontrol.RegionControl;
import com.featherminecraft.regioncontrol.ServerLogic;
import com.featherminecraft.regioncontrol.events.ControlPointCaptureEvent;
import com.featherminecraft.regioncontrol.events.ControlPointNeutraliseEvent;
import com.featherminecraft.regioncontrol.utils.PlayerUtils;

public class ControlPoint extends BukkitRunnable {

    private String controlpointname;
    private Location location;
    private Integer radius;
    private Boolean capturing;
    private Faction owner;
    private Map<Faction,Integer> pointinfluence;
    private int controlPointBaseInfluence;
    
    public ControlPoint(String controlpointname, Location location, Faction owner)
    {
        this.controlpointname = controlpointname;
        this.location = location;
        this.owner = owner;
        this.controlPointBaseInfluence = 5; //TODO: Make this customizable
        
        if (this.location.getWorld().getBlockAt(this.location).getTypeId() == 0)
        {
            this.location.getWorld().getBlockAt(this.location).setTypeId(76, false);
        }
    }
    
    @Override
    public void run() {
        Player[] players = RegionControl.plugin.getServer().getOnlinePlayers();
        
        double radiusSquared = radius*radius;
        
        PlayerUtils playerUtils = new PlayerUtils();
        Map<Faction,Integer> PlayersOnPoint = new HashMap<Faction,Integer>();

        for(Entry<String, Faction> faction : ServerLogic.factions.entrySet())
        {
            PlayersOnPoint.put(faction.getValue(), 0);
        }
     
        for (Player player : players) {
            if(player.getLocation().distanceSquared(location) <= radiusSquared && region.isCapturable(playerUtils.getPlayerFaction(player)))
            {
                PlayersOnPoint.put(playerUtils.getPlayerFaction(player), PlayersOnPoint.get(playerUtils.getPlayerFaction(player)) + 1);
            }
        }
        
        int majorityPlayers = 0;
        Faction majorityFaction = null;
        for (Entry<Faction, Integer> faction : PlayersOnPoint.entrySet())
        {
            if(faction.getValue() > majorityPlayers)
            {
                majorityFaction = faction.getKey();
            }
            else if(faction.getValue() == majorityPlayers)
            {
                majorityFaction = null;
            }
        }
        
        Faction factionwithinfluence = null;
        for(Entry<Faction,Integer> faction : pointinfluence.entrySet())
        {
            if(faction.getValue() > 0)
            {
                factionwithinfluence = faction.getKey();
            }
        }
        
        if(majorityFaction == null)
        {
            this.capturing = false;
        }
        
        if (factionwithinfluence == null )
        {
            this.pointinfluence.put(majorityFaction, this.pointinfluence.get(majorityFaction) + 1);
            this.capturing = true;
        }
        
        else if(factionwithinfluence == this.owner)
        {
            this.pointinfluence.put(majorityFaction, this.pointinfluence.get(majorityFaction) + 1);
        }
        
        else if (factionwithinfluence != this.owner)
        {
            this.pointinfluence.put(this.owner, this.pointinfluence.get(this.owner) - 1);
        }
        
        if(this.pointinfluence.get(this.owner) == 0 && this.owner != null)
        {
            Bukkit.getServer().getPluginManager().callEvent( new ControlPointNeutraliseEvent(this.region, this.owner, this) );
        }
        
        if(this.owner == null && this.pointinfluence.get(majorityFaction) == controlPointBaseInfluence)
        {
            Bukkit.getServer().getPluginManager().callEvent( new ControlPointCaptureEvent(this.region, majorityFaction, this) );
            this.capturing = false;
        }
        
        if(this.capturing)
        {
            if(region.getWorld().getBlockAt(this.location).getTypeId() == 76)
            {
                region.getWorld().getBlockAt(this.location).setTypeId(75, false);
            }
        }

        if(!capturing && region.getWorld().getBlockAt(this.location).getTypeId() == 75)
        {
            region.getWorld().getBlockAt(this.location).setTypeId(76, false);
        }

    }

    public String getControlPointName() {
        return controlpointname;
    }

    public Location getLocation() {
        return location;
    }

    public CapturableRegion getCapturableRegion() {
        return region;
    }

    public Integer getRadius() {
        return radius;
    }

    public Faction getOwner() {
        return owner;
    }

    public void setOwner(Faction owner) {
        this.owner = owner;
    }
    
    public Boolean isCapturing()
    {
        return capturing;
    }
}
