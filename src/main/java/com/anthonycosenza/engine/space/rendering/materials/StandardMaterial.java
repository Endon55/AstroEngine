package com.anthonycosenza.engine.space.rendering.materials;

import com.anthonycosenza.engine.assets.AssetManager;
import com.anthonycosenza.engine.space.entity.Mesh;
import com.anthonycosenza.engine.space.rendering.shader.ShaderPipeline;
import com.anthonycosenza.engine.space.rendering.shader.UniformMap;
import com.anthonycosenza.engine.util.math.Color;

import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class StandardMaterial implements Material
{
    public static final Color DEFAULT_COLOR = new Color(0f, 0f, 0f, 1f);
    public static final Texture DEFAULT_TEXTURE = new Texture("AstroEngine/resources/images/Default_Texture.png");
    
    public long resourceID = -1;
    public Color diffuseColor;
    public Texture texture;
    
    transient Set<Mesh> meshes;
    
    
    public StandardMaterial()
    {
        diffuseColor = DEFAULT_COLOR;
        meshes = new HashSet<>();
        texture = null;
    }
    
    public Set<Mesh> getMeshes()
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
        if(texture == null) return DEFAULT_TEXTURE;
        else return texture;
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
        getTexture();
    }
    
    @Override
    public void set(ShaderPipeline pipeline)
    {
        UniformMap uniforms = pipeline.getUniforms();
        uniforms.setUniform("material.diffuse", diffuseColor);
        glActiveTexture(GL_TEXTURE0);
        bind();
    }
    
    @Override
    public ShaderPipeline getShaderPipeline()
    {
        return null;
    }

    
}
