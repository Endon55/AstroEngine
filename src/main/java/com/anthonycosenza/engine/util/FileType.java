package com.anthonycosenza.engine.util;

import java.io.File;

public enum FileType
{
    SETTINGS,
    CODE,
    DIRECTORY,
    TEXT,
    SCENE,
    MODEL,
    MATERIAL,
    TEXTURE,
    PROJECT
    
    ;
    public static FileType getFileType(File file)
    {
        if(file.isDirectory())
        {
            return DIRECTORY;
        }
        String extension = file.getAbsolutePath();
        if(extension.endsWith(".ini"))
        {
            return SETTINGS;
        }
        else if(extension.endsWith(".astro"))
        {
            return PROJECT;
        }
        else if(extension.endsWith(".java"))
        {
            return CODE;
        }
        else if(extension.endsWith(".scene"))
        {
            return SCENE;
        }
        else if(extension.endsWith(".amaterial"))
        {
            return MATERIAL;
        }
        else if(extension.endsWith(".amodel"))
        {
            return MODEL;
        }
        else if(extension.endsWith(".atexture"))
        {
            return TEXTURE;
        }
        
        return TEXT;
    }
}
