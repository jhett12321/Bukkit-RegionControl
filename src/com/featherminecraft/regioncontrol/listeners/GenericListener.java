package com.featherminecraft.regioncontrol.listeners;

import java.util.List;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

//TODO: Migrate these imports:
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;
//

import com.featherminecraft.regioncontrol.ControlPoint;
import com.featherminecraft.regioncontrol.SpoutClientLogic;
import com.featherminecraft.regioncontrol.events.ChangeRegionEvent;
import com.featherminecraft.regioncontrol.events.ControlPointCaptureEvent;
import com.featherminecraft.regioncontrol.events.RegionCaptureEvent;
import com.featherminecraft.regioncontrol.events.RegionDefendEvent;
import com.featherminecraft.regioncontrol.utils.RegionUtils;
import com.featherminecraft.regioncontrol.utils.SpoutUtils;
import com.featherminecraft.regioncontrol.utils.Utils;

public class GenericListener {
    public class PlayerListener implements Listener {

        @EventHandler(priority = EventPriority.MONITOR)
        public void onChangeRegion(ChangeRegionEvent event) {
            RegionUtils.addPlayerToRegion(event.getPlayer(), event.getNewRegion());
            if(event.getOldRegion() != null) //Perhaps the player just joined.
                RegionUtils.removePlayerFromRegion(event.getPlayer(), event.getOldRegion());
            
            if(Utils.SpoutAvailable())
            {
                SpoutPlayer player = SpoutManager.getPlayer(event.getPlayer());
                SpoutUtils spoututils = new SpoutUtils();
                if(player.isSpoutCraftEnabled())
                    spoututils.UpdateLabelText(SpoutClientLogic.regioname, event.getNewRegion().getRegion().getId().replace("_", " "));
                    spoututils.UpdateTexture(SpoutClientLogic.factionicon, event.getNewRegion().getOwner().getName());
            }
            
            /*else
             * Maybe Scoreboard/Tab Api Implementation?
            */
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
        }
        
        @EventHandler(priority = EventPriority.MONITOR)
        public void onRegionDefend(RegionDefendEvent event)
        {
            event.getCapturableRegion().setExpectedCaptureTime(null);
        }
        
        @EventHandler(priority = EventPriority.MONITOR)
        public void onControlPointCapture(ControlPointCaptureEvent event)
        {
            
        }
    }
}
