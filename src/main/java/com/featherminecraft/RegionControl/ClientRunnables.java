package com.featherminecraft.RegionControl;

import static com.sk89q.worldguard.bukkit.BukkitUtil.toVector;

import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;


import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;
import com.featherminecraft.RegionControl.capturableregion.SpawnPoint;
import com.featherminecraft.RegionControl.events.ChangeRegionEvent;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class ClientRunnables extends BukkitRunnable {

    private RCPlayer player;

    public ClientRunnables(RCPlayer player)
    {
        this.player = player;
        
        World world = player.getBukkitPlayer().getWorld();
        RegionControl.plugin.getLogger().log(Level.INFO, "DEBUG: the player's current world is: " + world.getName());
        
        Faction faction = player.getFaction();
        RegionControl.plugin.getLogger().log(Level.INFO, "DEBUG: the player's current faction is: " + faction.getName());
        
        CapturableRegion spawnRegion = faction.getFactionSpawnRegion(world);
        RegionControl.plugin.getLogger().log(Level.INFO, "DEBUG: the faction's spawn region is: " + spawnRegion.getDisplayName());

        SpawnPoint spawnPoint = faction.getFactionSpawnRegion(world).getSpawnPoint();
        
        if(spawnPoint.getLocation() == null)
        {
            player.getBukkitPlayer().teleport(player.getBukkitPlayer().getWorld().getSpawnLocation());
        }
        else
        {
            player.getBukkitPlayer().teleport(spawnPoint.getLocation());
        }
    }
    
    @Override
    public void run()
    {
        CapturableRegion currentRegion = player.getCurrentRegion();
        
        //Player Region Watcher
        CapturableRegion newregion = null;
        
        Vector newlocation = toVector(player.getBukkitPlayer().getLocation());
        
        Map<String,CapturableRegion> capturableRegions = ServerLogic.capturableRegions;
        for(Entry<String, CapturableRegion> capturableRegion: capturableRegions.entrySet())
        {
        	ProtectedRegion region = capturableRegion.getValue().getRegion();
            if(region.contains(newlocation))
                newregion = capturableRegion.getValue();
        }
        
        if(newregion == null && currentRegion != null)
        {
            Bukkit.getServer().getPluginManager().callEvent(
                    new ChangeRegionEvent(null, currentRegion, player)
                    );
        }
        
        if(newregion != currentRegion && newregion != null)
        {
            Bukkit.getServer().getPluginManager().callEvent(
                    new ChangeRegionEvent(newregion, currentRegion, player)
                    );
        }
        
        if(newregion != currentRegion)
        {
            player.setCurrentRegion(newregion);
        }
    }
}
