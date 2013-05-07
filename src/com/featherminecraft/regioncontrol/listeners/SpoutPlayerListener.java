package com.featherminecraft.regioncontrol.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.getspout.spoutapi.event.spout.SpoutCraftEnableEvent;

import com.featherminecraft.regioncontrol.ClientRunnables;
import com.featherminecraft.regioncontrol.SpoutClientLogic;

public class SpoutPlayerListener implements Listener {
    @EventHandler
    public void onSpoutcraftEnable(SpoutCraftEnableEvent event) {
        new SpoutClientLogic(event.getPlayer());
        new ClientRunnables(event.getPlayer().getPlayer());
    }
}