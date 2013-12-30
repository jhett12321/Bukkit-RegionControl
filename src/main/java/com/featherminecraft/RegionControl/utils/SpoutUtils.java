package com.featherminecraft.RegionControl.utils;

import org.bukkit.entity.Player;

import org.getspout.spoutapi.player.SpoutPlayer;

public class SpoutUtils
{
    public boolean isSpoutPlayer(Player player)
    {
        if(((SpoutPlayer) player).isSpoutCraftEnabled())
        {
            return true;
        }
        return false;
    }
}