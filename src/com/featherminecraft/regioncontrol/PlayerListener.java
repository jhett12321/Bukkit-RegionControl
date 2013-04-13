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
        ClientLogic clientlogic = new ClientLogic();
        clientlogic.init(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChangeRegion(ChangeRegionEvent event) {
        ServerLogic.addPlayerToRegion(event.getPlayer(), event.getNewRegion());
        ServerLogic.removePlayerFromRegion(event.getPlayer(), event.getOldRegion());
    }
}
