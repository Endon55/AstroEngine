package com.anthonycosenza.rendering;

import com.anthonycosenza.Scene;
import com.anthonycosenza.UniformMap;
import com.anthonycosenza.projection.Projection2d;
import com.anthonycosenza.shader.ShaderData;
import com.anthonycosenza.shader.ShaderPipeline;
import com.anthonycosenza.text.TextStrip;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;

public class TextRenderer
{
    private ShaderPipeline shaderPipeline;
    private UniformMap uniforms;
    
    
    public TextRenderer()
    {
        shaderPipeline = new ShaderPipeline(new ShaderData("resources/shaders/text.vert", GL_VERTEX_SHADER),
        new ShaderData("resources/shaders/text.frag", GL_FRAGMENT_SHADER));
        
        uniforms = new UniformMap(shaderPipeline.getProgramID());
        uniforms.createUniform("projectionMatrix");
    }
    
    
    
    public void render(Scene scene, Projection2d projection)
    {
        shaderPipeline.bind();
        uniforms.setUniform("projectionMatrix", projection.getMatrix());
    
        for(TextStrip strip : scene.getTextStrips())
        {
            strip.getMesh().bind();
            glDrawElements(GL_TRIANGLES, strip.getMesh().getVertexCount(), GL_UNSIGNED_INT, 0);
        }
        shaderPipeline.unbind();
    }
    
    
}
