package com.featherminecraft.regioncontrol;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class CaptureTimer {
    
    //Constructor Vars:
    private CapturableRegion region;
    private Integer baseinfluenceamount;
    
    private long millisecondsremaining;
    private int minutesRemaining;
    private int secondsRemaining;
    private BukkitTask runnable;

    public CaptureTimer(CapturableRegion cregion)
    {
        this.region = cregion;
        this.baseinfluenceamount = region.getBaseInfluence();
        
        this.runnable = new BukkitRunnable() {

            @Override
            public void run() {
                
                if(region.getInfluenceOwner() == null)
                {
                    if(region.getInfluenceRate() == 3)
                    {
                        millisecondsremaining = (long) ((baseinfluenceamount - region.influence.get(region.getMajorityController())) / 3 * 1000);
                        region.influence.put(region.getMajorityController(), (float) 3);
                    }
                    
                    else if(region.getInfluenceRate() == 2)
                    {
                        millisecondsremaining = (long) ((baseinfluenceamount - region.influence.get(region.getMajorityController())) / 2 * 1000);
                        region.influence.put(region.getMajorityController(), (float) 3);
                    }
                    
                    else if(region.getInfluenceRate() == 1)
                    {
                        millisecondsremaining = (long) ((baseinfluenceamount - region.influence.get(region.getMajorityController())) * 1000);
                        region.influence.put(region.getMajorityController(), (float) 3);
                    }

                    Integer seconds = (int) ((millisecondsremaining / 1000) % 60);
                    Integer minutes = (int) ((millisecondsremaining / (1000*60)));
                    
                    String secondsString = seconds.toString();
                    if(seconds < 10)
                    {
                        secondsString = "0" + secondsString;
                    }
                }
                
                else if(region.getInfluenceOwner() == region.getMajorityController())
                {
                    
                }
                
                else if(region.getInfluenceOwner() != region.getMajorityController())
                {
                    
                }
            }
        }.runTaskTimer(RegionControl.plugin, 20, 20);
    }
}
