package com.anthonycosenza.engine.assets;

import com.anthonycosenza.editor.EditorIO;
import com.anthonycosenza.engine.space.ModelLoader;
import com.anthonycosenza.engine.space.node.Scene;
import com.anthonycosenza.engine.util.FileUtils;
import com.anthonycosenza.engine.util.Toml;
import com.anthonycosenza.engine.util.math.EngineMath;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AssetManager
{
    private static AssetManager INSTANCE;
    private boolean runtime;
    private File assetRoot;
    private Map<Long, AssetInfo> assetInfoMap;
    private Map<Long, Asset> assetMap;
    
    private AssetManager(boolean runtime, File assetRoot)//Path to asset map
    {
        this.runtime = runtime;
        this.assetRoot = assetRoot;
        assetInfoMap = new HashMap<>();
        assetMap = new HashMap<>();
        
        updateAssets();
    }
    private Asset loadAsset(long assetID)
    {
        AssetInfo info = assetInfoMap.get(assetID);
        
        if(info == null)
        {
            throw new RuntimeException("Where the fuck do I even find this thing?: " + assetID);
        }
        Asset asset = switch(info.assetType())
        {
            case MODEL -> ModelLoader.loadModel(info.filePath());
            case MESH, TEXTURE, MATERIAL -> throw new RuntimeException("Implement: " + info.assetType());
            case SCENE -> throw new RuntimeException("Shouldn't be trying to load scenes like this.");
        };
        return asset;
    }
    public AssetInfo getAssetInfo(long assetID)
    {
        return assetInfoMap.get(assetID);
    }
    public Asset getAsset(long assetID)
    {
        Asset asset = assetMap.get(assetID);
        
        if(asset == null)
        {
            asset = loadAsset(assetID);
        }
        return asset;
    }
    
    public void updateAssets()
    {
        //Local build
        if(runtime)
        {
            updateRuntimeAssets();
        }
        else updateLocalAssets(assetRoot);
    }
    
    private void updateLocalAssets(File assetDirectory)
    {
        File[] files = assetDirectory.listFiles();
        for(File file : files)
        {
            if(file.isDirectory())
            {
                updateLocalAssets(file);
            }
            else
            {
                String extension = FileUtils.getExtension(file);
                
                if(extension.equals("aasset") || extension.equals("scene"))
                {
                    AssetInfo info = Toml.getAssetHeader(file);
                    long id = info.assetID();
                    if(id == -1)
                    {
                        id = generateResourceID();
                        Toml.updateAssetHeader(id, info.assetType(), info.filePath(), file);
                    }
                    
                    
                    assetInfoMap.put(id, info);
                }
            }
        }
    }
    
    
    private void updateRuntimeAssets()
    {
    
    }
    
    public void importAsset(File assetPath)
    {
        if(!assetPath.exists()) throw new RuntimeException("Couldn't find asset: " + assetPath.getAbsolutePath());
        
        AssetInfo info = new AssetInfo(generateResourceID(), getAssetType(assetPath), assetPath.getPath());
        String name = assetPath.getName().split("\\.")[0];
        File projectPath = new File(EditorIO.getProjectDirectory().getPath() + "\\" + name + ".a" + info.assetType().toString().toLowerCase());
        new Toml.builder().asset(info).build(projectPath);
        assetInfoMap.put(info.assetID(), info);
    }
    
    public Scene createSceneAsset(File directory, String filename)
    {
        Scene scene = new Scene();
        scene.name = filename;
        scene.getResourceID();
        String filepath = directory.getPath() + "\\" + filename + ".scene";
        assetInfoMap.put(scene.getResourceID(), new AssetInfo(scene.getResourceID(), AssetType.SCENE, filepath));
        Toml.updateScene(scene);
        return scene;
    }
    
    public Scene instantiateScene(File sceneAsset)
    {
        return Toml.getScene(Toml.getAssetHeader(sceneAsset));
    }
    public Scene instantiateScene(long assetID)
    {
        AssetInfo info = getAssetInfo(assetID);
        return Toml.getScene(info);
    }

    public long generateResourceID()
    {
        return EngineMath.generateMaxLengthLong();
    }
    

    public static void setAssetPath(boolean runtime, File assetRoot)
    {
        AssetManager.INSTANCE = new AssetManager(runtime, assetRoot);
    }
    
    public static AssetManager getInstance()
    {
        if(INSTANCE == null)
        {
            throw new RuntimeException("Asset Manager wasn't instantiated.");
        }
        return INSTANCE;
    }
    public static AssetType getAssetType(File asset)
    {
        String extension = FileUtils.getExtension(asset).toLowerCase();
        if(extension.equals("png"))
        {
            return AssetType.TEXTURE;
        }
        else if(extension.equals("fbx"))
        {
            return AssetType.MODEL;
        }
        else if(extension.equals("scene"))
        {
            return AssetType.SCENE;
        }
        else throw new RuntimeException("Don't know what kind of asset this is: " + asset.getName());
    }
}
