package com.featherminecraft.RegionControl;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;
import com.featherminecraft.RegionControl.spout.SpoutClientLogic;

public class RCPlayer {
    
    private String playerName;
    private CapturableRegion currentRegion;
    private Faction faction;
    private Boolean hasSpout = false;
    private Location respawnLocation;
    private BukkitTask clientRunnables;
    
    //Player Classes/Runnables
    private SpoutClientLogic spoutClientLogic;

    public RCPlayer(Player player,Faction faction, CapturableRegion currentRegion)
    {
        this.playerName = player.getName();
        this.faction = faction;
        this.currentRegion = currentRegion;
        
        clientRunnables = new ClientRunnables(this).runTaskTimer(RegionControl.plugin, 20, 20);
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

    public Boolean getHasSpout() {
        return hasSpout;
    }

    public void setHasSpout(Boolean hasSpout) {
        this.hasSpout = hasSpout;
    }

    public SpoutClientLogic getSpoutClientLogic() {
        return spoutClientLogic;
    }

    public void setSpoutClientLogic(SpoutClientLogic spoutClientLogic) {
        this.spoutClientLogic = spoutClientLogic;
    }

    public Location getRespawnLocation() {
        return respawnLocation;
    }

    public void setRespawnLocation(Location respawnLocation) {
        this.respawnLocation = respawnLocation;
    }

    public BukkitTask getClientRunnables() {
        return clientRunnables;
    }
}
