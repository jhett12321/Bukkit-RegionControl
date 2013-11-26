package com.featherminecraft.RegionControl.spout;

import java.util.List;

import org.getspout.spoutapi.gui.AbstractListModel;
import org.getspout.spoutapi.gui.ListWidgetItem;
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
        
        if(!doubleClicked)
        {
            respawnScreen.getRedeployButton().setText("Redeploy: " + regionSelected.getDisplayName()).setDirty(true);
            return;
        }
        
        new PlayerUtils().respawnPlayer(rcplayer);
        splayer.getMainScreen().closePopup();
    }
    
}
