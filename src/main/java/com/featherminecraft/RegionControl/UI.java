package com.featherminecraft.RegionControl;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import com.featherminecraft.RegionControl.api.PlayerAPI;
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
                    ChatColor color = ChatColor.WHITE;
                    if(!controlPoint.isCapturing())
                    {
                        Faction owner = controlPoint.getOwner();
                        if(owner != null)
                        {
                            color = owner.getFactionColor().getChatColor();
                        }
                    }
                    
                    float barPercentage = controlPoint.getInfluence().floatValue() / controlPoint.getBaseInfluence().floatValue() * 100;
                    if(PlayerAPI.canCapture(controlPoint.getRegion(), player))
                    {
                        BarAPI.setMessage(player.getBukkitPlayer(), color + "[" + controlPoint.getIdentifier().toUpperCase() + "] - " + controlPoint.getRegion().getDisplayName(), barPercentage);
                    }
                    
                    else
                    {
                        BarAPI.setMessage(player.getBukkitPlayer(), ChatColor.RED + PlayerAPI.getCannotCaptureReasons(controlPoint.getRegion(), player).get(0), barPercentage);
                    }
                }
            }
            
        }.runTaskTimer(RegionControl.plugin, 10, 10));
    }
    
    public void setControlPoint(ControlPoint controlPoint)
    {
        this.controlPoint = controlPoint;
        
        if(controlPoint != null)
        {
            ChatColor color = ChatColor.WHITE;
            if(!controlPoint.isCapturing())
            {
                Faction owner = controlPoint.getOwner();
                if(owner != null)
                {
                    color = owner.getFactionColor().getChatColor();
                }
            }
            
            float barPercentage = controlPoint.getInfluence().floatValue() / controlPoint.getBaseInfluence().floatValue() * 100;
            BarAPI.setMessage(player.getBukkitPlayer(), color + "[" + controlPoint.getIdentifier().toUpperCase() + "] - " + controlPoint.getRegion().getDisplayName(), barPercentage);
        }
        else
        {
            BarAPI.removeBar(player.getBukkitPlayer());
        }
    }

}
