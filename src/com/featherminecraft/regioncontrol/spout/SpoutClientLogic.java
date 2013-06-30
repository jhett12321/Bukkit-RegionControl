package com.featherminecraft.regioncontrol.spout;

import java.io.File;
import java.util.Map;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.gui.*;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.featherminecraft.regioncontrol.CapturableRegion;
import com.featherminecraft.regioncontrol.Faction;
import com.featherminecraft.regioncontrol.RegionControl;

public class SpoutClientLogic {

    //Needs to be initalised before main setup begins.
    private SpoutPlayer splayer;
    private Map<Faction,String> factionIcons;
    private Map<Faction,Color> factionColors;
    
    //Variables created from setup
    private Label regionname;
    private Label capturetimer;
    private Gradient captureBar;
    private Texture captureBarAnim;
    private Gradient captureBarSpace;
    private Gradient captureBarBackground;
    private Texture ownericon;
    private Texture influenceownericon;
    
    //Definable Variables
    private Faction influenceOwner;
    private Faction owner;
    private Long millisecondsremaining;
    private Short barAnimRate;
    
    //Misc
    private BukkitTask runnable;
    protected Faction majorityController;
    private CapturableRegion region;

    public static void init()
    {
        //TODO check inside config what files have been defined for the faction icons.
        SpoutManager.getFileManager().addToCache(RegionControl.plugin, new File(RegionControl.plugin.getDataFolder().getAbsolutePath() + "/Capture_Anim_Losing.png"));
        SpoutManager.getFileManager().addToCache(RegionControl.plugin, new File(RegionControl.plugin.getDataFolder().getAbsolutePath() + "/Capture_Anim_Capturing.png"));
        SpoutManager.getFileManager().addToCache(RegionControl.plugin, new File(RegionControl.plugin.getDataFolder().getAbsolutePath() + "/faction.png"));
        SpoutManager.getFileManager().addToCache(RegionControl.plugin, new File(RegionControl.plugin.getDataFolder().getAbsolutePath() + "/faction2.png"));
        SpoutManager.getFileManager().addToCache(RegionControl.plugin, new File(RegionControl.plugin.getDataFolder().getAbsolutePath() + "/music.wav"));
    }
    /**
 * RegionControl UI Test - InDev 0.5 - Remarks and recommendations for implementation.
 * Ensure that before animating a texture that it has been defined.
 * Use Bukkit Runnables, not the scheduler
 * Pre-Cache textures.
    */

    public void setupClientElements(SpoutPlayer splayer) {
        millisecondsremaining = null;
        this.splayer = splayer;

        Screen screen = splayer.getMainScreen();
        
        //Always Visible
        Container regionInfo = (Container) new GenericContainer()
                .setLayout(ContainerType.HORIZONTAL)
                .setAlign(WidgetAnchor.CENTER_LEFT)
                .setAnchor(WidgetAnchor.CENTER_LEFT).setWidth(427)
                .setHeight(10).setX(10).setY(1);
        
        ownericon = (Texture) new GenericTexture().setMargin(0, 0, 0, 3)
                .setHeight(16).setWidth(16).setFixed(true);
        
        regionname = (Label) new GenericLabel().setResize(true)
        .setMargin(0, 3).setFixed(true);
        
        regionInfo.addChildren(ownericon, regionname);
        
        //Not Visible unless Capturing
        Container influenceOwnerIconContainer = (Container) new GenericContainer()
        .setLayout(ContainerType.OVERLAY)
        .setAlign(WidgetAnchor.CENTER_LEFT)
        .setAnchor(WidgetAnchor.CENTER_LEFT).setWidth(427)
        .setHeight(10).setX(5).setY(20);
        
        influenceownericon = (Texture) new GenericTexture()
        .setMargin(0, 0, 0, 3).setHeight(8).setWidth(8).setFixed(true);
        
        influenceOwnerIconContainer.addChild(influenceownericon);
        influenceownericon.setVisible(false);
        
        //Capture Bar
        Container captureBarContainer = (Container) new GenericContainer()
        .setLayout(ContainerType.OVERLAY)
        .setAlign(WidgetAnchor.CENTER_LEFT)
        .setAnchor(WidgetAnchor.CENTER_LEFT).setWidth(427)
        .setHeight(10).setX(20).setY(20); //Changed X to 20. (+10)
        
        captureBar = (Gradient) new GenericGradient().setHeight(10).setMargin(0, 3)
                .setFixed(true).setPriority(RenderPriority.High);
        
        captureBarContainer.addChild(captureBar);
        captureBar.setVisible(false);
        
        //Empty Capture Bar Overlay
        Container captureBarSpaceContainer = (Container) new GenericContainer()
        .setLayout(ContainerType.OVERLAY)
        .setAlign(WidgetAnchor.CENTER_RIGHT)
        .setAnchor(WidgetAnchor.CENTER_LEFT)
        .setHeight(10).setX(73).setY(20);
        
        captureBarSpace = (Gradient) new GenericGradient(new Color(0F, 0F, 0F, 1F)).setWidth(0) //Same here.
                .setHeight(10).setFixed(true).setPriority(RenderPriority.Low);
        
        captureBarSpaceContainer.addChild(captureBarSpace);
        captureBarSpace.setVisible(false);
        
        //Capture Bar Background
        Container captureBarBackgroundContainer = (Container) new GenericContainer()
        .setLayout(ContainerType.OVERLAY)
        .setAlign(WidgetAnchor.CENTER_LEFT)
        .setAnchor(WidgetAnchor.CENTER_LEFT).setWidth(427)
        .setHeight(12).setX(21).setY(19);
        
        captureBarBackground = (Gradient) new GenericGradient(new Color(0F, 0F, 0F, 1F)).setWidth(103)
                .setHeight(12).setFixed(true).setPriority(RenderPriority.Highest);
        
        captureBarBackgroundContainer.addChild(captureBarBackground);
        
        //Timer
        Container timerContainer = (Container) new GenericContainer()
        .setLayout(ContainerType.OVERLAY)
        .setAlign(WidgetAnchor.CENTER_LEFT)
        .setAnchor(WidgetAnchor.CENTER_LEFT).setWidth(427)
        .setHeight(10).setX(60).setY(21);
        
        capturetimer = (Label) new GenericLabel().setText("").setResize(true).setFixed(true).setPriority(RenderPriority.Lowest);
        
        timerContainer.addChild(capturetimer);
        
        //Capture Bar Animation
        //Add an if statement here to determine if bar should originate from the end of the bar, or the start of it. For promotional purposes, the anim will be
        //going down.
        Container barAnimContainer = (Container) new GenericContainer()
        .setLayout(ContainerType.OVERLAY)
        .setAlign(WidgetAnchor.CENTER_LEFT)
        .setAnchor(WidgetAnchor.CENTER_LEFT).setWidth(427)
        .setHeight(10).setMarginLeft(100).setY(20);
        
        captureBarAnim = (Texture) new GenericTexture().setDrawAlphaChannel(true)
                .setHeight(10).setFixed(true).setPriority(RenderPriority.Normal);
        
        barAnimContainer.addChild(captureBarAnim);
        
        screen.attachWidgets(RegionControl.plugin, regionInfo, influenceOwnerIconContainer, captureBarContainer, captureBarSpaceContainer, captureBarBackgroundContainer, timerContainer, barAnimContainer);
        screen.attachWidgets(RegionControl.plugin, ownericon, regionname, influenceownericon , captureBarBackground,captureBar, captureBarSpace, capturetimer,captureBarAnim);
    }
    
    public void updateRegion(CapturableRegion newregion)
    {
        this.region = newregion;
        this.owner = newregion.getOwner();
        ownericon.setUrl(factionIcons.get(owner));
        regionname.setText(newregion.getDisplayname());
        
        if(newregion.isBeingCaptured())
        {
            //Set all "capture" elements visible.
            
            millisecondsremaining = newregion.getExpectedCaptureTime() - System.currentTimeMillis();
            int influencerate = newregion.getTimer().getInfluenceRate();
            
            if(influencerate == 1)
            {
                barAnimRate = 5;
            }
            
            else if(influencerate == 2)
            {
                barAnimRate = 3;
            }
            
            else if(influencerate == 3)
            {
                barAnimRate = 1;
            }
            
            if(influencerate == 1 || influencerate == 2 || influencerate == 3)
            {
                captureBarAnim.animateStop(true);
                majorityController = newregion.getMajorityController();
                if(owner == influenceOwner)
                {
                    if(majorityController == owner)
                    {
                        captureBarAnim.setUrl("Capture_Anim_Capturing.png").setWidth(30);
                        captureBarAnim.animate(WidgetAnim.POS_X, 2.3F, (short) 40, barAnimRate, true, true).animateStart();
                    }
                    else if (majorityController != owner)
                    {
                        captureBarAnim.setUrl("Capture_Anim_Losing.png").setWidth(125);
                        captureBarAnim.animate(WidgetAnim.POS_X, -2.3F, (short) 40, barAnimRate, true, true).animateStart();
                    }
                }
                
                else if (owner != influenceOwner)
                {
                    if(majorityController == owner)
                    {
                        captureBarAnim.setUrl("Capture_Anim_Losing.png").setWidth(125);
                        captureBarAnim.animate(WidgetAnim.POS_X, -2.3F, (short) 40, barAnimRate, true, true).animateStart();
                    }
                    else if (majorityController != owner)
                    {
                        captureBarAnim.setUrl("Capture_Anim_Capturing.png").setWidth(30);
                        captureBarAnim.animate(WidgetAnim.POS_X, 2.3F, (short) 40, barAnimRate, true, true).animateStart();
                    }
                }
            }
            
            runnable = new BukkitRunnable() {
                @Override
                public void run() {
                    Integer seconds = (int) ((millisecondsremaining / 1000) % 60);
                    Integer minutes = (int) ((millisecondsremaining / (1000*60)));
                    
                    String secondsString = seconds.toString();
                    if(seconds < 10)
                    {
                        secondsString = "0" + secondsString;
                    }
                    capturetimer.setText(minutes.toString() + ":" + secondsString);
                    
                    int barwidth = (int) (region.getInfluence() / region.getBaseInfluence() * 100);
                    captureBar.setWidth(barwidth);
                    captureBarSpace.setWidth(100 - barwidth);
                    captureBar.setColor(factionColors.get(influenceOwner));
                }
                
            }.runTaskTimer(RegionControl.plugin, 20, 20);
        }
        
        else if(!newregion.isBeingCaptured() && runnable != null)
        {
            runnable.cancel();
            captureBarAnim.animateStop(false);
            
        }
    }
}
