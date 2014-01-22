package com.featherminecraft.RegionControl.spout;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.gui.Color;
import org.getspout.spoutapi.gui.Container;
import org.getspout.spoutapi.gui.ContainerType;
import org.getspout.spoutapi.gui.GenericContainer;
import org.getspout.spoutapi.gui.GenericGradient;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericTexture;
import org.getspout.spoutapi.gui.Gradient;
import org.getspout.spoutapi.gui.InGameHUD;
import org.getspout.spoutapi.gui.Label;
import org.getspout.spoutapi.gui.RenderPriority;
import org.getspout.spoutapi.gui.Texture;
import org.getspout.spoutapi.gui.Widget;
import org.getspout.spoutapi.gui.WidgetAnchor;
import org.getspout.spoutapi.gui.WidgetAnim;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.featherminecraft.RegionControl.Faction;
import com.featherminecraft.RegionControl.RCPlayer;
import com.featherminecraft.RegionControl.RegionControl;
import com.featherminecraft.RegionControl.ServerLogic;
import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;
import com.featherminecraft.RegionControl.capturableregion.ControlPoint;
import com.featherminecraft.RegionControl.utils.PlayerUtils;

public class SpoutClientLogic
{
    public static void init()
    {
        // Precache Included Assets
        if(!new File(RegionControl.plugin.getDataFolder(), "assets/images/background.png").exists())
        {
            RegionControl.plugin.saveResource("assets/images/background.png", false);
        }
        
        if(!new File(RegionControl.plugin.getDataFolder(), "assets/images/Capture_Anim_Losing.png").exists())
        {
            RegionControl.plugin.saveResource("assets/images/Capture_Anim_Losing.png", false);
        }
        
        if(!new File(RegionControl.plugin.getDataFolder(), "assets/images/Capture_Anim_Capturing.png").exists())
        {
            RegionControl.plugin.saveResource("assets/images/Capture_Anim_Capturing.png", false);
        }
        
        if(!new File(RegionControl.plugin.getDataFolder(), "assets/images/null.png").exists())
        {
            RegionControl.plugin.saveResource("assets/images/null.png", false);
        }
        
        SpoutManager.getFileManager().addToCache(RegionControl.plugin, new File(RegionControl.plugin.getDataFolder().getAbsolutePath() + "/assets/images/background.png"));
        SpoutManager.getFileManager().addToCache(RegionControl.plugin, new File(RegionControl.plugin.getDataFolder().getAbsolutePath() + "/assets/images/Capture_Anim_Losing.png"));
        SpoutManager.getFileManager().addToCache(RegionControl.plugin, new File(RegionControl.plugin.getDataFolder().getAbsolutePath() + "/assets/images/Capture_Anim_Capturing.png"));
        SpoutManager.getFileManager().addToCache(RegionControl.plugin, new File(RegionControl.plugin.getDataFolder().getAbsolutePath() + "/assets/images/null.png"));
        
        // Precache Faction Icons
        for(Faction faction : ServerLogic.factions.values())
        {
            if(faction.getFactionColor().getFactionIcon() != null && faction.getFactionColor().getFactionIcon() != "" && new File(RegionControl.plugin.getDataFolder().getAbsolutePath() + "/assets/images/factionIcons/" + faction.getFactionColor().getFactionIcon()).exists())
            {
                SpoutManager.getFileManager().addToCache(RegionControl.plugin, new File(RegionControl.plugin.getDataFolder().getAbsolutePath() + "/assets/images/factionIcons/" + faction.getFactionColor().getFactionIcon()));
            }
            // TODO Precache Faction Music/Voiceovers
        }
    }
    
    // Player Variables
    private RCPlayer rcPlayer;
    private SpoutPlayer sPlayer;
    private InGameHUD screen;
    private CapturableRegion region;
    private ControlPoint controlPoint;
    
    // GUI
    private List<Widget> screenElements = new ArrayList<Widget>();
    private List<Widget> screenCaptureElements = new ArrayList<Widget>();
    private boolean allElementsHidden = false;
    private boolean captureElementsHidden = true;
    
    // Region Info Background
    private Texture background = (Texture) new GenericTexture("background.png")
    .setDrawAlphaChannel(true)
    .setAnchor(WidgetAnchor.CENTER_LEFT)
    .setWidth(150).setHeight(40).setX(3).setY(-5)
    .setPriority(RenderPriority.Highest);
    
    // Region Info
    private Texture ownerIcon = (Texture) new GenericTexture("null.png")
    .setDrawAlphaChannel(true)
    .setMargin(-5, 6, 0, 0)
    .setFixed(true)
    .setWidth(16).setHeight(16);
    
    private Label regionName = (Label) new GenericLabel("A Region")
    .setShadow(false).setResize(true)
    .setFixed(true);
    
    private Container regionInfo = (Container) new GenericContainer(ownerIcon,regionName)
    .setLayout(ContainerType.HORIZONTAL).setAlign(WidgetAnchor.CENTER_LEFT)
    .setAnchor(WidgetAnchor.CENTER_LEFT)
    .setWidth(145).setHeight(10).setX(10).setY(3);
    
    // Region Status (Enemies/Allies Detected)
    private Label alliesDetectedText = (Label) new GenericLabel("Allies Detected: ")
    .setShadow(false).setScale(0.5F).setResize(true)
    .setFixed(true);
    
    private Label enemiesDetectedText = (Label) new GenericLabel("Enemies Detected: ")
    .setShadow(false).setScale(0.5F).setResize(true)
    .setFixed(true);
    
    private Gradient alliesDetectedBar = (Gradient) new GenericGradient(new Color(0, 0, 255))
    .setFixed(true)
    .setWidth(65).setHeight(5)
    .setPriority(RenderPriority.High);
    
    private Gradient enemiesDetectedBar = (Gradient) new GenericGradient(new Color(255, 0, 0))
    .setFixed(true)
    .setWidth(65).setHeight(5)
    .setPriority(RenderPriority.High);
    
    private Container regionStatusContainer = (Container) new GenericContainer(alliesDetectedText,enemiesDetectedText)
    .setLayout(ContainerType.VERTICAL).setAlign(WidgetAnchor.CENTER_CENTER)
    .setAnchor(WidgetAnchor.CENTER_LEFT)
    .setWidth(145).setHeight(12).setX(45).setY(18);
    
    private Container regionStatusBarContainer = (Container) new GenericContainer(alliesDetectedBar,enemiesDetectedBar)
    .setLayout(ContainerType.VERTICAL).setAlign(WidgetAnchor.CENTER_CENTER)
    .setAnchor(WidgetAnchor.CENTER_LEFT)
    .setWidth(145).setHeight(12).setX(45).setY(17);
    
    // Control Points
    private ArrayList<Label> controlPointLabels = new ArrayList<Label>();
    private List<Widget> controlPointScreenElements = new ArrayList<Widget>();
    
    private Container controlPointsContainer = (Container) new GenericContainer()
    .setLayout(ContainerType.HORIZONTAL).setAlign(WidgetAnchor.CENTER_CENTER)
    .setAnchor(WidgetAnchor.CENTER_LEFT)
    .setWidth(50).setHeight(10).setX(25).setY(25);
    
    // Control Point Capture Bar
    private Gradient controlPointCaptureBar = (Gradient) new GenericGradient(new Color(255, 255, 255, 255))
    .setAnchor(WidgetAnchor.TOP_CENTER)
    .setWidth(150).setHeight(5).setX(-75).setY(32);
    
    private Gradient controlPointCaptureBarBackground = (Gradient) new GenericGradient(new Color(0F, 0F, 0F, 1F))
    .setAnchor(WidgetAnchor.TOP_CENTER)
    .setWidth(154).setHeight(7).setX(-77).setY(31)
    .setPriority(RenderPriority.High);
    
    // Cannot Capture Control Point Indicator
    private ArrayList<Label> reasonWidgetList = new ArrayList<Label>();
    
    // Influence Owner Icon
    private Texture influenceOwnerIcon = (Texture) new GenericTexture("null.png")
    .setAnchor(WidgetAnchor.CENTER_LEFT)
    .setWidth(8).setHeight(8).setX(5).setY(40);
    
    // Capture Bar
    private Gradient captureBar = (Gradient) new GenericGradient(new Color(255, 255, 255, 255))
    .setAnchor(WidgetAnchor.CENTER_LEFT)
    .setWidth(100).setHeight(10).setX(23).setY(40)
    .setPriority(RenderPriority.High);
    
    private Gradient captureBarSpace = (Gradient) new GenericGradient(new Color(0F, 0F, 0F, 1F))
    .setFixed(true)
    .setWidth(0).setHeight(10)
    .setPriority(RenderPriority.Low);
    
    private Gradient captureBarBackground = (Gradient) new GenericGradient(new Color(0F, 0F, 0F, 1F))
    .setAnchor(WidgetAnchor.CENTER_LEFT)
    .setWidth(104).setHeight(12).setX(21).setY(39)
    .setPriority(RenderPriority.Highest);
    
    private Container captureBarSpaceContainer = (Container) new GenericContainer(captureBarSpace)
    .setLayout(ContainerType.OVERLAY).setAlign(WidgetAnchor.CENTER_RIGHT)
    .setAnchor(WidgetAnchor.CENTER_LEFT)
    .setHeight(10).setX(73).setY(40);
    
    // Timer
    private Label captureTimer = (Label) new GenericLabel("0:00")
    .setResize(true)
    .setAnchor(WidgetAnchor.CENTER_LEFT)
    .setPriority(RenderPriority.Lowest)
    .setX(60).setY(41);
    
    // Capture Bar Animation
    private Texture captureBarAnim = (Texture) new GenericTexture("null.png")
    .setDrawAlphaChannel(true)
    .setAnchor(WidgetAnchor.CENTER_LEFT)
    .setHeight(10).setY(40)
    .setPriority(RenderPriority.Normal).setVisible(false);
    
    public ControlPoint getControlPoint()
    {
        return controlPoint;
    }
    
    public void hideControlPointCaptureBar()
    {
        if(controlPointCaptureBar.isVisible())
        {
            for(Widget element : controlPointScreenElements)
            {
                element.setVisible(false);
            }
            
            if(reasonWidgetList != null && reasonWidgetList.size() > 0)
            {
                for(Label widget : reasonWidgetList)
                {
                    screen.removeWidget(widget);
                }
                
                reasonWidgetList = new ArrayList<Label>();
            }
        }
    }
    
    public void setControlPoint(ControlPoint controlPoint)
    {
        this.controlPoint = controlPoint;
    }
    
    public void setRegionCaptureStatus(Boolean captureStatus)
    {
        if(captureStatus == false)
        {
            hideCaptureElements();
            if(background.getHeight() != 40)
            {
                background.setHeight(40);
            }
            captureBarAnim.animateStop(false);
            // SpoutManager.getSoundManager().stopMusic(splayer, false, 1000);
        }
        
        else if(captureStatus == true)
        {
            showCaptureElements();
            if(background.getHeight() != 70)
            {
                background.setHeight(70);
            }
        }
    }
    
    public void setupClientElements(RCPlayer rPlayer)
    {
        rcPlayer = rPlayer;
        sPlayer = ((SpoutPlayer) rPlayer.getBukkitPlayer());
        screen = sPlayer.getMainScreen();
        region = rPlayer.getCurrentRegion();
        
        // Background
        screenElements.add(background);
        
        // Region Info
        screenElements.add(regionInfo);
        screenElements.add(ownerIcon);
        screenElements.add(regionName);
        
        // Region Status
        screenElements.add(regionStatusContainer);
        screenElements.add(regionStatusBarContainer);
        screenElements.add(alliesDetectedText);
        screenElements.add(alliesDetectedBar);
        screenElements.add(enemiesDetectedBar);
        
        // Control Points
        screenElements.add(controlPointsContainer);
        
        for(ControlPoint controlPoint : region.getControlPoints())
        {
            Color spoutColor = new Color(255, 255, 255);
            if(!controlPoint.isCapturing())
            {
                Integer red = controlPoint.getInfluenceOwner().getFactionColor().getColor().getRed();
                Integer green = controlPoint.getInfluenceOwner().getFactionColor().getColor().getGreen();
                Integer blue = controlPoint.getInfluenceOwner().getFactionColor().getColor().getBlue();
                
                spoutColor.setRed(red).setGreen(green).setBlue(blue);
            }
            controlPointLabels.add((Label) new GenericLabel(controlPoint.getIdentifier().toUpperCase())
            .setTextColor(spoutColor).setScale(1.5F).setShadow(false).setResize(true).setAlign(WidgetAnchor.CENTER_CENTER)
            .setFixed(true)
            .setMargin(5));
        }
        
        for(Label controlPoint : controlPointLabels)
        {
            controlPointsContainer.addChild(controlPoint);
            screenElements.add(controlPoint);
        }
        
        // ControlPoint Capture Bar
        controlPointScreenElements.add(controlPointCaptureBar);
        
        // ControlPoint Capture Bar Background
        controlPointScreenElements.add(controlPointCaptureBarBackground);
        
        // Influence Owner Icon
        screenElements.add(influenceOwnerIcon);
        screenCaptureElements.add(influenceOwnerIcon);
        
        // Capture Bar
        screenElements.add(captureBar);
        screenCaptureElements.add(captureBar);
        screenElements.add(captureBarSpaceContainer);
        screenCaptureElements.add(captureBarSpaceContainer);
        screenElements.add(captureBarSpace);
        screenCaptureElements.add(captureBarSpace);
        screenElements.add(captureBarBackground);
        screenCaptureElements.add(captureBarBackground);
        
        // Timer
        screenElements.add(captureTimer);
        screenCaptureElements.add(captureTimer);
        
        // Capture Bar Animation
        screenElements.add(captureBarAnim);
        screenCaptureElements.add(captureBarAnim);
        
        hideAllElements();
        showNonCaptureElements();
        updateRegion(region);
        
        screen.attachWidgets(RegionControl.plugin, regionInfo, regionStatusContainer, regionStatusBarContainer, controlPointsContainer, captureBarSpaceContainer);
        screen.attachWidgets(RegionControl.plugin, background, ownerIcon, regionName, alliesDetectedText, alliesDetectedBar, enemiesDetectedText, enemiesDetectedBar, controlPointCaptureBar, controlPointCaptureBarBackground, influenceOwnerIcon, captureBarBackground, captureBar, captureBarSpace, captureTimer, captureBarAnim);
        
        for(Label controlPoint : controlPointLabels)
        {
            screen.attachWidget(RegionControl.plugin, controlPoint);
        }
        
        hideControlPointCaptureBar();
        
        CapturableRegion currentRegion = rPlayer.getCurrentRegion();
        List<RCPlayer> playerList = currentRegion.getPlayers();
        for(RCPlayer player : playerList)
        {
            if(player.hasSpout())
            {
                player.getSpoutClientLogic().updatePlayersDetected();
            }
        }
        
        BukkitTask runnable = new BukkitRunnable()
        {
            @Override
            public void run()
            {
                if(region != null)
                {
                    if(controlPointCaptureBar.isVisible() && controlPoint != null)
                    {
                        if(controlPoint.getInfluenceOwner() != null)
                        {
                            float influence = controlPoint.getInfluenceMap().get(controlPoint.getInfluenceOwner());
                            float baseinfluence = controlPoint.getBaseInfluence();
                            
                            int barwidth = (int) (influence / baseinfluence * 150);
                            controlPointCaptureBar.setWidth(barwidth);
                            
                            Integer red = controlPoint.getInfluenceOwner().getFactionColor().getColor().getRed();
                            Integer green = controlPoint.getInfluenceOwner().getFactionColor().getColor().getGreen();
                            Integer blue = controlPoint.getInfluenceOwner().getFactionColor().getColor().getBlue();
                            
                            Color spoutColor = new Color(red, green, blue);
                            controlPointCaptureBar.setColor(spoutColor);
                            
                            if(reasonWidgetList.size() != PlayerUtils.getCannotCaptureReasons(region, rcPlayer).size())
                            {
                                for(Label widget : reasonWidgetList)
                                {
                                    screen.removeWidget(widget);
                                }
                                
                                reasonWidgetList = new ArrayList<Label>();
                                
                                if(!PlayerUtils.canCapture(region, rcPlayer))
                                {
                                    int y = 40;
                                    for(String reason : PlayerUtils.getCannotCaptureReasons(region, rcPlayer))
                                    {
                                        Label reasonLabel = (Label) new GenericLabel(reason)
                                        .setScale(0.5F).setTextColor(new Color(255, 0, 0)).setShadow(false).setResize(true).setAlign(WidgetAnchor.TOP_CENTER)
                                        .setFixed(true)
                                        .setAnchor(WidgetAnchor.TOP_CENTER)
                                        .setY(y);
                                        
                                        reasonLabel.shiftXPos(reasonLabel.getWidth() / 2);
                                        y = y + 5;
                                        reasonWidgetList.add(reasonLabel);
                                    }
                                    
                                    for(Label widget : reasonWidgetList)
                                    {
                                        screen.attachWidget(RegionControl.plugin, widget);
                                    }
                                }
                            }
                        }
                    }
                    
                    if(region.isBeingCaptured() && region.getInfluenceOwner() != null)
                    {
                        Integer seconds = region.getSecondsToCapture();
                        Integer minutes = region.getMinutesToCapture();
                        
                        if(seconds == 0 && minutes == 0)
                        {
                            captureTimer.setVisible(false);
                        }
                        else if(!captureElementsHidden)
                        {
                            if(!captureTimer.isVisible())
                            {
                                captureTimer.setVisible(true);
                            }
                            
                            String secondsString = seconds.toString();
                            if(seconds < 10)
                            {
                                secondsString = "0" + secondsString;
                            }
                            captureTimer.setText(minutes.toString() + ":" + secondsString);
                        }
                        
                        if(captureElementsHidden && captureBar.getWidth() != 0)
                        {
                            String secondsString = seconds.toString();
                            if(seconds < 10)
                            {
                                secondsString = "0" + secondsString;
                            }
                            captureTimer.setText(minutes.toString() + ":" + secondsString);
                            showCaptureElements();
                            if(background.getHeight() != 70)
                            {
                                background.setHeight(70);
                            }
                        }
                        
                        else if(!captureElementsHidden && captureBar.getWidth() == 0)
                        {
                            hideCaptureElements();
                            if(background.getHeight() != 40)
                            {
                                background.setHeight(40);
                            }
                        }
                        
                        float influence = region.getInfluenceMap().get(region.getInfluenceOwner());
                        float baseinfluence = region.getBaseInfluence();
                        
                        int barwidth = (int) (influence / baseinfluence * 100);
                        captureBar.setWidth(barwidth);
                        captureBarSpace.setWidth(100 - barwidth);
                        
                        /*
                         * if(barwidth > 75 && barwidth < 100 &&
                         * musicPlaying == false &&
                         * region.getInfluenceOwner() ==
                         * region.getMajorityController())
                         * {
                         * SpoutManager.getSoundManager().playCustomMusic(
                         * RegionControl.plugin, splayer, "music.wav",
                         * false);
                         * musicPlaying = true;
                         * }
                         * else if(musicPlaying == true && (barwidth < 75 ||
                         * barwidth == 100))
                         * {
                         * SpoutManager.getSoundManager().stopMusic(splayer,
                         * false, 1000);
                         * musicPlaying = false;
                         * }
                         */
                        
                        Integer red = region.getInfluenceOwner().getFactionColor().getColor().getRed();
                        Integer green = region.getInfluenceOwner().getFactionColor().getColor().getGreen();
                        Integer blue = region.getInfluenceOwner().getFactionColor().getColor().getBlue();
                        
                        Color spoutColor = new Color(red, green, blue);
                        captureBar.setColor(spoutColor);
                        
                    }
                }
            }
        }.runTaskTimer(RegionControl.plugin, 10, 10);
        
        rPlayer.getClientRunnables().put("spoutClientLogic", runnable);
    }
    
    public void showControlPointCaptureBar()
    {
        if(!controlPointCaptureBar.isVisible())
        {
            for(Widget element : controlPointScreenElements)
            {
                element.setVisible(true);
            }
            
            float influence = controlPoint.getInfluenceMap().get(controlPoint.getInfluenceOwner());
            float baseinfluence = controlPoint.getBaseInfluence();
            
            int barwidth = (int) (influence / baseinfluence * 150);
            controlPointCaptureBar.setWidth(barwidth);
            
            Integer red = controlPoint.getInfluenceOwner().getFactionColor().getColor().getRed();
            Integer green = controlPoint.getInfluenceOwner().getFactionColor().getColor().getGreen();
            Integer blue = controlPoint.getInfluenceOwner().getFactionColor().getColor().getBlue();
            
            Color spoutColor = new Color(red, green, blue);
            controlPointCaptureBar.setColor(spoutColor);
            
            if(!PlayerUtils.canCapture(region, rcPlayer))
            {
                reasonWidgetList = new ArrayList<Label>();
                int y = 40;
                for(String reason : PlayerUtils.getCannotCaptureReasons(region, rcPlayer))
                {
                    Label reasonLabel = (Label) new GenericLabel(reason)
                    .setScale(0.5F).setTextColor(new Color(255, 0, 0)).setShadow(false).setResize(true).setAlign(WidgetAnchor.TOP_CENTER)
                    .setFixed(true)
                    .setAnchor(WidgetAnchor.TOP_CENTER)
                    .setY(y);
                    
                    reasonLabel.shiftXPos(reasonLabel.getWidth() / 2);
                    y = y + 5;
                    reasonWidgetList.add(reasonLabel);
                }
                
                for(Label widget : reasonWidgetList)
                {
                    screen.attachWidget(RegionControl.plugin, widget);
                }
            }
        }
    }
    
    public void updateControlPoints(CapturableRegion region)
    {
        if(region != null && controlPointLabels.size() == 0)
        {
            for(ControlPoint controlPoint : region.getControlPoints())
            {
                Color spoutColor = new Color(255, 255, 255);
                if(!controlPoint.isCapturing())
                {
                    Integer red = controlPoint.getOwner().getFactionColor().getColor().getRed();
                    Integer green = controlPoint.getOwner().getFactionColor().getColor().getGreen();
                    Integer blue = controlPoint.getOwner().getFactionColor().getColor().getBlue();
                    
                    spoutColor.setRed(red).setGreen(green).setBlue(blue);
                }
                controlPointLabels.add((Label) new GenericLabel(controlPoint.getIdentifier().toUpperCase())
                .setTextColor(spoutColor).setScale(1.5F).setShadow(false).setResize(true).setAlign(WidgetAnchor.CENTER_CENTER)
                .setFixed(true)
                .setMargin(5));
            }
            
            for(Label controlPointLabel : controlPointLabels)
            {
                controlPointsContainer.addChild(controlPointLabel);
                screenElements.add(controlPointLabel);
                screen.attachWidget(RegionControl.plugin, controlPointLabel);
            }
        }
        
        else if(region != null && controlPointLabels.size() == region.getControlPoints().size())
        {
            for(ControlPoint controlPoint : region.getControlPoints())
            {
                for(Label controlPointLabel : controlPointLabels)
                {
                    if(controlPointLabel.getText().equalsIgnoreCase(controlPoint.getIdentifier()))
                    {
                        Color spoutColor = new Color(255, 255, 255);
                        if(!controlPoint.isCapturing())
                        {
                            Integer red = controlPoint.getOwner().getFactionColor().getColor().getRed();
                            Integer green = controlPoint.getOwner().getFactionColor().getColor().getGreen();
                            Integer blue = controlPoint.getOwner().getFactionColor().getColor().getBlue();
                            
                            spoutColor.setRed(red).setGreen(green).setBlue(blue);
                        }
                        controlPointLabel.setTextColor(spoutColor);
                        break;
                    }
                }
            }
        }
        
        else if(region != null && controlPointLabels.size() != region.getControlPoints().size() && controlPointLabels.size() != 0)
        {
            for(Label controlPoint : controlPointLabels)
            {
                if(controlPoint != null)
                {
                    controlPointsContainer.removeChild(controlPoint);
                    screenElements.remove(controlPoint);
                    screen.removeWidget(controlPoint);
                }
            }
            controlPointLabels = new ArrayList<Label>();
            for(ControlPoint controlPoint : region.getControlPoints())
            {
                Color spoutColor = new Color(255, 255, 255);
                if(!controlPoint.isCapturing())
                {
                    Integer red = controlPoint.getOwner().getFactionColor().getColor().getRed();
                    Integer green = controlPoint.getOwner().getFactionColor().getColor().getGreen();
                    Integer blue = controlPoint.getOwner().getFactionColor().getColor().getBlue();
                    
                    spoutColor.setRed(red).setGreen(green).setBlue(blue);
                }
                controlPointLabels.add((Label) new GenericLabel(controlPoint.getIdentifier().toUpperCase())
                .setTextColor(spoutColor).setScale(1.5F).setShadow(false).setResize(true).setAlign(WidgetAnchor.CENTER_CENTER)
                .setFixed(true)
                .setMargin(5));
            }
            
            for(Label controlPointLabel : controlPointLabels)
            {
                controlPointsContainer.addChild(controlPointLabel);
                screenElements.add(controlPointLabel);
                screen.attachWidget(RegionControl.plugin, controlPointLabel);
            }
        }
        
        else if(region == null && controlPointLabels.size() != 0)
        {
            for(Label controlPointLabel : controlPointLabels)
            {
                if(controlPointLabel != null)
                {
                    controlPointsContainer.removeChild(controlPointLabel);
                    screenElements.remove(controlPointLabel);
                    screen.removeWidget(controlPointLabel);
                }
            }
            controlPointLabels = new ArrayList<Label>();
        }
    }
    
    public void updateInfluenceOwnerIcon()
    {
        influenceOwnerIcon.setUrl(region.getMajorityController().getFactionColor().getFactionIcon());
        updateInfluenceRate(region.getInfluenceRate());
    }
    
    public void updateInfluenceRate(Float influenceRate)
    {
        screen.removeWidget(captureBarAnim);
        if(influenceRate != null && influenceRate != 0F && region.isBeingCaptured() && region.getInfluenceOwner() != null)
        {
            short barAnimRate = 0;
            float barFloatValue = 0F;
            short barShortValue = 0;
            
            if(influenceRate == 1F)
            {
                barAnimRate = 7;
                barFloatValue = 0.92F;
                barShortValue = 100;
            }
            
            else if(influenceRate == 2F)
            {
                barAnimRate = 5;
                barFloatValue = 1.15F;
                barShortValue = 80;
            }
            
            else if(influenceRate == 3F)
            {
                barAnimRate = 3;
                barFloatValue = 1.55F;
                barShortValue = 60;
            }
            
            else if(influenceRate == 4F)
            {
                barAnimRate = 1;
                barFloatValue = 2.35F;
                barShortValue = 40;
            }
            
            if(influenceRate == 1F || influenceRate == 2F || influenceRate == 3F || influenceRate == 4F)
            {
                if(region.getMajorityController() == region.getInfluenceOwner())
                {
                    captureBarAnim.setUrl("Capture_Anim_Capturing.png").setWidth(30);
                    captureBarAnim.animate(WidgetAnim.POS_X, barFloatValue, barShortValue, barAnimRate, true, true).animateStart().setDirty(true);
                }
                
                else if(region.getMajorityController() != region.getInfluenceOwner())
                {
                    captureBarAnim.setUrl("Capture_Anim_Losing.png").setWidth(125);
                    captureBarAnim.animate(WidgetAnim.POS_X, -barFloatValue, barShortValue, barAnimRate, true, true).animateStart().setDirty(true);
                }
                
                screen.attachWidgets(RegionControl.plugin, captureBarAnim);
                
                if(!captureBarAnim.isVisible() && !captureElementsHidden)
                {
                    captureBarAnim.setVisible(true);
                }
            }
        }
        
        else
        {
            captureBarAnim.animateStop(false).setVisible(false);
        }
    }
    
    public void updatePlayersDetected()
    {
        Map<Faction, Integer> factionPlayers = new HashMap<Faction, Integer>();
        
        for(Entry<String, Faction> faction : ServerLogic.factions.entrySet())
        {
            factionPlayers.put(faction.getValue(), 0);
        }
        
        for(RCPlayer player : region.getPlayers())
        {
            factionPlayers.put(player.getFaction(), factionPlayers.get(player.getFaction()) + 1);
        }
        
        Integer friendliesDetected = factionPlayers.get(rcPlayer.getFaction());
        Integer enemiesDetected = 0;
        
        for(Entry<Faction, Integer> faction : factionPlayers.entrySet())
        {
            if(faction.getKey() != rcPlayer.getFaction())
            {
                enemiesDetected += faction.getValue();
            }
        }
        
        if(friendliesDetected == 0)
        {
            alliesDetectedText.setText("Allies Detected: None").setTextColor(new Color(40, 220, 220));
            alliesDetectedBar.setColor(new Color(0, 0, 0));
        }
        else if(friendliesDetected <= 12)
        {
            alliesDetectedText.setText("Allies Detected: 1-12").setTextColor(new Color(40, 220, 220));
            alliesDetectedBar.setColor(new Color(10, 55, 55));
        }
        else if(friendliesDetected <= 24)
        {
            alliesDetectedText.setText("Allies Detected: 13-24").setTextColor(new Color(40, 220, 220));
            alliesDetectedBar.setColor(new Color(20, 100, 100));
        }
        else if(friendliesDetected <= 48)
        {
            alliesDetectedText.setText("Allies Detected: 25-48").setTextColor(new Color(0, 0, 0));
            alliesDetectedBar.setColor(new Color(30, 140, 140));
        }
        else
        {
            alliesDetectedText.setText("Allies Detected: 48+").setTextColor(new Color(0, 0, 0));
            alliesDetectedBar.setColor(new Color(40, 190, 190));
        }
        
        if(enemiesDetected == 0)
        {
            enemiesDetectedText.setText("Enemies Detected: None").setTextColor(new Color(180, 40, 40));
            enemiesDetectedBar.setColor(new Color(0, 0, 0));
        }
        else if(enemiesDetected <= 12)
        {
            enemiesDetectedText.setText("Enemies Detected: 1-12").setTextColor(new Color(180, 40, 40));
            enemiesDetectedBar.setColor(new Color(50, 20, 20));
        }
        else if(enemiesDetected <= 24)
        {
            enemiesDetectedText.setText("Enemies Detected: 13-24").setTextColor(new Color(180, 40, 40));
            enemiesDetectedBar.setColor(new Color(90, 30, 30));
        }
        else if(enemiesDetected <= 48)
        {
            enemiesDetectedText.setText("Enemies Detected: 25-48").setTextColor(new Color(0, 0, 0));
            enemiesDetectedBar.setColor(new Color(140, 40, 40));
        }
        else
        {
            enemiesDetectedText.setText("Enemies Detected: 48+").setTextColor(new Color(0, 0, 0));
            enemiesDetectedBar.setColor(new Color(180, 45, 45));
        }
    }
    
    public void updateRegion(CapturableRegion updatedRegion)
    {
        region = updatedRegion;
        /*
         * if(musicPlaying)
         * {
         * SpoutManager.getSoundManager().stopMusic(splayer, false, 1000);
         * musicPlaying = false;
         * }
         */
        
        if(updatedRegion == null)
        {
            if(allElementsHidden == false)
            {
                hideAllElements();
            }
            return;
        }
        
        else if(updatedRegion != null)
        {
            updateControlPoints(updatedRegion);
            if(allElementsHidden == true)
            {
                showNonCaptureElements();
                if(updatedRegion.isBeingCaptured())
                {
                    showCaptureElements();
                }
            }
            
            ownerIcon.setUrl(region.getOwner().getFactionColor().getFactionIcon());
            regionName.setText(updatedRegion.getDisplayName());
            
            if(!region.isSpawnRegion() && region.getInfluenceOwner() != null)
            {
                influenceOwnerIcon.setUrl(region.getInfluenceOwner().getFactionColor().getFactionIcon());
                
                Integer red = updatedRegion.getInfluenceOwner().getFactionColor().getColor().getRed();
                Integer green = updatedRegion.getInfluenceOwner().getFactionColor().getColor().getGreen();
                Integer blue = updatedRegion.getInfluenceOwner().getFactionColor().getColor().getBlue();
                
                Color spoutColor = new Color(red, green, blue);
                captureBar.setColor(spoutColor);
            }
            
            float currentscale = 1F;
            while(GenericLabel.getStringWidth(regionName.getText(), currentscale) > 116)
            {
                currentscale -= 0.01F;
            }
            regionName.setScale(currentscale);
            
            if(updatedRegion.isBeingCaptured() && !updatedRegion.isSpawnRegion())
            {
                showCaptureElements();
                background.setHeight(70);
                
                updateInfluenceRate(region.getInfluenceRate());
            }
            
            else
            {
                if(captureBarAnim.isVisible())
                {
                    captureBarAnim.animateStop(false);
                }
                hideCaptureElements();
                if(background.getHeight() != 40)
                {
                    background.setHeight(40);
                }
            }
        }
    }
    
    private void hideAllElements()
    {
        if(!allElementsHidden)
        {
            for(Widget element : screenElements)
            {
                element.setVisible(false);
            }
            allElementsHidden = true;
            captureElementsHidden = true;
        }
    }
    
    private void hideCaptureElements()
    {
        if(!captureElementsHidden)
        {
            for(Widget element : screenCaptureElements)
            {
                element.setVisible(false);
            }
            captureElementsHidden = true;
        }
    }
    
    private void showCaptureElements()
    {
        if(captureElementsHidden)
        {
            for(Widget element : screenCaptureElements)
            {
                element.setVisible(true);
            }
            captureElementsHidden = false;
        }
    }
    
    private void showNonCaptureElements()
    {
        if(allElementsHidden)
        {
            for(Widget element : screenElements)
            {
                if(!screenCaptureElements.contains(element))
                {
                    element.setVisible(true);
                }
            }
            allElementsHidden = false;
        }
    }
}