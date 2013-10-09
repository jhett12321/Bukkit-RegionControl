/* Task List
TODO:
    TODO 1. Code Testing
Details:
File/s: All
JIRA Link:
Classification: Improvement
Priority: Major

    TODO 2. Implement onDisable Code
Details:
File/s: RegionControl.java Line 91
JIRA Link: 
Classification: New Feature
Priority: Blocker

    TODO 3. Make sure that static is being used properly in functions.
Details:
File/s: RegionControl.java
JIRA Link: 
Classification: Improvement
Priority: Major

    TODO 4. Implement Dynmap (Or some similar full screen map) to client that shows current region control.
Details:
File/s: Unknown
JIRA Link: 
Classification: New Feature
Priority: Trivial

    TODO 5. Replace main configuration with individual configuration files
Details:
File/s: Config.java All Lines
JIRA Link: 
Classification: Improvement
Priority: Minor

    TODO 6. Map Filters
Details:
Pre-req: Dynmap Implementation
JIRA Link: 
Classification: Improvement
Priority: Trivial

    TODO 7. World Saving - Remember who owned each region after server restart/shutdown.
Details:
File/s: ServerLogic.java
JIRA Link: 
Classification: New Feature
Priority: Critical

ISSUE/S:
 */

package com.featherminecraft.RegionControl;

import java.util.logging.Level;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;


import com.featherminecraft.RegionControl.listeners.PlayerListener;
import com.featherminecraft.RegionControl.listeners.SpoutPlayerListener;
import com.featherminecraft.RegionControl.spout.SpoutClientLogic;
import com.featherminecraft.RegionControl.utils.Utils;

public final class RegionControl extends JavaPlugin {

private PlayerListener playerlistener;
private SpoutPlayerListener spoutplayerlistener;
public static Permission permission;
public static RegionControl plugin;
public static boolean isfirstrun;

    @Override
    public void onEnable() {
        RegionControl.plugin = this;
        
        //TODO: Migrate from spout to client mod.
        /*
        // This informs Bukkit that you will send messages through that channel
        Bukkit.getMessenger().registerOutgoingPluginChannel( this, "regioncontrol");
        Bukkit.getMessenger().registerIncomingPluginChannel( this, "regioncontrol", new ClientLogic() );
        */

        PluginManager pm = getServer().getPluginManager();
        if(!Utils.WorldGuardAvailable())
        {
            setEnabled(false);
            //Disable plugin due to missing dependency.
        }
        
        if(!Utils.VaultAvailable())
        {
            setEnabled(false);
        }

        //Server Setup
        Config config = new Config();
        config.reloadMainConfig();
        config.reloadDataFile();
        setupPermissions();
        if(!ServerLogic.init())
        {
            setEnabled(false);
        }
        
        playerlistener = new PlayerListener();
        pm.registerEvents(playerlistener, this);
        
        if(Utils.SpoutAvailable())
        {
            RegionControl.plugin.getLogger().log(Level.INFO, "DEBUG: Beginning Spout Init");
            SpoutClientLogic.init();
            spoutplayerlistener = new SpoutPlayerListener();
            pm.registerEvents(spoutplayerlistener, this);
        }
    }

    private boolean setupPermissions()
    {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }

    @Override
    public void onDisable() 
    {
        new Config().saveAll();
    }
}
