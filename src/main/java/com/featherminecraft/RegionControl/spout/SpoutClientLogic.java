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

public class SpoutClientLogic {
    public static void init() {
        // Precache Included Assets
        if (!new File(RegionControl.plugin.getDataFolder(), "background.png")
                .exists()) {
            RegionControl.plugin.saveResource("background.png", false);
        }

        if (!new File(RegionControl.plugin.getDataFolder(),
                "Capture_Anim_Losing.png").exists()) {
            RegionControl.plugin.saveResource("Capture_Anim_Losing.png", false);
        }

        if (!new File(RegionControl.plugin.getDataFolder(),
                "Capture_Anim_Capturing.png").exists()) {
            RegionControl.plugin.saveResource("Capture_Anim_Capturing.png",
                    false);
        }

        if (!new File(RegionControl.plugin.getDataFolder(), "null.png")
                .exists()) {
            RegionControl.plugin.saveResource("null.png", false);
        }

        if (!new File(RegionControl.plugin.getDataFolder(), "music.wav")
                .exists()) {
            RegionControl.plugin.saveResource("music.wav", false);
        }

        if (!new File(RegionControl.plugin.getDataFolder(), "captured.wav")
                .exists()) {
            RegionControl.plugin.saveResource("captured.wav", false);
        }

        if (!new File(RegionControl.plugin.getDataFolder(), "captured2.wav")
                .exists()) {
            RegionControl.plugin.saveResource("captured2.wav", false);
        }

        SpoutManager.getFileManager().addToCache(
                RegionControl.plugin,
                new File(RegionControl.plugin.getDataFolder().getAbsolutePath()
                        + "/background.png"));
        SpoutManager.getFileManager().addToCache(
                RegionControl.plugin,
                new File(RegionControl.plugin.getDataFolder().getAbsolutePath()
                        + "/Capture_Anim_Losing.png"));
        SpoutManager.getFileManager().addToCache(
                RegionControl.plugin,
                new File(RegionControl.plugin.getDataFolder().getAbsolutePath()
                        + "/Capture_Anim_Capturing.png"));
        SpoutManager.getFileManager().addToCache(
                RegionControl.plugin,
                new File(RegionControl.plugin.getDataFolder().getAbsolutePath()
                        + "/null.png"));
        SpoutManager.getFileManager().addToCache(
                RegionControl.plugin,
                new File(RegionControl.plugin.getDataFolder().getAbsolutePath()
                        + "/music.wav"));
        SpoutManager.getFileManager().addToCache(
                RegionControl.plugin,
                new File(RegionControl.plugin.getDataFolder().getAbsolutePath()
                        + "/captured.wav"));
        SpoutManager.getFileManager().addToCache(
                RegionControl.plugin,
                new File(RegionControl.plugin.getDataFolder().getAbsolutePath()
                        + "/captured2.wav"));

        // Precache Faction Icons
        Config config = new Config();
        for (Entry<String, Faction> faction : ServerLogic.factions.entrySet()) {
            String factionUrl = config.getMainConfig().getString(
                    "factions." + faction.getKey() + ".factionIcon");
            faction.getValue().setFactionIconUrl(factionUrl);
            if (faction.getValue().getFactionIconUrl() != null
                    && faction.getValue().getFactionIconUrl() != ""
                    && new File(RegionControl.plugin.getDataFolder(), faction
                            .getValue().getFactionIconUrl()).exists()) {
                SpoutManager.getFileManager().addToCache(
                        RegionControl.plugin,
                        new File(RegionControl.plugin.getDataFolder()
                                .getAbsolutePath()
                                + "/"
                                + faction.getValue().getFactionIconUrl()));
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
    protected CapturableRegion region;
    private boolean musicPlaying;

    private SpoutPlayer splayer;

    public BukkitTask getRunnable() {
        return this.runnable;
    }

    private void hideAllElements() {
        if (!this.allElementsHidden) {
            for (Widget element : this.screenElements) {
                element.setVisible(false);
            }
            this.allElementsHidden = true;
            this.captureElementsHidden = true;
        }
    }

    private void hideCaptureElements() {
        if (!this.captureElementsHidden) {
            for (Widget element : this.screenCaptureElements) {
                element.setVisible(false);
            }
            this.captureElementsHidden = true;
        }
    }

    public void setRegionCaptureStatus(Boolean captureStatus) {
        if (captureStatus == false) {
            hideCaptureElements();
            if (this.background.getHeight() != 40) {
                this.background.setHeight(40);
            }
            this.captureBarAnim.animateStop(false);
            SpoutManager.getSoundManager().stopMusic(this.splayer, false, 1000);
        }

        else if (captureStatus == true) {
            showCaptureElements();
            if (this.background.getHeight() != 70) {
                this.background.setHeight(70);
            }
        }
    }

    public void setupClientElements(RCPlayer rcplayer) {
        this.splayer = ((SpoutPlayer) rcplayer.getBukkitPlayer());
        this.screen = this.splayer.getMainScreen();
        this.region = rcplayer.getCurrentRegion();

        // Background
        this.backgroundContainer = (Container) new GenericContainer()
                .setLayout(ContainerType.VERTICAL)
                .setAlign(WidgetAnchor.CENTER_LEFT)
                .setAnchor(WidgetAnchor.CENTER_LEFT).setWidth(150)
                .setHeight(23).setX(3).setY(-5);

        this.screenElements.add(this.backgroundContainer);

        this.background = (Texture) new GenericTexture("background.png")
                .setDrawAlphaChannel(true).setWidth(150).setHeight(23)
                .setPriority(RenderPriority.Highest);

        this.screenElements.add(this.background);

        this.backgroundContainer.addChild(this.background);

        // Region Info
        this.regionInfo = (Container) new GenericContainer()
                .setLayout(ContainerType.HORIZONTAL)
                .setAlign(WidgetAnchor.CENTER_LEFT)
                .setAnchor(WidgetAnchor.CENTER_LEFT).setWidth(145)
                .setHeight(10).setX(10).setY(3).setMargin(3);

        this.screenElements.add(this.regionInfo);

        this.ownericon = (Texture) new GenericTexture()
                .setUrl(this.region.getOwner().getFactionIconUrl())
                .setHeight(16).setWidth(16).setFixed(true)
                .setMargin(-5, 6, 0, 0);

        this.screenElements.add(this.ownericon);

        this.regionname = (Label) new GenericLabel()
                .setText(this.region.getDisplayName()).setShadow(false)
                .setResize(true).setFixed(true);

        this.screenElements.add(this.regionname);

        float currentscale = 1F;
        while (GenericLabel.getStringWidth(this.regionname.getText(),
                currentscale) > 116) {
            currentscale -= 0.01F;
        }
        this.regionname.setScale(currentscale);

        this.regionInfo.addChildren(this.ownericon, this.regionname);

        // Control Points
        // For Container Width, recommended to get amount of control points from
        // region.
        this.controlPointsContainer = (Container) new GenericContainer()
                .setLayout(ContainerType.HORIZONTAL)
                .setAlign(WidgetAnchor.CENTER_CENTER)
                .setAnchor(WidgetAnchor.CENTER_LEFT).setWidth(50).setHeight(10)
                .setX(50).setY(25).setMarginRight(1);

        this.screenElements.add(this.controlPointsContainer);

        for (ControlPoint controlPoint : this.region.getControlPoints()) {
            Color spoutColor = new Color(255, 255, 255);
            if (!controlPoint.isCapturing()) {
                Integer red = controlPoint.getInfluenceOwner()
                        .getFactionColor().getRed();
                Integer green = controlPoint.getInfluenceOwner()
                        .getFactionColor().getGreen();
                Integer blue = controlPoint.getInfluenceOwner()
                        .getFactionColor().getBlue();

                spoutColor.setRed(red).setGreen(green).setBlue(blue);
            }
            this.controlPointLabels.add((Label) new GenericLabel()
                    .setText(controlPoint.getIdentifier().toUpperCase())
                    .setTextColor(spoutColor).setScale(1.5F).setShadow(false)
                    .setResize(true).setAlign(WidgetAnchor.CENTER_CENTER)
                    .setFixed(true).setMargin(5));
        }

        for (Label controlPoint : this.controlPointLabels) {
            this.controlPointsContainer.addChild(controlPoint);
            this.screenElements.add(controlPoint);
        }

        // Capture Icons Goes here
        this.influenceOwnerIconContainer = (Container) new GenericContainer()
                .setLayout(ContainerType.OVERLAY)
                .setAlign(WidgetAnchor.CENTER_LEFT)
                .setAnchor(WidgetAnchor.CENTER_LEFT).setWidth(427)
                .setHeight(10).setX(5).setY(40);

        this.screenElements.add(this.influenceOwnerIconContainer);
        this.screenCaptureElements.add(this.influenceOwnerIconContainer);

        this.influenceOwnerIcon = (Texture) new GenericTexture("null.png")
                .setMargin(0, 0, 0, 3).setHeight(8).setWidth(8).setFixed(true);

        this.screenElements.add(this.influenceOwnerIcon);
        this.screenCaptureElements.add(this.influenceOwnerIcon);

        this.influenceOwnerIconContainer.addChild(this.influenceOwnerIcon);

        // Capture Bar
        this.captureBarContainer = (Container) new GenericContainer()
                .setLayout(ContainerType.OVERLAY)
                .setAlign(WidgetAnchor.CENTER_LEFT)
                .setAnchor(WidgetAnchor.CENTER_LEFT).setWidth(427)
                .setHeight(10).setX(20).setY(40);

        this.screenElements.add(this.captureBarContainer);
        this.screenCaptureElements.add(this.captureBarContainer);

        this.captureBar = (Gradient) new GenericGradient(new Color(255, 255,
                255, 255)).setWidth(100).setHeight(10).setMargin(0, 3)
                .setFixed(true).setPriority(RenderPriority.High);

        this.screenElements.add(this.captureBar);
        this.screenCaptureElements.add(this.captureBar);

        this.captureBarContainer.addChild(this.captureBar);

        // Empty Capture Bar Overlay
        this.captureBarSpaceContainer = (Container) new GenericContainer()
                .setLayout(ContainerType.OVERLAY)
                .setAlign(WidgetAnchor.CENTER_RIGHT)
                .setAnchor(WidgetAnchor.CENTER_LEFT).setHeight(10).setX(73)
                .setY(40);

        this.screenElements.add(this.captureBarSpaceContainer);
        this.screenCaptureElements.add(this.captureBarSpaceContainer);

        this.captureBarSpace = (Gradient) new GenericGradient(new Color(0F, 0F,
                0F, 1F)).setWidth(0).setHeight(10).setFixed(true)
                .setPriority(RenderPriority.Low);

        this.screenElements.add(this.captureBarSpace);
        this.screenCaptureElements.add(this.captureBarSpace);

        this.captureBarSpaceContainer.addChild(this.captureBarSpace);

        // Capture Bar Background
        this.captureBarBackgroundContainer = (Container) new GenericContainer()
                .setLayout(ContainerType.OVERLAY)
                .setAlign(WidgetAnchor.CENTER_LEFT)
                .setAnchor(WidgetAnchor.CENTER_LEFT).setWidth(427)
                .setHeight(12).setX(21).setY(39);

        this.screenElements.add(this.captureBarBackgroundContainer);
        this.screenCaptureElements.add(this.captureBarBackgroundContainer);

        this.captureBarBackground = (Gradient) new GenericGradient(new Color(
                0F, 0F, 0F, 1F)).setWidth(104).setHeight(12).setFixed(true)
                .setPriority(RenderPriority.Highest);

        this.screenElements.add(this.captureBarBackground);
        this.screenCaptureElements.add(this.captureBarBackground);

        this.captureBarBackgroundContainer.addChild(this.captureBarBackground);

        // Timer
        this.timerContainer = (Container) new GenericContainer()
                .setLayout(ContainerType.OVERLAY)
                .setAlign(WidgetAnchor.CENTER_LEFT)
                .setAnchor(WidgetAnchor.CENTER_LEFT).setWidth(427)
                .setHeight(10).setX(60).setY(41);

        this.screenElements.add(this.timerContainer);
        this.screenCaptureElements.add(this.timerContainer);

        this.captureTimer = (Label) new GenericLabel().setText(" ")
                .setResize(true).setFixed(true)
                .setPriority(RenderPriority.Lowest);

        this.screenElements.add(this.captureTimer);
        this.screenCaptureElements.add(this.captureTimer);

        this.timerContainer.addChild(this.captureTimer);

        // Capture Bar Animation
        this.barAnimContainer = (Container) new GenericContainer()
                .setLayout(ContainerType.OVERLAY)
                .setAlign(WidgetAnchor.CENTER_LEFT)
                .setAnchor(WidgetAnchor.CENTER_LEFT).setWidth(427)
                .setHeight(10).setMarginLeft(100).setY(40);

        this.screenElements.add(this.barAnimContainer);
        this.screenCaptureElements.add(this.barAnimContainer);

        this.captureBarAnim = (Texture) new GenericTexture()
                .setDrawAlphaChannel(true).setHeight(10).setFixed(true)
                .setPriority(RenderPriority.Normal).setVisible(false);

        this.screenElements.add(this.captureBarAnim);
        this.screenCaptureElements.add(this.captureBarAnim);

        this.captureBarAnim.setUrl("null.png").setWidth(125);
        hideAllElements();
        showNonCaptureElements();

        this.barAnimContainer.addChild(this.captureBarAnim);

        this.screen.attachWidgets(RegionControl.plugin,
                this.backgroundContainer, this.regionInfo,
                this.controlPointsContainer, this.influenceOwnerIconContainer,
                this.captureBarContainer, this.captureBarSpaceContainer,
                this.captureBarBackgroundContainer, this.timerContainer,
                this.barAnimContainer);
        this.screen.attachWidgets(RegionControl.plugin, this.background,
                this.ownericon, this.regionname, this.influenceOwnerIcon,
                this.captureBarBackground, this.captureBar,
                this.captureBarSpace, this.captureTimer, this.captureBarAnim);

        for (Label controlPoint : this.controlPointLabels) {
            this.screen.attachWidget(RegionControl.plugin, controlPoint);
        }

        updateRegion(rcplayer.getCurrentRegion());

        this.runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (SpoutClientLogic.this.region != null
                        && SpoutClientLogic.this.region.isBeingCaptured()) {
                    Integer seconds = SpoutClientLogic.this.region
                            .getSecondsToCapture();
                    Integer minutes = SpoutClientLogic.this.region
                            .getMinutesToCapture();

                    if (seconds == 0 && minutes == 0) {
                        SpoutClientLogic.this.captureTimer.setVisible(false);
                    } else if (!SpoutClientLogic.this.captureElementsHidden) {
                        if (!SpoutClientLogic.this.captureTimer.isVisible()) {
                            SpoutClientLogic.this.captureTimer.setVisible(true);
                        }

                        String secondsString = seconds.toString();
                        if (seconds < 10) {
                            secondsString = "0" + secondsString;
                        }
                        SpoutClientLogic.this.captureTimer.setText(minutes
                                .toString() + ":" + secondsString);
                    }

                    if (SpoutClientLogic.this.region.getInfluenceOwner() != null) {
                        if (SpoutClientLogic.this.captureElementsHidden
                                && SpoutClientLogic.this.captureBar.getWidth() != 0) {
                            String secondsString = seconds.toString();
                            if (seconds < 10) {
                                secondsString = "0" + secondsString;
                            }
                            SpoutClientLogic.this.captureTimer.setText(minutes
                                    .toString() + ":" + secondsString);
                            showCaptureElements();
                            if (SpoutClientLogic.this.background.getHeight() != 70) {
                                SpoutClientLogic.this.background.setHeight(70);
                            }
                        }

                        float influence = SpoutClientLogic.this.region
                                .getInfluenceMap().get(
                                        SpoutClientLogic.this.region
                                                .getInfluenceOwner());
                        float baseinfluence = SpoutClientLogic.this.region
                                .getBaseInfluence();

                        int barwidth = (int) (influence / baseinfluence * 100);
                        SpoutClientLogic.this.captureBar.setWidth(barwidth);
                        SpoutClientLogic.this.captureBarSpace
                                .setWidth(100 - barwidth);

                        if (barwidth > 75
                                && barwidth < 100
                                && SpoutClientLogic.this.musicPlaying == false
                                && SpoutClientLogic.this.region
                                        .getInfluenceOwner() == SpoutClientLogic.this.region
                                        .getMajorityController()) {
                            SpoutManager.getSoundManager().playCustomMusic(
                                    RegionControl.plugin,
                                    SpoutClientLogic.this.splayer, "music.wav",
                                    false);
                            SpoutClientLogic.this.musicPlaying = true;
                        } else if (SpoutClientLogic.this.musicPlaying == true
                                && (barwidth < 75 || barwidth == 100)) {
                            SpoutManager.getSoundManager().stopMusic(
                                    SpoutClientLogic.this.splayer, false, 1000);
                            SpoutClientLogic.this.musicPlaying = false;
                        }

                        Integer red = SpoutClientLogic.this.region
                                .getInfluenceOwner().getFactionColor().getRed();
                        Integer green = SpoutClientLogic.this.region
                                .getInfluenceOwner().getFactionColor()
                                .getGreen();
                        Integer blue = SpoutClientLogic.this.region
                                .getInfluenceOwner().getFactionColor()
                                .getBlue();

                        Color spoutColor = new Color(red, green, blue);
                        SpoutClientLogic.this.captureBar.setColor(spoutColor);
                    }

                    if (SpoutClientLogic.this.captureBar.getWidth() == 0) {
                        hideCaptureElements();
                        if (SpoutClientLogic.this.background.getHeight() != 40) {
                            SpoutClientLogic.this.background.setHeight(40);
                        }
                    }
                }
            }
        }.runTaskTimer(RegionControl.plugin, 10, 10);
    }

    private void showCaptureElements() {
        if (this.captureElementsHidden) {
            for (Widget element : this.screenCaptureElements) {
                element.setVisible(true);
            }
            this.captureElementsHidden = false;
        }
    }

    private void showNonCaptureElements() {
        if (this.allElementsHidden) {
            for (Widget element : this.screenElements) {
                if (!this.screenCaptureElements.contains(element)) {
                    element.setVisible(true);
                }
            }
            this.allElementsHidden = false;
        }
    }

    public void updateControlPoints(CapturableRegion region) {
        if (region != null && this.controlPointLabels.size() == 0) {
            for (ControlPoint controlPoint : region.getControlPoints()) {
                Color spoutColor = new Color(255, 255, 255);
                if (!controlPoint.isCapturing()) {
                    Integer red = controlPoint.getOwner().getFactionColor()
                            .getRed();
                    Integer green = controlPoint.getOwner().getFactionColor()
                            .getGreen();
                    Integer blue = controlPoint.getOwner().getFactionColor()
                            .getBlue();

                    spoutColor.setRed(red).setGreen(green).setBlue(blue);
                }
                this.controlPointLabels.add((Label) new GenericLabel()
                        .setText(controlPoint.getIdentifier().toUpperCase())
                        .setTextColor(spoutColor).setScale(1.5F)
                        .setShadow(false).setResize(true)
                        .setAlign(WidgetAnchor.CENTER_CENTER).setFixed(true)
                        .setMargin(5));
            }

            for (Label controlPointLabel : this.controlPointLabels) {
                this.controlPointsContainer.addChild(controlPointLabel);
                this.screenElements.add(controlPointLabel);
                this.screen.attachWidget(RegionControl.plugin,
                        controlPointLabel);
            }
        }

        else if (region != null
                && this.controlPointLabels.size() == region.getControlPoints()
                        .size()) {
            for (ControlPoint controlPoint : region.getControlPoints()) {
                for (Label controlPointLabel : this.controlPointLabels) {
                    if (controlPointLabel.getText().equalsIgnoreCase(
                            controlPoint.getIdentifier())) {
                        Color spoutColor = new Color(255, 255, 255);
                        if (!controlPoint.isCapturing()) {
                            Integer red = controlPoint.getOwner()
                                    .getFactionColor().getRed();
                            Integer green = controlPoint.getOwner()
                                    .getFactionColor().getGreen();
                            Integer blue = controlPoint.getOwner()
                                    .getFactionColor().getBlue();

                            spoutColor.setRed(red).setGreen(green)
                                    .setBlue(blue);
                        }
                        controlPointLabel.setTextColor(spoutColor);
                        break;
                    }
                }
            }
        }

        else if (region != null
                && this.controlPointLabels.size() != region.getControlPoints()
                        .size() && this.controlPointLabels.size() != 0) {
            for (Label controlPoint : this.controlPointLabels) {
                if (controlPoint != null) {
                    this.controlPointsContainer.removeChild(controlPoint);
                    this.screenElements.remove(controlPoint);
                    this.screen.removeWidget(controlPoint);
                }
            }
            this.controlPointLabels = new ArrayList<Label>();
            for (ControlPoint controlPoint : region.getControlPoints()) {
                Color spoutColor = new Color(255, 255, 255);
                if (!controlPoint.isCapturing()) {
                    Integer red = controlPoint.getOwner().getFactionColor()
                            .getRed();
                    Integer green = controlPoint.getOwner().getFactionColor()
                            .getGreen();
                    Integer blue = controlPoint.getOwner().getFactionColor()
                            .getBlue();

                    spoutColor.setRed(red).setGreen(green).setBlue(blue);
                }
                this.controlPointLabels.add((Label) new GenericLabel()
                        .setText(controlPoint.getIdentifier().toUpperCase())
                        .setTextColor(spoutColor).setScale(1.5F)
                        .setShadow(false).setResize(true)
                        .setAlign(WidgetAnchor.CENTER_CENTER).setFixed(true)
                        .setMargin(5));
            }

            for (Label controlPointLabel : this.controlPointLabels) {
                this.controlPointsContainer.addChild(controlPointLabel);
                this.screenElements.add(controlPointLabel);
                this.screen.attachWidget(RegionControl.plugin,
                        controlPointLabel);
            }
        }

        else if (region == null && this.controlPointLabels.size() != 0) {
            for (Label controlPointLabel : this.controlPointLabels) {
                if (controlPointLabel != null) {
                    this.controlPointsContainer.removeChild(controlPointLabel);
                    this.screenElements.remove(controlPointLabel);
                    this.screen.removeWidget(controlPointLabel);
                }
            }
            this.controlPointLabels = new ArrayList<Label>();
        }
    }

    public void updateInfluenceRate(Float influenceRate) {
        this.screen.removeWidget(this.barAnimContainer).removeWidget(
                this.captureBarAnim);
        if (influenceRate != null && influenceRate != 0F
                && this.region.isBeingCaptured()) {
            short barAnimRate = 0;
            float barFloatValue = 0F;
            short barShortValue = 0;

            if (influenceRate == 1F) {
                barAnimRate = 7;
                barFloatValue = 0.92F;
                barShortValue = 100;
            }

            else if (influenceRate == 2F) {
                barAnimRate = 5;
                barFloatValue = 1.15F;
                barShortValue = 80;
            }

            else if (influenceRate == 3F) {
                barAnimRate = 3;
                barFloatValue = 1.55F;
                barShortValue = 60;
            }

            else if (influenceRate == 4F) {
                barAnimRate = 1;
                barFloatValue = 2.35F;
                barShortValue = 40;
            }

            if (influenceRate == 1F || influenceRate == 2F
                    || influenceRate == 3F || influenceRate == 4F) {
                if (this.region.getInfluenceOwner() == null) {
                    this.captureBarAnim.setUrl("Capture_Anim_Capturing.png")
                            .setWidth(30);
                    this.captureBarAnim
                            .animate(WidgetAnim.POS_X, barFloatValue,
                                    barShortValue, barAnimRate, true, true)
                            .animateStart().setDirty(true);
                    this.influenceOwnerIcon.setUrl(this.region
                            .getMajorityController().getFactionIconUrl());
                }

                else if (this.region.getMajorityController() == this.region
                        .getInfluenceOwner()) {
                    this.captureBarAnim.setUrl("Capture_Anim_Capturing.png")
                            .setWidth(30);
                    this.captureBarAnim
                            .animate(WidgetAnim.POS_X, barFloatValue,
                                    barShortValue, barAnimRate, true, true)
                            .animateStart().setDirty(true);
                }

                else if (this.region.getMajorityController() != this.region
                        .getInfluenceOwner()) {
                    this.captureBarAnim.setUrl("Capture_Anim_Losing.png")
                            .setWidth(125);
                    this.captureBarAnim
                            .animate(WidgetAnim.POS_X, -barFloatValue,
                                    barShortValue, barAnimRate, true, true)
                            .animateStart().setDirty(true);
                }

                this.screen.attachWidgets(RegionControl.plugin,
                        this.barAnimContainer, this.captureBarAnim);

                if (!this.captureBarAnim.isVisible()
                        && !this.captureElementsHidden) {
                    this.captureBarAnim.setVisible(true);
                }
            }
        }

        else {
            this.captureBarAnim.animateStop(false).setVisible(false);
        }
    }

    public void updateRegion(CapturableRegion updatedRegion) {
        this.region = updatedRegion;
        if (this.musicPlaying) {
            SpoutManager.getSoundManager().stopMusic(this.splayer, false, 1000);
            this.musicPlaying = false;
        }

        if (updatedRegion == null) {
            if (this.allElementsHidden == false) {
                hideAllElements();
            }
            return;
        }

        else if (updatedRegion != null) {
            updateControlPoints(updatedRegion);
            if (this.allElementsHidden == true) {
                showNonCaptureElements();
                if (updatedRegion.isBeingCaptured()) {
                    showCaptureElements();
                }
            }

            this.ownericon.setUrl(this.region.getOwner().getFactionIconUrl());
            this.regionname.setText(updatedRegion.getDisplayName());

            if (!this.region.isSpawnRegion()) {
                this.influenceOwnerIcon.setUrl(this.region.getInfluenceOwner()
                        .getFactionIconUrl());

                Integer red = updatedRegion.getInfluenceOwner()
                        .getFactionColor().getRed();
                Integer green = updatedRegion.getInfluenceOwner()
                        .getFactionColor().getGreen();
                Integer blue = updatedRegion.getInfluenceOwner()
                        .getFactionColor().getBlue();

                Color spoutColor = new Color(red, green, blue);
                this.captureBar.setColor(spoutColor);
            }

            float currentscale = 1F;
            while (GenericLabel.getStringWidth(this.regionname.getText(),
                    currentscale) > 116) {
                currentscale -= 0.01F;
            }
            this.regionname.setScale(currentscale);

            if (updatedRegion.isBeingCaptured()) {
                showCaptureElements();
                this.background.setHeight(70);

                updateInfluenceRate(this.region.getInfluenceRate());
            }

            else if (!updatedRegion.isBeingCaptured()) {
                if (this.captureBarAnim.isVisible()) {
                    this.captureBarAnim.animateStop(false);
                }
                hideCaptureElements();
                if (this.region.isSpawnRegion()
                        && this.background.getHeight() != 23) {
                    this.background.setHeight(23);
                } else if (!this.region.isSpawnRegion()
                        && this.background.getHeight() != 40) {
                    this.background.setHeight(40);
                }
            }
        }
    }
}
