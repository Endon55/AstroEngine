package com.anthonycosenza.engine.space.entity;


import com.anthonycosenza.engine.assets.Asset;
import com.anthonycosenza.engine.assets.AssetManager;
import com.anthonycosenza.engine.space.rendering.materials.Material;
import com.anthonycosenza.engine.space.rendering.materials.texture.Texture;

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
