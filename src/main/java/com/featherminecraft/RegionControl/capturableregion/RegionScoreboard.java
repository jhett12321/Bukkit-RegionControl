package com.featherminecraft.RegionControl.capturableregion;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class RegionScoreboard
{
    private static final int ELEMENT_COUNT = 20;
    
    private CapturableRegion region;
    
    private Scoreboard scoreBoard;
    private Objective objective;
    
    private Score ownerTitle;
    private Score owner;
    private Score influenceTitle;
    private Score influence;
    private Score influenceRate;
    private Score controlPoints;
    private Score controlPointTitle;
    private Score timer;
    private Score timerTitle;
    
    RegionScoreboard(CapturableRegion region)
    {
        this.region = region;
        
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        scoreBoard = manager.getNewScoreboard();
        
        objective = scoreBoard.registerNewObjective("regionInfo", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(region.getOwner().getFactionColor().getChatColor() + region.getDisplayName());
        
        ownerTitle = objective.getScore(Bukkit.getOfflinePlayer("Owned by:"));
        ownerTitle.setScore(ELEMENT_COUNT);
        
        owner = objective.getScore(Bukkit.getOfflinePlayer(region.getOwner().getFactionColor().getChatColor() + region.getOwner().getDisplayName()));
        owner.setScore(ownerTitle.getScore() - 1);
        
        influenceTitle = objective.getScore(Bukkit.getOfflinePlayer("Influence:"));
        influenceTitle.setScore(owner.getScore() - 1);
        
        if(region.getInfluenceOwner() != null)
        {
            influence = objective.getScore(Bukkit.getOfflinePlayer(region.getInfluenceOwner().getFactionColor().getChatColor() + Integer.valueOf(region.getInfluence().intValue()).toString() + "/" + Integer.valueOf(region.getBaseInfluence().intValue()).toString()));
            influence.setScore(influenceTitle.getScore() - 1);
        }
        
        if(!region.isSpawnRegion())
        {
            String influenceRateString = "";
            if(region.getInfluenceOwner() != null && region.getMajorityController() != null)
            {
                if(region.getInfluenceOwner() == region.getMajorityController())
                {
                    influenceRateString = region.getMajorityController().getFactionColor().getChatColor() + "+" + Integer.valueOf((int) region.getInfluenceRate()).toString() + "/sec";
                }
                else
                {
                    influenceRateString = region.getMajorityController().getFactionColor().getChatColor() + "-" + Integer.valueOf((int) region.getInfluenceRate()).toString() + "/sec";
                }
                
                influenceRate = objective.getScore(Bukkit.getOfflinePlayer(influenceRateString));
                influenceRate.setScore(influence.getScore() - 1);
            }
            
            controlPointTitle = objective.getScore(Bukkit.getOfflinePlayer("Control Points:"));
            controlPointTitle.setScore(influence.getScore() - 2);
            
            List<String> controlPointStrings = new ArrayList<String>();
            for(ControlPoint controlPoint : region.getControlPoints())
            {
                controlPointStrings.add(controlPoint.getOwner().getFactionColor().getChatColor() + controlPoint.getIdentifier().toUpperCase() + " ");
            }
            
            String controlPointsString = "";
            for(String controlPointString : controlPointStrings)
            {
                controlPointsString = controlPointsString + controlPointString + "";
            }
            
            controlPoints = objective.getScore(Bukkit.getOfflinePlayer(controlPointsString.trim()));
            controlPoints.setScore(controlPointTitle.getScore() - 1);
        }
    }
    
    public Scoreboard getScoreboard()
    {
        return scoreBoard;
    }
    
    protected void Runnable()
    {
        if(region.isBeingCaptured())
        {
            Integer seconds = region.getSecondsToCapture();
            Integer minutes = region.getMinutesToCapture();
            if(seconds != 0 || minutes != 0)
            {
                // Timer
                if(timerTitle == null)
                {
                    timerTitle = objective.getScore(Bukkit.getOfflinePlayer("Capture in:"));
                    timerTitle.setScore(controlPoints.getScore() - 1);
                }
                
                int score = timerTitle.getScore() - 1;
                
                String secondsString = seconds.toString();
                if(seconds < 10)
                {
                    secondsString = "0" + secondsString;
                }
                
                if(timer != null)
                {
                    scoreBoard.resetScores(timer.getPlayer());
                }
                timer = objective.getScore(Bukkit.getOfflinePlayer(minutes.toString() + ":" + secondsString));
                timer.setScore(score);
                
                // Influence
                scoreBoard.resetScores(influence.getPlayer());
                
                if(region.getInfluenceOwner() != null)
                {
                    influence = objective.getScore(Bukkit.getOfflinePlayer(region.getInfluenceOwner().getFactionColor().getChatColor() + Integer.valueOf(region.getInfluence().intValue()).toString() + "/" + Integer.valueOf(region.getBaseInfluence().intValue()).toString()));
                    influence.setScore(influenceTitle.getScore() - 1);
                }
            }
        }
        else if(!region.isBeingCaptured() && timerTitle != null)
        {
            scoreBoard.resetScores(timerTitle.getPlayer());
            scoreBoard.resetScores(timer.getPlayer());
            scoreBoard.resetScores(influence.getPlayer());
            
            timerTitle = null;
            timer = null;
            
            if(region.getInfluenceOwner() != null)
            {
                influence = objective.getScore(Bukkit.getOfflinePlayer(region.getInfluenceOwner().getFactionColor().getChatColor() + Integer.valueOf(region.getInfluence().intValue()).toString() + "/" + Integer.valueOf(region.getBaseInfluence().intValue()).toString()));
                influence.setScore(influenceTitle.getScore() - 1);
            }
        }
    }
    
    public void updateControlPoints()
    {
        int score = controlPoints.getScore();
        
        scoreBoard.resetScores(controlPoints.getPlayer());
        
        List<String> controlPointStrings = new ArrayList<String>();
        for(ControlPoint controlPoint : region.getControlPoints())
        {
            if(controlPoint.getInfluence() == controlPoint.getBaseInfluence())
            {
                controlPointStrings.add(controlPoint.getOwner().getFactionColor().getChatColor() + controlPoint.getIdentifier().toUpperCase() + " ");
            }
            else
            {
                controlPointStrings.add(ChatColor.WHITE + controlPoint.getIdentifier().toUpperCase() + " ");
            }
        }
        
        String controlPointsString = "";
        for(String controlPointString : controlPointStrings)
        {
            controlPointsString = controlPointsString + controlPointString + "";
        }
        
        controlPoints = objective.getScore(Bukkit.getOfflinePlayer(controlPointsString.trim()));
        controlPoints.setScore(score);
    }
    
    public void updateInfluenceRate()
    {
        int score = influence.getScore() - 1;
        
        if(influenceRate != null)
        {
            scoreBoard.resetScores(influenceRate.getPlayer());
        }
        
        String influenceRateString = null;
        if(region.getInfluenceOwner() != null && region.getMajorityController() != null)
        {
            influenceRateString = "";
            if(region.getInfluenceOwner() == region.getMajorityController())
            {
                influenceRateString = region.getInfluenceOwner().getFactionColor().getChatColor() + "+" + Integer.valueOf((int) region.getInfluenceRate()).toString() + "/sec";
            }
            else
            {
                influenceRateString = region.getInfluenceOwner().getFactionColor().getChatColor() + "-" + Integer.valueOf((int) region.getInfluenceRate()).toString() + "/sec";
            }
            
            influenceRate = objective.getScore(Bukkit.getOfflinePlayer(influenceRateString));
            influenceRate.setScore(influence.getScore() - 1);
        }
        
        if(influenceRateString != null)
        {
            influenceRate = objective.getScore(Bukkit.getOfflinePlayer(influenceRateString));
            influenceRate.setScore(score);
        }
    }
    
    public void updateOwner()
    {
        int score = owner.getScore();
        
        scoreBoard.resetScores(owner.getPlayer());
        
        objective.setDisplayName(region.getOwner().getFactionColor().getChatColor() + region.getDisplayName());
        
        owner = objective.getScore(Bukkit.getOfflinePlayer(region.getOwner().getFactionColor().getChatColor() + region.getOwner().getDisplayName()));
        owner.setScore(score);
    }
}
