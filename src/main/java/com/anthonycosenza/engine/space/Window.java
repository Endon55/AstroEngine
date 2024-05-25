package com.anthonycosenza.engine.space;

import com.anthonycosenza.engine.util.Constants;
import com.anthonycosenza.engine.util.math.vector.Vector2i;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
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
import static org.lwjgl.glfw.GLFW.glfwGetMonitorPos;
import static org.lwjgl.glfw.GLFW.glfwGetMonitors;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPosCallback;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

public class Window
{
    private final long windowHandle;
    private int width;
    private int height;
    private Vector2i windowPosition;
    
    public Window(String windowTitle, ProjectSettings settings)
    {
        windowPosition = new Vector2i(0, 0);
        int[] arrWidth = new int[1];
        int[] arrHeight = new int[1];
        
        //Clears any previous instances of GLFW initializes
        if(!glfwInit())
        {
            throw new RuntimeException("Failed to initialize GLFW");
        }
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, Constants.OPENGL_MAJOR_VERSION);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, Constants.OPENGL_MINOR_VERSION);
    
        //Get the x,y coordiantes for the specific monitor chosen
        PointerBuffer monitors = glfwGetMonitors();
        GLFWVidMode videoMode = glfwGetVideoMode(monitors.get(settings.monitor));
        glfwGetMonitorPos(monitors.get(settings.monitor), arrWidth, arrHeight);
        int monitorX = arrWidth[0];
        int monitorY = arrHeight[0];
        
        //Initializes all window hints to their default values.
        glfwDefaultWindowHints();
        //For all window options - https://javadoc.lwjgl.org/org/lwjgl/glfw/GLFW.html#enum-values--heading17
    
        //Sets window resizable mode
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        
        windowHandle = glfwCreateWindow(settings.width, settings.height, windowTitle, MemoryUtil.NULL, MemoryUtil.NULL);
    
        glfwMakeContextCurrent(windowHandle);
        
        if(windowHandle == MemoryUtil.NULL)
        {
            throw new RuntimeException("Failed to create window handle.");
        }
        //Add an error callback that prints to the system error stream.
        GLFWErrorCallback.createPrint(System.err).set();
    
        glfwShowWindow(windowHandle);
        
        
        
        //We set our preferred width and height when we initialized the window, now we're getting the "actual" width and height that GLFW was able to set
        glfwGetFramebufferSize(windowHandle, arrWidth, arrHeight);
        this.width = arrWidth[0];
        this.height = arrHeight[0];
        
        settings.width = width;
        settings.height = height;
        windowPosition.set(monitorX + (videoMode.width() - this.width) / 2, monitorY + (videoMode.height() - this.height) / 2);
        //Once everything is initialized properly, we center the window.
        glfwSetWindowPos(windowHandle, windowPosition.x(), windowPosition.y());
        glfwSetWindowPosCallback(windowHandle, this::windowPosCallback);
    }
    
    private void windowPosCallback(long handle, int posX, int posY)
    {
        windowPosition.set(posX, posY);
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
    
    public int getPosX()
    {
        return windowPosition.x();
    }
    
    public int getPosY()
    {
        return windowPosition.y();
    }
    
    public int getLeftEdge()
    {
        return getPosX();
    }
    
    public int getRightEdge()
    {
        return getPosX() + getWidth();
    }
    
    public int getTopEdge()
    {
        return getPosY();
    }
    
    public int getBottomEdge()
    {
        return getPosY() + getHeight();
    }
    
    
    public void resize(int width, int height)
    {
        this.width = width;
        this.height = height;
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
