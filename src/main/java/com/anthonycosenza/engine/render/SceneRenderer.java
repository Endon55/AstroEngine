package com.anthonycosenza.engine.render;

import com.anthonycosenza.engine.game.Scene;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;


public class SceneRenderer
{
    private ShaderProgram shaderProgram;
    
    public SceneRenderer()
    {
        List<ShaderProgram.ShaderModuleData> shaderModuleDataList = new ArrayList<>();
        shaderModuleDataList.add(new ShaderProgram.ShaderModuleData("/vertex.vs", GL_VERTEX_SHADER));
        shaderModuleDataList.add(new ShaderProgram.ShaderModuleData("/fragment.fs", GL_FRAGMENT_SHADER));
        
        
        try
        {
            shaderProgram = new ShaderProgram(shaderModuleDataList);
        } catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    public void render(Scene scene)
    {
        shaderProgram.bind();
        scene.getMeshMap().values().forEach(mesh ->
        {
            //Draw mesh
            glBindVertexArray(mesh.getVaoID());
            //glEnableVertexAttribArray(0);
            //glEnableVertexAttribArray(1);
            glDrawElements(GL_TRIANGLES, mesh.getVertexCount(), GL_UNSIGNED_INT, 0);
            //glDisableVertexAttribArray(0);
            //glDisableVertexAttribArray(1);
        });
        
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
