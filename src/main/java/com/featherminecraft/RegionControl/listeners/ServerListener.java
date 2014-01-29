package com.featherminecraft.RegionControl.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.featherminecraft.RegionControl.api.events.CaptureStatusChangeEvent;
import com.featherminecraft.RegionControl.api.events.ChangeRegionEvent;
import com.featherminecraft.RegionControl.api.events.ControlPointCaptureEvent;
import com.featherminecraft.RegionControl.api.events.ControlPointNeutralizeEvent;
import com.featherminecraft.RegionControl.api.events.RegionCaptureEvent;
import com.featherminecraft.RegionControl.capturableregion.ControlPoint;

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
    public void onControlPointNeutralise(ControlPointNeutralizeEvent event)
    {
        
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onCaptureStatusChange(CaptureStatusChangeEvent event)
    {
        event.getRegion().setBeingCaptured(event.isBeingCaptured());
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onRegionCapture(RegionCaptureEvent event)
    {
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
    }
}