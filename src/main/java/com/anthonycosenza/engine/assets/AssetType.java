package com.anthonycosenza.engine.assets;

import com.anthonycosenza.engine.space.rendering.materials.StandardMaterial;
import com.anthonycosenza.engine.space.node.Scene;

import java.util.Arrays;
import java.util.List;

public enum AssetType
{
    TEXTURE(null),
    MESH(null),
    SCENE(Scene::new),
    MODEL(null),
    MATERIAL(StandardMaterial::new),
    SHADER(null),
    
    ;
    final AssetFunction createNewFunction;
    
    AssetType(AssetFunction function)
    {
        this.createNewFunction = function;
    }
    public Asset create()
    {
        return createNewFunction.create();
    }
    public boolean hasFunction()
    {
        return createNewFunction != null;
    }
    public String getExtension()
    {
        return "a" + this.name().toLowerCase();
    }
    
    public static final List<String> FILE_EXTENSIONS = Arrays.stream(AssetType.values()).map(AssetType::getExtension).toList();

}
