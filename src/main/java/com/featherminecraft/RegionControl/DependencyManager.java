package com.featherminecraft.RegionControl;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import net.milkbowl.vault.permission.Permission;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

public class DependencyManager
{
    //Dependencies
    private static boolean worldGuardAvailable;
    private static boolean protocolLibAvailable;
    private static boolean vaultAvailable;
    
    //Optional Dependencies
    private static boolean spoutCraftAvailable;
    //private static boolean heroesAvailable;
    
    //Dependencies
    private static WorldGuardPlugin worldGuard;
    private static ProtocolManager protocolManager;
    private static Permission permission;
    
    //Optional Dependencies
    //private static Heroes heroes;
    
    protected static boolean init()
    {
        Plugin worldGuard = RegionControl.plugin.getServer().getPluginManager().getPlugin("WorldGuard");
        Plugin protocolLib = RegionControl.plugin.getServer().getPluginManager().getPlugin("ProtocolLib");
        Plugin vault = RegionControl.plugin.getServer().getPluginManager().getPlugin("Vault");

        if(worldGuard == null || !worldGuard.isEnabled())
        {
            worldGuardAvailable = false;
            DependencyManager.worldGuard = null;
        }
        else
        {
            worldGuardAvailable = true;
            DependencyManager.worldGuard = (WorldGuardPlugin) worldGuard;
        }
        
        if(protocolLib == null || !protocolLib.isEnabled())
        {
            protocolLibAvailable = false;
            protocolManager = null;
        }
        else
        {
            protocolLibAvailable = true;
            protocolManager = ProtocolLibrary.getProtocolManager();
        }
        
        if(vault == null || !vault.isEnabled())
        {
            vaultAvailable = false;
            permission = null;
        }
        else
        {
            vaultAvailable = true;
            RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
            if(permissionProvider != null)
            {
                permission = permissionProvider.getProvider();
            }
        }
        
        if(worldGuardAvailable == false || protocolLibAvailable == false || vaultAvailable == false)
        {
            return false;
        }
        
        Plugin spoutCraft = RegionControl.plugin.getServer().getPluginManager().getPlugin("Spout");
        Plugin heroes = RegionControl.plugin.getServer().getPluginManager().getPlugin("Heroes");
        
        if(spoutCraft == null || !spoutCraft.isEnabled())
        {
            spoutCraftAvailable = false;
        }
        else
        {
            spoutCraftAvailable = true;
        }
        
        if(heroes == null || !heroes.isEnabled())
        {
            //heroesAvailable = false;
        }
        else
        {
            //heroesAvailable = true;
            //DependencyManager.heroes = (Heroes) heroes;
        }
        
        return true;
    }
    
    public static WorldGuardPlugin getWorldGuard()
    {
        return worldGuard;
    }
    
    public static ProtocolManager getProtocolManager()
    {
        return protocolManager;
    }

    public static boolean isWorldGuardAvailable()
    {
        return worldGuardAvailable;
    }

    public static boolean isProtocolLibAvailable()
    {
        return protocolLibAvailable;
    }

    public static boolean isVaultAvailable()
    {
        return vaultAvailable;
    }

    public static boolean isSpoutCraftAvailable()
    {
        return spoutCraftAvailable;
    }

    public static boolean isHeroesAvailable()
    {
        //return heroesAvailable;
        return false;
    }

    public static Permission getPermission()
    {
        return permission;
    }

//    public static Heroes getHeroes()
//    {
//        return heroes;
//    }
}