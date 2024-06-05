package com.anthonycosenza.engine.assets;


public interface Asset
{
    long getResourceID();
    void setResourceID(long resourceID);
    
    /*
    private long resourceID = -1;
    public Asset() {}
    
    @Override
    public long getResourceID()
    {
        if(resourceID == -1)
        {
            resourceID = AssetManager.getInstance().generateResourceID();
        }
        return resourceID;
    }
    */
}
