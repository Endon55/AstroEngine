package com.anthonycosenza.engine.input;

import com.anthonycosenza.engine.events.KeyEvent;
import com.anthonycosenza.engine.util.math.vector.Vector2;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwSetCursorEnterCallback;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;

public class Input
{
    private final Map<Key, KeyAction> keys;
    private Vector2 lastMousePosition;
    private Vector2 currentMousePosition;
    
    private boolean mouseInWindow;
    private boolean cursorStale;
    private long windowID;
    
    
    public Input(long windowID)
    {
        this.windowID = windowID;
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
        
        setMouseType(MouseType.DISABLED);
        EventBus.getDefault().register(this);
        resetMouse();
    }
    
    private void resetMouse()
    {
        double[] x = new double[1];
        double[] y = new double[1];
        glfwGetCursorPos(windowID, x, y);
        currentMousePosition.set((float) x[0], (float) y[0]);
        lastMousePosition.set((float) x[0], (float) y[0]);
    }
    public void setMouseType(MouseType mouseType)
    {
        glfwSetInputMode(windowID, GLFW_CURSOR, mouseType.getType());
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
        if(key == GLFW_KEY_ESCAPE)
        {
            glfwSetWindowShouldClose(windowID, true);
            return;
        }
        
        for(Map.Entry<Key, KeyAction> keyEntry : keys.entrySet())
        {
            if(keyEntry.getKey().getGlfwKey() == key)
            {
                KeyAction keyAction = (action == GLFW_PRESS ? KeyAction.PRESSED : action == GLFW_RELEASE ? KeyAction.RELEASED : KeyAction.REPEAT);
                keyEntry.setValue(keyAction);
                EventBus.getDefault().post(new KeyEvent(keyEntry.getKey(), keyAction));
            }
        }
    }
    
    
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onKey(KeyEvent event)
    {
        Key key = event.key;
        KeyAction keyAction = event.keyAction;
        
        if(key == Key.ALT)
        {
            if(keyAction == KeyAction.PRESSED)
            {
                setMouseType(MouseType.NORMAL);
            }
            else if(keyAction == KeyAction.RELEASED)
            {
                setMouseType(MouseType.DISABLED);
                resetMouse();
            }
        }
        else if(key == Key.ESCAPE && keyAction == KeyAction.PRESSED)
        {
            glfwSetWindowShouldClose(windowID, true);
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