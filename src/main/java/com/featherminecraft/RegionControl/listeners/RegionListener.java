package com.featherminecraft.RegionControl.listeners;

import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.featherminecraft.RegionControl.DependencyManager;
import com.featherminecraft.RegionControl.RCPlayer;
import com.featherminecraft.RegionControl.api.events.ControlPointCaptureEvent;
import com.featherminecraft.RegionControl.api.events.ControlPointDefendEvent;
import com.featherminecraft.RegionControl.api.events.ControlPointInfluenceRateChangeEvent;
import com.featherminecraft.RegionControl.api.events.ControlPointNeutralizeEvent;
import com.featherminecraft.RegionControl.api.events.InfluenceOwnerChangeEvent;
import com.featherminecraft.RegionControl.api.events.RegionInfluenceRateChangeEvent;
import com.featherminecraft.RegionControl.capturableregion.ControlPoint;
import com.featherminecraft.RegionControl.spout.SpoutClientLogic;

public class RegionListener implements Listener
{
    @EventHandler(priority = EventPriority.MONITOR)
    public void onInfluenceOwnerChange(InfluenceOwnerChangeEvent event)
    {
        event.getRegion().getRegionScoreboard().updateInfluenceRate();
        
        // Spoutcraft
        if(DependencyManager.isSpoutCraftAvailable())
        {
            List<RCPlayer> affectedPlayers = event.getRegion().getPlayers();
            for(RCPlayer player : affectedPlayers)
            {
                if(player.hasSpout())
                {
                    SpoutClientLogic spoutClientLogic = player.getSpoutClientLogic();
                    spoutClientLogic.updateInfluenceOwnerIcon();
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onInfluenceRateChange(RegionInfluenceRateChangeEvent event)
    {
        event.getRegion().getRegionScoreboard().updateInfluenceRate();
        
        // Spoutcraft
        if(DependencyManager.isSpoutCraftAvailable())
        {
            List<RCPlayer> affectedPlayers = event.getRegion().getPlayers();
            for(RCPlayer player : affectedPlayers)
            {
                if(player.hasSpout())
                {
                    SpoutClientLogic spoutClientLogic = player.getSpoutClientLogic();
                    spoutClientLogic.updateInfluenceRate(event.getNewInfluenceRate());
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onControlPointCapture(ControlPointCaptureEvent event)
    {
        event.getRegion().getRegionScoreboard().updateControlPoints();
        
        // Spoutcraft
        if(DependencyManager.isSpoutCraftAvailable())
        {
            List<RCPlayer> playerList = event.getRegion().getPlayers();
            
            for(RCPlayer player : playerList)
            {
                if(player.hasSpout())
                {
                    player.getSpoutClientLogic().updateControlPoints(event.getRegion());
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onControlPointDefend(ControlPointDefendEvent event)
    {
        event.getRegion().getRegionScoreboard().updateControlPoints();
        
        // Spoutcraft
        if(DependencyManager.isSpoutCraftAvailable())
        {
            List<RCPlayer> playerList = event.getRegion().getPlayers();
            
            for(RCPlayer player : playerList)
            {
                if(player.hasSpout())
                {
                    player.getSpoutClientLogic().updateControlPoints(event.getRegion());
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onControlPointNeutralise(ControlPointNeutralizeEvent event)
    {
        event.getRegion().getRegionScoreboard().updateControlPoints();
        
        // Spoutcraft
        if(DependencyManager.isSpoutCraftAvailable())
        {
            List<RCPlayer> playerList = event.getRegion().getPlayers();
            
            for(RCPlayer player : playerList)
            {
                if(player.hasSpout())
                {
                    player.getSpoutClientLogic().updateControlPoints(event.getRegion());
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onControlPointPlayerInfluenceChange(ControlPointInfluenceRateChangeEvent event)
    {
        ControlPoint controlPoint = event.getControlPoint();
        
        List<RCPlayer> addedPlayers = event.getAddedPlayers();
        List<RCPlayer> removedPlayers = event.getRemovedPlayers();
        
        for(RCPlayer player : addedPlayers)
        {
            player.getUi().setControlPoint(controlPoint);
        }
        
        for(RCPlayer player : removedPlayers)
        {
            player.getUi().setControlPoint(null);
        }
    }
}
