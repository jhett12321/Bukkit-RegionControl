package com.featherminecraft.RegionControl.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.entity.Player;

import com.comphenix.protocol.Packets;
import com.comphenix.protocol.events.PacketContainer;

import com.featherminecraft.RegionControl.DependencyManager;
import com.featherminecraft.RegionControl.Faction;
import com.featherminecraft.RegionControl.RCPlayer;
import com.featherminecraft.RegionControl.ServerLogic;
import com.featherminecraft.RegionControl.capturableregion.CannotCaptureReason;
import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;
import com.featherminecraft.RegionControl.capturableregion.SpawnPoint;

@SuppressWarnings("deprecation")
public class PlayerUtils
{
    /**
     * Checks whether a player can capture a certain Region.
     * 
     * @param region
     *            - A CapturableRegion.
     * @param player
     *            - The Player to be checked
     * @return Boolean whether a player can capture the Region
     */
    public static boolean canCapture(CapturableRegion region, RCPlayer player)
    {
        if(getCannotCaptureReasons(region, player).size() > 0)
        {
            return false;
        }
        
        return true;
    }
    
    /**
     * Retrieves a list of available SpawnPoints (a RegionControl Object with a Bukkit location) for a Bukkit Player
     * 
     * @param player
     *            - The Player to check for available SpawnPoints.
     * @return The available SpawnPoints for the provided player.
     */
    public static List<SpawnPoint> getAvailableSpawnPoints(Player player)
    {
        List<SpawnPoint> availablespawnpoints = new ArrayList<SpawnPoint>();
        
        for(CapturableRegion capturableregion : ServerLogic.capturableRegions.values())
        {
            if(capturableregion.getOwner() == getPlayerFaction(player))
            {
                availablespawnpoints.add(capturableregion.getSpawnPoint());
            }
        }
        
        return availablespawnpoints;
    }
    
    /**
     * Gets the reason/s a player cannot capture a region, if any.
     * 
     * @param player
     *            - The player that cannot capture the region.
     * @param region
     *            - The region the player cannot capture.
     * @return a List containing strings explaining why the player cannot capture this region.
     */
    public static List<String> getCannotCaptureReasons(CapturableRegion region, RCPlayer player)
    {
        Map<String, Boolean> checks = new HashMap<String, Boolean>();
        checks.put(CannotCaptureReason.NO_CONNECTION, true);
        checks.put(CannotCaptureReason.CONNECTION_NOT_SECURE, true);
        checks.put(CannotCaptureReason.MOUNTED, true);
        // checks.put("INVALID_CLASS", true); //TODO
        
        // No Connection Check
        if(region.getOwner() == player.getFaction() || region.isBeingCaptured())
        {
            checks.put(CannotCaptureReason.NO_CONNECTION, false);
        }
        
        else
        {
            for(CapturableRegion adjacentregion : region.getAdjacentRegions())
            {
                if(adjacentregion.getOwner() == player.getFaction())
                {
                    checks.put(CannotCaptureReason.NO_CONNECTION, false);
                }
            }
        }
        
        // Connection Not Secure Check
        if(!checks.get(CannotCaptureReason.NO_CONNECTION))
        {
            for(CapturableRegion adjacentregion : region.getAdjacentRegions())
            {
                if(adjacentregion.getOwner() == player.getFaction() && adjacentregion.isSpawnRegion())
                {
                    checks.put(CannotCaptureReason.CONNECTION_NOT_SECURE, false);
                }
                else if(adjacentregion.getOwner() == player.getFaction() && adjacentregion.getInfluenceOwner() != null && adjacentregion.getInfluenceMap().get(adjacentregion.getInfluenceOwner()) >= adjacentregion.getBaseInfluence())
                {
                    checks.put(CannotCaptureReason.CONNECTION_NOT_SECURE, false);
                }
            }
        }
        else
        {
            checks.put(CannotCaptureReason.CONNECTION_NOT_SECURE, false);
        }
        
        // Player is Mounted Check
        if(!player.getBukkitPlayer().isInsideVehicle())
        {
            checks.put(CannotCaptureReason.MOUNTED, false);
        }
        
        List<String> reasons = new ArrayList<String>();
        for(Entry<String, Boolean> check : checks.entrySet())
        {
            if(check.getValue())
            {
                reasons.add(check.getKey());
            }
        }
        
        return reasons;
    }
    
    public static CapturableRegion getCurrentRegion(Player player)
    {
        RCPlayer rcPlayer = ServerLogic.players.get(player.getName());
        return rcPlayer.getCurrentRegion();
    }
    
    public static Faction getPlayerFaction(Player player)
    {
        String group = DependencyManager.getPermission().getPrimaryGroup(player);
        
        Faction playerFaction = null;
        
        for(Entry<String, Faction> faction : ServerLogic.factions.entrySet())
        {
            if(faction.getValue().getPermissionGroup().equals(group))
            {
                playerFaction = faction.getValue();
            }
        }
        
        if(playerFaction == null)
        {
            // TODO set player to use default faction, or kick player (possibly a config option?)
        }
        
        return playerFaction;
    }
    
    public static RCPlayer getRCPlayerFromBukkitPlayer(Player player)
    {
        RCPlayer rcPlayer = ServerLogic.players.get(player.getName());
        return rcPlayer;
    }
    
    public static void respawnPlayer(RCPlayer player)
    {
        PacketContainer respawn = null;
        
        try
        {
            Class.forName("com.comphenix.protocol.PacketType");
            respawn = DependencyManager.getProtocolManager().createPacket(com.comphenix.protocol.PacketType.Play.Client.CLIENT_COMMAND);
        }
        catch(ClassNotFoundException e)
        {
            respawn = DependencyManager.getProtocolManager().createPacket(Packets.Client.CLIENT_COMMAND);
        }
        
        if(respawn != null)
        {
            respawn.getIntegers().write(0, 1);
            
            try
            {
                DependencyManager.getProtocolManager().recieveClientPacket(player.getBukkitPlayer(), respawn);
            }
            catch(InvocationTargetException | IllegalAccessException e)
            {
                e.printStackTrace();
            }
        }
    }
}