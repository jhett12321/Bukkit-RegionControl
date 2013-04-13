package com.featherminecraft.regioncontrol;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.getspout.spoutapi.event.spout.SpoutCraftEnableEvent;

public class SpoutPlayerListener implements Listener {
    @EventHandler
    public void onSpoutcraftEnable(SpoutCraftEnableEvent event) {
        ClientLogic clientlogic = new ClientLogic();
        clientlogic.spoutInit(event.getPlayer());
    }
}
