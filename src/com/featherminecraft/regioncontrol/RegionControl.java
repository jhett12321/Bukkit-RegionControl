/* Task List
TODO:
    TODO 1. Code Testing
Details:
File/s: All
JIRA Link:
Classification: Improvement
Priority: Major

    TODO 2. Add configuration options to allow disabling of spout support
Details:
File/s: RegionControl.java Line 80, Config.java
JIRA Link: 
Classification: Improvement
Priority: Minor

    TODO 3. Finish Configuration Code
Details:
File/s: RegionControl.java Line 10, Config.java, Utils.java
JIRA Link: 
Classification: Improvement
Priority: Critical

    TODO 4. Implement onDisable Code
Details:
File/s: RegionControl.java Line 91
JIRA Link: 
Classification: New Feature
Priority: Blocker

    TODO 5. Make sure that static is being used properly in functions.
Details:
File/s: RegionControl.java
JIRA Link: 
Classification: Improvement
Priority: Major

    TODO 6. Implement Dynmap (Or some similar full screen map) to client that shows current region control.
Details:
File/s: Unknown
JIRA Link: 
Classification: New Feature
Priority: Trivial

    TODO 7. Spawn Points
    EDIT: Spawn points will be handled in a "re-spawn" room (When Spout is unavailable), with signs indicating where a player can spawn. Right Clicking the sign will spawn the player at that location.
    Valid spawn points will be regions that you own.
Details:
File/s: Unknown
JIRA Link: 
Classification: New Feature
Priority: Minor

    TODO 8. Replace main configuration with individual configuration files
Details:
File/s: Config.java All Lines
JIRA Link: 
Classification: Improvement
Priority: Minor

    TODO 9. Map Filters
Details:
Pre-req: Dynmap Implementation
JIRA Link: 
Classification: Improvement
Priority: Trivial

    TODO 10. World Saving - Remember who owned each region after server restart/shutdown.
Details:
File/s: ServerLogic.java
JIRA Link: 
Classification: New Feature
Priority: Critical

ISSUE/S:
 */

package com.featherminecraft.regioncontrol;

import java.util.Map;
import java.util.Map.Entry;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.featherminecraft.regioncontrol.listeners.PlayerListener;
import com.featherminecraft.regioncontrol.listeners.SpoutPlayerListener;
import com.featherminecraft.regioncontrol.utils.Utils;

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
        // TODO: Will this be needed to determine if the client has the mod installed?
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
        ServerLogic.init();

        if(Utils.SpoutAvailable())
        {
            spoutplayerlistener = new SpoutPlayerListener();
            pm.registerEvents(spoutplayerlistener, this);
        } else {
            playerlistener = new PlayerListener();
            pm.registerEvents(playerlistener, this);
        }
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        permission = rsp.getProvider();
        return permission != null;
    }

    @Override
    public void onDisable() {
        //TODO Finish save code.
        Map<String, CapturableRegion> registered_regions = ServerLogic.registeredregions;
        for(Entry<String, CapturableRegion> region : registered_regions.entrySet())
        {
            Integer influence = region.getValue().getInfluence();
            String influenceowner = region.getValue().getInfluenceOwner().getName();
            String regionowner = region.getValue().getOwner().getName();
            
            for(ControlPoint controlPoint : region.getValue().getControlpoints())
            {
                String controlPointOwner = controlPoint.getOwner().getName();
            }
        }

    }
}
