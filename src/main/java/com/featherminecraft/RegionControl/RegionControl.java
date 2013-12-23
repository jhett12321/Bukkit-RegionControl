package com.featherminecraft.RegionControl;

import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.permission.Permission;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;
import com.featherminecraft.RegionControl.listeners.GenericListener;
import com.featherminecraft.RegionControl.listeners.PlayerListener;
import com.featherminecraft.RegionControl.listeners.SpoutPlayerListener;
import com.featherminecraft.RegionControl.spout.SpoutClientLogic;
import com.featherminecraft.RegionControl.utils.PlayerUtils;
import com.featherminecraft.RegionControl.utils.RegionUtils;
import com.featherminecraft.RegionControl.utils.Utils;

public final class RegionControl extends JavaPlugin
{
    public static ProtocolManager protocolManager;
    public static Permission permission;
    public static RegionControl plugin;
    public static boolean isfirstrun;
    
    @Override
    public void onDisable()
    {
        new Config().saveAll();
    }
    
    @Override
    public void onEnable()
    {
        // Utilities Begin
        PlayerUtils playerUtils = new PlayerUtils();
        RegionUtils regionUtils = new RegionUtils();
        // Utilities End
        
        RegionControl.plugin = this;
        
        // TODO: Migrate from spout to client mod.
        /*
         * // This informs Bukkit that you will send messages through that
         * channel Bukkit.getMessenger().registerOutgoingPluginChannel( this,
         * "regioncontrol");
         * Bukkit.getMessenger().registerIncomingPluginChannel( this,
         * "regioncontrol", new ClientLogic() );
         */
        
        PluginManager pm = getServer().getPluginManager();
        if(!Utils.WorldGuardAvailable())
        {
            setEnabled(false);
        }
        
        if(!Utils.VaultAvailable())
        {
            setEnabled(false);
        }
        
        if(!Utils.ProtocolLibAvailable())
        {
            setEnabled(false);
        }
        
        // Server Setup
        Config config = new Config();
        config.reloadMainConfig();
        config.reloadDataFile();
        setupPermissions();
        setupProtocolManager();
        if(!ServerLogic.init())
        {
            setEnabled(false);
        }
        
        pm.registerEvents(new PlayerListener(), this);
        pm.registerEvents(new GenericListener(), this);
        
        if(Utils.SpoutAvailable())
        {
            SpoutClientLogic.init();
            pm.registerEvents(new SpoutPlayerListener(), this);
        }
        
        //Register Command Handler (NYI - TODO)
        //getCommand("regioncontrol").setExecutor(new CommandHandler(isfirstrun));
        
        //Server may have been reloaded, so setup all current online players.
        if(!Utils.SpoutAvailable())
        {
            for(Player player : this.getServer().getOnlinePlayers())
            {
                Faction faction = playerUtils.getPlayerFaction(player);
                CapturableRegion currentRegion = faction.getFactionSpawnRegion(player.getWorld());
                
                RCPlayer rcPlayer = new RCPlayer(player, faction, currentRegion);
                
                ServerLogic.players.put(player.getName(), rcPlayer);
                regionUtils.addPlayerToRegion(rcPlayer, currentRegion);
            }
        }
    }
    
    private void setupProtocolManager()
    {
        protocolManager = ProtocolLibrary.getProtocolManager();
    }

    private boolean setupPermissions()
    {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(Permission.class);
        if(permissionProvider != null)
        {
            permission = permissionProvider.getProvider();
        }
        return(permission != null);
    }
}
