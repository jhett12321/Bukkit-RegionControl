package com.featherminecraft.regioncontrol.spout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.getspout.spoutapi.gui.*;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.featherminecraft.regioncontrol.CapturableRegion;
import com.featherminecraft.regioncontrol.Config;
import com.featherminecraft.regioncontrol.RegionControl;
import com.featherminecraft.regioncontrol.SpawnPoint;
import com.featherminecraft.regioncontrol.utils.PlayerUtils;

public class SpoutClientLogic extends BukkitRunnable {

    public static Label regioname;
    public static Texture factionicon;
    private Map<SpawnPoint,Button> spawnbuttons;
    private Label regionname;
    private CapturableRegion region;
    private Map<String, Integer> timeremaining;
    private Long expectedcapture;
    private Label capturetimer;
    private Texture influenceownericon;
    private GenericGradient capturebar;
    
    public SpoutClientLogic(SpoutPlayer splayer)
    {
        //Test Implementation of Spout Widget.
        Color back = new Color(0.0F, 0.0F, 0.0F, 0.75F);
        Color bottom = new Color(1.0F, 1.0F, 1.0F, 0.75F);
        Screen screen = splayer.getMainScreen();
        
        //Always Displayed
        regionname = new GenericLabel().setText("");
        regionname.setAnchor(WidgetAnchor.CENTER_LEFT).shiftYPos(15).shiftXPos(15);
        factionicon = new GenericTexture().setUrl("defaultfaction.png");
        factionicon.setAnchor(WidgetAnchor.CENTER_LEFT).shiftXPos(15);
        
        //Displayed when region is being captured
        capturetimer = new GenericLabel().setText("0:00");
        capturetimer.setAnchor(WidgetAnchor.CENTER_LEFT).setVisible(false);
        capturebar = new GenericGradient();
        capturebar.setAnchor(WidgetAnchor.CENTER_LEFT).setVisible(false);
        
        influenceownericon = new GenericTexture().setUrl("defaultfaction.png");
        
        screen.attachWidgets(RegionControl.plugin, regionname, factionicon,capturetimer,capturebar,influenceownericon);
    }

    public Map<SpawnPoint,Button> getSpawnButtons() {
        return spawnbuttons;
    }
    
    public Map<SpawnPoint,Button> getBestSpawnPointsForPlayer(Player player)
    {
        CapturableRegion currentregion = new PlayerUtils().getCurrentRegion(player);
        List<CapturableRegion> regions = currentregion.getAdjacentregions();
        regions.add(currentregion);
        
        List<CapturableRegion> spawnableregions = new ArrayList<CapturableRegion>();
        
        int maxspawnpoints = new Config().getMainConfig().getInt("spout.maxspawnpoints");
        int currentspawnpoints = 0;
        
        for(CapturableRegion region : regions)
        {
            currentspawnpoints = currentspawnpoints + 1;
            if(currentspawnpoints == maxspawnpoints)
            {
                break;
            }
            if(region.getOwner() == new PlayerUtils().getPlayerFaction(player))
            {
                spawnableregions.add(region);
            }
        }
        
        Map<SpawnPoint,Button> bestspawnpoints = new HashMap<SpawnPoint,Button>();
        
        for(CapturableRegion region : spawnableregions)
        {
            bestspawnpoints.put(region.getSpawnPoint(), spawnbuttons.get(region.getSpawnPoint()));
        }
        
        return bestspawnpoints;
    }

    public void setSpawnButtons(Map<SpawnPoint,Button> spawnbuttons) {
        this.spawnbuttons = spawnbuttons;
    }

    @Override
    public void run() {
        long currenttime = System.currentTimeMillis();
        if(region.getExpectedCaptureTime() == null || region.getExpectedCaptureTime() - currenttime == 0)
        {
            capturetimer.setVisible(false);
        }
        
        long millisecondsremaining = region.getExpectedCaptureTime() - currenttime;
        
        Integer seconds = (int) ((millisecondsremaining / 1000) % 60) ;
        Integer minutes = (int) ((millisecondsremaining / (1000*60)));
        
        capturetimer.setText(minutes.toString() + ":" + seconds.toString());
    }
}
