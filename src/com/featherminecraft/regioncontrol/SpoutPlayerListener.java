package com.featherminecraft.regioncontrol;

import org.bukkit.event.EventHandler;
import org.getspout.spoutapi.event.spout.SpoutCraftEnableEvent;

public class SpoutPlayerListener {
    @EventHandler
    public void onSpoutcraftEnable(SpoutCraftEnableEvent event) {
        event.getPlayer();
    }
}
