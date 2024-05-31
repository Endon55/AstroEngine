package com.anthonycosenza.engine.util;

import java.io.File;

public enum FileType
{
    SETTINGS,
    CODE,
    DIRECTORY,
    TEXT,
    SCENE,
    
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
        else if(extension.endsWith(".java"))
        {
            return CODE;
        }
        else if(extension.endsWith(".scene"))
        {
            return SCENE;
        }
        
        return TEXT;
    }
}
