package com.anthonycosenza.engine.space.node;

import com.anthonycosenza.engine.assets.Asset;
import com.anthonycosenza.engine.assets.AssetManager;

public class Scene extends Node implements Asset
{
    
    @Override
    protected long createNodeID()
    {
        return AssetManager.getInstance().generateResourceID();
    }
    
    public long getResourceID()
    {
        return resourceID;
    }
}
