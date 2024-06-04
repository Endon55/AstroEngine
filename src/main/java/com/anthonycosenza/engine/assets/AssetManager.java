package com.anthonycosenza.engine.assets;

import com.anthonycosenza.editor.EditorIO;
import com.anthonycosenza.engine.space.ModelLoader;
import com.anthonycosenza.engine.space.node.Scene;
import com.anthonycosenza.engine.space.rendering.shader.ShaderData;
import com.anthonycosenza.engine.space.rendering.shader.ShaderPipeline;
import com.anthonycosenza.engine.util.FileUtils;
import com.anthonycosenza.engine.util.Toml;
import com.anthonycosenza.engine.util.math.EngineMath;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;

public class AssetManager
{
    private static AssetManager INSTANCE;
    public static final long DEFAULT_SHADER = 10000000;
    private final boolean runtime;
    private final File assetRoot;
    private final Map<Long, AssetInfo> assetInfoMap;
    private final Map<Long, Asset> assetMap;
    
    private AssetManager(boolean runtime, File assetRoot)//Path to asset map
    {
        this.runtime = runtime;
        this.assetRoot = assetRoot;
        assetInfoMap = new HashMap<>();
        assetMap = new HashMap<>();
        assetInfoMap.put(DEFAULT_SHADER, new AssetInfo(DEFAULT_SHADER, AssetType.SHADER, "engine"));
        
        ShaderPipeline defaultPipeline = new ShaderPipeline(new ShaderData("AstroEngine/resources/shaders/scene.vert", GL_VERTEX_SHADER),
                new ShaderData("AstroEngine/resources/shaders/scene.frag", GL_FRAGMENT_SHADER));
        defaultPipeline.setResourceID(DEFAULT_SHADER);
        assetMap.put(DEFAULT_SHADER, defaultPipeline);
        
        updateAssets();
    }

    /* ------------------------------------------
    
            Asset Loading from Disc
    
    ------------------------------------------ */
    
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
                
                if(AssetType.FILE_EXTENSIONS.contains(extension))
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
    
    /*
     * Creates an Asset Handle file for an asset located somewhere on disc.
     */
    public void importAsset(File assetPath)
    {
        if(!assetPath.exists()) throw new RuntimeException("Couldn't find asset: " + assetPath.getAbsolutePath());
        
        AssetInfo info = new AssetInfo(generateResourceID(), getAssetType(assetPath), assetPath.getPath());
        String name = assetPath.getName().split("\\.")[0];
        File projectPath = new File(EditorIO.getProjectDirectory().getPath() + "\\" + name + ".a" + info.assetType().toString().toLowerCase());
        new Toml.builder().assetHeader(info).build(projectPath);
        assetInfoMap.put(info.assetID(), info);
    }
    
    
    /* ------------------------------------------
    
            Asset Instance Creation
    
    ------------------------------------------ */

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
    
    private Asset loadAsset(long assetID)
    {
        AssetInfo info = assetInfoMap.get(assetID);
        
        if(info == null)
        {
            System.out.println(assetInfoMap);
            throw new RuntimeException("Where the fuck do I even find this thing?: " + assetID);
        }
        Asset asset = switch(info.assetType())
                {
                    case MODEL -> ModelLoader.loadModel(info.filePath());
                    case MESH, TEXTURE, MATERIAL, SHADER -> throw new RuntimeException("Implement: " + info.assetType());
                    case SCENE -> throw new RuntimeException("Shouldn't be trying to load scenes like this.");
                };
        asset.setResourceID(info.assetID());
        assetMap.put(info.assetID(), asset);
        return asset;
    }
    
    public Asset createNewAsset(File directory, String filename, AssetType assetType)
    {
        if(!assetType.hasFunction()) throw new RuntimeException("Cannot create assets of type: " + assetType);
        Asset asset = assetType.create();
        
        long resourceID = generateResourceID();
        asset.setResourceID(resourceID);
        String filepath = directory.getPath() + "\\" + filename + "." + assetType.getExtension();
        AssetInfo info = new AssetInfo(resourceID, assetType, filepath);
    
        assetInfoMap.put(resourceID, info);
        
        if(asset instanceof Scene scene)
        {
            scene.name = filename;
            Toml.updateScene(scene, filepath);
        }
        else
        {
            Toml.updateAsset(info, asset, filepath);
        }
        return asset;
    }
    /* ------------------------------------------
    
                Shader Creation
    
    ------------------------------------------ */
    
    public ShaderPipeline getShaderDefault()
    {
        return getShader(DEFAULT_SHADER);
    }
    
    public ShaderPipeline getShader(long assetID)
    {
        return (ShaderPipeline) getAsset(assetID);
    }
    
    /* ------------------------------------------
    
                Scenes Creation
    
    ------------------------------------------ */
    
    public Scene createSceneAsset(File directory, String filename)
    {
        Scene scene = new Scene();
        scene.name = filename;
        scene.getResourceID();
        String filepath = directory.getPath() + "\\" + filename + AssetType.SCENE.getExtension();
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
    
    /*
     * This is how we instantiate the AssetManager.
     */
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
        else if(extension.equals("ascene"))
        {
            return AssetType.SCENE;
        }
        else throw new RuntimeException("Don't know what kind of asset this is: " + asset.getName());
    }
}
