package com.anthonycosenza.engine.space.rendering;

import com.anthonycosenza.engine.space.Window;
import com.anthonycosenza.engine.space.rendering.projection.Projection;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glEnable;

public class Renderer
{
    private final SceneRenderer sceneRenderer;
    private final InterfaceRenderer interfaceRenderer;
    
    public Renderer(Window window)
    {
        sceneRenderer = new SceneRenderer();
        interfaceRenderer = new InterfaceRenderer(window);
    
        /*
         * GL_SRC_ALPHA - specifies how the source blending factors are computed.
         *
         *
         * GL_ONE_MINUS_SRC_ALPHA - specifies how the destination blending factors are computed.
         */
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_BLEND);
    }
    
    public void render(double delta, Scene scene, Projection projection3d)
    {
        sceneRenderer.render(scene, projection3d);
        interfaceRenderer.render(scene);
    }
    
    public void resize(int width, int height)
    {
        interfaceRenderer.resize(width, height);
    }
    
    public void cleanup()
    {
        interfaceRenderer.cleanup();
    }
    
}
