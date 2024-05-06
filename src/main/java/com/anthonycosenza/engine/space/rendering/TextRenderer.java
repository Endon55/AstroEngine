package com.anthonycosenza.engine.space.rendering;

import com.anthonycosenza.engine.space.rendering.shader.UniformMap;
import com.anthonycosenza.engine.space.rendering.projection.Projection2d;
import com.anthonycosenza.engine.space.rendering.shader.ShaderData;
import com.anthonycosenza.engine.space.rendering.shader.ShaderPipeline;
import com.anthonycosenza.engine.loader.text.TextStrip;

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
