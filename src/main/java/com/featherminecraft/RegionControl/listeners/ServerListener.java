package com.featherminecraft.RegionControl.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import com.featherminecraft.RegionControl.capturableregion.ControlPoint;
import com.featherminecraft.RegionControl.events.ChangeRegionEvent;
import com.featherminecraft.RegionControl.events.ControlPointCaptureEvent;
import com.featherminecraft.RegionControl.events.ControlPointNeutraliseEvent;
import com.featherminecraft.RegionControl.events.RegionCaptureEvent;
import com.featherminecraft.RegionControl.events.RegionDefendEvent;

public class ServerListener implements Listener
{
    @EventHandler(priority = EventPriority.LOWEST)
    public void onChangeRegion(ChangeRegionEvent event)
    {
        
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onControlPointCapture(ControlPointCaptureEvent event)
    {
        
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onControlPointNeutralise(ControlPointNeutraliseEvent event)
    {
        
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onRegionCapture(RegionCaptureEvent event)
    {
        event.getCapturableRegion().setOwner(event.getNewOwner());
        event.getCapturableRegion().setInfluenceRate(4F);
        event.getCapturableRegion().setBeingCaptured(false);
        Bukkit.getServer().broadcastMessage(ChatColor.YELLOW + event.getNewOwner().getDisplayName() + " have captured " + event.getCapturableRegion().getDisplayName() + "!");
        
        for(BlockState block : event.getCapturableRegion().getBlocksPlaced())
        {
            block.getBlock().setType(Material.AIR);
        }
        for(BlockState block : event.getCapturableRegion().getBlocksDestroyed())
        {
            block.update(true, false);
        }
        event.getCapturableRegion().getBlocksPlaced().clear();
        event.getCapturableRegion().getBlocksDestroyed().clear();
        
        for(ControlPoint controlPoint : event.getCapturableRegion().getControlPoints())
        {
            controlPoint.setOwner(event.getNewOwner());
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onRegionDefend(RegionDefendEvent event)
    {
        event.getCapturableRegion().setBeingCaptured(false);
    }
    
}