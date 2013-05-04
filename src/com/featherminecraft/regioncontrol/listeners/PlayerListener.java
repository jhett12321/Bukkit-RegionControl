package com.featherminecraft.regioncontrol.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import static com.sk89q.worldguard.bukkit.BukkitUtil.toVector;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import com.featherminecraft.regioncontrol.ClientRunnables;
import com.featherminecraft.regioncontrol.RegionControl;
import com.featherminecraft.regioncontrol.utils.ServerUtils;
import com.featherminecraft.regioncontrol.utils.Utils;

public class PlayerListener implements Listener {

    BukkitTask clientrunnables;
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        clientrunnables = new ClientRunnables(player).runTaskTimer(RegionControl.plugin, 20, 20);
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        clientrunnables.cancel();
        
        Vector location = toVector(event.getPlayer().getLocation());
        WorldGuardPlugin worldguard = Utils.getWorldGuard();
        RegionManager regionmanager = worldguard.getRegionManager(event.getPlayer().getWorld());
        ApplicableRegionSet applicableregions = regionmanager.getApplicableRegions(location);
        
        if(applicableregions != null && applicableregions.size() == 1) {
            for(ProtectedRegion region : applicableregions)
            {
                if(region.contains(location))
                    ServerUtils.removePlayerFromRegion(event.getPlayer(), region, event.getPlayer().getWorld());
            }
        }
    }
}
