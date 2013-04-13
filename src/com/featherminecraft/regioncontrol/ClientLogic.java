package com.featherminecraft.regioncontrol;

import static com.sk89q.worldguard.bukkit.BukkitUtil.toVector;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.featherminecraft.regioncontrol.events.ChangeRegionEvent;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class ClientLogic {

    public void init(Player player)
    {
        addPlayerRegionWatcher(player);
    }

    public void spoutInit(SpoutPlayer player)
    {
        addPlayerRegionWatcher(player.getPlayer());
    }
    
    public void addPlayerRegionWatcher(Player player)
    {
        WorldGuardPlugin worldGuard = RegionControl.getWorldGuard();
        RegionManager regionmanager;
        Vector currentlocation;
        Vector newlocation;
        ApplicableRegionSet currentregions;
        ApplicableRegionSet newregions;
        ProtectedRegion currentregion = null;
        ProtectedRegion newregion = null;

        regionmanager = worldGuard.getRegionManager(player.getWorld());
        currentlocation = toVector(player.getLocation());
        currentregions = regionmanager.getApplicableRegions(currentlocation);

        if(currentregions != null && currentregions.size() == 1) {
            for(ProtectedRegion region : currentregions)
            {
                if(region.contains(currentlocation))
                    currentregion = region;
            }
        }
        
        ChangeRegionEvent changeregionevent = new ChangeRegionEvent(newregion, currentregion, player);
        Bukkit.getServer().getPluginManager().callEvent(changeregionevent);
        
        for(;;)
        {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }

            if(!player.isOnline())
                break; //Player Disconnected

            regionmanager = worldGuard.getRegionManager(player.getWorld());
            currentlocation = toVector(player.getLocation());
            currentregions = regionmanager.getApplicableRegions(currentlocation);
     
            if(currentregions.size() > 1) {
                continue; //There is an overlapping region.

            } else if(currentregions != null) {
                for(ProtectedRegion region : currentregions)
                {
                    if(region.contains(currentlocation))
                        currentregion = region;
                }
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }

            if(!player.isOnline())
                break; //Player Disconnected during sleep period
            regionmanager = worldGuard.getRegionManager(player.getWorld());
            newlocation = toVector(player.getLocation());
            newregions = regionmanager.getApplicableRegions(newlocation);

            if(newregions.size() > 1)
                continue; //There is an overlapping region.

            for(ProtectedRegion region : newregions)
            {
                if(region.contains(currentlocation))
                    newregion = region;
                else
                    continue; //shouldn't get here
            }
            
            if(newregion != currentregion)
            {
                Bukkit.getServer().getPluginManager().callEvent(changeregionevent);
            }
        }
    }
}
