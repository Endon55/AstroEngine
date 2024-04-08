package com.anthonycosenza.engine.window;

import com.anthonycosenza.engine.MouseInput;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryUtil;

import java.util.concurrent.Callable;

import static java.awt.SystemColor.window;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_MAXIMIZED;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_SAMPLES;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetFramebufferSize;
import static org.lwjgl.glfw.GLFW.glfwGetKey;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.glfw.GLFW.nglfwSetFramebufferSizeCallback;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window
{
    private long windowHandle;
    int width;
    int height;
    private MouseInput mouseInput;
    private Callable<Void> resizeFunc;
    
    
    public Window(String title, WindowOptions options, Callable<Void> resizeFunc)
    {
        this.resizeFunc = resizeFunc;
        //Initialize GLFW, most GLFW functions won't work before doing this
        if(!glfwInit())
        {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
        
        //Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
    
        if(options.antiAliasing)
        {
            glfwWindowHint(GLFW_SAMPLES, 4);
        }
        
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
    
    
        if(options.width > 0 && options.height > 0)
        {
            this.width = options.width;
            this.height = options.height;
        }
        else
        {
            glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
            GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            width = vidMode.width();
            height = vidMode.height();
        }
        
        //Create the window
        windowHandle = glfwCreateWindow(width, height, title, NULL, NULL);
        if(windowHandle == NULL)
        {
            throw new RuntimeException("Failed to create GLFW window");
        }
        
        glfwSetFramebufferSizeCallback(windowHandle, (window, width, height) -> resized(width, height));
    

        glfwSetErrorCallback((int errorCode, long msgPtr) ->
                System.out.println("Error code: " + errorCode + ", Message: " + MemoryUtil.memUTF8(msgPtr)));
        
        //Setup a key callback. Will be called anytime a key is pressed, repeated, or released.
        glfwSetKeyCallback(windowHandle, (window, key, scancode, action, mods) ->
                keyCallBack(key, action));
        
        //Make the OpenGL context current
        glfwMakeContextCurrent(windowHandle);
        
        //Setting vSync
        if(options.fps > 0)
        {
            glfwSwapInterval(0);
        }
        else
        {
            glfwSwapInterval(1);
        }
    
        //Make the window visible
        glfwShowWindow(windowHandle);
        
        int[] arrWidth = new int[1];
        int[] arrHeight = new int[1];
        glfwGetFramebufferSize(windowHandle, arrWidth, arrHeight);
        width = arrWidth[0];
        height = arrHeight[0];
        
        mouseInput = new MouseInput(windowHandle);
        
    }

    protected void resized(int width, int height)
    {
        this.width = width;
        this.height = height;
    
        try
        {
            resizeFunc.call();
        } catch(Exception e)
        {
            throw new RuntimeException("Error calling resize callback", e);
        }
    }
    
    
    
    public boolean isKeyPressed(int keyCode)
    {
        return glfwGetKey(windowHandle, keyCode) == GLFW_PRESS;
    }
    
    public void keyCallBack(int key, int action)
    {
        if(key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
        {
            glfwSetWindowShouldClose(windowHandle, true);
        }
    }
    
    public void cleanup()
    {
        glfwFreeCallbacks(windowHandle);
        glfwDestroyWindow(windowHandle);
        glfwTerminate();
        GLFWErrorCallback callback = glfwSetErrorCallback(null);
        if(callback != null) callback.free();
    }

    public void update()
    {
        glfwSwapBuffers(windowHandle);
    }
    
    public MouseInput getMouseInput()
    {
        return mouseInput;
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
    
    public void pollEvents()
    {
        glfwPollEvents();
    }
    
}
