package com.anthonycosenza.engine.input;

import com.anthonycosenza.engine.Engine;
import com.anthonycosenza.engine.events.KeyEvent;
import com.anthonycosenza.engine.space.ProjectSettings;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.joml.Vector2f;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_2;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_MIDDLE;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwSetCharCallback;
import static org.lwjgl.glfw.GLFW.glfwSetCursorEnterCallback;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;

public class Input
{
    private static Input instance;
    private final Map<Key, KeyAction> keys;
    private Vector2f lastMousePosition;
    private Vector2f currentMousePosition;
    
    private boolean mouseInWindow;
    private boolean leftMouseButtonPressed;
    private boolean rightMouseButtonPressed;
    private boolean middleMouseButtonPressed;
    private boolean cursorStale;
    private long windowID;
    private MouseType mouseType;
    private int scrollPosition;
    private final ProjectSettings settings;
    
    
    public Input(long windowID, ProjectSettings settings)
    {
        this.windowID = windowID;
        this.settings = settings;
        glfwSetKeyCallback(windowID, this::keyCallback);
        glfwSetMouseButtonCallback(windowID, this::mouseButtonCallback);
        glfwSetCursorPosCallback(windowID, this::mouseCursorCallback);
        glfwSetCursorEnterCallback(windowID, this::mouseEnterCallback);
        glfwSetCharCallback(windowID, this::characterCallback);
        glfwSetScrollCallback(windowID, this::scrollCallback);
        keys = new HashMap<>();
    
        for(Key key : Key.values())
        {
            keys.put(key, KeyAction.RELEASED);
        }
        
        currentMousePosition = new Vector2f();
        lastMousePosition = new Vector2f();
        
        mouseInWindow = true;
        cursorStale = false;
        scrollPosition = 0;
        if(settings.lockMouse)
        {
            setMouseType(MouseType.DISABLED);
        }
        EventBus.getDefault().register(this);
        resetMouse();
    }

    
    
    public boolean isMouseLocked()
    {
        return mouseType == MouseType.DISABLED;
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
        this.mouseType = mouseType;
        glfwSetInputMode(windowID, GLFW_CURSOR, mouseType.getType());
    }
    
    public Vector2f getMousePosition()
    {
        return new Vector2f(currentMousePosition);
    }
    
    public int getScrollPosition()
    {
        return scrollPosition;
    }
    
    public KeyAction getState(Key key)
    {
        return keys.get(key);
    }
    
    public boolean isPressed(Key key)
    {
        KeyAction action = getState(key);
        return action == KeyAction.PRESSED || action == KeyAction.REPEAT;
    }
    
    public boolean isLeftMouseButtonPressed()
    {
        return leftMouseButtonPressed;
    }
    
    public boolean isRightMouseButtonPressed()
    {
        return rightMouseButtonPressed;
    }
    
    public boolean isMiddleMouseButtonPressed()
    {
        return middleMouseButtonPressed;
    }
    
    
    public Vector2f getMouseDirection()
    {
        return currentMousePosition.sub(lastMousePosition, new Vector2f());
    }
    
    public boolean isCursorStale()
    {
        return cursorStale;
    }
    
    public void resetFrame()
    {
        cursorStale = true;
        scrollPosition = 0;
    }
    
    private void keyCallback(long handle, int key, int scancode, int action, int mods)
    {
        /*if(key == GLFW_KEY_ESCAPE)
        {
            glfwSetWindowShouldClose(windowID, true);
            return;
        }*/
/*        ImGuiIO io = ImGui.getIO();
        //Im gui wants to consume an input
        if(io.getWantCaptureKeyboard())
        {
            if(action == GLFW_PRESS)
            {
                io.setKeysDown(key, true);
            }
            else if(action == GLFW_RELEASE)
            {
                io.setKeysDown(key, false);
            }
    
            io.setKeyCtrl(io.getKeysDown(GLFW_KEY_LEFT_CONTROL) || io.getKeysDown(GLFW_KEY_RIGHT_CONTROL));
            io.setKeyShift(io.getKeysDown(GLFW_KEY_LEFT_SHIFT) || io.getKeysDown(GLFW_KEY_RIGHT_SHIFT));
            io.setKeyAlt(io.getKeysDown(GLFW_KEY_LEFT_ALT) || io.getKeysDown(GLFW_KEY_RIGHT_ALT));
            io.setKeySuper(io.getKeysDown(GLFW_KEY_LEFT_SUPER) || io.getKeysDown(GLFW_KEY_RIGHT_SUPER));
    
        }*/

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
    
    private void characterCallback(long handle, int key)
    {
/*        ImGuiIO io = ImGui.getIO();
        
        if(io.getWantCaptureKeyboard())
        {
            io.addInputCharacter(key);
        }*/
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
        /*else if(key == Key.ESCAPE && keyAction == KeyAction.PRESSED)
        {
            glfwSetWindowShouldClose(windowID, true);
        }*/
    }
    
    private void mouseButtonCallback(long handle, int button, int action, int mods)
    {
        if(button == GLFW_MOUSE_BUTTON_1)
        {
            leftMouseButtonPressed = (action == GLFW_PRESS);
        }
        else if(button == GLFW_MOUSE_BUTTON_2)
        {
            rightMouseButtonPressed = (action == GLFW_PRESS);
        }
        else if(button == GLFW_MOUSE_BUTTON_MIDDLE)
        {
            middleMouseButtonPressed = (action == GLFW_PRESS);
        }
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
    
    /*
     * xOffset doesn't appear to do anything, it might be pushing the mouse wheel left or right as a button, idk.
     */
    private void scrollCallback(long handle, double xOffset, double yOffset)
    {
        scrollPosition += (int) yOffset;
        //System.out.println("xOffset: " + xOffset);
        //System.out.println("yOffset: " + yOffset);
    }
    
    public static Input getInstance()
    {
        return Engine.INPUT;
    }
}
