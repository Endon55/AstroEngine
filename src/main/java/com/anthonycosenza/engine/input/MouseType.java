package com.anthonycosenza.engine.input;

import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_CAPTURED;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_DISABLED;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_HIDDEN;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_NORMAL;

public enum MouseType
{
    NORMAL(GLFW_CURSOR_NORMAL),
    HIDDEN(GLFW_CURSOR_HIDDEN),
    CAPTURED(GLFW_CURSOR_CAPTURED),
    DISABLED(GLFW_CURSOR_DISABLED),
    ;
    
    private final int glfwCursor;
    
    MouseType(int glfwCursor)
    {
        this.glfwCursor = glfwCursor;
    }
    
    public int getType()
    {
        return glfwCursor;
    }
    
}
