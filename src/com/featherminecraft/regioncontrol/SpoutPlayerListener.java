package com.featherminecraft.regioncontrol;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.getspout.spoutapi.event.spout.SpoutCraftEnableEvent;

public class SpoutPlayerListener implements Listener {
    @EventHandler
    public void onSpoutcraftEnable(SpoutCraftEnableEvent event) {
        SpoutClientLogic spoutclientlogic = new SpoutClientLogic();
        ClientLogic clientlogic = new ClientLogic();
        spoutclientlogic.spoutInit(event.getPlayer());
        clientlogic.init(event.getPlayer().getPlayer());
    }
}