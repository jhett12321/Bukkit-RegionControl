package com.featherminecraft.RegionControl;

import static com.sk89q.worldguard.bukkit.BukkitUtil.toVector;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;
import com.featherminecraft.RegionControl.capturableregion.SpawnPoint;
import com.featherminecraft.RegionControl.events.ChangeRegionEvent;

public class ClientRunnables extends BukkitRunnable
{
    private RCPlayer player;
    
    public ClientRunnables(RCPlayer player)
    {
        this.player = player;
        SpawnPoint spawnPoint = player.getFaction().getFactionSpawnRegion(player.getBukkitPlayer().getWorld()).getSpawnPoint();
        
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
        CapturableRegion newRegion = null;
        
        Vector newlocation = toVector(player.getBukkitPlayer().getLocation());
        
        for(CapturableRegion capturableRegion : ServerLogic.capturableRegions.values())
        {
            ProtectedRegion region = capturableRegion.getRegion();
            if(region.contains(newlocation))
            {
                newRegion = capturableRegion;
            }
        }
        
        if(newRegion != currentRegion)
        {
            if(newRegion == null)
            {
                currentRegion.getPlayers().remove(player);
                Bukkit.getServer().getPluginManager().callEvent(new ChangeRegionEvent(null, currentRegion, player));
            }
            
            else if(newRegion != null && currentRegion != null)
            {
                currentRegion.getPlayers().remove(player);
                newRegion.getPlayers().add(player);
                Bukkit.getServer().getPluginManager().callEvent(new ChangeRegionEvent(newRegion, currentRegion, player));
            }
            
            else if(currentRegion == null)
            {
                newRegion.getPlayers().add(player);
                Bukkit.getServer().getPluginManager().callEvent(new ChangeRegionEvent(newRegion, null, player));
            }
            
            player.setCurrentRegion(newRegion);
        }
    }
}