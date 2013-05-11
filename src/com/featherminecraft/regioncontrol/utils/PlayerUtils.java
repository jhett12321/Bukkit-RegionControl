package com.featherminecraft.regioncontrol.utils;

import org.bukkit.entity.Player;

import com.featherminecraft.regioncontrol.Faction;
import com.featherminecraft.regioncontrol.ServerLogic;

public class PlayerUtils {
    public Faction getPlayerFaction(Player player) {
        //Get Player Permission Group
        String group = null;
        Faction faction = ServerLogic.factions.get(group);
        return faction;
    }
}
