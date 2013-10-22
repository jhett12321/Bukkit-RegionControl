package com.featherminecraft.RegionControl.spout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

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

import com.featherminecraft.RegionControl.RCPlayer;
import com.featherminecraft.RegionControl.RegionControl;
import com.featherminecraft.RegionControl.capturableregion.CapturableRegion;
import com.featherminecraft.RegionControl.capturableregion.ControlPoint;

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
    
    //Misc
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
    protected CapturableRegion region;

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
        screen = ((SpoutPlayer) rcplayer.getBukkitPlayer()).getMainScreen();
        region = rcplayer.getCurrentRegion();
        
        //Background
        backgroundContainer = (Container) new GenericContainer()
        .setLayout(ContainerType.VERTICAL)
        .setAlign(WidgetAnchor.CENTER_LEFT)
        .setAnchor(WidgetAnchor.CENTER_LEFT)
        .setWidth(150)
        .setHeight(40)
        .setX(3)
        .setY(-5);
        
        screenElements.add(backgroundContainer);
        
        background = (Texture) new GenericTexture("background.png")
        .setDrawAlphaChannel(true)
        .setHeight(40)
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
        
        regionname = (Label) ((Label) new GenericLabel().setText(region.getDisplayName()).
                setMargin(5, 5)).setShadow(false).setResize(true).setFixed(true);
        
        screenElements.add(regionname);
        
        float currentscale = 1F;
        while(GenericLabel.getStringWidth(regionname.getText(), currentscale) > 116)
        {
//            RegionControl.plugin.getLogger().log(Level.INFO, "DEBUG: Text Width is currently: " + GenericLabel.getStringWidth(regionname.getText(), currentscale)); //Debug
            currentscale -= 0.01F;
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
        for(ControlPoint controlPoint : region.getControlPoints())
        {
            Color spoutColor = new Color(255,255,255);
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
        
        //Capture Icons Goes here
        influenceOwnerIconContainer = (Container) new GenericContainer()
        .setLayout(ContainerType.OVERLAY)
        .setAlign(WidgetAnchor.CENTER_LEFT)
        .setAnchor(WidgetAnchor.CENTER_LEFT).setWidth(427)
        .setHeight(10).setX(5).setY(40);
        
        screenElements.add(influenceOwnerIconContainer);
        screenCaptureElements.add(influenceOwnerIconContainer);
        
        influenceOwnerIcon = (Texture) new GenericTexture("faction.png")
        .setMargin(0, 0, 0, 3).setHeight(8).setWidth(8).setFixed(true);
        
        screenElements.add(influenceOwnerIcon);
        screenCaptureElements.add(influenceOwnerIcon);
        
        influenceOwnerIconContainer.addChild(influenceOwnerIcon);
        
        //Capture Bar
        captureBarContainer = (Container) new GenericContainer()
        .setLayout(ContainerType.OVERLAY)
        .setAlign(WidgetAnchor.CENTER_LEFT)
        .setAnchor(WidgetAnchor.CENTER_LEFT).setWidth(427)
        .setHeight(10).setX(20).setY(40); //Changed X to 20. (+10)
        
        screenElements.add(captureBarContainer);
        screenCaptureElements.add(captureBarContainer);
        
//        Integer red = rcplayer.getCurrentRegion().getInfluenceOwner().getFactionColor().getRed();
//        Integer green = rcplayer.getCurrentRegion().getInfluenceOwner().getFactionColor().getGreen();
//        Integer blue = rcplayer.getCurrentRegion().getInfluenceOwner().getFactionColor().getBlue();
        
        Color spoutColor = new Color(255,0,0);
        
        captureBar = (Gradient) new GenericGradient(spoutColor).setWidth(100)
                .setHeight(10).setMargin(0, 3).setFixed(true).setPriority(RenderPriority.High);
        
        screenElements.add(captureBar);
        screenCaptureElements.add(captureBar);
        
        captureBarContainer.addChild(captureBar);
        
        //Empty Capture Bar Overlay
        captureBarSpaceContainer = (Container) new GenericContainer()
        .setLayout(ContainerType.OVERLAY)
        .setAlign(WidgetAnchor.CENTER_RIGHT)
        .setAnchor(WidgetAnchor.CENTER_LEFT)
        .setHeight(10).setX(73).setY(40);
        
        screenElements.add(captureBarSpaceContainer);
        screenCaptureElements.add(captureBarSpaceContainer);
        
        captureBarSpace = (Gradient) new GenericGradient(new Color(0F, 0F, 0F, 1F)).setWidth(0)
                .setHeight(10).setFixed(true).setPriority(RenderPriority.Low);
        
        screenElements.add(captureBarSpace);
        screenCaptureElements.add(captureBarSpace);
        
        captureBarSpaceContainer.addChild(captureBarSpace);
        
        //Capture Bar Background
        captureBarBackgroundContainer = (Container) new GenericContainer()
        .setLayout(ContainerType.OVERLAY)
        .setAlign(WidgetAnchor.CENTER_LEFT)
        .setAnchor(WidgetAnchor.CENTER_LEFT).setWidth(427)
        .setHeight(12).setX(21).setY(39);
        
        screenElements.add(captureBarBackgroundContainer);
        screenCaptureElements.add(captureBarBackgroundContainer);
        
        captureBarBackground = (Gradient) new GenericGradient(new Color(0F, 0F, 0F, 1F)).setWidth(103)
                .setHeight(12).setFixed(true).setPriority(RenderPriority.Highest);
        
        screenElements.add(captureBarBackground);
        screenCaptureElements.add(captureBarBackground);
        
        captureBarBackgroundContainer.addChild(captureBarBackground);
        
        //Timer
        timerContainer = (Container) new GenericContainer()
        .setLayout(ContainerType.OVERLAY)
        .setAlign(WidgetAnchor.CENTER_LEFT)
        .setAnchor(WidgetAnchor.CENTER_LEFT).setWidth(427)
        .setHeight(10).setX(60).setY(41);
        
        screenElements.add(timerContainer);
        screenCaptureElements.add(timerContainer);
        
        captureTimer = (Label) new GenericLabel().setText(" ").setResize(true).setFixed(true).setPriority(RenderPriority.Lowest);
        
        screenElements.add(captureTimer);
        screenCaptureElements.add(captureTimer);
        
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
        screenCaptureElements.add(barAnimContainer);
        
        captureBarAnim = (Texture) new GenericTexture().setDrawAlphaChannel(true)
                .setHeight(10).setFixed(true).setPriority(RenderPriority.Normal).setVisible(false);
        
        screenElements.add(captureBarAnim);
        screenCaptureElements.add(captureBarAnim);
        
        captureBarAnim.setUrl("null.png").setWidth(125);
        hideAllElements();
        showNonCaptureElements();
        
        barAnimContainer.addChild(captureBarAnim);
        
        screen.attachWidgets(RegionControl.plugin, backgroundContainer, regionInfo,controlPointsContainer, influenceOwnerIconContainer, captureBarContainer, captureBarSpaceContainer, captureBarBackgroundContainer, timerContainer, barAnimContainer);
        screen.attachWidgets(RegionControl.plugin, background , ownericon, regionname, influenceOwnerIcon , captureBarBackground,captureBar, captureBarSpace, captureTimer,captureBarAnim);
        for(Label controlPoint : controlPointLabels)
        {
            screen.attachWidget(RegionControl.plugin, controlPoint);
        }
        
        updateRegion(region);
        
        runnable = new BukkitRunnable()
        {
            @Override
            public void run()
            {
                if(region != null && region.isBeingCaptured())
                {
                    if(captureElementsHidden)
                    {
                        showCaptureElements();
                    }
                    
                    if(background.getHeight() != 70)
                    {
                        background.setHeight(70);
                    }
                    
                    Integer seconds = region.getSecondsToCapture();
                    Integer minutes = region.getMinutesToCapture();
                    
                    if(seconds == 0 && minutes == 0)
                    {
                        captureTimer.setVisible(false);
                    }
                    else
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
                        float influence = region.getInfluenceMap().get(region.getInfluenceOwner());
                        float baseinfluence = region.getBaseInfluence();
                        
                        int barwidth = (int) (influence / baseinfluence * 100);
                        captureBar.setWidth(barwidth);
                        captureBarSpace.setWidth(100 - barwidth);
                        
                        Integer red = region.getInfluenceOwner().getFactionColor().getRed();
                        Integer green = region.getInfluenceOwner().getFactionColor().getGreen();
                        Integer blue = region.getInfluenceOwner().getFactionColor().getBlue();
                        
                        Color spoutColor = new Color(red,green,blue);
                        captureBar.setColor(spoutColor);
                    }
                }
                else if(region != null && !region.isBeingCaptured() && !captureElementsHidden)
                {
                    hideCaptureElements();
                    if(background.getHeight() != 40)
                    {
                        background.setHeight(40);
                    }
                }
            }
        }.runTaskTimer(RegionControl.plugin, 20, 20);
    }
    
    public void updateRegion(CapturableRegion updatedRegion)
    {
        this.region = updatedRegion;
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
            
            //ownericon.setUrl(region.getOwner().getFactionIconUrl()); //TODO
            ownericon.setUrl("faction.png");
            regionname.setText(updatedRegion.getDisplayName());
            
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
                captureBarAnim.animateStop(false);
                hideCaptureElements();
                background.setHeight(40);
            }
        }
    }
    
    public void updateInfluenceRate(Float influenceRate)
    {
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
                captureBarAnim.animateStop(false).setVisible(true);
                if(region.getMajorityController() == region.getInfluenceOwner())
                {
                    captureBarAnim.setUrl("Capture_Anim_Capturing.png").setWidth(30);
                    captureBarAnim.animate(WidgetAnim.POS_X, barFloatValue, barShortValue, barAnimRate, true, true).animateStart();
                }
                
                else if (region.getMajorityController() != region.getInfluenceOwner())
                {
                    captureBarAnim.setUrl("Capture_Anim_Losing.png").setWidth(125);
                    captureBarAnim.animate(WidgetAnim.POS_X, -barFloatValue, barShortValue, barAnimRate, true, true).animateStart();
                }
            }
        }
        
        else
        {
            captureBarAnim.animateStop(false).setVisible(false);
        }
    }
    
    public void updateControlPoints(CapturableRegion region)
    {
        RegionControl.plugin.getLogger().log(Level.INFO, "DEBUG: Control Point Label Size is: " + String.valueOf(controlPointLabels.size()));
        if(region != null && controlPointLabels.size() == 0)
        {
            RegionControl.plugin.getLogger().log(Level.INFO, "DEBUG: Method Triggered.");
            for(ControlPoint controlPoint : region.getControlPoints())
            {
                Color spoutColor = new Color(255,255,255);
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
                        Color spoutColor = new Color(255,255,255);
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
                Color spoutColor = new Color(255,255,255);
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
}
