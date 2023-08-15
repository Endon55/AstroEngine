package com.anthonycosenza.engine.render;

import com.anthonycosenza.engine.game.Scene;
import com.anthonycosenza.engine.window.Window;
import org.lwjgl.opengl.GL;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glViewport;

public class Renderer
{
    SceneRenderer sceneRenderer;
    public Renderer()
    {
        GL.createCapabilities();
        sceneRenderer = new SceneRenderer();
    }

    public void init() throws Exception
    {

    }

    public void render(Window window, Scene scene)
    {
        clear();
        
        glViewport(0, 0, window.getWidth(), window.getHeight());
        sceneRenderer.render(scene);

    }
    
    private void clear()
    {
        glClearColor(.6f, 0.3f, 0.3f, 0.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }
    
    public void cleanup()
    {
        sceneRenderer.cleanup();
    }

}
