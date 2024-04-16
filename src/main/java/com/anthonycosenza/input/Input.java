package com.anthonycosenza.input;

import com.anthonycosenza.events.MessageEvent;
import com.anthonycosenza.math.vector.Vector2;
import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.glfwSetCursorEnterCallback;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;

public class Input
{
    private final Map<Key, KeyAction> keys;
    private Vector2 lastMousePosition;
    private Vector2 currentMousePosition;
    
    private boolean mouseInWindow;
    private boolean cursorStale;
    
    public Input(long windowID)
    {
        glfwSetKeyCallback(windowID, this::keyCallback);
        glfwSetMouseButtonCallback(windowID, this::mouseButtonCallback);
        glfwSetCursorPosCallback(windowID, this::mouseCursorCallback);
        glfwSetCursorEnterCallback(windowID, this::mouseEnterCallback);
        keys = new HashMap<>();
    
        for(Key key : Key.values())
        {
            keys.put(key, KeyAction.RELEASED);
        }
        
        currentMousePosition = new Vector2();
        lastMousePosition = new Vector2();
        
        mouseInWindow = true;
        cursorStale = false;
    }
    
    public Vector2 getMousePosition()
    {
        return currentMousePosition;
    }
    
    public KeyAction getState(Key key)
    {
        return keys.get(key);
    }
    
    public Vector2 getMouseDirection()
    {
        return currentMousePosition.subtract(lastMousePosition, new Vector2());
    }
    
    public boolean isCursorStale()
    {
        return cursorStale;
    }
    
    public void resetFrame()
    {
        cursorStale = true;
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
    
    private void mouseButtonCallback(long handle, int button, int action, int mods)
    {
    
    }
    
    private void mouseCursorCallback(long handle, double x, double y)
    {
        lastMousePosition.set(currentMousePosition);
        currentMousePosition.set((float) x, (float) y);
        cursorStale = false;
    }
    
    private void mouseEnterCallback(long handle, boolean entered)
    {
        mouseInWindow = entered;
    }
}
