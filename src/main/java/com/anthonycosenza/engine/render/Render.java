package com.anthonycosenza.engine.render;

import com.anthonycosenza.engine.render.gui.GuiRenderer;
import com.anthonycosenza.engine.scene.Scene;
import com.anthonycosenza.engine.window.Window;
import org.lwjgl.opengl.GL;

import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FRONT;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glPolygonMode;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;

public class Render
{
    private SceneRenderer sceneRenderer;
    private GuiRenderer guiRenderer;
    private SkyBoxRenderer skyBoxRenderer;
    private ShadowRenderer shadowRenderer;
    
    public Render(Window window)
    {
        GL.createCapabilities();
        glEnable(GL_MULTISAMPLE);
        glEnable(GL_DEPTH_TEST);
        
        //Support for transparencies
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        
        sceneRenderer = new SceneRenderer();
        guiRenderer = new GuiRenderer(window);
        skyBoxRenderer = new SkyBoxRenderer();
        shadowRenderer = new ShadowRenderer();
    }

    public void render(Window window, Scene scene)
    {
        shadowRenderer.render(scene);
        
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glViewport(0, 0, window.getWidth(), window.getHeight());
        
        skyBoxRenderer.render(scene);
        sceneRenderer.render(scene, shadowRenderer);
        guiRenderer.render(scene);
    }
    
    public void cleanup()
    {
        sceneRenderer.cleanup();
        guiRenderer.cleanup();
        skyBoxRenderer.cleanup();
        shadowRenderer.cleanup();
    }
    public void resize(int width, int height)
    {
        guiRenderer.resize(width, height);
    }
}
