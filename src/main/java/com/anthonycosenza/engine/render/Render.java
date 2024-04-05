package com.anthonycosenza.engine.render;

import com.anthonycosenza.engine.render.gui.GuiRenderer;
import com.anthonycosenza.engine.scene.Scene;
import com.anthonycosenza.engine.window.Window;
import org.lwjgl.opengl.GL;

import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glViewport;

public class Render
{
    SceneRenderer sceneRenderer;
    GuiRenderer guiRenderer;
    
    public Render(Window window)
    {
        GL.createCapabilities();
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        sceneRenderer = new SceneRenderer();
        guiRenderer = new GuiRenderer(window);
    }

    public void render(Window window, Scene scene)
    {
        glClearColor(.6f, 0.3f, 0.3f, 0.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glViewport(0, 0, window.getWidth(), window.getHeight());
        
        sceneRenderer.render(scene);
        guiRenderer.render(scene);
    }
    
    public void cleanup()
    {
        sceneRenderer.cleanup();
        guiRenderer.cleanup();
    }
    public void resize(int width, int height)
    {
        guiRenderer.resize(width, height);
    }
}
