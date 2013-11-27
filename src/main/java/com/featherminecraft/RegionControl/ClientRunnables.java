package com.featherminecraft.RegionControl;

import static com.sk89q.worldguard.bukkit.BukkitUtil.toVector;

import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;
import com.featherminecraft.RegionControl.capturableregion.SpawnPoint;
import com.featherminecraft.RegionControl.events.ChangeRegionEvent;
import com.featherminecraft.RegionControl.utils.RegionUtils;

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
        // Utilities Begin
        RegionUtils regionUtils = new RegionUtils();
        // Utilities End
        
        CapturableRegion currentRegion = player.getCurrentRegion();
        CapturableRegion newRegion = null;
        
        Vector newlocation = toVector(player.getBukkitPlayer().getLocation());
        
        for(Entry<String, CapturableRegion> capturableRegion : ServerLogic.capturableRegions.entrySet())
        {
            ProtectedRegion region = capturableRegion.getValue().getRegion();
            if(region.contains(newlocation))
            {
                newRegion = capturableRegion.getValue();
            }
        }
        
        if(newRegion != currentRegion)
        {
            if(newRegion == null)
            {
                regionUtils.removePlayerFromRegion(player, currentRegion);
                Bukkit.getServer().getPluginManager().callEvent(new ChangeRegionEvent(null, currentRegion, player));
            }
            
            else if(newRegion != null && currentRegion != null)
            {
                regionUtils.removePlayerFromRegion(player, currentRegion);
                regionUtils.addPlayerToRegion(player, newRegion);
                Bukkit.getServer().getPluginManager().callEvent(new ChangeRegionEvent(newRegion, currentRegion, player));
            }
            
            else if(currentRegion == null)
            {
                regionUtils.addPlayerToRegion(player, newRegion);
                Bukkit.getServer().getPluginManager().callEvent(new ChangeRegionEvent(newRegion, null, player));
            }
            
            player.setCurrentRegion(newRegion);
        }
    }
}
