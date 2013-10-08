package com.featherminecraft.RegionControl.spout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.gui.*;
import org.getspout.spoutapi.player.SpoutPlayer;


import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;
import com.featherminecraft.RegionControl.Faction;
import com.featherminecraft.RegionControl.RCPlayer;
import com.featherminecraft.RegionControl.RegionControl;

public class SpoutClientLogic {
    //Variables created from setup
    private Container controlPointsContainer;
    private Container backgroundContainer;
    private Label regionname;
    private Label captureTimer;
    private Gradient captureBar;
    private Texture captureBarAnim;
    private Gradient captureBarSpace;
    private Gradient captureBarBackground;
    private Texture ownericon;
    private Texture influenceOwnerIcon;
    
    //Bar Animation Info
    private Short barAnimRate;
    private Float barFloatValue;
    private Short barShortValue;
    
    //Definable Variables
    private Faction influenceOwner;
    private Faction owner;
    //Misc
    private BukkitTask runnable;
    protected Faction majorityController;
    private CapturableRegion region;
    private Container influenceOwnerIconContainer;
    private Container captureBarContainer;
    private Container captureBarSpaceContainer;
    private Container captureBarBackgroundContainer;
    private Container timerContainer;
    private Container barAnimContainer;
    private Container regionInfo;
    private Texture background;
    private List<Widget> screenElements = new ArrayList<Widget>();
    private boolean allElementsHidden;

    public static void init()
    {
        //TODO check inside config what files have been defined for the faction icons.
        //TODO Iterate over factions to find faction icon.
        
        if (!new File(RegionControl.plugin.getDataFolder(), "background.png").exists())
            RegionControl.plugin.saveResource("background.png", false);
        
        if (!new File(RegionControl.plugin.getDataFolder(), "Capture_Anim_Losing.png").exists())
            RegionControl.plugin.saveResource("Capture_Anim_Losing.png", false);
        
        if (!new File(RegionControl.plugin.getDataFolder(), "Capture_Anim_Capturing.png").exists())
            RegionControl.plugin.saveResource("Capture_Anim_Capturing.png", false);

        if (!new File(RegionControl.plugin.getDataFolder(), "null.png").exists())
            RegionControl.plugin.saveResource("null.png", false);
        
        if (!new File(RegionControl.plugin.getDataFolder(), "faction.png").exists())
            RegionControl.plugin.saveResource("faction.png", false);
        
        if (!new File(RegionControl.plugin.getDataFolder(), "faction2.png").exists())
            RegionControl.plugin.saveResource("faction2.png", false);
        
        if (!new File(RegionControl.plugin.getDataFolder(), "music.wav").exists())
            RegionControl.plugin.saveResource("music.wav", false);
        
        if (!new File(RegionControl.plugin.getDataFolder(), "captured.wav").exists())
            RegionControl.plugin.saveResource("captured.wav", false);
        
        if (!new File(RegionControl.plugin.getDataFolder(), "captured2.wav").exists())
            RegionControl.plugin.saveResource("captured2.wav", false);
        
        SpoutManager.getFileManager().addToCache(RegionControl.plugin, new File(RegionControl.plugin.getDataFolder().getAbsolutePath() + "/background.png"));
        SpoutManager.getFileManager().addToCache(RegionControl.plugin, new File(RegionControl.plugin.getDataFolder().getAbsolutePath() + "/Capture_Anim_Losing.png"));
        SpoutManager.getFileManager().addToCache(RegionControl.plugin, new File(RegionControl.plugin.getDataFolder().getAbsolutePath() + "/Capture_Anim_Capturing.png"));
        SpoutManager.getFileManager().addToCache(RegionControl.plugin, new File(RegionControl.plugin.getDataFolder().getAbsolutePath() + "/null.png"));
        SpoutManager.getFileManager().addToCache(RegionControl.plugin, new File(RegionControl.plugin.getDataFolder().getAbsolutePath() + "/faction.png"));
        SpoutManager.getFileManager().addToCache(RegionControl.plugin, new File(RegionControl.plugin.getDataFolder().getAbsolutePath() + "/faction2.png"));
        SpoutManager.getFileManager().addToCache(RegionControl.plugin, new File(RegionControl.plugin.getDataFolder().getAbsolutePath() + "/music.wav"));
        SpoutManager.getFileManager().addToCache(RegionControl.plugin, new File(RegionControl.plugin.getDataFolder().getAbsolutePath() + "/captured.wav"));
        SpoutManager.getFileManager().addToCache(RegionControl.plugin, new File(RegionControl.plugin.getDataFolder().getAbsolutePath() + "/captured2.wav"));
    }

    public void setupClientElements(RCPlayer rcplayer) {
        Screen screen = ((SpoutPlayer) rcplayer.getBukkitPlayer()).getMainScreen();
        
        Color factioncolor = new Color(1F, 0, 0, 0.8F);
        
        //Background
        backgroundContainer = (Container) new GenericContainer()
        .setLayout(ContainerType.VERTICAL)
        .setAlign(WidgetAnchor.CENTER_LEFT)
        .setAnchor(WidgetAnchor.CENTER_LEFT)
        .setWidth(150)
        .setHeight(80)
        .setX(3)
        .setY(-5);
        
        screenElements.add(backgroundContainer);
        
        background = (Texture) new GenericTexture("background.png")
        .setDrawAlphaChannel(true)
        .setHeight(80)
        .setWidth(150)
        .setPriority(RenderPriority.Highest);
        
        screenElements.add(background);
        
        backgroundContainer.addChild(
                background
                );
        
        //Region Info
        regionInfo = (Container) new GenericContainer()
                .setLayout(ContainerType.HORIZONTAL)
                .setAlign(WidgetAnchor.CENTER_LEFT)
                .setAnchor(WidgetAnchor.CENTER_LEFT)
                .setWidth(145)
                .setHeight(10)
                .setX(10)
                .setY(1)
                .setMargin(3);
        
        screenElements.add(regionInfo);
        
        ownericon = (Texture) new GenericTexture().setUrl("faction.png")
        .setHeight(16).setWidth(16).setFixed(true);
        
        screenElements.add(ownericon);
        
        regionname = (Label) ((Label) new GenericLabel().setText(rcplayer.getCurrentRegion().getDisplayName()).
                setMargin(0, 3)).setShadow(false).setResize(true).setFixed(true);
        
        screenElements.add(regionname);
        
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
        .setX(50)
        .setY(25)
        .setMarginRight(1);
        
        screenElements.add(controlPointsContainer);
        
        //TODO Iterate to display the amount of controlpoints in a region
        Label controlPointA = (Label) new GenericLabel().setText("A").setTextColor(new Color(1F,0,0)).setScale(1.5F).setShadow(false).setResize(true).setAlign(WidgetAnchor.CENTER_CENTER).setFixed(true).setMargin(5);
        screenElements.add(controlPointA);
        Label controlPointB = (Label) new GenericLabel().setText("B").setTextColor(new Color(1F,0,0)).setScale(1.5F).setShadow(false).setResize(true).setAlign(WidgetAnchor.CENTER_CENTER).setFixed(true).setMargin(5);
        screenElements.add(controlPointB);
        Label controlPointC = (Label) new GenericLabel().setText("C").setTextColor(new Color(1F,0,0)).setScale(1.5F).setShadow(false).setResize(true).setAlign(WidgetAnchor.CENTER_CENTER).setFixed(true).setMargin(5);
        screenElements.add(controlPointC);
        
        controlPointsContainer.addChildren(controlPointA, controlPointB, controlPointC);
        
        //Capture Icons Goes here
        influenceOwnerIconContainer = (Container) new GenericContainer()
        .setLayout(ContainerType.OVERLAY)
        .setAlign(WidgetAnchor.CENTER_LEFT)
        .setAnchor(WidgetAnchor.CENTER_LEFT).setWidth(427)
        .setHeight(10).setX(5).setY(40);
        
        screenElements.add(influenceOwnerIconContainer);
        
        influenceOwnerIcon = (Texture) new GenericTexture("faction.png")
        .setMargin(0, 0, 0, 3).setHeight(8).setWidth(8).setFixed(true);
        
        screenElements.add(influenceOwnerIcon);
        
        influenceOwnerIconContainer.addChild(influenceOwnerIcon);
        
        //Capture Bar
        captureBarContainer = (Container) new GenericContainer()
        .setLayout(ContainerType.OVERLAY)
        .setAlign(WidgetAnchor.CENTER_LEFT)
        .setAnchor(WidgetAnchor.CENTER_LEFT).setWidth(427)
        .setHeight(10).setX(20).setY(40); //Changed X to 20. (+10)
        
        screenElements.add(captureBarContainer);
        
        captureBar = (Gradient) new GenericGradient(factioncolor).setWidth(100)
                .setHeight(10).setMargin(0, 3).setFixed(true).setPriority(RenderPriority.High);
        
        screenElements.add(captureBar);
        
        captureBarContainer.addChild(captureBar);
        
        //Empty Capture Bar Overlay
        captureBarSpaceContainer = (Container) new GenericContainer()
        .setLayout(ContainerType.OVERLAY)
        .setAlign(WidgetAnchor.CENTER_RIGHT)
        .setAnchor(WidgetAnchor.CENTER_LEFT)
        .setHeight(10).setX(73).setY(40);
        
        screenElements.add(captureBarSpaceContainer);
        
        captureBarSpace = (Gradient) new GenericGradient(new Color(0F, 0F, 0F, 1F)).setWidth(0)
                .setHeight(10).setFixed(true).setPriority(RenderPriority.Low);
        
        screenElements.add(captureBarSpace);
        
        captureBarSpaceContainer.addChild(captureBarSpace);
        
        //Capture Bar Background
        captureBarBackgroundContainer = (Container) new GenericContainer()
        .setLayout(ContainerType.OVERLAY)
        .setAlign(WidgetAnchor.CENTER_LEFT)
        .setAnchor(WidgetAnchor.CENTER_LEFT).setWidth(427)
        .setHeight(12).setX(21).setY(39);
        
        screenElements.add(captureBarBackgroundContainer);
        
        captureBarBackground = (Gradient) new GenericGradient(new Color(0F, 0F, 0F, 1F)).setWidth(103)
                .setHeight(12).setFixed(true).setPriority(RenderPriority.Highest);
        
        screenElements.add(captureBarBackground);
        
        captureBarBackgroundContainer.addChild(captureBarBackground);
        
        //Timer
        timerContainer = (Container) new GenericContainer()
        .setLayout(ContainerType.OVERLAY)
        .setAlign(WidgetAnchor.CENTER_LEFT)
        .setAnchor(WidgetAnchor.CENTER_LEFT).setWidth(427)
        .setHeight(10).setX(60).setY(41);
        
        screenElements.add(timerContainer);
        
        captureTimer = (Label) new GenericLabel().setText(" ").setResize(true).setFixed(true).setPriority(RenderPriority.Lowest);
        
        screenElements.add(captureTimer);
        
        timerContainer.addChild(captureTimer);
        
        //Capture Bar Animation
        //Add an if statement here to determine if bar should originate from the end of the bar, or the start of it. For promotional purposes, the anim will be
        //going down.
        barAnimContainer = (Container) new GenericContainer()
        .setLayout(ContainerType.OVERLAY)
        .setAlign(WidgetAnchor.CENTER_LEFT)
        .setAnchor(WidgetAnchor.CENTER_LEFT).setWidth(427)
        .setHeight(10).setMarginLeft(100).setY(40);
        
        screenElements.add(barAnimContainer);
        
        captureBarAnim = (Texture) new GenericTexture().setDrawAlphaChannel(true)
                .setHeight(10).setFixed(true).setPriority(RenderPriority.Normal).setVisible(false);
        
        screenElements.add(captureBarAnim);
        
        captureBarAnim.setUrl("null.png").setWidth(125);
        captureBarAnim.setVisible(false);
        captureBar.setVisible(false);
        captureBarSpace.setVisible(false);
        captureBarBackground.setVisible(false);
        captureTimer.setVisible(false);
        influenceOwnerIcon.setVisible(false);
        
        barAnimContainer.addChild(captureBarAnim);
        
        screen.attachWidgets(RegionControl.plugin, backgroundContainer, regionInfo,controlPointsContainer, influenceOwnerIconContainer, captureBarContainer, captureBarSpaceContainer, captureBarBackgroundContainer, timerContainer, barAnimContainer);
        screen.attachWidgets(RegionControl.plugin, background , ownericon, regionname,controlPointA,controlPointB,controlPointC, influenceOwnerIcon , captureBarBackground,captureBar, captureBarSpace, captureTimer,captureBarAnim);
        
    }
    
    public void updateRegion(CapturableRegion updatedRegion)
    {
        if(updatedRegion == null)
        {
            if(allElementsHidden == false)
            {
                hideAllElements();
            }
            return;
        }
        
        else if(updatedRegion != null && allElementsHidden == true)
        {
            showAllElements();
        }
        
        this.region = updatedRegion;
        this.owner = updatedRegion.getOwner();
        this.influenceOwner = updatedRegion.getOwner();
        
        //ownericon.setUrl(region.getOwner().getFactionIconUrl()); //TODO
        ownericon.setUrl("faction.png");
        regionname.setText(region.getDisplayName());
        
        if(region.isBeingCaptured())
        {
            Integer red = region.getInfluenceOwner().getFactionColor().getRed();
            Integer green = region.getInfluenceOwner().getFactionColor().getGreen();
            Integer blue = region.getInfluenceOwner().getFactionColor().getBlue();
            Integer alpha = region.getInfluenceOwner().getFactionColor().getAlpha();
            
            Color spoutColor = new Color(red,green,blue,alpha);
            captureBar.setColor(spoutColor).setVisible(true);
            captureBarSpace.setVisible(true);
            captureBarBackground.setVisible(true);
            captureTimer.setVisible(true);
            influenceOwnerIcon.setVisible(true);
            backgroundContainer.setHeight(70);
            
            Float influencerate = region.getInfluenceRate();
            
            if(influencerate == 1)
            {
                barAnimRate = 7;
                barFloatValue = 0.92F;
                barShortValue = 100;
            }
            
            else if(influencerate == 2)
            {
                barAnimRate = 5;
                barFloatValue = 1.15F;
                barShortValue = 100;
            }
            
            else if(influencerate == 3)
            {
                barAnimRate = 3;
                barFloatValue = 1.55F;
                barShortValue = 100;
            }
            
            else if(influencerate == 4)
            {
                barAnimRate = 1;
                barFloatValue = 2.35F;
                barShortValue = 100;
            }
            
            if(influencerate == 1 || influencerate == 2 || influencerate == 3 || influencerate == 4)
            {
                captureBarAnim.animateStop(true);
                majorityController = updatedRegion.getMajorityController();
                if(owner == influenceOwner)
                {
                    if(majorityController == owner)
                    {
                        captureBarAnim.setUrl("Capture_Anim_Capturing.png").setWidth(30);
                        captureBarAnim.animate(WidgetAnim.POS_X, barFloatValue, barShortValue, barAnimRate, true, true).animateStart();
                    }
                    else if (majorityController != owner)
                    {
                        captureBarAnim.setUrl("Capture_Anim_Losing.png").setWidth(125);
                        captureBarAnim.animate(WidgetAnim.POS_X, -barFloatValue, barShortValue, barAnimRate, true, true).animateStart();
                    }
                }
                
                else if (owner != influenceOwner)
                {
                    if(majorityController == owner)
                    {
                        captureBarAnim.setUrl("Capture_Anim_Losing.png").setWidth(125);
                        captureBarAnim.animate(WidgetAnim.POS_X, -barFloatValue, barShortValue, barAnimRate, true, true).animateStart();
                    }
                    else if (majorityController != owner)
                    {
                        captureBarAnim.setUrl("Capture_Anim_Capturing.png").setWidth(30);
                        captureBarAnim.animate(WidgetAnim.POS_X, barFloatValue, barShortValue, barAnimRate, true, true).animateStart();
                    }
                }
            }
            
            runnable = new BukkitRunnable() {
                @Override
                public void run() {
                    Integer seconds = region.getSecondsToCapture();
                    Integer minutes = region.getMinutesToCapture();
                    
                    String secondsString = seconds.toString();
                    if(seconds < 10)
                    {
                        secondsString = "0" + secondsString;
                    }
                    captureTimer.setText(minutes.toString() + ":" + secondsString);
                    
                    float influence = region.getInfluenceMap().get(region.getInfluenceOwner());
                    float baseinfluence = region.getBaseInfluence();
                    
                    int barwidth = (int) (influence / baseinfluence * 100);
                    captureBar.setWidth(barwidth);
                    captureBarSpace.setWidth(100 - barwidth);
                    
                    Integer red = region.getInfluenceOwner().getFactionColor().getRed();
                    Integer green = region.getInfluenceOwner().getFactionColor().getGreen();
                    Integer blue = region.getInfluenceOwner().getFactionColor().getBlue();
                    Integer alpha = region.getInfluenceOwner().getFactionColor().getAlpha();
                    
                    Color spoutColor = new Color(red,green,blue,alpha);
                    captureBar.setColor(spoutColor).setVisible(true);
                }
                
            }.runTaskTimer(RegionControl.plugin, 20, 20);
        }
        
        else if(!updatedRegion.isBeingCaptured() && runnable != null)
        {
            runnable.cancel();
            captureBarAnim.animateStop(false).setVisible(false);
            captureBar.setVisible(false);
            captureBarSpace.setVisible(false);
            captureBarBackground.setVisible(false);
            captureTimer.setVisible(false);
            influenceOwnerIcon.setVisible(false);
            backgroundContainer.setHeight(40);
        }
    }
    
    public void hideAllElements()
    {
        if(!allElementsHidden)
        {
            for(Widget element : screenElements)
            {
                element.setVisible(false);
            }
            allElementsHidden = true;
        }
    }
    
    public void showAllElements()
    {
        if(allElementsHidden)
        {
            for(Widget element : screenElements)
            {
                element.setVisible(true);
            }
            allElementsHidden = false;
        }
    }
}
