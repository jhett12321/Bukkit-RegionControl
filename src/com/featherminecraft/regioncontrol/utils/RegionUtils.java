package com.featherminecraft.regioncontrol.utils;

import java.util.List;

import org.bukkit.World;
import org.bukkit.entity.Player;

import com.featherminecraft.regioncontrol.CapturableRegion;
import com.featherminecraft.regioncontrol.CaptureTimer;
import com.featherminecraft.regioncontrol.ServerLogic;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class RegionUtils {
    public static void addPlayerToRegion(Player player, CapturableRegion region)
    {
        List<Player> currentplayers = ServerLogic.players.get(region);
        currentplayers.add(player);
        ServerLogic.players.put(region, currentplayers);
    }

    public static void removePlayerFromRegion(Player player, CapturableRegion region)
    {
        List<Player> currentplayers = ServerLogic.players.get(region);
        currentplayers.remove(player);
    }
    
    public int getRegionPlayerCount(CapturableRegion region)
    {
        return ServerLogic.players.get(region).size();
    }
    
    public List<Player> getRegionPlayerList(CapturableRegion region)
    {
        return ServerLogic.players.get(region);
    }
    
    public CaptureTimer getRegionCaptureTimer(CapturableRegion region)
    {
        return ServerLogic.capturetimers.get(region);
    }
    
    public CapturableRegion getCapturableRegion(ProtectedRegion region, World world)
    {
        return ServerLogic.registeredregions.get(world.getName() + "_" + region.getId());
    }
}
