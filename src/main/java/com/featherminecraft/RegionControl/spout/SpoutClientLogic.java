package com.featherminecraft.RegionControl.spout;

import java.io.File;
import java.util.Map;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.gui.*;
import org.getspout.spoutapi.player.SpoutPlayer;


import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;
import com.featherminecraft.RegionControl.Faction;
import com.featherminecraft.RegionControl.RegionControl;

public class SpoutClientLogic {

    //Needs to be initalised before main setup begins.
    private SpoutPlayer splayer;
    private Map<Faction,String> factionIcons;
    private Map<Faction,Color> factionColors;
    
    //Variables created from setup
    private Container controlPointsContainer;
    private Container backgroundContainer;
    private Label controlPointC;
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
        SpoutManager.getFileManager().addToCache(RegionControl.plugin, new File(RegionControl.plugin.getDataFolder().getAbsolutePath() + "/background_top.png"));
        SpoutManager.getFileManager().addToCache(RegionControl.plugin, new File(RegionControl.plugin.getDataFolder().getAbsolutePath() + "/background_bottom.png"));
        SpoutManager.getFileManager().addToCache(RegionControl.plugin, new File(RegionControl.plugin.getDataFolder().getAbsolutePath() + "/Capture_Anim_Losing.png"));
        SpoutManager.getFileManager().addToCache(RegionControl.plugin, new File(RegionControl.plugin.getDataFolder().getAbsolutePath() + "/Capture_Anim_Capturing.png"));
        SpoutManager.getFileManager().addToCache(RegionControl.plugin, new File(RegionControl.plugin.getDataFolder().getAbsolutePath() + "/null.png"));
        SpoutManager.getFileManager().addToCache(RegionControl.plugin, new File(RegionControl.plugin.getDataFolder().getAbsolutePath() + "/faction.png"));
        SpoutManager.getFileManager().addToCache(RegionControl.plugin, new File(RegionControl.plugin.getDataFolder().getAbsolutePath() + "/faction2.png"));
        SpoutManager.getFileManager().addToCache(RegionControl.plugin, new File(RegionControl.plugin.getDataFolder().getAbsolutePath() + "/music.wav"));
        SpoutManager.getFileManager().addToCache(RegionControl.plugin, new File(RegionControl.plugin.getDataFolder().getAbsolutePath() + "/captured.wav"));
        SpoutManager.getFileManager().addToCache(RegionControl.plugin, new File(RegionControl.plugin.getDataFolder().getAbsolutePath() + "/captured2.wav"));
    }
    /**
 * RegionControl UI Test - InDev 0.5 - Remarks and recommendations for implementation.
 * Ensure that before animating a texture that it has been defined.
 * Use Bukkit Runnables, not the scheduler
 * Pre-Cache textures.
    */

    /**
 * RegionControl UI Test 2 - InDev 0.5 - Remarks and recommendations for implementation.
 * When Dividing or getting percentages, both values MUST BE FLOATS.
    */

    public void setupClientElements(SpoutPlayer splayer) {
        millisecondsremaining = null;
        this.splayer = splayer;

        Screen screen = splayer.getMainScreen();
        
        Color factioncolor = new Color(1F, 0, 0, 0.8F);
        
        //Background
        backgroundContainer = (Container) new GenericContainer()
        .setLayout(ContainerType.VERTICAL)
        .setAlign(WidgetAnchor.CENTER_LEFT)
        .setAnchor(WidgetAnchor.CENTER_LEFT)
        .setWidth(150)
        .setHeight(40)
        .setX(3)
        .setY(-5);
        
        Texture toptexture = (Texture) new GenericTexture("background_top.png")
        .setDrawAlphaChannel(true)
        .setHeight(10)
        .setWidth(150)
        .setPriority(RenderPriority.Highest);
        
        Gradient background = (Gradient) new GenericGradient(new Color(0, 0, 0, 0.7F))
        .setHeight(60)
        .setWidth(150)
        .setPriority(RenderPriority.Highest);
        
        Texture bottomtexture = (Texture) new GenericTexture("background_bottom.png")
        .setDrawAlphaChannel(true)
        .setHeight(10)
        .setWidth(150)
        .setPriority(RenderPriority.Highest);
        
        backgroundContainer.addChildren(
                toptexture,
                background,
                bottomtexture
                );
        
        //Region Info
        Container regionInfo = (Container) new GenericContainer()
                .setLayout(ContainerType.HORIZONTAL)
                .setAlign(WidgetAnchor.CENTER_LEFT)
                .setAnchor(WidgetAnchor.CENTER_LEFT)
                .setWidth(145)
                .setHeight(10)
                .setX(10)
                .setY(1)
                .setMargin(3);
        
        ownericon = (Texture) new GenericTexture().setUrl("faction.png")
        .setHeight(16).setWidth(16).setFixed(true);
        
        Label regionname = (Label) ((Label) new GenericLabel().setText("Mekka Valley Checkpoint").
                setMargin(0, 3)).setShadow(false).setResize(true).setFixed(true);
        
        float currentscale = 1F;
        while(GenericLabel.getStringWidth(regionname.getText(), currentscale) > 125)
        {
            currentscale -= 0.05F;
        }
        regionname.setScale(currentscale);
        
        regionInfo.addChildren(ownericon, regionname);
        
        //Control Points
        //For Container Width, recommended to get amount of control points from region.
        controlPointsContainer = (Container) new GenericContainer()
        .setLayout(ContainerType.HORIZONTAL)
        .setAlign(WidgetAnchor.CENTER_CENTER)
        .setAnchor(WidgetAnchor.CENTER_LEFT)
        .setWidth(50)
        .setHeight(10)
        .setX(55)
        .setY(20)
        .setMarginRight(1);
        
        Label controlPointA = (Label) new GenericLabel().setText("A").setScale(1.5F).setShadow(false).setResize(true).setAlign(WidgetAnchor.CENTER_CENTER).setFixed(true).setMargin(5);
        Label controlPointB = (Label) new GenericLabel().setText("B").setScale(1.5F).setShadow(false).setResize(true).setAlign(WidgetAnchor.CENTER_CENTER).setFixed(true).setMargin(5);
        controlPointC = (Label) new GenericLabel().setText("C").setScale(1.5F).setShadow(false).setResize(true).setAlign(WidgetAnchor.CENTER_CENTER).setFixed(true).setMargin(5);
        
        controlPointsContainer.addChildren(controlPointA, controlPointB, controlPointC);
        
        //Capture Icons Goes here
        Container influenceOwnerIconContainer = (Container) new GenericContainer()
        .setLayout(ContainerType.OVERLAY)
        .setAlign(WidgetAnchor.CENTER_LEFT)
        .setAnchor(WidgetAnchor.CENTER_LEFT).setWidth(427)
        .setHeight(10).setX(5).setY(40);
        
        influenceownericon = (Texture) new GenericTexture("faction.png")
        .setMargin(0, 0, 0, 3).setHeight(8).setWidth(8).setFixed(true);
        
        influenceOwnerIconContainer.addChild(influenceownericon);
        
        //Capture Bar
        Container captureBarContainer = (Container) new GenericContainer()
        .setLayout(ContainerType.OVERLAY)
        .setAlign(WidgetAnchor.CENTER_LEFT)
        .setAnchor(WidgetAnchor.CENTER_LEFT).setWidth(427)
        .setHeight(10).setX(20).setY(40); //Changed X to 20. (+10)
        
        
        captureBar = (Gradient) new GenericGradient(factioncolor).setWidth(100)
                .setHeight(10).setMargin(0, 3).setFixed(true).setPriority(RenderPriority.High);
        
        captureBarContainer.addChild(captureBar);
        
        //Empty Capture Bar Overlay
        Container captureBarSpaceContainer = (Container) new GenericContainer()
        .setLayout(ContainerType.OVERLAY)
        .setAlign(WidgetAnchor.CENTER_RIGHT)
        .setAnchor(WidgetAnchor.CENTER_LEFT)
        .setHeight(10).setX(73).setY(40);
        
        captureBarSpace = (Gradient) new GenericGradient(new Color(0F, 0F, 0F, 1F)).setWidth(0)
                .setHeight(10).setFixed(true).setPriority(RenderPriority.Low);
        
        captureBarSpaceContainer.addChild(captureBarSpace);
        
        //Capture Bar Background
        Container captureBarBackgroundContainer = (Container) new GenericContainer()
        .setLayout(ContainerType.OVERLAY)
        .setAlign(WidgetAnchor.CENTER_LEFT)
        .setAnchor(WidgetAnchor.CENTER_LEFT).setWidth(427)
        .setHeight(12).setX(21).setY(39);
        
        captureBarBackground = (Gradient) new GenericGradient(new Color(0F, 0F, 0F, 1F)).setWidth(103)
                .setHeight(12).setFixed(true).setPriority(RenderPriority.Highest);
        
        captureBarBackgroundContainer.addChild(captureBarBackground);
        
        //Timer
        Container timerContainer = (Container) new GenericContainer()
        .setLayout(ContainerType.OVERLAY)
        .setAlign(WidgetAnchor.CENTER_LEFT)
        .setAnchor(WidgetAnchor.CENTER_LEFT).setWidth(427)
        .setHeight(10).setX(60).setY(41);
        
        capturetimer = (Label) new GenericLabel().setText(" ").setResize(true).setFixed(true).setPriority(RenderPriority.Lowest);
        
        timerContainer.addChild(capturetimer);
        
        Container barAnimContainer = (Container) new GenericContainer()
        .setLayout(ContainerType.OVERLAY)
        .setAlign(WidgetAnchor.CENTER_LEFT)
        .setAnchor(WidgetAnchor.CENTER_LEFT).setWidth(427)
        .setHeight(10).setMarginLeft(100).setY(40);
        
        captureBarAnim = (Texture) new GenericTexture().setDrawAlphaChannel(true)
                .setHeight(10).setFixed(true).setPriority(RenderPriority.Normal).setVisible(false);
        
        captureBarAnim.setUrl("null.png").setWidth(125);
        captureBarAnim.setVisible(false);
        captureBar.setVisible(false);
        captureBarSpace.setVisible(false);
        captureBarBackground.setVisible(false);
        capturetimer.setVisible(false);
        influenceownericon.setVisible(false);
        
        barAnimContainer.addChild(captureBarAnim);
        
        screen.attachWidgets(RegionControl.plugin, backgroundContainer, regionInfo,controlPointsContainer, influenceOwnerIconContainer, captureBarContainer, captureBarSpaceContainer, captureBarBackgroundContainer, timerContainer, barAnimContainer);
        screen.attachWidgets(RegionControl.plugin, toptexture, background, bottomtexture , ownericon, regionname,controlPointA,controlPointB,controlPointC, influenceownericon , captureBarBackground,captureBar, captureBarSpace, capturetimer,captureBarAnim);
    }
    
    public void updateRegion(CapturableRegion newregion)
    {
        this.region = newregion;
        this.owner = newregion.getOwner();
        ownericon.setUrl(factionIcons.get(owner));
        regionname.setText(newregion.getDisplayName());
        
        if(newregion.isBeingCaptured())
        {
            //Set all "capture" elements visible.
            
            Float influencerate = newregion.getInfluenceRate();
            
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
                    
                    float influence = region.getInfluenceMap().get(region.getInfluenceOwner());
                    float baseinfluence = region.getBaseInfluence();
                    
                    int barwidth = (int) (influence / baseinfluence * 100);
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
