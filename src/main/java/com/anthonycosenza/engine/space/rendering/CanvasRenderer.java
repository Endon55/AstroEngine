package com.anthonycosenza.engine.space.rendering;

import com.anthonycosenza.engine.loader.text.TextStrip;
import com.anthonycosenza.engine.space.rendering.UI.Canvas;
import com.anthonycosenza.engine.space.rendering.projection.Projection2d;
import com.anthonycosenza.engine.space.rendering.shader.ShaderData;
import com.anthonycosenza.engine.space.rendering.shader.ShaderPipeline;
import com.anthonycosenza.engine.space.rendering.shader.UniformMap;

import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glPolygonMode;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;

public class CanvasRenderer
{
    private ShaderPipeline shaderPipeline;
    private UniformMap uniforms;
    
    
    public CanvasRenderer()
    {
        shaderPipeline = new ShaderPipeline(new ShaderData("resources/shaders/canvas.vert", GL_VERTEX_SHADER),
        new ShaderData("resources/shaders/canvas.frag", GL_FRAGMENT_SHADER));
        
        uniforms = new UniformMap(shaderPipeline.getProgramID());
        uniforms.createUniform("projectionMatrix");
        uniforms.createUniform("textureSampler");
    }
    
    
    
    public void render(Scene scene, Projection2d projection)
    {
        shaderPipeline.bind();
        uniforms.setUniform("projectionMatrix", projection.getMatrix());
        uniforms.setUniform("textureSampler", 0);
        glDisable(GL_DEPTH_TEST);
        //glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        for(Canvas canvas : scene.getCanvasItems())
        {
            canvas.getMesh().bind();
            glActiveTexture(0);
            canvas.getTexture().bind();
            
            glDrawElements(GL_TRIANGLES, canvas.getMesh().getVertexCount(), GL_UNSIGNED_INT, 0);
        }
        glEnable(GL_DEPTH_TEST);
        shaderPipeline.unbind();
    }
    
    
}
