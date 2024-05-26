package com.anthonycosenza.editor.scene;

import com.electronwill.nightconfig.core.file.FileConfig;

import java.util.HashMap;
import java.util.Map;

public class SceneLoader
{
    private Map<String, FileConfig> openScenes;
    
    
    public SceneLoader()
    {
        openScenes = new HashMap<>();
    }
    
    public void loadScene(String scenePath)
    {
        FileConfig config = FileConfig.builder(scenePath).autosave().build();
        openScenes.put(scenePath, config);
    }
    
    public void put(String scenePath, String key, Object value)
    {
        if(!openScenes.containsKey(scenePath))
        {
            loadScene(scenePath);
        }
        FileConfig config = openScenes.get(scenePath);
        config.set(key, value);
    }
    
    public void close()
    {
        //Since each of FileConfigs are threaded, we give each thread the save command.
        for(Map.Entry<String, FileConfig> entry: openScenes.entrySet())
        {
            entry.getValue().save();
        }
        //Then wait until they each finish.
        for(Map.Entry<String, FileConfig> entry : openScenes.entrySet())
        {
            entry.getValue().close();
        }
    }
    
}
