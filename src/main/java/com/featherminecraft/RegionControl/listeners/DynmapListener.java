package com.featherminecraft.RegionControl.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.featherminecraft.RegionControl.DependencyManager;
import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;
import com.featherminecraft.RegionControl.dynmap.DynmapImpl;
import com.featherminecraft.RegionControl.events.ControlPointCaptureEvent;
import com.featherminecraft.RegionControl.events.ControlPointDefendEvent;
import com.featherminecraft.RegionControl.events.RegionCaptureEvent;

public class DynmapListener implements Listener
{
    @EventHandler
    public void onControlPointCapture(ControlPointCaptureEvent event)
    {
        DynmapImpl.updateRegionControlPoints(event.getRegion());
    }
    
    @EventHandler
    public void onControlPointDefend(ControlPointDefendEvent event)
    {
        DynmapImpl.updateRegionControlPoints(event.getRegion());
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onRegionCapture(RegionCaptureEvent event)
    {
        DynmapImpl.updateRegion(event.getCapturableRegion());
        for(CapturableRegion adjacentRegion : event.getCapturableRegion().getAdjacentRegions())
        {
            DynmapImpl.updateLatticeLink(event.getCapturableRegion(), adjacentRegion);
        }
        
        DependencyManager.getDynmapAPI().sendBroadcastToWeb("Broadcast", ChatColor.YELLOW + event.getNewOwner().getDisplayName() + " have captured " + event.getCapturableRegion().getDisplayName() + "!");
    }
}
