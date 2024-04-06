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
    private SceneRenderer sceneRenderer;
    private GuiRenderer guiRenderer;
    private SkyBoxRenderer skyBoxRenderer;
    
    public Render(Window window)
    {
        GL.createCapabilities();
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        sceneRenderer = new SceneRenderer();
        guiRenderer = new GuiRenderer(window);
        skyBoxRenderer = new SkyBoxRenderer();
    }

    public void render(Window window, Scene scene)
    {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glViewport(0, 0, window.getWidth(), window.getHeight());
        
        skyBoxRenderer.render(scene);
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
