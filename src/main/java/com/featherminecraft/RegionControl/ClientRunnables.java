package com.featherminecraft.RegionControl;

import static com.sk89q.worldguard.bukkit.BukkitUtil.toVector;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;


import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;
import com.featherminecraft.RegionControl.capturableregion.SpawnPoint;
import com.featherminecraft.RegionControl.events.ChangeRegionEvent;
import com.featherminecraft.RegionControl.utils.RegionUtils;
import com.featherminecraft.RegionControl.utils.Utils;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;


public class ClientRunnables extends BukkitRunnable {

    private RCPlayer player;
    private CapturableRegion currentregion;

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
        //Player Region Watcher
        CapturableRegion newregion = null;
        
        WorldGuardPlugin worldguard = Utils.getWorldGuard();
        RegionManager regionmanager = worldguard.getRegionManager(player.getBukkitPlayer().getWorld());
        Vector newlocation = toVector(player.getBukkitPlayer().getLocation());
        ApplicableRegionSet newregions = regionmanager.getApplicableRegions(newlocation);
        
        if(newregions != null && newregions.size() == 1) {
            for(ProtectedRegion region : newregions)
            {
                CapturableRegion capturableregion = new RegionUtils().getCapturableRegionFromWorldGuardRegion(region, player.getBukkitPlayer().getWorld());
                if(capturableregion.getRegion().contains(newlocation))
                    newregion = capturableregion;
            }
        }
        
        if(newregion != currentregion && currentregion != null && newregion != null)
        {
            ChangeRegionEvent changeregionevent = new ChangeRegionEvent(newregion, currentregion, player.getBukkitPlayer(), player.getBukkitPlayer().getWorld());
            Bukkit.getServer().getPluginManager().callEvent(changeregionevent);
        }
        
        if(newregion != null)
        {
            currentregion = newregion;
        }
    }
}
