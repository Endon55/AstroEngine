package com.anthonycosenza.engine.render;

import com.anthonycosenza.engine.scene.Entity;
import com.anthonycosenza.engine.scene.Scene;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_TEXTURE;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL30.glBindVertexArray;


public class SceneRenderer
{
    private ShaderProgram shaderProgram;
    private UniformsMap uniformsMap;
    
    public SceneRenderer()
    {
        List<ShaderProgram.ShaderModuleData> shaderModuleDataList = new ArrayList<>();
        shaderModuleDataList.add(new ShaderProgram.ShaderModuleData("resources/shaders/vertex.vert", GL_VERTEX_SHADER));
        shaderModuleDataList.add(new ShaderProgram.ShaderModuleData("resources/shaders/fragment.frag", GL_FRAGMENT_SHADER));
        shaderProgram = new ShaderProgram(shaderModuleDataList);

        createUniforms();
    }
    
    private void createUniforms()
    {
        uniformsMap = new UniformsMap(shaderProgram.getProgramID());
        uniformsMap.createUniform("projectionMatrix");
        uniformsMap.createUniform("viewMatrix");
        uniformsMap.createUniform("modelMatrix");
        uniformsMap.createUniform("textureSampler");
        uniformsMap.createUniform("material.diffuse");
    }
    
    public void render(Scene scene)
    {
        shaderProgram.bind();
        
        uniformsMap.setUniform("projectionMatrix", scene.getProjection().getProjectionMatrix());
        uniformsMap.setUniform("viewMatrix", scene.getCamera().getViewMatrix());
        
        uniformsMap.setUniform("textureSampler", 0);
        
        Collection<Model> models = scene.getModelMap().values();
        TextureCache textureCache = scene.getTextureCache();
        for(Model model : models)
        {
            List<Entity> entities = model.getEntityList();
            for(Material material : model.getMaterialList())
            {
                uniformsMap.setUniform("material.diffuse", material.getDiffuseColor());
                Texture texture = textureCache.getTexture(material.getTexturePath());
                glActiveTexture(GL_TEXTURE);
                texture.bind();
                for(Mesh mesh : material.getMeshList())
                {
                    glBindVertexArray(mesh.getVaoID());
                    for(Entity entity : entities)
                    {
                        uniformsMap.setUniform("modelMatrix", entity.getModelMatrix());
                        glDrawElements(GL_TRIANGLES, mesh.getVertexCount(), GL_UNSIGNED_INT, 0);
                    }
                }
            }
        }

        
        glBindVertexArray(0);
        shaderProgram.unbind();
    }

    public void cleanup()
    {
        if(shaderProgram != null)
        {
            shaderProgram.cleanup();
        }
    }
}
