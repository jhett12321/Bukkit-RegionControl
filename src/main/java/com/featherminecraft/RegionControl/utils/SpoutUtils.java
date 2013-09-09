package com.featherminecraft.RegionControl.utils;

import org.getspout.spoutapi.gui.Label;
import org.getspout.spoutapi.gui.Texture;
import org.getspout.spoutapi.gui.Widget;

import com.featherminecraft.RegionControl.Config;

public class SpoutUtils {
    public Widget updateLabelText (Label widget, String text)
    {
        widget = widget.setText(text);
        return widget;
    }
    
    public Widget updateTexture (Texture widget, String newtexture)
    {
        widget = widget.setUrl(newtexture);
        return widget;
    }
    
    public int getMaxSpawnButtons()
    {
        return new Config().getMainConfig().getInt("spout.maxspawnbuttons");
    }
    
}
