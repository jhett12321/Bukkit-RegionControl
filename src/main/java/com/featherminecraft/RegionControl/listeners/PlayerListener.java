package com.featherminecraft.RegionControl.listeners;

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

import com.featherminecraft.RegionControl.Faction;
import com.featherminecraft.RegionControl.RCPlayer;
import com.featherminecraft.RegionControl.ServerLogic;
import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;
import com.featherminecraft.RegionControl.utils.PlayerUtils;
import com.featherminecraft.RegionControl.utils.RegionUtils;

public class PlayerListener implements Listener
{
    
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
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        // Utilities Begin
        PlayerUtils playerUtils = new PlayerUtils();
        RegionUtils regionUtils = new RegionUtils();
        // Utilities End
        
        Player player = event.getPlayer();
        if(player == null)
        {
            // Player is undefined
            return;
        }
        
        Faction faction = playerUtils.getPlayerFaction(player);
        if(faction == null)
        {
            player.kickPlayer("You currently do not belong to a valid faction!");
            return;
        }
        CapturableRegion currentRegion = faction.getFactionSpawnRegion(player.getWorld());
        
        RCPlayer rcPlayer = new RCPlayer(player, faction, currentRegion);
        
        ServerLogic.players.put(player.getName(), rcPlayer);
        regionUtils.addPlayerToRegion(rcPlayer, currentRegion);
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        // Utilities Begin
        PlayerUtils playerUtils = new PlayerUtils();
        RegionUtils regionUtils = new RegionUtils();
        // Utilities End
        
        // If a player is kicked for not being in a faction, they do not have an
        // RCPlayer object
        try
        {
            RCPlayer player = playerUtils.getRCPlayerFromBukkitPlayer(event.getPlayer());
            CapturableRegion currentRegion = player.getCurrentRegion();
            if(currentRegion != null)
            {
                regionUtils.removePlayerFromRegion(player, currentRegion);
            }
            
            player.getClientRunnables().cancel();
            ServerLogic.players.remove(event.getPlayer().getName());
        }
        catch(NullPointerException e)
        {
            return;
        }
    }
    
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event)
    {
        PlayerUtils playerUtils = new PlayerUtils();
        
        Player player = event.getPlayer();
        RCPlayer rcplayer = playerUtils.getRCPlayerFromBukkitPlayer(player);
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
