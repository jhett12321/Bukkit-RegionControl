package com.featherminecraft.RegionControl.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitTask;

import com.featherminecraft.RegionControl.Config;
import com.featherminecraft.RegionControl.Faction;
import com.featherminecraft.RegionControl.RCPlayer;
import com.featherminecraft.RegionControl.ServerLogic;
import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;
import com.featherminecraft.RegionControl.events.ChangeRegionEvent;
import com.featherminecraft.RegionControl.utils.PlayerUtils;

public class PlayerListener implements Listener
{
    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDamage(EntityDamageByEntityEvent event)
    {
        if(Config.getMainConfig().getBoolean("players.enableFriendlyDamage"))
        {
            return;
        }
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
            Faction damagerfaction = PlayerUtils.getPlayerFaction(playerdamager);
            Faction damagedentityfaction = PlayerUtils.getPlayerFaction(damagedplayer);
            if(damagerfaction == damagedentityfaction)
            {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();
        if(player == null)
        {
            // Player is undefined
            return;
        }
        
        Faction faction = PlayerUtils.getPlayerFaction(player);
        if(faction == null)
        {
            /*
             * if(DependencyManager.isSpoutCraftAvailable())
             * {
             * if(spoutUtils.isSpoutPlayer(player))
             * {
             * player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 2400, 0, false));
             * new FactionSelectScreen(player);
             * }
             * }
             */
            
            player.kickPlayer("You currently do not belong to a valid faction!");
            return;
        }
        CapturableRegion currentRegion = faction.getFactionSpawnRegion(player.getWorld());
        
        RCPlayer rcPlayer = new RCPlayer(player, faction, currentRegion);
        
        ServerLogic.players.put(player.getName(), rcPlayer);
        currentRegion.getPlayers().add(rcPlayer);
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        // If a player is kicked for not being in a faction, they do not have an
        // RCPlayer object
        try
        {
            RCPlayer player = PlayerUtils.getRCPlayerFromBukkitPlayer(event.getPlayer());
            CapturableRegion currentRegion = player.getCurrentRegion();
            if(currentRegion != null)
            {
                currentRegion.getPlayers().remove(player);
                Bukkit.getServer().getPluginManager().callEvent(new ChangeRegionEvent(null, currentRegion, player));
            }
            
            for(BukkitTask runnable : player.getClientRunnables().values())
            {
                runnable.cancel();
            }
            ServerLogic.players.remove(event.getPlayer().getName());
            
        }
        catch(NullPointerException e)
        {
            return;
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerRespawnEvent event)
    {
        Player player = event.getPlayer();
        RCPlayer rcplayer = PlayerUtils.getRCPlayerFromBukkitPlayer(player);
        Location respawnLocation = rcplayer.getRespawnLocation();
        if(respawnLocation != null)
        {
            event.setRespawnLocation(respawnLocation);
        }
        
        else
        {
            event.setRespawnLocation(rcplayer.getFaction().getFactionSpawnRegion(player.getWorld()).getSpawnPoint().getLocation());
        }
        
        rcplayer.setRespawnLocation(null);
    }
}