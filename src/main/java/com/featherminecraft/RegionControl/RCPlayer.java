package com.featherminecraft.RegionControl;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;

public class RCPlayer {
    
    private String playerName;
    private CapturableRegion currentRegion;
    private Faction faction;

    public RCPlayer(Player player,Faction faction, CapturableRegion currentRegion)
    {
        this.playerName = player.getName();
        this.faction = faction;
        this.currentRegion = currentRegion;
    }
    
    public Player getBukkitPlayer()
    {
        return Bukkit.getPlayer(playerName);
    }

    public CapturableRegion getCurrentRegion() {
        return currentRegion;
    }

    public void setCurrentRegion(CapturableRegion currentRegion) {
        this.currentRegion = currentRegion;
    }

    public Faction getFaction() {
        return faction;
    }

    public void setFaction(Faction faction) {
        this.faction = faction;
    }
}
