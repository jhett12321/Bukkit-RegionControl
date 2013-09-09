package com.featherminecraft.RegionControl.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import static com.sk89q.worldguard.bukkit.BukkitUtil.toVector;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;
import com.featherminecraft.RegionControl.ClientRunnables;
import com.featherminecraft.RegionControl.Faction;
import com.featherminecraft.RegionControl.RCPlayer;
import com.featherminecraft.RegionControl.RegionControl;
import com.featherminecraft.RegionControl.utils.PlayerUtils;
import com.featherminecraft.RegionControl.utils.RegionUtils;
import com.featherminecraft.RegionControl.utils.Utils;

public class PlayerListener implements Listener {

    BukkitTask clientrunnables;
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        RCPlayer rcPlayer = new RCPlayer(null, null, null); //TODO
        clientrunnables = new ClientRunnables(rcPlayer).runTaskTimer(RegionControl.plugin, 20, 20);
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
                CapturableRegion capturableregion = new RegionUtils().getCapturableRegionFromWorldGuardRegion(region, event.getPlayer().getWorld());
                    if(capturableregion.getRegion().contains(location))
                    {
                        RegionUtils.removePlayerFromRegion(event.getPlayer(), capturableregion);
                }
            }
        }
    }
    
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event)
    {
        Player playerdamager = null;
        Player damagedplayer = null;
        
        Entity damager = event.getDamager();
        Entity damagedentity = event.getEntity();
        
        if((damagedentity instanceof Player))
        {
            damagedplayer = (Player) damagedentity;
        }
        
        if(damager instanceof Player)
        {
            playerdamager = (Player) damager;
        }
        
        else if(damager instanceof Projectile)
        {
            Projectile projectile = (Projectile) damager;
            Entity entitydamager = projectile.getShooter();
            if(entitydamager instanceof Player && entitydamager != null)
            {
                playerdamager = (Player) entitydamager;
            }
        }
        
        if(playerdamager != null && damagedplayer != null)
        {
            PlayerUtils playerutils = new PlayerUtils();
            Faction damagerfaction = playerutils.getPlayerFaction(playerdamager);
            Faction damagedentityfaction = playerutils.getPlayerFaction(damagedplayer);
            if(damagerfaction == damagedentityfaction)
            {
                event.setCancelled(true);
            }
        }
    }
}
