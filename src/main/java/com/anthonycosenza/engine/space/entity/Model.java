package com.anthonycosenza.engine.space.entity;


import com.anthonycosenza.engine.assets.Asset;
import com.anthonycosenza.engine.assets.AssetManager;
import com.anthonycosenza.engine.space.rendering.materials.StandardMaterial;
import com.anthonycosenza.engine.space.rendering.materials.Texture;

import java.util.List;

public class Model implements Asset
{
    private long resourceID = -1;
    
    private List<StandardMaterial> materials;
    private Texture texture;
    
    public Model(List<StandardMaterial> materials)
    {
        this.materials = materials;
        this.texture = null;
    }
    
    public List<StandardMaterial> getMaterials()
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
    
    @Override
    public void setResourceID(long resourceID)
    {
        this.resourceID = resourceID;
    }
}
