package com.anthonycosenza.engine.input;

import static org.lwjgl.glfw.GLFW.*;

public enum Key
{
    A(GLFW_KEY_A),
    D(GLFW_KEY_D),
    S(GLFW_KEY_S),
    W(GLFW_KEY_W),
    ESCAPE(GLFW_KEY_ESCAPE),
    ALT(GLFW_KEY_LEFT_ALT),
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
