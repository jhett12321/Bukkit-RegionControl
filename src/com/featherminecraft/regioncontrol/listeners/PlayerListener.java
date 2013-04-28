package com.featherminecraft.regioncontrol.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.featherminecraft.regioncontrol.ClientLogic;
import com.featherminecraft.regioncontrol.events.ChangeRegionEvent;
import com.featherminecraft.regioncontrol.utils.ServerUtils;

public class PlayerListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        ClientLogic clientlogic = new ClientLogic();
        clientlogic.init(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChangeRegion(ChangeRegionEvent event) {
        ServerUtils.addPlayerToRegion(event.getPlayer(), event.getNewRegion(), event.getWorld());
        if(event.getOldRegion() != null) //Perhaps the player just joined.
            ServerUtils.removePlayerFromRegion(event.getPlayer(), event.getOldRegion(), event.getWorld());
    }
}
