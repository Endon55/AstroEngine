package com.anthonycosenza.engine.space;

import com.anthonycosenza.engine.space.node.Camera;
import com.anthonycosenza.engine.space.node.Node;
import com.anthonycosenza.engine.space.node.Scene;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class SceneManager
{
    private static int sceneIndex = 0;
    private static Scene currentScene = null;
    private static Camera camera = null;
    
    private static Map<Long, Node> sceneMap = new HashMap<>();
    
    public static void setSceneIndex(int sceneIndex)
    {
        SceneManager.sceneIndex = sceneIndex;
    }

    public static Scene getScene()
    {
        if(currentScene == null)
        {
            throw new RuntimeException("Scene doesn't exist.");
        }
        
        return currentScene;
    }
    public static void setScene(Scene scene)
    {
        SceneManager.currentScene = scene;
        camera = null;
        getCamera();
    }

    public static void update(float delta)
    {
        getScene().update(delta);
    }
    
    public static void updatePhysics(float delta)
    {
        getScene().updatePhysics(delta);
    }
    
    public static void updateUI(float delta)
    {
        getScene().updateUI(delta);
    }
    
    public static Camera getCamera()
    {
        if(camera == null)
        {
            Stack<Node> nodes = new Stack<>();
            nodes.add(getScene());
            while(!nodes.isEmpty())
            {
                Node node = nodes.pop();
                if(node instanceof Camera)
                {
                    camera = (Camera) node;
                    return camera;
                }
                nodes.addAll(node.getChildren());
            }
            camera = Camera.DEFAULT_CAMERA;
        }
        return camera;
    }
}
