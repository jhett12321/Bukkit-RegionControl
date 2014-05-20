package com.featherminecraft.RegionControl.spout;

import java.util.List;

import org.bukkit.scheduler.BukkitRunnable;

import org.getspout.spoutapi.gui.AbstractListModel;
import org.getspout.spoutapi.gui.ListWidgetItem;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.featherminecraft.RegionControl.RCPlayer;
import com.featherminecraft.RegionControl.RegionControl;
import com.featherminecraft.RegionControl.ServerLogic;
import com.featherminecraft.RegionControl.api.PlayerAPI;
import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;

public class RespawnListModel extends AbstractListModel
{
    private SpoutPlayer splayer;
    private List<ListWidgetItem> spawnPoints;
    private RCPlayer rcplayer;
    private RespawnScreen respawnScreen;
    private ListWidgetItem item;
    
    RespawnListModel(RespawnScreen respawnScreen, RCPlayer rcplayer, List<ListWidgetItem> spawnPoints)
    {
        this.respawnScreen = respawnScreen;
        this.rcplayer = rcplayer;
        splayer = (SpoutPlayer) rcplayer.getBukkitPlayer();
        this.spawnPoints = spawnPoints;
        
        item = getItem(0);
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
        
        rcplayer.getClientRunnables().put("spoutRespawnTooltip", new BukkitRunnable()
        {
            @Override
            public void run()
            {
                updateTooltip();
            }
            
        }.runTaskTimer(RegionControl.plugin, 10, 10));
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
        item = getItem(arg0);
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
        
        if(!doubleClicked)
        {
            respawnScreen.getRedeployButton().setText("Redeploy: " + regionSelected.getDisplayName()).setDirty(true);
            return;
        }
        
        if(rcplayer.getBukkitPlayer().isDead())
        {
            PlayerAPI.respawnPlayer(rcplayer);
        }
        else
        {
            rcplayer.getBukkitPlayer().teleport(rcplayer.getRespawnLocation());
        }
        rcplayer.showPlayer();
        
        splayer.getMainScreen().closePopup();
        rcplayer.getClientRunnable("spoutRespawnTooltip").cancel();
    }
    
    private void updateTooltip()
    {
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
        
        Integer seconds = regionSelected.getSecondsToCapture();
        Integer minutes = regionSelected.getMinutesToCapture();
        
        String secondsString = seconds.toString();
        if(seconds < 10)
        {
            secondsString = "0" + secondsString;
        }
        
        if(!regionSelected.isBeingCaptured() || (seconds == 0 && minutes == 0))
        {
            respawnScreen.getListWidget().setTooltip("§l§n" + regionSelected.getDisplayName() + "§r\n" + alliesDetectedText + "\n" + enemiesDetectedText);
            respawnScreen.getRedeployButton().setTooltip("§l§n" + regionSelected.getDisplayName() + "§r\n" + alliesDetectedText + "\n" + enemiesDetectedText);
        }
        else
        {
            respawnScreen.getListWidget().setTooltip("§l§n" + regionSelected.getDisplayName() + "§r\n" + "Capture in: " + minutes.toString() + ":" + secondsString + "\n" + alliesDetectedText + "\n" + enemiesDetectedText);
            respawnScreen.getRedeployButton().setTooltip("§l§n" + regionSelected.getDisplayName() + "§r\n" + "Capture in: " + minutes.toString() + ":" + secondsString + "\n" + alliesDetectedText + "\n" + enemiesDetectedText);
        }
    }
    
    public void updateSpawnPoints(List<ListWidgetItem> spawnPoints)
    {
        this.spawnPoints = spawnPoints;
    }
}