package com.anthonycosenza.engine.space.rendering;

import com.anthonycosenza.engine.space.Window;
import com.anthonycosenza.engine.space.entity.Entity;
import com.anthonycosenza.engine.space.rendering.projection.Projection2d;
import com.anthonycosenza.engine.space.rendering.shader.UniformMap;
import com.anthonycosenza.engine.space.rendering.shader.ShaderData;
import com.anthonycosenza.engine.space.rendering.shader.ShaderPipeline;
import com.anthonycosenza.engine.space.rendering.projection.Projection3d;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;

public class Renderer
{
    private SceneRenderer sceneRenderer;
    private TextRenderer textRenderer;
    private CanvasRenderer canvasRenderer;
    private InterfaceRenderer interfaceRenderer;
    
    public Renderer(Window window)
    {
        sceneRenderer = new SceneRenderer();
        textRenderer = new TextRenderer();
        canvasRenderer = new CanvasRenderer();
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
    
    public void render(double delta, Scene scene, Projection2d projection2d, Projection3d projection3d)
    {
        sceneRenderer.render(scene, projection3d);
        //textRenderer.render(scene, projection2d);
        canvasRenderer.render(scene, projection2d);
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
