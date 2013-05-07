package com.featherminecraft.regioncontrol;

import org.getspout.spoutapi.gui.*;
import org.getspout.spoutapi.player.SpoutPlayer;

public class SpoutClientLogic {

    public static Label regioname;
    public static Texture factionicon;
    public Widget regionname;
    
    public SpoutClientLogic(SpoutPlayer splayer)
    {
        //Test Implementation of Spout Widget.
        Color back = new Color(0.0F, 0.0F, 0.0F, 0.75F);
        Color bottom = new Color(1.0F, 1.0F, 1.0F, 0.75F);
        Screen screen = splayer.getMainScreen();
        
        regionname = new GenericLabel().setText(""); //Region Name to be Displayed at all times.
        factionicon = new GenericTexture().setUrl("defaultfaction.png");
        
        screen.attachWidgets(RegionControl.plugin, regionname, regionname);
    }
}
