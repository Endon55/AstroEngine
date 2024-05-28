package com.anthonycosenza.engine.space.entity.texture;

import com.anthonycosenza.engine.assets.Asset;
import com.anthonycosenza.engine.space.entity.Mesh;
import com.anthonycosenza.engine.util.math.Color;

import java.util.ArrayList;
import java.util.List;

public class Material extends Asset
{
    public static final Color DEFAULT_COLOR = new Color(0f, 0f, 0f, 1f);
    
    private Color diffuseColor;
    private Texture texture;
    List<Mesh> meshes;
    
    
    public Material()
    {
        diffuseColor = DEFAULT_COLOR;
        meshes = new ArrayList<>();
    }
    
    public List<Mesh> getMeshes()
    {
        return meshes;
    }
    
    public Color getDiffuseColor()
    {
        return diffuseColor;
    }
    
    public void setDiffuseColor(Color diffuseColor)
    {
        this.diffuseColor = diffuseColor;
    }
    
    public Texture getTexture()
    {
        return texture;
    }
    
    public void setTexture(String texturePath)
    {
        this.texture = new Texture(texturePath);
    }
    
}
