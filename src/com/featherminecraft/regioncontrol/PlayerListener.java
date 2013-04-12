package com.featherminecraft.regioncontrol;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.featherminecraft.regioncontrol.events.ChangeRegionEvent;

public class PlayerListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerRegionManager playerregionmanager = new PlayerRegionManager();
        playerregionmanager.addPlayerRegionWatcher(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEnterRegion(ChangeRegionEvent event) {
//        Player player = event.getPlayer();
        
    }
}
