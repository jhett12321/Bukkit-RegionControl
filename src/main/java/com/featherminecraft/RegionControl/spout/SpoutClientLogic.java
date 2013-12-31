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

import com.featherminecraft.RegionControl.Config;
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
        Config config = new Config();
        for(Entry<String, Faction> faction : ServerLogic.factions.entrySet())
        {
            String factionUrl = config.getFactionConfig().getString("factions." + faction.getKey() + ".factionIcon");
            faction.getValue().setFactionIconUrl(factionUrl);
            if(faction.getValue().getFactionIconUrl() != null && faction.getValue().getFactionIconUrl() != "" && new File(RegionControl.plugin.getDataFolder().getAbsolutePath() + "/assets/images/factionIcons/" + faction.getValue().getFactionIconUrl()).exists())
            {
                SpoutManager.getFileManager().addToCache(RegionControl.plugin, new File(RegionControl.plugin.getDataFolder().getAbsolutePath() + "/assets/images/factionIcons/" + faction.getValue().getFactionIconUrl()));
            }
            // TODO Precache Faction Music/Voiceovers
        }
    }
    
    // Player Variables
    private RCPlayer rcplayer;
    private SpoutPlayer splayer;
    private InGameHUD screen;
    private CapturableRegion region;
    private ControlPoint controlPoint;
    
    // GUI
    private List<Widget> screenElements = new ArrayList<Widget>();
    private List<Widget> screenCaptureElements = new ArrayList<Widget>();
    private boolean allElementsHidden = false;
    private boolean captureElementsHidden = true;
    
    // Region Info Background
    private Texture background = (Texture) new GenericTexture("background.png").setDrawAlphaChannel(true).setAnchor(WidgetAnchor.CENTER_LEFT).setWidth(150).setHeight(40).setX(3).setY(-5).setPriority(RenderPriority.Highest);
    
    // Region Info
    private Container regionInfo = (Container) new GenericContainer().setLayout(ContainerType.HORIZONTAL).setAlign(WidgetAnchor.CENTER_LEFT).setAnchor(WidgetAnchor.CENTER_LEFT).setWidth(145).setHeight(10).setX(10).setY(3);
    private Texture ownericon = (Texture) new GenericTexture("null.png").setDrawAlphaChannel(true).setWidth(16).setHeight(16).setFixed(true).setMargin(-5, 6, 0, 0);
    private Label regionname = (Label) new GenericLabel().setShadow(false).setResize(true).setFixed(true);
    
    // Region Status (Enemies/Allies Detected)
    private Container regionStatusContainer = (Container) new GenericContainer().setLayout(ContainerType.VERTICAL).setAlign(WidgetAnchor.CENTER_CENTER).setAnchor(WidgetAnchor.CENTER_LEFT).setWidth(145).setHeight(12).setX(45).setY(18);
    private Container regionStatusBarContainer = (Container) new GenericContainer().setLayout(ContainerType.VERTICAL).setAlign(WidgetAnchor.CENTER_CENTER).setAnchor(WidgetAnchor.CENTER_LEFT).setWidth(145).setHeight(12).setX(45).setY(17);
    private Label alliesDetectedText = (Label) new GenericLabel().setShadow(false).setScale(0.5F).setResize(true).setFixed(true);
    private Gradient alliesDetectedBar = (Gradient) new GenericGradient(new Color(0, 0, 255)).setWidth(65).setHeight(5).setFixed(true).setPriority(RenderPriority.High);
    private Label enemiesDetectedText = (Label) new GenericLabel().setShadow(false).setScale(0.5F).setResize(true).setFixed(true);
    private Gradient enemiesDetectedBar = (Gradient) new GenericGradient(new Color(255, 0, 0)).setWidth(65).setHeight(5).setFixed(true).setPriority(RenderPriority.High);
    
    // Control Points
    private ArrayList<Label> controlPointLabels = new ArrayList<Label>();
    private List<Widget> controlPointScreenElements = new ArrayList<Widget>();
    
    private Container controlPointsContainer = (Container) new GenericContainer().setLayout(ContainerType.HORIZONTAL).setAlign(WidgetAnchor.CENTER_CENTER).setAnchor(WidgetAnchor.CENTER_LEFT).setWidth(50).setHeight(10).setX(25).setY(25).setMarginRight(1);
    
    // Control Point Capture Bar
    private Container controlPointCaptureBarContainer = (Container) new GenericContainer().setLayout(ContainerType.OVERLAY).setAlign(WidgetAnchor.CENTER_LEFT).setAnchor(WidgetAnchor.TOP_CENTER).setWidth(427).setHeight(10).shiftXPos(-75).shiftYPos(30);
    private Gradient controlPointCaptureBar = (Gradient) new GenericGradient(new Color(255, 255, 255, 255)).setWidth(150).setHeight(5).setMargin(0, 3).setFixed(true).setPriority(RenderPriority.High);
    
    // Cannot Capture Control Point Indicator
    private ArrayList<Label> reasonWidgetList;
    private Map<String, String> reasonLocalisations = new HashMap<String, String>();
    
    private Container reasonContainer = (Container) new GenericContainer().setLayout(ContainerType.VERTICAL).setAlign(WidgetAnchor.CENTER_CENTER).setAnchor(WidgetAnchor.TOP_CENTER).setWidth(170).shiftXPos(-83).shiftYPos(25);
    
    // Capture Bar Background
    private Container controlPointCaptureBarBackgroundContainer = (Container) new GenericContainer().setLayout(ContainerType.OVERLAY).setAlign(WidgetAnchor.CENTER_LEFT).setAnchor(WidgetAnchor.TOP_CENTER).setWidth(427).setHeight(7).shiftXPos(-74).shiftYPos(31);
    private Gradient controlPointCaptureBarBackground = (Gradient) new GenericGradient(new Color(0F, 0F, 0F, 1F)).setWidth(154).setHeight(7).setFixed(true).setPriority(RenderPriority.Highest);
    
    // Influence Owner Icon
    private Container influenceOwnerIconContainer = (Container) new GenericContainer().setLayout(ContainerType.OVERLAY).setAlign(WidgetAnchor.CENTER_LEFT).setAnchor(WidgetAnchor.CENTER_LEFT).setWidth(427).setHeight(10).setX(5).setY(40);
    private Texture influenceOwnerIcon = (Texture) new GenericTexture("null.png").setMargin(0, 0, 0, 3).setHeight(8).setWidth(8).setFixed(true);
    
    // Capture Bar
    private Container captureBarContainer = (Container) new GenericContainer().setLayout(ContainerType.OVERLAY).setAlign(WidgetAnchor.CENTER_LEFT).setAnchor(WidgetAnchor.CENTER_LEFT).setWidth(427).setHeight(10).setX(20).setY(40);
    private Gradient captureBar = (Gradient) new GenericGradient(new Color(255, 255, 255, 255)).setWidth(100).setHeight(10).setMargin(0, 3).setFixed(true).setPriority(RenderPriority.High);
    private Container captureBarSpaceContainer = (Container) new GenericContainer().setLayout(ContainerType.OVERLAY).setAlign(WidgetAnchor.CENTER_RIGHT).setAnchor(WidgetAnchor.CENTER_LEFT).setHeight(10).setX(73).setY(40);
    private Gradient captureBarSpace = (Gradient) new GenericGradient(new Color(0F, 0F, 0F, 1F)).setWidth(0).setHeight(10).setFixed(true).setPriority(RenderPriority.Low);
    private Container captureBarBackgroundContainer = (Container) new GenericContainer().setLayout(ContainerType.OVERLAY).setAlign(WidgetAnchor.CENTER_LEFT).setAnchor(WidgetAnchor.CENTER_LEFT).setWidth(427).setHeight(12).setX(21).setY(39);
    private Gradient captureBarBackground = (Gradient) new GenericGradient(new Color(0F, 0F, 0F, 1F)).setWidth(104).setHeight(12).setFixed(true).setPriority(RenderPriority.Highest);
    
    // Timer
    private Container timerContainer = (Container) new GenericContainer().setLayout(ContainerType.OVERLAY).setAlign(WidgetAnchor.CENTER_LEFT).setAnchor(WidgetAnchor.CENTER_LEFT).setWidth(427).setHeight(10).setX(60).setY(41);
    private Label captureTimer = (Label) new GenericLabel().setText(" ").setResize(true).setFixed(true).setPriority(RenderPriority.Lowest);
    
    // Capture Bar Animation
    private Container barAnimContainer = (Container) new GenericContainer().setLayout(ContainerType.OVERLAY).setAlign(WidgetAnchor.CENTER_LEFT).setAnchor(WidgetAnchor.CENTER_LEFT).setWidth(427).setHeight(10).setMarginLeft(100).setY(40);
    private Texture captureBarAnim = (Texture) new GenericTexture().setDrawAlphaChannel(true).setHeight(10).setFixed(true).setPriority(RenderPriority.Normal).setVisible(false);
    
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
                    reasonContainer.removeChild(widget);
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
    
    public void setupClientElements(RCPlayer rcplayer)
    {
        this.rcplayer = rcplayer;
        splayer = ((SpoutPlayer) rcplayer.getBukkitPlayer());
        screen = splayer.getMainScreen();
        region = rcplayer.getCurrentRegion();
        
        // Background
        screenElements.add(background);
        
        // Region Info
        screenElements.add(regionInfo);
        screenElements.add(ownericon);
        screenElements.add(regionname);
        
        regionInfo.addChildren(ownericon, regionname);
        
        // Region Status
        screenElements.add(regionStatusContainer);
        screenElements.add(regionStatusBarContainer);
        screenElements.add(alliesDetectedText);
        screenElements.add(alliesDetectedBar);
        screenElements.add(enemiesDetectedBar);
        
        regionStatusContainer.addChildren(alliesDetectedText, enemiesDetectedText);
        regionStatusBarContainer.addChildren(alliesDetectedBar, enemiesDetectedBar);
        
        // Control Points
        screenElements.add(controlPointsContainer);
        
        for(ControlPoint controlPoint : region.getControlPoints())
        {
            Color spoutColor = new Color(255, 255, 255);
            if(!controlPoint.isCapturing())
            {
                Integer red = controlPoint.getInfluenceOwner().getFactionColor().getRed();
                Integer green = controlPoint.getInfluenceOwner().getFactionColor().getGreen();
                Integer blue = controlPoint.getInfluenceOwner().getFactionColor().getBlue();
                
                spoutColor.setRed(red).setGreen(green).setBlue(blue);
            }
            controlPointLabels.add((Label) new GenericLabel().setText(controlPoint.getIdentifier().toUpperCase()).setTextColor(spoutColor).setScale(1.5F).setShadow(false).setResize(true).setAlign(WidgetAnchor.CENTER_CENTER).setFixed(true).setMargin(5));
        }
        
        for(Label controlPoint : controlPointLabels)
        {
            controlPointsContainer.addChild(controlPoint);
            screenElements.add(controlPoint);
        }
        
        // ControlPoint Capture Bar
        controlPointScreenElements.add(controlPointCaptureBarContainer);
        controlPointScreenElements.add(controlPointCaptureBar);
        
        controlPointCaptureBarContainer.addChild(controlPointCaptureBar);
        
        // Unable to capture ControlPoint Reasons (under Capture Bar)
        reasonLocalisations.put("NO_CONNECTION", "Cannot capture Control Point: You do not own a connecting region.");
        reasonLocalisations.put("CONNECTION_NOT_SECURE", "Cannot capture Control Point: All of your connecting regions are not secure.");
        reasonLocalisations.put("MOUNTED", "Cannot capture Control Point: You are currently Mounted.");
        reasonLocalisations.put("INVALID_CLASS", "Cannot capture Control Point: You are an Invalid Class");
        
        // ControlPoint Capture Bar Background
        controlPointScreenElements.add(controlPointCaptureBarBackgroundContainer);
        controlPointScreenElements.add(controlPointCaptureBarBackground);
        
        controlPointCaptureBarBackgroundContainer.addChild(controlPointCaptureBarBackground);
        
        // Influence Owner Icon
        screenElements.add(influenceOwnerIconContainer);
        screenCaptureElements.add(influenceOwnerIconContainer);
        screenElements.add(influenceOwnerIcon);
        screenCaptureElements.add(influenceOwnerIcon);
        
        influenceOwnerIconContainer.addChild(influenceOwnerIcon);
        
        // Capture Bar
        screenElements.add(captureBarContainer);
        screenCaptureElements.add(captureBarContainer);
        screenElements.add(captureBar);
        screenCaptureElements.add(captureBar);
        screenElements.add(captureBarSpaceContainer);
        screenCaptureElements.add(captureBarSpaceContainer);
        screenElements.add(captureBarSpace);
        screenCaptureElements.add(captureBarSpace);
        screenElements.add(captureBarBackgroundContainer);
        screenCaptureElements.add(captureBarBackgroundContainer);
        screenElements.add(captureBarBackground);
        screenCaptureElements.add(captureBarBackground);
        
        captureBarContainer.addChild(captureBar);
        captureBarSpaceContainer.addChild(captureBarSpace);
        captureBarBackgroundContainer.addChild(captureBarBackground);
        
        // Timer
        screenElements.add(timerContainer);
        screenCaptureElements.add(timerContainer);
        screenElements.add(captureTimer);
        screenCaptureElements.add(captureTimer);
        
        timerContainer.addChild(captureTimer);
        
        // Capture Bar Animation
        screenElements.add(barAnimContainer);
        screenCaptureElements.add(barAnimContainer);
        screenElements.add(captureBarAnim);
        screenCaptureElements.add(captureBarAnim);
        
        captureBarAnim.setUrl("null.png").setWidth(125);
        
        hideAllElements();
        showNonCaptureElements();
        updateRegion(region);
        
        barAnimContainer.addChild(captureBarAnim);
        
        screen.attachWidgets(RegionControl.plugin, regionInfo, regionStatusContainer, regionStatusBarContainer, controlPointsContainer, controlPointCaptureBarContainer, controlPointCaptureBarBackgroundContainer, influenceOwnerIconContainer, captureBarContainer, captureBarSpaceContainer, captureBarBackgroundContainer, timerContainer, barAnimContainer);
        screen.attachWidgets(RegionControl.plugin, background, ownericon, regionname, alliesDetectedText, alliesDetectedBar, enemiesDetectedText, enemiesDetectedBar, controlPointCaptureBar, controlPointCaptureBarBackground, influenceOwnerIcon, captureBarBackground, captureBar, captureBarSpace, captureTimer, captureBarAnim);
        
        for(Label controlPoint : controlPointLabels)
        {
            screen.attachWidget(RegionControl.plugin, controlPoint);
        }
        
        hideControlPointCaptureBar();
        
        CapturableRegion currentRegion = rcplayer.getCurrentRegion();
        List<RCPlayer> playerList = currentRegion.getPlayers();
        for(RCPlayer rPlayer : playerList)
        {
            if(rPlayer.hasSpout())
            {
                rPlayer.getSpoutClientLogic().updatePlayersDetected();
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
                            
                            Integer red = controlPoint.getInfluenceOwner().getFactionColor().getRed();
                            Integer green = controlPoint.getInfluenceOwner().getFactionColor().getGreen();
                            Integer blue = controlPoint.getInfluenceOwner().getFactionColor().getBlue();
                            
                            Color spoutColor = new Color(red, green, blue);
                            controlPointCaptureBar.setColor(spoutColor);
                        }
                    }
                    
                    if(region.isBeingCaptured())
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
                        
                        if(region.getInfluenceOwner() != null)
                        {
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
                            
                            Integer red = region.getInfluenceOwner().getFactionColor().getRed();
                            Integer green = region.getInfluenceOwner().getFactionColor().getGreen();
                            Integer blue = region.getInfluenceOwner().getFactionColor().getBlue();
                            
                            Color spoutColor = new Color(red, green, blue);
                            captureBar.setColor(spoutColor);
                        }
                        
                        else if(captureBar.getWidth() == 0)
                        {
                            hideCaptureElements();
                            if(background.getHeight() != 40)
                            {
                                background.setHeight(40);
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(RegionControl.plugin, 10, 10);
        
        rcplayer.getClientRunnables().put("spoutClientLogic", runnable);
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
            
            Integer red = controlPoint.getInfluenceOwner().getFactionColor().getRed();
            Integer green = controlPoint.getInfluenceOwner().getFactionColor().getGreen();
            Integer blue = controlPoint.getInfluenceOwner().getFactionColor().getBlue();
            
            Color spoutColor = new Color(red, green, blue);
            controlPointCaptureBar.setColor(spoutColor);
            
            PlayerUtils playerUtils = new PlayerUtils();
            
            if(!playerUtils.canCapture(region, rcplayer))
            {
                reasonWidgetList = new ArrayList<Label>();
                for(String reason : playerUtils.getCannotCaptureReasons(region, rcplayer))
                {
                    String localisedReason = reasonLocalisations.get(reason);
                    if(localisedReason == null)
                    {
                        localisedReason = reason;
                    }
                    Label reasonLabel = (Label) new GenericLabel(localisedReason).setScale(0.5F).setTextColor(new Color(255, 0, 0)).setShadow(false).setResize(true).setAlign(WidgetAnchor.CENTER_LEFT).setFixed(true);
                    reasonWidgetList.add(reasonLabel);
                }
                
                for(Label widget : reasonWidgetList)
                {
                    reasonContainer.addChild(widget);
                }
                
                screen.attachWidget(RegionControl.plugin, reasonContainer);
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
                    Integer red = controlPoint.getOwner().getFactionColor().getRed();
                    Integer green = controlPoint.getOwner().getFactionColor().getGreen();
                    Integer blue = controlPoint.getOwner().getFactionColor().getBlue();
                    
                    spoutColor.setRed(red).setGreen(green).setBlue(blue);
                }
                controlPointLabels.add((Label) new GenericLabel().setText(controlPoint.getIdentifier().toUpperCase()).setTextColor(spoutColor).setScale(1.5F).setShadow(false).setResize(true).setAlign(WidgetAnchor.CENTER_CENTER).setFixed(true).setMargin(5));
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
                            Integer red = controlPoint.getOwner().getFactionColor().getRed();
                            Integer green = controlPoint.getOwner().getFactionColor().getGreen();
                            Integer blue = controlPoint.getOwner().getFactionColor().getBlue();
                            
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
                    Integer red = controlPoint.getOwner().getFactionColor().getRed();
                    Integer green = controlPoint.getOwner().getFactionColor().getGreen();
                    Integer blue = controlPoint.getOwner().getFactionColor().getBlue();
                    
                    spoutColor.setRed(red).setGreen(green).setBlue(blue);
                }
                controlPointLabels.add((Label) new GenericLabel().setText(controlPoint.getIdentifier().toUpperCase()).setTextColor(spoutColor).setScale(1.5F).setShadow(false).setResize(true).setAlign(WidgetAnchor.CENTER_CENTER).setFixed(true).setMargin(5));
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
    
    public void updateInfluenceRate(Float influenceRate)
    {
        screen.removeWidget(barAnimContainer).removeWidget(captureBarAnim);
        if(influenceRate != null && influenceRate != 0F && region.isBeingCaptured())
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
                if(region.getInfluenceOwner() == null)
                {
                    captureBarAnim.setUrl("Capture_Anim_Capturing.png").setWidth(30);
                    captureBarAnim.animate(WidgetAnim.POS_X, barFloatValue, barShortValue, barAnimRate, true, true).animateStart().setDirty(true);
                    influenceOwnerIcon.setUrl(region.getMajorityController().getFactionIconUrl());
                }
                
                else if(region.getMajorityController() == region.getInfluenceOwner())
                {
                    captureBarAnim.setUrl("Capture_Anim_Capturing.png").setWidth(30);
                    captureBarAnim.animate(WidgetAnim.POS_X, barFloatValue, barShortValue, barAnimRate, true, true).animateStart().setDirty(true);
                }
                
                else if(region.getMajorityController() != region.getInfluenceOwner())
                {
                    captureBarAnim.setUrl("Capture_Anim_Losing.png").setWidth(125);
                    captureBarAnim.animate(WidgetAnim.POS_X, -barFloatValue, barShortValue, barAnimRate, true, true).animateStart().setDirty(true);
                }
                
                screen.attachWidgets(RegionControl.plugin, barAnimContainer, captureBarAnim);
                
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
        
        Integer friendliesDetected = factionPlayers.get(rcplayer.getFaction());
        Integer enemiesDetected = 0;
        
        for(Entry<Faction, Integer> faction : factionPlayers.entrySet())
        {
            if(faction.getKey() != rcplayer.getFaction())
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
            
            ownericon.setUrl(region.getOwner().getFactionIconUrl());
            regionname.setText(updatedRegion.getDisplayName());
            
            if(!region.isSpawnRegion())
            {
                influenceOwnerIcon.setUrl(region.getInfluenceOwner().getFactionIconUrl());
                
                Integer red = updatedRegion.getInfluenceOwner().getFactionColor().getRed();
                Integer green = updatedRegion.getInfluenceOwner().getFactionColor().getGreen();
                Integer blue = updatedRegion.getInfluenceOwner().getFactionColor().getBlue();
                
                Color spoutColor = new Color(red, green, blue);
                captureBar.setColor(spoutColor);
            }
            
            float currentscale = 1F;
            while(GenericLabel.getStringWidth(regionname.getText(), currentscale) > 116)
            {
                currentscale -= 0.01F;
            }
            regionname.setScale(currentscale);
            
            if(updatedRegion.isBeingCaptured())
            {
                showCaptureElements();
                background.setHeight(70);
                
                updateInfluenceRate(region.getInfluenceRate());
            }
            
            else if(!updatedRegion.isBeingCaptured())
            {
                if(captureBarAnim.isVisible())
                {
                    captureBarAnim.animateStop(false);
                }
                hideCaptureElements();
                if(!region.isSpawnRegion() && background.getHeight() != 40)
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