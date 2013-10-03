package com.featherminecraft.RegionControl.spout;

import java.util.List;
import java.util.Map;

import org.getspout.spoutapi.gui.AbstractListModel;
import org.getspout.spoutapi.gui.ListWidgetItem;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.featherminecraft.RegionControl.RCPlayer;
import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;

public class RespawnListModel extends AbstractListModel
{
    private SpoutPlayer splayer;
    private List<ListWidgetItem> spawnPoints;
    private RCPlayer rcplayer;
    private Map<Integer, CapturableRegion> identifiers;

    public RespawnListModel(RCPlayer rcplayer, List<ListWidgetItem> spawnPoints, Map<Integer,CapturableRegion> identifiers)
    {
        this.rcplayer = rcplayer;
        this.splayer = (SpoutPlayer) rcplayer.getBukkitPlayer();
        this.spawnPoints = spawnPoints;
        this.identifiers = identifiers;
    }
    
    @Override
    public ListWidgetItem getItem(int i) {
        return (ListWidgetItem) this.spawnPoints.get(i);
    }

    @Override
    public int getSize() {
        return this.spawnPoints.size();
    }

    @Override
    public void onSelected(int arg0, boolean doubleClicked) {
        if (!doubleClicked) return;
        
        CapturableRegion regionSelected = identifiers.get(arg0);
        rcplayer.setRespawnLocation(regionSelected.getSpawnPoint().getLocation());
        this.splayer.getMainScreen().closePopup();
    }

}
