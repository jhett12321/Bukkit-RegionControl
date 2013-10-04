package com.featherminecraft.RegionControl.listeners;

import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.featherminecraft.RegionControl.RCPlayer;
import com.featherminecraft.RegionControl.events.ChangeRegionEvent;
import com.featherminecraft.RegionControl.events.ControlPointCaptureEvent;
import com.featherminecraft.RegionControl.events.ControlPointNeutraliseEvent;
import com.featherminecraft.RegionControl.events.RegionCaptureEvent;
import com.featherminecraft.RegionControl.events.RegionDefendEvent;

public class GenericListener {
    public class PlayerListener implements Listener {

        @EventHandler(priority = EventPriority.MONITOR)
        public void onChangeRegion(ChangeRegionEvent event) 
        {
            RCPlayer player = event.getPlayer();
            List<RCPlayer> oldPlayerList = event.getOldRegion().getPlayers();
            List<RCPlayer> newPlayerList = event.getNewRegion().getPlayers();
            
            oldPlayerList.remove(player);
            newPlayerList.add(player);
        }
        
        @EventHandler(priority = EventPriority.MONITOR)
        public void onRegionCapture(RegionCaptureEvent event)
        {
            
        }
        
        @EventHandler(priority = EventPriority.MONITOR)
        public void onRegionDefend(RegionDefendEvent event)
        {
            
        }
        
        @EventHandler(priority = EventPriority.MONITOR)
        public void onControlPointNeutralise(ControlPointNeutraliseEvent event)
        {
            
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onControlPointCapture(ControlPointCaptureEvent event)
        {
            
        }
    }
}
