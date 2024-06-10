package com.anthonycosenza.engine.space.rendering.materials;

import com.anthonycosenza.engine.assets.AssetManager;
import com.anthonycosenza.engine.assets.ShaderManager;
import com.anthonycosenza.engine.space.entity.Mesh;
import com.anthonycosenza.engine.space.rendering.shader.FragmentShader;
import com.anthonycosenza.engine.space.rendering.shader.ShaderPipeline;
import com.anthonycosenza.engine.space.rendering.shader.VertexShader;

import java.util.HashSet;
import java.util.Set;

public class ShaderMaterial implements Material
{
    
    public long resourceID = -1;
    private final transient Set<Mesh> meshes;
    private transient ShaderPipeline pipeline = null;
    
    public VertexShader vertexShader;
    public FragmentShader fragmentShader;
    
    public ShaderMaterial()
    {
        this.meshes = new HashSet<>();
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
    public Set<Mesh> getMeshes()
    {
        return meshes;
    }
    
    @Override
    public void addMesh(Mesh mesh)
    {
        meshes.add(mesh);
    }
    
    @Override
    public void bind()
    {
    
    }
    
    @Override
    public void setUniforms(ShaderPipeline pipeline)
    {
    
    }
    public void updatePipeline()
    {
        if(vertexShader != null) vertexShader.assemble = true;
        if(fragmentShader != null) fragmentShader.assemble = true;
        
        pipeline = ShaderManager.createPipeline(vertexShader, fragmentShader);
    }
    @Override
    public ShaderPipeline getShaderPipeline()
    {
        if(pipeline == null)
        {
            updatePipeline();
        }
        return pipeline;
    }
}
