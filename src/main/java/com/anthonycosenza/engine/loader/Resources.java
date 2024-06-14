package com.anthonycosenza.engine.loader;

import com.anthonycosenza.Main;

import java.io.File;

public class Resources
{
    private static final File jarFile = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath());
    private static final File jarDir = jarFile.getParentFile();
    
    
    public static String get(String path)
    {
        return jarDir.getAbsolutePath() + "\\resources\\" + path;
    }
}
