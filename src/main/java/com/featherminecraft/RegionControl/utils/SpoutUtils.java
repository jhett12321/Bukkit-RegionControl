package com.featherminecraft.RegionControl.utils;

import org.bukkit.entity.Player;

import org.getspout.spoutapi.player.SpoutPlayer;

import com.featherminecraft.RegionControl.RCPlayer;

public class SpoutUtils
{
    public static boolean isSpoutPlayer(Player player)
    {
        SpoutPlayer sPlayer = (SpoutPlayer) player;
        return sPlayer.isSpoutCraftEnabled();
    }
    
    public static boolean isSpoutPlayer(RCPlayer player)
    {
        SpoutPlayer sPlayer = (SpoutPlayer) player.getBukkitPlayer();
        return sPlayer.isSpoutCraftEnabled();
    }
}
