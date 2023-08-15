package com.anthonycosenza.engine.window;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryUtil;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_MAXIMIZED;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetFramebufferSize;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window
{
    private long windowHandle;
    private String title;
    int width;
    int height;
    
    
    public Window(String gameTitle, WindowOptions options)
    {
        this.title = gameTitle;
    
        //Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
    
        //No idea if useful
        //glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        //glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        
    
        //Setup an error callback that prints into System.err
    
        //Initialize GLFW, most GLFW functions won't work before doing this
        if(!glfwInit())
        {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
    

        
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
        
        //glfwSetFramebufferSizeCallback(window, (window, width, height) -> resized(width, height));
    
        GLFWErrorCallback.createPrint(System.err).set();
       /* glfwSetErrorCallback((int errorCode, long msgPtr) ->
                System.out.println("Error code: " + errorCode + ", Message: " + MemoryUtil.memUTF8(msgPtr)));*/
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
