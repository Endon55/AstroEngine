package com.anthonycosenza.engine.space.entity;


import com.anthonycosenza.engine.assets.Asset;
import com.anthonycosenza.engine.assets.AssetManager;
import com.anthonycosenza.engine.space.entity.texture.Material;
import com.anthonycosenza.engine.space.entity.texture.Texture;

import java.util.List;

public class Model implements Asset
{
    private long resourceID = -1;
    
    private List<Material> materials;
    private Texture texture;
    
    public Model(List<Material> materials)
    {
        this.materials = materials;
        this.texture = null;
    }
    
    public List<Material> getMaterials()
    {
        return materials;
    }
    
    public Texture getTexture()
    {
        return texture;
    }
    
    public Entity createEntity()
    {
        return new Entity(this);
    }
    
    public long getResourceID()
    {
        if(resourceID == -1)
        {
            resourceID = AssetManager.getInstance().generateResourceID();
        }
        return resourceID;
    }
}
