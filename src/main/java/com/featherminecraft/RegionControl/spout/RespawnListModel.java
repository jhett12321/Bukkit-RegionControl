package com.featherminecraft.RegionControl.spout;

import java.util.List;

import org.getspout.spoutapi.gui.AbstractListModel;
import org.getspout.spoutapi.gui.Color;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.ListWidgetItem;
import org.getspout.spoutapi.gui.WidgetAnchor;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.featherminecraft.RegionControl.RCPlayer;
import com.featherminecraft.RegionControl.ServerLogic;
import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;
import com.featherminecraft.RegionControl.utils.PlayerUtils;

public class RespawnListModel extends AbstractListModel
{
    private SpoutPlayer splayer;
    private List<ListWidgetItem> spawnPoints;
    private RCPlayer rcplayer;
    private RespawnScreen respawnScreen;
    
    public RespawnListModel(RespawnScreen respawnScreen, RCPlayer rcplayer, List<ListWidgetItem> spawnPoints)
    {
        this.respawnScreen = respawnScreen;
        this.rcplayer = rcplayer;
        splayer = (SpoutPlayer) rcplayer.getBukkitPlayer();
        this.spawnPoints = spawnPoints;
        
        ListWidgetItem item = getItem(0);
        String displayName = item.getText();
        
        CapturableRegion regionSelected = null;
        for(CapturableRegion region : ServerLogic.capturableRegions.values())
        {
            if(region.getDisplayName().equalsIgnoreCase(displayName))
            {
                regionSelected = region;
                break;
            }
        }
        
        rcplayer.setRespawnLocation(regionSelected.getSpawnPoint().getLocation());
        respawnScreen.getRedeployButton().setText("Redeploy: " + regionSelected.getDisplayName()).setDirty(true);
    }
    
    @Override
    public ListWidgetItem getItem(int i)
    {
        return spawnPoints.get(i);
    }
    
    @Override
    public int getSize()
    {
        return spawnPoints.size();
    }
    
    @Override
    public void onSelected(int arg0, boolean doubleClicked)
    {
        ListWidgetItem item = getItem(arg0);
        String displayName = item.getText();
        
        CapturableRegion regionSelected = null;
        for(CapturableRegion region : ServerLogic.capturableRegions.values())
        {
            if(region.getDisplayName().equalsIgnoreCase(displayName))
            {
                regionSelected = region;
                break;
            }
        }
        
        rcplayer.setRespawnLocation(regionSelected.getSpawnPoint().getLocation());
        
        int allyCount = 0;
        int enemyCount = 0;
        
        for(RCPlayer rcPlayer : regionSelected.getPlayers())
        {
            if(rcPlayer.getFaction() == rcplayer.getFaction())
            {
                allyCount = allyCount + 1;
            }
            else
            {
                enemyCount = enemyCount + 1;
            }
        }
        
        String alliesDetectedText = "";
        String enemiesDetectedText = "";
        
        if(allyCount == 0)
        {
            alliesDetectedText = "Allies Detected: None";
        }
        else if(allyCount <= 12)
        {
            alliesDetectedText = "Allies Detected: 1-12";
        }
        else if(allyCount <= 24)
        {
            alliesDetectedText = "Allies Detected: 13-24";
        }
        else if(allyCount <= 48)
        {
            alliesDetectedText = "Allies Detected: 25-48";
        }
        else
        {
            alliesDetectedText = "Allies Detected: 48+";
        }
        
        if(enemyCount == 0)
        {
            enemiesDetectedText = "Enemies Detected: None";
        }
        else if(enemyCount <= 12)
        {
            enemiesDetectedText = "Enemies Detected: 1-12";
        }
        else if(enemyCount <= 24)
        {
            enemiesDetectedText = "Enemies Detected: 13-24";
        }
        else if(enemyCount <= 48)
        {
            enemiesDetectedText = "Enemies Detected: 25-48";
        }
        else
        {
            enemiesDetectedText = "Enemies Detected: 48+";
        }
        
        if(!doubleClicked)
        {
            if(regionSelected.isBeingCaptured())
            {
                respawnScreen.getListWidget().setTooltip("§l§n" + regionSelected.getDisplayName()
                                                         + "§r\n" + "Capture in: " + regionSelected.getMinutesToCapture() + ":" + regionSelected.getSecondsToCapture()
                                                         + "\n" + alliesDetectedText
                                                         + "\n" + enemiesDetectedText);
                
                respawnScreen.getRedeployButton().setTooltip("§l§n" + regionSelected.getDisplayName()
                                                         + "§r\n" + "Capture in: " + regionSelected.getMinutesToCapture() + ":" + regionSelected.getSecondsToCapture()
                                                         + "\n" + alliesDetectedText
                                                         + "\n" + enemiesDetectedText);
            }
            else
            {
                respawnScreen.getListWidget().setTooltip("§l§n" + regionSelected.getDisplayName()
                                                         + "§r\n" + alliesDetectedText
                                                         + "\n" + enemiesDetectedText);
                respawnScreen.getRedeployButton().setTooltip("§l§n" + regionSelected.getDisplayName()
                                                         + "§r\n" + alliesDetectedText
                                                         + "\n" + enemiesDetectedText);
            }
            
            respawnScreen.getRedeployButton().setText("Redeploy: " + regionSelected.getDisplayName()).setDirty(true);
            return;
        }
        
        new PlayerUtils().respawnPlayer(rcplayer);
        splayer.getMainScreen().closePopup();
    }
}
