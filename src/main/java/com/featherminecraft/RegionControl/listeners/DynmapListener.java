package com.featherminecraft.RegionControl.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.featherminecraft.RegionControl.DependencyManager;
import com.featherminecraft.RegionControl.api.events.ControlPointCaptureEvent;
import com.featherminecraft.RegionControl.api.events.ControlPointDefendEvent;
import com.featherminecraft.RegionControl.api.events.RegionCaptureEvent;
import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;
import com.featherminecraft.RegionControl.dynmap.DynmapImpl;

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
        DynmapImpl.updateRegion(event.getRegion());
        for(CapturableRegion adjacentRegion : event.getRegion().getAdjacentRegions())
        {
            DynmapImpl.updateLatticeLink(event.getRegion(), adjacentRegion);
        }
        
        DependencyManager.getDynmapAPI().sendBroadcastToWeb("Broadcast", ChatColor.YELLOW + event.getNewOwner().getDisplayName() + " have captured " + event.getRegion().getDisplayName() + "!");
    }
}
