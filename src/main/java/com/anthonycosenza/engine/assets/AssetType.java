package com.anthonycosenza.engine.assets;

import java.util.Arrays;
import java.util.List;

public enum AssetType
{
    TEXTURE,
    MESH,
    SCENE,
    MODEL,
    MATERIAL
    
    ;
    
    public static final List<String> FILE_EXTENSIONS = Arrays.stream(AssetType.values()).map(assetType -> "a" + assetType.name().toLowerCase()).toList();
}
