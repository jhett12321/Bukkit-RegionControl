package com.featherminecraft.RegionControl;

import static com.sk89q.worldguard.bukkit.BukkitUtil.toVector;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import com.featherminecraft.RegionControl.api.events.ChangeRegionEvent;
import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;

public class ClientLogic
{
    public static void init()
    {
        BukkitTask runnable = new BukkitRunnable()
        {
            @Override
            public void run()
            {
                for(RCPlayer player : ServerLogic.players.values())
                {
                    CalculateCurrentRegion(player);
                }
            }
            
        }.runTaskTimer(RegionControl.plugin, 10, 10);
        
        ServerLogic.addServerRunnable(runnable);
    }
    
    private static void CalculateCurrentRegion(RCPlayer player)
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