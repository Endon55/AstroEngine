package com.anthonycosenza.rending;

import com.anthonycosenza.Entity;
import com.anthonycosenza.Scene;
import com.anthonycosenza.UniformMap;
import com.anthonycosenza.shader.ShaderData;
import com.anthonycosenza.shader.ShaderPipeline;
import com.anthonycosenza.transformation.Projection;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;

public class Renderer
{
    private ShaderPipeline shaderPipeline;
    private UniformMap uniforms;
    
    public Renderer()
    {
        shaderPipeline = new ShaderPipeline(new ShaderData("resources/shaders/scene.vert", GL_VERTEX_SHADER),
                new ShaderData("resources/shaders/scene.frag", GL_FRAGMENT_SHADER));
    
        uniforms = new UniformMap(shaderPipeline.getProgramID());
        uniforms.createUniform("projectionMatrix");
        uniforms.createUniform("modelMatrix");
    
        //The color that the window frame gets cleared to right before a new frame is rendered.
        glClearColor(0.0f, 1.0f, 0.0f, 0.0f);
    }
    
    
    public void render(Scene scene, Projection projection)
    {
        //https://registry.khronos.org/OpenGL-Refpages/gl4/html/glClear.xhtml
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        
        shaderPipeline.bind();
        uniforms.setUniform("projectionMatrix", projection.getProjectionMatrix());
        for(Entity entity : scene.getEntities())
        {
            entity.getModel().getMesh().bind();
            uniforms.setUniform("modelMatrix", entity.getModelMatrix());
    
    
            glDrawElements(GL_TRIANGLES, entity.getModel().getMesh().getVertexCount(), GL_UNSIGNED_INT, 0);
        }

        
        shaderPipeline.unbind();
    }
}
