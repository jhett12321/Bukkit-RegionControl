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
    public boolean canCapture(CapturableRegion region, RCPlayer player)
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
    public List<SpawnPoint> getAvailableSpawnPoints(Player player)
    {
        Map<String, CapturableRegion> capturableregions = ServerLogic.capturableRegions;
        List<SpawnPoint> availablespawnpoints = new ArrayList<SpawnPoint>();
        
        for(Entry<String, CapturableRegion> capturableregion : capturableregions.entrySet())
        {
            if(capturableregion.getValue().getOwner() == getPlayerFaction(player))
            {
                availablespawnpoints.add(capturableregion.getValue().getSpawnPoint());
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
     * @return a List containing strings: NO_CONNECTION, CONNECTION_NOT_SECURE, MOUNTED, if Applicable.
     */
    public List<String> getCannotCaptureReasons(CapturableRegion region, RCPlayer player)
    {
        Map<String, Boolean> checks = new HashMap<String, Boolean>();
        checks.put("NO_CONNECTION", true);
        checks.put("CONNECTION_NOT_SECURE", true);
        checks.put("MOUNTED", true);
        // checks.put("INVALID_CLASS", true); //TODO
        
        // No Connection Check
        if(region.getOwner() == getPlayerFaction(player.getBukkitPlayer()))
        {
            checks.put("NO_CONNECTION", false);
        }
        
        else
        {
            for(CapturableRegion adjacentregion : region.getAdjacentRegions())
            {
                if(adjacentregion.getOwner() == getPlayerFaction(player.getBukkitPlayer()))
                {
                    checks.put("NO_CONNECTION", false);
                }
            }
        }
        
        // Connection Not Secure Check
        if(!checks.get("NO_CONNECTION"))
        {
            for(CapturableRegion adjacentregion : region.getAdjacentRegions())
            {
                if(adjacentregion.getOwner() == getPlayerFaction(player.getBukkitPlayer()) && adjacentregion.isSpawnRegion())
                {
                    checks.put("CONNECTION_NOT_SECURE", false);
                }
                else if(adjacentregion.getOwner() == getPlayerFaction(player.getBukkitPlayer()) && adjacentregion.getInfluenceMap().get(adjacentregion.getInfluenceOwner()) >= adjacentregion.getBaseInfluence())
                {
                    checks.put("CONNECTION_NOT_SECURE", false);
                }
            }
        }
        else
        {
            checks.put("CONNECTION_NOT_SECURE", false);
        }
        
        // Player is Mounted Check
        if(!player.getBukkitPlayer().isInsideVehicle())
        {
            checks.put("MOUNTED", false);
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
    
    public CapturableRegion getCurrentRegion(Player player)
    {
        RCPlayer rcPlayer = ServerLogic.players.get(player.getName());
        return rcPlayer.getCurrentRegion();
    }
    
    public Faction getPlayerFaction(Player player)
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
            playerFaction = ServerLogic.factions.get("relkanaForces"); // For Test Clients TODO REMOVE!!
            // TODO set player to use default faction, or kick player (possibly a config option?)
        }
        
        return playerFaction;
    }
    
    public RCPlayer getRCPlayerFromBukkitPlayer(Player player)
    {
        RCPlayer rcPlayer = ServerLogic.players.get(player.getName());
        return rcPlayer;
    }
    
    public void respawnPlayer(RCPlayer player)
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