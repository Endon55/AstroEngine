package com.anthonycosenza.engine.render;

import com.anthonycosenza.engine.window.Window;
import org.lwjgl.opengl.GL;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;

public class Renderer
{
    public Renderer()
    {
        GL.createCapabilities();
    }

    public void render(Window window)
    {
        clear();
    }
    
    private void clear()
    {
        glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }
    
    public void cleanup()
    {
    
    }

}
