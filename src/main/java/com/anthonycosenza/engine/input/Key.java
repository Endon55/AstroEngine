package com.anthonycosenza.engine.input;

import static org.lwjgl.glfw.GLFW.*;

public enum Key
{
    A(GLFW_KEY_A),
    D(GLFW_KEY_D),
    S(GLFW_KEY_S),
    W(GLFW_KEY_W),
    C(GLFW_KEY_C),
    SPACE(GLFW_KEY_SPACE),
    ESCAPE(GLFW_KEY_ESCAPE),
    ALT(GLFW_KEY_LEFT_ALT),
    LEFT_SHIFT(GLFW_KEY_LEFT_SHIFT),
    RIGHT_SHIFT(GLFW_KEY_RIGHT_SHIFT),
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
