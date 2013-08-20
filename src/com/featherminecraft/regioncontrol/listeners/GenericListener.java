package com.featherminecraft.regioncontrol.listeners;

import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.featherminecraft.regioncontrol.capturableregion.ControlPoint;
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
            event.getCapturableRegion().setOwner(event.getNewOwner());
            List<ControlPoint> controlpoints = event.getCapturableRegion().getControlpoints();
            for(ControlPoint controlpoint  : controlpoints)
            {
                controlpoint.setOwner(event.getNewOwner());
            }
            
            event.getCapturableRegion().setExpectedCaptureTime(null);
            //DynmapImpl.WorldGuardUpdate().run(); TODO
        }
        
        @EventHandler(priority = EventPriority.MONITOR)
        public void onRegionDefend(RegionDefendEvent event)
        {
            event.getCapturableRegion().setExpectedCaptureTime(null);
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
        
        //Client Mod Implementation
        /*
        @EventHandler(priority = EventPriority.MONITOR)
        public void onCaptureTimeChange(CaptureTimeChangeEvent event)
        {

            for(Player player : ServerLogic.players.get(event.getRegion()))
            {
                 String tosend = event.getExpectedCaptureTime().toString();
                 byte[] bytestosend = tosend.getBytes();
                 player.sendPluginMessage( RegionControl.plugin, "regioncontrol", bytestosend );
                 ("DEBUG: Sent timer to client.");
            }
            
        }
        */
    }
}
