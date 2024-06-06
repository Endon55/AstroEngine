package com.anthonycosenza.engine.space.entity;

import com.anthonycosenza.engine.assets.Asset;
import com.anthonycosenza.engine.assets.AssetManager;

public abstract class GeneratedMesh extends Mesh implements Asset
{
    public long resourceID = -1;
    

    @Override
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
    
    /*public void pyramid3(float height, float baseEdgeLength)
    {
        float hHeight = height * .5f;
        float hBEL = baseEdgeLength * .5f;
        float width = (float) Math.sqrt(baseEdgeLength * baseEdgeLength - hBEL * hBEL);
        float hWidth = width * .5f;
        
*//*        return new float[]{
                //Top Point
                0, 0, hHeight,
                //Bottom Left
                -hBEL, -hWidth, -hHeight,
                //Bottom Right
                hBEL, -hWidth, -hHeight,
                //Bottom Top
                0, -hWidth, hHeight
        };*//*
        return new float[]{
                //Top Point
                0, hHeight, 0,
                //Bottom Left
                -hBEL, -hHeight, -hWidth,
                //Bottom Right
                hBEL, -hHeight, -hWidth,
                //Bottom Top
                0, -hHeight, hWidth
        };
    }*/
}
