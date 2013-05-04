package com.featherminecraft.regioncontrol.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.featherminecraft.regioncontrol.SpoutClientLogic;
import com.featherminecraft.regioncontrol.events.ChangeRegionEvent;
import com.featherminecraft.regioncontrol.events.ControlPointCaptureEvent;
import com.featherminecraft.regioncontrol.events.RegionCaptureEvent;
import com.featherminecraft.regioncontrol.utils.ServerUtils;
import com.featherminecraft.regioncontrol.utils.SpoutUtils;
import com.featherminecraft.regioncontrol.utils.Utils;

public class GenericListener {
    public class PlayerListener implements Listener {

        @EventHandler(priority = EventPriority.MONITOR)
        public void onChangeRegion(ChangeRegionEvent event) {
            ServerUtils.addPlayerToRegion(event.getPlayer(), event.getNewRegion(), event.getWorld());
            if(event.getOldRegion() != null) //Perhaps the player just joined.
                ServerUtils.removePlayerFromRegion(event.getPlayer(), event.getOldRegion(), event.getWorld());
            
            if(Utils.SpoutAvailable())
            {
                SpoutPlayer player = SpoutManager.getPlayer(event.getPlayer());
                SpoutUtils spoututils = new SpoutUtils();
                Utils utils = new Utils();
                if(player.isSpoutCraftEnabled())
                    spoututils.UpdateLabelText(SpoutClientLogic.regioname, event.getNewRegion().getId().replace("_", " "));
                    spoututils.UpdateTexture(SpoutClientLogic.factionicon, utils.getOwner(event.getNewRegion()));
            }
            
            /*else
             * tabapi
            */
        }
        
        @EventHandler(priority = EventPriority.MONITOR)
        public void onRegionCapture(RegionCaptureEvent event)
        {
            
        }
        
        @EventHandler(priority = EventPriority.MONITOR)
        public void onControlPointCapture(ControlPointCaptureEvent event)
        {
            
        }
    }
}
