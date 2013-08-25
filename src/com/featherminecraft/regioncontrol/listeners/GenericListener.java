package com.featherminecraft.regioncontrol.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

//import com.featherminecraft.regioncontrol.dynmap.DynmapImpl; TODO
import com.featherminecraft.regioncontrol.events.ChangeRegionEvent;
import com.featherminecraft.regioncontrol.events.ControlPointCaptureEvent;
import com.featherminecraft.regioncontrol.events.ControlPointNeutraliseEvent;
import com.featherminecraft.regioncontrol.events.RegionCaptureEvent;
import com.featherminecraft.regioncontrol.events.RegionDefendEvent;
import com.featherminecraft.regioncontrol.utils.RegionUtils;


public class GenericListener {
    public class PlayerListener implements Listener {

        @EventHandler(priority = EventPriority.MONITOR)
        public void onChangeRegion(ChangeRegionEvent event) {
            RegionUtils.addPlayerToRegion(event.getPlayer(), event.getNewRegion());
            if(event.getOldRegion() != null) //Perhaps the player just joined.
                RegionUtils.removePlayerFromRegion(event.getPlayer(), event.getOldRegion());
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
            event.getControlPoint().setOwner(null);
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onControlPointCapture(ControlPointCaptureEvent event)
        {
            event.getControlpoint().setOwner(event.getOwner());
        }
    }
}
