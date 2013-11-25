package com.featherminecraft.RegionControl;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.featherminecraft.RegionControl.listeners.GenericListener;
import com.featherminecraft.RegionControl.listeners.PlayerListener;
import com.featherminecraft.RegionControl.listeners.SpoutPlayerListener;
import com.featherminecraft.RegionControl.spout.SpoutClientLogic;
import com.featherminecraft.RegionControl.utils.Utils;

public final class RegionControl extends JavaPlugin {

    public static Permission permission;
    public static RegionControl plugin;
    public static boolean isfirstrun;

    @Override
    public void onDisable() {
        new Config().saveAll();
    }

    @Override
    public void onEnable() {
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
        if (!Utils.WorldGuardAvailable()) {
            setEnabled(false);
            // Disable plugin due to missing dependency.
        }

        if (!Utils.VaultAvailable()) {
            setEnabled(false);
        }

        // Server Setup
        Config config = new Config();
        config.reloadMainConfig();
        config.reloadDataFile();
        setupPermissions();
        if (!ServerLogic.init()) {
            setEnabled(false);
        }

        pm.registerEvents(new PlayerListener(), this);
        pm.registerEvents(new GenericListener(), this);

        if (Utils.SpoutAvailable()) {
            SpoutClientLogic.init();
            pm.registerEvents(new SpoutPlayerListener(), this);
        }
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> permissionProvider = getServer()
                .getServicesManager().getRegistration(Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }
}
