package com.anthonycosenza.engine.space.rendering.materials;

import com.anthonycosenza.engine.annotations.Property;
import com.anthonycosenza.engine.assets.AssetManager;
import com.anthonycosenza.engine.space.entity.texture.Texture;
import com.anthonycosenza.engine.space.rendering.shader.ShaderPipeline;
import com.anthonycosenza.engine.space.rendering.shader.UniformMap;
import com.anthonycosenza.engine.util.math.Color;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class StandardMaterial implements Material
{
    @Property
    public Texture texture;
    @Property
    public ShaderPipeline pipeline;
    @Property
    public Color diffuseColor;
    
    public StandardMaterial()
    {
        pipeline = AssetManager.getInstance().getShaderDefault();
    }
    
    @Override
    public void bind()
    {
        pipeline.bind();
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
