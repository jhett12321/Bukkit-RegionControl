package com.featherminecraft.RegionControl.capturableregion;

import com.featherminecraft.RegionControl.Faction;

public class CaptureTimer
{
    
    private CapturableRegion region;
    
    public CaptureTimer(CapturableRegion cregion)
    {
        region = cregion;
    }
    
    public void Runnable()
    {
        
        long millisecondsremaining = 0;
        
        Faction influenceOwner = region.getInfluenceOwner();
        Faction majorityController = region.getMajorityController();
        Float baseInfluence = region.getBaseInfluence();
        
        Float influence = 0F;
        if(influenceOwner != null)
        {
            influence = region.getInfluenceMap().get(influenceOwner);
        }
        
        if(influenceOwner == null)
        {
            if(region.getInfluenceRate() == 4)
            {
                millisecondsremaining = (long) ((baseInfluence) / 4 * 1000);
            }
            
            if(region.getInfluenceRate() == 3)
            {
                millisecondsremaining = (long) ((baseInfluence) / 3 * 1000);
            }
            
            else if(region.getInfluenceRate() == 2)
            {
                millisecondsremaining = (long) ((baseInfluence) / 2 * 1000);
            }
            
            else if(region.getInfluenceRate() == 1)
            {
                millisecondsremaining = (long) ((baseInfluence) * 1000);
            }
        }
        
        else if(influenceOwner == majorityController)
        {
            if(region.getInfluenceRate() == 4)
            {
                millisecondsremaining = (long) ((baseInfluence - influence) / 4 * 1000);
            }
            
            if(region.getInfluenceRate() == 3)
            {
                millisecondsremaining = (long) ((baseInfluence - influence) / 3 * 1000);
            }
            
            else if(region.getInfluenceRate() == 2)
            {
                millisecondsremaining = (long) ((baseInfluence - influence) / 2 * 1000);
            }
            
            else if(region.getInfluenceRate() == 1)
            {
                millisecondsremaining = (long) ((baseInfluence - influence) * 1000);
            }
        }
        
        else if(region.getInfluenceOwner() != region.getMajorityController())
        {
            if(region.getInfluenceRate() == 4)
            {
                millisecondsremaining = (long) ((baseInfluence + influence) / 4 * 1000);
            }
            
            if(region.getInfluenceRate() == 3)
            {
                millisecondsremaining = (long) ((baseInfluence + influence) / 3 * 1000);
            }
            
            else if(region.getInfluenceRate() == 2)
            {
                millisecondsremaining = (long) ((baseInfluence + influence) / 2 * 1000);
            }
            
            else if(region.getInfluenceRate() == 1)
            {
                millisecondsremaining = (long) ((baseInfluence + influence) * 1000);
            }
        }
        
        if(millisecondsremaining != 0)
        {
            Integer seconds = (int) ((millisecondsremaining / 1000) % 60);
            Integer minutes = (int) ((millisecondsremaining / (1000 * 60)));
            
            region.setSecondsToCapture(seconds);
            region.setMinutesToCapture(minutes);
        }
        
        else
        {
            region.setSecondsToCapture(0);
            region.setMinutesToCapture(0);
        }
    }
}
