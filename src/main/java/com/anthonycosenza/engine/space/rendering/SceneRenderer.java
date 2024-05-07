package com.anthonycosenza.engine.space.rendering;

import com.anthonycosenza.engine.space.entity.Entity;
import com.anthonycosenza.engine.space.rendering.projection.Projection3d;
import com.anthonycosenza.engine.space.rendering.shader.ShaderData;
import com.anthonycosenza.engine.space.rendering.shader.ShaderPipeline;
import com.anthonycosenza.engine.space.rendering.shader.UniformMap;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glPolygonMode;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;

public class SceneRenderer
{
    private ShaderPipeline shaderPipeline;
    private UniformMap uniforms;
    
    public SceneRenderer()
    {
        shaderPipeline = new ShaderPipeline(new ShaderData("resources/shaders/scene.vert", GL_VERTEX_SHADER),
                new ShaderData("resources/shaders/scene.frag", GL_FRAGMENT_SHADER));
        
        uniforms = new UniformMap(shaderPipeline.getProgramID());
        uniforms.createUniform("projectionMatrix");
        uniforms.createUniform("cameraMatrix");
        uniforms.createUniform("entityMatrix");
        uniforms.createUniform("textureSampler");
        
        //The color that the window frame gets cleared to right before a new frame is rendered.
        glClearColor(0.0f, 1.0f, 0.0f, 0.0f);
        

    }
    
    
    public void render(Scene scene, Projection3d projection)
    {
        //https://registry.khronos.org/OpenGL-Refpages/gl4/html/glClear.xhtml
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        //glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        shaderPipeline.bind();
        uniforms.setUniform("projectionMatrix", projection.getMatrix());
        uniforms.setUniform("cameraMatrix", scene.getCamera().getMatrix());
        uniforms.setUniform("textureSampler", 0);
        
        for(Entity entity : scene.getEntities())
        {
            entity.getModel().getMesh().bind();
            glActiveTexture(0);
            entity.getModel().getTexture().bind();
            uniforms.setUniform("entityMatrix", entity.getMatrix());
            
            //glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
            glDrawElements(GL_TRIANGLES, entity.getModel().getMesh().getVertexCount(), GL_UNSIGNED_INT, 0);
        }
    
    
        //glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        shaderPipeline.unbind();
    }
}
