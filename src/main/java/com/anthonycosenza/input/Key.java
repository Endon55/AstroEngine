package com.anthonycosenza.input;

import static org.lwjgl.glfw.GLFW.*;

public enum Key
{
    A(GLFW_KEY_A),
    D(GLFW_KEY_D),
    S(GLFW_KEY_S),
    W(GLFW_KEY_W),
    ;
    
    private final int glfwKey;
    Key(int glfwKey)
    {
        this.glfwKey = glfwKey;
    }
    
    public int getGlfwKey()
    {
        return glfwKey;
    }
}
