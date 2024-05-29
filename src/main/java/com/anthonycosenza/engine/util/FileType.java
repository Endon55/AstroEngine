package com.anthonycosenza.engine.util;

import java.io.File;

public enum FileType
{
    SETTINGS,
    CODE,
    DIRECTORY,
    TEXT,
    
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
        if(extension.endsWith(".java"))
        {
            return CODE;
        }
        
        return TEXT;
    }
}
