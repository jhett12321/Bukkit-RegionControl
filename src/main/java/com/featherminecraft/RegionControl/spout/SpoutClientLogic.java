package com.featherminecraft.RegionControl.spout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
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

public class SpoutClientLogic
{
    public static void init()
    {
        // Precache Included Assets
        if(!new File(RegionControl.plugin.getDataFolder(), "background.png").exists())
        {
            RegionControl.plugin.saveResource("background.png", false);
        }
        
        if(!new File(RegionControl.plugin.getDataFolder(), "Capture_Anim_Losing.png").exists())
        {
            RegionControl.plugin.saveResource("Capture_Anim_Losing.png", false);
        }
        
        if(!new File(RegionControl.plugin.getDataFolder(), "Capture_Anim_Capturing.png").exists())
        {
            RegionControl.plugin.saveResource("Capture_Anim_Capturing.png", false);
        }
        
        if(!new File(RegionControl.plugin.getDataFolder(), "null.png").exists())
        {
            RegionControl.plugin.saveResource("null.png", false);
        }
        
        if(!new File(RegionControl.plugin.getDataFolder(), "music.wav").exists())
        {
            RegionControl.plugin.saveResource("music.wav", false);
        }
        
        if(!new File(RegionControl.plugin.getDataFolder(), "captured.wav").exists())
        {
            RegionControl.plugin.saveResource("captured.wav", false);
        }
        
        if(!new File(RegionControl.plugin.getDataFolder(), "captured2.wav").exists())
        {
            RegionControl.plugin.saveResource("captured2.wav", false);
        }
        
        SpoutManager.getFileManager().addToCache(RegionControl.plugin, new File(RegionControl.plugin.getDataFolder().getAbsolutePath() + "/background.png"));
        SpoutManager.getFileManager().addToCache(RegionControl.plugin, new File(RegionControl.plugin.getDataFolder().getAbsolutePath() + "/Capture_Anim_Losing.png"));
        SpoutManager.getFileManager().addToCache(RegionControl.plugin, new File(RegionControl.plugin.getDataFolder().getAbsolutePath() + "/Capture_Anim_Capturing.png"));
        SpoutManager.getFileManager().addToCache(RegionControl.plugin, new File(RegionControl.plugin.getDataFolder().getAbsolutePath() + "/null.png"));
        SpoutManager.getFileManager().addToCache(RegionControl.plugin, new File(RegionControl.plugin.getDataFolder().getAbsolutePath() + "/music.wav"));
        SpoutManager.getFileManager().addToCache(RegionControl.plugin, new File(RegionControl.plugin.getDataFolder().getAbsolutePath() + "/captured.wav"));
        SpoutManager.getFileManager().addToCache(RegionControl.plugin, new File(RegionControl.plugin.getDataFolder().getAbsolutePath() + "/captured2.wav"));
        
        // Precache Faction Icons
        Config config = new Config();
        for(Entry<String, Faction> faction : ServerLogic.factions.entrySet())
        {
            String factionUrl = config.getMainConfig().getString("factions." + faction.getKey() + ".factionIcon");
            faction.getValue().setFactionIconUrl(factionUrl);
            if(faction.getValue().getFactionIconUrl() != null && faction.getValue().getFactionIconUrl() != "" && new File(RegionControl.plugin.getDataFolder(), faction.getValue().getFactionIconUrl()).exists())
            {
                SpoutManager.getFileManager().addToCache(RegionControl.plugin, new File(RegionControl.plugin.getDataFolder().getAbsolutePath() + "/" + faction.getValue().getFactionIconUrl()));
            }
            // TODO Precache Faction Music/Voiceovers
        }
    }
    
    // Variables created from setup
    private Container controlPointsContainer;
    private Container backgroundContainer;
    private Label regionname;
    private Label captureTimer;
    private Gradient captureBar;
    private Texture captureBarAnim;
    private Gradient captureBarSpace;
    private Gradient captureBarBackground;
    
    private Container controlPointCaptureBarContainer;
    private Container controlPointCaptureBarBackgroundContainer;
    private Gradient controlPointCaptureBar;
    private Gradient controlPointCaptureBarBackground;
    
    private Texture ownericon;
    
    private Texture influenceOwnerIcon;
    // Misc
    private BukkitTask runnable;
    private Container influenceOwnerIconContainer;
    private Container captureBarContainer;
    private Container captureBarSpaceContainer;
    private Container captureBarBackgroundContainer;
    private Container timerContainer;
    private Container barAnimContainer;
    private Container regionInfo;
    private Texture background;
    private List<Widget> screenElements = new ArrayList<Widget>();
    private List<Widget> screenCaptureElements = new ArrayList<Widget>();
    private boolean allElementsHidden = false;
    private boolean captureElementsHidden = true;
    private ArrayList<Label> controlPointLabels = new ArrayList<Label>();
    private InGameHUD screen;
    private CapturableRegion region;
    private ControlPoint controlPoint;
    private boolean musicPlaying;
    
    private SpoutPlayer splayer;
    private List<Widget> controlPointScreenElements = new ArrayList<Widget>();
    
    public ControlPoint getControlPoint()
    {
        return controlPoint;
    }
    
    public BukkitTask getRunnable()
    {
        return runnable;
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
    
    public void hideControlPointCaptureBar()
    {
        if(controlPointCaptureBar.isVisible())
        {
            for(Widget element : controlPointScreenElements)
            {
                element.setVisible(false);
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
            SpoutManager.getSoundManager().stopMusic(splayer, false, 1000);
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
        splayer = ((SpoutPlayer) rcplayer.getBukkitPlayer());
        screen = splayer.getMainScreen();
        region = rcplayer.getCurrentRegion();
        
        // Background
        backgroundContainer = (Container) new GenericContainer().setLayout(ContainerType.VERTICAL).setAlign(WidgetAnchor.CENTER_LEFT).setAnchor(WidgetAnchor.CENTER_LEFT).setWidth(150).setHeight(23).setX(3).setY(-5);
        
        screenElements.add(backgroundContainer);
        
        background = (Texture) new GenericTexture("background.png").setDrawAlphaChannel(true).setWidth(150).setHeight(23).setPriority(RenderPriority.Highest);
        
        screenElements.add(background);
        
        backgroundContainer.addChild(background);
        
        // Region Info
        regionInfo = (Container) new GenericContainer().setLayout(ContainerType.HORIZONTAL).setAlign(WidgetAnchor.CENTER_LEFT).setAnchor(WidgetAnchor.CENTER_LEFT).setWidth(145).setHeight(10).setX(10).setY(3).setMargin(3);
        
        screenElements.add(regionInfo);
        
        ownericon = (Texture) new GenericTexture().setUrl(region.getOwner().getFactionIconUrl()).setHeight(16).setWidth(16).setFixed(true).setMargin(-5, 6, 0, 0);
        
        screenElements.add(ownericon);
        
        regionname = (Label) new GenericLabel().setText(region.getDisplayName()).setShadow(false).setResize(true).setFixed(true);
        
        screenElements.add(regionname);
        
        float currentscale = 1F;
        while(GenericLabel.getStringWidth(regionname.getText(), currentscale) > 116)
        {
            currentscale -= 0.01F;
        }
        regionname.setScale(currentscale);
        
        regionInfo.addChildren(ownericon, regionname);
        
        // Control Points
        controlPointsContainer = (Container) new GenericContainer().setLayout(ContainerType.HORIZONTAL).setAlign(WidgetAnchor.CENTER_CENTER).setAnchor(WidgetAnchor.CENTER_LEFT).setWidth(50).setHeight(10).setX(50).setY(25).setMarginRight(1);
        
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
        controlPointCaptureBarContainer = (Container) new GenericContainer().setLayout(ContainerType.OVERLAY).setAlign(WidgetAnchor.CENTER_LEFT).setAnchor(WidgetAnchor.TOP_CENTER).setWidth(427).setHeight(10).shiftXPos(-75).shiftYPos(30);
        
        controlPointScreenElements.add(controlPointCaptureBarContainer);
        
        controlPointCaptureBar = (Gradient) new GenericGradient(new Color(255, 255, 255, 255)).setWidth(150).setHeight(5).setMargin(0, 3).setFixed(true).setPriority(RenderPriority.High);
        
        controlPointScreenElements.add(controlPointCaptureBar);
        
        controlPointCaptureBarContainer.addChild(controlPointCaptureBar);
        
        // ControlPoint Capture Bar Background
        controlPointCaptureBarBackgroundContainer = (Container) new GenericContainer().setLayout(ContainerType.OVERLAY).setAlign(WidgetAnchor.CENTER_LEFT).setAnchor(WidgetAnchor.TOP_CENTER).setWidth(427).setHeight(7).shiftXPos(-74).shiftYPos(31);
        
        controlPointScreenElements.add(controlPointCaptureBarBackgroundContainer);
        
        controlPointCaptureBarBackground = (Gradient) new GenericGradient(new Color(0F, 0F, 0F, 1F)).setWidth(154).setHeight(7).setFixed(true).setPriority(RenderPriority.Highest);
        
        controlPointScreenElements.add(controlPointCaptureBarBackground);
        
        controlPointCaptureBarBackgroundContainer.addChild(controlPointCaptureBarBackground);
        
        // Capture Icons Goes here
        influenceOwnerIconContainer = (Container) new GenericContainer().setLayout(ContainerType.OVERLAY).setAlign(WidgetAnchor.CENTER_LEFT).setAnchor(WidgetAnchor.CENTER_LEFT).setWidth(427).setHeight(10).setX(5).setY(40);
        
        screenElements.add(influenceOwnerIconContainer);
        screenCaptureElements.add(influenceOwnerIconContainer);
        
        influenceOwnerIcon = (Texture) new GenericTexture("null.png").setMargin(0, 0, 0, 3).setHeight(8).setWidth(8).setFixed(true);
        
        screenElements.add(influenceOwnerIcon);
        screenCaptureElements.add(influenceOwnerIcon);
        
        influenceOwnerIconContainer.addChild(influenceOwnerIcon);
        
        // Capture Bar
        captureBarContainer = (Container) new GenericContainer().setLayout(ContainerType.OVERLAY).setAlign(WidgetAnchor.CENTER_LEFT).setAnchor(WidgetAnchor.CENTER_LEFT).setWidth(427).setHeight(10).setX(20).setY(40);
        
        screenElements.add(captureBarContainer);
        screenCaptureElements.add(captureBarContainer);
        
        captureBar = (Gradient) new GenericGradient(new Color(255, 255, 255, 255)).setWidth(100).setHeight(10).setMargin(0, 3).setFixed(true).setPriority(RenderPriority.High);
        
        screenElements.add(captureBar);
        screenCaptureElements.add(captureBar);
        
        captureBarContainer.addChild(captureBar);
        
        // Empty Capture Bar Overlay
        captureBarSpaceContainer = (Container) new GenericContainer().setLayout(ContainerType.OVERLAY).setAlign(WidgetAnchor.CENTER_RIGHT).setAnchor(WidgetAnchor.CENTER_LEFT).setHeight(10).setX(73).setY(40);
        
        screenElements.add(captureBarSpaceContainer);
        screenCaptureElements.add(captureBarSpaceContainer);
        
        captureBarSpace = (Gradient) new GenericGradient(new Color(0F, 0F, 0F, 1F)).setWidth(0).setHeight(10).setFixed(true).setPriority(RenderPriority.Low);
        
        screenElements.add(captureBarSpace);
        screenCaptureElements.add(captureBarSpace);
        
        captureBarSpaceContainer.addChild(captureBarSpace);
        
        // Capture Bar Background
        captureBarBackgroundContainer = (Container) new GenericContainer().setLayout(ContainerType.OVERLAY).setAlign(WidgetAnchor.CENTER_LEFT).setAnchor(WidgetAnchor.CENTER_LEFT).setWidth(427).setHeight(12).setX(21).setY(39);
        
        screenElements.add(captureBarBackgroundContainer);
        screenCaptureElements.add(captureBarBackgroundContainer);
        
        captureBarBackground = (Gradient) new GenericGradient(new Color(0F, 0F, 0F, 1F)).setWidth(104).setHeight(12).setFixed(true).setPriority(RenderPriority.Highest);
        
        screenElements.add(captureBarBackground);
        screenCaptureElements.add(captureBarBackground);
        
        captureBarBackgroundContainer.addChild(captureBarBackground);
        
        // Timer
        timerContainer = (Container) new GenericContainer().setLayout(ContainerType.OVERLAY).setAlign(WidgetAnchor.CENTER_LEFT).setAnchor(WidgetAnchor.CENTER_LEFT).setWidth(427).setHeight(10).setX(60).setY(41);
        
        screenElements.add(timerContainer);
        screenCaptureElements.add(timerContainer);
        
        captureTimer = (Label) new GenericLabel().setText(" ").setResize(true).setFixed(true).setPriority(RenderPriority.Lowest);
        
        screenElements.add(captureTimer);
        screenCaptureElements.add(captureTimer);
        
        timerContainer.addChild(captureTimer);
        
        // Capture Bar Animation
        barAnimContainer = (Container) new GenericContainer().setLayout(ContainerType.OVERLAY).setAlign(WidgetAnchor.CENTER_LEFT).setAnchor(WidgetAnchor.CENTER_LEFT).setWidth(427).setHeight(10).setMarginLeft(100).setY(40);
        
        screenElements.add(barAnimContainer);
        screenCaptureElements.add(barAnimContainer);
        
        captureBarAnim = (Texture) new GenericTexture().setDrawAlphaChannel(true).setHeight(10).setFixed(true).setPriority(RenderPriority.Normal).setVisible(false);
        
        screenElements.add(captureBarAnim);
        screenCaptureElements.add(captureBarAnim);
        
        captureBarAnim.setUrl("null.png").setWidth(125);
        hideAllElements();
        showNonCaptureElements();
        
        barAnimContainer.addChild(captureBarAnim);
        
        screen.attachWidgets(RegionControl.plugin, backgroundContainer, regionInfo, controlPointsContainer, controlPointCaptureBarContainer, controlPointCaptureBarBackgroundContainer, influenceOwnerIconContainer, captureBarContainer, captureBarSpaceContainer, captureBarBackgroundContainer, timerContainer, barAnimContainer);
        screen.attachWidgets(RegionControl.plugin, background, ownericon, regionname, controlPointCaptureBar, controlPointCaptureBarBackground, influenceOwnerIcon, captureBarBackground, captureBar, captureBarSpace, captureTimer, captureBarAnim);
        
        for(Label controlPoint : controlPointLabels)
        {
            screen.attachWidget(RegionControl.plugin, controlPoint);
        }
        
        hideControlPointCaptureBar();
        
        updateRegion(rcplayer.getCurrentRegion());
        
        runnable = new BukkitRunnable()
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
                            
                            if(barwidth > 75 && barwidth < 100 && musicPlaying == false && region.getInfluenceOwner() == region.getMajorityController())
                            {
                                SpoutManager.getSoundManager().playCustomMusic(RegionControl.plugin, splayer, "music.wav", false);
                                musicPlaying = true;
                            }
                            else if(musicPlaying == true && (barwidth < 75 || barwidth == 100))
                            {
                                SpoutManager.getSoundManager().stopMusic(splayer, false, 1000);
                                musicPlaying = false;
                            }
                            
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
    
    public void updateRegion(CapturableRegion updatedRegion)
    {
        region = updatedRegion;
        if(musicPlaying)
        {
            SpoutManager.getSoundManager().stopMusic(splayer, false, 1000);
            musicPlaying = false;
        }
        
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
                if(region.isSpawnRegion() && background.getHeight() != 23)
                {
                    background.setHeight(23);
                }
                else if(!region.isSpawnRegion() && background.getHeight() != 40)
                {
                    background.setHeight(40);
                }
            }
        }
    }
}
