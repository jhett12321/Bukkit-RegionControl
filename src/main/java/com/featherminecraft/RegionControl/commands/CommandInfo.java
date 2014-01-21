package com.featherminecraft.RegionControl.commands;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CommandInfo
{
    public String aliases();
    
    public String desc();
    
    public String name();
    
    public String permission();
    
    public String usage();
}
