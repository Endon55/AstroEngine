package com.anthonycosenza.engine.space.rendering;

import com.anthonycosenza.engine.space.entity.Entity;
import com.anthonycosenza.engine.space.entity.Mesh;
import com.anthonycosenza.engine.space.entity.texture.Material;
import com.anthonycosenza.engine.space.entity.texture.Texture;
import com.anthonycosenza.engine.space.rendering.projection.Projection;
import com.anthonycosenza.engine.space.rendering.shader.ShaderData;
import com.anthonycosenza.engine.space.rendering.shader.ShaderPipeline;
import com.anthonycosenza.engine.space.rendering.shader.UniformMap;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;

public class SceneRenderer
{
    private final ShaderPipeline shaderPipeline;
    private final UniformMap uniforms;
    
    public SceneRenderer()
    {
        shaderPipeline = new ShaderPipeline(new ShaderData("AstroEngine/resources/shaders/scene.vert", GL_VERTEX_SHADER),
                new ShaderData("AstroEngine/resources/shaders/scene.frag", GL_FRAGMENT_SHADER));
        
        uniforms = new UniformMap(shaderPipeline.getProgramID());
        uniforms.createUniform("projectionMatrix");
        uniforms.createUniform("cameraMatrix");
        uniforms.createUniform("entityMatrix");
        uniforms.createUniform("hasTexture");
        uniforms.createUniform("textureSampler");
        uniforms.createUniform("material.diffuse");
        
        //The color that the window frame gets cleared to right before a new frame is rendered.
        glClearColor(0.0f, 1.0f, 0.0f, 0.0f);
        

    }
    
    
    public void render(Scene scene, Projection projection)
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
            for(Material material : entity.getModel().getMaterials())
            {
                uniforms.setUniform("material.diffuse", material.getDiffuseColor());
                Texture texture = material.getTexture();
                if(texture != null)
                {
                    uniforms.setUniform("hasTexture", 1);
                    texture.bind();
                    glActiveTexture(0);
                }
                else uniforms.setUniform("hasTexture", 0);
                
                for(Mesh mesh : material.getMeshes())
                {
                    mesh.bind();
                    uniforms.setUniform("entityMatrix", entity.getMatrix());
        
                    //glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
                    glDrawElements(GL_TRIANGLES, mesh.getVertexCount(), GL_UNSIGNED_INT, 0);
                }
            }

        }
    
    
        //glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        shaderPipeline.unbind();
    }
}
