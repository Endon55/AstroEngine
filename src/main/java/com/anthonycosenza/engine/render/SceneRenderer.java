package com.anthonycosenza.engine.render;

import com.anthonycosenza.engine.scene.Entity;
import com.anthonycosenza.engine.scene.Scene;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
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
        shaderModuleDataList.add(new ShaderProgram.ShaderModuleData("/vertex.vs", GL_VERTEX_SHADER));
        shaderModuleDataList.add(new ShaderProgram.ShaderModuleData("/fragment.fs", GL_FRAGMENT_SHADER));
        shaderProgram = new ShaderProgram(shaderModuleDataList);


        createUniforms();
    }
    
    private void createUniforms()
    {
        uniformsMap = new UniformsMap(shaderProgram.getProgramID());
        uniformsMap.createUniform("projectionMatrix");
        uniformsMap.createUniform("modelMatrix");
    }
    
    public void render(Scene scene)
    {
        shaderProgram.bind();
        uniformsMap.setUniform("projectionMatrix", scene.getProjection().getProjectionMatrix());
        
        Collection<Model> models = scene.getModelMap().values();
        for(Model model : models)
        {
            model.getMeshList().forEach(mesh ->
            {
                //Draw mesh
                glBindVertexArray(mesh.getVaoID());
                for(Entity entity : model.getEntityList())
                {
                    uniformsMap.setUniform("modelMatrix", entity.getModelMatrix());
                    glDrawElements(GL_TRIANGLES, mesh.getVertexCount(), GL_UNSIGNED_INT, 0);
                }
            });
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
