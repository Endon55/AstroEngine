package com.anthonycosenza.engine.loader;

import com.anthonycosenza.engine.space.rendering.Scene;

public class SceneManager
{
    private static int sceneIndex = 0;
    private static boolean isLoaded = false;
    private static Scene cachedScene = null;
    
    
    public static void setSceneIndex(int sceneIndex)
    {
        SceneManager.sceneIndex = sceneIndex;
    }
    public static Scene getCurrentScene()
    {
        if(cachedScene == null || !isLoaded)
        {
            loadScene();
        }
        return cachedScene;
    }
    private static void loadScene()
    {
        /*
         * Generate all the entities and assemble the scene.
         */
        //isLoaded = true;
    }
}
