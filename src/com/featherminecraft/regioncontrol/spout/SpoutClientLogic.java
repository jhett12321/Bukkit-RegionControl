package com.featherminecraft.regioncontrol.spout;

import java.util.Map;

import org.bukkit.scheduler.BukkitRunnable;
import org.getspout.spoutapi.gui.*;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.featherminecraft.regioncontrol.CapturableRegion;
import com.featherminecraft.regioncontrol.RegionControl;
import com.featherminecraft.regioncontrol.SpawnPoint;

public class SpoutClientLogic extends BukkitRunnable {

    public static Label regioname;
    public static Texture factionicon;
    private Map<SpawnPoint,Button> spawnbuttons;
    private Widget regionname;
    private CapturableRegion region;
    private Map<String, Integer> timeremaining;
    private Long expectedcapture;
    
    public SpoutClientLogic(SpoutPlayer splayer)
    {
        //Test Implementation of Spout Widget.
        Color back = new Color(0.0F, 0.0F, 0.0F, 0.75F);
        Color bottom = new Color(1.0F, 1.0F, 1.0F, 0.75F);
        Screen screen = splayer.getMainScreen();
        
        regionname = new GenericLabel().setText(""); //Region Name to be Displayed at all times.
        factionicon = new GenericTexture().setUrl("defaultfaction.png");
        
        screen.attachWidgets(RegionControl.plugin, regionname, regionname);
    }

    public Map<SpawnPoint,Button> getSpawnButtons() {
        return spawnbuttons;
    }

    public void setSpawnButtons(Map<SpawnPoint,Button> spawnbuttons) {
        this.spawnbuttons = spawnbuttons;
    }

    @Override
    public void run() {
        long currenttime = System.currentTimeMillis();
        
        long millisecondsremaining = region.getExpectedCaptureTime() - currenttime;
        
        int seconds = (int) ((millisecondsremaining / 1000) % 60) ;
        int minutes = (int) ((millisecondsremaining / (1000*60)) % 60);
        int hours   = (int) ((millisecondsremaining / (1000*60*60)) % 24);
        
        timeremaining.put("seconds", seconds);
        timeremaining.put("minutes", minutes);
        timeremaining.put("hours", hours);
    }
}
