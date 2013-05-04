package com.featherminecraft.regioncontrol.utils;

import java.util.List;

import org.bukkit.World;
import org.bukkit.entity.Player;

import com.featherminecraft.regioncontrol.ServerLogic;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class ServerUtils {
    public static void addPlayerToRegion(Player player, ProtectedRegion region, World world)
    {
        List<Player> currentplayers = ServerLogic.players.get(world.getName() + region.getId());
        currentplayers.add(player);
        ServerLogic.players.put(world.getName() + region.getId(), currentplayers);
    }

    public static void removePlayerFromRegion(Player player, ProtectedRegion region, World world)
    {
        List<Player> currentplayers = ServerLogic.players.get(world.getName() + region.getId());
        currentplayers.remove(player);
    }
    
    public static int getRegionPlayerCount(ProtectedRegion region, World world)
    {
        return ServerLogic.players.get(world.getName() + region.getId()).size();
    }
    
    public static List<Player> getRegionPlayerList(ProtectedRegion region, World world)
    {
        return ServerLogic.players.get(world.getName() + region.getId());
    }
    
    public static int getRegionCaptureTimer(ProtectedRegion region, World world)
    {
        return ServerLogic.capturetimers.get(world.getName() + region.getId());
    }
    
    public static void setRegionCaptureTimer(ProtectedRegion region, World world, int newvalue)
    {
        ServerLogic.capturetimers.put(world.getName() + region.getId(), newvalue);
    }
}
