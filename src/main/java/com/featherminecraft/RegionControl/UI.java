package com.featherminecraft.RegionControl;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import com.featherminecraft.RegionControl.api.PlayerAPI;
import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;
import com.featherminecraft.RegionControl.capturableregion.ControlPoint;

import me.confuser.barapi.BarAPI;

public class UI
{
    private RCPlayer player;
    private ControlPoint controlPoint;
    
    public UI(RCPlayer rcPlayer)
    {
        this.player = rcPlayer;
        
        rcPlayer.getClientRunnables().put("ui_task", new BukkitRunnable()
        {
            @Override
            public void run()
            {
                if(controlPoint != null)
                {
                    CapturableRegion region = controlPoint.getRegion();
                    ChatColor regionColor = ChatColor.WHITE;
                    if(region.getOwner() != null)
                    {
                        regionColor = region.getOwner().getFactionColor().getChatColor();
                    }
                    
                    ChatColor pointColor = ChatColor.WHITE;
                    if(!controlPoint.isCapturing())
                    {
                        Faction owner = controlPoint.getOwner();
                        if(owner != null)
                        {
                            pointColor = owner.getFactionColor().getChatColor();
                        }
                    }
                    
                    float barPercentage = controlPoint.getInfluence().floatValue() / controlPoint.getBaseInfluence().floatValue() * 100;
                    if(PlayerAPI.canCapture(controlPoint.getRegion(), player))
                    {
                        BarAPI.setMessage(player.getBukkitPlayer(), regionColor + controlPoint.getRegion().getDisplayName() + pointColor + " [" + controlPoint.getIdentifier().toUpperCase() + "]", barPercentage);
                    }
                    
                    else
                    {
                        BarAPI.setMessage(player.getBukkitPlayer(), ChatColor.RED + PlayerAPI.getCannotCaptureReasons(controlPoint.getRegion(), player).get(0), barPercentage);
                    }
                }
                else if(!player.hasSpout())
                {
                    CapturableRegion region = player.getCurrentRegion();
                    
                    ChatColor regionColor = ChatColor.WHITE;
                    if(region.getOwner() != null)
                    {
                        regionColor = region.getOwner().getFactionColor().getChatColor();
                    }
                    
                    float barPercentage = region.getInfluence().floatValue() / region.getBaseInfluence().floatValue() * 100;
                    BarAPI.setMessage(player.getBukkitPlayer(), regionColor + region.getDisplayName(), barPercentage);
                }
            }
            
        }.runTaskTimer(RegionControl.plugin, 10, 10));
    }
    
    public void setControlPoint(ControlPoint controlPoint)
    {
        this.controlPoint = controlPoint;
        
        if(controlPoint != null)
        {
            CapturableRegion region = controlPoint.getRegion();
            ChatColor regionColor = ChatColor.WHITE;
            if(region.getOwner() != null)
            {
                regionColor = region.getOwner().getFactionColor().getChatColor();
            }
            
            ChatColor pointColor = ChatColor.WHITE;
            if(!controlPoint.isCapturing())
            {
                Faction owner = controlPoint.getOwner();
                if(owner != null)
                {
                    pointColor = owner.getFactionColor().getChatColor();
                }
            }
            
            float barPercentage = controlPoint.getInfluence().floatValue() / controlPoint.getBaseInfluence().floatValue() * 100;
            BarAPI.setMessage(player.getBukkitPlayer(), regionColor + controlPoint.getRegion().getDisplayName() + pointColor + " [" + controlPoint.getIdentifier().toUpperCase() + "]", barPercentage);
        }
        else
        {
            BarAPI.removeBar(player.getBukkitPlayer());
        }
    }

}
