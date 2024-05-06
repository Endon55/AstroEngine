package com.anthonycosenza.engine.space;

import com.anthonycosenza.engine.util.Constants;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.system.MemoryUtil;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetFramebufferSize;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

public class Window
{
    private long windowHandle;
    private int width;
    private int height;
    
    public Window(String windowTitle, int width, int height, boolean vSync)
    {
        //Clears any previous instances of GLFW initializes
        if(!glfwInit())
        {
            throw new RuntimeException("Failed to initialize GLFW");
        }
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, Constants.OPENGL_MAJOR_VERSION);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, Constants.OPENGL_MINOR_VERSION);
    
        //Initializes all window hints to their default values.
        glfwDefaultWindowHints();
        //For all window options - https://javadoc.lwjgl.org/org/lwjgl/glfw/GLFW.html#enum-values--heading17
    
        //Sets window resizable mode
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        
        windowHandle = glfwCreateWindow(width, height, windowTitle, MemoryUtil.NULL, MemoryUtil.NULL);
    
        glfwMakeContextCurrent(windowHandle);
    
        if(windowHandle == MemoryUtil.NULL)
        {
            throw new RuntimeException("Failed to create window handle.");
        }
        //Add an error callback that prints to the system error stream.
        GLFWErrorCallback.createPrint(System.err).set();
        
        glfwShowWindow(windowHandle);
        
        //We set our preferred width and height when we initialized the window, now we're getting the "actual" width and height that GLFW was able to set
        int[] arrWidth = new int[1];
        int[] arrHeight = new int[1];
        glfwGetFramebufferSize(windowHandle, arrWidth, arrHeight);
        this.width = arrWidth[0];
        this.height = arrHeight[0];
    }
    
    
    
    public long getWindowHandle()
    {
        return windowHandle;
    }
    
    public boolean shouldClose()
    {
        return glfwWindowShouldClose(windowHandle);
    }
    
    public int getWidth()
    {
       return width;
    }
    
    public int getHeight()
    {
        return height;
    }
    
    public void update()
    {
    
    }
    
    public void cleanup()
    {
        //Unload all callback functions
        glfwFreeCallbacks(windowHandle);
        //Close the window
        glfwDestroyWindow(windowHandle);
        //Un-initalize GLFW
        glfwTerminate();
        GLFWErrorCallback callback = glfwSetErrorCallback(null);
        if(callback != null) callback.free();
    }
}
