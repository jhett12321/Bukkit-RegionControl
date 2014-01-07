package com.featherminecraft.RegionControl;

import org.bukkit.plugin.Plugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import net.milkbowl.vault.permission.Permission;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

import com.herocraftonline.heroes.Heroes;

public final class DependencyManager
{
    public static boolean areDependenciesAvailable()
    {
        return isWorldGuardAvailable() && isVaultAvailable() && isProtocolLibAvailable();
    }
    
    public static Heroes getHeroes()
    {
        if(isHeroesAvailable())
        {
            return (Heroes) RegionControl.plugin.getServer().getPluginManager().getPlugin("Heroes");
        }
        
        return null;
    }
    
    public static Permission getPermission()
    {
        return RegionControl.plugin.getServer().getServicesManager().getRegistration(Permission.class).getProvider();
    }
    
    public static ProtocolManager getProtocolManager()
    {
        if(isProtocolLibAvailable())
        {
            return ProtocolLibrary.getProtocolManager();
        }
        
        return null;
    }
    
    public static WorldGuardPlugin getWorldGuard()
    {
        if(isWorldGuardAvailable())
        {
            return (WorldGuardPlugin) RegionControl.plugin.getServer().getPluginManager().getPlugin("WorldGuard");
        }
        
        return null;
    }
    
    public static boolean isHeroesAvailable()
    {
        return isOptionalPluginAvailable(RegionControl.plugin.getServer().getPluginManager().getPlugin("Heroes"), "hooks.heroes.enabled");
    }
    
    public static boolean isProtocolLibAvailable()
    {
        return isPluginAvailable(RegionControl.plugin.getServer().getPluginManager().getPlugin("ProtocolLib"));
    }
    
    public static boolean isSpoutCraftAvailable()
    {
        return isOptionalPluginAvailable(RegionControl.plugin.getServer().getPluginManager().getPlugin("Spout"), "hooks.spout.enabled");
    }
    
    public static boolean isVaultAvailable()
    {
        return isPluginAvailable(RegionControl.plugin.getServer().getPluginManager().getPlugin("Vault"));
    }
    
    public static boolean isWorldGuardAvailable()
    {
        return isPluginAvailable(RegionControl.plugin.getServer().getPluginManager().getPlugin("WorldGuard"));
    }
    
    private static boolean isOptionalPluginAvailable(Plugin plugin, String mainConfigPath)
    {
        if(plugin == null || !plugin.isEnabled() || !Config.getMainConfig().getBoolean(mainConfigPath))
        {
            return false;
        }
        
        return true;
    }
    
    private static boolean isPluginAvailable(Plugin plugin)
    {
        if(plugin == null || !plugin.isEnabled())
        {
            return false;
        }
        
        return true;
    }
}