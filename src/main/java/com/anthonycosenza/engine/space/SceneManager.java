package com.anthonycosenza.engine.space;

import com.anthonycosenza.engine.space.node.Node;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class SceneManager
{
    private static int sceneIndex = 0;
    private static Node currentScene = null;
    private static Camera camera = null;
    private static boolean initialized = false;
    private static boolean isModified = false;
    
    private static Map<Long, Node> sceneMap = new HashMap<>();
    
    public static void setSceneIndex(int sceneIndex)
    {
        SceneManager.sceneIndex = sceneIndex;
    }
    
    public static Node getScene()
    {
        if(currentScene == null)
        {
            throw new RuntimeException("Scene doesn't exist.");
        }
        if(!initialized)
        {
            Stack<Node> nodes = new Stack<>();
            nodes.add(currentScene);
            while(!nodes.isEmpty())
            {
                Node node = nodes.pop();
                node.initialize();
    
                nodes.addAll(node.children);
            }
            initialized = true;
        }
        
        return currentScene;
    }
    public static void setScene(Node scene)
    {
        SceneManager.currentScene = scene;
        camera = null;
        initialized = false;
        getCamera();
    }
    
    public static void update(float delta)
    {
        Node scene = getScene();
        scene.update(delta);
        scene.updateChildren(delta);
    }
    
    public static void updatePhysics(float delta)
    {
        Node scene = getScene();
        scene.updatePhysics(delta);
        scene.updateChildrenPhysics(delta);
    }
    
    public static void updateUI(float delta)
    {
        Node scene = getScene();
        scene.updateUI(delta);
        scene.updateChildrenUI(delta);
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
                nodes.addAll(node.children);
            }
            camera = Camera.DEFAULT_CAMERA;
        }
        return camera;
    }
    
}
