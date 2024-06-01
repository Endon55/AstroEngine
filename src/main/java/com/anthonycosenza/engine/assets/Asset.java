package com.anthonycosenza.engine.assets;

import com.anthonycosenza.engine.annotations.Property;

public interface Asset
{
    @Property
    long getResourceID();
    void setResourceID(long resourceID);
    
    /*
    private long resourceID = -1;
    public Asset() {}
    
    
    public long getResourceID()
    {
        if(resourceID == -1)
        {
            resourceID = AssetManager.generateResourceID();
        }
        return resourceID;
    }
    */
}
