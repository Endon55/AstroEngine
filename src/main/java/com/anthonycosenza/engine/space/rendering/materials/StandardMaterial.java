package com.anthonycosenza.engine.space.rendering.materials;

import com.anthonycosenza.engine.annotations.Property;
import com.anthonycosenza.engine.assets.AssetManager;
import com.anthonycosenza.engine.space.entity.Mesh;
import com.anthonycosenza.engine.space.rendering.shader.ShaderPipeline;
import com.anthonycosenza.engine.space.rendering.shader.UniformMap;
import com.anthonycosenza.engine.util.math.Color;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class StandardMaterial implements Material
{
    public static final Color DEFAULT_COLOR = new Color(0f, 0f, 0f, 1f);
    
    @Property
    public long resourceID = -1;
    @Property
    public Color diffuseColor;
    @Property
    public Texture texture;
    @Property
    public ShaderPipeline pipeline;
    
    List<Mesh> meshes;
    
    
    public StandardMaterial()
    {
        diffuseColor = DEFAULT_COLOR;
        meshes = new ArrayList<>();
    }
    
    public List<Mesh> getMeshes()
    {
        return meshes;
    }
    
    public Color getDiffuseColor()
    {
        return diffuseColor;
    }
    
    public void setDiffuseColor(Color diffuseColor)
    {
        this.diffuseColor = diffuseColor;
    }
    
    public Texture getTexture()
    {
        return texture;
    }
    
    public void setTexture(String texturePath)
    {
        this.texture = new Texture(texturePath);
    }
    
    public long getResourceID()
    {
        if(resourceID == -1)
        {
            resourceID = AssetManager.getInstance().generateResourceID();
        }
        return resourceID;
    }
    
    @Override
    public void setResourceID(long resourceID)
    {
        this.resourceID = resourceID;
    }
    
    @Override
    public void bind()
    {
    
    }
    
    @Override
    public void set()
    {
        UniformMap uniforms = pipeline.getUniforms();
        uniforms.setUniform("material.diffuse", diffuseColor);
        glActiveTexture(GL_TEXTURE0);
        texture.bind();
    }
    
    @Override
    public ShaderPipeline getShaderPipeline()
    {
        return pipeline;
    }
}
