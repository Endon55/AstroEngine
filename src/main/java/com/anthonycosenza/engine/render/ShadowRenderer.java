package com.anthonycosenza.engine.render;

import com.anthonycosenza.engine.render.light.shadow.CascadeShadow;
import com.anthonycosenza.engine.render.light.shadow.ShadowBuffer;
import com.anthonycosenza.engine.render.model.Material;
import com.anthonycosenza.engine.render.model.Mesh;
import com.anthonycosenza.engine.render.model.Model;
import com.anthonycosenza.engine.render.model.animation.AnimationData;
import com.anthonycosenza.engine.scene.Entity;
import com.anthonycosenza.engine.scene.Scene;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL30.GL_DEPTH_ATTACHMENT;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glFramebufferTexture2D;

public class ShadowRenderer
{
    private List<CascadeShadow> cascadeShadows;
    private ShaderProgram shaderProgram;
    private ShadowBuffer shadowBuffer;
    private UniformsMap uniformsMap;
    
    public ShadowRenderer()
    {
        List<ShaderProgram.ShaderModuleData> shaderModuleDataList = new ArrayList<>();
        shaderModuleDataList.add(new ShaderProgram.ShaderModuleData("resources/shaders/shadow.vert", GL_VERTEX_SHADER));
        shaderProgram = new ShaderProgram(shaderModuleDataList);
        shadowBuffer = new ShadowBuffer();
        
        cascadeShadows = new ArrayList<>();
        for(int i = 0; i < CascadeShadow.SHADOW_MAP_CASCADE_COUNT; i++)
        {
            CascadeShadow cascadeShadow = new CascadeShadow();
            cascadeShadows.add(cascadeShadow);
        }
        
        createUniforms();
    }
    
    public void render(Scene scene)
    {
        //Might want to cache some of these values to save calculating the exact same thing if nothing has changed.
        CascadeShadow.updateCascadeShadows(cascadeShadows, scene);
        
        glBindFramebuffer(GL_FRAMEBUFFER, shadowBuffer.getDepthMapFBO());
        glViewport(0, 0, ShadowBuffer.SHADOW_MAP_WIDTH, ShadowBuffer.SHADOW_MAP_HEIGHT);
        
        shaderProgram.bind();
        
        Collection<Model> models = scene.getModelMap().values();
        for(int i = 0; i < CascadeShadow.SHADOW_MAP_CASCADE_COUNT; i++)
        {
            //Bind the frame buffer and then empty it out to be refilled.
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, shadowBuffer.getDepthMapTexture().getIds()[i], 0);
            glClear(GL_DEPTH_BUFFER_BIT);
            
            CascadeShadow shadowCascade = cascadeShadows.get(i);
            uniformsMap.setUniform("projViewMatrix", shadowCascade.getProjViewMatrix());
            
            for(Model model : models)
            {
                List<Entity> entities = model.getEntityList();
                for(Material material : model.getMaterialList())
                {
                    for(Mesh mesh : material.getMeshList())
                    {
                        glBindVertexArray(mesh.getVaoID());
                        for(Entity entity : entities)
                        {
                            uniformsMap.setUniform("modelMatrix", entity.getModelMatrix());
                            AnimationData animationData = entity.getAnimationData();
                            if(animationData == null)
                            {
                                uniformsMap.setUniform("bonesMatrices", AnimationData.DEFAULT_BONES_MATRICES);
                            }
                            else
                            {
                                uniformsMap.setUniform("bonesMatrices", animationData.getCurrentFrame().boneMatrices());
                            }
                            glDrawElements(GL_TRIANGLES, mesh.getVertexCount(), GL_UNSIGNED_INT, 0);
                        }
                    }
                }
            }
        }
        shaderProgram.unbind();
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }
    
    private void createUniforms()
    {
        uniformsMap = new UniformsMap(shaderProgram.getProgramID());
        uniformsMap.createUniform("modelMatrix");
        uniformsMap.createUniform("projViewMatrix");
        uniformsMap.createUniform("bonesMatrices");
    }
    
    public ShadowBuffer getShadowBuffer()
    {
        return shadowBuffer;
    }
    
    public List<CascadeShadow> getCascadeShadows()
    {
        return cascadeShadows;
    }
    
    public void cleanup()
    {
        shaderProgram.cleanup();
        shadowBuffer.cleanup();
    }
}
