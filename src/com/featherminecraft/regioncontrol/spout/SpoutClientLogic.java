package com.featherminecraft.regioncontrol.spout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.getspout.spoutapi.gui.*;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.featherminecraft.regioncontrol.CapturableRegion;
import com.featherminecraft.regioncontrol.Config;
import com.featherminecraft.regioncontrol.Faction;
import com.featherminecraft.regioncontrol.RegionControl;
import com.featherminecraft.regioncontrol.ServerLogic;
import com.featherminecraft.regioncontrol.SpawnPoint;
import com.featherminecraft.regioncontrol.utils.PlayerUtils;

public class SpoutClientLogic extends BukkitRunnable {

    private Label regionname;
    private Texture ownericon;
    private Map<SpawnPoint,Button> spawnbuttons;
    private CapturableRegion region;
    private Map<Faction,Color> spoutcolor;
    private Label capturetimer;
    private Texture influenceownericon;
    private Gradient capturebar;
    private SpoutPlayer splayer;
    private Gradient capturebaranim;
    private Container regionInfo;
    private Container captureInfo;
    
    public SpoutClientLogic(SpoutPlayer splayer)
    {
        this.splayer = splayer;
        Screen screen = splayer.getMainScreen();
        
        Config config = new Config();
        for(Entry<String, Faction> faction : ServerLogic.registeredfactions.entrySet())
        {
            int red = config.getMainConfig().getInt("factions." + faction + ".color" + ".red");
            int green = config.getMainConfig().getInt("factions." + faction + ".color" + ".green");
            int blue = config.getMainConfig().getInt("factions." + faction + ".color" + ".blue");
            
            Color factioncolor = new Color(red/255F, green/255F, blue/255F);
            spoutcolor.put(faction.getValue(), factioncolor);
        }
        
        regionInfo = new GenericContainer().setLayout(ContainerType.HORIZONTAL);
        captureInfo = new GenericContainer().setLayout(ContainerType.OVERLAY);
        
        //Always Displayed
        ownericon = (Texture) new GenericTexture("defaultfaction.png").setMarginRight(5);
        regionname = (Label) new GenericLabel("DEBUG: REGION NAME NOT FOUND");

        regionInfo.addChildren(ownericon, regionname).setAlign(WidgetAnchor.CENTER_LEFT).setAnchor(WidgetAnchor.CENTER_LEFT).shiftXPos(15);
        
        //Displayed when region is being captured
        influenceownericon = (Texture) new GenericTexture("defaultfaction.png").setMarginRight(5);
        capturebar = (Gradient) new GenericGradient().shiftXPos(15);
        capturebaranim = (Gradient) new GenericGradient().setColor(new Color(1F, 1F, 1F, 0.25F)).shiftXPos(15).setPriority(RenderPriority.Low);
        capturetimer = (Label) new GenericLabel().setText("0:00").shiftXPos(15).setPriority(RenderPriority.Lowest);

        captureInfo.addChildren(influenceownericon,capturebar,capturebaranim,capturetimer);
        
        screen.attachWidgets(RegionControl.plugin, regionname, ownericon,capturetimer,capturebar,capturebaranim,influenceownericon);
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
        if(region.isBeingCaptured())
        {
            captureInfo.setVisible(true);
            long currenttime = System.currentTimeMillis();
            if(region.getExpectedCaptureTime() == null || region.getExpectedCaptureTime() - currenttime == 0)
            {
                capturetimer.setVisible(false);
            }
            
            long millisecondsremaining = region.getExpectedCaptureTime() - currenttime;
            
            Integer seconds = (int) ((millisecondsremaining / 1000) % 60) ;
            Integer minutes = (int) ((millisecondsremaining / (1000*60)));
            
            capturetimer.setText(minutes.toString() + ":" + seconds.toString());
            capturebar.setWidth(region.getInfluence() / region.getBaseInfluence() * 100);
            capturebar.setColor(spoutcolor.get(region.getInfluenceOwner()));
            influenceownericon.setUrl(region.getInfluenceOwner().getName()); //TODO

            int animrate = 0;
            if(region.getTimer().getInfluenceRate() == 1)
            {
                animrate = 20;
            }
            
            else if(region.getTimer().getInfluenceRate() == 2)
            {
                animrate = 15;
            }
            
            else if (region.getTimer().getInfluenceRate() == 3)
            {
                animrate = 5;
            }
            
            if(animrate != 0)
            {
                if(region.getInfluenceOwner() != region.getOwner())
                {
                    capturebaranim.animate(WidgetAnim.OFFSET_LEFT, -capturebar.getWidth(), (short) 10, (short)animrate, false, true);
                }
                
                else if (region.getInfluenceOwner() == region.getOwner())
                {
                    capturebaranim.animate(WidgetAnim.OFFSET_LEFT, capturebar.getWidth(), (short) 10, (short)animrate, false, true);
                }
            }
        }
        else
        {
            capturetimer.setVisible(false);
            capturebar.setVisible(false);
            influenceownericon.setVisible(false);
        }
    }

    public void updateCurrentRegion(Player player, CapturableRegion region) {
        this.region = region;
        
        if(splayer.isSpoutCraftEnabled())
            regionname.setText(region.getDisplayname());
            ownericon.setUrl(region.getOwner().getName()); //TODO
            
            if(region.isBeingCaptured())
            {
                captureInfo.setVisible(true);
                capturebar.setColor(spoutcolor.get(region.getInfluenceOwner())).setVisible(true);
                influenceownericon.setUrl(region.getInfluenceOwner().getName()).setVisible(true);//TODO
            }
    }
}
