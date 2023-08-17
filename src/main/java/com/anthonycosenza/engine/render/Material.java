package com.anthonycosenza.engine.render;

import java.util.ArrayList;
import java.util.List;

public class Material
{
    private List<Mesh> meshList;
    private String texturePath;
    
    public Material()
    {
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
    
    public void setTextureDefault()
    {
        this.texturePath = TextureCache.DEFAULT_TEXTURE;
    }
    
    public void cleanup()
    {
        meshList.forEach(Mesh::cleanup);
    }
    
    
}
