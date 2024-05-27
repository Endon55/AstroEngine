package com.anthonycosenza.engine.assets;

public class Asset
{
    public long resourceID = -1;
    public Asset() {}
    
    
    public long getResourceID()
    {
        if(resourceID == -1)
        {
            resourceID = AssetManager.generateResourceID();
        }
        return resourceID;
    }
}
