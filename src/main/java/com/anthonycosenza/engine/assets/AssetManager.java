package com.anthonycosenza.engine.assets;

import com.anthonycosenza.editor.EditorIO;
import com.anthonycosenza.editor.logger.EditorLogger;
import com.anthonycosenza.editor.scripts.ScriptCompiler;
import com.anthonycosenza.engine.space.ModelLoader;
import com.anthonycosenza.engine.space.node.Scene;
import com.anthonycosenza.engine.space.rendering.shader.FragmentShader;
import com.anthonycosenza.engine.space.rendering.shader.ShaderPipeline;
import com.anthonycosenza.engine.space.rendering.shader.VertexShader;
import com.anthonycosenza.engine.util.FileUtils;
import com.anthonycosenza.engine.util.Toml;
import com.anthonycosenza.engine.util.math.EngineMath;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class AssetManager
{
    private static AssetManager INSTANCE;
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
                    if(info != null)
                    {
                        long id = info.assetID();
                        if(id == -1)
                        {
                            id = generateResourceID();
                            Toml.updateAssetHeader(id, info.assetType(), info.filePath(), file);
                        }
                        assetInfoMap.put(id, info);
                    }
                    else EditorLogger.error("File doesn't have an asset header: " + file);
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
    
    public Asset getAsset(File file)
    {
        AssetInfo info = Toml.getAssetHeader(file);
        if(info == null)
        {
            EditorLogger.error("File doesn't have an asset header: " + file);
            return null;
        }
        return getAsset(info.assetID());
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
            throw new RuntimeException("Where the fuck do I even find this thing?: " + assetID);
        }
        Asset asset = switch(info.assetType())
                {
                    case MODEL -> ModelLoader.loadModel(info.filePath());
                    case MESH, TEXTURE, MATERIAL -> throw new RuntimeException("Implement: " + info.assetType());
                    case VERTEX ->  new VertexShader(info.filePath(), info.assetID());
                    case FRAGMENT -> new FragmentShader(info.filePath(), info.assetID());
                    case SCENE, SCRIPT -> throw new RuntimeException("Shouldn't be trying to load scenes like this.");
                };
        asset.setResourceID(info.assetID());
        assetMap.put(info.assetID(), asset);
        return asset;
    }
    
    public Asset createNewAsset(File directory, String filename, AssetType assetType)
    {
        if(assetType == AssetType.SCRIPT)
        {
            String path = EditorIO.getScriptsDirectory().getAbsolutePath() + "\\" + filename + ".java";
            File script = new File(path);
            try
            {
                Files.write(script.toPath(), ScriptCompiler.getDefaultScript(filename).getBytes());
            } catch(IOException e)
            {
                throw new RuntimeException(e);
            }
            return null;
        }
        
        if(!assetType.hasFunction()) throw new RuntimeException("Cannot create assets of type: " + assetType);
        
        long resourceID = generateResourceID();
        Asset asset = assetType.create();
        asset.setResourceID(resourceID);

        String projectPath = directory.getPath() + "\\" + filename + "." + assetType.getExtension();
        String rawAssetPath = switch(assetType)
                {
                    case VERTEX ->
                    {
                        String path = EditorIO.getShaderDirectory().getPath() + "\\" + filename + ".vert";
                        File shaderFile = new File(path);
                        try
                        {
                            if(shaderFile.createNewFile())
                            {
                                Files.writeString(shaderFile.toPath(), VertexShader.DEFAULT_SHADER_CODE);
                            }
                        } catch(IOException e)
                        {
                            throw new RuntimeException(e);
                        }
                        yield path;
                    }
                    case FRAGMENT ->
                    {
                        String path = EditorIO.getShaderDirectory().getPath() + "\\" + filename + ".frag";
                        File file = new File(path);
                        try
                        {
                            if(file.createNewFile())
                            {
                                Files.writeString(file.toPath(), FragmentShader.DEFAULT_SHADER_CODE);
                            }
                        } catch(IOException e)
                        {
                            throw new RuntimeException(e);
                        }
                        yield path;
                    }
                    case SCENE, TEXTURE, MESH, MODEL, MATERIAL -> projectPath;
                    case SCRIPT -> throw new RuntimeException("Shouldn't be here loading scripts");
                };
        
        AssetInfo info = new AssetInfo(resourceID, assetType, rawAssetPath);
    
        
        
        assetInfoMap.put(resourceID, info);
        
        if(asset instanceof Scene scene)
        {
            scene.setName(filename);
            Toml.updateScene(scene, projectPath);
        }
        else
        {
            Toml.updateAsset(info, asset, projectPath);
        }
        return asset;
    }
    /* ------------------------------------------
    
                Shader Creation
    
    ------------------------------------------ */
    
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
        scene.setName(filename);
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
        if(info == null)
        {
            System.out.println("Cannot instantiate a scene that doesn't exist: " + assetID);
            throw new RuntimeException();
        }
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
