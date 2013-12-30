package com.featherminecraft.RegionControl.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.featherminecraft.RegionControl.events.ChangeRegionEvent;
import com.featherminecraft.RegionControl.events.ControlPointCaptureEvent;
import com.featherminecraft.RegionControl.events.ControlPointNeutraliseEvent;
import com.featherminecraft.RegionControl.events.RegionCaptureEvent;
import com.featherminecraft.RegionControl.events.RegionDefendEvent;

public class GenericListener implements Listener
{
    @EventHandler(priority = EventPriority.MONITOR)
    public void onChangeRegion(ChangeRegionEvent event)
    {
        
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onControlPointCapture(ControlPointCaptureEvent event)
    {
        
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onControlPointNeutralise(ControlPointNeutraliseEvent event)
    {
        
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onRegionCapture(RegionCaptureEvent event)
    {
        Bukkit.getServer().broadcastMessage(ChatColor.YELLOW + event.getNewOwner().getDisplayName() + " have captured " + event.getCapturableRegion().getDisplayName() + "!");
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onRegionDefend(RegionDefendEvent event)
    {
        
    }
}