package com.anthonycosenza.input;

import com.anthonycosenza.events.MessageEvent;
import org.greenrobot.eventbus.EventBus;
import org.lwjgl.glfw.Callbacks;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;

public class Input
{
    private final Map<Key, KeyAction> keys;
    
    
    public Input(long windowID)
    {
        glfwSetKeyCallback(windowID, this::keyCallback);
        keys = new HashMap<>();
    
        for(Key key : Key.values())
        {
            keys.put(key, KeyAction.RELEASED);
        }
        
    }
    
    private void keyCallback(long handle, int key, int scancode, int action, int mods)
    {
        for(Map.Entry<Key, KeyAction> keyEntry : keys.entrySet())
        {
            if(keyEntry.getKey().getGlfwKey() == key)
            {
                KeyAction keyAction = (action == GLFW_PRESS ? KeyAction.PRESSED : action == GLFW_RELEASE ? KeyAction.RELEASED : KeyAction.REPEAT);
                keyEntry.setValue(keyAction);
                EventBus.getDefault().post(new MessageEvent(keyEntry.getKey() + " Key " + keyAction));
            }
        }
    }
    
    public KeyAction getState(Key key)
    {
        return keys.get(key);
    }
    
}
