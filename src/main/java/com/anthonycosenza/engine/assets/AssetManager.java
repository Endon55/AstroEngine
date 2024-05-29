package com.anthonycosenza.engine.assets;

import com.anthonycosenza.engine.util.math.EngineMath;

import java.util.ArrayList;
import java.util.List;

public class AssetManager
{
    List<AssetType> assetTypes;
    List<Long> assetHandles;
    List<Asset> assets;
    
    public AssetManager()//Path to asset map
    {
        assetTypes = new ArrayList<>();
        assetHandles = new ArrayList<>();
        assets = new ArrayList<>();
    }
    
    private <T> Asset loadAsset(long assetHandle)
    {
        return loadAsset(getAssetIndex(assetHandle));
    }
    private Asset loadAsset(int index)
    {
       return null;
    }
    
    private <T> int getAssetIndex(long assetHandle)
    {
        int index = assetHandles.indexOf(assetHandle);
        if(index == -1)
        {
            throw new RuntimeException("Cant find asset.");
        }
        return index;
    }
    
/*    public Asset getAsset(long assetHandle)
    {
        int index = getAssetIndex(assetHandle);
        Asset asset = assets.get(index);
        if(asset == null)
        {
            //load asset
            asset = loadAsset(index);
        }
        
        return asset;
    }*/
    /*public Model getModel(long assetHandle)
    {
        return (Model) getAsset(assetHandle);
    }*/
    public static long generateResourceID()
    {
        return EngineMath.generateMaxLengthLong();
    }
    public static Asset getAsset(long resourceID)
    {
        //reads asset directory, finds asset matching resourceID.
        //calls asset.load();
        //returns asset.
        return null;
    }
}
