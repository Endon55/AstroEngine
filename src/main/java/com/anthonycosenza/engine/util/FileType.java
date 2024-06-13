package com.anthonycosenza.engine.util;

import com.anthonycosenza.engine.assets.AssetType;

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
    SHADER,
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
        else if(extension.endsWith(AssetType.SCENE.getExtension()))
        {
            return SCENE;
        }
        else if(extension.endsWith(AssetType.MATERIAL.getExtension()))
        {
            return MATERIAL;
        }
        else if(extension.endsWith(AssetType.MODEL.getExtension()))
        {
            return MODEL;
        }
        else if(extension.endsWith(AssetType.TEXTURE.getExtension()))
        {
            return TEXTURE;
        }
        else if(extension.endsWith(AssetType.VERTEX.getExtension()) || extension.endsWith(AssetType.FRAGMENT.getExtension()))
        {
            return SHADER;
        }
        
        return TEXT;
    }
}
