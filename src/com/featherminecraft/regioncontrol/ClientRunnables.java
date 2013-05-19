package com.featherminecraft.regioncontrol;

import static com.sk89q.worldguard.bukkit.BukkitUtil.toVector;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.featherminecraft.regioncontrol.events.ChangeRegionEvent;
import com.featherminecraft.regioncontrol.utils.RegionUtils;
import com.featherminecraft.regioncontrol.utils.Utils;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class ClientRunnables extends BukkitRunnable {

    private Player player;
    private CapturableRegion currentregion;

    public ClientRunnables(Player player)
    {
        this.player = player;
        //TODO Replace with Faction Spawn points
        if(player.getBedSpawnLocation() != null)
            player.teleport(player.getBedSpawnLocation());
        else
            player.teleport(player.getWorld().getSpawnLocation());
    }
    
    @Override
    public void run()
    {
        CapturableRegion newregion = null;
        
        WorldGuardPlugin worldguard = Utils.getWorldGuard();
        RegionManager regionmanager = worldguard.getRegionManager(player.getWorld());
        Vector newlocation = toVector(player.getLocation());
        ApplicableRegionSet newregions = regionmanager.getApplicableRegions(newlocation);
        
        if(newregions != null && newregions.size() == 1) {
            for(ProtectedRegion region : newregions)
            {
                CapturableRegion capturableregion = new RegionUtils().getCapturableRegion(region, player.getWorld());
                if(capturableregion.getRegion().contains(newlocation))
                    newregion = capturableregion;
            }
        }
        
        if(newregion != currentregion && currentregion != null && newregion != null)
        {
            ChangeRegionEvent changeregionevent = new ChangeRegionEvent(newregion, currentregion, player, player.getWorld());
            Bukkit.getServer().getPluginManager().callEvent(changeregionevent);
        }
        
        if(newregion != null)
        {
            currentregion = newregion;
        }
    }
}
