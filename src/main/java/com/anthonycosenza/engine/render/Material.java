package com.anthonycosenza.engine.render;

import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class Material
{
    public static final Vector4f DEFAULT_COLOR = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);
    private Vector4f diffuseColor;
    private List<Mesh> meshList;
    private String texturePath;
    
    public Material()
    {
        diffuseColor = DEFAULT_COLOR;
        meshList = new ArrayList<>();
    }
    
    public List<Mesh> getMeshList()
    {
        return meshList;
    }
    
    public String getTexturePath()
    {
        return texturePath;
    }
    
    public void setTexturePath(String texturePath)
    {
        this.texturePath = texturePath;
    }
    
    public Vector4f getDiffuseColor()
    {
        return diffuseColor;
    }
    
    public void setDiffuseColor(Vector4f diffuseColor)
    {
        this.diffuseColor = diffuseColor;
    }
    
    
    
    public void setTextureDefault()
    {
        this.texturePath = TextureCache.DEFAULT_TEXTURE;
    }

    public void cleanup()
    {
        meshList.forEach(Mesh::cleanup);
    }
 
}
