package com.featherminecraft.RegionControl.listeners;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.featherminecraft.RegionControl.DependencyManager;
import com.featherminecraft.RegionControl.RCPlayer;
import com.featherminecraft.RegionControl.api.events.CaptureStatusChangeEvent;
import com.featherminecraft.RegionControl.api.events.RegionCaptureEvent;
import com.featherminecraft.RegionControl.api.events.RegionDefendEvent;
import com.featherminecraft.RegionControl.capturableregion.ControlPoint;

public class ServerListener implements Listener
{
    @EventHandler(priority = EventPriority.LOWEST)
    public void onCaptureStatusChange(CaptureStatusChangeEvent event)
    {
        event.getRegion().setBeingCaptured(event.isBeingCaptured());
        
        if(DependencyManager.isSpoutCraftAvailable())
        {
            List<RCPlayer> playerList = event.getRegion().getPlayers();
            
            for(RCPlayer player : playerList)
            {
                if(player.hasSpout())
                {
                    player.getSpoutClientLogic().setRegionCaptureStatus(event.isBeingCaptured());
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onRegionCapture(RegionCaptureEvent event)
    {
        // Region
        event.getRegion().setOwner(event.getNewOwner());
        for(ControlPoint controlPoint : event.getRegion().getControlPoints())
        {
            controlPoint.setOwner(event.getNewOwner());
        }
        Bukkit.getServer().broadcastMessage(ChatColor.YELLOW + event.getNewOwner().getDisplayName() + " have captured " + event.getRegion().getDisplayName() + "!");
        
        for(BlockState block : event.getRegion().getBlocksPlaced())
        {
            block.getBlock().setType(Material.AIR);
        }
        for(BlockState block : event.getRegion().getBlocksDestroyed())
        {
            block.update(true, false);
        }
        event.getRegion().getBlocksPlaced().clear();
        event.getRegion().getBlocksDestroyed().clear();
        
        // Player Stats
        for(RCPlayer player : event.getRegion().getPlayers())
        {
            player.addRegionCapture();
        }
        
        // Scoreboard
        event.getRegion().getRegionScoreboard().updateOwner();
        
        // Spoutcraft
        if(DependencyManager.isSpoutCraftAvailable())
        {
            List<RCPlayer> playerList = event.getRegion().getPlayers();
            for(RCPlayer player : playerList)
            {
                if(player.hasSpout())
                {
                    player.getSpoutClientLogic().updateRegion(event.getRegion());
                    if(player.getSpoutClientLogic().getRespawnScreen() != null)
                    {
                        if(event.getNewOwner() == player.getFaction())
                        {
                            player.getSpoutClientLogic().getRespawnScreen().addRegionToSpawnList(event.getRegion());
                            player.getSpoutClientLogic().getRespawnScreen().updateRegions();
                        }
                        else
                        {
                            player.getSpoutClientLogic().getRespawnScreen().removeRegionFromSpawnList(event.getRegion());
                            player.getSpoutClientLogic().getRespawnScreen().updateRegions();
                        }
                    }
                }
                // TODO Play Capture Sounds/Music
            }
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onRegionDefend(RegionDefendEvent event)
    {
        // Player Stats
        for(RCPlayer player : event.getRegion().getPlayers())
        {
            player.addRegionDefend();
        }
        
        // Scoreboard
        event.getRegion().getRegionScoreboard().updateOwner();
        
        // Spoutcraft
        if(DependencyManager.isSpoutCraftAvailable())
        {
            List<RCPlayer> playerList = event.getRegion().getPlayers();
            for(RCPlayer player : playerList)
            {
                if(player.hasSpout())
                {
                    player.getSpoutClientLogic().updateRegion(event.getRegion());
                }
            }
        }
    }
}