package com.featherminecraft.regioncontrol.utils;

import org.getspout.spoutapi.gui.Label;
import org.getspout.spoutapi.gui.Widget;

public class SpoutUtils {
    public Widget UpdateLabelText (Label widget, String text)
    {
        widget = widget.setText(text);
        return widget;
    }
}
