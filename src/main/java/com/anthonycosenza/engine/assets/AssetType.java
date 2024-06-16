package com.anthonycosenza.engine.assets;

import com.anthonycosenza.engine.space.rendering.materials.StandardMaterial;
import com.anthonycosenza.engine.space.node.Scene;
import com.anthonycosenza.engine.space.rendering.shader.FragmentShader;
import com.anthonycosenza.engine.space.rendering.shader.VertexShader;

import java.util.Arrays;
import java.util.List;

public enum AssetType
{
    TEXTURE(null, false),
    MESH(null, false),
    SCENE(Scene::new, true),
    MODEL(null, false),
    MATERIAL(StandardMaterial::new, true),
    VERTEX(VertexShader::new, true),
    FRAGMENT(FragmentShader::new, true),
    SCRIPT(null, true),
    
    ;
    final AssetFunction createNewFunction;
    final boolean implemented;
    
    AssetType(AssetFunction function, boolean implemented)
    {
        this.createNewFunction = function;
        this.implemented = implemented;
    }
    public Asset create()
    {
        return createNewFunction.create();
    }
    public boolean hasFunction()
    {
        return createNewFunction != null;
    }
    
    public boolean isImplemented()
    {
        return implemented;
    }
    
    public String getExtension()
    {
        return "a" + this.name().toLowerCase();
    }
    
    public static final List<String> FILE_EXTENSIONS = Arrays.stream(AssetType.values()).map(AssetType::getExtension).toList();

}
