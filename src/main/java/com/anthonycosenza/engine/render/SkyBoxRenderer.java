package com.anthonycosenza.engine.render;

import com.anthonycosenza.engine.render.model.Material;
import com.anthonycosenza.engine.render.model.Mesh;
import com.anthonycosenza.engine.render.model.Model;
import com.anthonycosenza.engine.render.model.Texture;
import com.anthonycosenza.engine.scene.Entity;
import com.anthonycosenza.engine.scene.Scene;
import com.anthonycosenza.engine.scene.SkyBox;
import org.joml.Matrix4f;
import org.lwjgl.opengl.ARBGeometryShader4;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.EXTGeometryShader4;
import org.lwjgl.opengl.GL32;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_TEXTURE;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class SkyBoxRenderer
{
    private ShaderProgram shaderProgram;
    private UniformsMap uniformsMap;
    private Matrix4f viewMatrix;
    
    public SkyBoxRenderer()
    {
        List<ShaderProgram.ShaderModuleData> shaderModuleDataList = new ArrayList<>();
        shaderModuleDataList.add(new ShaderProgram.ShaderModuleData("resources/shaders/skybox.vert", GL_VERTEX_SHADER));
        //shaderModuleDataList.add(new ShaderProgram.ShaderModuleData("resources/shaders/skybox.geom", GL32.GL_GEOMETRY_SHADER));
        shaderModuleDataList.add(new ShaderProgram.ShaderModuleData("resources/shaders/skybox.frag", GL_FRAGMENT_SHADER));
        shaderProgram = new ShaderProgram(shaderModuleDataList);
        viewMatrix = new Matrix4f();
        createUniforms();
    }
    
    private void createUniforms()
    {
        uniformsMap = new UniformsMap(shaderProgram.getProgramID());
        uniformsMap.createUniform("projectionMatrix");
        uniformsMap.createUniform("viewMatrix");
        uniformsMap.createUniform("modelMatrix");
        uniformsMap.createUniform("diffuse");
        uniformsMap.createUniform("txtSampler");
        uniformsMap.createUniform("hasTexture");
    }
    
    
    public void render(Scene scene)
    {
        SkyBox skybox = scene.getSkyBox();
        if(skybox == null)
        {
            return;
        }
        
        shaderProgram.bind();
        
        uniformsMap.setUniform("projectionMatrix", scene.getProjection().getProjectionMatrix());
        viewMatrix.set(scene.getCamera().getViewMatrix());
        //Manually set the 3 values that correspond to position coordinate within the matrix to 0 because the skybox is always drawn at the origin. We must use the entire matrix to maintain rotation.
        viewMatrix.m30(0);
        viewMatrix.m31(0);
        viewMatrix.m32(0);
        
        uniformsMap.setUniform("viewMatrix", viewMatrix);
        uniformsMap.setUniform("txtSampler", 0);
        
        Model skyBoxModel = skybox.getSkyBoxModel();
        Entity skyBoxEntity = skybox.getSkyBoxEntity();
        TextureCache textureCache = scene.getTextureCache();
        for(Material material : skyBoxModel.getMaterialList())
        {
            Texture texture = textureCache.getTexture(material.getTexturePath());
            glActiveTexture(GL_TEXTURE0);
            texture.bind();
            
            uniformsMap.setUniform("diffuse", material.getDiffuseColor());
            uniformsMap.setUniform("hasTexture", texture.getTexturePath().equals(TextureCache.DEFAULT_TEXTURE) ? 0 : 1);
            
            for(Mesh mesh : material.getMeshList())
            {
                glBindVertexArray(mesh.getVaoID());
                uniformsMap.setUniform("modelMatrix", skyBoxEntity.getModelMatrix());
                glDrawElements(GL_TRIANGLES, mesh.getVertexCount(), GL_UNSIGNED_INT, 0);
            }
        }
        
        glBindVertexArray(0);
        shaderProgram.unbind();
    }
    
    public void cleanup()
    {
        shaderProgram.cleanup();
    }
    
}
